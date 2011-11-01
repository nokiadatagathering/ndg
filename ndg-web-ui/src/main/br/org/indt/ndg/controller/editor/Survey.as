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

package main.br.org.indt.ndg.controller.editor {
	
	
	import flash.utils.ByteArray;
	import mx.controls.Alert;
	import mx.collections.XMLListCollection;
	
	
	//singleton
	public class Survey{
		
		private static var instance:Survey;
		private var questionaryList:XMLListCollection;
		private var questionary:XML;

		public static function getInstance(): Survey {
			if (instance == null) {
				instance = new Survey(new PrivateClass());
			}
			return instance;
		}
		
		//TODO: MUST me private for Singleton to work
		public function Survey(pvt: PrivateClass) {
			var surveyID:Number = getNewSurveyID();
			questionary = <survey display="1-1" id={surveyID} title="" deployed="false"></survey>;
			questionaryList = new XMLListCollection(questionary.category);	
		}
		
		public function appendCategory(category:Category):void{
			questionaryList.addItem(category.getContent());
		}
		public function removeCategory(iPos:int):void{
			questionaryList.removeItemAt(iPos);
		}
		
		public function getContent():XML{
			return questionary;
		}
		public function setContent(xml:XML):void{
			questionary = xml;
			questionaryList = new XMLListCollection(questionary.category);
		}
		public function getContentList():XMLListCollection{
			return questionaryList;
		}
		
		public function getCategoryCount():int{
			var categoryList:XMLList = questionary.category;
			return categoryList.length();
		}

		public function updateCategory(category:XML, payload:Payload):void{
			category.@name = payload.getCategoryText();
			if( payload.getCategoryConditionText() != null ) {
				category.@condition = payload.getCategoryConditionText();
			}
			else
			{
				delete category.@condition;
			}
		}	
		
		public function setSurveyName(strName:String):void{
			questionary.@title = strName;
		}
		public function getSurveyName():String {
			return questionary.@title;
		}
		
		public function setSurveyId(strId:String):void{
			questionary.@id = strId;
		}
		public function getSurveyId():String {
			return questionary.@id;
		}
		
		public function reset():void {
			var surveyID:int = getNewSurveyID();
			questionary = <survey display="1-1" id={surveyID} title="" deployed="false"></survey>;
			questionaryList = new XMLListCollection(questionary.category);		
		}

		public function setSurveyDeployed(bval: Boolean):void {
			questionary.@deployed = bval;
		}
		public function getSurveyDeployed(): Boolean {
			return questionary.@deployed == "true" ? true : false;
		}
		
		public function prepareSave():String {
			questionary.@checksum = "";
			
			//Kivia Ramos - Gambi para retirar item xmlns
			var szQuestionary:String = questionary.toXMLString();
			var szResult:String = "";
			
			for (var y:int = 0; y < szQuestionary.length; y++)
		    {
		    	if ( (szQuestionary.charCodeAt(y)==32)
                   &&(szQuestionary.charCodeAt(y+1)==120)
		    	   &&(szQuestionary.charCodeAt(y+2)==109)
		    	   &&(szQuestionary.charCodeAt(y+3)==108)
		    	   &&(szQuestionary.charCodeAt(y+4)==110)
		    	   &&(szQuestionary.charCodeAt(y+5)==115) )
		    	{
		    		y = y + 56;
		    	}
		    	
		    	szResult += szQuestionary.charAt(y);
		    }
		    
		    szQuestionary = szResult;
			var byteArr:ByteArray = new ByteArray();
			byteArr.writeUTFBytes(szResult.toString());
			var strBuf: String = "";
			for (var i: int = 0; i < byteArr.length; i++){
				
				strBuf += String.fromCharCode(byteArr[i]);
			}
				
			
			var objMD5:ChecksumMD5 = new ChecksumMD5();
			var strMD5:String = objMD5.calcMD5(strBuf);
			
			questionary.@checksum = strMD5;
			
			szResult = "";
			for (var z:int = 0; z < szQuestionary.length; z++)
		    {
		    	if ( (szQuestionary.charCodeAt(z)==115)
                   &&(szQuestionary.charCodeAt(z+1)==117)
		    	   &&(szQuestionary.charCodeAt(z+2)==109)
		    	   &&(szQuestionary.charCodeAt(z+3)==61)
		    	   &&(szQuestionary.charCodeAt(z+4)==34) )
		    	{
		    		szResult += szQuestionary.charAt(z);
		    		szResult += szQuestionary.charAt(z+1);
		    		szResult += szQuestionary.charAt(z+2);
		    		szResult += szQuestionary.charAt(z+3);
		    		szResult += szQuestionary.charAt(z+4);
		    		szResult += strMD5;
		    		z = z + 5;
		    	}
		    	
		    	szResult += szQuestionary.charAt(z);
		    }
			
			// var strMD5:String = objMD5.calcMD5("The quick brown fox jumps over the lazy dog");
			// MD5("The quick brown fox jumps over the lazy dog") 
			// 9e107d9d372bb6826bd81d3542a419d6
			return szResult;
		}
		
		// the range of values represented by the uint class is 0 to 4,294,967,295 (2^32-1)
		public function getNewSurveyID():uint {
			var now:Date = new Date();
			return now.getTime() / 1000;
		}
		
		public function isEmpty():Boolean {
			return !(questionaryList.length > 0);
		}
		
		
	}
	
}


class PrivateClass {
	public function PrivateClass(){};
}


