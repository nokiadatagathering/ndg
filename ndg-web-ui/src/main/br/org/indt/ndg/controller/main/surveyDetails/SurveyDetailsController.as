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


	import main.br.org.indt.ndg.controller.access.SessionTimer;
	import main.br.org.indt.ndg.i18n.ConfigI18n;
	import main.br.org.indt.ndg.model.SurveyDTO;
	import main.br.org.indt.ndg.ui.component.powerdatagrid.ChangePageEvent;
	import main.br.org.indt.ndg.ui.view.main.surveyDetails.surveyDetailsExport.SurveyDetailsExport;
	
	import mx.collections.ArrayCollection;
	import mx.containers.ViewStack;
	import mx.controls.Alert;
	import mx.managers.PopUpManager;
	import mx.rpc.remoting.mxml.RemoteObject;
	
    [Bindable] public var imeiList:ArrayCollection = new ArrayCollection();				
	[Bindable] public var remoteListImeis:RemoteObject = new RemoteObject(REMOTE_SERVICE);
	[Bindable] private var searchOptionsLabels:ArrayCollection = null;
    [Bindable] private var searchOptionsFields:ArrayCollection = null;
    public var myStack:ViewStack = null;

	private var selectedSurveyDTO:SurveyDTO = null;
	private static const REMOTE_SERVICE:String = "myService";
	private static const IMEI_PAGE_SIZE:int = 15;
	
	private function init():void{
		searchOptionsLabels = new ArrayCollection();
		searchOptionsLabels.addItem(ConfigI18n.getInstance().getString("comboSearchAll"));
		searchOptionsLabels.addItem(ConfigI18n.getInstance().getString("colSendSurveyImei"));
		
		searchOptionsFields = new ArrayCollection();
		searchOptionsFields.addItem(new Array("imei"));
		searchOptionsFields.addItem(new Array("imei"));	
		
		customCheck.addEventListener(MouseEvent.CLICK, selectedAll);		
	}
	
	private function selectedAll(event:MouseEvent):void{
		pagination.selectAll(customCheck.selected);
	}
	
	private function loadImeiList():void{
		pagination.refresh();
	}

	private function listImeis(event:ChangePageEvent):void{
		remoteListImeis.showBusyCursor = true;
		remoteListImeis.listImeisBySurvey(selectedSurveyDTO.idSurvey, event.page, event.pageSize, 
				event.filterText, event.filterFields,
				event.sortField, event.sortDescending);
		SessionTimer.getInstance().resetTimer();
	}
	
	public function listResultsFirstPage(survey:SurveyDTO):void{
		selectedSurveyDTO = survey;
		resetView(selectedSurveyDTO);
		pagination.refresh();
	}
	
	private function resetView(survey:SurveyDTO):void{
		surveyTitle.text = survey.title;
		search.clearUI();
		pagination.reset();
		imeiList.source = new Array();
	}	

	private function export():void{
		if (imeiList.length > 0){
			var surveyDetailsExport:SurveyDetailsExport = new SurveyDetailsExport();
			surveyDetailsExport.setSelectedSurveyDTO(selectedSurveyDTO);
			PopUpManager.addPopUp(surveyDetailsExport, this, true);
			PopUpManager.centerPopUp(surveyDetailsExport);
		} else {
			Alert.show(ConfigI18n.getInstance().getString("noImeisToExport"),
					ConfigI18n.getInstance().getString("lblWarning"));
		}
	}
	
	private function closeDetails(event:MouseEvent):void{
		PopUpManager.removePopUp(this);
	}	
	
	private function showSurveys(event:MouseEvent):void{
		myStack.selectedIndex = 0;
	}	
	
	private function detachImei():void{
		
//		var remoteObject:RemoteObject = new RemoteObject(REMOTE_SERVICE);
//		remoteObject.showBusyCursor = true;
//		remoteObject.addEventListener(FaultEvent.FAULT, onFault);
//		remoteObject.addEventListener(ResultEvent.RESULT, onSuccess);
//
//		remoteObject.detachImeiFromSurvey(selectedSurveyDTO.idSurvey, pagination.getSelectedAllItems());
//			
//		function onSuccess(event:ResultEvent):void {
//			if (event.result != null) {
//				Alert.show(ConfigI18n.getInstance().getString("lblDetachImeiSuccess"));
//			}
//		}
//				
//		function onFault(event:FaultEvent):void	{
//			Alert.show(ExceptionUtil.getMessage(event.fault.faultString),
//				ConfigI18n.getInstance().getString("lblError"));
//		
//		}
	}
	

	
