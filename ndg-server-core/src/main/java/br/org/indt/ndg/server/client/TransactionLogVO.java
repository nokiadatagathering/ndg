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
import java.sql.Timestamp;

public class TransactionLogVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String TYPE_SEND_SURVEY = "SS"; //Send Survey to Client
	public static final String TYPE_RECEIVE_SURVEY = "RS"; //Received Survey from Editor
	public static final String TYPE_RECEIVE_RESULT = "RR"; //Received Result from Client
	public static final String TYPE_SEND_RESULT = "SR"; //Send Result to Client
	public static final String SEND_ALERT = "SA"; //Send Alert by SMS to Client
	public static final String DOWNLOAD_CLIENT = "DC"; //Download Client
	public static final String NEW_USER_ADMIN = "NU"; //register new user by Request Access
	
	public static final String MODE_CABLE = "CABLE";
	public static final String MODE_SMS = "SMS";
	public static final String MODE_HTTP = "HTTP";
	public static final String MODE_GPRS = "GPRS";
	
	public static final String STATUS_SUCCESS = "SUCCESS";
	public static final String STATUS_AVAILABLE = "AVAILABLE";
	public static final String STATUS_PENDING = "PENDING";
	public static final String STATUS_ERROR = "ERROR";
	
	private String transactionType;
	
	private Timestamp dtLog;
	
	private String surveyId;
	
	private String imei;
	
	private String resultId;

	private String status;
	
	private String user;
	
	private String address;
	
	private String transmissionMode;
	
	private String msisdn;

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public Timestamp getDtLog() {
		return dtLog;
	}

	public void setDtLog(Timestamp dtLog) {
		this.dtLog = dtLog;
	}

	public String getSurveyId() {
		return surveyId;
	}

	public void setSurveyId(String surveyId) {
		this.surveyId = surveyId;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getResultId() {
		return resultId;
	}

	public void setResultId(String resultId) {
		this.resultId = resultId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTransmissionMode() {
		return transmissionMode;
	}

	public void setTransmissionMode(String transmissionMode) {
		this.transmissionMode = transmissionMode;
	}
	
}
