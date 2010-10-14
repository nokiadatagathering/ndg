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
	
	import mx.collections.ArrayCollection;
	
	[RemoteClass(alias='br.org.indt.ndg.server.controller.QueryInputOutputVO')]
	[Bindable]	
	public class QueryInputOutputDTO {
		
		public var pageNumber:int;
		public var pageCount:int;
		public var recordCount:int;
		public var queryResult:ArrayCollection;
			
		public function QueryInputOutputDTO() {
		}

	}
	
	
}