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

	import flash.display.DisplayObject;
	
	import main.br.org.indt.ndg.controller.access.SessionClass;
	import main.br.org.indt.ndg.controller.access.SessionTimer;
	import main.br.org.indt.ndg.i18n.ConfigI18n;
	import main.br.org.indt.ndg.model.SurveyDTO;
	import main.br.org.indt.ndg.ui.component.powerdatagrid.ChangePageEvent;
	import main.br.org.indt.ndg.ui.view.main.sendSurvey.SendSurvey;
	import main.br.org.indt.ndg.ui.view.main.wizard.Wizard;
	
	import mx.collections.ArrayCollection;
	import mx.containers.ViewStack;
	import mx.controls.Alert;
	import mx.managers.PopUpManager;
	import mx.rpc.remoting.mxml.RemoteObject;
				
    [Bindable] public var surveyList:ArrayCollection = new ArrayCollection();
	[Bindable] public var remoteListSurveys:RemoteObject = new RemoteObject(REMOTE_SERVICE);
	[Bindable] private var searchOptionsLabels:ArrayCollection = null;
    [Bindable] private var searchOptionsFields:ArrayCollection = null;
    public var myStack:ViewStack = null;

	private static const REMOTE_SERVICE:String = "myService";
	private static const SURVEY_PAGE_SIZE:int = 15;
	
	
	private function init():void{
		searchOptionsLabels = new ArrayCollection();
		searchOptionsLabels.addItem(ConfigI18n.getInstance().getString("comboSearchAll"));
		searchOptionsLabels.addItem(ConfigI18n.getInstance().getString("colSurveyId"));
		
		searchOptionsFields = new ArrayCollection();
		searchOptionsFields.addItem(new Array("idSurvey"));
		searchOptionsFields.addItem(new Array("idSurvey"));	
		
		pagination.refresh();
		customCheck.addEventListener(MouseEvent.CLICK, selectedAll);	
		var session:SessionClass = SessionClass.getInstance();
		if (session.isHostedMode && session.loggedUser.firstTimeUse.toUpperCase() == 'Y' && session.isAdmin()) {
			showWizard(null);
		}	
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

	private function showSendSurveys(event:MouseEvent):void{
		var sizeSelectedItems:int = pagination.getSelectedAllItems().length; 
		if(sizeSelectedItems > 0) {
			var sendSurvey:SendSurvey;
			sendSurvey = new SendSurvey();
			sendSurvey.setListSurvey(pagination.getSelectedAllItems());
			PopUpManager.addPopUp(sendSurvey, this.parentDocument as DisplayObject,true);
			PopUpManager.centerPopUp(sendSurvey);
		} else {
			Alert.show(ConfigI18n.getInstance().getString("selectOneMoreSurveys"),
					   ConfigI18n.getInstance().getString("lblError"));
		}
	}
	
	private function showSurveyDetails(event:MouseEvent):void{
		myStack.selectedIndex = 2;
	}
	
	private function showResults(event:Event):void{
		//if (getSelectedSurvey().results <= 0){
		//	Alert.show(ConfigI18n.getInstance().getString("surveyListNoResults"),
		//		ConfigI18n.getInstance().getString("lblWarning"));
		//} else{
			myStack.selectedIndex = 1;
		//}
	}
	
	private function listSurveys(event:ChangePageEvent):void{
		remoteListSurveys.showBusyCursor = true;
		remoteListSurveys.listSurveys(SessionClass.getInstance().
				loggedUser.username, event.page, event.pageSize, 
				event.filterText, event.filterFields,
				event.sortField, event.sortDescending, true);
		SessionTimer.getInstance().resetTimer();
	}
	
	public function getSelectedSurvey():SurveyDTO{
		return pagination.getSelectedAllItems().getItemAt(0) as SurveyDTO;
	}

	private function showWizard(event:MouseEvent):void{
		var wizard:Wizard = new Wizard();
		PopUpManager.addPopUp(wizard, this.parentApplication as DisplayObject, true);
		PopUpManager.centerPopUp(wizard);		
	}
	
