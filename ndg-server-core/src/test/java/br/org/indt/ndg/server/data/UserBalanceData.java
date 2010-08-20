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

package br.org.indt.ndg.server.data;

/** 
 * Simple class that represents user balance 
 */

public class UserBalanceData {
	private int users;
	private int imeis;
	private int sendAlerts;
	private int results;
	private int surveys;
	
	UserBalanceData(){
		
	}
	
	public int getUsers() {
		return users;
	}
	public void setUsers(int users) {
		this.users = users;
	}
	public int getImeis() {
		return imeis;
	}
	public void setImeis(int imeis) {
		this.imeis = imeis;
	}
	public int getSendAlerts() {
		return sendAlerts;
	}
	public void setSendAlerts(int sendAlerts) {
		this.sendAlerts = sendAlerts;
	}
	public int getResults() {
		return results;
	}
	public void setResults(int results) {
		this.results = results;
	}
	public int getSurveys() {
		return surveys;
	}
	public void setSurveys(int surveys) {
		this.surveys = surveys;
	}
}
