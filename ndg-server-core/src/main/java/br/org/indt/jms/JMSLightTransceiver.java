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
import javax.jms.TextMessage;

/**
 * @author Fabio
 * 
 */
public class JMSLightTransceiver extends JMSClient implements MessageListener
{
    private Logger logger;
    
	private String connectionFactoryName;
	private String connectionUsername;
	private String connectionPassword;
	private String destinationName;
	private String messageSelector;
	private int sessionMode;
	
	private MessageListener listener;
	
	private Connection connection;
	private Session session;
	private Destination destination;
	private MessageConsumer consumer;
	private MessageProducer producer;
	
	//vari�veis para lidar com mensagens sincronas
	//indica se existe um msgListener correntemente setado
	private boolean msgListenerSet;
	
	//indica o ID da msg original em modo sincrono a espera de uma resposta
	private String syncAwaitingId;
	
	//guarda a mensagem de resposta, se houver
	private Message syncReplyMessage;
	
    /**
     * 
     */
    public JMSLightTransceiver(String connectionFactoryName, String connectionUsername, String connectionPassword, String destinationName, String messageSelector, int sessionMode) throws JMSException
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
			this.messageSelector		= messageSelector;
			
		}
		catch(Exception exc)
		{
            JMSException jmsException = new JMSException("General exception initializing JMSConsumer. " + exc.toString() + " - " + exc.getMessage());
            jmsException.initCause(exc);
            throw jmsException;
		}
    }
    
    public void open() throws JMSException, Exception
    {
		connection = getConnection(connectionFactoryName, connectionUsername, connectionPassword);
		
		session = connection.createSession(sessionMode == Session.SESSION_TRANSACTED, sessionMode);

		if(sessionMode == Session.CLIENT_ACKNOWLEDGE)
		    session.recover();
				
		destination = JMSUtil.getDestination(destinationName);
				
		consumer = session.createConsumer(destination, messageSelector, true);
		
		producer = session.createProducer(destination);
		
		consumer.setMessageListener(listener);
			
		connection.start();
    }
    
	public void close() throws JMSException
	{
		consumer.close();
		producer.close();
		session.close();
		
		closeConnection(connectionFactoryName,connectionUsername);
	}

	/*------------------------------
	 * CONSUMER METHODS
	 *------------------------------*/
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
		
		producer.send(originalMessage.getJMSReplyTo(), replyMessage);
	}
	
	/*------------------------------
	 * PRODUCER METHODS
	 *------------------------------*/
	
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
	
	public Message sendSync(Message msg) throws JMSException
	{
		return sendSync(msg, 0);
	}
	
	/**
	 * Envia uma mensagem em modo s�ncrono.
	 * Neste modo, o transceiver esperar� que uma resposta seja enviada de volta
	 * ou que o timeout expire antes de retornar deste m�todo.
	 * 
	 * O message listener DEVE ESTAR SETADO antes que este m�todo seja chamado
	 * uma vez que seria imposs�vel ler a resposta se o transceiver n�o estiver
	 * recebendo mensagens.
	 * 
	 * Notas:
	 * Timeout pode ser qualquer valor acima de 5000.
	 * Valores abaixo de 5000 s�o aceitos mas n�o tem efeito
	 * O tempo de retorno pode ser at� 5 segundos maior do que o tempo solicitado
	 * 
	 * @param msg mensagem original da qual se espera uma resposta
	 * @param timeout tempo limite para se esperar a resposta. Zero para infinito.
	 * @return a mensagem de resposta ou nulo se n�o houver resposta
	 * @throws IllegalArgumentException caso n�o exista um messageListener setado.
	 */
	public synchronized Message sendSync(Message msg, long timeout) throws JMSException
	{
		msg.setJMSReplyTo(destination);
		
		producer.send(msg);
		
		if(session.getAcknowledgeMode() == Session.SESSION_TRANSACTED)
			session.commit();
		
		return waitForReply(msg, timeout);
	}
	
	/**
	 * Espera por uma resposta por um determinado per�odo de tempo.
	 * Uma resposta � uma mensagem com a propriedade "correlationID" setada
	 * igual ao ID da mensagem enviada.
	 * 
	 * @param msg mensagem original da qual se espera uma resposta
	 * @param timeout tempo limite para se esperar a resposta. Zero para infinito.
	 * @return a mensagem de resposta ou nulo se n�o houver resposta
	 * @throws IllegalArgumentException caso n�o exista um messageListener setado.
	 */
	private Message waitForReply(Message msg, long timeout) throws JMSException
	{
		if(!msgListenerSet)
		    throw new IllegalStateException("Message Listener must be set to use sync sends");
		
		try
		{
		    syncReplyMessage = null;
		    
		    setSyncAwaitingID(msg.getJMSMessageID());
			
			wait(timeout);
			
			return syncReplyMessage;
		}
		catch(InterruptedException exc)
		{
		    logger.finest("Interrupted while waiting for reply");
		}
		
		return null;
		
	}
	
	private synchronized String getSyncAwaitingID()
	{
	    return syncAwaitingId;
	}
	
	private synchronized void setSyncAwaitingID(String id)
	{
	    syncAwaitingId = id;
	}
	
	/*------------------------------
	 * GENERAL METHODS
	 *------------------------------*/
	public void setMessageListener(MessageListener msgListener)
	{
	    msgListenerSet = (msgListener != null);
	    
	    this.listener = msgListener;
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

	
    public void onMessage(Message msg)
    {
        String awaitingID = getSyncAwaitingID();
        
        if(awaitingID != null)
        {
	        try
	        {
	            String correlationID = msg.getJMSCorrelationID();

	            if(correlationID != null && correlationID.equals(awaitingID))
	            {
	                syncReplyMessage = msg;
	                
	                setSyncAwaitingID(null);
	                
	                synchronized(this)
	                {
	                    notify();
	                }
	            }
	        }
	        catch(JMSException exc)
	        {
	            //JMSException jogada apenas por getCorrelationID, no caso, apenas
	            //desistimos de fazer o match da resposta esperada com a mensagem 
	            //recebida
	        }
        }

        //Notifica um possivel listener sobre a mensagem
        if(listener != null)
            listener.onMessage(msg);

    }

    /* (non-Javadoc)
     * @see javax.jms.ExceptionListener#onException(javax.jms.JMSException)
     */
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
