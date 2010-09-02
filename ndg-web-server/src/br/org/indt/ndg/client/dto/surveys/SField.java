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


public class SField {
	
	
	private int id;
	private String name;
	private String value;
	private String xmlType;
	private int categoryId;
	private String direction;
	private String elementName;
	private String description;
	private String convention;
	
	private SChoice choice; 
	
	public String getXmlType() {
		return xmlType;
	}

	public void setXmlType(String xmlType) {
		this.xmlType = xmlType;
	    if (xmlType.equals("_str")) {
	    	this.elementName = "str";
	    } else if (xmlType.equals("_int")) {
	    	this.elementName = "int";
	    } else if (xmlType.equals("_choice")) {
	    	this.elementName = "item";
	    } else if (xmlType.equals("_date")) {
	    	this.elementName = "date";
	    } else if (xmlType.equals("_time")) {
	    	this.elementName = "time";	    	
	    } else if (xmlType.equals("_decimal")) {
	    	this.elementName = "decimal";
	    } else if (xmlType.equals("_img")) {
	    	this.elementName = "img_data";
	    }
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}
	
	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getElementName() {
		return elementName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public SChoice getChoice() {
		return choice;
	}

	public void setChoice(SChoice choice) {
		this.choice = choice;
	}

	public void setElementName(String elementName) {
		this.elementName = elementName;
	}

	public void setConvention(String convention) {
		this.convention = convention;
	}

	public String getConvention() {
		return convention;
	}

}
