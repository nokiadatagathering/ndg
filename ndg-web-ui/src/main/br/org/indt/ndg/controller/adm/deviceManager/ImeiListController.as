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


	import main.br.org.indt.ndg.controller.access.SessionTimer;
	import main.br.org.indt.ndg.controller.util.ExceptionUtil;
	import main.br.org.indt.ndg.i18n.ConfigI18n;
	import main.br.org.indt.ndg.model.ImeiDTO;
	import main.br.org.indt.ndg.model.UserDTO;
	import main.br.org.indt.ndg.ui.component.powerdatagrid.ChangePageEvent;
	
	import mx.collections.ArrayCollection;
	import mx.containers.ViewStack;
	import mx.controls.Alert;
	import mx.controls.dataGridClasses.DataGridColumn;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	import mx.rpc.remoting.mxml.RemoteObject;
				
    [Bindable] public var imeiList:ArrayCollection = new ArrayCollection();
	[Bindable] public var remoteImeiList:RemoteObject = new RemoteObject(REMOTE_SERVICE);
	[Bindable] private var searchOptionsLabels:ArrayCollection = null;
    [Bindable] private var searchOptionsFields:ArrayCollection = null;
    public var myStack:ViewStack = null;

	[Bindable] private var selectedUserDTO:UserDTO = null;
	private static const REMOTE_SERVICE:String = "myService";
	private static const IMEI_PAGE_SIZE:int = 15;

	
	private function init():void{
		searchOptionsLabels = new ArrayCollection();
		searchOptionsLabels.addItem(ConfigI18n.getInstance().getString("comboSearchAll"));
		searchOptionsLabels.addItem(ConfigI18n.getInstance().getString("colDeviceManagerImei"));
		searchOptionsLabels.addItem(ConfigI18n.getInstance().getString("colDeviceManagerPhone"));
		searchOptionsFields = new ArrayCollection();
		searchOptionsFields.addItem(new Array("imei", "msisdn"));
		searchOptionsFields.addItem(new Array("imei"));
		searchOptionsFields.addItem(new Array("msisdn"));
		
		customCheck.addEventListener(MouseEvent.CLICK, selectedAll);
	}
	
	private function selectedAll(event:MouseEvent):void{
		pagination.selectAll(customCheck.selected);
	}
	
	public function listImeisFirstPage(user:UserDTO):void{
		imeiPreview.dto = null;
		selectedUserDTO = user;
		resetView(selectedUserDTO);		
		
		var deviceRemObj:RemoteObject = new RemoteObject(REMOTE_SERVICE);
		deviceRemObj.showBusyCursor = true;
		deviceRemObj.addEventListener(FaultEvent.FAULT, onFault);
		deviceRemObj.addEventListener(ResultEvent.RESULT, onSuccess);
		deviceRemObj.listDevices();
		SessionTimer.getInstance().resetTimer();
		
		function onFault(event:FaultEvent):void{
			Alert.show(ExceptionUtil.getMessage(event.fault.faultString),
					ConfigI18n.getInstance().getString("lblError"));
		}
		function onSuccess(event:ResultEvent):void{
			if (event.result != null) {
				var array:ArrayCollection = event.result as ArrayCollection;
				imeiPreview.comboDevicesDataProvider = array.toArray();
				pagination.refresh();
			}				
		}		
	}	
	
	private function listImeis(event:ChangePageEvent):void{
		imeiPreview.dto = null;
		imeiList.source = new Array();
		remoteImeiList.showBusyCursor = true;
		remoteImeiList.listImeisByUserWithoutPlus(selectedUserDTO.username, event.page, event.pageSize,
				event.filterText, event.filterFields, event.sortField, event.sortDescending,
				true, false);
		SessionTimer.getInstance().resetTimer();
	}
		
	private function resetView(user:UserDTO):void{
		userTitle.text = user.firstName + " " + user.lastName;
		search.clearUI();
		pagination.reset();
		imeiList.source = new Array();
	}	
	
	private function markAll(event:MouseEvent):void{
		pagination.selectAll(true);
	}
			
	private function unmarkAll(event:MouseEvent):void{
		pagination.selectAll(false);
	}
	
	private function getDeviceModel(item:Object, column:DataGridColumn):String{
		return item.device.deviceModel;
	}
	private function getMsisdn(item:Object, column:DataGridColumn):String{
		return "+" + item.msisdn;
	}
	
	private function showPreview(event:Event):void{
		var dto:ImeiDTO = imeiGrid.selectedItem as ImeiDTO;
		//var dtoClone:ImeiDTO = dto.clone();
		
		//var myPattern:RegExp = /\+/;  
		//dtoClone.msisdn = dtoClone.msisdn.replace(myPattern, "");
		
		//imeiPreview.dto = dtoClone;
		imeiPreview.dto = dto;
		imeiPreview.setSelectedComboDevicesItem();
		if (imeiPreview.editMode){
			imeiPreview.refresh();
		}
	}

	private function showUsers(event:Event):void{
		myStack.selectedIndex = 0;
	}
	
	private function getImei(item:Object, column:DataGridColumn):String{
		var imei:ImeiDTO = item as ImeiDTO;
		if (imei.realImei.toUpperCase() == "Y"){
			return imei.imei;
		} else{
			return ConfigI18n.getInstance().getString("gridWaitingImei");
		}
	}
	
