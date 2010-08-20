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

package br.org.indt.ndg.server.imei;

import javax.ejb.Remote;

import br.org.indt.ndg.common.exception.MSMApplicationException;
import br.org.indt.ndg.server.client.DeviceVO;
import br.org.indt.ndg.server.controller.QueryInputOutputVO;
import br.org.indt.ndg.server.pojo.NdgDevice;

@Remote
public interface DeviceManager {

	public void createDevice(DeviceVO deviceVO) throws MSMApplicationException;
	
	public void deleteDevice(String deviceName) throws MSMApplicationException;
		
	public QueryInputOutputVO listAllDevices(QueryInputOutputVO queryIOVO) throws MSMApplicationException;

	public NdgDevice findNdgDeviceByModel(String deviceModel) throws MSMApplicationException;
	
	public void updateDevice(DeviceVO deviceVO) throws MSMApplicationException;
	
	public NdgDevice findNdgDeviceByIdDevice(DeviceVO deviceVO) throws MSMApplicationException;
	
	public String createDynamicJad(String msisdn) throws MSMApplicationException;
	
	public void deleteJadDir(String msisdn) throws MSMApplicationException;
	
	
}
