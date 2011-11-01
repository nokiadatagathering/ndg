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

package br.org.indt.ndg.server.controller;

import java.util.ArrayList;

public class QueryInputOutputVO implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;
	
	// input - pagination
	private Integer recordsPerPage;
	
	// input - search (filter)
	private String filterText;
	private ArrayList<String> filterFields;
	
	// input - ordering
	private String sortField;
	private Boolean isDescending;
	
	// output - record count
	private Integer recordCount;
	private Integer pageCount;
	
	// output - query result	
	private ArrayList<Object> queryResult;

	// input/output - page number
	private Integer pageNumber;
	
	public Integer getPageNumber() 
	{
		return pageNumber;
	}

	public void setPageNumber(Integer pageNumber) 
	{
		this.pageNumber = pageNumber;
	}

	public Integer getRecordsPerPage() 
	{
		return recordsPerPage;
	}

	public void setRecordsPerPage(Integer recordsPerPage) 
	{
		this.recordsPerPage = recordsPerPage;
	}

	public String getFilterText() 
	{
		return filterText;
	}

	public void setFilterText(String filterText) 
	{
		this.filterText = filterText;
	}

	public ArrayList<String> getFilterFields() 
	{
		return filterFields;
	}

	public void setFilterFields(ArrayList<String> filterFields) 
	{
		this.filterFields = filterFields;
	}

	public String getSortField() 
	{
		return sortField;
	}

	public void setSortField(String sortField) 
	{
		this.sortField = sortField;
	}

	public Boolean getIsDescending() 
	{
		return isDescending;
	}

	public void setIsDescending(Boolean isDescending) 
	{
		this.isDescending = isDescending;
	}

	public Integer getRecordCount() 
	{
		return recordCount;
	}

	public void setRecordCount(Integer recordCount) 
	{
		this.recordCount = recordCount;
	}

	public ArrayList<Object> getQueryResult() 
	{
		return queryResult;
	}

	public void setQueryResult(ArrayList<Object> queryResult) 
	{
		this.queryResult = queryResult;
	}

	public void setPageCount(Integer pageCount){
		this.pageCount = pageCount;
	}
	
	public Integer getPageCount(){
		return this.pageCount;
	}
	
	public Integer getPageCountByRecordCount(){
		Integer pageCount = null;
		if (recordCount != null && recordsPerPage != null){
			double div = (double)recordCount.intValue()/(double)recordsPerPage.intValue();
			int pages = new Double(Math.ceil(div)).intValue();
			pageCount = new Integer(pages);
		}
		return pageCount;
	}
	
	
}