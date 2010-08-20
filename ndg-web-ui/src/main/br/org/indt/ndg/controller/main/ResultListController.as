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
	import flash.events.Event;
	
	import main.br.org.indt.ndg.controller.access.SessionClass;
	import main.br.org.indt.ndg.controller.access.SessionTimer;
	import main.br.org.indt.ndg.controller.main.resultimport.CloseResultImportEvent;
	import main.br.org.indt.ndg.controller.util.ExceptionUtil;
	import main.br.org.indt.ndg.i18n.ConfigI18n;
	import main.br.org.indt.ndg.model.ResultDTO;
	import main.br.org.indt.ndg.model.SurveyDTO;
	import main.br.org.indt.ndg.ui.component.powerdatagrid.ChangePageEvent;
	import main.br.org.indt.ndg.ui.view.main.resultMap.ResultMap;
	import main.br.org.indt.ndg.ui.view.main.resultexport.ResultExport;
	import main.br.org.indt.ndg.ui.view.main.resultimport.ResultImport;
	
	import mx.collections.ArrayCollection;
	import mx.containers.ViewStack;
	import mx.controls.Alert;
	import mx.managers.PopUpManager;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	import mx.rpc.remoting.mxml.RemoteObject;
			
    [Bindable] public var resultList:ArrayCollection = new ArrayCollection();
    [Bindable] public var remoteListResults:RemoteObject = new RemoteObject(REMOTE_SERVICE);
    [Bindable] private var searchOptionsLabels:ArrayCollection = null;
    [Bindable] private var searchOptionsFields:ArrayCollection = null;
    public var myStack:ViewStack = null;
    
	private var selectedSurveyDTO:SurveyDTO = null;
	private static const REMOTE_SERVICE:String = "myService";	
	private static const RESULT_PAGE_SIZE:int = 13;


	private function init():void{
		searchOptionsLabels = new ArrayCollection();
		searchOptionsLabels.addItem(ConfigI18n.getInstance().getString("comboSearchAll"));
		searchOptionsLabels.addItem(ConfigI18n.getInstance().getString("colResultId"));
		searchOptionsLabels.addItem(ConfigI18n.getInstance().getString("colImei"));
		
		searchOptionsFields = new ArrayCollection();
		searchOptionsFields.addItem(new Array("idResult", "imei"));
		searchOptionsFields.addItem(new Array("idResult"));
		searchOptionsFields.addItem(new Array("imei"));
		
		customCheck.addEventListener(MouseEvent.CLICK, selectedAll);
	}


	public function listResultsFirstPage(survey:SurveyDTO):void{
		selectedSurveyDTO = survey;
		resetView(selectedSurveyDTO);
		pagination.refresh();
	}
		
	private function listResults(event:ChangePageEvent):void{
		preview.htmlText = "";
		resultList.source = new Array();
		remoteListResults.showBusyCursor = true;
		remoteListResults.listResultsBySurvey(SessionClass.getInstance().loggedUser.username,
				selectedSurveyDTO.idSurvey, event.page, event.pageSize,
				event.filterText, event.filterFields, event.sortField, event.sortDescending);
		SessionTimer.getInstance().resetTimer();
	}
			
	private function resetView(survey:SurveyDTO):void{
		surveyTitle.text = survey.title;
		preview.htmlText = "";
		search.clearUI();
		pagination.reset();
		resultList.source = new Array();
	}
			
	private function showSurveys(event:MouseEvent):void{
		myStack.selectedIndex = 0;
	}

	private function selectedAll(event:MouseEvent):void{
		pagination.selectAll(customCheck.selected);
	}
			
	private function markAll(event:MouseEvent):void{
		pagination.selectAll(true);
	}
			
	private function unmarkAll(event:MouseEvent):void{
		pagination.selectAll(false);
	}
	
	private function showPreview(event:Event):void{
		var result:ResultDTO = resultsGrid.selectedItem as ResultDTO;
		if (result != null){
			var remoteObject:RemoteObject = new RemoteObject(REMOTE_SERVICE);
			remoteObject.showBusyCursor = true;
			remoteObject.addEventListener(FaultEvent.FAULT, onFault);
			remoteObject.addEventListener(ResultEvent.RESULT, onSuccess);
			remoteObject.getPreview(SessionClass.getInstance().loggedUser.username, 
					selectedSurveyDTO.idSurvey, result.idResult);
			SessionTimer.getInstance().resetTimer();
		} else{
			preview.htmlText = "";
		}
				
		function onSuccess(event:ResultEvent):void {
			if (event.result != null) {
				preview.htmlText = event.result as String;
			}
		}
				
		function onFault(event:FaultEvent):void	{
			Alert.show(ExceptionUtil.getMessage(event.fault.faultString),
					ConfigI18n.getInstance().getString("lblError"));
		}		
	}
	
	private function export():void{
		if (resultList.length > 0){
			var resultExport:ResultExport = new ResultExport();
			resultExport.surveyDTO = selectedSurveyDTO;
			PopUpManager.addPopUp(resultExport, this, true);
			PopUpManager.centerPopUp(resultExport);
		} else {
			Alert.show(ConfigI18n.getInstance().getString("noResultsToExport"),
					ConfigI18n.getInstance().getString("lblWarning"));
		}
	}
	
	private function showImport():void {
		var resultImport:ResultImport = new ResultImport();
		resultImport.surveyDTO = selectedSurveyDTO;
		resultImport.addEventListener(CloseResultImportEvent.EVENT_NAME, closePopup);
		
		PopUpManager.addPopUp(resultImport, this, true);
		PopUpManager.centerPopUp(resultImport);
		
		function closePopup(event:CloseResultImportEvent):void{
			pagination.refresh();
		}
	}
	
	private function showMap():void {
		if (pagination.selectedAllItems.length > 0) {
			var mapPosition:ResultMap = new ResultMap();
			mapPosition.setSelectedResultList(pagination.selectedAllItems);
			PopUpManager.addPopUp(mapPosition, this.parentApplication as DisplayObject, true);
			PopUpManager.centerPopUp(mapPosition);
		} else {
			Alert.show(ConfigI18n.getInstance().getString("noResultsSelected"),
					ConfigI18n.getInstance().getString("lblWarning"));			
		}
	}
	
	private function showMapAll():void {
		var mapPosition:ResultMap = new ResultMap();
		mapPosition.setSurvey(selectedSurveyDTO.idSurvey);
		PopUpManager.addPopUp(mapPosition, this.parentApplication as DisplayObject , true);
		PopUpManager.centerPopUp(mapPosition);		
	}
