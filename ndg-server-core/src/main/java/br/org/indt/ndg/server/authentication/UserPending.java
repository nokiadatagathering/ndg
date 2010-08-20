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

package br.org.indt.ndg.server.authentication;

import javax.persistence.Id;

import br.org.indt.ndg.server.authorization.Role;

//@Entity
//@Table(name = "USERSPENDING")
public class UserPending implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private String username;

	private String password;

	private String name;

	private String email;

	private Role role;

	@Id
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the role
	 */
	public Role getRole() {
		return role;
	}

	/**
	 * @param role the role to set
	 */
	public void setRole(Role role) {
		this.role = role;
	}

	@Override
	public String toString() {
		StringBuffer userPendingToString = new StringBuffer();
		userPendingToString.append("Name\t: " + name + "\n");
		userPendingToString.append("Email\t: " + email + "\n");
		userPendingToString.append("Username\t: " + username);
		return new String(userPendingToString);
	}

}
