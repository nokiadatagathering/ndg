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

package main.br.org.indt.ndg.controller.editor {
	
	
	import flash.events.EventDispatcher;



	// handles user's actions, add listeners, dispatch events
	public class FrontController extends EventDispatcher {		
		
		private static var instance:FrontController;

		public function FrontController(pvt: PrivateClass) {
			super(this);
			addEventListener(EventTypes.TREE_CLICK_EVENT, TreeHelper.getInstance().handleTreeClickEvent);
			addEventListener(EventTypes.TREE_DOUBLECLICK_EVENT, TreeHelper.getInstance().handleTreeDoubleClickEvent);
			addEventListener(EventTypes.TREE_DRAGDROP_EVENT, TreeHelper.getInstance().handleTreeDragDropEvent);
			addEventListener(EventTypes.EDIT_QUESTION_EVENT, QuestionHelper.getInstance().handleEditQuestionEvent);
			addEventListener(EventTypes.EDIT_CATEGORY_EVENT, QuestionHelper.getInstance().handleEditCategoryEvent);
			addEventListener(EventTypes.REMOVE_QUESTION_EVENT, QuestionHelper.getInstance().handleRemoveQuestionEvent);
			addEventListener(EventTypes.ADD_CHOICEITEM_EVENT, ChoiceItemHelper.getInstance().handleAddChoiceItemEvent);
			addEventListener(EventTypes.EDIT_CHOICEITEM_EVENT, ChoiceItemHelper.getInstance().handleEditChoiceItemEvent);
			addEventListener(EventTypes.REMOVE_CHOICEITEM_EVENT, ChoiceItemHelper.getInstance().handleRemoveChoiceItemEvent);
			addEventListener(EventTypes.SHOW_CHOICEITEM_EVENT, ChoiceItemHelper.getInstance().handleShowChoiceItemEvent);			
			addEventListener(EventTypes.SKIP_LOGIC_EVENT, QuestionHelper.getInstance().handleSkipLogicEvent);
			//Inicio Kivia Ramos
			addEventListener(EventTypes.ADD_CSV_ITEM_EVENT, ChoiceItemHelper.getInstance().handleAddCSVItemEvent);
			addEventListener(EventTypes.ADD_STRINGITEM_EVENT, QuestionHelper.getInstance().handleAddStringItemEvent);
			//Fim Kivia Ramos
			addEventListener(EventTypes.CLEAR_SKIP_LOGIC_EVENT, QuestionHelper.getInstance().handleClearSkipLogicEvent);
			addEventListener(EventTypes.MOVEDOWN_QUESTION_EVENT, TreeHelper.getInstance().handleMoveDownQuestionEvent);
			addEventListener(EventTypes.MOVEUP_QUESTION_EVENT, TreeHelper.getInstance().handleMoveUpQuestionEvent);
			addEventListener(EventTypes.ADD_ITEMTREE_EVENT, TreeHelper.getInstance().handleAddItemTree);
			addEventListener(EventTypes.SEARCH_ITEM_EVENT, TreeHelper.getInstance().handleSearchItem);
			addEventListener(EventTypes.CHOICE_ITEM_CLICK, ChoiceItemHelper.getInstance().handleClickChoiceItemCheckBox);

			addEventListener(EventTypes.CLONE_CATEGORY_EVENT, QuestionHelper.getInstance().handleCloneCategoryEvent);
		}

		public static function getInstance():FrontController {
			if (instance == null) {
				instance = new FrontController(new PrivateClass());
			}
			return instance;
		}

		public function dispatch(event:ControllerEvent):void {
            super.dispatchEvent(event);
        }
	}
	
}

class PrivateClass{
	public function PrivateClass(){};
}


