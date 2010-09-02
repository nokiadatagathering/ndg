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

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Transactionlog entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "transactionlog")
@NamedQueries({
	@NamedQuery (name = "transactionlog.findByImeiType", query = "SELECT T FROM Transactionlog t where imei like :_imei and transactionType = _transactionType"),
	@NamedQuery (name = "transactionlog.findByUser", query = "SELECT T FROM Transactionlog t WHERE user like :_user")})
public class Transactionlog implements java.io.Serializable {

	// Fields

	private int idTransactionLog;
	private Result result;
	private Survey survey;
	private NdgUser user;
	private Imei imei;
	private String transactionType;
	private Date transactionDate;
	private String transactionStatus;
	private String address;
	private String transmissionMode;

	// Property accessors
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "idTransactionLog", unique = true, nullable = false)
	public int getIdTransactionLog() {
		return this.idTransactionLog;
	}

	public void setIdTransactionLog(int idTransactionLog) {
		this.idTransactionLog = idTransactionLog;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "idResult")
	public Result getResult() {
		return this.result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "idSurvey")
	public Survey getSurvey() {
		return this.survey;
	}

	public void setSurvey(Survey survey) {
		this.survey = survey;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "idUser", nullable = false)
	public NdgUser getUser() {
		return this.user;
	}

	public void setUser(NdgUser user) {
		this.user = user;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "imei")
	public Imei getImei() {
		return this.imei;
	}

	public void setImei(Imei imei) {
		this.imei = imei;
	}

	@Column(name = "transactionType", length = 2)
	public String getTransactionType() {
		return this.transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "transactionDate", length = 10)
	public Date getTransactionDate() {
		return this.transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	@Column(name = "transactionStatus", length = 20)
	public String getTransactionStatus() {
		return this.transactionStatus;
	}

	public void setTransactionStatus(String transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	@Column(name = "address", length = 20)
	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Column(name = "transmissionMode", length = 5)
	public String getTransmissionMode() {
		return this.transmissionMode;
	}

	public void setTransmissionMode(String transmissionMode) {
		this.transmissionMode = transmissionMode;
	}

}