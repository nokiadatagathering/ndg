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

package br.org.indt.ndg.server.pojo;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * User entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "user")
@NamedQueries( {
		@NamedQuery(name = "user.findByName", query = "SELECT U FROM NdgUser U WHERE username like :userName"),
		@NamedQuery(name = "user.findByvalidationKey", query = "SELECT U FROM NdgUser U WHERE validationKey like :userValidationKey"),
		@NamedQuery(name = "user.findByEmail", query = "SELECT U FROM NdgUser U WHERE email like :email"),
		@NamedQuery(name = "user.getAllUsersByAdmin", query = "SELECT U FROM NdgUser U WHERE userAdmin like :useradmin") })
public class NdgUser implements java.io.Serializable {

	/**
*
*/
	private static final long serialVersionUID = 1L;
	// Fields
	private int idUser;
	private Company company;
	private NdgRole role;
	private String userAdmin;
	private String firstName;
	private String lastName;
	private String username;
	private String password;
	private String countryCode;
	private String areaCode;
	private String phoneNumber;
	private String email;
	private String howDoYouPlanUseNdg;
	private String validationKey;
	private char whoUseIt;
	private char emailPreferences;
	private char userValidated;
	private char firstTimeUse;
	private char hasFullPermissions;
	private String editorSettings;

	// Property accessors
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "idUser", unique = true, nullable = false)
	public int getIdUser() {
		return this.idUser;
	}

	public void setIdUser(int idUser) {
		this.idUser = idUser;
	}

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "idCompany", nullable = false)
	public Company getCompany() {
		return this.company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "idRole", nullable = false)
	public NdgRole getRole() {
		return this.role;
	}

	public void setRole(NdgRole role) {
		this.role = role;
	}

	@Column(name = "userAdmin", length = 50, nullable = false)
	public String getUserAdmin() {
		return this.userAdmin;
	}

	public void setUserAdmin(String userAdmin) {
		this.userAdmin = userAdmin;
	}

	@Column(name = "firstName", length = 50, nullable = false)
	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@Column(name = "lastName", length = 50, nullable = false)
	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Column(name = "username", length = 50, nullable = false)
	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Column(name = "countryCode", length = 4, nullable = false)
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getCountryCode() {
		return countryCode;
	}

	@Column(name = "phoneNumber", length = 4, nullable = false)
	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public String getAreaCode() {
		return areaCode;
	}

	@Column(name = "phoneNumber", length = 20, nullable = false)
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	@Column(name = "password", nullable = false)
	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the email
	 */
	@Column(name = "email", unique = true, nullable = false)
	public String getEmail() {
		return email;
	}

	/**
	 * @param email
	 *            the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "whoUseIt", nullable = false)
	public char getWhoUseIt() {
		return this.whoUseIt;
	}

	public void setWhoUseIt(char whoUseIt) {
		this.whoUseIt = whoUseIt;
	}

	@Column(name = "emailPreferences")
	public char getEmailPreferences() {
		return this.emailPreferences;
	}

	public void setEmailPreferences(char emailPreferences) {
		this.emailPreferences = emailPreferences;
	}

	@Column(name = "howDoYouPlanUseNdg", length = 65535)
	public void setHowDoYouPlanUseNdg(String howDoYouPlanUseNdg) {
		this.howDoYouPlanUseNdg = howDoYouPlanUseNdg;
	}

	public String getHowDoYouPlanUseNdg() {
		return howDoYouPlanUseNdg;
	}

	@Column(name = "userValidated", nullable = false)
	public void setUserValidated(char userValidated) {
		if (userValidated == 'y' || userValidated == 'Y') {
			this.userValidated = 'Y';
		} else {
			this.userValidated = 'N';
		}
	}

	public char getUserValidated() {
		return userValidated;
	}

	@Column(name = "validationKey")
	public void setValidationKey(String validationKey) {
		this.validationKey = validationKey;
	}

	public String getValidationKey() {
		return validationKey;
	}

	@Column(name = "firstTimeUse", nullable = false)
	public void setFirstTimeUse(char firstTimeUse) {
		this.firstTimeUse = firstTimeUse;
	}

	public char getFirstTimeUse() {
		return firstTimeUse;
	}

	@Column(name = "hasFullPermissions", nullable = false)
	public void setHasFullPermissions(char hasFullPermissions) {
		this.hasFullPermissions = hasFullPermissions;
	}

	public char getHasFullPermissions() {
		return hasFullPermissions;
	}

	@Column(name = "editorSettings", length = 65535)
	public String getEditorSettings() {
		return this.editorSettings;
	}

	public void setEditorSettings(String editorSettings) {
		this.editorSettings = editorSettings;
	}
}
