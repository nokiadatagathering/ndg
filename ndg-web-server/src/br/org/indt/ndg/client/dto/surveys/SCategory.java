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

import java.util.HashMap;
import java.util.Vector;


public class SCategory {
	
	private int id;
	private String name;
	private HashMap<String, Vector<SField>> subCategories;
	
	public SCategory() {
		subCategories = new HashMap<String, Vector<SField>>();
	}
	
	public void addField(String subCatId, SField field) {
		Vector<SField> answers = subCategories.get(subCatId);
		if( answers == null ) {
			answers = new Vector<SField>();
			subCategories.put(subCatId, answers);
		}
		answers.add(field);
	}
	
	public SField getField(String subCateId, int answerId) {
		return subCategories.get(subCateId).get(answerId-1);
	}
	
	public HashMap<String, Vector<SField>> getSubCategories() {
		return subCategories;
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
	
}
