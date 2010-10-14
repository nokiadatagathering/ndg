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

package br.org.indt.ndg.server.data;



public class ImeiData {
	private String remove;
	private String imei;
	private String msisdn;
	private String name;
	private String device;
	private String check;
	private String result;
	private String download;
	private DeviceData deviceData;
	
	
	public ImeiData() {
		
	}

	public ImeiData(String imei, String msisdn, String name, String device) {
		this.setImei(imei);
		this.setMsisdn(msisdn);
		this.setName(name);
		this.setDevice(device);
	}
	
	public ImeiData(String remove, String imei, String msisdn, String name, String device) {
		this.setRemove(remove);
		this.setImei(imei);
		this.setMsisdn(msisdn);
		this.setName(name);
		this.setDevice(device);
	}
	
	public ImeiData(String imei, String msisdn, String name, DeviceData deviceData) {
		this.setImei(imei);
		this.setMsisdn(msisdn);
		this.setName(name);
		this.setDeviceData(deviceData);
	}

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
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public String getCheck() {
		return check;
	}

	public void setCheck(String check) {
		this.check = check;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getDownload() {
		return download;
	}

	public void setDownload(String download) {
		this.download = download;
	}

	public String getRemove() {
		return remove;
	}

	public void setRemove(String remove) {
		this.remove = remove;
	}

	public DeviceData getDeviceData() {
		return deviceData;
	}

	public void setDeviceData(DeviceData deviceData) {
		this.deviceData = deviceData;
	}
}
