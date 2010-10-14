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

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;


/**
 * @author Fabio
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class JMSConsumer extends JMSClient {
	
	private Connection connection;
	private Session session;
	private Session replySession;
	private Destination destination;
	private MessageConsumer consumer;
	
	private MessageProducer replyProducer;
	
	private String connectionFactoryName;
	private String connectionUsername;
	private String connectionPassword;
	private String destinationName;
	private String messageSelector;
	private MessageListener listener;
	private boolean noLocal;
	private int sessionMode;
	
	private Logger logger;
	
	
	
	/**
	 * 
	 */
	public JMSConsumer(String connectionFactoryName, String connectionUsername, String connectionPassword, String destinationName, String messageSelector, boolean noLocal, MessageListener listener, int sessionMode) throws JMSException 
	{

		super();
		
		try
		{
			//connectionFactory = JMSUtil.geConnectionFactory(connectionFactoryName);
					
			//connection = connectionFactory.createConnection(connectionUsername, connectionPassword);
			this.connectionFactoryName	= connectionFactoryName;
			this.connectionUsername 	= connectionUsername;
			this.connectionPassword 	= connectionPassword;
			this.destinationName 		= destinationName;
			this.sessionMode 			= sessionMode;
			this.listener 				= listener;
			this.noLocal				= noLocal;
			this.messageSelector		= messageSelector;
			
			open();
		}
		catch(JMSException exc)
		{
			throw exc;
		}
		catch(Exception exc)
		{
            JMSException jmsException = new JMSException("General exception initializing JMSConsumer.");
            jmsException.initCause(exc);
            throw jmsException;
		}

	}
	
	public Message receive() throws JMSException
	{
		return consumer.receive();
	}
	
	public Message receive(long timeout) throws JMSException
	{
		return consumer.receive(timeout);
	}
	
	public Message receiveNoWait() throws JMSException
	{
		return consumer.receiveNoWait();
	}
	
	public void reply(Message originalMessage, Serializable replyData) throws JMSException
	{
		if(logger.isLoggable(Level.FINEST))
		{
			if(originalMessage != null)
				logger.finest("Replying to: " + originalMessage.getJMSMessageID() + " with: " + replyData);	
		}
		
		ObjectMessage replyMessage = session.createObjectMessage();
		
		replyMessage.setJMSCorrelationID(originalMessage.getJMSMessageID());
		
		replyMessage.setObject(replyData);
		
		replyProducer.send(originalMessage.getJMSReplyTo(), replyMessage);
	}
		
	public void commit() throws JMSException
	{
		session.commit();
		replySession.commit();
	}
	
	public void rollback() throws JMSException
	{
		session.rollback();
		replySession.rollback();
	}
	
	public void recover() throws JMSException
	{
		session.recover();
	}
	
	public void open() throws JMSException, Exception
	{
		connection = getConnection(connectionFactoryName, connectionUsername, connectionPassword);
					
		session = connection.createSession(sessionMode == Session.SESSION_TRANSACTED, sessionMode);
			
		replySession = connection.createSession(sessionMode == Session.SESSION_TRANSACTED, sessionMode);
			
		if(sessionMode == Session.CLIENT_ACKNOWLEDGE)
			session.recover();
					
		destination = JMSUtil.getDestination(destinationName);
					
		consumer = session.createConsumer(destination, messageSelector, noLocal);
			
		replyProducer = replySession.createProducer(null);

		consumer.setMessageListener(listener);
				
		connection.start();
	}

	
	public void close() throws JMSException
	{
		consumer.close();
		replyProducer.close();
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
			logger.log(Level.WARNING, "Connection failure detected. Attempting to reconnect...", exc);
        	
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
