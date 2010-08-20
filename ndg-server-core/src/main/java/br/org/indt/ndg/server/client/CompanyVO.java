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

package br.org.indt.ndg.server.client;

import java.io.Serializable;

/**
 * @author samourao
 * 
 */
public class CompanyVO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int idCompany;
	private String companyName;
	private String companyType;
	private String companyIndustry;
	private String companyCountry;
	private String companySize;
	/**
	 * @return the idCompany
	 */
	public int getIdCompany() {
		return idCompany;
	}
	/**
	 * @param idCompany the idCompany to set
	 */
	public void setIdCompany(int idCompany) {
		this.idCompany = idCompany;
	}
	/**
	 * @return the companyName
	 */
	public String getCompanyName() {
		return companyName;
	}
	/**
	 * @param companyName the companyName to set
	 */
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	/**
	 * @return the companyType
	 */
	public String getCompanyType() {
		return companyType;
	}
	/**
	 * @param companyType the companyType to set
	 */
	public void setCompanyType(String companyType) {
		this.companyType = companyType;
	}
	/**
	 * @return the companyIndustry
	 */
	public String getCompanyIndustry() {
		return companyIndustry;
	}
	/**
	 * @param companyIndustry the companyIndustry to set
	 */
	public void setCompanyIndustry(String companyIndustry) {
		this.companyIndustry = companyIndustry;
	}
	/**
	 * @return the companyCountry
	 */
	public String getCompanyCountry() {
		return companyCountry;
	}
	/**
	 * @param companyCountry the companyCountry to set
	 */
	public void setCompanyCountry(String companyCountry) {
		this.companyCountry = companyCountry;
	}
	/**
	 * @return the companySize
	 */
	public String getCompanySize() {
		return companySize;
	}
	/**
	 * @param companySize the companySize to set
	 */
	public void setCompanySize(String companySize) {
		this.companySize = companySize;
	}
	
	
}
