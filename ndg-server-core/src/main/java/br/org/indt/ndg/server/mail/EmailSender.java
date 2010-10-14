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

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import br.org.indt.ndg.server.util.PropertiesUtil;

public class EmailSender {

	private Properties props, sessionProps;

	public EmailSender() {
		props = PropertiesUtil.loadFileProperty(PropertiesUtil.SETTINGS_FILE);
		sessionProps = new Properties();
		for (Enumeration keys = props.keys(); keys.hasMoreElements();) {
			String key = (String) keys.nextElement();
			if (key.startsWith("mail.")) {
				String value = props.getProperty(key);
				sessionProps.put(key, value);
			}
		}
	}

	public void postMail(Email email) throws MessagingException {
		boolean debug = props.getProperty("javamail.debug").equalsIgnoreCase(
				"true") ? true : false;

		Session session = Session.getDefaultInstance(sessionProps);
		session.setDebug(debug);

		Message msg = new MimeMessage(session);

		InternetAddress addressFrom = new InternetAddress(props
				.getProperty("smtp.mail.from"));

		Address arrayTo[] = new InternetAddress[email.getCollRecipient().size()];
		msg.setFrom(addressFrom);
		msg.setSubject(email.getSubject());
		msg.setContent(new String(email.getText()), "text/plain");

		StringBuffer mensagem = new StringBuffer();
		String linha;

		int i = -1;
		for (Iterator<String> iterator = email.getCollRecipient().iterator(); iterator
				.hasNext();) {
			arrayTo[++i] = new InternetAddress(iterator.next());

		}
		msg.setRecipients(Message.RecipientType.TO, arrayTo);
		Transport t = session.getTransport();
		try {
			t.connect(props.getProperty("mail.smtps.host"), new Integer(props
					.getProperty("mail.smtps.port")), props
					.getProperty("smtp.mail.user"), props
					.getProperty("smtp.mail.password"));
			t.sendMessage(msg, msg.getAllRecipients());
		} catch (Exception e){
			throw new MessagingException(e.getLocalizedMessage());
		}finally {
			t.close();
		}
	}

}
