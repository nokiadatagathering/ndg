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

package main.br.org.indt.ndg.model {
	
	[RemoteClass(alias='br.org.indt.ndg.server.client.UserVO')]
	[Bindable]
	public class UserDTO {
		
		public var selected:Boolean = false;
		
		public var username:String = null;
		public var password:String = null;
		public var firstName:String = null;
		public var lastName:String = null;
		public var email:String = null;
		public var userAdmin:String = null;

		public var countryCode:String = null;
		public var areaCode:String = null;
		public var phoneNumber:String = null;

		public var retCode:String = null;
		public var userValidated:String = null;
		public var validationKey:String = null;
		public var hasFullPermissions:String = null;
		public var firstTimeUse:String = null;

		public var howDoYouPlanUseNdg:String = null;
		public var whoUseIt:String = null;
		public var ndgNewsLetter:String = null;
		public var emailPreferences:String = null;
		
		public var role:RoleDTO = null;
		public var company:CompanyDTO = null;
		//public var userBalance:UserBalanceDTO = null;
		
		
		public function UserDTO() {
		}
		
		public function getId():String{
			return username;
		}
		

	}
	
	
}