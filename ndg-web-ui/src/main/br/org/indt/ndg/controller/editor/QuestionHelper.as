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
	import main.br.org.indt.ndg.controller.editor.*;
	import main.br.org.indt.ndg.ui.view.editor.*;
	import mx.collections.ArrayCollection;
	import mx.controls.Alert;
	import mx.resources.ResourceManager;
	import main.br.org.indt.ndg.i18n.ConfigI18n; 

	public class QuestionHelper
	{
		private static var instance:QuestionHelper;
		
		public static const NODE_CATEGORY:String = "category";
		public static const NODE_DESCRIPTION:String = "description";
		public static const NODE_QUESTION:String = "question";
		
		private var mainView:EditorEditSurveys;
		
		private var skipLogicOptionIndex:int = -1;
		private var skipLogicOriginNode:XML;
		private var skipLogicOperator:int = 0;
		
		//private var selectedNode:XML;
		
		public function QuestionHelper(pvt: PrivateClass)
		{
			super();
		}
		
		public static function getInstance():QuestionHelper
		{
			if(instance == null)
			{
				instance = new QuestionHelper(new PrivateClass());
			}
			return instance;
		}
		
		public function handleClearSkipLogicEvent(mobisusEvent:Event):void
		{
			var controllerEvent:ControllerEvent = mobisusEvent as ControllerEvent;
			mainView = controllerEvent.getPayload().getView() as EditorEditSurveys;
			
			mainView.multipleChoice.lbl_Skip.text = "";
			mainView.multipleChoice.ckBox_Inverse.selected = false;
			Question.clearSkipLogic(TreeHelper.getInstance().getSelectedNode(), controllerEvent.getPayload().getQuestionAttributes().getSkipLogicOptionIndex());
		}
		
		public function handleSkipLogicEvent(mobisusEvent:Event):void
		{
			var controllerEvent:ControllerEvent = mobisusEvent as ControllerEvent;
			mainView = controllerEvent.getPayload().getView() as EditorEditSurveys;
			
			Question.clearSkipLogic(TreeHelper.getInstance().getSelectedNode(), controllerEvent.getPayload().getQuestionAttributes().getSkipLogicOptionIndex());
			
			skipLogicOriginNode = mainView.questTree.selectedItem as XML;
			skipLogicOptionIndex = controllerEvent.getPayload().getQuestionAttributes().getSkipLogicOptionIndex();
			skipLogicOperator = controllerEvent.getPayload().getQuestionAttributes().getItemOtr() ? 1 : 0;
		}
		
		public function handleSkipLogicSelection(previousSelection:String):void
		{
			var selectedNode:XML = TreeHelper.getInstance().getSelectedNode();
			var questionDescription:String = selectedNode.child("description");

			Question.insertSkipLogic(skipLogicOriginNode, selectedNode, skipLogicOptionIndex, skipLogicOperator);
			
			mainView.multipleChoice.lbl_Skip.text = questionDescription;
			mainView.multipleChoice.ckBox_Inverse.selected = (skipLogicOperator == 1) ? true : false;
			skipLogicOptionIndex = -1;
			
			Alert.show(ResourceManager.getInstance().getString("editorResources", "SKIP_LOGIC_SELECTION_WARNING", [mainView.selectedAlternative, previousSelection, questionDescription]), ConfigI18n.getInstance().getStringFile("editorResources", "SKIP_LOGIC"), Alert.OK, mainView);
		}
		
		public function handleRemoveQuestionEvent(mobisusEvent:Event): void
		{			
			var myClickHandler:Function = function(event:Object): void 
			{
				if (event.detail == Alert.YES)
				{
					if (nodeName == NODE_QUESTION)
					{
						var categoryNode:XML = node.parent();		
						var catIndex:int = mainView.questTree.getItemIndex(categoryNode);	
						var questionsCount:int = Category.getQuestionsCountByCategory(node.parent());
						Category.removeQuestion(node);
						
						if (aReferences.length > 0)
						{
							Category.removeSkipLogicReferences(aReferences);
						}
						
						mainView.questTree.selectedIndex = catIndex;
						TreeHelper.getInstance().setSelectedNode(mainView.questTree.selectedItem as XML);
						TreeHelper.getInstance().updateScreenWithCategoryInformation(categoryNode);
						mainView.questTree.lastSelectedItem = mainView.questTree.selectedItem;
					}
					else if (nodeName == NODE_CATEGORY)
					{
						var rootNode:XML = node.parent();
						// Remove the selected category and items
						Survey.getInstance().removeCategory(Category.removeCategory(node));
					    // Set selected Index to -1 (nothing selected)
					    mainView.questTree.selectedIndex = -1;
					    // Set selected node to null since nothing is selected
						TreeHelper.getInstance().setSelectedNode(null);
						// Reset tree (necessary to not show <title> as a question)
					    // Survey.getInstance().setContent(rootNode);
					    // mainView.questTree.populateTree();
					    mainView.currentState = "";
						mainView.questTree.lastSelectedItem = null;
					}
					mainView.setModifiedSurvey(true);
				}
			};
			
			var controllerEvent:ControllerEvent = mobisusEvent as ControllerEvent;
			mainView = controllerEvent.getPayload().getView() as EditorEditSurveys;
			var node:XML = TreeHelper.getInstance().getSelectedNode();
			
			if(node != null)
			{
				var nodeName:String = node.localName() as String;
				var strItem:String = "";
				var strSkip:String = "";
				
				if (nodeName == NODE_QUESTION)
				{
					var aReferences:ArrayCollection = Category.checkSkipLogicReferences(node, node.parent().@id);
					if (aReferences.length > 0)
					{
						strSkip = "\n" + ConfigI18n.getInstance().getStringFile("editorResources", "REF_QUESTIONS") + ":";
						for (var nIndex:int = 0; nIndex < aReferences.length; nIndex++)
						{
							var nodeSkip:XML = Category.getQuestion(aReferences[nIndex][0], aReferences[nIndex][1]);
							if (nodeSkip != null)
							{
								strSkip += "\n - " + nodeSkip.description;
							}
						}
					}
				}
				else if (nodeName == NODE_CATEGORY)
				{
					var aReferences2:ArrayCollection = new ArrayCollection();
					var questChildren:XMLList = XMLList(node).children();
					var numberOfQuestions:int = questChildren.length();
					for (var iQuestion:int=0; iQuestion < numberOfQuestions; iQuestion++)
					{
						var aRef:ArrayCollection = Category.checkSkipLogicReferences(questChildren[iQuestion], node.@id);
						if (aRef.length > 0) 
							aReferences2.addItem(aRef);
					}
					strSkip = "\n" + ConfigI18n.getInstance().getStringFile("editorResources", "NUM_QUESTIONS") + ": " + numberOfQuestions;
					if (aReferences2.length > 0)
					{
						strSkip += "\n" + ConfigI18n.getInstance().getStringFile("editorResources", "REF_QUESTIONS_WARNING");
					}
				}
							
				if (nodeName == NODE_QUESTION)
				  strItem = ConfigI18n.getInstance().getStringFile("editorResources", "QUESTION") + ": " + node.description
				else
					strItem = ConfigI18n.getInstance().getStringFile("editorResources", "CATEGORY") + ": " + node.@name;
				Alert.show(ConfigI18n.getInstance().getStringFile("editorResources", "REMOVE_ITEM_WARNING") + " \n\n" + strItem + strSkip, ConfigI18n.getInstance().getStringFile("editorResources", "REMOVE_ITEM"), (Alert.YES | Alert.NO), mainView, myClickHandler);								
			}
		}
		
		public function isSkipLogicEnabled():Boolean
		{
			return (skipLogicOptionIndex != -1);
		}
		
		
		public function handleEditQuestionEvent(mobisusEvent:Event): void
		{		
			var controllerEvent:ControllerEvent = mobisusEvent as ControllerEvent;
			mainView = controllerEvent.getPayload().getView() as EditorEditSurveys;

			//var node:XML = mainView.questTree.selectedItem as XML;
			var node:XML = TreeHelper.getInstance().getSelectedNode();
			
			var payload:Payload = controllerEvent.getPayload();
			
			Question.updateQuestion(node, payload.getQuestionAttributes());
			mainView.setModifiedSurvey(true);
			
			//Preview
			if (node.@type != Question.CHOICE_TYPE)
				mainView.previewQuestion.showSimpleQuestion(node, mainView.cmbDevice.text)
			else
			  	mainView.previewQuestion.showMultipleChoiceQuestion(node, mainView.cmbDevice.text);
		} 
		
				
		public function handleEditCategoryEvent(mobisusEvent:Event):void
		{
			var controllerEvent:ControllerEvent = mobisusEvent as ControllerEvent;
			mainView = controllerEvent.getPayload().getView() as EditorEditSurveys;// controllerEvent.getView() as MainGUI;
			//var node:XML = mainView.questTree.selectedItem as XML;
			var node:XML = TreeHelper.getInstance().getSelectedNode();
			
			Survey.getInstance().updateCategory(node, controllerEvent.getPayload().getCategoryText());		   
			mainView.setModifiedSurvey(true);
		}
		
		public function handleAddStringItemEvent(mobisusEvent:Event): void
		{
			var controllerEvent:ControllerEvent = mobisusEvent as ControllerEvent;
			mainView = controllerEvent.getPayload().getView() as EditorEditSurveys;
			var node:XML = TreeHelper.getInstance().getSelectedNode();
			var nIndex:int = Question.getChoiceItensCountByQuestion(node);
			var szString:String = controllerEvent.getPayload().getChoice(); 
			Question.addStringItem(node, nIndex, szString);
			Question.populateList(node);
		}

	}
}

class PrivateClass
{
	public function PrivateClass()	{};
}