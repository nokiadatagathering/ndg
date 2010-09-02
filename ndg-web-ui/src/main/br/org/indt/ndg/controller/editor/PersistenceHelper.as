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

package main.br.org.indt.ndg.controller.editor {
	
	
	import flash.utils.*;
	
	import main.br.org.indt.ndg.controller.access.SessionClass;
	import main.br.org.indt.ndg.controller.access.SessionTimer;
	import main.br.org.indt.ndg.controller.util.ExceptionUtil;
	import main.br.org.indt.ndg.i18n.ConfigI18n;
	import main.br.org.indt.ndg.ui.view.editor.*;
	
	import mx.collections.IViewCursor;
	import mx.collections.XMLListCollection;
	import mx.controls.Alert;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	import mx.rpc.remoting.mxml.RemoteObject;
	
	
	public class PersistenceHelper{
		
		private static var instance:PersistenceHelper = null;
		
		private static var SAVE:int = 1;
		private static var SAVE_AS:int = 2;
		private static var MAKE_AVAILABLE:int = 3;
		private var idBeforeSave:String = null;
		private var availableBeforeSave:Boolean = false;
		
		
		public function PersistenceHelper(pvt:PrivateClass){
			super();			
		}
		
		public static function getInstance():PersistenceHelper{
			if (instance == null){
				instance = new PersistenceHelper(new PrivateClass());
			}
			return instance;
		}
		
		public function loadSurvey(survey:String):void{//surveyID:String, loadTree:Function):void {
			Survey.getInstance().setContent(new XML(survey));
			Category.calculateNewIndex();
			runActionsAfterLoadSurvey();
		}
				
		
		private function runActionsAfterLoadSurvey():void {
			// Check is the file has been modified out of Mobisus Editor
			var curMD5:String = Survey.getInstance().getContent().@checksum;
			
			if (curMD5 != ""){ // Workaround to support openning old surveys
				Survey.getInstance().getContent().@checksum = "";
				
				var byteArr:ByteArray = new ByteArray();
				byteArr.writeUTFBytes(Survey.getInstance().getContent().toXMLString());
				
				var strBuf: String = "";
				for (var i: int = 0; i < byteArr.length; i++){
					strBuf += String.fromCharCode(byteArr[i]);
				}
				
				var objMD5:ChecksumMD5 = new ChecksumMD5();
				var strMD5:String = objMD5.calcMD5(strBuf);
				if (curMD5 != strMD5){
					strMD5 = objMD5.calcMD5(Survey.getInstance().getContent().toXMLString());
					
					if (curMD5 != strMD5){
						// Modified -> Exit
						Survey.getInstance().reset();
						Alert.show(ConfigI18n.getInstance().getStringFile("editorResources", "FILE_CORRUPTED"),
							ConfigI18n.getInstance().getStringFile("editorResources", "lblError"), 4);
						return;
					}
				}
			}
			
			// Restore value
			Survey.getInstance().getContent().@checksum = curMD5;
		}
		
		public function createNewSurvey(loadTree:Function):void {
			Survey.getInstance().reset();
			loadTree.call();
		}
			
		public function saveSurvey(successFunc:Function):void {
			save(SAVE, successFunc);
		}
		
		public function saveSurveyAs(successFunc:Function):void{
			idBeforeSave = Survey.getInstance().getSurveyId();
			Survey.getInstance().setSurveyId(new String(Survey.getInstance().getNewSurveyID()));
			
			availableBeforeSave = Survey.getInstance().getSurveyDeployed();
			Survey.getInstance().setSurveyDeployed(false);
			
			save(SAVE_AS, successFunc);
		}
		
		public function makeAvailable(successFunc:Function):void{
			availableBeforeSave = Survey.getInstance().getSurveyDeployed();
			Survey.getInstance().setSurveyDeployed(true);

			save(MAKE_AVAILABLE, successFunc);	
		}
		
		private function save(action:int, successFunc:Function): void	{
			
			var szSurvey:String = Survey.getInstance().prepareSave();
			var survey:String = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n";
			
			//survey += Survey.getInstance().getContent().toXMLString();
			survey += szSurvey;
			
			var remoteObject:RemoteObject = new RemoteObject("myService");
			remoteObject.showBusyCursor = true;
	      	remoteObject.addEventListener(ResultEvent.RESULT, onResult);
			remoteObject.addEventListener(FaultEvent.FAULT, onFault);
			remoteObject.saveSurveyFromEditorToServer(SessionClass.getInstance().loggedUser.username, survey);
			SessionTimer.getInstance().resetTimer();

			function onResult(event:ResultEvent):void{
				if (successFunc != null){
					successFunc.call();
				}
	      	}
			function onFault(event:FaultEvent):void{
				var msg:String = "";
				
				if (action == SAVE){
					msg = ConfigI18n.getInstance().getStringFile("editorResources", "surveyNotSavedError");
				} else if (action == SAVE_AS){
					Survey.getInstance().setSurveyId(idBeforeSave);
					Survey.getInstance().setSurveyDeployed(availableBeforeSave);
					msg = ConfigI18n.getInstance().getStringFile("editorResources", "surveyNotDuplicatedError");
				} else if (action == MAKE_AVAILABLE){
					Survey.getInstance().setSurveyDeployed(availableBeforeSave);
					msg = ConfigI18n.getInstance().getStringFile("editorResources", "surveyNotMakeAvailableError");
				}
				
				msg = msg + ":\n" + ExceptionUtil.getMessage(event.fault.faultString);
				Alert.show(msg, ConfigI18n.getInstance().getString("lblError"));
			}
		}		

		public function makeAvailableForSurveysList(surveyContent:String, successFunc:Function):void{
			Survey.getInstance().setContent(new XML(surveyContent));
			runActionsAfterLoadSurvey();
			Survey.getInstance().setSurveyDeployed(true);
			
			var categoriesName:Array = getEmptyCategories();
			if (categoriesName.length > 0){
				Alert.show(ConfigI18n.getInstance().getStringFile("editorResources", "CATEGORIES_EMPTY_QUESTION1"),
					ConfigI18n.getInstance().getStringFile("editorResources", "lblError"));
			} else{
				save(MAKE_AVAILABLE, successFunc);
			}
		}
		
		//Return an array of strings containing the name of the categories that has
		//empty questions
		public function getEmptyCategories():Array {
			var list:XMLListCollection = Survey.getInstance().getContentList();//questTree.dataProvider as XMLListCollection;
			var cursor:IViewCursor = list.createCursor();
			var node:XMLList = null;
			var categoriesName:Array = new Array();
			
			while (cursor.current != null){
				node = cursor.current.children() as XMLList;
				if (node.length() == 0) {
					categoriesName.push(cursor.current.@name);
				}
				cursor.moveNext();
			}
			return categoriesName;
		}	

		public function isSurveyNull():Boolean {
			var list:XMLListCollection = Survey.getInstance().getContentList();//questTree.dataProvider as XMLListCollection;
			var cursor:IViewCursor = list.createCursor();
			return (cursor.current == null);
		}

	}
}

class PrivateClass {
	public function PrivateClass(){};
}


