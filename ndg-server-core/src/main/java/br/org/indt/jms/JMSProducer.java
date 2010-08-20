/*
*  Copyright (C) 2010  INdT - Instituto Nokia de Tecnologia
*
*  NDG is free software; you can redistribute it and/or
*  modify it under the terms of the GNU Lesser General Public
*  License as published by the Free Software Foundation; either 
*  version 2.1 of the License, or (at your option) any later version.
*
*  NDG is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
*  Lesser General Public License for more details.
*
*  You should have received a copy of the GNU Lesser General Public 
*  License along with NDG.  If not, see <http://www.gnu.org/licenses/ 
*/

package br.org.indt.jms;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TemporaryQueue;
import javax.jms.TextMessage;


/**
 * @author Fabio
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class JMSProducer extends JMSClient {

	private static final long REPLY_TIMEOUT = 5000; //default reply waiting time //TODO properties?
	//private static final int REPLY_MAX_TIMEOUT = 20000; //maximum time allowed before attempts to receive a reply are aborted
	
	private Connection connection;
	private Session session;
	private Session replySession;
	private Destination destination;
	private MessageProducer producer;
	
	private MessageConsumer replyConsumer;
	private TemporaryQueue replyQueue;
	
	private String connectionFactoryName;
	private String connectionUsername;
	private String connectionPassword;
	private String destinationName;
	private int sessionMode;
	
	private Logger logger;
	

	
	
	
	/**
	 * 
	 */
	public JMSProducer(String connectionFactoryName, String connectionUsername, String connectionPassword, String destinationName, int sessionMode) throws JMSException 
	{
		super();
		
		try
		{
			//connectionFactory = JMSUtil.geConnectionFactory(connectionFactoryName);
			//connection = connectionFactory.createConnection(connectionUserName, connectionPassword);
			
			this.connectionFactoryName	= connectionFactoryName;
			this.connectionUsername 	= connectionUsername;
			this.connectionPassword 	= connectionPassword;
			this.destinationName 		= destinationName;
			this.sessionMode 			= sessionMode;
			
			open();
		}
		catch(JMSException exc)
		{
			throw exc;
		}
		catch(Exception exc)
		{
            JMSException jmsException =  new JMSException("General exception initializing JMSConsumer. " + exc.toString() + " - " + exc.getMessage());
            jmsException.initCause(exc);
            throw jmsException;
		}

	}
	
	public ObjectMessage createObjectMessage() throws JMSException
	{
		return session.createObjectMessage();
	}
	
	public TextMessage createTextMessage() throws JMSException
	{
		return session.createTextMessage();
	}
	
	public void send(Message msg) throws JMSException
	{
		producer.send(msg);
	}
	
	public void send(Destination dest, Message msg, int deliveryMode, int priority, int ttl) throws JMSException
	{
		producer.send(dest, msg, deliveryMode, priority, ttl);
	}
	
	public void send(Message msg, int deliveryMode, int priority, int ttl) throws JMSException
	{
		producer.send(msg, deliveryMode, priority, ttl);
	}
	
	public void send(Destination dest, Message msg) throws JMSException
	{
		producer.send(dest, msg);
	}
	
	public Message sendConfirmed(Message msg) throws JMSException
	{
		return sendConfirmed(msg, 0);
	}
	
	
	//Timeout pode ser qualquer valor acima de 5000.
	//Valores abaixo de 5000 s�o aceitos mas n�o tem efeito
	//O tempo de retorno pode ser at� 5 segundos maior do que o tempo solicitado
	public Message sendConfirmed(Message msg, long timeout) throws JMSException
	{
		msg.setJMSReplyTo(replyQueue);
		
		producer.send(msg);
		
		if(session.getAcknowledgeMode() == Session.SESSION_TRANSACTED)
        {
			session.commit();
        }
			
		if(logger.isLoggable(Level.INFO)) //alterado de FINEST para INFO para facilitar debug no jboss
		{
			logger.info("Sending confirmed msg: ID: " + msg.getJMSMessageID());
		}	
		
		long startTime = System.currentTimeMillis();
		
		Message replyMsg = null;
		
		
		while(replyMsg == null || !replyMsg.getJMSCorrelationID().equals(msg.getJMSMessageID()))
		{
			if(timeout == 0 || ((System.currentTimeMillis() - startTime) < timeout))
			{
				replyMsg = replyConsumer.receive(REPLY_TIMEOUT);

				if(logger.isLoggable(Level.INFO))
				{
					if(replyMsg == null)
                    {
						logger.info("Reply received: NULL");
                    }
					else
					{
						logger.info("Reply received: ID: " + msg.getJMSMessageID());
					}
				}	

			}
			else
			{
				logger.info("REPLY_MAX_TIMEOUT reached!");
				break;
			}
				
		}
		
		if(replyMsg != null) {
			return replyMsg;
        }
		else {
            throw new JMSException("Time limit to receive reply reached");
        }		
	}
	
	public void commit() throws JMSException
	{
		session.commit();
	}
	
	public void rollback() throws JMSException
	{
		session.rollback();
	}
	
	public void open() throws JMSException, Exception
	{
		connection = getConnection(connectionFactoryName, connectionUsername, connectionPassword);
			
		if(sessionMode == Session.SESSION_TRANSACTED)
			session = connection.createSession(true, Session.SESSION_TRANSACTED);
		else
			session = connection.createSession(false, sessionMode);

		replySession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);//caso transacted == true, as mensagens ficam na fila do swift para sempre
			
		destination = JMSUtil.getDestination(destinationName);
			
		producer = session.createProducer(destination);
			
		replyQueue = replySession.createTemporaryQueue();
			
		replyConsumer = replySession.createConsumer(replyQueue);
			
		connection.start();
	}
	
	public void close() throws JMSException
	{
		producer.close();
		replyConsumer.close();
        replyQueue.delete();
		session.close();
		replySession.close();
		
		closeConnection(connectionFactoryName,connectionUsername);
	}


   	
    public void onException(JMSException exc)
    {
    	if(!autoReconnect && excListener != null)
        {
        	try
        	{
        		excListener.onException(exc);	
        	}
        	catch(Exception exc2)
        	{
        		//Ignora erro do listener
        	}
        }
        
		if(autoReconnect)
        {
        	logger.log(Level.INFO, "Connection failure detected. Attempting to reconnect...");
        	
        	//tentamos reconectar automaticamente
        	for(int i = 0; reconnectAttempts <= 0 || i < reconnectAttempts; i++)
        	{
        		try
        		{
        			open();	
					
					logger.log(Level.INFO, "Reconnected successfully");
        			
        			return;
        		}
        		catch(Exception exc2)
        		{
					logger.log(Level.INFO, "Reconnection failed... Attempt #" + (i+1));
        		}
        	
        	}//end for
        	
        	if(excListener != null)
        		excListener.onException(new JMSException("Could not reconnect after " + reconnectAttempts + " attempts"));
        	else
				logger.log(Level.SEVERE, "Could not reconnect after " + reconnectAttempts + " attempts", exc);
        }
    }

 

}
