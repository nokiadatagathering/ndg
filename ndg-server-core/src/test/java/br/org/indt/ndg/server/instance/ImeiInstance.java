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

package br.org.indt.ndg.server.instance;

import br.org.indt.ndg.server.data.DeviceData;
import br.org.indt.ndg.server.data.ImeiData;

public class ImeiInstance {
	
	ImeiData bean = new ImeiData();
	
	public ImeiData getImeiBean(){
		
		String imei = "01020100201200";
		String msisdn = "21239735";
		String owner = "admin";
		
		DeviceInstance inst = new DeviceInstance();
		DeviceData deviceData = inst.getDeviceBean();
		
		bean.setImei(imei);
		bean.setMsisdn(msisdn);
		bean.setName(owner);
		bean.setDeviceData(deviceData);
				
		return bean;
	}
}
