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

package br.org.indt.ndg.server.mail;import javax.mail.PasswordAuthentication;public class SMTPAuthenticator extends javax.mail.Authenticator{	 private String email;	 private String password;	 public SMTPAuthenticator(){	 }	 public SMTPAuthenticator(String email, String pw){		 this.email = email;		 this.password = pw;	 }    public PasswordAuthentication getPasswordAuthentication(){        return new PasswordAuthentication(this.email, this.password);    }	public String getEmail() {		return email;	}	public void setEmail(String email) {		this.email = email;	}	public String getPassword() {		return password;	}	public void setPassword(String password) {		this.password = password;	}}