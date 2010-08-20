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

package main.br.org.indt.ndg.ui.component.powerdatagrid {
	
	import flash.events.Event;
	
	[Bindable]
	public class ChangePageEvent extends Event{
		
		public static const EVENT_NAME:String = "changePage";
		public var page:int;
		public var pageSize:int;
		public var sortField:String;
		public var sortDescending:Boolean;
		public var filterText:String;
		public var filterFields:Array;
		
		public function ChangePageEvent(type:String) {
			super(type);
		}

	}
	
	
}
