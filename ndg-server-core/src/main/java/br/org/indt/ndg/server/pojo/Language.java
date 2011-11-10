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

package br.org.indt.ndg.server.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * Result entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "languages")
@NamedQueries({
    @NamedQuery (name = "languages.getLanguageList", query = "SELECT l FROM Language l"),
    @NamedQuery (name = "languages.getPath", query = "SELECT l FROM Language l WHERE localeString LIKE :locale")
})

public class Language implements java.io.Serializable {

	// Fields

	private int language_id;
	private String name;
	private String localeString;
	private String translationFilePath;
	private String fontFilePath;

	@Id
	@Column(name = "language_id", unique = true, nullable = false, length = 11)
	public int getLanguageId() {
		return this.language_id;
	}

	public void setLanguageId(int language_id) {
		this.language_id = language_id;
	}

	@Column(name = "name", length = 45)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "localeString", length = 45)
	public String getLocaleString()
	{
		return this.localeString;
	}

	public void setLocaleString(String localeString)
	{
		this.localeString = localeString;
	}

	@Column(name = "translationFilePath", length = 256)
	public String getPath()
	{
		return this.translationFilePath;
	}

	public void setPath(String translationFilePath)
	{
		this.translationFilePath = translationFilePath;
	}

	@Column(name = "fontFilePath", length = 256)
	public String getFontPath()
	{
		return this.fontFilePath;
	}

	public void setFontPath(String fontFilePath)
	{
		this.fontFilePath = fontFilePath;
	}

}
