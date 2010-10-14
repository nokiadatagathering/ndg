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
	import main.br.org.indt.ndg.controller.adm.deviceManager.deviceModel.CloseDeviceModelManagerEvent;
	import main.br.org.indt.ndg.controller.util.ExceptionUtil;
	import main.br.org.indt.ndg.i18n.ConfigI18n;
	import main.br.org.indt.ndg.model.DeviceDTO;
	
	import mx.collections.ArrayCollection;
	import mx.controls.Alert;
	import mx.events.CloseEvent;
	import mx.events.ListEvent;
	import mx.managers.PopUpManager;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	import mx.rpc.remoting.mxml.RemoteObject;
	import mx.validators.Validator;

	
	[Bindable] public var modelList:ArrayCollection = new ArrayCollection();
	[Bindable] public var remoteList:RemoteObject = new RemoteObject(REMOTE_SERVICE);
	private static const REMOTE_SERVICE:String = "myService";
	
	[Bindable] public var dto:DeviceDTO = null;
	[Bindable] private var dtoAux:DeviceDTO = null;
		
	[Bindable] public var newMode:Boolean = false;
	[Bindable] public var editMode:Boolean = false;

	

	private function init():void{
		remoteList.showBusyCursor = true;
		remoteList.addEventListener(FaultEvent.FAULT, onFault);
		remoteList.addEventListener(ResultEvent.RESULT, onSuccess);
		this.listModels();
		
		function onFault(event:FaultEvent):void{
			Alert.show(ExceptionUtil.getMessage(event.fault.faultString),
					ConfigI18n.getInstance().getString("lblError"));
		}
			
		function onSuccess(event:ResultEvent):void{
			if (event.result != null) {
				modelList = event.result as ArrayCollection;
				if (modelList.length > 0){
					grid.selectedIndex = 0;
					grid.dispatchEvent(new ListEvent(ListEvent.CHANGE));
				}
			} else{
				modelList = new ArrayCollection();
			}
		}				
	}
	
	private function listModels():void{
		remoteList.listDevices();
		SessionTimer.getInstance().resetTimer();
	}
	
	private function close():void{
		var event:CloseDeviceModelManagerEvent = new CloseDeviceModelManagerEvent(CloseDeviceModelManagerEvent.EVENT_NAME);
		event.deviceModelsArray = modelList.toArray();
		dispatchEvent(event);
		PopUpManager.removePopUp(this);
	}
	
	private function showPreview(event:Event):void{
		dto = grid.selectedItem as DeviceDTO;
		if (editMode){
			newMode = false;
			editMode = false;
			setEditModeStyle(false, false);
			if (modelList.length > 0){
				grid.selectedIndex = 0;
				var eventt:ListEvent = new ListEvent(ListEvent.CHANGE);
				grid.dispatchEvent(eventt);
			}
		}
	}
	
	private function add():void{
		newMode = true;
		editMode = true;
		
		dtoAux = dto;
		dto = null;
		grid.selectedItem = null;
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
			
			var newDto:DeviceDTO = new DeviceDTO();
			if (newMode){
				newDto.deviceModel = inputModel.text;
				remoteObject.addEventListener(ResultEvent.RESULT, onCreateSuccess);
				remoteObject.saveDevice(newDto, false);
			} else{
				newDto.deviceModel = inputModel.text;
				newDto.idDevice = dto.idDevice;
				remoteObject.addEventListener(ResultEvent.RESULT, onUpdateSuccess);
				remoteObject.saveDevice(newDto, true);
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
			onSuccess();
			listModels();
		}
		
		function onUpdateSuccess(event:ResultEvent):void{
			dto.deviceModel = inputModel.text;
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
		if (modelList.length > 0){
			if (newModeCopy){
				if (dtoAux == null){
					inputModel.text = "";
				}				
				grid.selectedItem = dtoAux;
				var event:ListEvent = new ListEvent(ListEvent.CHANGE);
				grid.dispatchEvent(event);
			} else{
				dto = null;
				dto = grid.selectedItem as DeviceDTO;
			}
		}
	}
	
	private function remove():void{
		Alert.yesLabel = ConfigI18n.getInstance().getString("btnYes");
		Alert.noLabel = ConfigI18n.getInstance().getString("btnNo");
		Alert.show(ConfigI18n.getInstance().getString("deleteDeviceModelMessage"),
				ConfigI18n.getInstance().getString("lblQuestion"),
				(Alert.YES | Alert.NO), null, removeDevice, null, Alert.YES);
		
		function removeDevice(event:CloseEvent):void{
			if (event.detail == Alert.YES){
				var remoteObject:RemoteObject = new RemoteObject(REMOTE_SERVICE);
				remoteObject.showBusyCursor = true;
				remoteObject.addEventListener(FaultEvent.FAULT, onFault);			
				remoteObject.addEventListener(ResultEvent.RESULT, onSuccess);
				remoteObject.deleteDevice(dto);
				SessionTimer.getInstance().resetTimer();
			}
		}
		
		function onFault(event:FaultEvent):void{
			Alert.show(ExceptionUtil.getMessage(event.fault.faultString),
					ConfigI18n.getInstance().getString("lblError"));
		}
		
		function onSuccess(event:ResultEvent):void{
			listModels();
			newMode = false;
			editMode = false;
			setEditModeStyle(false, false);
		}
	}
		private function setEditModeStyle(edit:Boolean, neew:Boolean):void{
		inputModel.enabled = edit;
		if (edit){
			inputModel.setFocus();
		} else{
			inputModel.errorString = "";
		}			
	}
	
	private function getMaxLengthMsg(length:String):String{
		return resourceManager.getString(ConfigI18n.LOCALE_FILE, 'tooLongMsg') + length;
	}
	
	
	
	