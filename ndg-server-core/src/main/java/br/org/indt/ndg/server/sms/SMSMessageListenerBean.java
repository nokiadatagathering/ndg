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

import br.org.indt.ndg.server.sms.handler.MessageProtocolHandler;
import br.org.indt.ndg.server.sms.vo.MessageHeaderVO;
import br.org.indt.ndg.server.sms.vo.ResultMessageHeaderVO;
import br.org.indt.ndg.server.sms.vo.SMSCommMessageHeaderVO;
import br.org.indt.ndg.server.sms.vo.SMSMessageVO;

@MessageDriven(name = "SMSMessageListenerBean", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/MSM_SMSReceiver") })
public class SMSMessageListenerBean implements MessageListener {
	private static final Logger log = Logger.getLogger("smslog");

	public void onMessage(Message msg) {

		log
				.info("##################### Reading from MSM_SMSReceiver Queue ########################");
		MessageHeaderVO messageHeaderVO = null;
		SMSMessageVO message = null;
		try {
			message = (SMSMessageVO) ((ObjectMessage) msg).getObject();
			log.info("Message = " + message.message);

			messageHeaderVO = MessageProtocolHandler
					.retrieveProtocolHeader(message.message);
		} catch (InvalidMessageTypeException e) {
			log.error("ERROR-" + e.getMessage());
			new InvalidMessageTypeException("SMS Received: " + e.getMessage());

		} catch (JMSException e) {
			log.error(e.getMessage());
			new SMSReceiverException(
					"Error reading from JSM queue MSM_SMSReceiver");
		}

		if (messageHeaderVO instanceof ResultMessageHeaderVO) {
			ResultMessageHeaderVO resulHeaderVO = (ResultMessageHeaderVO) messageHeaderVO;
			MessageProtocolHandler m = MessageProtocolHandler
					.getInstance(resulHeaderVO.surveyId
							+ resulHeaderVO.resultId);
			if (message.message.endsWith("#")) {
				m.messageNumberTotal = resulHeaderVO.messageNumber;
			}
			m.preHandleMessage(resulHeaderVO, message.message, message);
			m = null;
		} else if (messageHeaderVO instanceof SMSCommMessageHeaderVO) {
			SMSCommMessageHeaderVO smsCommMessageHeaderVO = (SMSCommMessageHeaderVO) messageHeaderVO;
			MessageProtocolHandler m = MessageProtocolHandler
					.getInstance(smsCommMessageHeaderVO.getImei()
							+ smsCommMessageHeaderVO.getTimestamp());
			m.preHandleMessage(smsCommMessageHeaderVO, smsCommMessageHeaderVO.getPayload(), message);
		}

	}

}
