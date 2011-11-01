/*
*  Nokia Data Gathering
*
*  Copyright (C) 2011 Nokia Corporation
*
*  This program is free software; you can redistribute it and/or
*  modify it under the terms of the GNU Lesser General Public
*  License as published by the Free Software Foundation; either
*  version 2.1 of the License, or (at your option) any later version.
*
*  This program is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
*  Lesser General Public License for more details.
*
*  You should have received a copy of the GNU Lesser General Public License
*  along with this program.  If not, see <http://www.gnu.org/licenses/
*/

package main.br.org.indt.ndg.model {
	
	[RemoteClass(alias='br.org.indt.ndg.server.client.ImeiVO')]
	[Bindable]	
	public class ImeiDTO {
		
		public var selected:Boolean = false;
		public var imei:String = null;
		public var msisdn:String = null;
		public var userName:String = null;
		public var status:String = null;
		public var qtdeResults:int = 0;
		public var device:DeviceDTO = null;
		public var realImei:String = null;
				
		public function ImeiDTO() {
		}

		public function getId():String{
			return imei;
		}
		
		public function clone():ImeiDTO{
			var clone:ImeiDTO = new ImeiDTO();
			clone.selected = this.selected;
			clone.imei = this.imei;
			clone.msisdn = this.msisdn;
			clone.userName = this.userName;
			clone.status = this.status;
			clone.qtdeResults = this.qtdeResults;
			clone.device = this.device.clone();
			clone.realImei = this.realImei;
			return clone;
		}
	}
}	
	
	