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

package br.org.indt.ndg.server.instance;

import br.org.indt.ndg.server.data.UserData;

public class UserInstance {
	
	UserData bean = new UserData();
		
	public UserData getUserBean(){
		
		String firstName = "antonio";
		String lastName = "aguiar";
		String email = "user@user.com";
		String permission = "admin";
		String password = "123";
		String countryCode = "55";
		String areaCode = "92";
		String phoneNumber = "21239735";
		String user = "aaguiar";
			
		bean.setUserName(user);
		bean.setFirstName(firstName);
		bean.setLastName(lastName);
		bean.setEmail(email);
		bean.setPermission(permission);
		bean.setCountryCode(countryCode);
		bean.setAreaCode(areaCode);
		bean.setPhoneNumber(phoneNumber);
		bean.setPassword(password);
				
		return bean;
	}
	
	public UserData getUpdateUserBean(){
		
		String firstName = "antonietolisty";
		String lastName = "aguiar";
		String email = "user@user.com";
		String permission = "admin";
		String password = "123";
		String countryCode = "55";
		String areaCode = "92";
		String phoneNumber = "21239735";
		String user = "aaguiar";
			
		bean.setUserName(user);
		bean.setFirstName(firstName);
		bean.setLastName(lastName);
		bean.setEmail(email);
		bean.setPermission(permission);
		bean.setCountryCode(countryCode);
		bean.setAreaCode(areaCode);
		bean.setPhoneNumber(phoneNumber);
		bean.setPassword(password);
				
		return bean;
	}
	
	public UserData getLoggedUser() {
		
		bean.setUserName("admin");
		bean.setPermission("Admin");
		bean.setUserAdmin("admin");
		
		return bean;
	}
	
	public UserData getInvalidUser() {
		
		bean.setUserName(null);
		
		return bean;
	}
	
public UserData getExistentUserBean(){
		
		String firstName = "daniella";
		String lastName = "bezerra";
		String email = "daniella@dani.com";
		String permission = "admin";
		String password = "123";
		String countryCode = "55";
		String areaCode = "92";
		String phoneNumber = "21239735";
		String user = "dbezerra";
			
		bean.setUserName(user);
		bean.setFirstName(firstName);
		bean.setLastName(lastName);
		bean.setEmail(email);
		bean.setPermission(permission);
		bean.setCountryCode(countryCode);
		bean.setAreaCode(areaCode);
		bean.setPhoneNumber(phoneNumber);
		bean.setPassword(password);
				
		return bean;
	}

}
