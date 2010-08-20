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

package br.org.indt.ndg.server.util;

import java.util.ArrayList;
import java.util.Iterator;

public class SqlUtil 
{
	public static String getFilterCondition(String filterText, ArrayList<String> filterFields)
	{
		String filterCondition = " AND (";
		
		for (int i = 0; i < filterFields.size(); i++) 
		{
			if ((i > 0) && (i < filterFields.size()))
			{
				filterCondition += " OR ";
			}
			
			filterCondition += filterFields.get(i) + " LIKE '%" + filterText + "%'";			
		}
		
		filterCondition += ")";
		
		return filterCondition;
	}
	
	public static String getSortCondition(String sortField, Boolean isDescending)
	{
		String sortCondition = " ORDER BY " + sortField;
		
		if (isDescending)
		{
			sortCondition += " DESC";
		}
		else
		{
			sortCondition += " ASC";
		}

		return sortCondition;
	}
	
	
	public static String getSortCondition(ArrayList<String> sortFields, ArrayList<Boolean> isDescending){
		String sortCondition = " ORDER BY ";
		
		Iterator<String> itFields = sortFields.iterator();
		Iterator<Boolean> itIsDescendig = isDescending.iterator();
		
		while (itFields.hasNext()){
			sortCondition += itFields.next();
			sortCondition += itIsDescendig.next() ? " DESC" : " ASC";
			if (itFields.hasNext()){
				sortCondition += ", ";	
			}
		}

		return sortCondition;
	}
}
