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

	import flash.events.MouseEvent;
	
	import main.br.org.indt.ndg.controller.access.SessionClass;
	import main.br.org.indt.ndg.controller.access.SessionTimer;
	import main.br.org.indt.ndg.controller.util.ExceptionUtil;
	import main.br.org.indt.ndg.i18n.ConfigI18n;
	import main.br.org.indt.ndg.model.ImeiDTO;
	import main.br.org.indt.ndg.model.UserBalanceDTO;
	import main.br.org.indt.ndg.model.UserDTO;
	import main.br.org.indt.ndg.ui.component.powerdatagrid.QueryInputOutputDTO;
	
	import mx.collections.ArrayCollection;
	import mx.controls.Alert;
	import mx.events.ListEvent;
	import mx.managers.PopUpManager;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	import mx.rpc.remoting.mxml.RemoteObject;
	[Bindable] private var selectedUsersList:ArrayCollection = new ArrayCollection();
	[Bindable] private var imeiList:ArrayCollection = new ArrayCollection();	
	[Bindable] private var selectedImeis:ArrayCollection = new ArrayCollection();
	
	private var selectedUser:UserDTO;
	private static const REMOTE_SERVICE:String = "myService";
	[Bindable] private var stepText:int = 1;
	[Bindable] private var messageSize:int = 0;
	
	[Bindable] private var userBalanceMessage:String = "";
	[Bindable] private var hasBalance:Boolean = false;
	[Bindable] private var hasBalanceCheck:Boolean = false;
	private var balance:int = 0;

	private function init():void{
		usersGrid.selectedIndex = 0;
		loadImeis(null);
		customCheck.addEventListener(MouseEvent.CLICK, markAll);
		
		updatetUserBalanceUI();
	}
	
	private function updatetUserBalanceUI():void{
		var full:String = SessionClass.getInstance().loggedUser.hasFullPermissions;
		if (SessionClass.getInstance().isHostedMode && full.toUpperCase() != 'Y'){
			hasBalanceCheck = true;
			balancePanel.height = 16;
			var remoteObject:RemoteObject = new RemoteObject(REMOTE_SERVICE);
			remoteObject.showBusyCursor = true;
			remoteObject.addEventListener(FaultEvent.FAULT, onFault);
			remoteObject.addEventListener(ResultEvent.RESULT, onSuccess);
			remoteObject.getUserBalanceDTO(SessionClass.getInstance().loggedUser.username);
			SessionTimer.getInstance().resetTimer();
		} else{
			hasBalance = true;
		}

		function onFault(event:FaultEvent):void{
			Alert.show(ExceptionUtil.getMessage(event.fault.faultString),
					ConfigI18n.getInstance().getString("lblError"));
		}
		function onSuccess(event:ResultEvent):void{
			if (event.result != null) {
				var dto:UserBalanceDTO = event.result as UserBalanceDTO;
				balance = dto.sendAlerts;
				if (balance > 0){
					userBalanceMessage = ConfigI18n.getInstance().getString("lblSendAlertHasBalance01") 
						+ " " + balance + " " + ConfigI18n.getInstance().getString("lblSendAlertHasBalance02");
					hasBalance = true;
				} else if (balance <= 0){
					userBalanceMessage = ConfigI18n.getInstance().getString("lblSendAlertNoBalance");
					hasBalance = false;
				}
			}				
		}		
	}
		
	private function markAll(event:MouseEvent):void {
		selectedAll(customCheck.selected);
	}
	
	// Set selected surveys in main list	
	public function setSelectedUsers(listUsers:ArrayCollection):void{
		selectedUsersList = listUsers;
	}

	private function cancelSendAlert(event:MouseEvent):void{
		PopUpManager.removePopUp(this);
	}
	
	private function composeMessage(event:MouseEvent):void{
		if (selectedImeis.length > 0) {
			if (hasBalanceCheck && balance < selectedImeis.length){
				var msg:String = ConfigI18n.getInstance().getString("lblSendAlertNoAvaiableBalance01") + "\n" +
					ConfigI18n.getInstance().getString("lblSendAlertNoAvaiableBalance02") + selectedImeis.length + "\n" + 
					ConfigI18n.getInstance().getString("lblSendAlertNoAvaiableBalance03") + balance;
				Alert.show(msg, ConfigI18n.getInstance().getString("lblWarning"));
			} else{
				viewStackSendAlert.selectedIndex = 1;
			}
		} else {
			Alert.show(ConfigI18n.getInstance().getString("selectOneMoreImeis"));
		}
	}

	private function backStep(event:MouseEvent):void{
		var index:int = viewStackSendAlert.selectedIndex;
		if (index == 3){
			viewStackSendAlert.selectedIndex = 1;
		} else{
			viewStackSendAlert.selectedIndex = viewStackSendAlert.selectedIndex - 1;	
		}
	}	

	private function loadImeis(event:ListEvent):void{
		var remoteListImeis:RemoteObject = new RemoteObject(REMOTE_SERVICE);
		selectedUser = usersGrid.selectedItem as UserDTO;
		remoteListImeis.showBusyCursor = true;
		remoteListImeis.addEventListener(FaultEvent.FAULT, onFault);
		remoteListImeis.addEventListener(ResultEvent.RESULT, onSuccess);
		remoteListImeis.listImeisByUser(selectedUser.username, null, null, null, null, null, false);
		SessionTimer.getInstance().resetTimer();
		 
		function onSuccess(event:ResultEvent):void {
			if (event.result != null) {
				var result:QueryInputOutputDTO = event.result as QueryInputOutputDTO;
				var array:ArrayCollection = result.queryResult;
				imeiList = array;
				updateChecks();
			}
		}

		function onFault(event:FaultEvent):void	{
			Alert.show(event.message.toString());
		}
		
	}
	
	public function itemClick(event:ListEvent):void{
		var array:Array = imeiList.source as Array;
		var selItem:Object = null;
		var imei:ImeiDTO = imeiGrid.selectedItem as ImeiDTO;
		if (imei.selected){
			for (var j:int=0; j<selectedImeis.length; j++){
				selItem = selectedImeis.getItemAt(j);
				if (selItem.getId() == imei.getId()){
					break;
				}
			}
			if (j==selectedImeis.length){
				selectedImeis.addItem(imei);
			}
		} else{
			for (j=0; j<selectedImeis.length; j++){
				selItem = selectedImeis.getItemAt(j);
				if (selItem.getId() == imei.getId()){
					selectedImeis.removeItemAt(j);
					break;
				}
			}			
		}
	}
	
	private function updateChecks():void{
		var array:Array = imeiList.source as Array;
		var selItem:Object = null;
		for (var j:int=0; j<selectedImeis.length; j++){
			selItem = selectedImeis.getItemAt(j);
			for (var i:int=0; i<array.length; i++){
				if (selItem.getId() == array[i].getId()){
					array[i].selected = true;
					break;
				}
			}					
		}
	}
	
	private function selectedAll(selected:Boolean):void{
		var array:Array = imeiList.source as Array;
		var selItem:Object = null;
		if (selected){
			for (var i:int=0; i<array.length; i++){
				array[i].selected = true;
				for (var j:int=0; j<selectedImeis.length; j++){
					selItem = selectedImeis.getItemAt(j);
					if (selItem.getId() == array[i].getId()){
						break;
					}
				}
				if (j==selectedImeis.length){
					selectedImeis.addItem(array[i]);
				}
			}
		} else{
			for (i=0; i<array.length; i++){
				array[i].selected = false;
				for (j=0; j<selectedImeis.length; j++){
					selItem = selectedImeis.getItemAt(j);
					if (selItem.getId() == array[i].getId()){
						selectedImeis.removeItemAt(j);
						break;
					}
				}
			}	
		}		
	}
	
	private function sendAlert():void {
		
		if (message.length == 0) {
			Alert.show(ConfigI18n.getInstance().getString("lblValidateMessage"));
			return;
		} 
		var listOfImeis:ArrayCollection = new ArrayCollection();
		var remoteListImeis:RemoteObject = new RemoteObject(REMOTE_SERVICE);
		var selItem:ImeiDTO = null;
		
		for(var i:int=0; i<selectedImeis.length; i++){
			selItem = selectedImeis[i];
			listOfImeis.addItem(selItem.imei);
		}
		
		remoteListImeis.showBusyCursor = true;
		remoteListImeis.addEventListener(FaultEvent.FAULT, onFault);
		remoteListImeis.addEventListener(ResultEvent.RESULT, onSuccess);
		remoteListImeis.sendAlert(message.text, listOfImeis, 
				SessionClass.getInstance().loggedUser.username);
		SessionTimer.getInstance().resetTimer();
		 
		function onSuccess(event:ResultEvent):void {
				viewStackSendAlert.selectedIndex = 2;
		}

		function onFault(event:FaultEvent):void	{
			viewStackSendAlert.selectedIndex = 3;
		}
	}
	
	private function updateSteps():void{
		if (viewStackSendAlert.selectedIndex == 0){
			stp1.styleName = "wizardProgress";
			stp2.styleName = "wizardBlank";
			stp3.styleName = "wizardBlank";
			stepText = 1;
		} else if(viewStackSendAlert.selectedIndex == 1){
			stp1.styleName = "wizardProgress";
			stp2.styleName = "wizardProgress";
			stp3.styleName = "wizardBlank";
			stepText = 2;			
		} else if(viewStackSendAlert.selectedIndex == 2){
			stp1.styleName = "wizardProgress";
			stp2.styleName = "wizardProgress";
			stp3.styleName = "wizardProgress";
			stepText = 3;
		} else if(viewStackSendAlert.selectedIndex == 3){
			stp1.styleName = "wizardProgress";
			stp2.styleName = "wizardProgress";
			stp3.styleName = "wizardProgress";
			stepText = 3;
		}
	}
	
	private function updateSizeAlert():void{
		messageSize = message.length;
	}
	
	
