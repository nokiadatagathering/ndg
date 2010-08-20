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
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import br.org.indt.jms.JMSSenderHelper;
import br.org.indt.jms.JMSSenderHelperException;
import br.org.indt.ndg.common.exception.EmailNotSentException;
import br.org.indt.ndg.common.exception.MSMSystemException;
import br.org.indt.ndg.common.exception.ModemException;
import br.org.indt.ndg.common.exception.SmsNotSentException;
import br.org.indt.ndg.server.client.ImeiVO;
import br.org.indt.ndg.server.client.UserVO;
import br.org.indt.ndg.server.sms.SMSModemHandler;
import br.org.indt.ndg.server.sms.SmsHandlerFactory;
import br.org.indt.ndg.server.sms.vo.SMSMessageVO;
import br.org.indt.ndg.server.util.PropertiesUtil;

@Stateless
public class MessageManagerBean implements MessageManager {
	private static final int EMAIL_RECOVERY_PASSWORD = 0;
	private static final int EMAIL_REQUEST_ACCESS = 1;
	private static final String EMAIL_SUBJECT_RECOVERY_PASSWORD = "Nokia Data Gathering - Recovery Password";
	private static final String EMAIL_SUBJECT_REQUEST_ACCESS = "Nokia Data Gathering - Request Access";
	private static final String TEMPLATE_EMAIL_REQUEST_ACCESS = "email_verification_link.txt";
	private static final String TEMPLATE_EMAIL_RECOVERY_PASSWORD = "email_recovery_password_link.txt";
	private static final String URL_VALIDATION_ACCESS = "/ndgFlex/swf/main.html?rakey";
	private static final String URL_RECOVERY_PASSWORD = "/ndgFlex/swf/main.html?rpkey";
	private Properties properties = PropertiesUtil
			.loadFileProperty(PropertiesUtil.SETTINGS_FILE);
	private static final Logger log = Logger.getLogger("smslog");

	@Override
	public void sendTextEmail(UserVO user, int typeOfEmail)
			throws MSMSystemException {
		try {
			EmailSender emailSender = new EmailSender();
			emailSender.postMail(createEmail(user, typeOfEmail));
		} catch (Exception e) {
			throw new EmailNotSentException();
		}
	}

	private Email createEmail(UserVO user, int typeOfEmail) throws Exception {
		ArrayList<String> listOfEmails = new ArrayList<String>();
		Map<String, String> emailContext = new HashMap<String, String>();
		Email email = new Email();
		switch (typeOfEmail) {
		case EMAIL_REQUEST_ACCESS:
			emailContext.put("__name", user.getFirstName());
			emailContext.put("__lastname", user.getLastName());
			emailContext.put("__username", user.getUsername());
			emailContext.put("__urlValidationKey", properties
					.getProperty("urlServer")
					+ URL_VALIDATION_ACCESS);
			emailContext.put("__validationKey", user.getValidationKey());
			emailContext.put("__email", user.getEmail());

			listOfEmails.add(user.getEmail());

			email.setSubject(EMAIL_SUBJECT_REQUEST_ACCESS);
			email.createText(TEMPLATE_EMAIL_REQUEST_ACCESS, emailContext);
			email.setCollRecipient(listOfEmails);
			break;
		case EMAIL_RECOVERY_PASSWORD:
			emailContext.put("__name", user.getFirstName());
			emailContext.put("__lastname", user.getLastName());
			emailContext.put("__username", user.getUsername());
			emailContext.put("__urlRecoveryPassword", properties
					.getProperty("urlServer")
					+ URL_RECOVERY_PASSWORD);
			emailContext.put("__validationKey", user.getValidationKey());
			emailContext.put("__email", user.getEmail());

			listOfEmails.add(user.getEmail());

			email.setSubject(EMAIL_SUBJECT_RECOVERY_PASSWORD);
			email.createText(TEMPLATE_EMAIL_RECOVERY_PASSWORD, emailContext);
			email.setCollRecipient(listOfEmails);
			break;
		}
		return email;
	}

	@Override
	public void sendSMS(SMSMessageVO vo) throws MSMSystemException {
		JMSSenderHelper jmsHelper;

		try {
			jmsHelper = new JMSSenderHelper("queue/MSM_SMSSender");
			jmsHelper.queue(vo);
		} catch (JMSSenderHelperException e) {
			throw new SmsNotSentException();
		}
	}

	@Override
	public void sendTextSMS(String message, ArrayList<String> listoOfDevices,
			int port) throws ModemException {
		log.debug("sendLinkSMS(3): " + message);
		if (SmsHandlerFactory.getInstance().getSmsHandler() instanceof SMSModemHandler) {
			JMSSenderHelper jmsHelper;

			for (String toMobile : listoOfDevices) {
				SMSMessageVO sms = new SMSMessageVO();
				sms.from = "";
				sms.to = toMobile;
				sms.port = port;
				sms.message = (port == SMSModemHandler.SMS_NDG_PORT) ? SMSMessageVO.SMS_BROADCASTING
						+ message
						: message;
				try {
					jmsHelper = new JMSSenderHelper("queue/MSM_SMSSender");
					jmsHelper.queue(sms);
				} catch (JMSSenderHelperException e) {
					throw new ModemException(e);
				} finally {
					sms = null;
				}
			}
		} else {
			throw new ModemException(
					"Modem is not ready to send or receive SMS.");
		}
	}

	@Override
	public void sendLinkSMS(String message, ArrayList<ImeiVO> listoOfDevices)
			throws ModemException {
		if (SmsHandlerFactory.getInstance().getSmsHandler() instanceof SMSModemHandler) {
			JMSSenderHelper jmsHelper;
			log.debug("sendLinkSMS(2): " + message);
			for (ImeiVO toMobile : listoOfDevices) {
				SMSMessageVO sms = new SMSMessageVO();
				sms.from = "";
				sms.to = toMobile.getMsisdn();
				sms.port = 0;
				sms.message = message;// + toMobile.getImei();
				try {
					jmsHelper = new JMSSenderHelper("queue/MSM_SMSSender");
					jmsHelper.queue(sms);
				} catch (JMSSenderHelperException e) {
					throw new ModemException(e);
				} finally {
					sms = null;
				}
			}
		} else {
			throw new ModemException("Modem is not ready to send/receive SMS.");
		}
	}
}
