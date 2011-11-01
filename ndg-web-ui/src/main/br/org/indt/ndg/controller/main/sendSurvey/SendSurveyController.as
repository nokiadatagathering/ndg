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
	
	import flash.events.MouseEvent;
	
	import main.br.org.indt.ndg.controller.access.SessionClass;
	import main.br.org.indt.ndg.controller.access.SessionTimer;
	import main.br.org.indt.ndg.controller.util.ExceptionUtil;
	import main.br.org.indt.ndg.i18n.ConfigI18n;
	import main.br.org.indt.ndg.model.ImeiDTO;
	import main.br.org.indt.ndg.model.SurveyDTO;
	import main.br.org.indt.ndg.ui.component.powerdatagrid.ChangePageEvent;
	
	import mx.collections.ArrayCollection;
	import mx.controls.Alert;
	import mx.events.ListEvent;
	import mx.managers.PopUpManager;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	import mx.rpc.remoting.mxml.RemoteObject;
	import mx.utils.Base64Decoder;
	
	[Bindable] public var imeiList:ArrayCollection = new ArrayCollection();
	[Bindable] public var selectedImeisList:ArrayCollection = new ArrayCollection();
	[Bindable] public var selectedSurveyList:ArrayCollection = new ArrayCollection();
	[Bindable] public var remoteListImeis:RemoteObject = new RemoteObject(REMOTE_SERVICE);
	[Bindable] private var deviceList:Array = null;
	[Bindable] private var stepText:int = 1;
	[Bindable] private var searchOptionsLabels:ArrayCollection = null;
    [Bindable] private var searchOptionsFields:ArrayCollection = null;

	private var transmissionMode:String;
	private var imeiDTO:ImeiDTO;
	private var fileName:String = null;
	private	var strFileContent:String;

	private static const REMOTE_SERVICE:String = "myService";
	private static const MODE_CABLE:String = "cable";
	private static const MODE_SMS:String = "sms";
	private static const MODE_GPRS:String = "gprs";
	private static const MODE_DOWNLOAD:String = "download";
	private static const IMEI_PAGE_SIZE:int = 5;

	[Embed("../../../../../../../resources/images/ICON_HEADER_USB.png")] private var cableModeIcon:Class;
	[Embed("../../../../../../../resources/images/ICON_HEADER_SMS.png")] private var smsModeIcon:Class;
	[Embed("../../../../../../../resources/images/ICON_HEADER_INTERNET.png")] private var gprsModeIcon:Class;
	[Embed("../../../../../../../resources/images/ICON_HEADER_INTERNET.png")] private var downloadModeIcon:Class;

	
	private function customCheckAddListener():void{
		customCheck.addEventListener(MouseEvent.CLICK, selectedAll);
	}
	
	private function listImeis(event:ChangePageEvent):void{
		remoteListImeis.showBusyCursor = true;
		remoteListImeis.listImeisByUser(SessionClass.getInstance().
				loggedUser.username, event.page, event.pageSize, 
				event.filterText, event.filterFields,
				event.sortField, event.sortDescending, false, true);
		SessionTimer.getInstance().resetTimer();
	}

	// Set selected surveys in main list	
	public function setListSurvey(listSurveys:ArrayCollection):void{
		selectedSurveyList = listSurveys;
	}
	
	private function sendSurveys(event:MouseEvent):void{
		var size:int;
		var surveysIdToSend:Array = new Array();
        var surveysTitlesToSend:Array = new Array();
		var msisdnToSend:Array = new Array();
		var imeisToSend:Array = new Array();
		var imeiToSend:String;
		var surveyId:String;
        var surveyTitle:String;
		var msisdn:String;
		var imei:String;
		var i:int;

		// Add all selected surveys on list
		size = selectedSurveyList.length;
		for (i = 0; i<size; i++){
			surveyId = (selectedSurveyList.getItemAt(i) as SurveyDTO).idSurvey;
            surveyTitle = (selectedSurveyList.getItemAt(i) as SurveyDTO).title;
			surveysIdToSend.push(surveyId);
            surveysTitlesToSend.push(surveyTitle);
		}
		
		// Add all phone nulmber of the selected imeis on list
		size = selectedImeisList.length;
		for (i = 0; i<size; i++){
			msisdn = (selectedImeisList.getItemAt(i) as ImeiDTO).msisdn;
			msisdnToSend.push(msisdn);
			
			imei = (selectedImeisList.getItemAt(i) as ImeiDTO).imei;
			imeisToSend.push(imei);
		}
		
		var remoteObject:RemoteObject = new RemoteObject(REMOTE_SERVICE);
		remoteObject.showBusyCursor = true;
		remoteObject.addEventListener(FaultEvent.FAULT, onFault);
		remoteObject.addEventListener(ResultEvent.RESULT, onSuccess);
		switch(transmissionMode){
    		case MODE_CABLE:
    			imeiToSend = (selectedImeisList.getItemAt(0) as ImeiDTO).imei;
        		remoteObject.sendSurveyCable(deviceCombo.selectedItem, SessionClass.getInstance().loggedUser.username, surveysIdToSend, imeiToSend);
        		break;
    		case MODE_SMS:
    			remoteObject.sendSurveySMS(SessionClass.getInstance().loggedUser.username, surveysIdToSend, msisdnToSend);
        		break;
    		case MODE_GPRS:
        		remoteObject.sendSurveyGPRS(SessionClass.getInstance().loggedUser.username, surveysIdToSend, imeisToSend, surveysTitlesToSend);
        		break;
		}
		SessionTimer.getInstance().resetTimer();
		
		function onSuccess(event:ResultEvent):void {
			goToSuccessScreen();
		}

		function onFault(event:FaultEvent):void	{
			Alert.show(ExceptionUtil.getMessage(event.fault.faultString),
					ConfigI18n.getInstance().getString("lblError"));
			goToFailScreen();
		}

	}
	
	private function selectedAll(event:MouseEvent):void{
		pagination.selectAll(customCheck.selected);
	}	
	
	private function cancelSendSurvey(event:MouseEvent):void{
		PopUpManager.removePopUp(this);
	}

	private function modeSelected(event:MouseEvent):void{
		// set selected mode
		transmissionMode = event.target.id;
		if (transmissionMode == MODE_CABLE){
			modeIcon.source = cableModeIcon;
			modeText.text = ConfigI18n.getInstance().getString('lblCableMode');
			viewStackSendSurvey.selectedIndex = 1;
		} else if (transmissionMode == MODE_SMS){
			modeIcon.source = smsModeIcon;
			modeText.text = ConfigI18n.getInstance().getString('lblSMSMode');
			viewStackSendSurvey.selectedIndex = 2;
		} else if (transmissionMode == MODE_GPRS){
			modeIcon.source = gprsModeIcon;
			modeText.text = ConfigI18n.getInstance().getString('lblInternetMode');
			viewStackSendSurvey.selectedIndex = 2;
		} else if (transmissionMode == MODE_DOWNLOAD){
			modeIcon.source = downloadModeIcon;
			modeText.text = ConfigI18n.getInstance().getString('lblDownloadMode');
			downloadSurvey();
		}
		modeIcon.visible = true;
		modeText.visible = true;
	}
	
	private function loadImeiList():void{
		pagination.refresh();
	}
	
	private function deviceSelected(event:MouseEvent):void{
		if (deviceCombo.selectedIndex == 0) {
			Alert.show(ConfigI18n.getInstance().getString('lblSendSurveySelectWarn'),
					ConfigI18n.getInstance().getString("lblWarning"));
		} else if (imeiDTO != null) {
			selectedImeisList = new ArrayCollection();
			selectedImeisList.addItem(imeiDTO);
			goToConfirmationScreen();
		} else {
			Alert.show(ConfigI18n.getInstance().getString('driveIsNotMobile'),
					ConfigI18n.getInstance().getString("lblWarning"));
		}
	}

	private function imeiSelected(event:MouseEvent):void{
		selectedImeisList = pagination.getSelectedAllItems();
		selectedImeisList.refresh();
		if (selectedImeisList.length > 0) {
			if ((transmissionMode == MODE_CABLE) && (selectedImeisList.length > 1)) {
				Alert.show(ConfigI18n.getInstance().getString('lblChosenOnlyOneImei'),
					ConfigI18n.getInstance().getString("lblWarning"));
			} else {
				nextStep(event);	
			}
		} else {
			Alert.show(ConfigI18n.getInstance().getString('lblChosenImeis'),
					ConfigI18n.getInstance().getString("lblWarning"));
		}
	}
	
	private function checkImei():void{
		if (deviceCombo.selectedIndex == 0) {
			deviceImeiText.text = "";
			btnDeviceSelect.enabled = false;
			return;
		}
		
		var remoteObject:RemoteObject = new RemoteObject(REMOTE_SERVICE);
		remoteObject.showBusyCursor = true;	
		remoteObject.addEventListener(FaultEvent.FAULT, onFault);
		remoteObject.addEventListener(ResultEvent.RESULT, onSuccess);
		remoteObject.getImeiFromDrive(deviceCombo.text);
		SessionTimer.getInstance().resetTimer();
		
		function onSuccess(event:ResultEvent):void {
			if (event != null) {
				btnDeviceSelect.enabled = true;
				imeiDTO = event.result as ImeiDTO;
				deviceImeiText.text = ConfigI18n.getInstance().getString('lblSendSurveyImeiDevice') + ' ' + imeiDTO.imei;
			}
		}
		function onFault(event:FaultEvent):void	{
			deviceImeiText.text = ConfigI18n.getInstance().getString('driveIsNotMobile');
			btnDeviceSelect.enabled = false;
		}
	}	

	private function showComboDevices():void{
		if (transmissionMode == MODE_CABLE) {
			deviceCombo.setFocus();			
			loadDevices();
		}
	}
	
	private function loadDevices():void {
		btnDeviceSelect.enabled = false;
		deviceList = new Array();
		deviceList.push(ConfigI18n.getInstance().getString("selectDrive"));
		deviceCombo.selectedIndex = 0;

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
				deviceList.push(array[i]);
		}
		
		function onFault(event:FaultEvent):void	{
			Alert.show(ConfigI18n.getInstance().getString('lblCantLoadDrives'),
					   ConfigI18n.getInstance().getString("lblError"));		
		}
	}
	
	private function enableImeiGrid():void{
		if (deviceCombo.selectedIndex > 0) {
			imeiGrid.enabled = true;
		} else {
			imeiGrid.enabled = false;
		}
	}
	
	private function firstStep():void{
		viewStackSendSurvey.selectedIndex = 0;
	}	
	
	private function goToSuccessScreen():void{
		viewStackSendSurvey.selectedIndex = 4;
	}
	
	private function goToFailScreen():void{
		viewStackSendSurvey.selectedIndex = 5;
	}
	
	private function goToDownloadScreen():void{
		viewStackSendSurvey.selectedIndex = 6;
	}

	private function goToConfirmationScreen():void{
		viewStackSendSurvey.selectedIndex = 3;
	}
	
	private function backFailScreen(event:MouseEvent):void{
		viewStackSendSurvey.selectedIndex = 2;
	}

	private function nextStep(event:MouseEvent):void{
		viewStackSendSurvey.selectedIndex = viewStackSendSurvey.selectedIndex + 1;
	}

	private function backStep(event:MouseEvent):void{
		viewStackSendSurvey.selectedIndex = viewStackSendSurvey.selectedIndex - 1;
	}
	private function backToSelectImei(event:MouseEvent):void{
		if (transmissionMode == MODE_CABLE){
			viewStackSendSurvey.selectedIndex = 1;
		} else{
			viewStackSendSurvey.selectedIndex = 2;
		}		
	}
	
	private function isEditable(event:ListEvent):void{
		if (customCheck.editable) {
			pagination.itemClick(imeiGrid.selectedItem);
		}
	}
	
	private function updateSteps():void{
		if (viewStackSendSurvey.selectedIndex == 0){
			modeIcon.visible = false;
			modeText.visible = false;
			stp1.styleName = "wizardProgress";
			stp2.styleName = "wizardBlank";
			stp3.styleName = "wizardBlank";
			stp4.styleName = "wizardBlank";
			stepText = 1;
		} else if(viewStackSendSurvey.selectedIndex == 1){
			stp1.styleName = "wizardProgress";
			stp2.styleName = "wizardProgress";
			stp3.styleName = "wizardBlank";
			stp4.styleName = "wizardBlank";
			stepText = 2;			
		} else if(viewStackSendSurvey.selectedIndex == 2){
			stp1.styleName = "wizardProgress";
			stp2.styleName = "wizardProgress";
			stp3.styleName = "wizardBlank";
			stp4.styleName = "wizardBlank";
			stepText = 2;
		} else if(viewStackSendSurvey.selectedIndex == 3){
			stp1.styleName = "wizardProgress";
			stp2.styleName = "wizardProgress";
			stp3.styleName = "wizardProgress";
			stp4.styleName = "wizardBlank";
			stepText = 3;
		} else if(viewStackSendSurvey.selectedIndex == 4){
			stp1.styleName = "wizardProgress";
			stp2.styleName = "wizardProgress";
			stp3.styleName = "wizardProgress";
			stp4.styleName = "wizardProgress";
			stepText = 4;
		} else if(viewStackSendSurvey.selectedIndex == 5){
			stp1.styleName = "wizardProgress";
			stp2.styleName = "wizardProgress";
			stp3.styleName = "wizardProgress";
			stp4.styleName = "wizardProgress";
			stepText = 4;
		}
	}
	
	private function loadSearch():void{
		searchOptionsLabels = new ArrayCollection();
		searchOptionsLabels.addItem(ConfigI18n.getInstance().getString("comboSearchAll"));
		searchOptionsLabels.addItem(ConfigI18n.getInstance().getString("colSendSurveyImei"));
		searchOptionsLabels.addItem(ConfigI18n.getInstance().getString("colSendSurveyPhone"));
		searchOptionsFields = new ArrayCollection();
		searchOptionsFields.addItem(new Array("imei", "msisdn"));
		searchOptionsFields.addItem(new Array("imei"));
		searchOptionsFields.addItem(new Array("msisdn"));
	}

	private function downloadSurvey():void{
		var remoteObject:RemoteObject = new RemoteObject(REMOTE_SERVICE);
		var i:int;
		var surveyId:String;
		var size:int;
		var surveysIdToDownload:Array = new Array();

		size = selectedSurveyList.length;

		for (i = 0; i<size; i++) {
			surveyId = (selectedSurveyList.getItemAt(i) as SurveyDTO).idSurvey;
			surveysIdToDownload.push(surveyId);
		}

		remoteObject.showBusyCursor = true;
		remoteObject.addEventListener(FaultEvent.FAULT, onFault);
		remoteObject.addEventListener(ResultEvent.RESULT, onSuccess);
		remoteObject.downloadSurvey(SessionClass.getInstance().loggedUser.username,
				surveysIdToDownload);
		SessionTimer.getInstance().resetTimer();

		function onSuccess(event:ResultEvent):void {
			if (event.result != null) {
				fileName = "survey.zip";
				strFileContent = event.result as String;
				goToDownloadScreen();
			}
		}

		function onFault(event:FaultEvent):void	{
			Alert.show(ExceptionUtil.getMessage(event.fault.faultString),
					ConfigI18n.getInstance().getString("lblError"));
			goToFailScreen();
		}
	}

	private function close():void{
		PopUpManager.removePopUp(this);
	}

	private function downloadPressed():void
	{
		var de:Base64Decoder = new Base64Decoder();
		de.decode(strFileContent);
		var byteArr:ByteArray = de.toByteArray();
		var fr:FileReference = new FileReference();
		fr.save(byteArr, fileName);
	}
