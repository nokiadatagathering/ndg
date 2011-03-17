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
	import main.br.org.indt.ndg.model.SurveyDTO;
	
	import mx.collections.ArrayCollection;
	import mx.managers.PopUpManager;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	import mx.rpc.remoting.mxml.RemoteObject;
	import mx.utils.Base64Decoder;
			
			
	private static const REMOTE_SERVICE:String = "myService";
	private static var FILENAME:String = null;
    //private static const CSV:String = ".csv";
	private static const XLS:String = ".XLS";
			
	private var exportedFileName:String = null;
	[Bindable]private var failStr:String = null;
	[Bindable]private var successStr:String = null;
	[Bindable]private var waitStr:String = null;
	private var surveyDTO:SurveyDTO = null;
	private var strFileContent:String = null;
	[Bindable] private var stepText:int = 1;
	public var header:ArrayCollection = new ArrayCollection();
			
			
			
	private function init():void{
		FILENAME = ConfigI18n.getInstance().getString("devicesFileName");
	}
			
	private function close():void{
		PopUpManager.removePopUp(this);
	}
			
			
	private function export(format:String):void{
		//if (format == CSV){
		//	modeIcon.source = "main/resources/images/ICON_HEADER_EXPORT_CSV.png";
		//} else
		if (format == XLS){
			modeIcon.source = "main/resources/images/ICON_HEADER_EXPORT_XLS.png";
		}
		modeIcon.visible = true;		
		
		
		exportedFileName = FILENAME + surveyDTO.idSurvey + format.toLowerCase();
		waitStr = ConfigI18n.getInstance().getString("exportWaitMessage01") + exportedFileName +
				ConfigI18n.getInstance().getString("exportWaitMessage02");
				
		header.addItem(ConfigI18n.getInstance().getString("colDetailImeiId"));
		header.addItem(ConfigI18n.getInstance().getString("colDetailPhoneNumber"));
		header.addItem(ConfigI18n.getInstance().getString("colDetailUser"));
		header.addItem(ConfigI18n.getInstance().getString("colDetailDevice"));

		viewStack.selectedIndex = 1;
		var remoteObject:RemoteObject = new RemoteObject(REMOTE_SERVICE);
		remoteObject.showBusyCursor = true;
		remoteObject.addEventListener(FaultEvent.FAULT, onFault);
		remoteObject.addEventListener(ResultEvent.RESULT, onSuccess);
		remoteObject.exportSurveyDevices(surveyDTO.idSurvey, header, format);
		SessionTimer.getInstance().resetTimer();
				
		function onSuccess(event:ResultEvent):void {
			if (event.result != null) {
				strFileContent = event.result as String;
				successStr = ConfigI18n.getInstance().getString("exportSaveMessage01") + exportedFileName +
						ConfigI18n.getInstance().getString("exportSaveMessage02");
				viewStack.selectedIndex = 2;
			}
		}
				
		function onFault(event:FaultEvent):void	{
			viewStack.selectedIndex = 3;
			failStr = ExceptionUtil.getMessage(event.fault.faultString);
		}
	}
			
	private function download():void{
		var de:Base64Decoder = new Base64Decoder();
		de.decode(strFileContent);
		var byteArr:ByteArray = de.toByteArray();
		var fr:FileReference = new FileReference();
		fr.save(byteArr, exportedFileName);
	}
	
	public function setSelectedSurveyDTO(survey:SurveyDTO):void{
		surveyDTO = survey;
	}
	
	private function updateSteps():void{
		if (viewStack.selectedIndex == 0){
			modeIcon.visible = false;
			stp1.styleName = "wizardProgress";
			stp2.styleName = "wizardBlank";
			stp3.styleName = "wizardBlank";
			stepText = 1;
		} else if(viewStack.selectedIndex == 1){
			stp1.styleName = "wizardProgress";
			stp2.styleName = "wizardProgress";
			stp3.styleName = "wizardBlank";
			stepText = 2;			
		} else if(viewStack.selectedIndex == 2){
			stp1.styleName = "wizardProgress";
			stp2.styleName = "wizardProgress";
			stp3.styleName = "wizardProgress";
			stepText = 3;
		} else if(viewStack.selectedIndex == 3){
			stp1.styleName = "wizardProgress";
			stp2.styleName = "wizardProgress";
			stp3.styleName = "wizardProgress";
			stepText = 3;
		}
	}
	