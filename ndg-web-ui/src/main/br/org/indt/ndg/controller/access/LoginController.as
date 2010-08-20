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

	import flash.events.Event;
	import flash.net.SharedObject;
	
	import main.br.org.indt.ndg.controller.access.QueryString;
	import main.br.org.indt.ndg.controller.access.SessionClass;
	import main.br.org.indt.ndg.controller.access.SessionTimer;
	import main.br.org.indt.ndg.controller.util.ExceptionUtil;
	import main.br.org.indt.ndg.i18n.ConfigI18n;
	import main.br.org.indt.ndg.model.UserDTO;
	
	import mx.controls.Alert;
	import mx.containers.ViewStack;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	import mx.rpc.remoting.mxml.RemoteObject;
	import mx.validators.Validator;


	private static const REMOTE_SERVICE:String = "myService";
	private static const COOKIE_NAME:String = "login";
	private static const COOKIE_FIELD_USER:String = "user";
	private var cookie:SharedObject = SharedObject.getLocal(COOKIE_NAME);
	public var myStack:ViewStack = null;
	
	private var flagValidating:Boolean = false;
	private var keyMessage:int;
	private static const REQUEST_ACCOUNT:String = "requestAccount";
	private static const REQUEST_PASSWORD:String = "requestPassword";
	private var validatedUser:UserDTO;	

	
	private function init():void{
		var qs:QueryString = new QueryString();
		
		if (qs.parameters.rakey) {
			remoteValidateKey(qs.parameters.rakey, REQUEST_ACCOUNT);
			flagValidating = true;
			loginStack.selectedIndex = 2;
			//asyncCallScreenTitle.text = ConfigI18n.getInstance().getString("titleRequestAccount01");
		}
		if (qs.parameters.rpkey) {
			remoteValidateKey(qs.parameters.rpkey, REQUEST_PASSWORD);
			flagValidating = true;
			loginStack.selectedIndex = 2;
			//asyncCallScreenTitle.text = ConfigI18n.getInstance().getString("titleRecoverPassword");
		}		
		
		resetView();
	}
	
	
	private function login(event:Event):void{
		var errors:Array = Validator.validateAll(loginValidators);
		
		clearValidateMsg();
		
		if (errors.length == 0){
			remoteLogin(event);
		} else{
			loginErrorMsg.text = ConfigI18n.getInstance().getString("redFieldsMsg");
			txLogin.setFocus();
		}
	}
	
	private function remoteLogin(event:Event):void{
		var remoteObject:RemoteObject = new RemoteObject(REMOTE_SERVICE);
		remoteObject.showBusyCursor = true;
		remoteObject.addEventListener(FaultEvent.FAULT, onFault);
		remoteObject.addEventListener(ResultEvent.RESULT, onSuccess);
		remoteObject.validateLogin(txLogin.text, txPassword.text);
			
		function onSuccess(event:ResultEvent):void {
			if (event.result != null) {
				SessionTimer.getInstance().startTimer();
				
				var loggedUser:UserDTO = event.result as UserDTO;
				SessionClass.getInstance().loggedUser = loggedUser;
				
				if (chkRemember.selected){
					cookie.data.user = txLogin.text;
					cookie.data.password = txPassword.text;
					cookie.flush();
				} else{
					cookie.clear();
				}
				//show application body view
				myStack.selectedIndex = MainConstants.APPLICATION_BODY_INDEX;
			} else{
				loginErrorMsg.text = ConfigI18n.getInstance().getString("invalidUserPassError");
				txLogin.setFocus();
			}
		}
			
		function onFault(event:FaultEvent):void	{
			loginErrorMsg.text = ExceptionUtil.getMessage(event.fault.faultString);
			txLogin.setFocus();
		}
	}
	
	private function requestAccount():void{
		//show request account view
		myStack.selectedIndex = MainConstants.REQUEST_ACCESS_VIEW_INDEX;
		clearValidateMsg();
	}
	
	
	public function resetView():void{
		resetLoginErrorMsg();
		getCookie();
	}
	
	private function resetLoginErrorMsg():void{
		loginErrorMsg.text = "";
		txLogin.setFocus();
	}
	
	public function getCookie():void{
		if (cookie.data.hasOwnProperty(COOKIE_FIELD_USER)){
			txLogin.text = cookie.data.user;
			txPassword.text = cookie.data.password;
			chkRemember.selected = true;
		}
	}
	
	
	
	/* Recover Password methods */
	private function requestPassView():void{
		loginStack.selectedIndex = 1;
		clearValidateMsg();
	}
	
	private function requestPassword(event:Event):void {
		var errors:Array = Validator.validateAll(recPassValidators);
		if (errors.length == 0){
			remoteRequestPassword(event);
		} else{
			recoverErrorMsg.text = ConfigI18n.getInstance().getString("redFieldsMsg");
			txEmail.setFocus();
		}
	}
	
	private function remoteRequestPassword(event:Event):void {
		var remoteObject:RemoteObject = new RemoteObject(REMOTE_SERVICE);
		remoteObject.showBusyCursor = true;
		remoteObject.addEventListener(FaultEvent.FAULT, onFault);
		remoteObject.addEventListener(ResultEvent.RESULT, onSuccess);
		remoteObject.requestPassword(txEmail.text);
		
		function onSuccess(event:ResultEvent):void {
			if (event.result == true) {
				Alert.show(ConfigI18n.getInstance().getString("lblRecPassCheckEmail"),
						   ConfigI18n.getInstance().getString("lblRecPassSuccess"));
				goBackLogin();				
			} else {
				recoverErrorMsg.text = ConfigI18n.getInstance().getString("msgRecPassEmailNotFound");
				txEmail.setFocus();
			}
		}
		
		function onFault(event:FaultEvent):void {
			recoverErrorMsg.text = ExceptionUtil.getMessage(event.fault.faultString);
			txEmail.setFocus();
		}
	}
	
	private function goBackLogin():void{
		txEmail.text = "";
		txEmail.errorString = "";
		recoverErrorMsg.text = "";
		//show login view
		loginStack.selectedIndex = 0;
	}
	
	private function resetRecPassErrorMsg():void{
		if (loginStack.selectedIndex == 1) {
			recoverErrorMsg.text = "";
			txEmail.setFocus();
			txEmail.errorString = "";
		}
	}
	
	
	private function updatePassword():void {
		var errors:Array = Validator.validateAll(updatePassValidators);
		
		if (rpTxPass1.text != rpTxPass2.text) {
			errors.push("password");
		}
		
		if (errors.length == 0){
			remoteUpdatePassword();
		} else{
			if (errors[0].toString() == "password") {
				recoverChangeErrorMsg.text = ConfigI18n.getInstance().getStringFile("requestAccount","passwordsDoesntMatch");
				rpTxPass1.errorString   = ConfigI18n.getInstance().getStringFile("requestAccount","passwordsDoesntMatch");
				rpTxPass2.errorString = ConfigI18n.getInstance().getStringFile("requestAccount","passwordsDoesntMatch");
			}
			else {
				recoverChangeErrorMsg.text = ConfigI18n.getInstance().getString("redFieldsMsg");
			}

			rpTxPass1.setFocus();
		}
	}
	
	private function remoteUpdatePassword():void {
		var remoteObject:RemoteObject = new RemoteObject(REMOTE_SERVICE);
		remoteObject.showBusyCursor = true;
		remoteObject.addEventListener(FaultEvent.FAULT, onFault);
		remoteObject.addEventListener(ResultEvent.RESULT, onSuccess);
		
		validatedUser.password = rpTxPass1.text;
		
		remoteObject.updatePassword(validatedUser);
		
		function onSuccess(event:ResultEvent):void {
				Alert.show(ConfigI18n.getInstance().getString("lblRecPassPasswordUpdated"),
						   ConfigI18n.getInstance().getString("lblRecPassSuccess"));
				loginStack.selectedIndex = 0;
		}
		
		function onFault(event:FaultEvent):void {
			Alert.show(ConfigI18n.getInstance().getString("lblRecPassPasswordUpdatedFailure"),
					   ConfigI18n.getInstance().getString("lblError"));
			loginStack.selectedIndex = 0;
		}
	}
	
	
	/* Validate key methods */
	public function remoteValidateKey(key:String, type:String):void {
		var remoteObject:RemoteObject = new RemoteObject(REMOTE_SERVICE);
		remoteObject.showBusyCursor = true;
		remoteObject.addEventListener(FaultEvent.FAULT, onFault);
		remoteObject.addEventListener(ResultEvent.RESULT, onSuccess);
		remoteObject.validateKey(key);
		
		function onSuccess(event:ResultEvent):void {
			validatedUser = event.result as UserDTO;
			
			if (type == REQUEST_ACCOUNT) {
				loginStack.selectedIndex = 0;
				keyMsg.text = ConfigI18n.getInstance().getString("keyValidated");
				modeIcon.visible = true;
				keyMessage = 1;
			}
			else if (type == REQUEST_PASSWORD) {
				loginStack.selectedIndex = 3;
			}
		}
		function onFault(event:FaultEvent):void	{
			loginStack.selectedIndex = 0;
			keyMsg.text = ExceptionUtil.getMessage(event.fault.faultString);
			modeIcon.visible = true;

			switch (ExceptionUtil.getErrorCode(event.fault.faultString)) {
				case "MSM_CORE_MSG_USER_NOT_FOUND":
					keyMessage = 2;
					break;
				case "UNEXPECTED_SERVER_EXCEPTION":
					keyMessage = 3;
					break;
			}
		}
	}
	
	public function updateLanguage():void {
			if (flagValidating) {
				switch (keyMessage) {
					case 1: //User validated
						keyMsg.text = ConfigI18n.getInstance().getString("keyValidated");
						break;
					case 2: //User not found
						keyMsg.text = ConfigI18n.getInstance().getString("userNotFoundError");
						break;
					case 3: //Unexpected server exception
						keyMsg.text = ConfigI18n.getInstance().getString("unexpectedError");
						break;
				}
			}	
	}
	
	public function clearValidateMsg():void {
		keyMsg.text = "";
		modeIcon.visible = false;
	}
