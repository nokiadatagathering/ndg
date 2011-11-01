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

/**
 * @author Fabio
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
import java.io.InputStream;
import java.util.Properties;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Queue;
import javax.jms.QueueConnectionFactory;
import javax.jms.Topic;
import javax.jms.TopicConnectionFactory;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
public class JMSUtil 
{
	
	private static Context jndiContext = null;    
	
	/**
	* Returns a ConnectionFactory object.
	*
	* @return a ConnectionFactory object
	
	* @throws javax.naming.NamingException (or other exception) if name cannot be found
	*/
	public static ConnectionFactory geConnectionFactory(String name) throws Exception {
		
		return (ConnectionFactory) jndiLookup(name);
	}
	/**
	* Returns a QueueConnectionFactory object.
	*
	* @return a QueueConnectionFactory object
	
	* @throws javax.naming.NamingException (or other exception) if name cannot be found
	*/
	public static QueueConnectionFactory getQueueConnectionFactory(String name) throws Exception {
		
		return (QueueConnectionFactory) jndiLookup(name);
	}
	/**
	* Returns a TopicConnectionFactory object.
	*
	* @return a TopicConnectionFactory object
	* @throws javax.naming.NamingException (or other exception) if name cannot be found
	*/
	public static TopicConnectionFactory getTopicConnectionFactory(String name) throws Exception {
	
		return (TopicConnectionFactory) jndiLookup(name);
	}
	
	/**
	* Returns a Destination object.
	*
	* @param name String specifying destination name
	*
	* @return a Destination object
	* @throws javax.naming.NamingException (or other exception) if name cannot be found
	*/
	public static Destination getDestination(String name) throws Exception {
		
		return (Destination)jndiLookup(name);
	}
	
	/**
	* Returns a Queue object.
	*
	* @param name String specifying queue name
	* @param session a QueueSession object
	*
	* @return a Queue object
	* @throws javax.naming.NamingException (or other exception) if name cannot be found
	*/
	public static Queue getQueue(String name) throws Exception {
		
		return (Queue) jndiLookup(name);
	}
	/**
	* Returns a Topic object.
	*
	* @param name String specifying topic name
	* @param session a TopicSession object
	*
	* @return a Topic object
	* @throws javax.naming.NamingException (or other
	* exception) if name cannot be found
	*/
	public static Topic getTopic(String name) throws Exception {
		
		return (Topic) jndiLookup(name);
	}
	
	/**
	* Creates a JNDI API InitialContext object if none exists
	* yet. Then looks up the string argument and returns the
	* associated object.
	*
	* @param name the name of the object to be looked up
	*
	* @return the object bound to name
	* @throws javax.naming.NamingException (or other
	* exception) if name cannot be found
	*/
	public static Object jndiLookup(String name) throws Exception 
	{
	
		Object obj = null;
		
		if (jndiContext == null) 
		{
				//determine classLoader
				
			ClassLoader cl = JMSUtil.class.getClassLoader();
			
			InputStream is = cl.getResourceAsStream("m4u-util-jndi.properties");
			
			if(is == null) {
                Logger.getLogger(JMSUtil.class).debug("N�o foi possivel encontrar o arquivo m4u-util-jndi.properties. Ser� usado jndi default.");
                jndiContext = new InitialContext();
            }
            else
            {
                Properties props = new Properties();
                props.load(is);
                jndiContext = new InitialContext(props);
            }
		}
		
		obj = jndiContext.lookup(name);
	
		return obj;
	}
	
	/**
	 * 
	 * Fecha o contexto jndi.
	 * 
	 * Em alguns casos pode ser necess�rio caso o contexto 
	 * utilize recursos externos. Por exemplo, ao se utilizar o swiftmq
	 * o contexto deve ser fechado pois ele usa uma conexao JMS
	 * 
	 */
	
	public static void closeContext() throws NamingException
	{
		if(jndiContext != null)
			jndiContext.close();
			
		jndiContext = null;
	}
	
	
	/**
	* Waits for 'count' messages on controlQueue before
	* continuing. Called by a publisher to make sure that
	* subscribers have started before it begins publishing
	* messages.
	*
	* If controlQueue does not exist, the method throws an
	* exception.
	*
	* @param prefix prefix (publisher or subscriber) to be
	* displayed
	* @param controlQueueName name of control queue
	* @param count number of messages to receive
	
	public static void receiveSynchronizeMessages(String prefix, String controlQueueName, int count) throws Exception 
	{
		QueueConnectionFactory queueConnectionFactory = null;
		QueueConnection queueConnection = null;
		QueueSession queueSession = null;
		Queue controlQueue = null;
		QueueReceiver queueReceiver = null;
		
		try 
		{
			queueConnectionFactory = JMSUtil.getQueueConnectionFactory();
			queueConnection = queueConnectionFactory.createQueueConnection();
			queueSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
			
			controlQueue = getQueue(controlQueueName, queueSession);
			
			queueConnection.start();
		}
		catch (Exception e) 
		{
			System.err.println("Connection problem: " +	e.toString());
	
			if (queueConnection != null) 
			{
				try
				{
					queueConnection.close();
				} 
				catch (JMSException ee) {}
			}
			
			throw e;
		}
		
		try 
		{
			System.out.println(prefix +	"Receiving synchronize messages from " + controlQueueName + "; count = " + count);
			
			queueReceiver = queueSession.createReceiver(controlQueue);
			
			while (count > 0) 
			{
				queueReceiver.receive();
				count--;
				System.out.println(prefix + "Received synchronize message; " + " expect " + count + " more");
			}
		} 
		catch (JMSException e) 
		{
			System.err.println("Exception occurred: " +	e.toString());
			throw e;
		} 
		finally 
		{
			if (queueConnection != null) 
			{
				try 
				{
					queueConnection.close();
				} 
				catch (JMSException e) {}
			}
		}
	}
	*/
	/**
	
	* Sends a message to controlQueue. Called by a subscriber
	* to notify a publisher that it is ready to receive
	* messages.
	* <p>
	* If controlQueue doesn't exist, the method throws an
	* exception.
	*
	* @param prefix prefix (publisher or subscriber) to be
	* displayed
	* @param controlQueueName name of control queue
	
	public static void sendSynchronizeMessage(String prefix, String controlQueueName) throws Exception 
	{
		QueueConnectionFactory queueConnectionFactory = null;
		QueueConnection queueConnection = null;
		QueueSession queueSession = null;
		Queue controlQueue = null;
		QueueSender queueSender = null;
		TextMessage message = null;
	
		try 
		{
			queueConnectionFactory = JMSUtil.getQueueConnectionFactory();
			queueConnection = queueConnectionFactory.createQueueConnection();
			queueSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
			controlQueue = getQueue(controlQueueName, queueSession);
		}
		catch (Exception e) 
		{
			System.err.println("Connection problem: " + e.toString());
			
			if (queueConnection != null) 
			{
				try 
				{
					queueConnection.close();
				} catch (JMSException ee) {}
			}
	
			throw e;
		}
	
		try 
		{
			queueSender = queueSession.createSender(controlQueue);
			message = queueSession.createTextMessage();
			message.setText("synchronize");
			System.out.println(prefix +	"Sending synchronize message to " +	controlQueueName);
			queueSender.send(message);
		} 
		catch (JMSException e) 
		{
			System.err.println("Exception occurred: " +	e.toString());
			throw e;
		} 
		finally 
		{
			if (queueConnection != null) 
			{
				try 
				{
					queueConnection.close();
				} catch (JMSException e) {}
			}
		}
	}
	*/
	/**
	* Monitor class for asynchronous examples. Producer signals
	* end of message stream; listener calls allDone() to notify
	* consumer that the signal has arrived, while consumer calls
	* waitTillDone() to wait for this notification.
	*/
	static public class DoneLatch 
	{
		boolean done = false;
	
		/**
		* Waits until done is set to true.
		*/
		public void waitTillDone() 
		{
			synchronized (this) 
			{
				while (! done) 
				{
					try 
					{
						this.wait();
					} catch (InterruptedException ie) {}
				}
			}
		}
	
		/**
		* Sets done to true.
		*/
		public void allDone() 
		{
			synchronized (this) 
			{
				done = true;
				this.notify();
			}
		}
	}

}
