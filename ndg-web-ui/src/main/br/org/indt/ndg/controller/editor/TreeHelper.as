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
	import main.br.org.indt.ndg.ui.component.renderes.*;
	import main.br.org.indt.ndg.ui.view.editor.*;
	
	import mx.controls.Alert;
	import mx.controls.DateField;
	import mx.core.ClassFactory;
	import mx.core.DragSource;
	import mx.events.DragEvent;
	import mx.events.InvalidateRequestData;
	
	public class TreeHelper
	{
		private static var instance:TreeHelper;
		
		public static const NODE_CATEGORY:String = "category";
		public static const NODE_DESCRIPTION:String = "description";
		public static const NODE_QUESTION:String = "question";
		
		private var mainView:EditorEditSurveys;
		
		private var skipLogicOriginNode:XML;
		
		private var selectedNode:XML;
		
		public function TreeHelper(pvt:PrivateClass)
		{
			super();
		}
		
		public static function getInstance():TreeHelper
		{
			if(instance == null)
			{
				instance = new TreeHelper(new PrivateClass());
			}
			return instance;
		}
		
		public function getListItemRenderer(): ClassFactory{
			var bVal:Boolean = Question.isExclusiveChoice(TreeHelper.getInstance().getSelectedNode());
			if(bVal){
				return new ClassFactory(ListRadioButtonRenderer);
			}else{
				return new ClassFactory(ListCheckBoxRenderer);
			}
		}



		public function handleTreeClickEvent(mobisusEvent:Event):void
		{
			var controllerEvent:ControllerEvent = mobisusEvent as ControllerEvent;
			mainView = controllerEvent.getPayload().getView() as EditorEditSurveys;
			
			var nodeName:String = "";
			var currentSelection:String = "";
			
			if (mainView.questTree.selectedItem)
			{
			     nodeName = (mainView.questTree.selectedItem as XML).localName() as String;
			     
			     if (selectedNode != null)
			     {
			     	currentSelection = selectedNode.description;
			     }
			}
			
			if (nodeName == NODE_QUESTION)
			{
				// selectedNode = event.itemRenderer.data as XML;
				selectedNode = mainView.questTree.selectedItem as XML;


				// handle tree click for Skip Logic			
				if (QuestionHelper.getInstance().isSkipLogicEnabled())
				{
					QuestionHelper.getInstance().handleSkipLogicSelection(currentSelection);
				}
					
				if (selectedNode.@type == Question.STRING_TYPE)
				{
					mainView.currentState = EditorEditSurveys.STRING_STATE;
					mainView.txtQuestion.text = selectedNode.description;
					mainView.txtQuestionString_Length.text = selectedNode.length;
					
					//Create and set MLC Attributes
					/* mainView.initPopUpButton_str();
					setMLCAttributes(mainView.titleWin_str);
					 */
					//Preview

					mainView.previewQuestion.showSimpleQuestion(selectedNode, mainView.cmbDevice.text);
				}
				else if(selectedNode.@type == Question.CHOICE_TYPE)
				{
					if(Question.isExclusiveChoice(selectedNode)){
						mainView.currentState = EditorEditSurveys.CHOICE_EXCLUSIVE_RADIO_STATE;
						mainView.panelMultQuest.text = ConfigI18n.getInstance().getStringFile("editorResources", "EXCLUSIVE_CHOICE");
					}
					else{
						mainView.currentState = EditorEditSurveys.CHOICE_EXCLUSIVE_CHECK_STATE;
						mainView.panelMultQuest.text = ConfigI18n.getInstance().getStringFile("editorResources", "MULTIPLE_CHOICE");
					}
					
					mainView.ckBox_ChoiceExclusive.selected = Question.isExclusiveChoice(selectedNode);
					mainView.setChoiceList(mainView.ckBox_ChoiceExclusive.selected);
					mainView.txtQuestionExclusive.text = selectedNode.description;
					mainView.ckBox_ChoiceExclusive.selected = Question.isExclusiveChoice(selectedNode);

					mainView.lstChoices.dataProvider = Question.populateList(selectedNode);
					mainView.multipleChoice.clearAll();

					//Create and set MLC Attributes
					/* mainView.initPopUpButton_choice();
					setMLCAttributes(mainView.titleWin_choice); */
					//Preview

					mainView.previewQuestion.showMultipleChoiceQuestion(selectedNode, mainView.cmbDevice.text);
				}
				else if(selectedNode.@type == Question.INTEGER_TYPE)
				{
					mainView.currentState = EditorEditSurveys.INTEGER_STATE;
					mainView.txtQuestionInteger.text = selectedNode.description;		
					mainView.txtQuestionInteger_Length.text = selectedNode.length;
					var strTmp:String = selectedNode.@min;
					mainView.ckBoxMinRange_int.selected = (strTmp.length > 0);
					mainView.txtMinRange_int.enabled = mainView.ckBoxMinRange_int.selected;
					mainView.txtMinRange_int.text = selectedNode.@min;
					strTmp = selectedNode.@max;
					mainView.ckBoxMaxRange_int.selected = (strTmp.length > 0);
					mainView.txtMaxRange_int.enabled = mainView.ckBoxMaxRange_int.selected;
					mainView.txtMaxRange_int.text = selectedNode.@max;
					mainView.radioInt.selected = true;
					mainView.radioDecimal.selected = false;
					//Create and set MLC Attributes
					/* mainView.initPopUpButton_int();
					setMLCAttributes(mainView.titleWin_int); */
					//Preview
					mainView.previewQuestion.showSimpleQuestion(selectedNode, mainView.cmbDevice.text);
				}
				else if(selectedNode.@type == Question.DECIMAL_TYPE)
				{
					mainView.currentState = EditorEditSurveys.INTEGER_STATE;
					mainView.txtQuestionInteger.text = selectedNode.description;		
					mainView.txtQuestionInteger_Length.text = selectedNode.length;
					var strTmp3:String = selectedNode.@min;
					mainView.ckBoxMinRange_int.selected = (strTmp3.length > 0);
					mainView.txtMinRange_int.enabled = mainView.ckBoxMinRange_int.selected;
					mainView.txtMinRange_int.text = selectedNode.@min;
					strTmp3 = selectedNode.@max;
					mainView.ckBoxMaxRange_int.selected = (strTmp3.length > 0);
					mainView.txtMaxRange_int.enabled = mainView.ckBoxMaxRange_int.selected;
					mainView.txtMaxRange_int.text = selectedNode.@max;
					mainView.radioInt.selected = false;
					mainView.radioDecimal.selected = true;
					//Create and set MLC Attributes
					/* mainView.initPopUpButton_int();
					setMLCAttributes(mainView.titleWin_int); */
					//Preview
					mainView.previewQuestion.showSimpleQuestion(selectedNode, mainView.cmbDevice.text);
				}
				else if(selectedNode.@type == Question.DATE_TYPE)
				{
					mainView.currentState = EditorEditSurveys.DATE_STATE;
					mainView.txtQuestionDate.text = selectedNode.description;
					var strTmp2:String = selectedNode.@min;
					mainView.ckBoxMinRange_date.selected = (strTmp2.length > 0);
					mainView.txtMinRange_date.enabled = mainView.ckBoxMinRange_date.selected;
					mainView.txtMinRange_date.selectedDate = DateField.stringToDate(selectedNode.@min, "DD/MM/YYYY");
					strTmp2 = selectedNode.@max;
					mainView.ckBoxMaxRange_date.selected = (strTmp2.length > 0);
					mainView.txtMaxRange_date.enabled = mainView.ckBoxMaxRange_date.selected;
					mainView.txtMaxRange_date.selectedDate = DateField.stringToDate(selectedNode.@max, "DD/MM/YYYY");
					//Create and fill MLC Attributes
					/* mainView.initPopUpButton_date();
					setMLCAttributes(mainView.titleWin_date); */
					//Preview
					mainView.previewQuestion.showSimpleQuestion(selectedNode, mainView.cmbDevice.text);
				}
				else if(selectedNode.@type == Question.TIME_TYPE)
				{
					mainView.currentState = EditorEditSurveys.TIME_STATE;
					mainView.txtQuestionTime.text = selectedNode.description;
					
					var strConvention:String = selectedNode.@convention;
					
					if (strConvention == "12")
					   mainView.ckBoxAMPM_time.selected = true;
					else
					   mainView.ckBox24_time.selected = true;
  
  
					//Kivia Ramos - Valor do Campo Time
					/*var strValue:String = selectedNode.@value;
					
					if (mainView.ckBoxAMPM_time.selected)
					   mainView.txt_time.text = strValue + " " + strConvention;
					else
						mainView.txt_time.text = strValue;*/
					
					//Create and fill MLC Attributes
					/* mainView.initPopUpButton_date();
					setMLCAttributes(mainView.titleWin_date); */
					//Preview
					mainView.previewQuestion.showSimpleQuestion(selectedNode, mainView.cmbDevice.text);
				}
				else if(selectedNode.@type == Question.IMAGE_TYPE)
				{
					mainView.currentState = EditorEditSurveys.IMAGE_STATE;
					mainView.txtQuestion_img.text = selectedNode.description;
					mainView.numericImageCount.value = selectedNode.@maxCount;
					//Create and fill MLC Attributes
					/* mainView.initPopUpButton_image();
					setMLCAttributes(mainView.titleWin_image); */
					//Preview
					mainView.previewQuestion.showSimpleQuestion(selectedNode, mainView.cmbDevice.text);
				}
				
				//Check First Question
				setLabelFirstVisability(selectedNode.@type, (mainView.questTree.selectedItem as XML).parent().@id, (mainView.questTree.selectedItem as XML).@id, mainView);	
			}
		
			else if(nodeName == NODE_CATEGORY)
			{
				//selectedNode = event.itemRenderer.data as XML;
				selectedNode = mainView.questTree.selectedItem as XML;
				updateScreenWithCategoryInformation(selectedNode);
				//Preview
				mainView.previewQuestion.showMain(mainView.cmbDevice.text);
			}
			else
			{				
				//do nothing, for a while
			}			
		}
		
		private function setLabelFirstVisability(questionType:String, catID:String, questionID:String, mainView:EditorEditSurveys):void
		{
			if (questionType == Question.STRING_TYPE)
			{
				mainView.lblFirstQuestion_str.visible = false;
				if ( (questionID == '1') && (catID == '1') )
					mainView.lblFirstQuestion_str.visible = true;
			}
			else if ( (questionType == Question.CHOICE_TYPE) || (questionType == Question.CHOICE_EXCLUSIVE_TYPE) )	
			{
				mainView.lblFirstQuestion_choice.visible = false;
				if ( (questionID == '1') && (catID == '1') )
					mainView.lblFirstQuestion_choice.visible = true;
			}
			else if (questionType == Question.INTEGER_TYPE)
			{
				mainView.lblFirstQuestion_int.visible = false;
				if ( (questionID == '1') && (catID == '1') )
					mainView.lblFirstQuestion_int.visible = true;
			}
			else if (questionType == Question.DATE_TYPE)
			{
				mainView.lblFirstQuestion_date.visible = false;
				if ( (questionID == '1') && (catID == '1') )
					mainView.lblFirstQuestion_date.visible = true;
			}
			else if (questionType == Question.TIME_TYPE)
			{
				mainView.lblFirstQuestion_time.visible = false;
				if ( (questionID == '1') && (catID == '1') )
					mainView.lblFirstQuestion_time.visible = true;
			}
			else if (questionType == Question.IMAGE_TYPE)
			{
				mainView.lblFirstQuestion_img.visible = false;
				if ( (questionID == '1') && (catID == '1') )
					mainView.lblFirstQuestion_img.visible = true;
			}
		}
		
		public function handleTreeDoubleClickEvent(mobisusEvent:Event):void
		{
			var controllerEvent:ControllerEvent = mobisusEvent as ControllerEvent;
			mainView = controllerEvent.getPayload().getView() as EditorEditSurveys;
			
			var nodeName:String = (mainView.questTree.selectedItem as XML).localName() as String;
			
			if(nodeName == NODE_CATEGORY)
			{
				if (mainView.questTree.isItemOpen(mainView.questTree.selectedItem))
				{
					mainView.questTree.expandItem(mainView.questTree.selectedItem, false);
				}
				else
				{
					mainView.questTree.expandItem(mainView.questTree.selectedItem, true);
				}
			}
		}
		
		/* private function setMLCAttributes(titleWin: MLCView):void
		{
			titleWin.txtMLCField_var = selectedNode.@field;
			titleWin.ckBoxMLCEditable_var = (selectedNode.@editable == "false") ? false : true;
			titleWin.rbtnMLCDirIn_var = (selectedNode.@direction == "in");
			titleWin.rbtnMLCDirOut_var = (selectedNode.@direction == "out");
			titleWin.rbtnMLCDirOut_var == true ? titleWin.rbtnMLCDir_var = "out" : titleWin.rbtnMLCDir_var = "in"; 
		} */
		
		public function updateScreenWithCategoryInformation(node:XML):void
		{
			mainView.currentState = EditorEditSurveys.CATEGORY_STATE;//"Category";
			mainView.txtCategory.text = node.@name;				
		}
		
		public function handleTreeDragDropEvent(mobisusEvent:Event): void
		{
			var controllerEvent:ControllerEvent = mobisusEvent as ControllerEvent;
			var treeView:TreeViewSurvey = controllerEvent.getPayload().getView() as TreeViewSurvey;
			mainView = controllerEvent.getPayload().getExtraView() as EditorEditSurveys;
			var event:DragEvent = controllerEvent.getPayload().getEvent() as DragEvent;
						
			var ds:DragSource = event.dragSource;
		    var r:int = treeView.calculateDropIndex(event);		    
		  
		    treeView.selectedIndex = r;
		    var node:XML = treeView.selectedItem as XML;		    
		   
		    var item:String = event.dragSource.dataForFormat("qType") as String;
		    var questionType:String = event.dragSource.dataForFormat("buttonID") as String;
		    
		    addItemsToTree(node, item, questionType, mainView, treeView);
		    
		}
		
		public function handleAddItemTree(mobisusEvent:Event):void
		{
			var controllerEvent:ControllerEvent = mobisusEvent as ControllerEvent;
			var treeView:TreeViewSurvey = controllerEvent.getPayload().getView() as TreeViewSurvey;
			mainView = controllerEvent.getPayload().getExtraView() as EditorEditSurveys;
			var strType:String = controllerEvent.getPayload().getCategoryText();
			
			var item:String;
			if (strType == "category")
				item = "categ"
			else
				item = "quest";
				
			var node:XML = treeView.selectedItem as XML;
			
			addItemsToTree(node, item, strType, mainView, treeView);
			mainView.setModifiedSurvey(true);
		}
		
		private function addItemsToTree(node:XML, item:String, questionType:String, mainView:EditorEditSurveys, treeView:TreeViewSurvey):void
		{
			if(item == "categ")
		    {
				Survey.getInstance().appendCategory(Category.create(Category.CATEGORY_DEFAULT_DISPLAY_NAME, Category.getNewIndexForCategory()));
				// Select the new category (just created)
				treeView.selectedIndex = treeView.getItemIndex(Survey.getInstance().getContent().children()[Survey.getInstance().getContent().children().length()-1] as XML);
				mainView.tree_ItemClick(null);
				setFocusToDescription(item, "");
		    }
		    else if ((item == "quest") && (node != null))
		    {
				if(node.localName() == "question")
			    {
			    	node = node.parent();
			    	treeView.selectedIndex = treeView.getItemIndex(node);
			    }
				
				Category.appendQuestion(node, new Question(questionType));
				
				// Expand the category when adding a new question (to avoid DragOver error)
				treeView.expandItem(treeView.selectedItem, true);
				
				// Select the new question (just created)
				treeView.selectedIndex = treeView.getItemIndex((treeView.selectedItem as XML).children()[(treeView.selectedItem as XML).children().length()-1] as XML);
				mainView.tree_ItemClick(null);
				setFocusToDescription(item, questionType);
		    }
		    else
		    {
		    	Alert.show(ConfigI18n.getInstance().getStringFile("editorResources", "SELECT_CATEGORY_WARNING"), 
		    	ConfigI18n.getInstance().getStringFile("editorResources", "lblSuccess"), 4, mainView);
		    }
		}
		
		
		public function getSelectedNode():XML
		{
			return selectedNode;
		}
		
		public function setSelectedNode(node:XML):void
		{
			selectedNode = node;
		}
		
		private function setFocusToDescription(item: String, questionType: String):void
		{
			if (item == "categ")
			{
				mainView.txtCategory.setFocus();
				EditorEditSurveys.TextAreaSelectField(mainView.txtCategory);
			}
			else if (item == "quest")
			{
				if (questionType == Question.STRING_TYPE)
				{
					mainView.txtQuestion.setFocus();
					EditorEditSurveys.TextAreaSelectField(mainView.txtQuestion);
				}
				else if ( (questionType == Question.CHOICE_TYPE) || (questionType == Question.CHOICE_EXCLUSIVE_TYPE) ) 				
				{
					mainView.txtQuestionExclusive.setFocus();
					EditorEditSurveys.TextAreaSelectField(mainView.txtQuestionExclusive);
				}
				else if (questionType == Question.INTEGER_TYPE)
				{
					mainView.txtQuestionInteger.setFocus();
					EditorEditSurveys.TextAreaSelectField(mainView.txtQuestionInteger);
				}
				else if (questionType == Question.DATE_TYPE)
				{
					mainView.txtQuestionDate.setFocus();
					EditorEditSurveys.TextAreaSelectField(mainView.txtQuestionDate);
				}
				else if (questionType == Question.TIME_TYPE)
				{
					mainView.txtQuestionTime.setFocus();
					EditorEditSurveys.TextAreaSelectField(mainView.txtQuestionTime);
				}
				else if (questionType == Question.IMAGE_TYPE)
				{
					mainView.txtQuestion_img.setFocus();
					EditorEditSurveys.TextAreaSelectField(mainView.txtQuestion_img);
				}
			}
		}
		
		public function handleSearchItem(mobisusEvent:Event):void
		{
			var controllerEvent:ControllerEvent = mobisusEvent as ControllerEvent;
			mainView = controllerEvent.getPayload().getView() as EditorEditSurveys;
			var treeView:TreeViewSurvey = controllerEvent.getPayload().getExtraView() as TreeViewSurvey;
			
			var bFound:Boolean = false; var tmpCat:XML; var tmpQuest:XML; var lowCatID:int = -1; var lowQuestID:int = -1;
			
			var node:XML = treeView.selectedItem as XML;
			if (node != null)
			{
				if (node.localName() == "question")
				{
					lowCatID = node.parent().@id;
					lowQuestID = node.@id;
				}
				else if (node.localName() == "category")
				{
					lowCatID = node.@id;
					lowQuestID = -1;
				}
			}
			
			var strSearch:String = mainView.txtSearch.text;
			var strDesc:String;
			var survey:XML = Survey.getInstance().getContent();
			for each (var category:XML in survey.category)
			{
				if (category.@id >= lowCatID)
				{
					for each (var question:XML in category.question)
					{
						var pattern:RegExp = new RegExp(strSearch, "i");
						strDesc = question.description;
						if ( (strDesc.search(pattern) >= 0) && (bFound == false) && (question.@id  > lowQuestID) )
						{
							tmpCat = category;
							tmpQuest = question;
							bFound = true;
						}
					}
					
					lowQuestID = -1;
				}
			}
			
			if (bFound)
			{
				treeView.expandItem(tmpCat, true);
				treeView.selectedIndex = treeView.getItemIndex(Category.getQuestion(tmpCat.@id, tmpQuest.@id));
				mainView.tree_ItemClick(null);
			}
			else
			{
				Alert.show(ConfigI18n.getInstance().getStringFile("editorResources", "STRING_NOT_FOUND"), ConfigI18n.getInstance().getStringFile("editorResources", "STRING_WARNING"), 4, mainView);
			}
		}
		
		public function handleMoveDownQuestionEvent(mobisusEvent:Event): void
		{
			var controllerEvent:ControllerEvent = mobisusEvent as ControllerEvent;
			mainView = controllerEvent.getPayload().getView() as EditorEditSurveys;
			var node:XML = TreeHelper.getInstance().getSelectedNode();
			
			if (node != null)
			{
				var nodeName:String = node.localName() as String;
				var strItem:String = "";
				var strSkip:String = "";
				
				if (nodeName == NODE_QUESTION)
				{
					Category.moveDownQuestion(node);
					setLabelFirstVisability(node.@type, node.parent().@id, node.@id, mainView);
					mainView.setModifiedSurvey(true);
				}
				else if (nodeName == NODE_CATEGORY)
				{
					//Category.moveDownQuestion(node);
					//mainView.setModifiedSurvey(true);
				}
			}
		}		
		
		public function handleMoveUpQuestionEvent(mobisusEvent:Event): void
		{
			var controllerEvent:ControllerEvent = mobisusEvent as ControllerEvent;
			mainView = controllerEvent.getPayload().getView() as EditorEditSurveys;
			var node:XML = TreeHelper.getInstance().getSelectedNode();
			
			if (node != null)
			{
				var nodeName:String = node.localName() as String;
				var strItem:String = "";
				var strSkip:String = "";
				
				if (nodeName == NODE_QUESTION)
				{
					Category.moveUpQuestion(node);
					setLabelFirstVisability(node.@type, node.parent().@id, node.@id, mainView);
					mainView.setModifiedSurvey(true);
				}
				else if (nodeName == NODE_CATEGORY)
                {
                    //Category.moveUpQuestion(node);
                    //mainView.setModifiedSurvey(true);
                }
			}
		}
	}
}

class PrivateClass
{
	public function PrivateClass()
	{
		// empty
	};
}