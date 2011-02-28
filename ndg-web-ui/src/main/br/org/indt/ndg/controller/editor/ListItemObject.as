package main.br.org.indt.ndg.controller.editor
{
	import mx.controls.RadioButtonGroup;

	public class ListItemObject
	{
		[Bindable]
		public var label:String;

		[Bindable]
		public var isSelected:Boolean;

		[Bindable]
		public var group:Object;

		public function ListItemObject(lab:String, sel:Boolean, gro:Object	):void
		{
			label = lab;
			isSelected = sel;
			group = gro;
		}
	}
}