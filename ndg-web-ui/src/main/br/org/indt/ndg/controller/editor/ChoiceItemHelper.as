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

package main.br.org.indt.ndg.controller.editor
{
	import flash.events.Event;
	
	import main.br.org.indt.ndg.i18n.ConfigI18n;
	import main.br.org.indt.ndg.ui.view.editor.*;

	public class ChoiceItemHelper
	{
		private static var instance:ChoiceItemHelper = new ChoiceItemHelper();
		
		public static const NODE_CATEGORY:String = "category";
		public static const NODE_DESCRIPTION:String = "description";
		public static const NODE_QUESTION:String = "question";
		
		private var mainView:EditorEditSurveys;
		
		public static function getInstance():ChoiceItemHelper
		{
			return instance;
		}
		
		public function handleAddChoiceItemEvent(mobisusEvent:Event): void
		{
			var controllerEvent:ControllerEvent = mobisusEvent as ControllerEvent;
			mainView = controllerEvent.getPayload().getView() as EditorEditSurveys;
			
			var node:XML = TreeHelper.getInstance().getSelectedNode();
			var nIndex:int = Question.getChoiceItensCountByQuestion(node); 
			Question.addChoiceItem(node, nIndex);
			mainView.lstChoices.dataProvider = Question.populateList(node);
		}
		

		//Inicio Kivia Ramos
		
		public function handleAddCSVItemEvent(mobisusEvent:Event): void
		{
			var controllerEvent:ControllerEvent = mobisusEvent as ControllerEvent;
			mainView = controllerEvent.getPayload().getView() as EditorEditSurveys;
			var node:XML = TreeHelper.getInstance().getSelectedNode();
			var nIndex:int = Question.getChoiceItensCountByQuestion(node);
			var szChoice:String = controllerEvent.getPayload().getChoice(); 
			Question.addCSVItem(node, nIndex, szChoice);
			mainView.lstChoices.dataProvider = Question.populateList(node);
		}
		
		//Fim Kivia Ramos
		
		public function handleEditChoiceItemEvent(mobisusEvent:Event): void
		{	
			var controllerEvent:ControllerEvent = mobisusEvent as ControllerEvent;
			mainView = controllerEvent.getPayload().getView() as EditorEditSurveys;

			var choiceIndex:int = controllerEvent.getPayload().getQuestionAttributes().getSelectedChoiceItem();
			var txtChoiceItem:String = controllerEvent.getPayload().getQuestionAttributes().getDescription();
			var otrChoice:Boolean = controllerEvent.getPayload().getQuestionAttributes().getItemOtr(); 
			var node:XML = TreeHelper.getInstance().getSelectedNode();
			var skipLogicNode:XML = Question.getSkipLogicNode(node, choiceIndex);
			
			Question.editChoiceItem(node, choiceIndex, txtChoiceItem, (otrChoice == true) ? 1 : 0);
			mainView.lstChoices.dataProvider = Question.populateList(node);
			mainView.lstChoices.selectedIndex = choiceIndex;
			mainView.vboxAlternative.visible = false;
			if (skipLogicNode != null)
				skipLogicNode.@operator = int(mainView.multipleChoice.ckBox_Inverse.selected);
		} 
		
		public function handleRemoveChoiceItemEvent(mobisusEvent:Event): void
		{
			var controllerEvent:ControllerEvent = mobisusEvent as ControllerEvent;
			mainView = controllerEvent.getPayload().getView() as EditorEditSurveys;
			
			var choiceIndex:int = controllerEvent.getPayload().getQuestionAttributes().getSelectedChoiceItem();
			var node:XML = TreeHelper.getInstance().getSelectedNode();
			
			if(node != null)
			{
				Question.removeChoiceItem(node, choiceIndex);
				mainView.lstChoices.dataProvider = Question.populateList(node);
			}
		}
		
		public function handleShowChoiceItemEvent(mobisusEvent:Event): void
		{
			var controllerEvent:ControllerEvent = mobisusEvent as ControllerEvent;
			mainView = controllerEvent.getPayload().getView() as EditorEditSurveys;
			
			var choiceIndex:int = controllerEvent.getPayload().getQuestionAttributes().getSelectedChoiceItem();
			var node:XML = TreeHelper.getInstance().getSelectedNode();
			
			mainView.multipleChoice.txtChoiceItem.text = node.child("item")[choiceIndex];
			mainView.multipleChoice.ckBox_ItemOtr.selected = (node.child("item")[choiceIndex].@otr == "0") ? false : true;
			
			mainView.multipleChoice.lbl_Skip.text = "";
			mainView.multipleChoice.btn_Add.label = ConfigI18n.getInstance().getStringFile("editorResources", "ADD");
			mainView.multipleChoice.ckBox_Inverse.selected = false;
			var skipLogicNode:XML = Question.getSkipLogicNode(node, choiceIndex);
			if (skipLogicNode != null)
			{
				var skipToQuestion:XML = Category.getQuestion(skipLogicNode.@catTo, skipLogicNode.@skipTo);
				if(skipToQuestion != null)
				{			
					mainView.multipleChoice.lbl_Skip.text = skipToQuestion.description;
					mainView.multipleChoice.ckBox_Inverse.selected = skipLogicNode.@operator == "1" ? true : false;
					mainView.multipleChoice.btn_Add.label = ConfigI18n.getInstance().getStringFile("editorResources", "CLEAR"); 
				}
			}
			mainView.vboxAlternative.visible = true;
			//mainView.vboxAlternative.x = controllerEvent.getPayload().getQuestionAttributes().getMousePos().x;
			//mainView.vboxAlternative.y = controllerEvent.getPayload().getQuestionAttributes().getMousePos().y;
		}
	}
}