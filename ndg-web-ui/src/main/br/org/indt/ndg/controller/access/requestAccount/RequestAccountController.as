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
	
	import main.br.org.indt.ndg.controller.util.ExceptionUtil;
	import main.br.org.indt.ndg.i18n.ConfigI18n;
	import main.br.org.indt.ndg.model.CompanyDTO;
	import main.br.org.indt.ndg.model.RoleDTO;
	import main.br.org.indt.ndg.model.UserDTO;
	
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	import mx.rpc.remoting.mxml.RemoteObject;
	import mx.validators.Validator;

	private static const REMOTE_SERVICE:String = "myService";
	public var myStack:ViewStack = null;
	
	
	private function init():void{
		txFirstname.setFocus();
	}
	
	private function requestAccount(event:Event):void {
		var errors:Array = Validator.validateAll(validators);
		
		if (txPasswordReqAc.text != txtVerifyPassword.text) {
			errors.push("password");
		}
		
		if (errors.length == 0){
			remoteRequestAccount(event);
		} else{
			if (errors[0].toString() == "password") {
				errorMsg.text = ConfigI18n.getInstance().getStringFile("requestAccount","passwordsDoesntMatch");
				txPasswordReqAc.errorString   = ConfigI18n.getInstance().getStringFile("requestAccount","passwordsDoesntMatch");
				txtVerifyPassword.errorString = ConfigI18n.getInstance().getStringFile("requestAccount","passwordsDoesntMatch");
			}
			else {
				errorMsg.text = ConfigI18n.getInstance().getString("redFieldsMsg");
			}

			txFirstname.setFocus();
		}
	}
	
	private function remoteRequestAccount(event:Event):void {
		var remoteObject:RemoteObject = new RemoteObject(REMOTE_SERVICE);
		remoteObject.showBusyCursor = true;
		remoteObject.addEventListener(FaultEvent.FAULT, onFault);
		remoteObject.addEventListener(ResultEvent.RESULT, onSuccess);
		remoteObject.requestAccount(getUserDto());
		
		function onSuccess(event:ResultEvent):void {
			Alert.show(resourceManager.getString("requestAccount", "raCheckYourEmail"),
				resourceManager.getString("requestAccount", "lblSuccess"),
				4, null, afterOk);
		}
		
		function afterOk():void{
			goBackLogin();
		}
		
		function onFault(event:FaultEvent):void {
			errorMsg.text = ExceptionUtil.getMessage(event.fault.faultString);
			txFirstname.setFocus();
		}
		 
	}

	private function getMaxLengthMsg(length:String):String{
		return resourceManager.getString(ConfigI18n.LOCALE_FILE, 'tooLongMsg') + length;
	}
	private function getMinLengthMsg(length:String):String{
		return resourceManager.getString(ConfigI18n.LOCALE_FILE, 'tooShortMsg') + length;
	}
	
	private function goBackLogin():void {
		errorMsg.text = "";
		resetForm();
		//show login view
		myStack.selectedIndex = MainConstants.LOGIN_VIEW_INDEX;
		//raStack.selectedIndex = 0;
	}
	
	private function companyChange():void{
		if (txCompany.text.length <= 0){
			cbIndustry.selectedIndex = 0;
			cbIndustrySize.selectedIndex = 0;
			txCompany.errorString = "";
			cbIndustry.errorString = "";
			cbIndustrySize.errorString = "";
		}
	}
	
	private function getUserDto():UserDTO{
		var dto:UserDTO = new UserDTO();
		dto.firstName = txFirstname.text;
		dto.lastName = txLastname.text;
		dto.username = txUsername.text;
		dto.password = txPasswordReqAc.text;
		dto.countryCode = "+" + txCountryCode.text;
		dto.areaCode = txAreaCode.text;
		dto.phoneNumber = txPhoneNumber.text;
		dto.email = txEmail.text;
		dto.userAdmin = txUsername.text;
		
		//company
		var company:CompanyDTO = new CompanyDTO();
		company.companyCountry = cbCountry.selectedItem.data;
		company.companyIndustry = cbIndustry.selectedItem.data;
		company.companyName = txCompany.text;
		company.companySize = cbIndustrySize.selectedItem.data;
		company.companyType = "";//cbCompanyType.selectedItem.data;
		dto.company = company;

		//role
		var role:RoleDTO = new RoleDTO();
		role.name = "Admin";
		dto.role = role;
		
		//other
		dto.firstTimeUse = "Y";	
		dto.whoUseIt = "Y";
		dto.ndgNewsLetter = "Y";
		dto.howDoYouPlanUseNdg = "";
		dto.userValidated = "N";
		dto.hasFullPermissions = "N";
		dto.emailPreferences = "Y";
		
		return dto;
	}
	
	private function resetForm():void{
		txFirstname.text = "";
		txLastname.text = "";
		txUsername.text = "";
		txPasswordReqAc.text = "";
		txtVerifyPassword.text = "";
		txCompany.text = "";
		cbCountry.selectedIndex = 0;
		txCountryCode.text = "";
		txAreaCode.text = "";
		txPhoneNumber.text = "";
		txEmail.text = "";
		//cbCompanyType.selectedIndex = 0;
		cbIndustry.selectedIndex = 0;
		cbIndustrySize.selectedIndex = 0;
		check.selected = false;
		txFirstname.errorString = "";
		txLastname.errorString = "";
		txUsername.errorString = "";
		txPasswordReqAc.errorString = "";
		txtVerifyPassword.errorString = "";
		txCompany.errorString = "";
		cbCountry.errorString = "";
		txCountryCode.errorString = "";
		txAreaCode.errorString = "";
		txPhoneNumber.errorString = "";
		txEmail.errorString = "";
		//cbCompanyType.errorString = "";
		cbIndustry.errorString = "";
		cbIndustrySize.errorString = "";
	}
	
	private function updateLanguage():void{
		errorMsg.text = "";
		updateCombos();
		txFirstname.setFocus();
	}
	
	
	