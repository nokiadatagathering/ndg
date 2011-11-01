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

package br.org.indt.ndg.common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Vector;


public  class CategoryAnswer implements Serializable{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String name;
	private HashMap<String, Vector<Field>> subCategories;

	public CategoryAnswer() {
		subCategories = new HashMap<String, Vector<Field>>();
	}

	public void addField(String subCatId, Field field) {
		Vector<Field> answers = subCategories.get(subCatId);
		if( answers == null ) {
			answers = new Vector<Field>();
			subCategories.put(subCatId, answers);
		}
		answers.add(field);
	}

	public Field getField(String subCateId, int answerId) {
		return subCategories.get(subCateId).get(answerId);
	}

	public HashMap<String, Vector<Field>> getSubCategories() {
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
