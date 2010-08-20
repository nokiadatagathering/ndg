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

package br.org.indt.ndg.server.mail;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import br.org.indt.ndg.common.exception.MSMApplicationException;
import br.org.indt.ndg.server.client.ImeiVO;
import br.org.indt.ndg.server.client.MSMBusinessDelegate;
import br.org.indt.ndg.server.util.PropertiesUtil;

/**
 * @author samourao
 *
 */
public class SendTextSms extends Thread {
	private String textMessage;
	private boolean sendLink = false;
	private ArrayList<String> listOfDevices = new ArrayList<String>();
	private ArrayList<ImeiVO> listOfImeiVO = new ArrayList<ImeiVO>();
	private int port = 0;
	private MessageManager messageManager = null;
	private MSMBusinessDelegate businessDelegate = new MSMBusinessDelegate();
	private Properties properties;
	private static final Logger log = Logger.getLogger("smslog");

	public SendTextSms(String textMessage, ArrayList<String> listOfDevices,
			int port) throws MSMApplicationException {
		this.textMessage = textMessage;
		this.port = port;
		InitialContext ctx;
		try {
			ctx = new InitialContext();
			messageManager = (MessageManager) ctx
					.lookup("ndg-core/MessageManagerBean/remote");
		} catch (NamingException e) {
			e.printStackTrace();
		}

		for (String imei : listOfDevices) {
			this.listOfDevices.add(businessDelegate.getImei(imei).getMsisdn());
		}
	}

	public SendTextSms(String message, ArrayList<ImeiVO> listoOfDevices) {
		properties = PropertiesUtil
		.loadFileProperty(PropertiesUtil.JNDI_PROPERTIES);
		this.textMessage = message;
		this.listOfImeiVO = listoOfDevices;
		sendLink = true;
		InitialContext ctx;
		try {
			ctx = new InitialContext();
			messageManager = (MessageManager) ctx
					.lookup("ndg-core/MessageManagerBean/local");
		} catch (NamingException e) {
			System.out.println("InitialContext Hard Coded");
			Hashtable<String, String> env = new Hashtable<String, String>();
			env.put(Context.INITIAL_CONTEXT_FACTORY,
					"org.jnp.interfaces.NamingContextFactory");
			env.put(Context.PROVIDER_URL, properties.getProperty("java.naming.provider.url"));
			try {
				ctx = new InitialContext(env);
				messageManager = (MessageManager) ctx
						.lookup("ndg-core/MessageManagerBean/remote");
			}catch (NamingException e1) {
				e1.printStackTrace();
			}
				
		}
	}

	public void run() {
		log.debug("SendTextSms " + sendLink);
		if(sendLink)
			messageManager.sendLinkSMS(textMessage, listOfImeiVO);
		else
			messageManager.sendTextSMS(textMessage, listOfDevices, port);
	}
}
