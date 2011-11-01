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

package br.org.indt.ndg.server.mail;import java.io.BufferedReader;import java.io.IOException;import java.io.InputStreamReader;import java.io.Serializable;import java.net.URL;import java.util.ArrayList;import java.util.Collection;import java.util.Iterator;import java.util.Map;import java.util.regex.Matcher;import java.util.regex.Pattern;public class Email implements Serializable {	/**	 * 	 */	private static final long serialVersionUID = 1L;	private Collection<String> collRecipient;	private String subject;	private String text;	public Email() {		this.collRecipient = new ArrayList<String>();	}	public String getSubject() {		return subject;	}	public void setSubject(String subject) {		this.subject = subject;	}	public String getText() {		return text;	}	public void setText(String text) {		this.text = text;	}	public Collection<String> getCollRecipient() {		return collRecipient;	}	public void setCollRecipient(Collection<String> collRecipient) {		this.collRecipient = collRecipient;	}	public void addRecipient(String recipient) {		this.collRecipient.add(recipient);	}	public void createText(String template,			Map<String, String> emailContext) throws Exception {		InputStreamReader in = null;		String emailText;		StringBuffer stringBuffer;		URL url = this.getClass().getClassLoader().getResource(				"META-INF/"+template);//		System.out.println(">>>> URL: " + url.getPath());		in = new InputStreamReader(url.openStream(), "UTF-8");		BufferedReader survey = new BufferedReader(in);		StringBuffer sb = new StringBuffer();		String line = null;		try {			while ((line = survey.readLine()) != null) {				sb.append(line + "\n");			}		} catch (IOException e) {			e.printStackTrace();		}				for (Iterator<String> iterator = emailContext.keySet().iterator(); iterator.hasNext();) {			String key = iterator.next();			Pattern pattern = Pattern.compile(key);			Matcher matcher = pattern.matcher(sb);			sb = new StringBuffer(matcher.replaceAll(emailContext.get(key)));		}		setText(sb.toString());	}	public String toString() {		String strEmail = "Recipient: ";		for (String recipient : this.getCollRecipient())			strEmail += recipient + ",";		strEmail += "CC: ";		strEmail += "Text: ";		strEmail += this.getText();		return strEmail;	}}