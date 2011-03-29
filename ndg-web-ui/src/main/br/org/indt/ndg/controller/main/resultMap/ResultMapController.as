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


	import flash.events.MouseEvent;
	import flash.external.*;
	
	import main.br.org.indt.ndg.controller.access.SessionClass;
	import main.br.org.indt.ndg.controller.access.SessionTimer;
	import main.br.org.indt.ndg.controller.util.ExceptionUtil;
	import main.br.org.indt.ndg.i18n.ConfigI18n;
	import main.br.org.indt.ndg.ui.component.map.ResultMappingPanel;
	import main.br.org.indt.ndg.ui.component.powerdatagrid.QueryInputOutputDTO;
	
	import mx.collections.ArrayCollection;
	import mx.controls.Alert;
	import mx.managers.PopUpManager;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	import mx.rpc.remoting.mxml.RemoteObject;
	
	[Bindable] public var selectedResultList:ArrayCollection = null;
	
	private static const REMOTE_SERVICE:String = "myService";	
	private var POIs:Array = new Array();
	private var survey:String;
	
	public function init():void {
		if (selectedResultList != null) {
			selectedList.width = 270;			
			selectedList.visible = true;	
			if (ExternalInterface.available) {
		   		ExternalInterface.call("setPOIs", POIs);
	       	}
	   		var resultMapping:ResultMappingPanel = new ResultMappingPanel();
	   		containerPanel.addChild(resultMapping);
		}
	}
	
	public function setSelectedResultList(list:ArrayCollection):void{
		selectedResultList = list;
		var array:Array = selectedResultList.toArray();
		
		for (var i:int=0; i<array.length; i++){
			if (array[i].lat != 0 || array[i].lon != 0 ) {
				POIs.push({lat: array[i].lat, lon: array[i].lon, title: array[i].title, date: array[i].date,
                                           user: array[i].user, imei: array[i].imei});
			}
		}
	}
	
	public function setSurvey(surveyId:String):void {
		survey = surveyId;
		
		var remoteListResults:RemoteObject = new RemoteObject(REMOTE_SERVICE);
		remoteListResults.showBusyCursor = true;
		remoteListResults.addEventListener(FaultEvent.FAULT, onFault);
		remoteListResults.addEventListener(ResultEvent.RESULT, onSuccess);
		remoteListResults.listResultsBySurvey(SessionClass.getInstance().loggedUser.username,
			survey, null, null,	null, null, null, false);
		SessionTimer.getInstance().resetTimer();
			
		function onSuccess(event:ResultEvent):void {
			if (event.result != null) {
				var result:QueryInputOutputDTO = event.result as QueryInputOutputDTO;
				var array:Array = result.queryResult.toArray();
				for (var i:int=0; i<array.length; i++){
					if (array[i].lat != 0 || array[i].lon != 0 ) {
						POIs.push({lat: array[i].lat, lon: array[i].lon, title: array[i].title, date: array[i].date,
                                                           user: array[i].user, imei: array[i].imei});		
					}
				}
				setPOIs();
			}
		}
				
		function onFault(event:FaultEvent):void	{
			Alert.show(ExceptionUtil.getMessage(event.fault.faultString),
					ConfigI18n.getInstance().getString("lblError"));
		}		
				
	}
	
	public function closeMap(envet:MouseEvent):void {
		PopUpManager.removePopUp(this);
	}
	
	public function setPOIs():void {
		
		if (ExternalInterface.available) {
	   		ExternalInterface.call("setPOIs", POIs);
       	}
       		
   		var resultMapping:ResultMappingPanel = new ResultMappingPanel();
   		containerPanel.addChild(resultMapping);
  	}
