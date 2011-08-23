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

package br.org.indt.ndg.server.client;

import java.io.Serializable;

public class ResultVO implements Serializable 
{
	private static final long serialVersionUID = 1L;
	
	private String surveyId;
	private String idResult;
	private String imei;
	private String user;
	private String title;
	private String lat;
	private String lon;
	private String date;
	private String time;
	private String resultXml;
	private boolean hasCoordinates;

	public String getIdResult() 
	{
		return idResult;
	}

	public void setIdResult(String idResult) 
	{
		this.idResult = idResult;
	}

	public ResultVO() 
	{
		// empty
	}

	public ResultVO(String idResult, String surveyId, String imei, String title, String lat, String lon) 
	{
		this.hasCoordinates = false;
		this.idResult = idResult;
		this.surveyId = surveyId;
		this.imei = imei;
		this.title = title;
		this.setLat(lat);
		this.setLon(lon);
	}

	public String getSurveyId() 
	{
		return surveyId;
	}

	public void setSurveyId(String surveyId) 
	{
		this.surveyId = surveyId;
	}

	public String getTitle() 
	{
		return title;
	}

	public void setTitle(String title) 
	{
		this.title = title;
	}

	public String getLat() 
	{
		return lat;
	}

	public void setLat(String lat) 
	{
		if (lat != null) 
		{
			this.hasCoordinates = true;
			this.lat = lat;
		}
		else 
		{
			this.hasCoordinates = false;
		}
	}

	public String getLon() 
	{
		return lon;
	}

	public void setLon(String lon) 
	{
		if (lon != null) 
		{
			this.hasCoordinates = true;
			this.lon = lon;
		}
		else 
		{
			this.hasCoordinates = false;
		}
	}

	public boolean hasCoordinates() 
	{
		return hasCoordinates;
	}

	public String getUser() 
	{
		return user;
	}

	public void setUser(String user) 
	{
		this.user = user;
	}

	public String getImei() 
	{
		return imei;
	}

	public void setImei(String imei) 
	{
		this.imei = imei;
	}

	public String getDate() 
	{
		return date;
	}

	public void setDate(String date) 
	{
		this.date = date;
	}
	
	public String getTime() 
	{
		return time;
	}

	public void setTime(String time) 
	{
		this.time = time;
	}

	public void setResultXml(String resultXml) 
	{
		this.resultXml = resultXml;
	}

	public String getResultXml() 
	{
		return resultXml;
	}
}