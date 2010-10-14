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
	
	
	[RemoteClass(alias='br.org.indt.ndg.server.client.ResultVO')]
	[Bindable]	
	public class ResultDTO {
		
		public var selected:Boolean = false;
		public var surveyId:String = null;
		public var idResult:String = null;
		public var imei:String = null;
		public var user:String = null;
		public var title:String = null;
		public var lat:Number = 0;
		public var lon:Number = 0;
		public var date:String = null;
		public var hasCoordinates:Boolean = false;
		
			
		public function ResultDTO() {
		}
		
		public function getId():String{
			return idResult;
		}

	}
	
	
}