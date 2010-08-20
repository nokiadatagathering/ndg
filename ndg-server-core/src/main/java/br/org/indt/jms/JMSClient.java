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

import java.util.Hashtable;
import java.util.Vector;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Session;

/**
 * @author Fabio
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public abstract class JMSClient implements ExceptionListener
{
	public static final int SESSION_MODE_AUTO_ACKNOWLEDGE = Session.AUTO_ACKNOWLEDGE;
	public static final int SESSION_MODE_CLIENT_ACKNOWLEDGE = Session.CLIENT_ACKNOWLEDGE;
	public static final int SESSION_MODE_DUPS_OK_ACKNOWLEDGE = Session.DUPS_OK_ACKNOWLEDGE;
	public static final int SESSION_MODE_TRANSACTED = Session.SESSION_TRANSACTED;
	
	public static final int DEFAULT_RECONNECT_ATTEMPTS = 5;
	
	
	private class ConnectionHolder implements ExceptionListener
	{
		String connectionFactoryName;
		String connectionUsername;
		
		Connection connection;
		
		//vetor que contera todos os clientes usando esta conexao
		Vector clients;
		
		int usageCount;
		
		public ConnectionHolder(String connectionFactoryName, String connectionUsername)
		{
			this.connectionFactoryName = connectionFactoryName;
			this.connectionUsername = connectionUsername;
			
			clients = new Vector();
		}
		
		public void onException(JMSException exc)
	 	{
			//remove este holder da tabela para que um novo pedido de conexao para
			//este connectionFactoryName crie uma nova conexao
			removeConnectionHolder(connectionFactoryName,connectionUsername);
            
            try
            {
                connection.close();
            }
            catch(Exception e)
            {
            }
			
			//A conexao do jndi pode ter caido tb..
			try
			{
				JMSUtil.geConnectionFactory(connectionFactoryName);
			}
			catch(Exception exc2)
			{
				//fecha o contexto para q ela possa ser novamente procurada
				shutdown();
			}
			
			
			//Notifica todos os clientes para que eles possam se recuperar 
			notifyException(exc);
		}
		
		//TODO Este m�todo de notificacao linear na verdade
		//fara com que apenas o primeiro cliente seja notificado
		//imediatamente da desconexao. Os clientes seguintes serao
		//notificados apenas apos o primeiro ter retornado e, como o cliente
		//abre uma nova conexao neste m�todo, os clientes seguintes somente ser�o
		//notificados apos o primeiro ter conseguido abrir a conexao. (Ou apos o primeiro
		//ter desistido de reabrir a conexao) 		
		private void notifyException(JMSException exc)
		{
			for(int i = 0; i < clients.size(); i++)
			{
				try
				{
					((JMSClient)clients.elementAt(i)).onException(exc);
				}
				catch(Exception exc2)
				{
					//ignora erro em listener
				}
			}
		}

	}
	
	private static Hashtable connectionsTable;
	
	protected ExceptionListener excListener;
	protected int reconnectAttempts;
	
	protected boolean autoReconnect;
    
    /**
     * 
     */
    protected JMSClient()
    {
        super();

		reconnectAttempts = DEFAULT_RECONNECT_ATTEMPTS;	
		
		autoReconnect = true;		

		synchronized(JMSClient.class)
		{
			if(connectionsTable == null)
				connectionsTable = new Hashtable();
		}
    }
    
    protected synchronized Connection getConnection(String connectionFactoryName, String connectionUsername, String connectionPassword) throws Exception
    {
    	
    	ConnectionHolder ch = (ConnectionHolder)connectionsTable.get( getConnectionId(connectionFactoryName,connectionUsername) );
       
    	if(ch == null)
    	{
			ConnectionFactory connectionFactory = JMSUtil.geConnectionFactory(connectionFactoryName);
			
			Connection connection = connectionFactory.createConnection(connectionUsername, connectionPassword);
			
			ch = new ConnectionHolder(connectionFactoryName, connectionUsername);
			
			ch.connection = connection;
			
			connection.setExceptionListener(ch);
			
			connectionsTable.put(getConnectionId(connectionFactoryName,connectionUsername), ch);
    	}
		
		ch.clients.add(this);
		    	
		ch.usageCount++;
    	
    	return ch.connection;
    	
		/*
		ConnectionFactory connectionFactory = JMSUtil.geConnectionFactory(connectionFactoryName);
		
		return connectionFactory.createConnection(connectionUsername, connectionPassword);
    	*/
    }
    
    protected synchronized void closeConnection(String connectionFactoryName, String connectionUsername) throws JMSException
    {
		ConnectionHolder ch = (ConnectionHolder)connectionsTable.get( getConnectionId( connectionFactoryName, connectionUsername) );
    	
		if(ch != null)
		{
			ch.usageCount--;
			
			ch.clients.remove(this);
			
			if(ch.usageCount == 0)
			{
				connectionsTable.remove( getConnectionId(connectionFactoryName,connectionUsername) );
				//no more connection users
				ch.connection.close();
			}
		}
		else
		{
			//conexao nao existe??
		}
		
    }
    
    private synchronized void removeConnectionHolder(String connectionFactoryName,String connectionUsername)
    {
    	connectionsTable.remove( getConnectionId(connectionFactoryName, connectionUsername) );
    }
    
    private String getConnectionId(String connectionFactoryName, String connectionUsername)
    {
    	StringBuffer sb = new StringBuffer();
    	return sb.append(connectionFactoryName).append('_').append(connectionUsername).toString();
    }
    
    public static void shutdown()
    {
    	try
    	{
    	   	JMSUtil.closeContext();
    	}
    	catch(Exception exc){}
    }
    
	/**
	  * @return
	  */
	 public ExceptionListener getExcListener()
	 {
		 return excListener;
	 }

	 /**
	  * @param listener
	  */
	 public void setExcListener(ExceptionListener listener)
	 {
		 excListener = listener;
	 }

	 /**
	  * @return
	  */
	 public int getReconnectAttempts()
	 {
		 return reconnectAttempts;
	 }

	 /**
	  * @param i
	  */
	 public void setReconnectAttempts(int i)
	 {
		 reconnectAttempts = i;
	 }

    /**
     * @return
     */
    public boolean isAutoReconnect()
    {
        return autoReconnect;
    }

    /**
     * @param b
     */
    public void setAutoReconnect(boolean b)
    {
        autoReconnect = b;
    }

}
