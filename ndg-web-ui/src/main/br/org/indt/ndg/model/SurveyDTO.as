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
	
	
	[RemoteClass(alias='br.org.indt.ndg.server.client.SurveyVO')]
	[Bindable]	
	public class SurveyDTO{
		
		public var selected:Boolean = false;
		public var idSurvey:String = null;
		public var results:int = 0;
		public var title:String = null;
		public var status:String = null;
		public var device:String = null;
		public var pending:String = null;
		public var userAdmin:UserDTO = null;
		public var date:String = null;
		public var check:String = null;
		public var user:String = null;
		public var survey:String = null;
		public var resultsSent:int = 0;
		
				
		public function SurveyDTO(){
		}
		
		
		public function getId():String{
			return idSurvey;
		}

	}
	
	
}
