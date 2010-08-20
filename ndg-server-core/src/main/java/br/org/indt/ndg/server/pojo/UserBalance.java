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

package br.org.indt.ndg.server.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * @author samourao
 * 
 */
@Entity
@Table(name = "userbalance")
@NamedQueries({@NamedQuery(name = "userbalance.findByUserAdmin", query = "SELECT U FROM UserBalance U WHERE user.username like :useradmin")})
public class UserBalance {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idUSerBalance;

	@OneToOne
	@JoinColumn(name = "idUser", nullable = false)
	public NdgUser user;

	@Column(name = "users", nullable = false)
	public Integer users;

	@Column(name = "imeis", nullable = false)
	public Integer imeis;

	@Column(name = "sendAlerts", nullable = false)
	public Integer sendAlerts;

	@Column(name = "results", nullable = false)
	public Integer results;

	@Column(name = "surveys", nullable = false)
	public Integer surveys;

	/**
	 * @return the user
	 */
	public NdgUser getUser() {
		return user;
	}

	/**
	 * @param user
	 *            the user to set
	 */
	public void setUser(NdgUser user) {
		this.user = user;
	}

	public void setIdUSerBalance(int idUSerBalance) {
		this.idUSerBalance = idUSerBalance;
	}

	public int getIdUSerBalance() {
		return idUSerBalance;
	}

	/**
	 * @return the users
	 */
	public Integer getUsers() {
		return users;
	}

	/**
	 * @param users
	 *            the users to set
	 */
	public void setUsers(Integer users) {
		this.users = users;
	}

	/**
	 * @return the imeis
	 */
	public Integer getImeis() {
		return imeis;
	}

	/**
	 * @param imeis
	 *            the imeis to set
	 */
	public void setImeis(Integer imeis) {
		this.imeis = imeis;
	}

	/**
	 * @return the sendAlerts
	 */
	public Integer getSendAlerts() {
		return sendAlerts;
	}

	/**
	 * @param sendAlerts
	 *            the sendAlerts to set
	 */
	public void setSendAlerts(Integer sendAlerts) {
		this.sendAlerts = sendAlerts;
	}

	/**
	 * @return the results
	 */
	public Integer getResults() {
		return results;
	}

	/**
	 * @param results
	 *            the results to set
	 */
	public void setResults(Integer results) {
		this.results = results;
	}

	/**
	 * @return the surveys
	 */
	public Integer getSurveys() {
		return surveys;
	}

	/**
	 * @param surveys
	 *            the surveys to set
	 */
	public void setSurveys(Integer surveys) {
		this.surveys = surveys;
	}

}
