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

package main.br.org.indt.ndg.ui.component.renderes
{
	import flash.events.MouseEvent;

	import main.br.org.indt.ndg.controller.editor.*;
	import main.br.org.indt.ndg.controller.editor.FrontController;
	import main.br.org.indt.ndg.controller.editor.Payload;

	import mx.controls.CheckBox;
	import mx.controls.Label;
	import mx.controls.List;

	public class ListCheckBoxRenderer extends CheckBox
	{
		public function ListCheckBoxRenderer()
		{
			super();
		}

		override public function set data (val:Object) : void {
			super.data = val;
			if(val != null){
				label = data.label;
				selected = data.isSelected;
			}
			super.invalidateDisplayList();
		}

		override protected function clickHandler(event:MouseEvent):void {
			super.clickHandler(event);
			data.isSelected = selected;
			data.label = label;
			var payload:Payload = new Payload();
			payload.setView(parentDocument);

			var contrllerEvent:ControllerEvent = new ControllerEvent(EventTypes.CHOICE_ITEM_CLICK, payload);
			FrontController.getInstance().dispatch(contrllerEvent);
		}
	}
}