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


public class Field implements Serializable
{
	private static final long serialVersionUID = 1L;

	private int id;
	private String name;
	private String value;
	private String xmlType;
	private int categoryId;
	private String direction;
	private FieldType type;
	private String elementName;
	private String description;
	private FieldType fieldType;
	private String printvalue;
	private String convention;
	private Choice choice;

	public String getXmlType()
	{
		return xmlType;
	}

	public void setXmlType(String xmlType)
	{
		this.xmlType = xmlType;
		
		if (xmlType.equals("_str"))
		{
			this.elementName = "str";
			this.type = FieldType.STR;
		}
		else if (xmlType.equals("_int"))
		{
			this.elementName = "int";
			this.type = FieldType.INT;
		}
		else if (xmlType.equals("_choice"))
		{
			this.elementName = "item";
			this.type = FieldType.CHOICE;
		}
		else if (xmlType.equals("_date"))
		{
			this.elementName = "date";
			this.type = FieldType.DATE;
		}
		else if (xmlType.equals("_time"))
		{
			this.elementName = "time";
			this.type = FieldType.TIME;
		}
		else if (xmlType.equals("_decimal"))
		{
			this.elementName = "decimal";
			this.type = FieldType.DECIMAL;
		}
		else if (xmlType.equals("_img"))
		{
			this.elementName = "img_data";
			this.type = FieldType.IMAGE;
		}
	}

	public void setFieldType(FieldType fieldType)
	{
		this.fieldType = fieldType;
		
		if (fieldType == FieldType.STR)
		{
			this.elementName = "str";
			this.xmlType = "_str";
		}
		else if (fieldType == FieldType.INT)
		{
			this.elementName = "int";
			this.xmlType = "_int";
		}
		else if (fieldType == FieldType.CHOICE)
		{
			this.elementName = "item";
			this.xmlType = "_choice";
		}
		else if (fieldType == FieldType.DATE)
		{
			this.elementName = "date";
			this.xmlType = "_date";
		}
		else if (fieldType == FieldType.TIME)
		{
			this.elementName = "time";
			this.xmlType = "_time";
		}
		else if (fieldType == FieldType.DECIMAL)
		{
			this.elementName = "decimal";
			this.xmlType = "_decimal";
		}
		else if (fieldType == FieldType.IMAGE)
		{
			this.elementName = "img_data";
			this.xmlType = "_img";
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

	public FieldType getFieldType() {
		return this.type;
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

	public Choice getChoice() {
		return choice;
	}

	public void setChoice(Choice choice) {
		this.choice = choice;
	}

	public String getPrintvalue() {
		return printvalue;
	}

	public void setPrintvalue(String printvalue) {
		this.printvalue = printvalue;
	}

	public void setConvention(String convention) {
		this.convention = convention;
	}

	public String getConvention() {
		return convention;
	}
}
