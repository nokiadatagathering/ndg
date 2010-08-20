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

package br.org.indt.ndg.server.data;

import br.org.indt.ndg.server.data.CompanyData;
import br.org.indt.ndg.server.data.RoleData;
import br.org.indt.ndg.server.data.UserBalanceData;

/** 
 * Simple class that represents user data 
 */

public class UserData {
	private String userName;
	private String firstName;
	private String lastName;
	private String password;
	private String email;
	private String permission;
	private String retCode;
	private String companyName;
	private String userAdmin;
	private String countryCode;
	private String areaCode;
	private String phoneNumber;
	private String howDoYouPlanUseNdg;
	private String validationKey;
	private char whoUseIt;
	private char ndgNewsLetter;
	private char promotions;
	private char userValidated;
	private char emailPreferences;
	private char firstTimeUse;
	private String check;
	private RoleData role;
	private CompanyData company;
	private UserBalanceData userBalance;
	
	public UserData() {
		
	}
	
	public UserData(String firstName) {
		this.setFirstName(firstName);
	}
	
	public UserData(String userName, String fisrtName, String email, String permission) {
		this.setUserName(userName);
		this.setEmail(email);
		this.setPermission(permission);
		this.setFirstName(firstName);
	}
	
	public UserData(String userName, String fisrtName, String email, String permission, String password) {
		this.setUserName(userName);
		this.setEmail(email);
		this.setPermission(permission);
		this.setFirstName(firstName);
		this.setPassword(password);
	}
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public RoleData getRole() {
		return role;
	}

	public void setRole(RoleData role) {
		this.role = role;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getRetCode() {
		return retCode;
	}

	public void setRetCode(String retCode) {
		this.retCode = retCode;
	}
	
	public CompanyData getCompany() {
		return company;
	}

	public void setCompany(CompanyData company) {
		this.company = company;
	}

	public String getUserAdmin() {
		return userAdmin;
	}

	public void setUserAdmin(String userAdmin) {
		this.userAdmin = userAdmin;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getHowDoYouPlanUseNdg() {
		return howDoYouPlanUseNdg;
	}

	public void setHowDoYouPlanUseNdg(String howDoYouPlanUseNdg) {
		this.howDoYouPlanUseNdg = howDoYouPlanUseNdg;
	}

	public String getValidationKey() {
		return validationKey;
	}

	public void setValidationKey(String validationKey) {
		this.validationKey = validationKey;
	}

	public char getWhoUseIt() {
		return whoUseIt;
	}

	public void setWhoUseIt(char whoUseIt) {
		this.whoUseIt = whoUseIt;
	}

	public char getNdgNewsLetter() {
		return ndgNewsLetter;
	}

	public void setNdgNewsLetter(char ndgNewsLetter) {
		this.ndgNewsLetter = ndgNewsLetter;
	}

	public char getPromotions() {
		return promotions;
	}

	public void setPromotions(char promotions) {
		this.promotions = promotions;
	}

	public char getUserValidated() {
		return userValidated;
	}

	public void setUserValidated(char userValidated) {
		this.userValidated = userValidated;
	}

	public char getEmailPreferences() {
		return emailPreferences;
	}

	public void setEmailPreferences(char emailPreferences) {
		this.emailPreferences = emailPreferences;
	}

	public String getCheck() {
		return check;
	}

	public void setCheck(String check) {
		this.check = check;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public char getFirstTimeUse() {
		return firstTimeUse;
	}

	public void setFirstTimeUse(char firstTimeUse) {
		this.firstTimeUse = firstTimeUse;
	}

	public UserBalanceData getUserBalance() {
		return userBalance;
	}

	public void setUserBalance(UserBalanceData userBalance) {
		this.userBalance = userBalance;
	}

}
