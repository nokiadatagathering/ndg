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
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.w3c.dom.Document;


public class SurveyXML implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String display; 
	private String title;
	private String deployed;
	private String checksum;
	private int displayCategory;
	private int displayQuestion;
	private Document xmldoc;
	private TreeMap<Integer, Category> categories;
	private ArrayList<ResultXml> results;

	public SurveyXML() {
		this.id = "";
		this.display = "";
		this.categories = new TreeMap<Integer, Category>();
		this.displayCategory = 0;
		this.displayQuestion = 0;
		this.results = new ArrayList<ResultXml>();
	}

	public TreeMap<Integer, Category> getCategories() {
		return categories;
	}
	public void setCategories(TreeMap<Integer, Category> categories) {
		this.categories = categories;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
		StringTokenizer st = new StringTokenizer(display, "-");
		if (st.countTokens() == 2) {
			int count = 1;
			while(st.hasMoreTokens()) {
				this.displayCategory = (count == 1 ? Integer.parseInt(st.nextToken()) : this.displayCategory);
				this.displayQuestion = (count == 2 ? Integer.parseInt(st.nextToken()) : this.displayQuestion);
				count++;
			}
		}

	}

	public int getDisplayCategory() {
		return displayCategory;
	}

	public int getDisplayQuestion() {
		return displayQuestion;
	}

	public Document getXmldoc() {
		return xmldoc;
	}

	public void setXmldoc(Document xmldoc) {
		this.xmldoc = xmldoc;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public ArrayList<ResultXml> getResults() {
		return results;
	}

	public void setResults(ArrayList<ResultXml> results) {
		this.results = results;
	}

	public void addResult(ResultXml result) {
		this.results.add(result);
	}

	public int getResultsSize() {
		return results.size();
	}

	public String getItemValue(int categoryId, int FieldId, int itemIndex) {
		Category category = categories.get(new Integer(categoryId));
		Field field = category.getFieldById(FieldId);
		Choice choice = field.getChoice();
		String value = choice.getItem(itemIndex).getValue();
		return value;
	}

	public String getDeployed() 
	{
		return deployed;
	}

	public void setDeployed(String deployed) 
	{
		this.deployed = deployed;
	}

	public String getChecksum() 
	{
		return checksum;
	}

	public void setChecksum(String checksum) 
	{
		this.checksum = checksum;
	}
}