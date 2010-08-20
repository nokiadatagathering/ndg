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

package br.org.indt.ndg.common;

import java.io.Serializable;
import java.util.Vector;


public  class Category implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String name;
	private Vector<Field> fields;
	
	
	public Category() {
		fields = new Vector<Field>();
		
	}
	
	public void addField(Field field) {
		fields.add(field);
	}
	
	public Field getFieldByIndex(int index) {
		return fields.get(index);
	}
	
	public Vector<Field> getFields() {
		return fields;
	}
	
	public void setFields(Vector<Field> fields) {
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
	
	public Field getFieldById(int fieldId) {
		Field tmpField = null;
		for (Field field : fields) {
			if (field.getId() == fieldId) {
				tmpField = field;
				break;
			}
		}
		return tmpField;
	}

}
