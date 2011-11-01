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

package br.org.indt.ndg.server.controller;

import br.org.indt.ndg.server.client.UserVO;

/**
 * @author samourao
 * 
 */
public class UserBalanceVO implements java.io.Serializable{

	private static final long serialVersionUID = 1L;
	private int idUserBalance;
	private UserVO user;
	private int users;
	private int imeis;
	private int sendAlerts;
	private int results;
	private int surveys;
	/**
	 * @return the idUserBalance
	 */
	public int getIdUserBalance() {
		return idUserBalance;
	}
	/**
	 * @param idUserBalance the idUserBalance to set
	 */
	public void setIdUserBalance(int idUserBalance) {
		this.idUserBalance = idUserBalance;
	}
	/**
	 * @return the user
	 */
	public UserVO getUser() {
		return user;
	}
	/**
	 * @param user the user to set
	 */
	public void setUser(UserVO user) {
		this.user = user;
	}
	/**
	 * @return the users
	 */
	public int getUsers() {
		return users;
	}
	/**
	 * @param users the users to set
	 */
	public void setUsers(int users) {
		this.users = users;
	}
	/**
	 * @return the imeis
	 */
	public int getImeis() {
		return imeis;
	}
	/**
	 * @param imeis the imeis to set
	 */
	public void setImeis(int imeis) {
		this.imeis = imeis;
	}
	/**
	 * @return the sendAlerts
	 */
	public int getSendAlerts() {
		return sendAlerts;
	}
	/**
	 * @param sendAlerts the sendAlerts to set
	 */
	public void setSendAlerts(int sendAlerts) {
		this.sendAlerts = sendAlerts;
	}
	/**
	 * @return the results
	 */
	public int getResults() {
		return results;
	}
	/**
	 * @param results the results to set
	 */
	public void setResults(int results) {
		this.results = results;
	}
	/**
	 * @return the surveys
	 */
	public int getSurveys() {
		return surveys;
	}
	/**
	 * @param surveys the surveys to set
	 */
	public void setSurveys(int surveys) {
		this.surveys = surveys;
	}
	
	
}
