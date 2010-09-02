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


	import flash.events.Event;
	
	import main.br.org.indt.ndg.controller.access.SessionClass;
	import main.br.org.indt.ndg.controller.access.SessionTimer;
	import main.br.org.indt.ndg.controller.adm.deviceManager.deviceModel.CloseDeviceModelManagerEvent;
	import main.br.org.indt.ndg.controller.util.ExceptionUtil;
	import main.br.org.indt.ndg.i18n.ConfigI18n;
	import main.br.org.indt.ndg.model.DeviceDTO;
	import main.br.org.indt.ndg.model.ImeiDTO;
	import main.br.org.indt.ndg.model.UserBalanceDTO;
	import main.br.org.indt.ndg.model.UserDTO;
	import main.br.org.indt.ndg.ui.component.powerdatagrid.PowerDataPagination;
	import main.br.org.indt.ndg.ui.view.adm.deviceManager.associatedSurveys.AssociatedSurveys;
	import main.br.org.indt.ndg.ui.view.adm.deviceManager.deviceModel.DeviceModelManager;
	
	import mx.controls.Alert;
	import mx.events.CloseEvent;
	import mx.events.ListEvent;
	import mx.managers.PopUpManager;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	import mx.rpc.remoting.mxml.RemoteObject;
	import mx.utils.Base64Decoder;
	import mx.validators.Validator;
		
		
	private var strFileContent:String = null;
	public var pagination:PowerDataPagination = null;
	public var selectedUser:UserDTO = null;
		
	[Bindable] public var comboDevicesDataProvider:Array = null;
	[Bindable] private var comboDevicesSelectedIndex:int = -1;
	
	[Bindable] public var dto:ImeiDTO = null;
	[Bindable] private var dtoAux:ImeiDTO = null;
				
	[Bindable] public var newMode:Boolean = false;
	[Bindable] public var editMode:Boolean = false;
	
	[Bindable] private var userBalanceMessage:String = "";
	[Bindable] private var hasBalance:Boolean = false;
	[Bindable] private var hasBalanceCheck:Boolean = false;
	
	private static const REMOTE_SERVICE:String = "myService";
	
	
	private function init():void{
		updatetUserBalanceUI();
	}
	
	private function updatetUserBalanceUI():void{
		var full:String = SessionClass.getInstance().loggedUser.hasFullPermissions;
		if (SessionClass.getInstance().isHostedMode && full.toUpperCase() != 'Y'){
			hasBalanceCheck = true;
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
				var qtd:int = dto.imeis;
				if (qtd > 0){
					userBalanceMessage = ConfigI18n.getInstance().getString("lblImeiHasBalance01") 
						+ " " + qtd + " " + ConfigI18n.getInstance().getString("lblImeiHasBalance02");
					hasBalance = true;
				} else if (qtd <= 0){
					userBalanceMessage = ConfigI18n.getInstance().getString("lblImeiNoBalance");
					hasBalance = false;
				}
			}				
		}		
	}
	
	
	private function add():void{
		newMode = true;
		editMode = true;
		
		dtoAux = dto;
		dto = null;
		pagination.grid.selectedItem = null;
		comboDevicesSelectedIndex = -1;
		setEditModeStyle(true, true);
	}
	
	private function edit():void{
		newMode = false;
		editMode = true;
		setEditModeStyle(true, false);
	}
	
	private function save():void{
		if (isValidatedFields()){
			var remoteObject:RemoteObject = new RemoteObject(REMOTE_SERVICE);
			remoteObject.showBusyCursor = true;
			remoteObject.addEventListener(FaultEvent.FAULT, onFault);
			if (newMode){
				var newDto:ImeiDTO = new ImeiDTO();
				getTypedDto(newDto);
				remoteObject.addEventListener(ResultEvent.RESULT, onCreateSuccess);
				remoteObject.saveImei(newDto, false);
			} else{
				getTypedDto(dto);
				remoteObject.addEventListener(ResultEvent.RESULT, onUpdateSuccess);
				remoteObject.saveImei(dto, true);
			}
			SessionTimer.getInstance().resetTimer();
		} else{
			Alert.show(ConfigI18n.getInstance().getString("redFieldsMsg"),
					ConfigI18n.getInstance().getString("lblWarning"));
		}
		
		function onFault(event:FaultEvent):void{
			Alert.show(ExceptionUtil.getMessage(event.fault.faultString),
					ConfigI18n.getInstance().getString("lblError"));
		}
		
		function onCreateSuccess(event:ResultEvent):void{
			if (SessionClass.getInstance().hasSmsSupport){
				Alert.show(ConfigI18n.getInstance().getString("smsLinkJad"),
					ConfigI18n.getInstance().getString("lblWarning"));
			} else{
				jadDownload(inputPhoneNumber.text);	
			}
			updateFirstTimeUser();
			updatetUserBalanceUI();
			pagination.refresh();
			onSuccess();
		}
		
		function onUpdateSuccess(event:ResultEvent):void{
			onSuccess();
		}
		
		function onSuccess():void{
			newMode = false;
			editMode = false;
			setEditModeStyle(false, false);
		}
		
		function isValidatedFields():Boolean{
			var result:Boolean = false;
			var errors:Array = Validator.validateAll(validators);
			if (errors.length == 0){
				result = true;
			}
			return result;
		}
	}
	
	public function cancel():void{
		var newModeCopy:Boolean = newMode;
		newMode = false;
		editMode = false;
		setEditModeStyle(false, false);
		if ((pagination.grid.dataProvider.source as Array).length > 0){
			if (newModeCopy){
				if (dtoAux == null){
					//inputImei.text = "";
					inputPhoneNumber.text = "";
					comboModels.selectedIndex = -1;
				}				
				pagination.grid.selectedItem = dtoAux;
				var event:ListEvent = new ListEvent(ListEvent.CHANGE);
				pagination.grid.dispatchEvent(event);
			} else{
				dto = null;
				dto = pagination.grid.selectedItem as ImeiDTO;
				setSelectedComboDevicesItem();
			}
		}
	}
	
	private function remove():void{
		Alert.yesLabel = ConfigI18n.getInstance().getString("btnYes");
		Alert.noLabel = ConfigI18n.getInstance().getString("btnNo");
		Alert.show(ConfigI18n.getInstance().getString("deleteImeiMessage"),
				ConfigI18n.getInstance().getString("lblQuestion"),
				(Alert.YES | Alert.NO), null, removeImei, null, Alert.YES);
		
		function removeImei(event:CloseEvent):void{
			if (event.detail == Alert.YES){
				var remoteObject:RemoteObject = new RemoteObject(REMOTE_SERVICE);
				remoteObject.showBusyCursor = true;
				remoteObject.addEventListener(FaultEvent.FAULT, onFault);			
				remoteObject.addEventListener(ResultEvent.RESULT, onSuccess);
				remoteObject.deleteImei(dto);
				SessionTimer.getInstance().resetTimer();
			}
		}
		
		function onFault(event:FaultEvent):void{
			Alert.show(ExceptionUtil.getMessage(event.fault.faultString),
					ConfigI18n.getInstance().getString("lblError"));
		}
		
		function onSuccess(event:ResultEvent):void{
			pagination.refresh();
			newMode = false;
			editMode = false;
			setEditModeStyle(false, false);
		}
	}
	
	private function setEditModeStyle(edit:Boolean, neew:Boolean):void{
		//inputImei.enabled = (edit && neew);
		inputPhoneNumber.enabled = edit;
		comboModels.enabled = edit;
		if (edit){
			//if (neew){
			//	inputImei.setFocus();
			//} else{
				inputPhoneNumber.setFocus();
			//}
		} else{
			//inputImei.errorString = "";
			inputPhoneNumber.errorString = "";
			comboModels.errorString = "";
		}			
	}
	
	private function getTypedDto(dto:ImeiDTO):void{
		//dto.imei = inputImei.text;
		dto.msisdn = inputPhoneNumber.text;
		dto.userName = selectedUser.username;
		dto.device = comboModels.selectedItem as DeviceDTO;
	}
	
	public function refresh():void{
		newMode = false;
		editMode = false;
		setEditModeStyle(false, false);
		if ((pagination.grid.dataProvider.source as Array).length > 0){
			pagination.grid.selectedIndex = 0;
			var event:ListEvent = new ListEvent(ListEvent.CHANGE);
			pagination.grid.dispatchEvent(event);
		}
	}
	
	public function setSelectedComboDevicesItem():void{
		var devicesArray:Array = comboDevicesDataProvider;
		var selectedIndex:int = -1;
		for (var i:int = 0; i<devicesArray.length; i++){
			if (dto.device.idDevice == devicesArray[i].idDevice){
				selectedIndex = i;
				break;
			}
		}
		comboDevicesSelectedIndex = -1;
		comboDevicesSelectedIndex = selectedIndex;
	}
	
	private function getMaxLengthMsg(length:String):String{
		return resourceManager.getString(ConfigI18n.LOCALE_FILE, 'tooLongMsg') + length;
	}
	private function getMinLengthMsg(length:String):String{
		return resourceManager.getString(ConfigI18n.LOCALE_FILE, 'tooShortMsg') + length;
	}
	
	private function showAssociatedSurveys():void{
		var surveys:AssociatedSurveys = new AssociatedSurveys();
		surveys.imeiDto = dto;
		PopUpManager.addPopUp(surveys, this.parentApplication as DisplayObject, true);
		PopUpManager.centerPopUp(surveys);
	}
	
	private function showDeviceModelManager(event:Event):void{
		var view:DeviceModelManager = new DeviceModelManager();
		view.addEventListener(CloseDeviceModelManagerEvent.EVENT_NAME, closePopup);
		PopUpManager.addPopUp(view, this.parentApplication as DisplayObject, true);
		PopUpManager.centerPopUp(view);
		
		function closePopup(event:CloseDeviceModelManagerEvent):void{
			var selectedDevice:DeviceDTO = comboModels.selectedItem as DeviceDTO;
			comboDevicesDataProvider = event.deviceModelsArray;
			
			if (selectedDevice != null){
				for (var i:int = 0; i<comboDevicesDataProvider.length; i++){
					if (selectedDevice.idDevice == comboDevicesDataProvider[i].idDevice){
						comboDevicesSelectedIndex = -1;
						comboDevicesSelectedIndex = i;
						break;
					}
				}
				
			}
		}
	}
	
	private function updateFirstTimeUser():void{
		if (SessionClass.getInstance().loggedUser.firstTimeUse.toLocaleUpperCase() == 'Y') {
			var userDTO:UserDTO = new UserDTO();
			userDTO = SessionClass.getInstance().loggedUser;
			userDTO.password = "";
			userDTO.firstTimeUse = 'N';
			
			var remoteObject:RemoteObject = new RemoteObject(REMOTE_SERVICE);
			remoteObject.showBusyCursor = true;
			remoteObject.addEventListener(FaultEvent.FAULT, onFault);
			remoteObject.addEventListener(ResultEvent.RESULT, onSuccess);
			remoteObject.saveUser(SessionClass.getInstance().loggedUser.username, userDTO, true);
			
			function onFault(event:FaultEvent):void{
				Alert.show(ExceptionUtil.getMessage(event.fault.faultString),
						ConfigI18n.getInstance().getString("lblError"));
			}
			
			function onSuccess(event:ResultEvent):void{
				
			}
			
		}
	}
	
	
	private function jadDownload(phoneNumber:String):void{		
		var remoteObject:RemoteObject = new RemoteObject(REMOTE_SERVICE);
		remoteObject.showBusyCursor = true;
		remoteObject.addEventListener(FaultEvent.FAULT, onFault);
		remoteObject.addEventListener(ResultEvent.RESULT, onSuccess);
		remoteObject.jadDownload(phoneNumber);
		SessionTimer.getInstance().resetTimer();
				
		function onSuccess(event:ResultEvent):void {
			if (event.result != null) {
				strFileContent = event.result as String;
				Alert.show(ConfigI18n.getInstance().getString("saveDownloadJad"),
					ConfigI18n.getInstance().getString("lblWarning"),
					Alert.OK, null, afterConfirmation);
			}
		}
		
		function afterConfirmation(event:CloseEvent):void{
			var fileName:String = "ndg_" + phoneNumber + ".jad";
			var de:Base64Decoder = new Base64Decoder();
			de.decode(strFileContent);
			var byteArr:ByteArray = de.toByteArray();
			var fr:FileReference = new FileReference();
			fr.save(byteArr, fileName);
		}
			
		function onFault(event:FaultEvent):void	{
			Alert.show(ExceptionUtil.getMessage(event.fault.faultString),
					ConfigI18n.getInstance().getString("lblError"));
		}
	}
	
	
	
	