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

package main.br.org.indt.ndg.controller.editor
{
	import flash.events.Event;
	
	import main.br.org.indt.ndg.controller.editor.Payload;
	
	public class ControllerEvent extends Event
	{
		
		private var view:Object;
		private var event:Event;		
		private var payload:Payload;
		
		public function ControllerEvent(type:String, mobisusInfo:Payload)
		{
			super(type);
			this.payload = mobisusInfo;		
		}
		
		public function getPayload():Payload
		{
			return payload;
		}
	}
}