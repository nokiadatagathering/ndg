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

// ActionScript file

	import main.br.org.indt.ndg.controller.access.SessionClass;
	import main.br.org.indt.ndg.i18n.ConfigI18n;

	import mx.controls.Alert;
	import mx.managers.PopUpManager;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	import mx.rpc.remoting.mxml.RemoteObject;

	private static const REMOTE_SERVICE:String = "myService";
	
	public function init():void {
		getNdgVersion();
	}
	
	private function getNdgVersion():void
	{
		lblNdgVersion.text = SessionClass.getInstance().ndgVersion;
	}
	
	public function close():void {
		PopUpManager.removePopUp(this);
	}
