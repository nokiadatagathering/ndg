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

import java.io.Serializable;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;

/**
 * @author Fabio
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class JMSDurableSubscriber {

	private ConnectionFactory connectionFactory;
	private Connection connection;
	private Session session;
	private Topic topic;
	private MessageConsumer consumer;
	
	private MessageProducer replyProducer;
	/**
	 * 
	 */
	public JMSDurableSubscriber(String connectionFactoryName, String connectionUserName, String connectionPassword, String topicName,  String subscriberName, String selector, boolean noLocal, MessageListener listener, int sessionMode) throws JMSException 
	{

		super();
		
		try
		{
			connectionFactory = JMSUtil.geConnectionFactory(connectionFactoryName);
					
			connection = connectionFactory.createConnection(connectionUserName, connectionPassword);
					
			session = connection.createSession(sessionMode == Session.SESSION_TRANSACTED, sessionMode);
			
			if(sessionMode == Session.CLIENT_ACKNOWLEDGE)
				session.recover();
					
			topic = JMSUtil.getTopic(topicName);
					
			consumer = session.createDurableSubscriber(topic, subscriberName, selector, noLocal);
			
			replyProducer = session.createProducer(null);
			
			consumer.setMessageListener(listener);
				
			connection.start();
		}
		catch(Exception exc)
		{
            JMSException jmsException = new JMSException("Unable to initialize durable subscriber. " + exc.getMessage());
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
		ObjectMessage replyMessage = session.createObjectMessage();
		
		replyMessage.setJMSCorrelationID(originalMessage.getJMSMessageID());
		
		replyMessage.setObject(replyData);
		
		replyProducer.send(originalMessage.getJMSReplyTo(), replyMessage);
	}

	
	public void commit() throws JMSException
	{
		session.commit();
	}
	
	public void rollback() throws JMSException
	{
		session.rollback();
	}
	
	public void recover() throws JMSException
	{
		session.recover();
	}
	
	public void close() throws JMSException
	{
		consumer.close();
		replyProducer.close();
		session.close();
	}

}
