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

package br.org.indt.ndg.client.dto.surveys;

import java.util.Vector;

public class SCategory {
	
	private int id;
	private String name;
	private Vector fields;
	
	
	public SCategory() {
		fields = new Vector(5);
	}
	
	public void addField(SField field) {
		fields.add(field);
	}
	
	public SField getFieldByIndex(int index) {
		return (SField) fields.get(index);
	}
	
	public Vector getFields() {
		return fields;
	}
	
	public void setFields(Vector fields) {
		this.fields = fields;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public SField getFieldById(int fieldId) {
		SField tmpField = null;
		for (int i=0; i < fields.size(); i++) {
			SField field = (SField) fields.get(i);
			if (field.getId() == fieldId) {
				tmpField = field;
				break;
			}
		}
		return tmpField;
	}

}
