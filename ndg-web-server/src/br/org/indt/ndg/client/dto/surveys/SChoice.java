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

import java.util.ArrayList;


public class SChoice {
	
	public static int EXCLUSIVE = 1;
	public static int MULTIPLE = 2;
	
	private int choiceType;
	private ArrayList items;
	
	public SChoice() {
		items = new ArrayList(5);
	}
	public int getChoiceType() {
		return choiceType;
	}
	public void setChoiceType(int choiceType) {
		this.choiceType = choiceType;
	}
	public ArrayList getItems() {
		return items;
	}
	public void setItems(ArrayList items) {
		this.items = items;
	}
	public void addItem(SItem item) {
		this.items.add(item);
	}
	public SItem getItem(int index) {
		return (SItem) this.items.get(index);
	}
}
