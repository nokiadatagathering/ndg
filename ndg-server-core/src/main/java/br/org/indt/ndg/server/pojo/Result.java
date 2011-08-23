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

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Result entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "results")
@NamedQueries({
	@NamedQuery (name = "result.findByIdResult", query = "SELECT U FROM Result U WHERE idResult = :idResult"),
	@NamedQuery (name = "result.findBySurvey", query = "SELECT U FROM Result U WHERE survey.idSurvey = :survey"),
	@NamedQuery (name = "result.getQtResultsBySurvey", query = "SELECT COUNT(idResult) FROM Result where survey.idSurvey = :IDSurvey")
})
public class Result implements java.io.Serializable {

	// Fields

	private String idResult;
	private Survey survey;
	private Imei imei;
	private String resultXml;
	private String latitude;
	private String longitude;
	private String title;
	private Date dateSaved;

	@Id
	@Column(name = "idResult", unique = true, nullable = false, length = 8)
	public String getIdResult() {
		return this.idResult;
	}

	public void setIdResult(String idResult) {
		this.idResult = idResult;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "idSurvey", nullable = false)
	public Survey getSurvey() {
		return this.survey;
	}

	public void setSurvey(Survey survey) {
		this.survey = survey;
	}
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "imei", nullable = false)
	public Imei getImei() {
		return this.imei;
	}

	public void setImei(Imei imei) {
		this.imei = imei;
	}

	@Column(name = "resultXML", length = 16777215)
	public String getResultXml() {
		return this.resultXml;
	}

	public void setResultXml(String resultXml) {
		this.resultXml = resultXml;
	}
	
	@Column(name = "latitude", length = 25)
	public String getLatitude()
	{
		return this.latitude;
	}

	public void setLatitude(String latitude)
	{
		this.latitude = latitude;
	}
	
	@Column(name = "longitude", length = 25)
	public String getLongitude()
	{
		return this.longitude;
	}

	public void setLongitude(String longitude)
	{
		this.longitude = longitude;
	}
	
	@Column(name = "title", length = 150)
	public String getTitle()
	{
		return this.title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "dateSaved", length = 150)
	public Date getDateSaved()
	{
		return this.dateSaved;
	}

	public void setDateSaved(Date dateSaved)
	{
		this.dateSaved = dateSaved;
	}
}