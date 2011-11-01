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


	import main.br.org.indt.ndg.controller.access.SessionTimer;
	import main.br.org.indt.ndg.model.UserDTO;
	import main.br.org.indt.ndg.ui.component.powerdatagrid.ChangePageEvent;
	import main.br.org.indt.ndg.i18n.ConfigI18n;
	
	import mx.collections.ArrayCollection;
	import mx.managers.PopUpManager;
	import mx.rpc.remoting.mxml.RemoteObject;
			
			
	private static const REMOTE_SERVICE:String = "myService";
	private static const IMEIS_PAGE_SIZE:int = 5;
	
	public var userDto:UserDTO = null;
	[Bindable] public var imeiList:ArrayCollection = new ArrayCollection();
	[Bindable] public var remoteList:RemoteObject = new RemoteObject(REMOTE_SERVICE);


			
	private function init():void{
		pagination.refresh();
	}
	
	private function listImeis(event:ChangePageEvent):void{
		remoteList.showBusyCursor = true;
		remoteList.listImeisByUser(userDto.username, event.page, event.pageSize, 
				event.filterText, event.filterFields, event.sortField, event.sortDescending,
				false, false);
		SessionTimer.getInstance().resetTimer();
	}
			
	private function close():void{
		PopUpManager.removePopUp(this);
	}
	
	private function getDeviceModel(item:Object, column:DataGridColumn):String{
		return item.device.deviceModel;
	}
	
	