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

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.log4j.Logger;

import br.org.indt.ndg.server.sms.vo.SMSMessageVO;

@MessageDriven(name = "SMSMessageSenderBean", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/MSM_SMSSender") })
public class SMSMessageSenderBean implements MessageListener {

	private static final Logger log = Logger.getLogger("smslog");

	public void onMessage(Message msg) {
		String typeOfSMS = null;
		log
				.info("##################### Reading from MSM_SMSSender Queue ########################");

		SMSMessageVO message = null;
		try {
			message = (SMSMessageVO) ((ObjectMessage) msg).getObject();
			typeOfSMS = message.message.substring(0, 1);
		} catch (JMSException e) {
			log.error("Error reading from JSM queue MSM_SMSSender. Error: "
							+ e.getMessage());
			new SMSSenderException("Error reading from JSM queue MSM_SMSSender");
		}
		log.info("Sending SMS message: " + "to:" + message.to + " - text: "
				+ message.message);
		if(typeOfSMS.startsWith(SMSMessageVO.SMS_BROADCASTING) || message.port == SMSModemHandler.SMS_NORMAL_PORT) {
			SmsHandlerFactory.getInstance().getSmsHandler().sendTextSMS(message.to,
					message.message, message.port);
		}else {
			SmsHandlerFactory.getInstance().getSmsHandler().sendSMS(message.to,
					message.message);	
		}
	}
}