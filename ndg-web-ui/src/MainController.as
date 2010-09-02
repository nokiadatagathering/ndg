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
	import main.br.org.indt.ndg.controller.app.LocaleEvent;
	import main.br.org.indt.ndg.controller.util.ExceptionUtil;
	import main.br.org.indt.ndg.i18n.ConfigI18n;
	
	import mx.controls.Alert;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	import mx.rpc.remoting.mxml.RemoteObject;
	
	private static const REMOTE_SERVICE:String = "myService";

	
	private function init():void{
		
		headerBeforeLoginView.addEventListener(LocaleEvent.EVENT_NAME, selectLocale);
		ConfigI18n.getInstance().setCurrentLocale(ConfigI18n.en_US);
		
		remoteGetMode();
		remoteGetVersion();
		remoteGetSmsSupport();
		
		//Kivia Ramos - Deixar que passe todo dominio
        //Security.allowDomain();
        //Security.allowInsecureDomain();
     
	}
	
	private function selectLocale(event:LocaleEvent):void{
		//loginView.resetLoginErrorMsg();
		ConfigI18n.getInstance().setCurrentLocale(event.currentLocale);
	}
	
	private function changeHeaderStack():void{
		if (bodyStack.selectedIndex == MainConstants.LOGIN_VIEW_INDEX){
			bodyPanel.height = 570;
			bodyStack.height = 562;
			//change to "header before login" view
			headerStack.selectedIndex = MainConstants.HEADER_BEFORE_LOGIN_VIEW_INDEX;
		} else if (bodyStack.selectedIndex == MainConstants.REQUEST_ACCESS_VIEW_INDEX){
			bodyPanel.height = 570;
			bodyStack.height = 562;
			//change to "header before login" view
			headerStack.selectedIndex = MainConstants.HEADER_BEFORE_LOGIN_VIEW_INDEX;
		} else if (bodyStack.selectedIndex == MainConstants.APPLICATION_BODY_INDEX){
			bodyPanel.height = 570;
			bodyStack.height = 562;
			//change to "header after login" view
			headerStack.selectedIndex = MainConstants.HEADER_AFTER_LOGIN_VIEW_INDEX;
		}
	}
	
	private function remoteGetMode():void
	{
		var remoteObject:RemoteObject = new RemoteObject(REMOTE_SERVICE);
		remoteObject.showBusyCursor = true;
		remoteObject.addEventListener(FaultEvent.FAULT, onFault);
		remoteObject.addEventListener(ResultEvent.RESULT, onSuccess);
		remoteObject.isHostedMode();
		
		function onFault(event:FaultEvent):void{
			Alert.show(ExceptionUtil.getMessage(event.fault.faultString),
					ConfigI18n.getInstance().getString("lblError"));
		}
		function onSuccess(event:ResultEvent):void{
			if (event.result != null) {
				SessionClass.getInstance().isHostedMode = event.result as Boolean;
			}				
		}
	}
	
	private function remoteGetVersion():void {
		var ndgVersion:String = "0.0.0";
		var remoteObject:RemoteObject = new RemoteObject(REMOTE_SERVICE);
		remoteObject.showBusyCursor = true;
		remoteObject.addEventListener(FaultEvent.FAULT, onFault);
		remoteObject.addEventListener(ResultEvent.RESULT, onSuccess);
		remoteObject.getVersion();
			
		function onSuccess(event:ResultEvent):void {
			if (event.result != null) {
				ndgVersion = event.result as String;
				SessionClass.getInstance().ndgVersion = ndgVersion;
			}
		}	
		function onFault(event:FaultEvent):void	{
		}
	}
	
	private function remoteGetSmsSupport():void{
		var remoteObject:RemoteObject = new RemoteObject(REMOTE_SERVICE);
		remoteObject.showBusyCursor = true;
		remoteObject.addEventListener(FaultEvent.FAULT, onFault);
		remoteObject.addEventListener(ResultEvent.RESULT, onSuccess);
		remoteObject.hasSmsSupport();

		function onSuccess(event:ResultEvent):void {
			if (event.result != null) {
				SessionClass.getInstance().hasSmsSupport = event.result as Boolean;
			}
		}	
		function onFault(event:FaultEvent):void	{
		}
		
	}

	
	