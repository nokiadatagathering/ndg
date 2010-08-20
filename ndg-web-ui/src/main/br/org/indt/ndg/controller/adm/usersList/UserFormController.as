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


	import flash.display.DisplayObject;
	
	import main.br.org.indt.ndg.controller.access.SessionClass;
	import main.br.org.indt.ndg.controller.access.SessionTimer;
	import main.br.org.indt.ndg.controller.util.ExceptionUtil;
	import main.br.org.indt.ndg.i18n.ConfigI18n;
	import main.br.org.indt.ndg.model.RoleDTO;
	import main.br.org.indt.ndg.model.UserBalanceDTO;
	import main.br.org.indt.ndg.model.UserDTO;
	import main.br.org.indt.ndg.ui.component.powerdatagrid.PowerDataPagination;
	import main.br.org.indt.ndg.ui.view.adm.usersList.associatedImeis.AssociatedIMEIs;
	
	import mx.collections.ArrayCollection;
	import mx.controls.Alert;
	import mx.events.CloseEvent;
	import mx.events.ListEvent;
	import mx.managers.PopUpManager;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	import mx.rpc.remoting.mxml.RemoteObject;
	import mx.validators.Validator;
		
	public var pagination:PowerDataPagination = null;
		
	[Bindable] private var comboRolesDataProvider:Array = null;
	[Bindable] private var comboRolesSelectedIndex:int = -1;
	
	[Bindable] public var dto:UserDTO = null;
	[Bindable] private var dtoAux:UserDTO = null;
				
	[Bindable] public var newMode:Boolean = false;
	[Bindable] public var editMode:Boolean = false;
	
	[Bindable] private var userBalanceMessage:String = "";
	[Bindable] private var hasBalance:Boolean = false;
	[Bindable] private var hasBalanceCheck:Boolean = false;
	
	private static const REMOTE_SERVICE:String = "myService";
	
	
	private function init():void{
		initRoles();
		updatetUserBalanceUI();
	}
	
	private function initRoles():void{
		var remoteObject:RemoteObject = new RemoteObject(REMOTE_SERVICE);
		remoteObject.showBusyCursor = true;
		remoteObject.addEventListener(FaultEvent.FAULT, onFault);
		remoteObject.addEventListener(ResultEvent.RESULT, onSuccess);
		remoteObject.listRoles();
		SessionTimer.getInstance().resetTimer();
				
		function onFault(event:FaultEvent):void{
			Alert.show(ExceptionUtil.getMessage(event.fault.faultString),
					ConfigI18n.getInstance().getString("lblError"));
		}
		function onSuccess(event:ResultEvent):void{
			if (event.result != null) {
				var array:ArrayCollection = event.result as ArrayCollection;
				comboRolesDataProvider = array.toArray();
			}				
		}
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
				var qtd:int = dto.users;
				if (qtd > 0){
					userBalanceMessage = ConfigI18n.getInstance().getString("lblUserHasBalance01") 
						+ " " + qtd + " " + ConfigI18n.getInstance().getString("lblUserHasBalance02");
					hasBalance = true;
				} else if (qtd <= 0){
					userBalanceMessage = ConfigI18n.getInstance().getString("lblUserNoBalance");
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
		comboRolesSelectedIndex = -1;
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
				var newDto:UserDTO = new UserDTO();
				getTypedDto(newDto);
				remoteObject.addEventListener(ResultEvent.RESULT, onCreateSuccess);
				remoteObject.saveUser(SessionClass.getInstance().loggedUser.username, newDto, false);
			} else{
				getTypedDto(dto);
				remoteObject.addEventListener(ResultEvent.RESULT, onUpdateSuccess);
				remoteObject.saveUser(SessionClass.getInstance().loggedUser.username, dto, true);
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
					inputUser.text = "";
					inputFirstName.text = "";
					inputLastName.text = "";
					inputEmail.text = "";
					comboRoles.selectedIndex = -1;
					inputCountryCode.text = "";
					inputAreaCode.text = "";
					inputPhone.text = "";
					inputPassword.text = "";
				}				
				pagination.grid.selectedItem = dtoAux;
				var event:ListEvent = new ListEvent(ListEvent.CHANGE);
				pagination.grid.dispatchEvent(event);
			} else{
				dto = null;
				dto = pagination.grid.selectedItem as UserDTO;
				setSelectedComboRolesItem();
			}
		}
	}
	
	private function remove():void{
		Alert.yesLabel = ConfigI18n.getInstance().getString("btnYes");
		Alert.noLabel = ConfigI18n.getInstance().getString("btnNo");
		Alert.show(ConfigI18n.getInstance().getString("deleteUserMessage"),
				ConfigI18n.getInstance().getString("lblQuestion"),
				(Alert.YES | Alert.NO), null, removeUser, null, Alert.YES);
		
		function removeUser(event:CloseEvent):void{
			if (event.detail == Alert.YES){
				var remoteObject:RemoteObject = new RemoteObject(REMOTE_SERVICE);
				remoteObject.showBusyCursor = true;
				remoteObject.addEventListener(FaultEvent.FAULT, onFault);			
				remoteObject.addEventListener(ResultEvent.RESULT, onSuccess);
				remoteObject.deleteUser(dto);
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
		inputUser.enabled = (edit && neew);
		inputFirstName.enabled = edit;
		inputLastName.enabled = edit;
		inputEmail.enabled = (edit && neew);
		comboRoles.enabled = edit;
		inputCountryCode.enabled = edit;
		inputAreaCode.enabled = edit;
		inputPhone.enabled = edit;
		inputPassword.enabled = edit;
		if (edit){
			if (neew){
				inputUser.setFocus();
			} else{
				inputFirstName.setFocus();
			}
			passValidator.enabled = neew;
		} else{
			inputUser.errorString = "";
			inputFirstName.errorString = "";
			inputLastName.errorString = "";
			inputEmail.errorString = "";
			comboRoles.errorString = "";
			inputCountryCode.errorString = "";
			inputAreaCode.errorString = "";
			inputPhone.errorString = "";
			inputPassword.errorString = "";
			
			inputPassword.text = "";
		}			
	}
	
	private function getTypedDto(dto:UserDTO):void{
		dto.username = inputUser.text;
		dto.firstName = inputFirstName.text;
		dto.lastName = inputLastName.text;
		dto.email = inputEmail.text;
		dto.role = comboRoles.selectedItem as RoleDTO;
		dto.countryCode = inputCountryCode.text;
		dto.areaCode = inputAreaCode.text;
		dto.phoneNumber = inputPhone.text;
		dto.password = inputPassword.text;
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
	
	public function setSelectedComboRolesItem():void{
		var rolesArray:Array = comboRolesDataProvider;
		var selectedIndex:int = -1;
		for (var i:int = 0; i<rolesArray.length; i++){
			if (dto.role.getId() == rolesArray[i].getId()){
				selectedIndex = i;
				break;
			}
		}
		comboRolesSelectedIndex = -1;
		comboRolesSelectedIndex = selectedIndex;
	}
	
	private function updatePassValidatorState():void{
		passValidator.enabled = (inputPassword.text.length > 0);
	}
	
	private function getMaxLengthMsg(length:String):String{
		return resourceManager.getString(ConfigI18n.LOCALE_FILE, 'tooLongMsg') + length;
	}
	private function getMinLengthMsg(length:String):String{
		return resourceManager.getString(ConfigI18n.LOCALE_FILE, 'tooShortMsg') + length;
	}		
	
	private function showAssociatedImeis():void{
		var imeis:AssociatedIMEIs = new AssociatedIMEIs();
		imeis.userDto = dto;
		PopUpManager.addPopUp(imeis, this.parentApplication as DisplayObject, true);
		PopUpManager.centerPopUp(imeis);
	}
	
	
	
	
