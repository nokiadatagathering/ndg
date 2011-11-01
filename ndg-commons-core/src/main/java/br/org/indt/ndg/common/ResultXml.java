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

package br.org.indt.ndg.common;

import java.io.Serializable;
import java.util.TreeMap;

import org.w3c.dom.Document;

public class ResultXml implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String surveyId;
	private String resultId;
	private String phoneNumber;
	private String imei;
	private String user;
	private String time;                 //Date saved
	private String timeSent;             //Date sent
	private String title;
	private String latitude;
	private String longitude;
	private Document xmldoc;
	
	private TreeMap<Integer, CategoryAnswer> categories;
	
	public ResultXml() {
		categories = new TreeMap<Integer, CategoryAnswer>();
	}

	public TreeMap<Integer, CategoryAnswer> getCategories() {
		return categories;
	}

	public void setCategories(TreeMap<Integer, CategoryAnswer> categories) {
		this.categories = categories;
	}

	public void addCategory(int categoryId, CategoryAnswer category){
		this.categories.put(new Integer(categoryId), category);
	}

	public String getResultId() {
		return resultId;
	}

	public void setResultId(String resultId) {
		this.resultId = resultId;
	}

	public String getSurveyId() {
		return surveyId;
	}

	public void setSurveyId(String surveyId) {
		this.surveyId = surveyId;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getTimeSent() {
		return timeSent;
	}

	public void setTimeSent(String timeSent) {
		this.timeSent = timeSent;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getUser() {
		return user;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	public Document getXmldoc() {
		return xmldoc;
	}

	public void setXmldoc(Document xmldoc) {
		this.xmldoc = xmldoc;
	}
}
