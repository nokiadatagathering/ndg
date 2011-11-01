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

package main.br.org.indt.ndg.controller.editor
{
	import main.br.org.indt.ndg.i18n.ConfigI18n;
	
	import mx.collections.ArrayCollection;
	import mx.controls.Alert;
	
	public class Category
	{		
		public static const CATEGORY_DEFAULT_DISPLAY_NAME:String = ConfigI18n.getInstance().getStringFile("editorResources", "NEW_CATEGORY");
		private var content:XML;		
		
		private var aQuestions:Array = new Array();
		private static var lastIndexAvailable:int = 0;
		
		public function Category()
		{
		}

		public static function create(name:String, id:int):Category
		{
			var category:Category = new Category();
			category.content = <category id={id} name={name}></category>;
			return category;
		}
		
		public static function clone(node:XML):Category
		{
			var self:Category = new Category();
			self.content = node.copy();
			self.content.@id = Category.getNewIndexForCategory();
			self.content.@name = node.@name + self.content.@id;
			self.updateSkipLogic(node.@id);
			return self;
		}

		private function updateSkipLogic(clonedId:int):void
		{
			var questions:XMLList = XMLList(content.question);
			var questionsLength:int = questions.length();

			for (var i:int=0; i < questionsLength; i++)
			{
				var skipLogicList:XMLList = questions[i].SkipLogic;
				if(skipLogicList != null && skipLogicList.length() > 0)
				{
					if(skipLogicList[0].@catTo == clonedId)//maintain intercategory skiplogic
					{
						skipLogicList[0].@catTo = this.content.@id
					}
					else//remove skiplogic to external categories
					{
						delete questions[i].SkipLogic;
					}
				}
			}
		}

		public static function appendQuestion(category:XML, question:Question):void
		{
			if (category.localName() == "category")
			{	
				question.setContent(Category.getNewIndexForQuestion(category));
				category.appendChild(question.getContent());
			}
			else
			{
				throw new ArgumentError("Parameter is not a Category");
			}
		}
		
		public function getContent():XML
		{
			return content;
		}
		
		public static function getQuestionsCountByCategory(category:XML):int
		{
			if (category.localName() == "category")
			{
				return category.children().length();
			}
			else
			{
				throw new ArgumentError("Node is not a Category");
			}
		}
		
		public static function getQuestion(categoryId:int, questionId:int):XML
        {
        	var surveyContent:XML = Survey.getInstance().getContent();
        	var skipToQuestion:XML = null;
        	var category:XMLList = XMLList(surveyContent.category);
		    var length:int = category.length();

		    for (var i:int=0; i < length; i++)
		    {
				if (category[i].@id == categoryId)
				{
					var questions:XMLList = XMLList(category[i].question);
				    var questionsLength:int = questions.length();
				    
				    for (var j:int=0; j < questionsLength; j++)
				    {
				    	if (questions[j].@id == questionId)
				       	{
				       		skipToQuestion = questions[j];
				       	}				       		
				    }			       
				 }				  
		     }
		     
		     return skipToQuestion;      	
        }
        
        public static function removeQuestion(node:XML): void
		{
			if (node.localName() == "question")
			{
				 var children:XMLList = XMLList(node.parent()).children();
			     var length:int = children.length();
			     
			     for (var i:int=0; i < length; i++)
			     {
					if (children[i].@id == node.@id)
					{
						// Re-indexar todos as questões seguintes na categoria (Posicional)
						for (var j:int=(i+1); j < length; j++)
						{
							children[j].@id = children[j].@id - 1; 
						}
						
						delete children[i];
						break;
					}				  
				}
			}
			else
			{
				throw new ArgumentError("Parameter is not a Question");
			}
		}
		
		//static function resetIndex():void
		//{
		//	lastIndexAvailable = 1;
		//}
		
		public function getId():int
		{
			return content.@id;
		}
		
		public function getQuestionById(id:int):Question
		{
			var length:int = aQuestions.length;
			var question:Question;
			for(var i:int = 0; i < length; i++)
			{
				question = aQuestions[i] as Question;
				if(question.getId() == id)
				return question;
			}
			return null;
		}

		// O indice não pode ser simplesmente sequencial, tem que ser posicional
		//public static function getNewIndex():int
		//{
		//	return ++lastIndexAvailable;
		//}
		
		// Indice posicional para Categoria.
		public static function getNewIndexForCategory():int
		{
			var rootNode: XML = Survey.getInstance().getContent();
			var catChildren:XMLList = XMLList(rootNode.category);
			var numberOfCategories:int = catChildren.length();
			return (numberOfCategories + 1);
		}
		
		// Indice posicional para Questão.
		public static function getNewIndexForQuestion(node: XML):int
		{
			var questChildren:XMLList = XMLList(node).children();
			var numberOfQuestions:int = questChildren.length();
			return (numberOfQuestions + 1);
		}
		
		public static function removeCategory(node:XML): int
		{
			var rootNode:XML = node.parent();
			var catChildren:XMLList = XMLList(rootNode).children();
		    var numberOfCategories:int = catChildren.length();
		    for(var iCategory:int=0; iCategory < numberOfCategories; iCategory++)
		    {
			  if( catChildren[iCategory].@id == node.@id )
			  {
					var questChildren:XMLList = XMLList(catChildren[iCategory]).children();
					var numberOfQuestions:int = questChildren.length();
					for (var iQuestion:int=0; iQuestion < numberOfQuestions; iQuestion++)
					{
						var aReferences:ArrayCollection = Category.checkSkipLogicReferences(questChildren[iQuestion], catChildren[iCategory].@id);
						if (aReferences.length > 0)
								removeSkipLogicReferences(aReferences);
					}
					
					// Re-indexar todos as categorias seguintes no survey (Posicional)
					for (var j:int=(iCategory+1); j < numberOfCategories; j++)
			        {
			       	   catChildren[j].@id = catChildren[j].@id - 1; 
			        }
					///////////////////////////////////////////////////
					
					//delete catChildren[iCategory];
					break;
			  }				  
		    }
		    return iCategory;
		}
		
		public static function calculateNewIndex():void
		{
			var higherIndex:int = 0;
			var rootNode: XML = Survey.getInstance().getContent();
			var catChildren:XMLList = XMLList(rootNode.category);
		    var numberOfCategories:int = catChildren.length();
		    for (var iCategory:int=0; iCategory < numberOfCategories; iCategory++)
		    {
		    	var questChildren:XMLList = XMLList(catChildren[iCategory]).children();
		    	var numberOfQuestions:int = questChildren.length();
		    	for (var iQuestion:int=0; iQuestion < numberOfQuestions; iQuestion++)
		    	{
		    		if (questChildren[iQuestion].@id > higherIndex)
		    			higherIndex = questChildren[iQuestion].@id; 
		    	}
		    }
		    
		    lastIndexAvailable = higherIndex;
		}
        
        public static function checkSkipLogicReferences(node:XML, catIndex:int):ArrayCollection
        {
        	var aReferences:ArrayCollection = new ArrayCollection();
        	aReferences.removeAll();
        	
        	var rootNode: XML = Survey.getInstance().getContent();
			var catChildren:XMLList = XMLList(rootNode.category);
		    var numberOfCategories:int = catChildren.length();
		    for (var iCategory:int=0; iCategory < numberOfCategories; iCategory++)
		    {
		    	var questChildren:XMLList = XMLList(catChildren[iCategory]).children();
		    	var numberOfQuestions:int = questChildren.length();
		    	for (var iQuestion:int=0; iQuestion < numberOfQuestions; iQuestion++)
		    	{
		    		if ( (questChildren[iQuestion].SkipLogic.@skipTo == node.@id) && (questChildren[iQuestion].SkipLogic.@catTo == catIndex))
		    		{
		    			aReferences.addItem(new Array(questChildren[iQuestion].parent().@id ,questChildren[iQuestion].@id));
		    		} 
		    	}
		    }
        	
        	return aReferences;
        }
        
        public static function removeSkipLogicReferences(aReferences:ArrayCollection):void
        {
        	var aNode:Array;
        	for (var i:int=0; i < aReferences.length; i++)
        	{
        		aNode = aReferences.getItemAt(i) as Array;
        		var node:XML = getQuestion(aNode[0], aNode[1]);
        		delete node.SkipLogic;
        	}
        	
        }
        
        public static function moveDownQuestion(node:XML):void
        {
			 var tmpNode:XML = node;
			 var children:XMLList = XMLList(node.parent()).children();
		     var length:int = children.length();
		     for(var i:int=0; i < length; i++)
		     {
				  if( children[i].@id == node.@id )
				  {
				       if (children[i+1] != null)
				       {
				       		// Changing places
				       		children[i] = new XML(children[i+1]);
				       		children[i+1] = tmpNode;
				       		
				       		// Fix ID's
				       		var tempID:int = children[i].@id;
				       		children[i].@id = children[i+1].@id;
				       		children[i+1].@id = tempID;
				       }
				       break;
				  }				  
		     }
        }
        
        public static function moveUpQuestion(node:XML):void
        {
			 var tmpNode:XML = node;
			 var children:XMLList = XMLList(node.parent()).children();
		     var length:int = children.length();
		     for(var i:int=0; i < length; i++)
		     {
				  if( children[i].@id == node.@id )
				  {
				       if ( ((i-1) >= 0) && (children[i-1] != null) )
				       {
				       		// Changing places
				       		children[i] = new XML(children[i-1]);
				       		children[i-1] = tmpNode;
				       		
				       		// Fix ID's
				       		var tempID:int = children[i].@id;
				       		children[i].@id = children[i-1].@id;
				       		children[i-1].@id = tempID;
				       }
				       break;
				  }				  
		     }
        }
		
		//color="#BAC0C1" (mx:TextArea)
	}
}