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