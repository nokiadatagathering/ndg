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

package main.br.org.indt.ndg.ui.component.powerdatagrid {
	
	import flash.events.Event;
	
	[Bindable]
	public class SearchEvent extends Event{
		
		public static const EVENT_NAME:String = "search";
		public var filterText:String = null;
		public var filterFields:Array = null;
		
		public function SearchEvent(type:String) {
			super(type);
		}

	}
	
	
}