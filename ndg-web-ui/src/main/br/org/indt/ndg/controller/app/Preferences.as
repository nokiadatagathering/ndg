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

import flash.events.Event;

import main.br.org.indt.ndg.controller.util.ExceptionUtil;
import main.br.org.indt.ndg.i18n.ConfigI18n;
import main.br.org.indt.ndg.model.PreferenceDTO;

import mx.collections.ArrayCollection;
import mx.collections.Sort;
import mx.collections.SortField;
import mx.controls.Alert;
import mx.managers.PopUpManager;
import mx.rpc.events.FaultEvent;
import mx.rpc.events.ResultEvent;
import mx.rpc.remoting.mxml.RemoteObject;
import mx.utils.ObjectUtil;

	private static const REMOTE_SERVICE:String = "myService";

	[Bindable]public var arrayListPreferences:ArrayCollection = new ArrayCollection();
	[Bindable]public var arrayListPreferencesBackUp:ArrayCollection = new ArrayCollection();

	private function init():void {
		remoteGetPreferences();
	}
	
	private function remoteGetPreferences():void
	{
		var preferencesList:PreferenceDTO;
		
		var remoteObject:RemoteObject = new RemoteObject(REMOTE_SERVICE);
		remoteObject.addEventListener(FaultEvent.FAULT, onFault);
		remoteObject.addEventListener(ResultEvent.RESULT, onSuccess);
		remoteObject.listPreferences();
			
		function onSuccess(event:ResultEvent):void {
			if (event.result != null) {
				var valueSortField:SortField =  new SortField();
				valueSortField.name = "preference";

				var dataSort:Sort = new Sort();
				dataSort.fields = [valueSortField];

				arrayListPreferences = event.result as ArrayCollection;
				arrayListPreferences.sort = dataSort;
				arrayListPreferences.refresh();
				
				arrayListPreferencesBackUp = new ArrayCollection(ObjectUtil.copy(arrayListPreferences.source) as Array);
			}
		}
			
		function onFault(event:FaultEvent):void	{
			Alert.show(ExceptionUtil.getMessage(event.fault.faultString),
					   ConfigI18n.getInstance().getString("lblPrefFail"));
		}
		
	}
	
	private function close(event:Event):void {
		PopUpManager.removePopUp(this);
	}

	private function savePreferences(event:Event):void {
		
		/*  Validation with validators
		var errors:Array = Validator.validateAll(validators);
		
		if (errors.length == 0){
			remoteSavePreferences();
		} else{
			Alert.show("validate as paradas");
		}*/
		
		
		
		/* Hard-coded validation */
		var errors:Array = new Array();		
		var field:Array = new Array(17);
		var i:int = 0;
	
		//Getting the values
		for (i = 0; i < field.length; i++) {
			field[i] = preferencesGrid.dataProvider[i].value;
		}

		//Checking values
		for (i = 0; i < field.length; i++) {
			if (preferencesGrid.dataProvider[i].preference == "javamail.debug" ||
			    preferencesGrid.dataProvider[i].preference == "mail.smtps.auth" ||
			    preferencesGrid.dataProvider[i].preference == "proxy.set" ||
			    preferencesGrid.dataProvider[i].preference == "smtp.mail.starttls.enable")
			{
				if (field[i].search(new RegExp("(^true(?!.))|(^false(?!.))"))) {
					errors.push(preferencesGrid.dataProvider[i].preference + " - " + ConfigI18n.getInstance().getString("lblPrefErrorBoolean") + "\n");
					continue;
				}
			}
			if (preferencesGrid.dataProvider[i].preference == "proxy.host") {
				if (field[i].search(new RegExp("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$"))) {
					errors.push(preferencesGrid.dataProvider[i].preference + " - " + ConfigI18n.getInstance().getString("lblPrefErrorIP") + "\n");
					continue;
				}
			}
			
			if (preferencesGrid.dataProvider[i].preference == "smtp.mail.user") {
				if (field[i].search(new RegExp("^[a-z0-9_\-]+(\.[_a-z0-9\-]+)*@([_a-z0-9\-]+\.)+([a-z]{2}|aero|arpa|biz|com|coop|edu|gov|info|int|jobs|mil|museum|name|nato|net|org|pro|travel)$"))) {
					errors.push(preferencesGrid.dataProvider[i].preference + " - " + ConfigI18n.getInstance().getString("lblPrefErrorEMail") + "\n");
					continue;
				}
			}
			if (field[i].search(new RegExp("[^]"))) {
				errors.push(preferencesGrid.dataProvider[i].preference + " - " + ConfigI18n.getInstance().getString("lblPrefErrorNull") + "\n");
			}
	
						
		}

		if (errors.length != 0) {
		var errorsMsg:String = ConfigI18n.getInstance().getString("lblPrefCheckFieldsAbove") + " \n\n";
			for (i = 0; i < errors.length; i++) {
				errorsMsg += errors[i];
			}
			errorsMsg += "\n";
			Alert.show(errorsMsg, ConfigI18n.getInstance().getString("lblPrefFail"));
		}
		else {
			remoteSavePreferences();
		}
		
	}
	
	private function remoteSavePreferences():void {
		var remoteObject:RemoteObject = new RemoteObject(REMOTE_SERVICE);
		remoteObject.addEventListener(FaultEvent.FAULT, onFault);
		remoteObject.addEventListener(ResultEvent.RESULT, onSuccess);
		remoteObject.editPreference(arrayListPreferences);
			
		function onSuccess(event:ResultEvent):void {
			Alert.show(ConfigI18n.getInstance().getString("lblEditPrefSuccess"),
					   ConfigI18n.getInstance().getString("lblPrefSuccess"));
		}
			
		function onFault(event:FaultEvent):void	{
			Alert.show(ExceptionUtil.getMessage(event.fault.faultString),
					   ConfigI18n.getInstance().getString("lblPrefFail"));
		}
	}
	
	private function getMaxLengthMsg(length:String):String{
		return resourceManager.getString(ConfigI18n.LOCALE_FILE, 'tooLongMsg') + length;
	}
	
	private function resetPreferences(event:Event):void {
		arrayListPreferences = new ArrayCollection(ObjectUtil.copy(arrayListPreferencesBackUp.source) as Array);
		
		var valueSortField:SortField =  new SortField();
		valueSortField.name = "preference";
		var dataSort:Sort = new Sort();
		dataSort.fields = [valueSortField];

		arrayListPreferences.sort = dataSort;
		arrayListPreferences.refresh();
		
		
		Alert.show(ConfigI18n.getInstance().getString("lblPrefValuesReseted"),
		   ConfigI18n.getInstance().getString("lblPrefSuccess"));
	}
	