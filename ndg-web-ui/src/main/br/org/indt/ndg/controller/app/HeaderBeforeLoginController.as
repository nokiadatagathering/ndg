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

// ActionScript file

	import main.br.org.indt.ndg.i18n.ConfigI18n;
	import main.br.org.indt.ndg.controller.app.LocaleEvent;
	
			
	private function changeLocale(event:MouseEvent):void{
		var lEvent:LocaleEvent = new LocaleEvent(LocaleEvent.EVENT_NAME);
		var locale:String = event.currentTarget.id;
		
		lEvent.currentLocale = locale as String;
		dispatchEvent(lEvent);
	}