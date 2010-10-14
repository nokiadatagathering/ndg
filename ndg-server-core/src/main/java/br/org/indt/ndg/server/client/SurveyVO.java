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

public class SurveyVO implements Serializable 
{
	private static final long serialVersionUID = 1L;

	private String idSurvey;
	private int results;
	private String title;
	private String status;
	private String device;
	private String pending;
	private UserVO userAdmin;
	private String date;
	private String check;
	private String user;
	private String survey;
	private int resultsSent;
	private char isUploaded;

	public String getIdSurvey() 
	{
		return idSurvey;
	}

	public void setIdSurvey(String idSurvey) 
	{
		this.idSurvey = idSurvey;
	}

	public void setResults(int results)
	{
		this.results = results;
	}

	public int getResults()
	{
		return results;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public int getResultsSent()
	{
		return resultsSent;
	}

	public void setResultsSent(int resultsSent)
	{
		this.resultsSent = resultsSent;
	}

	public String getDevice()
	{
		return device;
	}

	public void setDevice(String device)
	{
		this.device = device;
	}

	public String getPending()
	{
		return pending;
	}

	public void setPending(String pending)
	{
		this.pending = pending;
	}

	public UserVO getUserAdmin()
	{
		return userAdmin;
	}

	public void setUserAdmin(UserVO user)
	{
		this.userAdmin = user;
	}

	public String getDate()
	{
		return date;
	}

	public void setDate(String date)
	{
		this.date = date;
	}

	public String getCheck()
	{
		return check;
	}

	public void setCheck(String check)
	{
		this.check = check;
	}

	public void setUser(String user)
	{
		this.user = user;
	}

	public String getUser()
	{
		return user;
	}

	public void setSurvey(String survey)
	{
		this.survey = survey;
	}

	public String getSurvey()
	{
		return survey;
	}
	
	public char getIsUploaded()
	{
		return isUploaded;
	}

	public void setIsUploaded(char isUploaded)
	{
		this.isUploaded = isUploaded;
	}

	@Override
	public boolean equals(Object obj) 
	{
		if (obj == this)
		{
			return true;
		}
		
		if (!(obj instanceof SurveyVO))
		{
			return false;
		}
		
		SurveyVO tocompare = (SurveyVO)obj;
		
		if (tocompare.getIdSurvey().equals(this.idSurvey))
		{
			return true;
		}
		
		return false;
	}
	
	@Override
	public int hashCode() 
	{
		return Integer.parseInt(idSurvey);
	}
}