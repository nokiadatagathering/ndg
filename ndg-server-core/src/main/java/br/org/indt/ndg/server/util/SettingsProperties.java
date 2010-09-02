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

package br.org.indt.ndg.server.util;

/**
 * @author samourao
 * 
 */
public class SettingsProperties {
	public static String SMTP_MAIL_FROM = "smtp.mail.from";
	public static String SMTP_MAIL_USER = "smtp.mail.user";
	public static String SMTP_MAIL_PASSWORD = "smtp.mail.password";
	public static String SMTP_MAIL_STARTTLS_ENABLE = "smtp.mail.starttls.enable";

	public static String MAIL_SMTPS_AUTH = "mail.smtps.auth";
	public static String MAIL_TRANSPORT_PROTOCOL = "mail.transport.protocol";
	public static String MAIL_SMTPS_HOST = "mail.smtps.host";
	public static String MAIL_SMTPS_PORT = "mail.smtps.port";

	public static String SMS_GATEWAY_ID = "sms.gateway.id";
	public static String SMS_API_ID = "sms.api.id";
	public static String SMS_USERNAME = "sms.username";
	public static String SMS_PASSWORD = "sms.password";

	public static String PROXY_SET = "proxy.set";
	public static String PROXY_HOST = "proxy.host";
	public static String PROXY_PORT = "proxy.port";

	public static String URLSERVER = "urlServer";

	public static String CLIENT_VERSION = "client.version";
	public static String CLIENT_OTA = "client.ota";
}
