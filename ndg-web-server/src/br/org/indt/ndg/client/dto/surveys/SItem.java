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

package br.org.indt.ndg.client.dto.surveys;


public class SItem {
	
	
	String otr;
	String def;
	String value;
	int index;
	
	public SItem(){
		
	}
	
	public SItem(String otr, String def, String value) {
		this.otr = otr;
		this.def = def;
		this.value = value;
	}
	
	public String getOtr() {
		return otr;
	}
	public void setOtr(String otr) {
		this.otr = otr;
	}

	public void setDef(String def){
		this.def = def;
	}
	public String getDef(){
		return def;
	}

	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}
