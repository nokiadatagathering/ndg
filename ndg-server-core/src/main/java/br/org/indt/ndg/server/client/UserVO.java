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

import br.org.indt.ndg.server.controller.UserBalanceVO;

public class UserVO implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String AUTHENTICATED = "0000";
	public static final String INVALID_PASSWORD = "0001";
	public static final String INVALID_USERNAME = "0002";
	public static final String EMAIL_NOT_VALIDATED = "0003";
	
	public static final Integer USER_LIMIT = 0;
	public static final Integer IMEI_LIMIT = 1;
	public static final Integer ALERT_LIMIT = 2;
	public static final Integer RESULT_LIMIT = 3;
	public static final Integer SURVEY_LIMIT = 4;

	private String username;
	private String firstName;
	private String lastName;
	private String password;
	private String email;
	private RoleVO role;
	private String retCode;
	private CompanyVO company;
	private String userAdmin;
	private String countryCode;
	private String areaCode;
	private String phoneNumber;
	private String howDoYouPlanUseNdg;
	private String validationKey;
	private String editorSettings;
	private char whoUseIt;
	private char ndgNewsLetter;
	private char promotions;
	private char userValidated;
	private char emailPreferences;
	private char firstTimeUse;
	private char hasFullPermissions;
	private UserBalanceVO userBalance;

	/**
	 * @param userAdmin
	 *            the userAdmin to set
	 */
	public void setUserAdmin(String userAdmin) {
		this.userAdmin = userAdmin;
	}

	/**
	 * @return the userAdmin
	 */
	public String getUserAdmin() {
		return userAdmin;
	}

	public void setHowDoYouPlanUseNdg(String howDoYouPlanUseNdg) {
		this.howDoYouPlanUseNdg = howDoYouPlanUseNdg;
	}

	public String getHowDoYouPlanUseNdg() {
		return howDoYouPlanUseNdg;
	}

	public RoleVO getRole() {
		return role;
	}

	public void setRole(RoleVO role) {
		this.role = role;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getRetCode() {
		return retCode;
	}

	public void setRetCode(String retCode) {
		this.retCode = retCode;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @param lastName
	 *            the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	public void setEmailPreferences(char emailPreferences) {
		this.emailPreferences = emailPreferences;
	}

	public char getEmailPreferences() {
		return emailPreferences;
	}

	/**
	 * @param whoUseIt
	 *            the whoUseIt to set
	 */
	public void setWhoUseIt(char whoUseIt) {
		this.whoUseIt = whoUseIt;
	}

	/**
	 * @return the whoUseIt
	 */
	public char getWhoUseIt() {
		return whoUseIt;
	}

	/**
	 * @param ndgNewsLetter
	 *            the ndgNewsLetter to set
	 */
	public void setNdgNewsLetter(char ndgNewsLetter) {
		this.ndgNewsLetter = ndgNewsLetter;
	}

	/**
	 * @return the ndgNewsLetter
	 */
	public char getNdgNewsLetter() {
		return ndgNewsLetter;
	}

	/**
	 * @param promotions
	 *            the promotions to set
	 */
	public void setPromotions(char promotions) {
		this.promotions = promotions;
	}

	/**
	 * @return the promotions
	 */
	public char getPromotions() {
		return promotions;
	}

	/**
	 * @param userValid
	 *            the userValid to set y,Y = Yes; n,N=No
	 */
	public void setUserValidated(char userValidated) {
		if (userValidated == 'y' || userValidated == 'Y') {
			this.userValidated = 'Y';
		} else {
			this.userValidated = 'N';
		}
	}

	/**
	 * @return the userValid
	 */
	public char getUserValidated() {
		return userValidated;
	}

	/**
	 * @param company
	 *            the company to set
	 */
	public void setCompany(CompanyVO company) {
		this.company = company;
	}

	/**
	 * @return the company
	 */
	public CompanyVO getCompany() {
		return company;
	}

	public void setValidationKey(String validationKey) {
		this.validationKey = validationKey;
	}

	public String getValidationKey() {
		return validationKey;
	}
	
	public void setFirstTimeUse(char firstTimeUse) {
		this.firstTimeUse = firstTimeUse;
	}

	public char getFirstTimeUse() {
		return firstTimeUse;
	}

	public char getHasFullPermissions() {
		return hasFullPermissions;
	}

	public void setHasFullPermissions(char hasFullPermissions) {
		this.hasFullPermissions = hasFullPermissions;
	}

	public void setUserBalance(UserBalanceVO userBalance) {
		this.userBalance = userBalance;
	}

	public UserBalanceVO getUserBalance() {
		return userBalance;
	}
	
	public String getEditorSettings() {
		return editorSettings;
	}

	public void setEditorSettings(String editorSettings) {
		this.editorSettings = editorSettings;
	}
	
	@Override
	public String toString() {
		StringBuffer userAuthenticationVO = new StringBuffer();
		userAuthenticationVO.append("Name\t: " + firstName + "\n");
		userAuthenticationVO.append("Email\t: " + email + "\n");
		userAuthenticationVO.append("Username\t: " + username + "\n");
		userAuthenticationVO.append("Role\t: " + role.getName());
		return new String(userAuthenticationVO);
	}
}