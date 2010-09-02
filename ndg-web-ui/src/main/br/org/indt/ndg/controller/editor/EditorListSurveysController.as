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
	import main.br.org.indt.ndg.controller.editor.PersistenceHelper;
	import main.br.org.indt.ndg.controller.util.ExceptionUtil;
	import main.br.org.indt.ndg.i18n.ConfigI18n;
	import main.br.org.indt.ndg.model.SurveyDTO;
	import main.br.org.indt.ndg.ui.component.powerdatagrid.ChangePageEvent;
	
	import mx.collections.ArrayCollection;
	import mx.containers.ViewStack;
	import mx.controls.Alert;
	import mx.events.CloseEvent;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	import mx.rpc.remoting.mxml.RemoteObject;
				
    [Bindable] public var surveyList:ArrayCollection = new ArrayCollection();
	[Bindable] public var remoteListSurveys:RemoteObject = new RemoteObject(REMOTE_SERVICE);
	[Bindable] private var searchOptionsLabels:ArrayCollection = null;
    [Bindable] private var searchOptionsFields:ArrayCollection = null;

	private static const REMOTE_SERVICE:String = "myService";
	private static const SURVEY_PAGE_SIZE:int = 15;
	
	public var myStack:ViewStack = null;
	public var isNewSurvey:Boolean = true;
	
	
	private function init():void{
		searchOptionsLabels = new ArrayCollection();
		searchOptionsLabels.addItem(ConfigI18n.getInstance().getString("comboSearchAll"));
		searchOptionsLabels.addItem(ConfigI18n.getInstance().getString("colSurveyId"));

		searchOptionsFields = new ArrayCollection();
		searchOptionsFields.addItem(new Array("idSurvey"));
		searchOptionsFields.addItem(new Array("idSurvey"));	
		
		pagination.refresh();
		
		customCheck.addEventListener(MouseEvent.CLICK, selectedAll);		
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
	
	private function showEditSurveys(event:Event):void{
		isNewSurvey = false;
		myStack.selectedIndex = 1;
	}
	
	public function getSelectedSurvey():SurveyDTO{
		var checkedSurvey:SurveyDTO = null;
		if (pagination.selectedAllItems.length == 1){
			checkedSurvey = pagination.getSelectedAllItems().getItemAt(0) as SurveyDTO;
		}
		return checkedSurvey;
	}
	
	private function getUploadedStatus(item:Object, column:DataGridColumn):String{
		if (item.isUploaded.toString().toUpperCase() == "Y"){
			return ConfigI18n.getInstance().getStringFile("editorResources", "YES");
		} else {
			return ConfigI18n.getInstance().getStringFile("editorResources", "NO");
		}
	}
	
	private function showNewSurvey(event:Event):void {
		isNewSurvey = true;
		myStack.selectedIndex = 1;
	}
	
	private function listSurveys(event:ChangePageEvent):void {
		remoteListSurveys.showBusyCursor = true;
		remoteListSurveys.listSurveys(SessionClass.getInstance().loggedUser.username, event.page, event.pageSize, 
			event.filterText, event.filterFields, event.sortField, event.sortDescending, false);
		SessionTimer.getInstance().resetTimer();
	}
	
	private function deleteSurvey():void{
		var dto:SurveyDTO = getSelectedSurvey();
		if (dto.isUploaded != null && dto.isUploaded.toUpperCase() == "N"){
			Alert.yesLabel = ConfigI18n.getInstance().getString("btnYes");
			Alert.noLabel = ConfigI18n.getInstance().getString("btnNo");
			Alert.show(ConfigI18n.getInstance().getStringFile("editorResources", "deleteSurveyMessage"),
					ConfigI18n.getInstance().getString("lblQuestion"),
					(Alert.YES | Alert.NO), null, removeImei, null, Alert.YES);
		}
		
		function removeImei(event:CloseEvent):void{
			if (event.detail == Alert.YES){
				var remoteObject:RemoteObject = new RemoteObject(REMOTE_SERVICE);
				remoteObject.showBusyCursor = true;
				remoteObject.addEventListener(FaultEvent.FAULT, onFault);			
				remoteObject.addEventListener(ResultEvent.RESULT, onSuccess);
				remoteObject.deleteSurveyFromServer(dto.idSurvey);
				SessionTimer.getInstance().resetTimer();
			}
		}
		
		function onFault(event:FaultEvent):void{
			Alert.show(ExceptionUtil.getMessage(event.fault.faultString),
					ConfigI18n.getInstance().getString("lblError"));
		}
		
		function onSuccess(event:ResultEvent):void{
			pagination.refresh();
		}
	}
	

	private function makeAvailableSurvey():void{
		var dto:SurveyDTO = getSelectedSurvey();
		if (dto.isUploaded != null && dto.isUploaded.toUpperCase() == "N"){
			Alert.yesLabel = ConfigI18n.getInstance().getString("btnYes");
			Alert.noLabel = ConfigI18n.getInstance().getString("btnNo");
			Alert.show(ConfigI18n.getInstance().getStringFile("editorResources", "MAKE_AVAILABLE_CONFIRMATION"),
					ConfigI18n.getInstance().getString("lblQuestion"),
					(Alert.YES | Alert.NO), null, doIt, null, Alert.YES);
		}
		
		function doIt(event:CloseEvent):void{
			if (event.detail == Alert.YES){
				PersistenceHelper.getInstance().makeAvailableForSurveysList(dto.survey, onSuccess);
			}
		}
		
		function onSuccess():void{
			pagination.refresh();
		}
	}
	
	
	