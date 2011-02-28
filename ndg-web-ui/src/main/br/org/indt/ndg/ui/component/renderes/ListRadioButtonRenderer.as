package main.br.org.indt.ndg.ui.component.renderes
{
	import flash.events.MouseEvent;

	import main.br.org.indt.ndg.controller.editor.*;

	import mx.controls.RadioButton;

	public class ListRadioButtonRenderer extends RadioButton
	{
		public function ListRadioButtonRenderer()
		{
			super();
		}

		override public function set data (valData:Object) : void {
			super.data = valData;
			if(valData != null){
				label = data.label;
				group = data.group;
				selected = data.isSelected;
			}
			super.invalidateDisplayList();
		}

		override protected function clickHandler(event:MouseEvent) : void {
			super.clickHandler(event);
			var payload:Payload = new Payload();
			payload.setView(parentDocument);

			var contrllerEvent:ControllerEvent = new ControllerEvent(EventTypes.CHOICE_ITEM_CLICK, payload);
			FrontController.getInstance().dispatch(contrllerEvent);
			super.invalidateDisplayList();

		}

	}
}