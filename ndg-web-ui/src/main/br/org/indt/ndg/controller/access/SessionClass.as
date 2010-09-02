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

package main.br.org.indt.ndg.controller.access {
	
	import flash.net.URLRequest;
	import flash.net.navigateToURL;
	
	import main.br.org.indt.ndg.model.UserDTO;  
	
	
	[Bindable]
	public class SessionClass {
		
		private static const ADMIN_ROLE:String = "ADMIN";
		private static const OPERATOR_ROLE:String = "OPERATOR";
		private static var instance:SessionClass;
		
		public var loggedUser:UserDTO = null;
		public var isHostedMode:Boolean = false;
		public var ndgVersion:String = "0.0.0";
		public var hasSmsSupport:Boolean = false;
		
		public function SessionClass(enforcer:SingletonEnforcer) {
			if (enforcer == null){
				throw new Error("");
			} else{
				loggedUser = null;
			}
		}
		
		public static function getInstance():SessionClass{
			instance = (instance == null) ? new SessionClass(new SingletonEnforcer()) : instance;
			return instance;
		}
		
		public function isAdmin():Boolean{
			var boo:Boolean = false;
			if (loggedUser.role.name.toUpperCase() == ADMIN_ROLE){
				boo = true;
			}
			return boo;
		}
		
		public function isOperator():Boolean{
			var boo:Boolean = false;
			if (loggedUser.role.name.toUpperCase() == OPERATOR_ROLE){
				boo = true;
			}
			return boo;
		}
		
		public function logout():void{
			isHostedMode = false;
			loggedUser = null;
			var ref:URLRequest = new URLRequest("javascript:location.reload(true)");
			navigateToURL(ref, "_self");
		}
		
	}
	
	
}
class SingletonEnforcer{}