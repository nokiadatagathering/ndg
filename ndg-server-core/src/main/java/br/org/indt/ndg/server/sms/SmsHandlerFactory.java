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

package br.org.indt.ndg.server.sms;

import java.util.Properties;

import br.org.indt.ndg.server.util.PropertiesUtil;

/**
 * @author samourao
 *
 */
public class SmsHandlerFactory {
	
	private SmsHandlerAbs smsHandler = null;
	
	private static SmsHandlerFactory instance = null;
	
	public static SmsHandlerFactory getInstance() {
		if (instance == null) {
			synchronized (SmsHandlerFactory.class) {
				if (instance == null) {
					instance = new SmsHandlerFactory();
				}
			}
		}
		return instance;
	}
	
	public SmsHandlerAbs getSmsHandler() {
		Properties properties = PropertiesUtil.loadFileProperty(PropertiesUtil.PROPERTIES_CORE_FILE);
		if(smsHandler == null) {
			if (properties.getProperty("SMS_SUPPORT").equalsIgnoreCase("true")) {
				smsHandler = SMSModemHandler.getInstance();
			}
		}
		return smsHandler;
	}
	
	public boolean hasSmsSupport(){
		boolean result = false;
		Properties properties = PropertiesUtil.loadFileProperty(PropertiesUtil.PROPERTIES_CORE_FILE);
		String pp = properties.getProperty("SMS_SUPPORT");
		if (pp != null && pp.equalsIgnoreCase("true")){
			result = true;
		}
		return result;
	}
	

}
