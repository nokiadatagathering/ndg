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
	
	
	import flash.events.Event;
	
	public class Payload{
		
		private var view:Object;
		private var extraView:Object;
		private var event:Event;
		private var questionAttributes:AttributeList;
		private var categoryText:String;
		private var choice:String;
		private var categoryConditionText:String;
				
		
		//Inicio Kivia Ramos
		
		public function setChoice(szChoice:String):void {
			this.choice =  szChoice;
		}
		
		public function getChoice():String {
			return choice;
		}
		//Fim Kivia Ramos
		
		public function getView():Object{
			return view;
		}
		public function setView(view:Object):void {
			this.view = view;
		}
		
		public function getExtraView():Object {
			return extraView;
		}
		public function setExtraView(view:Object):void {
			this.extraView = view;
		}
		
		public function getQuestionAttributes():AttributeList {
			return questionAttributes;
		}
		public function setQuestionAttribute(attribute:AttributeList):void {
			questionAttributes = attribute;
		}
		
		public function setCategoryText(text:String):void{
			this.categoryText = text;
		}

		public function getCategoryText():String{
			return categoryText;
		}

		public function setCategoryConditionText(text:String):void{
			this.categoryConditionText = text;
		}

		public function getCategoryConditionText():String{
			return categoryConditionText;
		}

		public function getEvent():Event{
			return event;
		}
		public function setEvent(event:Event):void{
			this.event = event;
		}
	}
	
	
}