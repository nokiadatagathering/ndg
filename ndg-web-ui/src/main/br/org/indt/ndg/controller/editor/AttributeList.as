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

package main.br.org.indt.ndg.controller.editor
{
	import flash.geom.Point;
	
	public class AttributeList
	{
		private var description:String;
		private var choiceItem:String;
		private var exclusive:Boolean;
		private var length:String;
		private var skipLogicOptionIndex:int;
		private var selectedChoiceItem:int;
		private var otr:Boolean;
		private var mlcField:String;
		private var mlcDirection:String;
		private var mlcEditable:Boolean;
		private var mousePos:Point;
		private var minRange:String;
		private var maxRange:String;
		private var decimalAllowed:Boolean;
		private var formatTime:String;
		private var imageMaxCount:int;

		public function AttributeList()
		{}
		
		public function getImageMaxCount():int{
			return imageMaxCount;
		}

		public function setImageMaxCount(val:int):void{
			imageMaxCount = val;
		}

		public function setDescription(text:String):void
		{
			description = text;
		}
		
		public function getDescription():String
		{
			return description;
		}
		
		public function setChoiceItem(text:String):void
		{
			choiceItem = text;
		}
		
		public function getChoiceItem():String
		{
			return choiceItem;
		}
		
		public function setExclusivity(isExclusive:Boolean):void
		{
			this.exclusive = isExclusive;			
		}
		
		public function isExclusive():Boolean
		{
			return exclusive;
		}	
		
		public function setLength(length:String):void
		{
			this.length = length;
		}
		
		public function getLength():String
		{
			return length;
		}
		
		public function setSkipLogicOption(skipLogicOptionIndex:int):void
		{
			this.skipLogicOptionIndex = skipLogicOptionIndex;
		}
		
		public function getSkipLogicOptionIndex():int
		{
			return skipLogicOptionIndex;
		}
		
		public function setSelectedChoiceItem(sel:int):void
		{
			selectedChoiceItem = sel;
		}
		
		public function getSelectedChoiceItem():int
		{
			return selectedChoiceItem;
		}
		
		public function setItemOtr(isOtr:Boolean):void
		{
			otr = isOtr;			
		}
		
		public function getItemOtr():Boolean
		{
			return otr;
		}
		
		public function setMLCField(field:String):void
		{
			mlcField = field;
		}
		
		public function getMLCField():String
		{
			return mlcField;
		}
		
		public function setMLCDirection(dir:String):void
		{
			mlcDirection = dir;
		}
		
		public function getMLCDirection():String
		{
			return mlcDirection;
		}
		
		public function setMLCEditable(val:Boolean):void
		{
			mlcEditable = val;
		}
		
		public function getMLCEditable():Boolean
		{
			return mlcEditable;
		}
		
		public function setMousePos(val:Point):void
		{
			mousePos = val;
		}
		
		public function getMousePos():Point
		{
			return mousePos;
		}
		
		public function setMinRange(val:String):void
		{
			minRange = val;
		}
		
		public function getMinRange():String
		{
			return minRange;
		}
		
		public function setFormatTime(val:String):void
		{
			formatTime = val;
		}
		
		public function getFormatTime():String
		{
			return formatTime;
		}
		
		public function setMaxRange(val:String):void
		{
			maxRange = val;
		}
		
		public function getMaxRange():String
		{
			return maxRange;
		}
		
		public function setDecimalAllowed(val: Boolean):void
		{
			decimalAllowed = val;
		}
		
		public function getDecimalAllowed():Boolean
		{
			return decimalAllowed;
		}
	}
}