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
import java.util.ArrayList;

public class Choice implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ChoiceType choiceType;
	private ArrayList<Item> items;
	
	public Choice() {
		items = new ArrayList<Item>(5);
	}
	public ChoiceType getChoiceType() {
		return choiceType;
	}
	public void setChoiceType(ChoiceType choiceType) {
		this.choiceType = choiceType;
	}
	public ArrayList<Item> getItems() {
		return items;
	}
	public void setItems(ArrayList<Item> items) {
		this.items = items;
	}
	public void addItem(Item item) {
		this.items.add(item);
	}
	public Item getItem(int index) {
		return this.items.get(index);
	}
}
