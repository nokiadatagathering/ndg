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

public class ImeiVO implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private String imei;
	private String msisdn;
	private String username;
	private DeviceVO device;
	private String status;
	private int qtdeResults;
	private char realImei;

	public String getImei() {
		return imei; 
	}
	public void setImei(String imei) {
		this.imei = imei;
	}
	public String getMsisdn() {
		return msisdn;
	}
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
	public String getUserName() {
		return username;
	}
	public void setUserName(String username) {
		this.username = username;
	}
	public DeviceVO getDevice() {
		return device;
	}
	public void setDevice(DeviceVO device) {
		this.device = device;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public void setQtdeResults(int qtdeResults) {
		this.qtdeResults = qtdeResults;
	}
	public int getQtdeResults() {
		return qtdeResults;
	}
	public char getRealImei() {
		return realImei;
	}
	public void setRealImei(char realImei) {
		this.realImei = realImei;
	}
	
	
	
}
