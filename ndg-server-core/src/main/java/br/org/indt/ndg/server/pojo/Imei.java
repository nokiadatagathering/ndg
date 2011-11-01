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

package br.org.indt.ndg.server.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * Imei entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "imei")
@NamedQueries({
			@NamedQuery (name = "imei.findByUser", query = "SELECT I FROM Imei I WHERE I.user = :_user"),
			@NamedQuery (name = "imei.findByMsisdn", query = "SELECT I FROM Imei I WHERE I.msisdn like :msisdn")
			})
public class Imei implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	

	// Fields
	private String imei;
	private String msisdn;
	private Integer qtdeResults;
	private NdgUser user;
	private NdgDevice device;
	private char realImei;

	// Property accessors
	@Id
	@Column(name = "imei", nullable = false ,length = 15)
	public String getImei() {
		return this.imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	@Column(name = "msisdn", nullable = false, unique=true ,length = 25)
	public String getMsisdn() {
		return this.msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
		
	@Column(name = "qtdeResults")
	public void setQtdeResults(Integer qtdeResults) {
		this.qtdeResults = qtdeResults;
	}

	public Integer getQtdeResults() {
		return qtdeResults;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "idUser", nullable = false, referencedColumnName = "idUser")
	public NdgUser getUser() {
		return this.user;
	}

	public void setUser(NdgUser user) {
		this.user = user;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "idDevice", nullable = false)
	public NdgDevice getDevice() {
		return this.device;
	}

	public void setDevice(NdgDevice device) {
		this.device = device;
	}

	@Column(name = "realImei", nullable = false)
	public char getRealImei() {
		return realImei;
	}

	public void setRealImei(char realImei) {
		this.realImei = realImei;
	}
	
	
	
	
}