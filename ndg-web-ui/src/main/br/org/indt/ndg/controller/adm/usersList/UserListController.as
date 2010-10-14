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
	import main.br.org.indt.ndg.controller.access.SessionTimer;
	import main.br.org.indt.ndg.i18n.ConfigI18n;
	import main.br.org.indt.ndg.model.UserDTO;
	import main.br.org.indt.ndg.ui.component.powerdatagrid.ChangePageEvent;
	import main.br.org.indt.ndg.ui.view.adm.sendAlert.SendAlert;
	
	import mx.collections.ArrayCollection;
	import mx.containers.ViewStack;
	import mx.controls.Alert;
	import mx.controls.dataGridClasses.DataGridColumn;
	import mx.managers.PopUpManager;
	import mx.rpc.remoting.mxml.RemoteObject;
				
    [Bindable] public var userList:ArrayCollection = new ArrayCollection();
	[Bindable] public var remoteUserList:RemoteObject = new RemoteObject(REMOTE_SERVICE);
	[Bindable] private var searchOptionsLabels:ArrayCollection = null;
    [Bindable] private var searchOptionsFields:ArrayCollection = null;
    public var myStack:ViewStack = null;

	private static const REMOTE_SERVICE:String = "myService";
	private static const USER_PAGE_SIZE:int = 15;
	
	
	private function init():void{
		searchOptionsLabels = new ArrayCollection();
		searchOptionsLabels.addItem(ConfigI18n.getInstance().getString("comboSearchAll"));
		searchOptionsLabels.addItem(ConfigI18n.getInstance().getString("colUserName"));
		searchOptionsLabels.addItem(ConfigI18n.getInstance().getString("colUserEmail"));
		searchOptionsFields = new ArrayCollection();
		searchOptionsFields.addItem(new Array("username", "email"));
		searchOptionsFields.addItem(new Array("username"));
		searchOptionsFields.addItem(new Array("email"));

		pagination.refresh();
		
		customCheck.addEventListener(MouseEvent.CLICK, selectedAll);		
	}
	
	private function selectedAll(event:MouseEvent):void{
		pagination.selectAll(customCheck.selected);
	}
	
	private function listUsers(event:ChangePageEvent):void{
		userPreview.dto = null;
		userList.source = new Array();
		remoteUserList.showBusyCursor = true;
		remoteUserList.listUsers(SessionClass.getInstance().
				loggedUser.username, event.page, event.pageSize, 
				event.filterText, event.filterFields,
				event.sortField, event.sortDescending);
		SessionTimer.getInstance().resetTimer();
	}

	private function getPersonalName(item:Object, column:DataGridColumn):String{
		return item.firstName + " " + item.lastName;
	}
	private function getPermission(item:Object, column:DataGridColumn):String{
		return item.role.name;
	}
	private function getPhoneNumber(item:Object, column:DataGridColumn):String{
		return "+" + item.countryCode + " " + item.areaCode + " " + item.phoneNumber;
	}
	
	private function showPreview(event:Event):void{
		var dto:UserDTO = userGrid.selectedItem as UserDTO;
		userPreview.dto = dto;
		userPreview.setSelectedComboRolesItem();
		if (userPreview.editMode){
			userPreview.refresh();
		}
	}

	private function showDeviceManager(event:Event):void{
		myStack.selectedIndex = 1;
	}
	
	private function showSendAlert(event:Event):void{
		var sizeSelectedItems:int = pagination.getSelectedAllItems().length; 
		if(sizeSelectedItems > 0) {
			var sendAlert:SendAlert;
			sendAlert = new SendAlert();
			sendAlert.setSelectedUsers(pagination.getSelectedAllItems());
			PopUpManager.addPopUp(sendAlert, this.parentDocument as DisplayObject,true);
			PopUpManager.centerPopUp(sendAlert);
		} else {
			Alert.show(ConfigI18n.getInstance().getString("selectOneMoreSurveys"),
					   ConfigI18n.getInstance().getString("lblError"));
		}
	}
	
	public function getSelectedUser():UserDTO{
		return pagination.getSelectedAllItems().getItemAt(0) as UserDTO;
	}
	
	