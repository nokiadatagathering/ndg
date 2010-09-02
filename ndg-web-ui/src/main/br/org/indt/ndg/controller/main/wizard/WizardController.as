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
	import main.br.org.indt.ndg.controller.main.wizard.AddDeviceEvent;
	import main.br.org.indt.ndg.controller.main.wizard.RemoveDeviceEvent;
	import main.br.org.indt.ndg.controller.util.ExceptionUtil;
	import main.br.org.indt.ndg.i18n.ConfigI18n;
	import main.br.org.indt.ndg.model.UserDTO;
	import main.br.org.indt.ndg.ui.view.main.wizard.DeviceItemSelected;
	
	import mx.collections.ArrayCollection;
	import mx.controls.Alert;
	import mx.managers.PopUpManager;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	import mx.rpc.remoting.mxml.RemoteObject;
	
	[Bindable] private var stepText:int = 1;
	[Bindable] private var wizardTitle:String;
	[Bindable] private var arrayDevices:ArrayCollection = new ArrayCollection();
	[Bindable] private var arrayDevicesSaved:Array = new Array(false, false, false, false, false);
	[Bindable] private var xBy:int = 150;	
	private var selectedIndex:int = 0;
	private var boxSelectedPosition:int = 0;
	private var distanceIndex:int = 0;
	private var isPCinstruction:Boolean = false;
	
	
	private static const REMOTE_SERVICE:String = "myService";	
	
	private function createDevicesBox():void{
		
		var device:DeviceItemSelected = new DeviceItemSelected();
		device.deviceId = "1";
		device.id = "device1";
		device.x = 18;
		device.y = 6;
		device.addEventListener(AddDeviceEvent.EVENT_NAME, addDevice);
		device.addEventListener(RemoveDeviceEvent.EVENT_NAME, removeDevice);
		arrayDevices.addItem(device);
		containerDevices.addChild(device);
		
		device = new DeviceItemSelected();
		device.deviceId = "2";
		device.id = "device2";
		device.x = 173;
		device.y = 6;
		device.addEventListener(AddDeviceEvent.EVENT_NAME, addDevice);
		device.addEventListener(RemoveDeviceEvent.EVENT_NAME, removeDevice);
		arrayDevices.addItem(device);
		containerDevices.addChild(device);
		
		device = new DeviceItemSelected();
		device.deviceId = "3";
		device.id = "device3";
		device.x = 327;
		device.y = 6;
		device.addEventListener(AddDeviceEvent.EVENT_NAME, addDevice);
		device.addEventListener(RemoveDeviceEvent.EVENT_NAME, removeDevice);
		arrayDevices.addItem(device);
		containerDevices.addChild(device);		
		
		device = new DeviceItemSelected();
		device.deviceId = "4";
		device.id = "device4";
		device.x = 481;
		device.y = 6;
		device.addEventListener(AddDeviceEvent.EVENT_NAME, addDevice);
		device.addEventListener(RemoveDeviceEvent.EVENT_NAME, removeDevice);
		arrayDevices.addItem(device);
		containerDevices.addChild(device);		

		device = new DeviceItemSelected();
		device.deviceId = "5";
		device.id = "device5";
		device.x = 636;
		device.y = 6;
		device.addEventListener(AddDeviceEvent.EVENT_NAME, addDevice);
		device.addEventListener(RemoveDeviceEvent.EVENT_NAME, removeDevice);
		arrayDevices.addItem(device);
		containerDevices.addChild(device);
	}
	
	private function addDevice(event:AddDeviceEvent):void {
		for (var i:int=0; i<arrayDevices.length; i++) {
			if (arrayDevices[i].imeiDTO != null){
				btnSaveAndSend.enabled = true;
				return;
			}
		}
	}
	
	private function removeDevice(event:RemoveDeviceEvent):void {
		for (var i:int=0; i<arrayDevices.length; i++) {
			if (arrayDevices[i].imeiDTO != null){
				return;
			}
		}
		btnSaveAndSend.enabled = false;
	}
	
	private function closeWizard(event:MouseEvent):void{
		updateFirstTimeUser();
		PopUpManager.removePopUp(this);
	}
	
	private function nextStep(event:MouseEvent):void{
		viewStackWizard.selectedIndex = viewStackWizard.selectedIndex + 1;
	}

	private function backStep(event:MouseEvent):void{
		viewStackWizard.selectedIndex = viewStackWizard.selectedIndex - 1;
	}

	private function pcInstructions(event:MouseEvent):void{
		isPCinstruction = true;
		viewStackWizard.selectedIndex = 8;
	}
	
	private function smsInstructions(event:MouseEvent):void{
		isPCinstruction = false;
		nextStep(event);
	}	

	private function finishStep(event:MouseEvent):void{
		viewStackWizard.selectedIndex = 7;
	}

	private function backFinishStep(event:MouseEvent):void{
		if (isPCinstruction) {
			viewStackWizard.selectedIndex = 9;	
		} else {
			backStep(event);	
		}
	}
	
	private function messageConfirmation(event:MouseEvent):void{
		viewStackWizard.selectedIndex = 2;
	}		
	
	private function validateDevices():Boolean{
		var stringDevices:String = "";
		
		for (var i:int=0; i<arrayDevices.length; i++) {
			if (arrayDevices[i].currentState == "cancel_ok"){
				if (stringDevices.length > 0) {
					stringDevices += ", #" + arrayDevices[i].deviceId;	
				} else {
					stringDevices += "#" + arrayDevices[i].deviceId;
				}
			}
		}
		
		if (stringDevices.length > 0) {
				Alert.show(ConfigI18n.getInstance().getString("lblValidationMessage") + " " + stringDevices,
						ConfigI18n.getInstance().getString("lblError"));
				return false;			
		}		
		return true;
	}
	
	private function saveAndSend(event:MouseEvent):void{
		if (!validateDevices()) {
			return;
		}
		
		var arrayImei:ArrayCollection = new ArrayCollection();
		var remoteObject:RemoteObject = new RemoteObject(REMOTE_SERVICE);
				
		for (var i:int=0; i<arrayDevices.length; i++) {
			arrayImei.addItem(arrayDevices[i].imeiDTO);
		}

		remoteObject.showBusyCursor = true;
		remoteObject.addEventListener(FaultEvent.FAULT, onFault);
		remoteObject.addEventListener(ResultEvent.RESULT, onSuccess);
		remoteObject.saveImeis(arrayImei, arrayDevicesSaved);
						
		SessionTimer.getInstance().resetTimer();
		
		function onFault(event:FaultEvent):void{
			Alert.show(ExceptionUtil.getMessage(event.fault.faultString),
					ConfigI18n.getInstance().getString("lblError"));
		}
		
		function onSuccess(event:ResultEvent):void{
			var arrayResult:ArrayCollection = event.result as ArrayCollection;
			var statusFail:Boolean = false;
			
			for (var i:int=0; i  < arrayResult.length; i++) {
				if (arrayResult[i].toString() == "ok") {
					(arrayDevices[i] as DeviceItemSelected).buttonAdd.enabled = false;
					(arrayDevices[i] as DeviceItemSelected).buttonRemove.enabled = false;
					arrayDevicesSaved[i] = true;
				} else if (arrayResult[i].toString() != "not ok") {
					(arrayDevices[i] as DeviceItemSelected).setCurrentState("problem", true);
					(arrayDevices[i] as DeviceItemSelected).lblError.text = ExceptionUtil.getLocalizedMessage(arrayResult[i].toString());
					arrayDevicesSaved[i] = false;
					statusFail = true;
				}
			}

			if (!statusFail) {
				nextStep(null);
			}			
		} 				
	}
	
	private function updateFirstTimeUser():void{
		if (chkFirstTimeUser.selected || viewStackWizard.selectedIndex > 1) {
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
	
	private function updateSteps():void{
		if (viewStackWizard.selectedIndex == 0){
 			wizardTitle = ConfigI18n.getInstance().getString('lblWizardWelcomeTitle');			
			stp1.styleName = "wizardProgress";
			stp2.styleName = "wizardBlank";
			stp3.styleName = "wizardBlank";
			stp4.styleName = "wizardBlank";
			stepText = 1;
		} else if(viewStackWizard.selectedIndex == 1){
			wizardTitle = ConfigI18n.getInstance().getString('lblWizardDownloadTitle');
			stp1.styleName = "wizardProgress";
			stp2.styleName = "wizardProgress";
			stp3.styleName = "wizardBlank";
			stp4.styleName = "wizardBlank";
			stepText = 2;			
		} else if(viewStackWizard.selectedIndex == 2){
			wizardTitle = ConfigI18n.getInstance().getString('lblWizardMessageTitle');
			stp1.styleName = "wizardProgress";
			stp2.styleName = "wizardProgress";
			stp3.styleName = "wizardProgress";
			stp4.styleName = "wizardBlank";
			stepText = 3;
		} else if(viewStackWizard.selectedIndex == 3){
			wizardTitle = ConfigI18n.getInstance().getString('lblWizardInstallationTitle');			
			stp1.styleName = "wizardProgress";
			stp2.styleName = "wizardProgress";
			stp3.styleName = "wizardProgress";
			stp4.styleName = "wizardProgress";
			stepText = 4;
		} else if(viewStackWizard.selectedIndex == 7){
			wizardTitle = ConfigI18n.getInstance().getString('lblWizardInstallationTitle');			
			stp1.styleName = "wizardProgress";
			stp2.styleName = "wizardProgress";
			stp3.styleName = "wizardProgress";
			stp4.styleName = "wizardProgress";
			stepText = 4;
		}
	}