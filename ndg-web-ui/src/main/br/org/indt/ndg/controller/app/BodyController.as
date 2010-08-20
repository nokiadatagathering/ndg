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
	
	import mx.containers.ViewStack;
			
	private var selectedButtonIndex:int = 0;
	public var myStack:ViewStack = null;
	
	private function init():void{
		setBtnMainState(true);
		mouseClickButton(1, null);
		buttonBar.setChildIndex(btnMain, 2);
	}
	
	private function setBtnMainState(selected:Boolean):void{
		if (selected){
			btnMain.height = 96;
			btnMain.width = 332;
			btnMain.x = 0;
			btnMain.y = 6;
		} else{
			btnMain.height = 77;
			btnMain.width = 319;
			btnMain.x = 6;
			btnMain.y = 12;
		}
	}
	private function setBtnManageState(selected:Boolean):void{
		if (selected){
			btnManage.height = 96;
			btnManage.width = 332;
			btnManage.x = 318;
			btnManage.y = 6;
		} else{
			btnManage.height = 77;
			btnManage.width = 319;
			btnManage.x = 324;
			btnManage.y = 12;
		}
	}			
	private function setBtnEditorState(selected:Boolean):void{
		if (selected){
			btnEditor.height = 96;
			btnEditor.width = 332;
			btnEditor.x = 636;
			btnEditor.y = 6;
		} else{
			btnEditor.height = 77;
			btnEditor.width = 319;
			btnEditor.x = 642;
			btnEditor.y = 12;
		}
	}			
		

			
	private function mouseOverButton(index:int, event:Event):void{
		if (index == 1 && selectedButtonIndex != 1){
			buttonBar.setChildIndex(btnMain, 2);
			setBtnMainState(true);
		} else if (index == 2 && selectedButtonIndex != 2){
			buttonBar.setChildIndex(btnManage, 2);
			setBtnManageState(true);
		} else if (index == 3 && selectedButtonIndex != 3){
			buttonBar.setChildIndex(btnEditor, 2);
			setBtnEditorState(true);
		}
	}
	private function mouseOutButton(index:int, event:Event):void{
		if (index == 1 && selectedButtonIndex != 1){
			setBtnMainState(false);
		} else if (index == 2 && selectedButtonIndex != 2){
			setBtnManageState(false);
		} else if (index == 3 && selectedButtonIndex != 3){
			setBtnEditorState(false);
		}
		if (selectedButtonIndex == 1){
			buttonBar.setChildIndex(btnMain, 2);
		} else if (selectedButtonIndex == 2){
			buttonBar.setChildIndex(btnManage, 2);
		} else if (selectedButtonIndex == 3){
			buttonBar.setChildIndex(btnEditor, 2);
		}
	}
		
	
	private function mouseClickButton(index:int, event:MouseEvent):void{
		selectedButtonIndex = index;
		if (index == 1){
			btnMain.selected = true;
			btnManage.selected = false;
			btnEditor.selected = false;
			setBtnManageState(false);
			setBtnEditorState(false);
		} else if (index == 2){
			btnMain.selected = false;
			btnManage.selected = true;
			btnEditor.selected = false;
			setBtnMainState(false);
			setBtnEditorState(false);
		} else if (index == 3){
			btnMain.selected = false;
			btnManage.selected = false;
			btnEditor.selected = true;
			setBtnMainState(false);
			setBtnManageState(false);
		}
		viewStack.selectedIndex = index - 1
	}
	
	private function updateButtonBarSize():void{
		buttonBar.width = buttonBar.width + 6;
		viewStackButtonBar.width = viewStackButtonBar.width + 6;
	}

	
	private function getHeight():int{
		var result:int = 68;
		viewStackButtonBar.selectedIndex = 1;
		if (SessionClass.getInstance().isAdmin()){
			result = 102;
			viewStackButtonBar.selectedIndex = 0;
		}
		return result;
	}
	
	
	
