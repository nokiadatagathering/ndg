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
 * Survey entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "surveys")
@NamedQueries({
	@NamedQuery (name = "survey.findByUserAdmin", query = "SELECT U FROM Survey U WHERE idUser like :userAdmin"),
	@NamedQuery (name = "survey.getUserBySurvey", query = "SELECT U FROM Survey U where idSurvey = :IDSurvey"),
	@NamedQuery (name = "survey.findByUser", query = "SELECT S FROM Survey S WHERE idUserAdmin like :_user")})	
public class Survey implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Fields

	private String idSurvey;
	private NdgUser userAdmin;
	private String surveyXml;
	private char isUploaded;

	// Property accessors
	@Id
	@Column(name = "idSurvey", unique = true, nullable = false, length = 10)
	public String getIdSurvey() {
		return this.idSurvey;
	}

	public void setIdSurvey(String idSurvey) {
		this.idSurvey = idSurvey;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "idUser", nullable = false)
	public NdgUser getIdUserAdmin() {
		return this.userAdmin;
	}

	public void setIdUserAdmin(NdgUser userAdmin) {
		this.userAdmin = userAdmin;
	}

	@Column(name = "surveyXML", length = 65535)
	public String getSurveyXml() {
		return this.surveyXml;
	}

	public void setSurveyXml(String surveyXml) {
		this.surveyXml = surveyXml;
	}
	
	@Column(name = "isUploaded", nullable = false)
	public void setIsUploaded(char isUploaded) {
		this.isUploaded = isUploaded;
	}

	public char getIsUploaded() {
		return isUploaded;
	}
}
