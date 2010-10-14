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

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SenderEmail {

	public static void main(String[] args) {
		Properties p = new Properties();
		p.put("mail.host", "smtp.gmail.com");
		Session session = Session.getInstance(p, null);
		MimeMessage msg = new MimeMessage(session);
		try {
			// "de" e "para"!!
			msg.setFrom(new InternetAddress("sau.manaus@gmail.com"));
			msg.setRecipient(Message.RecipientType.TO, new InternetAddress(
					"scion-reloaded@yahoogrupos.com.br"));

			// nao esqueca da data!
			// ou ira 31/12/1969 !!!
			msg.setSentDate(new Date());

			msg.setSubject("Teste JavaMail");

			msg.setText("Não responder ...");

			// evniando mensagem (tentando)
			Transport.send(msg);
		} catch (AddressException e1) {
			// nunca deixe catches vazios!
		} catch (MessagingException e) {
			// nunca deixe catches vazios!
		}
	}

}
