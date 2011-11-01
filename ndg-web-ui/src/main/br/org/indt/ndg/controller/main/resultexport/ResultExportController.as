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
	import main.br.org.indt.ndg.controller.util.ExceptionUtil;
	import main.br.org.indt.ndg.i18n.ConfigI18n;
	import main.br.org.indt.ndg.model.SurveyDTO;
        import main.br.org.indt.ndg.model.ResultDTO;

        import mx.collections.ArrayCollection;
        import mx.controls.Alert;
        import mx.managers.PopUpManager;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	import mx.rpc.remoting.mxml.RemoteObject;
	import mx.utils.Base64Decoder;
	import flash.net.FileReference;


	private static const REMOTE_SERVICE:String = "myService";
	private static var FILENAME:String = null;
	private static const CSV:String = ".CSV";
	private static const XLS:String = ".XLS";
	private static const ZIP:String = ".ZIP";

	private var exportedFileName:String = null;
	[Bindable]private var failStr:String = null;
	[Bindable]private var successStr:String = null;
	[Bindable]private var waitStr:String = null;
	public var surveyDTO:SurveyDTO = null;
	private var strFileContent:String = null;
	[Bindable] private var stepText:int = 1;

	private var surveyHasImages:Boolean = false;
	private var exportFormat:String;
        public var selectedResultList:ArrayCollection = null;
        public var resultIdsToSend:Array = null;
        private var isSelected:Boolean = false;

	[Embed("../../../../../../../resources/images/ICON_HEADER_EXPORT_CSV.png")] private var csvIcon:Class;
	[Embed("../../../../../../../resources/images/ICON_HEADER_EXPORT_XLS.png")] private var xlsIcon:Class;

	private function init():void{
		FILENAME = ConfigI18n.getInstance().getString("resultFileName");
	}

	private function close():void{
		PopUpManager.removePopUp(this);
	}

	private function export(format:String, exportWithImages:Boolean, isSelected:Boolean):void{
		if (format == CSV){
			modeIcon.source = csvIcon;
		} else if (format == XLS){
			modeIcon.source = xlsIcon;
		}

		modeIcon.visible = true;

		exportedFileName = FILENAME + surveyDTO.idSurvey + format.toLowerCase();
		waitStr = ConfigI18n.getInstance().getString("exportWaitMessage01") + exportedFileName +
				ConfigI18n.getInstance().getString("exportWaitMessage02");

		viewStack.selectedIndex = 2;
		var remoteObject:RemoteObject = new RemoteObject(REMOTE_SERVICE);
		remoteObject.showBusyCursor = true;
		remoteObject.addEventListener(FaultEvent.FAULT, onFault);
		remoteObject.addEventListener(ResultEvent.RESULT, onSuccess);
                remoteObject.exportResults(SessionClass.getInstance().loggedUser.username, format,
                                           surveyDTO.idSurvey, exportWithImages, resultIdsToSend, isSelected);
		SessionTimer.getInstance().resetTimer();

		function onSuccess(event:ResultEvent):void {
			if (event.result != null) {
				if (exportWithImages == true) {
					format = ZIP;
					exportedFileName = FILENAME + surveyDTO.idSurvey + format.toLowerCase();
				}

				strFileContent = event.result as String;
				successStr = ConfigI18n.getInstance().getString("exportSaveMessage01") + exportedFileName +
						ConfigI18n.getInstance().getString("exportSaveMessage02");
				viewStack.selectedIndex = 3;
			}
		}

		function onFault(event:FaultEvent):void	{
			viewStack.selectedIndex = 4;
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

	private function updateSteps():void{
		if (viewStack.selectedIndex == 0){ //Format Type
			modeIcon.visible = false;
			stp1.styleName = "wizardProgress";
			stp2.styleName = "wizardBlank";
			stp3.styleName = "wizardBlank";
			if (surveyHasImages == true)
				stp4.styleName = "wizardBlank";

			stepText = 1;
		} else if(viewStack.selectedIndex == 1){ //Accept exporting Images
			stp1.styleName = "wizardProgress";
			stp2.styleName = "wizardProgress";
			stp3.styleName = "wizardBlank";
			if (surveyHasImages == true)
				stp4.styleName = "wizardBlank";
			stepText = 2;			
		} else if(viewStack.selectedIndex == 2){ //Loading
			stp1.styleName = "wizardProgress";
			stp2.styleName = "wizardProgress";
			stp3.styleName = "wizardBlank";
			if (surveyHasImages == true) {
				stp3.styleName = "wizardProgress";
				stp4.styleName = "wizardBlank";
				stepText = 3;
			}
			else
				stepText = 2;
		} else if(viewStack.selectedIndex == 3){ //Success
			stp1.styleName = "wizardProgress";
			stp2.styleName = "wizardProgress";
			stp3.styleName = "wizardProgress";
			if (surveyHasImages == true) {
				stp4.styleName = "wizardProgress";
				stepText = 4;
			}
			else
				stepText = 3;
		} else if(viewStack.selectedIndex == 4){ //Fail
			stp1.styleName = "wizardProgress";
			stp2.styleName = "wizardProgress";
			stp3.styleName = "wizardProgress";
			if (surveyHasImages == true) {
				stp4.styleName = "wizardProgress";
				stepText = 4;
			}
			else
				stepText = 3;
		}
	}

	public function exportWithImages(format:String):void {
		if (surveyHasImages == false) {
			export(format, false, isSelected);
                        isSelected = false;
		}
		else {
			exportFormat = format;
			viewStack.selectedIndex = 1;
		}
	}

	public function renderasExportImages(surveyHasImages:Boolean):void {
		if (surveyHasImages == true) {
			stp4.visible = true;
			stp4.includeInLayout = true;
			maxSteps.text = "4";
			this.surveyHasImages = true;
		}
	}

        public function setSelectedResultList(list:ArrayCollection):void {
            selectedResultList = list;
            resultIdsToSend = new Array();
            var idResult:String;
            var i:int;
 
            isSelected = true;

            // Add selected result ids to the list
            for (i = 0; i < selectedResultList.length; i++){
                idResult = (selectedResultList.getItemAt(i) as ResultDTO).idResult;
                resultIdsToSend.push(idResult);
            }
        }
