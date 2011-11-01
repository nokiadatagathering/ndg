/*
*  Nokia Data Gathering
*
*  Copyright (C) 2011 Nokia Corporation
*
*  This program is free software; you can redistribute it and/or
*  modify it under the terms of the GNU Lesser General Public
*  License as published by the Free Software Foundation; either
*  version 2.1 of the License, or (at your option) any later version.
*
*  This program is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
*  Lesser General Public License for more details.
*
*  You should have received a copy of the GNU Lesser General Public License
*  along with this program.  If not, see <http://www.gnu.org/licenses/
*/

package br.org.indt.jms;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;

public class JMSSenderHelper implements ExceptionListener
{
    private static final String DEFAULT_JNDI_RESOURCE_NAME = "m4u-util-jndi.properties";
    private static final String DEFAULT_CONNECTION_FACTORY = "ConnectionFactory";
    
    private Logger      logger;

    private Connection  jmsConnection  = null;

    private Destination jmsDestination = null;

    private String      queueName;

    Session             jmsSession     = null;
 
    MessageProducer     jmsProducer    = null;
    
    Properties          jndiProperties  = null;
    
    String              connectionFactory;

    public JMSSenderHelper(String queueName, String jndiResourceName) throws JMSSenderHelperException
    {
        this(queueName, jndiResourceName, DEFAULT_CONNECTION_FACTORY);
    }
    
    public JMSSenderHelper(String queueName, String jndiResourceName, String connectionFactoryName) throws JMSSenderHelperException 
    {
        if(jndiResourceName == null)
        {
            jndiResourceName = DEFAULT_JNDI_RESOURCE_NAME;
        }
        
        this.queueName = queueName;
        
        logger = Logger.getLogger(JMSSenderHelper.class);
        
        connectionFactory = connectionFactoryName;

        init(jndiResourceName);
    }
    
    public JMSSenderHelper(String queueName) throws JMSSenderHelperException
    {
        this.queueName = queueName;
        
        logger = Logger.getLogger(JMSSenderHelper.class);
        
        connectionFactory = DEFAULT_CONNECTION_FACTORY;

        init(DEFAULT_JNDI_RESOURCE_NAME);        
    }

    private void init(String jndiResourceName) throws JMSSenderHelperException
    {
        InputStream jndiPropsFile = (jndiResourceName != null) ? getClass().getClassLoader().getResourceAsStream(jndiResourceName) : null;
        
        try
        {
            if(jndiPropsFile != null)
            {
                jndiProperties = new Properties();
                
                jndiProperties.load(jndiPropsFile);

                logger.info("Usando as seguintes propriedades jndi: " + jndiProperties);
            }
            else
            {
                logger.info("Usando propriedades jndi default");
            }
        }
        catch(IOException exc)
        {
            logger.error("Erro ao carregar propriedades JNDI \"" + jndiResourceName + '\"', exc);
        }
        
        reconnectJms();
    }

    public void destroy()
    {
        closeJms();
    }

    public void queue(Serializable vo) throws JMSSenderHelperException
    {
        try
        {
            queueMessage(createObjectMessage(vo));
        }
        catch(JMSException exc)
        {
            throw new JMSSenderHelperException("Erro criando ObjectMessage", exc);
        }
    }
    
    public void queueText(String text) throws JMSSenderHelperException
    {
        try
        {
            queueMessage(createTextMessage(text));
        }
        catch(JMSException exc)
        {
            throw new JMSSenderHelperException("Erro criando TextMessage", exc);
        }
    }
    
    public void queueMessage(Message msg) throws JMSSenderHelperException
    {
        try
        {
            jmsProducer.send(msg);
        }
        catch(JMSException exc)
        {
            throw new JMSSenderHelperException("Erro publicando mensagem", exc);
        }
    }
    
    public ObjectMessage createObjectMessage(Serializable vo) throws JMSException
    {
        return jmsSession.createObjectMessage(vo);
    }
    
    public TextMessage createTextMessage(String text) throws JMSException
    {
        return jmsSession.createTextMessage(text);
    }

    public synchronized void onException(JMSException exception)
    {
        logger.warn("Conexao jms perdida", exception);
        
        try
        {
            reconnectJms();
        }
        catch(Exception e)
        {
            logger.error("Tentativa de reconnexao com jms falhou. Fila: " + queueName, e);
        }
    }
    
    private synchronized void reconnectJms() throws JMSSenderHelperException
    {
		closeJms();
		
    	Connection newJmsConnection = null;
        Destination newJmsDestination = null;
        
    	InitialContext ic;
    	
        try
        {
        	if (jndiProperties != null)
        	{
        		ic = new InitialContext(jndiProperties);
        	}
        	else
        	{
        		ic = new InitialContext();
        	}
        	
        	ConnectionFactory newJmsFactory = (ConnectionFactory)ic.lookup("java:ConnectionFactory");
        	newJmsDestination = (Destination)ic.lookup(queueName);
        	newJmsConnection = newJmsFactory.createConnection();
           
        	try
        	{
        		logger.debug("Connection created ...");

        		// key - register for exception call backs
        		newJmsConnection.setExceptionListener(this);

        		jmsSession = newJmsConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        		logger.debug("Session created ...");
        		jmsProducer = jmsSession.createProducer(newJmsDestination);

        		// jmsProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        		
        		logger.debug("Producer created ...");
        	}
        	catch (Exception e)
        	{
        		// we failed so close the connection
        		try
        		{
        			newJmsConnection.close();
        		}
        		catch (JMSException ignored)
        		{
        			// Pointless
        		}
              
        		// re-throw the initial problem to where we will log it
        		throw e;
        	} 
        	finally
        	{
        		// and close the initial context
        		// we don't want to wait for the garbage collector to close it
        		// otherwise we'll have useless hanging network connections
        		ic.close();
        	}
        }
        catch(Exception e)
        {
            throw new JMSSenderHelperException("Erro ao conectar na fila jms: " + queueName, e);
        }
    }

    private void closeJms()
    {
        if(jmsProducer != null)
        {
            try
            {
                jmsProducer.close();
            }
            catch(Exception e)
            {
            }
            jmsProducer = null;
        }
        
        if(jmsSession != null)
        {
            try
            {
                jmsSession.close();
            }
            catch(Exception e)
            {
            }
            jmsSession = null;
        }
        
        if(jmsConnection != null)
        {
            try
            {
                jmsConnection.close();
            }
            catch(Exception e)
            {
            }
            jmsConnection = null;
        }

        jmsDestination = null;
    }
}
