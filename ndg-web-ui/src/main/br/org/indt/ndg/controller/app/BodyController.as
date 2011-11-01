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


	import main.br.org.indt.ndg.controller.access.SessionClass;
	import main.br.org.indt.ndg.i18n.ConfigI18n;
	
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
	private function setBtnEditorState(selected:Boolean):void{
		if (selected){
			btnEditor.height = 96;
			btnEditor.width = 332;
			btnEditor.x = 318;
			btnEditor.y = 6;
		} else{
			btnEditor.height = 77;
			btnEditor.width = 319;
			btnEditor.x = 324;
			btnEditor.y = 12;
		}
	}			
	private function setBtnManagerState(selected:Boolean):void{
		if (selected){
			btnManager.height = 96;
			btnManager.width = 332;
			btnManager.x = 636;
			btnManager.y = 6;
		} else{
			btnManager.height = 77;
			btnManager.width = 319;
			btnManager.x = 642;
			btnManager.y = 12;
		}
	}			
		

			
	private function mouseOverButton(index:int, event:Event):void{
		if (index == 1 && selectedButtonIndex != 1){
			buttonBar.setChildIndex(btnMain, 2);
			setBtnMainState(true);
		} else if (index == 2 && selectedButtonIndex != 2){
			buttonBar.setChildIndex(btnEditor, 2);
			setBtnEditorState(true);
		} else if (index == 3 && selectedButtonIndex != 3){
			buttonBar.setChildIndex(btnManager, 2);
			setBtnManagerState(true);
		}
	}
	private function mouseOutButton(index:int, event:Event):void{
		if (index == 1 && selectedButtonIndex != 1){
			setBtnMainState(false);
		} else if (index == 2 && selectedButtonIndex != 2){
			setBtnEditorState(false);
		} else if (index == 3 && selectedButtonIndex != 3){
			setBtnManagerState(false);
		}
		if (selectedButtonIndex == 1){
			buttonBar.setChildIndex(btnMain, 2);
		} else if (selectedButtonIndex == 2){
			buttonBar.setChildIndex(btnEditor, 2);
		} else if (selectedButtonIndex == 3){
			buttonBar.setChildIndex(btnManager, 2);
		}
	}
		
	
	private function mouseClickButton(index:int, event:MouseEvent):void{
		selectedButtonIndex = index;
		if (index == 1){
			btnMain.selected = true;
			btnEditor.selected = false;
			btnManager.selected = false;
			setBtnEditorState(false);
			setBtnManagerState(false);
		} else if (index == 2){
			btnMain.selected = false;
			btnEditor.selected = true;
			btnManager.selected = false;
			setBtnMainState(false);
			setBtnManagerState(false);
		} else if (index == 3){
			btnMain.selected = false;
			btnEditor.selected = false;
			btnManager.selected = true;
			setBtnMainState(false);
			setBtnEditorState(false);
		}
		viewStack.selectedIndex = index - 1
	}
	
	private function updateButtonBar():void{
		buttonBar.width = buttonBar.width + 6;
		//viewStackButtonBar.width = viewStackButtonBar.width + 6;
		if (SessionClass.getInstance().isAdmin()){
			btnManager.visible = true;
		}		
	}

	
	//private function getHeight():int{
	//	var result:int = 68;
	//	viewStackButtonBar.selectedIndex = 1;
	//	if (SessionClass.getInstance().isAdmin()){
	//		result = 102;
	//		viewStackButtonBar.selectedIndex = 0;
	//	}
	//	return result;
	//}
	
	
	