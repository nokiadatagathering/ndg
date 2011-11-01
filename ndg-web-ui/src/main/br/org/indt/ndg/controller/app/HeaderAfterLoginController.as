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
	
	import main.br.org.indt.ndg.controller.access.SessionClass;
	import main.br.org.indt.ndg.i18n.ConfigI18n;
	import main.br.org.indt.ndg.model.UserDTO;
	import main.br.org.indt.ndg.ui.view.app.Preferences;
	import main.br.org.indt.ndg.ui.view.main.about.About;
	
	import mx.containers.ViewStack;
	import mx.managers.PopUpManager;
	
	public var bodyStack:ViewStack = null;

	private static const MAIN_PAGE:String = "main.html";

	
	private function init():void{
		resetView();
	}
				
	private function logout(event:MouseEvent):void{
		SessionClass.getInstance().logout();
		//bodyStack.selectedIndex = MainConstants.LOGIN_VIEW_INDEX;//change to Login view
		//var ref:URLRequest = new URLRequest("javascript:location.reload(true)");
		var ref:URLRequest = new URLRequest(MAIN_PAGE);
		navigateToURL(ref, "_self");
	}
	
	public function resetView():void{
		var user:UserDTO = SessionClass.getInstance().loggedUser;
		userMessage.text = ConfigI18n.getInstance().getString("lblHelloUser01") +
				user.firstName + " " + user.lastName;
		
		if (!SessionClass.getInstance().isHostedMode && SessionClass.getInstance().isAdmin()){
			btnPref.visible = true;
			btnPref.includeInLayout = true;
			rulePref.visible = true;
			rulePref.includeInLayout = true;
		}
	}	

	private function showAbout():void {
		var about:About = new About();
		
		PopUpManager.addPopUp(about, this, true);
		PopUpManager.centerPopUp(about);
	}
	
     public function goToURL(urlStr:String):void {
         var webPageURL:URLRequest = new URLRequest( urlStr );
         
         navigateToURL(webPageURL, '_blank')
     }	
     
     public function showPreferences(event:Event):void {
     	var preferences:Preferences = new Preferences();
     	
		PopUpManager.addPopUp(preferences, this, true);
		PopUpManager.centerPopUp(preferences);
     }