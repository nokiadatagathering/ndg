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

	import main.br.org.indt.ndg.controller.access.SessionClass;
	import main.br.org.indt.ndg.controller.access.SessionTimer;
	import main.br.org.indt.ndg.controller.main.resultimport.CloseResultImportEvent;
	import main.br.org.indt.ndg.i18n.ConfigI18n;
	import main.br.org.indt.ndg.model.ImeiDTO;	
	import main.br.org.indt.ndg.model.SurveyDTO;
	
	import mx.collections.ArrayCollection;
	import mx.controls.Alert;
	import mx.managers.PopUpManager;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	import mx.rpc.remoting.mxml.RemoteObject;

	[Bindable]private var comboArr:Array = null;

	private static const REMOTE_SERVICE:String = "myService";

	public var surveyDTO:SurveyDTO = null;
	private var imeiDTO:ImeiDTO;

	[Bindable] private var stepText:int = 1;


	private function init():void{
		comboArr = new Array();
		comboArr.push(ConfigI18n.getInstance().getString("selectDrive"));
		driveCombo.selectedIndex = 0;
		
		var remoteObject:RemoteObject = new RemoteObject(REMOTE_SERVICE);
		remoteObject.showBusyCursor = true;
		remoteObject.addEventListener(FaultEvent.FAULT, onFault);
		remoteObject.addEventListener(ResultEvent.RESULT, onSuccess);
		remoteObject.listRoots();
		SessionTimer.getInstance().resetTimer();
		
		function onSuccess(event:ResultEvent):void {
			var array:ArrayCollection = event.result as ArrayCollection;
			for (var i:int; i < array.length; i++){
				comboArr.push(array[i]);
			}
		}
		function onFault(event:FaultEvent):void	{
		}
	}
	
	private function callImport():void{
		var remoteObject:RemoteObject = new RemoteObject(REMOTE_SERVICE);
		remoteObject.showBusyCursor = true;
		remoteObject.addEventListener(FaultEvent.FAULT, onFault);
		remoteObject.addEventListener(ResultEvent.RESULT, onSuccess);

		if (driveCombo.selectedIndex > 0){
			remoteObject.readResultsFromDevice(SessionClass.getInstance().loggedUser.username,
					surveyDTO.idSurvey, comboArr[driveCombo.selectedIndex]);
			SessionTimer.getInstance().resetTimer();
		} else{
			Alert.show(ConfigI18n.getInstance().getString("wrongDrive"),
					   ConfigI18n.getInstance().getString("lblError"));
		}
		
		function onSuccess(event:ResultEvent):void {
			viewStack.selectedIndex = 2;
		}
		
		function onFault(event:FaultEvent):void	{
			viewStack.selectedIndex = 1;
		}
	}

	private function close(success:int):void{
		if (success) {
			var event:CloseResultImportEvent = new CloseResultImportEvent(CloseResultImportEvent.EVENT_NAME);
			dispatchEvent(event);
		}
		PopUpManager.removePopUp(this);
	}
	
	private function updateSteps():void{
		if (viewStack.selectedIndex == 0){
			modeIcon.visible = false;
			stp1.styleName = "wizardProgress";
			stp2.styleName = "wizardBlank";
			stepText = 1;
		} else if(viewStack.selectedIndex == 1){
			stp1.styleName = "wizardProgress";
			stp2.styleName = "wizardProgress";
			stepText = 2;			
		} else if(viewStack.selectedIndex == 2){
			stp1.styleName = "wizardProgress";
			stp2.styleName = "wizardProgress";
			stepText = 2;
		}
	}

	private function checkImei():void{
		if (driveCombo.selectedIndex == 0) {
			deviceImeiText.text = "";
			btnImportNext.enabled = false;
			return;
		}
		
		var remoteObject:RemoteObject = new RemoteObject(REMOTE_SERVICE);
		remoteObject.showBusyCursor = true;	
		remoteObject.addEventListener(FaultEvent.FAULT, onFault);
		remoteObject.addEventListener(ResultEvent.RESULT, onSuccess);
		remoteObject.getImeiFromDrive(driveCombo.text);
		SessionTimer.getInstance().resetTimer();
		
		function onSuccess(event:ResultEvent):void {
			if (event != null) {
				btnImportNext.enabled = true;
				imeiDTO = event.result as ImeiDTO;
				deviceImeiText.text = ConfigI18n.getInstance().getString('lblSendSurveyImeiDevice') + ' ' + imeiDTO.imei;
			}
		}
		function onFault(event:FaultEvent):void	{
			deviceImeiText.text = ConfigI18n.getInstance().getString('driveIsNotMobile');
			btnImportNext.enabled = false;
		}
	}
	
	private function loadDevices():void{
		btnImportNext.enabled = false;
		comboArr = new Array();
		comboArr.push(ConfigI18n.getInstance().getString("selectDrive"));
		driveCombo.selectedIndex = 0;

		var remoteObject:RemoteObject = new RemoteObject(REMOTE_SERVICE);
		remoteObject.showBusyCursor = true;		
		remoteObject.addEventListener(FaultEvent.FAULT, onFault);
		remoteObject.addEventListener(ResultEvent.RESULT, onSuccess);
		remoteObject.listRoots();
		SessionTimer.getInstance().resetTimer();
		
		function onSuccess(event:ResultEvent):void {
			var array:ArrayCollection = event.result as ArrayCollection;
			deviceImeiText.text = "";
			for (var i:int; i < array.length; i++)
				comboArr.push(array[i]);
		}
		
		function onFault(event:FaultEvent):void	{
			Alert.show(ConfigI18n.getInstance().getString('lblCantLoadDrives'));		
		}
	}	