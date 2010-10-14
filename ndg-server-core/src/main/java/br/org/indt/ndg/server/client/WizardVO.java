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
import java.util.ArrayList;
import java.util.List;

public class WizardVO implements Serializable{
	
	
	private static final long serialVersionUID = 1L;
	private List<String> imeiList;
	private List<String> phoneList;
	private boolean filled;
	
	
	public WizardVO(){
		this.imeiList = new ArrayList<String>();
		this.phoneList = new ArrayList<String>();
		this.filled = false;
	}
	
	public void addImei(String imei){
		this.imeiList.add(imei);
		this.filled = true;
	}
	
	public void addPhone(String phone){
		this.phoneList.add(phone);
		this.filled = true;
	}
	
	public List<String> getImeiList(){
		return this.imeiList;
	}
	
	public List<String> getPhoneList(){
		return this.phoneList;
	}
	
	public boolean isFilled(){
		return this.filled;
	}
	
	
}
