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

	
	import flash.events.Event;
	import flash.geom.Point;
	import flash.net.getClassByAlias;
	
	import main.br.org.indt.ndg.controller.access.SessionTimer;
	import main.br.org.indt.ndg.controller.editor.*;
	import main.br.org.indt.ndg.i18n.ConfigI18n;
	import main.br.org.indt.ndg.model.SurveyDTO;
	import main.br.org.indt.ndg.ui.view.editor.*;
	
	import mx.collections.ArrayCollection;
	import mx.containers.ViewStack;
	import mx.controls.Alert;
	import mx.controls.List;
	import mx.events.*;
	import mx.managers.PopUpManager;
	import mx.utils.StringUtil;
	
	[Bindable] private var searchOptionsFields:ArrayCollection = null;
	[Bindable] private var searchOptionsLabels:ArrayCollection = null;
	
	private static const REMOTE_SERVICE:String = "myService";
	private static const RESULT_PAGE_SIZE:int = 13;
	
	[Bindable] private var bModifiedSurvey:Boolean = false; // modified file flag 
	[Bindable] private var deployed:Boolean = false;
	private var lastState:String = ""; // hold the last state
	private var win:PreviewWin = null; // run survey preview window
	
	
	public static const CATEGORY_STATE:String = "Category";
	public static const CHOICE_EXCLUSIVE_STATE:String = "ChoiceExclusive";
	public static const CHOICE_EXCLUSIVE_RADIO_STATE:String = "ChoiceExRadio";
	public static const CHOICE_EXCLUSIVE_CHECK_STATE:String = "ChoiceExCheckBox";
	public static const DATE_STATE:String = "Date";
	public static const TIME_STATE:String = "Time";
	public static const INTEGER_STATE:String = "Integer";
	public static const IMAGE_STATE:String = "Image";
	public static const STRING_STATE:String = "String";
	public static const SURVEYNAME_STATE:String = "EditSurveyName";
	
	public var selectedAlternative:String = "";
	public var editClean:Boolean = false;
	public var myStack:ViewStack = null;

    private var fileRef:FileReference = new FileReference();
    //private var szValueTime:String = "";
    public static var szChoice:String = "";

	public var lstChoices:List  = null;


	private function init(mainGUIReference:Object):void {
		
		searchOptionsLabels = new ArrayCollection();
		searchOptionsLabels.addItem(ConfigI18n.getInstance().getStringFile("editorResources", "comboSearchAll"));
		searchOptionsLabels.addItem(ConfigI18n.getInstance().getStringFile("editorResources", "colResultId"));
		searchOptionsLabels.addItem(ConfigI18n.getInstance().getStringFile("editorResources", "colImei"));
	
		searchOptionsFields = new ArrayCollection();
		searchOptionsFields.addItem(new Array("idResult", "imei"));
		searchOptionsFields.addItem(new Array("idResult"));
		searchOptionsFields.addItem(new Array("imei"));
		
		// Set Alert Window Buttons
		Alert.yesLabel = ConfigI18n.getInstance().getStringFile("editorResources", "YES");
		Alert.noLabel = ConfigI18n.getInstance().getStringFile("editorResources", "NO");
		Alert.cancelLabel = ConfigI18n.getInstance().getStringFile("editorResources", "CANCEL");
		Alert.buttonWidth = 80;
		
	}		

	public function setChoiceList(isExclusive:Boolean) :void {
		if(isExclusive){
			lstChoices = lstChoicesRadio;
		}
		else{
			lstChoices = lstChoicesCheck;
		}
	}
	
	public function editSurvey(survey:SurveyDTO):void{
		lblTitle.text = ConfigI18n.getInstance().getStringFile('editorResources', 'EDIT_SURVEY_STATUS');
		lblSurveyName.text = survey.title;
		lblSurveyId.text = survey.idSurvey;
		deployed = (survey.isUploaded.toUpperCase() == "N") ? false : true;
		
		PersistenceHelper.getInstance().loadSurvey(survey.survey);
		questTree.populateTree();
		setModifiedSurvey(false);
		currentState = "";		
	}
	
	public function createNewSurvey():void{
		lblTitle.text = ConfigI18n.getInstance().getStringFile('editorResources', 'NEW_SURVEY_STATUS');
		deployed = false;
		PersistenceHelper.getInstance().createNewSurvey(loadTree);
		
		function loadTree():void{
			lblSurveyId.text = Survey.getInstance().getSurveyId();
			questTree.populateTree();
			setModifiedSurvey(true);
			currentState = "";
		}
	}
	
	private function backToSurveysList(event:MouseEvent):void{
		
		if (getModifiedSurvey()) {
			Alert.show(ConfigI18n.getInstance().getStringFile("editorResources", "SAVE_BEFORE_BACK"),
				ConfigI18n.getInstance().getStringFile("editorResources", "lblWarning"),
				(Alert.YES | Alert.NO | Alert.CANCEL), this, afterConfirmation);
		} else {
			back();			
		}
		
		function afterConfirmation(event:CloseEvent):void{
			if (event.detail == Alert.YES){
				if (canSave()){
					PersistenceHelper.getInstance().saveSurvey(back);
				}
			} else if (event.detail == Alert.NO){
				back();
			}
		}
		
		function back():void{
			myStack.selectedIndex = 0;
			resetView();
		}
	}
	
	private function resetView():void{
		
		setModifiedSurvey(false);
		
		//reset tree
		questTree.resetView();
	
		//reset preview
		this.previewQuestion.showMain(cmbDevice.text);
		
		//reset Survey Name//
		if (boxName != null){
			boxName.visible = true;
			boxName.includeInLayout = true;
		}
		if (boxEditName != null){
			boxEditName.visible = false;
			boxEditName.includeInLayout = false;
		}
		lblSurveyName.text = ConfigI18n.getInstance().getStringFile("editorResources", "SURVEY_NAME");
		lblSurveyId.text = "Survey ID";
		
		//reset search
		txtSearch.text = "";
	}
	
	private function clickSurveyName():void{
		boxName.visible = false;
		boxName.includeInLayout = false;
		
		txtSurveyName.text = lblSurveyName.text;
		txtSurveyName.setFocus();
		TextInputSelectField(txtSurveyName);
		
		boxEditName.visible = true;
		boxEditName.includeInLayout = true;
	}
	
	private function okSurveyName():void{
		if (txtSurveyName.text == ""){
			cancelEditSurveyName();
		} else{
			lblSurveyName.text = txtSurveyName.text;
			
			Survey.getInstance().setSurveyName(txtSurveyName.text);
			setModifiedSurvey(true);
			
			boxName.visible = true;
			boxName.includeInLayout = true;
			boxEditName.visible = false;
			boxEditName.includeInLayout = false;
		}
	}
	
	private function cancelEditSurveyName():void{
		boxName.visible = true;
		boxName.includeInLayout = true;
		boxEditName.visible = false;
		boxEditName.includeInLayout = false;
	}	
	
	private function save():void {
		if (canSave()){
			PersistenceHelper.getInstance().saveSurvey(successFunc);
		}
		
		function successFunc():void{
			lblTitle.text = ConfigI18n.getInstance().getStringFile('editorResources', 'EDIT_SURVEY_STATUS');
			setModifiedSurvey(false);
			Alert.show(ConfigI18n.getInstance().getStringFile("editorResources", "SAVE_SUCCESS"), 
				ConfigI18n.getInstance().getStringFile("editorResources", "lblSuccess"), 4);
		}
	}

	private function canSave():Boolean{
		var result:Boolean = false;
		if (Survey.getInstance().getSurveyDeployed() == true){
			Alert.show(ConfigI18n.getInstance().getStringFile("editorResources", "ALREADY_UPLOADED_SAVE"),
				ConfigI18n.getInstance().getStringFile("editorResources", "lblError"), 4);
		} else if (Survey.getInstance().getSurveyName() == ""){
			Alert.show(ConfigI18n.getInstance().getStringFile("editorResources", "TITLE_EMPTY_ERROR"),
				ConfigI18n.getInstance().getStringFile("editorResources", "lblError"), 4);
		} else if (Survey.getInstance().isEmpty()){
			Alert.show(ConfigI18n.getInstance().getStringFile("editorResources", "SURVEY_IS_EMPTY"),
			   ConfigI18n.getInstance().getStringFile("editorResources", "lblError"));
		} else{
			result = true;
		}
		return result;
	}


	private function saveAs():void {
		if (getModifiedSurvey()) {
			Alert.show(ConfigI18n.getInstance().getStringFile("editorResources", "SAVE_SURVEY_CHANGES")	+
				"  \"" + ConfigI18n.getInstance().getStringFile("editorResources", "SURVEY") +
				" ID: " + Survey.getInstance().getContent().@id + "\"",
				ConfigI18n.getInstance().getStringFile("editorResources", "lblWarning"),
				(Alert.YES | Alert.NO | Alert.CANCEL), this, closeHandler);
		} else {
			savveAs();
		}
		
		function closeHandler(event:Object):void{
			if (event.detail==Alert.YES){
				if (canSave()){
					PersistenceHelper.getInstance().saveSurvey(null);
					savveAs();
				}				
			} else if (event.detail==Alert.NO){
				if (Survey.getInstance().getSurveyName() == ""){
					Alert.show(ConfigI18n.getInstance().getStringFile("editorResources", "DUPLICATE_TITLE_EMPTY_ERROR"),
						ConfigI18n.getInstance().getStringFile("editorResources", "lblError"), 4);
				} else if (Survey.getInstance().isEmpty()){
					Alert.show(ConfigI18n.getInstance().getStringFile("editorResources", "DUPLICATE_SURVEY_IS_EMPTY"),
					   ConfigI18n.getInstance().getStringFile("editorResources", "lblError"));				
				} else{
					savveAs();	
				}
			}
		}
		
		function savveAs():void{
			PersistenceHelper.getInstance().saveSurveyAs(successFunc);
			
			function successFunc():void{
				deployed = false;
				lblTitle.text = ConfigI18n.getInstance().getStringFile('editorResources', 'EDIT_SURVEY_STATUS');
				lblSurveyId.text = Survey.getInstance().getSurveyId();
				setModifiedSurvey(false);
				Alert.show(ConfigI18n.getInstance().getStringFile("editorResources", "SAVE_DUPLICATE_SUCCESS"), 
					ConfigI18n.getInstance().getStringFile("editorResources", "lblSuccess"), 4);
			}
		}
	}
	
	private function makeAvailable():void {
		if (Survey.getInstance().getSurveyDeployed() == true){
			Alert.show(ConfigI18n.getInstance().getStringFile("editorResources", "ALREADY_UPLOADED"),
				ConfigI18n.getInstance().getStringFile("editorResources", "lblError"), 4);
		} else if (PersistenceHelper.getInstance().isSurveyNull()){
			Alert.show(ConfigI18n.getInstance().getStringFile("editorResources", "CANT_UPLOAD_EMPTY_SURVEY"),
					   ConfigI18n.getInstance().getStringFile("editorResources", "lblError"));			
		} else {	
			var categoriesName:Array = PersistenceHelper.getInstance().getEmptyCategories();
			if (categoriesName.length > 0){
				var message:String = ConfigI18n.getInstance().getStringFile("editorResources", "CATEGORIES_EMPTY_QUESTION");
				for (var i:int = 0; i < categoriesName.length; i++) {
					message += categoriesName[i];
					message += "\n";
				}
				Alert.show(message, ConfigI18n.getInstance().getStringFile("editorResources", "lblError"));
			} else {
				Alert.yesLabel = ConfigI18n.getInstance().getString("btnYes");
				Alert.noLabel = ConfigI18n.getInstance().getString("btnNo");
				Alert.show(ConfigI18n.getInstance().getStringFile("editorResources", "MAKE_AVAILABLE_CONFIRMATION"),
					ConfigI18n.getInstance().getString("lblQuestion"),
					(Alert.YES | Alert.NO), null, afterConfirmation, null, Alert.YES);
			}
		}
		
		function afterConfirmation(event:Object):void{
			if (event.detail==Alert.YES){
				if (getModifiedSurvey()){
					Alert.show(ConfigI18n.getInstance().getStringFile("editorResources", "NOT_SAVED_UPLOAD"),
						ConfigI18n.getInstance().getStringFile("editorResources", "lblWarning"),
						(Alert.YES | Alert.NO), null, saveBefore);
				} else{
					makkeAvailable();
				}
			}
		}
		
		function saveBefore(event:Object):void{
			if (event.detail==Alert.YES){
				if (Survey.getInstance().getSurveyName() == ""){
					Alert.show(ConfigI18n.getInstance().getStringFile("editorResources", "TITLE_EMPTY_ERROR"),
						ConfigI18n.getInstance().getStringFile("editorResources", "lblError"), 4);				
				} else{
					makkeAvailable();
				}
			}
		}
		function makkeAvailable():void{
			PersistenceHelper.getInstance().makeAvailable(successFunc);
			
			function successFunc():void{
				deployed = true;
				lblTitle.text = ConfigI18n.getInstance().getStringFile('editorResources', 'EDIT_SURVEY_STATUS');
				setModifiedSurvey(false);
				currentState = "";
				Alert.show(ConfigI18n.getInstance().getStringFile("editorResources", "UPLOAD_COMPLETE"),
					ConfigI18n.getInstance().getStringFile("editorResources", "lblSuccess"), 4);	
			}
		}
	}
	
	
	public function getModifiedSurvey():Boolean{
		return bModifiedSurvey;
	}
	public function setModifiedSurvey(bVal:Boolean):void{
		bModifiedSurvey = bVal;
		if (bModifiedSurvey){
			SessionTimer.getInstance().resetTimer();
		}
	}
	
	public static function TextAreaSelectField(area:TextArea):void{
		area.setSelection(0, area.text.length);
	}
	public static function TextInputSelectField(area:TextInput):void{
		area.setSelection(0, area.text.length);
	}
	
	private function initMultipleChoice():void{
		multipleChoice.setMainGUI(this);
	}

	private function showPreviewWindow():void{
		win = PopUpManager.createPopUp(this, PreviewWin, true) as PreviewWin;
		win.setMainView(getMainGUI());
		changeDevicePreview(false);
		PopUpManager.bringToFront(win);
	}

	/* Tree events --------------------------------------------------- */
	
	public function tree_ItemClick(event:ListEvent):void{
		cmbDeviceOnChange();
		
		// Save last selected item
		saveLastSelectedItem();
		
		var payload:Payload = new Payload();	
		payload.setView(this);
		payload.setEvent(event);
		
		dispatchControllerEvent(EventTypes.TREE_CLICK_EVENT, payload);
	}
	
	public function tree_ItemClose(event:TreeEvent):void{
        var node:XML = null;
        if (questTree.lastSelectedItem != null){
            node = ((questTree.lastSelectedItem) as XML).parent();
        }
        
        // Save last selected item
        saveLastSelectedItem();
        // Force selectedItem as parent
        questTree.selectedItem = node;
        
        var payload:Payload = new Payload();  
        payload.setView(this);
        payload.setEvent(event);
        
        dispatchControllerEvent(EventTypes.TREE_CLICK_EVENT, payload);
	}
	
	public function tree_ItemDbClick(event:MouseEvent):void{
		var payload:Payload = new Payload();
		payload.setView(this);
		payload.setEvent(event);
		dispatchControllerEvent(EventTypes.TREE_DOUBLECLICK_EVENT, payload);		
	}
	
	
	
	/* Question event ------------------------------------- */
	
	private function editStringQuestion(): void {
		cmbDeviceOnChange();
	
		var attributes:AttributeList = new AttributeList();
		attributes.setDescription(txtQuestion.text);
		attributes.setLength(txtQuestionString_Length.text);
		
		//this.txtPathStringFile.text = "";
				
		var payload:Payload = new Payload();
		payload.setQuestionAttribute(attributes);
		payload.setView(this);
		
		dispatchControllerEvent(EventTypes.EDIT_QUESTION_EVENT, payload);
	}
	
	public function editChoiceQuestion(): void{
		cmbDeviceOnChange();

		var attributes:AttributeList = new AttributeList();
		attributes.setDescription(txtQuestionExclusive.text);
		attributes.setExclusivity(ckBox_ChoiceExclusive.selected);
		
		this.txtPathFile.text = "";
		
		var payload:Payload = new Payload();
		payload.setQuestionAttribute(attributes);
		payload.setView(this);
		
		dispatchControllerEvent(EventTypes.EDIT_QUESTION_EVENT, payload);		
	}
	
	private function editIntegerQuestion(): void{
		cmbDeviceOnChange();
		
		var attributes:AttributeList = new AttributeList();
		attributes.setDescription(txtQuestionInteger.text);
		attributes.setLength(txtQuestionInteger_Length.text);
		attributes.setMinRange(txtMinRange_int.text);
		attributes.setMaxRange(txtMaxRange_int.text);
		attributes.setDecimalAllowed(radioDecimal.selected);

		var payload:Payload = new Payload();
		payload.setQuestionAttribute(attributes);
		payload.setView(this);
		
		dispatchControllerEvent(EventTypes.EDIT_QUESTION_EVENT, payload);		
	}
	
	private function editDateQuestion(): void{
		cmbDeviceOnChange();
		
		var attributes:AttributeList = new AttributeList();
		attributes.setDescription(txtQuestionDate.text);
		attributes.setMinRange(DateField.dateToString(txtMinRange_date.selectedDate, "DD/MM/YYYY"));
		attributes.setMaxRange(DateField.dateToString(txtMaxRange_date.selectedDate, "DD/MM/YYYY"));
				
		var payload:Payload = new Payload();
		payload.setQuestionAttribute(attributes);
		payload.setView(this);
		
		dispatchControllerEvent(EventTypes.EDIT_QUESTION_EVENT, payload);	
	}
	
	
	private function editTimeQuestion(): void{
		cmbDeviceOnChange();
		
		var attributes:AttributeList = new AttributeList();
		attributes.setDescription(txtQuestionTime.text);

		if (ckBox24_time.selected)
		   attributes.setFormatTime("24");
		else if (ckBoxAMPM_time.selected)
		   attributes.setFormatTime("12");
		else
		   attributes.setFormatTime("24");

		var payload:Payload = new Payload();
		payload.setQuestionAttribute(attributes);
		payload.setView(this);
		
		dispatchControllerEvent(EventTypes.EDIT_QUESTION_EVENT, payload);	
	}
	
	private function editImageQuestion(): void{
		cmbDeviceOnChange();
		
		var attributes:AttributeList = new AttributeList();
		attributes.setDescription(txtQuestion_img.text);
		attributes.setImageMaxCount(numericImageCount.value);
				
		var payload:Payload = new Payload();
		payload.setQuestionAttribute(attributes);
		payload.setView(this);
		
		dispatchControllerEvent(EventTypes.EDIT_QUESTION_EVENT, payload);	
	}
	
	private function editCategory(): void{
		var payload:Payload = new Payload();
		payload.setCategoryText(txtCategory.text);
		if( conditionDisabledRadioButton.selected ) {
			payload.setCategoryConditionText(null);
		}
		else
		{
			payload.setCategoryConditionText(conditionQuestion.text);
		}
		payload.setView(this);
		
		dispatchControllerEvent(EventTypes.EDIT_CATEGORY_EVENT, payload);
		
		cmbDeviceOnChange();
	}	
	
	
	
	/* move up, move down, delete question ------------------- */
	
	private function removeQuestion(): void	{
		var payload:Payload = new Payload();
		payload.setView(this);
		dispatchControllerEvent(EventTypes.REMOVE_QUESTION_EVENT, payload);	
	}
	
	private function moveDownQuestion():void{
		var payload:Payload = new Payload();
		payload.setView(this);
		dispatchControllerEvent(EventTypes.MOVEDOWN_QUESTION_EVENT, payload);
	}
	
	private function moveUpQuestion():void{
		var payload:Payload = new Payload();
		payload.setView(this);
		dispatchControllerEvent(EventTypes.MOVEUP_QUESTION_EVENT, payload);
	}
	
	
	
	/* choice item --------------------------------- */
	
	private function deleteChoiceItem():void{
		if (lstChoices.selectedIndex >= 0){
			if (lstChoices.dataProvider.length <= 2){
				Alert.show(ConfigI18n.getInstance().getStringFile("editorResources", "CHOICE_ITEM_WARNING"),
					ConfigI18n.getInstance().getStringFile("editorResources", "lblWarning"), 4, this);
				return;
			}
			
			var attributes:AttributeList = new AttributeList();
			attributes.setSelectedChoiceItem(lstChoices.selectedIndex);
			
			var payload:Payload = new Payload();
			payload.setQuestionAttribute(attributes);
			payload.setView(this);
	
			var event:ControllerEvent = new ControllerEvent(EventTypes.REMOVE_CHOICEITEM_EVENT, payload);
			FrontController.getInstance().dispatch(event);
		} else {
			Alert.show(ConfigI18n.getInstance().getStringFile("editorResources", "CHOICEITEM_NOT_SELECTED"), 
			ConfigI18n.getInstance().getStringFile("editorResources", "lblError"), 4, this);
		}
	}
	
	private function addChoiceItem():void
	{		
		var payload:Payload = new Payload();
		payload.setView(this);
		var event:ControllerEvent = new ControllerEvent(EventTypes.ADD_CHOICEITEM_EVENT, payload);
		FrontController.getInstance().dispatch(event);
		
	}
	
	//Inicio Kivia Ramos - Search, Upload CSV File and Clear List
	
    private function searchCSV():void
	{		
		import flash.net.FileReference;

        var csvTypes:FileFilter = new FileFilter("CSV Files (*.csv)", "*.csv");
        var allTypes:Array = new Array(csvTypes);
        fileRef.browse(allTypes);
        // rdiniz
		fileRef.addEventListener(Event.SELECT, handleSelectedFile);
		fileRef.addEventListener(Event.COMPLETE, completeHandler);
    } 	
    
    private function searchStringCSV():void
	{		
		import flash.net.FileReference;

        var csvStringTypes:FileFilter = new FileFilter("CSV Files (*.csv)", "*.csv");
        var allStringTypes:Array = new Array(csvStringTypes);
        fileRef.browse(allStringTypes);
        // rdiniz
		fileRef.addEventListener(Event.SELECT, handleSelectedFile);
		fileRef.addEventListener(Event.COMPLETE, completeStringHandler);
    }
    
    private function completeStringHandler(event:Event):void 
    { 
       //this.txtPathStringFile.text = fileRef.name;
       
       var importFileLines:Array = event.target.data.toString().split("\n");
       var szData:String = "";
       for (var i:int = 0; i < importFileLines.length; i++) 
       {
       	  szData = importFileLines[i].toString();
       	  
       	  if ( szData.charCodeAt(0) == 34 )
       	  {
            szChoice = szData.substr(1,szData.length-2);
       	  }
       	  else if ( szData == "" )
       	  {
       	  	break;
       	  }
       	  else
       	  {
       	    szChoice = szData;
       	  }
       	  
       	  addStringItem(szChoice);
       	  
       }
    }  
    
	//rdiniz
	private function handleSelectedFile(event:Event):void 
	{
       try 
       {
          fileRef.load(); 
       }
       catch (error:IOError) 
       {
          trace ("IOError to load requested document."); 
       }  
       catch (error:Error) 
       {
          trace ("Unable to load requested document."); 
       } 
	}

	
    private function completeHandler(event:Event):void 
    { 
       // ";" deve ser um constante
       this.txtPathFile.text = fileRef.name;
       
       var importFileLines:Array = event.target.data.toString().split("\n");
       var szData:String = "";
       for (var i:int = 0; i < importFileLines.length; i++) 
       {
       	  szData = importFileLines[i].toString();
       	  
       	  if ( szData.charCodeAt(0) == 34 )
       	  {
            szChoice = szData.substr(1,szData.length-2);
       	  }
       	  else if ( szData == "" )
       	  {
       	  	break;
       	  }
       	  else
       	  {
       	    szChoice = szData;
       	  }
       	  
       	  addCSVItem(szChoice);
       }
       
       for (var x:int = 0; x < 2; x++)
       {
          if (lstChoices.dataProvider[0].toString().substr(0,6) == "Choice")
          {
		     var attributes:AttributeList = new AttributeList();
		     attributes.setSelectedChoiceItem(0);
			
		     var payload:Payload = new Payload();
		     payload.setQuestionAttribute(attributes);
		     payload.setView(this);
	
		     var event1:ControllerEvent = new ControllerEvent(EventTypes.REMOVE_CHOICEITEM_EVENT, payload);
		     FrontController.getInstance().dispatch(event1);
          }
       }
       
    } 
    
    private function addCSVItem(szChoice:String):void
	{		
		var payload:Payload = new Payload();
		payload.setView(this);
		payload.setChoice(szChoice);
		var event:ControllerEvent = new ControllerEvent(EventTypes.ADD_CSV_ITEM_EVENT, payload);
		FrontController.getInstance().dispatch(event);
	}
	
	private function addStringItem(szString:String):void
	{		
		var payload:Payload = new Payload();
		payload.setView(this);
		payload.setChoice(szString);
		var event:ControllerEvent = new ControllerEvent(EventTypes.ADD_STRINGITEM_EVENT, payload);
		FrontController.getInstance().dispatch(event);
	}
	
	private function clearChoiceItem():void
	{
		for (var i:int = lstChoices.dataProvider.length; i >= 0; i--) 
		{
			var attributes:AttributeList = new AttributeList();
			attributes.setSelectedChoiceItem(i);
			
			var payload:Payload = new Payload();
			payload.setQuestionAttribute(attributes);
			payload.setView(this);
	
			var event:ControllerEvent = new ControllerEvent(EventTypes.REMOVE_CHOICEITEM_EVENT, payload);
			FrontController.getInstance().dispatch(event);
		}
		
		addChoiceItem();
		addChoiceItem();
	}
	
	//Fim Kivia Ramos
	
	private function showChoiceItem():void{
		if (lstChoices.selectedIndex >= 0){
			selectedAlternative = lstChoices.selectedItem.toString();

			var attributes:AttributeList = new AttributeList();
			attributes.setSelectedChoiceItem(lstChoices.selectedIndex);
			editClean = true;
			//attributes.setDescription(selectedAlternative);
			
			// Get mouse position
			var point1:Point = new Point();
			point1.x = 21;
			point1.y = lstChoices.contentMouseY + 110;
			attributes.setMousePos(point1);
			
			var payload:Payload = new Payload();
			payload.setQuestionAttribute(attributes);
			payload.setView(this);
			
			//var event:ControllerEvent = new ControllerEvent(EventTypes.SHOW_CHOICEITEM_EVENT, payload);
			//FrontController.getInstance().dispatch(event);
			
			dispatchControllerEvent(EventTypes.SHOW_CHOICEITEM_EVENT, payload);
		} else {
			Alert.show(ConfigI18n.getInstance().getStringFile("editorResources", "CHOICEITEM_NOT_SELECTED"), 
				ConfigI18n.getInstance().getStringFile("editorResources", "lblError"), 4, this);
		}
	}
	
	private function onMultipleChoiceShow():void{
		// SetFocus and select text
		multipleChoice.txtChoiceItem.setFocus();
		TextInputSelectField(multipleChoice.txtChoiceItem);
		// Set visible for Skip Logic visual components
		multipleChoice.lbl_SkipTxt.visible = ckBox_ChoiceExclusive.selected;
		multipleChoice.lbl_Skip.visible = ckBox_ChoiceExclusive.selected;
		multipleChoice.ckBox_Inverse.visible = ckBox_ChoiceExclusive.selected;
		multipleChoice.btn_Add.visible = ckBox_ChoiceExclusive.selected;
		// Set window size
		ckBox_ChoiceExclusive.selected ? vboxAlternative.height = 173 : vboxAlternative.height = 100;
	}	
	
	
	
	/* int and date ranges ---------------------------- */
	private function minRangeClick_int():void{
		txtMinRange_int.enabled = ckBoxMinRange_int.selected;
		txtMinRange_int.enabled ? txtMinRange_int.setFocus() : txtMinRange_int.text = "";
	}
	
	private function maxRangeClick_int():void{
		txtMaxRange_int.enabled = ckBoxMaxRange_int.selected;
		txtMaxRange_int.enabled ? txtMaxRange_int.setFocus() : txtMaxRange_int.text = "";
	}
	
	private function minRangeClick_date():void{
		txtMinRange_date.enabled = ckBoxMinRange_date.selected;
		txtMinRange_date.enabled ? txtMinRange_date.setFocus() : txtMinRange_date.selectedDate = null;
	}
	
	private function maxRangeClick_date():void{
		txtMaxRange_date.enabled = ckBoxMaxRange_date.selected;
		txtMaxRange_date.enabled ? txtMaxRange_date.setFocus() : txtMaxRange_date.selectedDate = null;
	}
	
	/* icons --------------------------- */
	
	[Embed("../../../../../../../resources/images/editor/string-tmp-16x16.png")] private var icoStrType:Class;
	[Embed("../../../../../../../resources/images/editor/int-tmp-16x16.png")] private var icoIntType:Class;
	[Embed("../../../../../../../resources/images/editor/date-tmp-16x16.png")] private var icoDateType:Class;
	[Embed("../../../../../../../resources/images/editor/time-tmp-16x16.png")] private var icoTimeType:Class;
	[Embed("../../../../../../../resources/images/editor/image-tmp-16x16.png")] private var icoImageType:Class;
	[Embed("../../../../../../../resources/images/editor/choice-tmp-16x16.png")] private var icoChoiceMultType:Class;
	[Embed("../../../../../../../resources/images/editor/choice-excl-tmp-16x16.png")] private var icoChoiceExclType:Class;
	
	private function setIcons(item:Object):Class {
		var questType:String = XML(item).attribute("type");
		if (questType == Question.STRING_TYPE){
			return icoStrType;
		} else if ((questType == Question.INTEGER_TYPE) || (questType == Question.DECIMAL_TYPE)) {
			return icoIntType;
		} else if (questType == Question.DATE_TYPE) {
			return icoDateType;
        } else if (questType == Question.TIME_TYPE) {
			return icoTimeType;			
		} else if (questType == Question.IMAGE_TYPE) {
			return icoImageType;
		} else if (questType == Question.CHOICE_TYPE) {
			var strTemp:String = XML(item).select;
			if (strTemp == "exclusive") {
				return icoChoiceExclType;
			} else {
				return icoChoiceMultType; 
			}
		}
		return null;
	}	
	
	
	
	/* other methods ---------------------------- */
	
	private function saveLastSelectedItem():void{
		if (questTree.lastSelectedItem != null){
			var nodeName:String = (questTree.lastSelectedItem as XML).localName() as String;
			if (nodeName == TreeHelper.NODE_QUESTION){
				var selectedNode:XML = questTree.lastSelectedItem as XML;
				if (selectedNode.@type == Question.STRING_TYPE){
					if (isQuestionValid(selectedNode.@type)) editStringQuestion();
				} else if ((selectedNode.@type == Question.CHOICE_EXCLUSIVE_TYPE) || (selectedNode.@type == Question.CHOICE_TYPE)){
					if (isQuestionValid(selectedNode.@type)) editChoiceQuestion();
					vboxAlternative.visible = false;
				} else if ((selectedNode.@type == Question.INTEGER_TYPE) || (selectedNode.@type == Question.DECIMAL_TYPE)){
					if (isQuestionValid(selectedNode.@type)) editIntegerQuestion();
				} else if (selectedNode.@type == Question.DATE_TYPE) {
					if (isQuestionValid(selectedNode.@type)) editDateQuestion();
				} else if (selectedNode.@type == Question.TIME_TYPE) {
					if (isQuestionValid(selectedNode.@type)) editTimeQuestion();	
				} else if (selectedNode.@type == Question.IMAGE_TYPE){
					if (isQuestionValid(selectedNode.@type)) editImageQuestion();
				}
			} else if(nodeName == TreeHelper.NODE_CATEGORY) {
				if (btnCategoryOk.enabled) editCategory();
			}
		}
		questTree.lastSelectedItem = questTree.selectedItem;
	}
	

	
	
	private function dispatchControllerEvent(type:String, payload:Payload):void	{
		var event:ControllerEvent = new ControllerEvent(type, payload);
		FrontController.getInstance().dispatch(event);
	}



	private function onKeyUp_quest(event:KeyboardEvent, nIndex:int):void
	{
		if (event.keyCode == 13) // Enter KEY
		{
			switch (nIndex)
			{
				case 0: if (btnCategoryOk.enabled) editCategory(); break;
				case Question.QUESTION_STRING: if (btnOk_StringQuestion.enabled) editStringQuestion(); break;
				case Question.QUESTION_CHOICE: if (btnOK_EditChoiceQuestion.enabled) editChoiceQuestion(); break;
				case Question.QUESTION_INTEGER: if (btnOk_IntegerQuestion.enabled) editIntegerQuestion(); break;
				case Question.QUESTION_DATE: if (btnOk_DateQuestion.enabled) editDateQuestion(); break;
				case Question.QUESTION_TIME: if (btnOk_TimeQuestion.enabled) editTimeQuestion(); break;
				case Question.QUESTION_IMAGE: if (btnOk_ImageQuestion.enabled) editImageQuestion(); break;
			}
		}
		else if (event.keyCode == 27) // ESC Key
		{
			var tmpEvent:ListEvent;
			questTree.lastSelectedItem = null;
			tree_ItemClick(tmpEvent);
		}
	}
	
	private function onEnter_search(event:Event):void
	{
		// Execute search
		var payload:Payload = new Payload();
		payload.setView(this);
		payload.setExtraView(questTree);
		dispatchControllerEvent(EventTypes.SEARCH_ITEM_EVENT, payload);
	}
	
	private function textArea_textInput(evt:TextEvent):void 
	{
        if (evt.text == "\n")
        {
            evt.preventDefault();
        }
    }
    
    private function txtQuestionFocus(event:Event, nIndex:int):void
    {
    	if ( (event.type == flash.events.FocusEvent.FOCUS_IN) || (event.type == flash.events.MouseEvent.CLICK) )
    	{
	    	switch (nIndex)
			{
				case 0: if (txtCategory.text == Category.CATEGORY_DEFAULT_DISPLAY_NAME) EditorEditSurveys.TextAreaSelectField(txtCategory); break;
				case Question.QUESTION_STRING: if (txtQuestion.text == Question.QUESTION_DEFAULT_DISPLAY_NAME) EditorEditSurveys.TextAreaSelectField(txtQuestion); break;
				case Question.QUESTION_CHOICE: if (txtQuestionExclusive.text == Question.QUESTION_DEFAULT_DISPLAY_NAME) EditorEditSurveys.TextAreaSelectField(txtQuestionExclusive); break;
				case Question.QUESTION_INTEGER: if (txtQuestionInteger.text == Question.QUESTION_DEFAULT_DISPLAY_NAME) EditorEditSurveys.TextAreaSelectField(txtQuestionInteger); break;
				case Question.QUESTION_DATE: if (txtQuestionDate.text == Question.QUESTION_DEFAULT_DISPLAY_NAME) EditorEditSurveys.TextAreaSelectField(txtQuestionDate); break;
				case Question.QUESTION_TIME: if (txtQuestionTime.text == Question.QUESTION_DEFAULT_DISPLAY_NAME) EditorEditSurveys.TextAreaSelectField(txtQuestionTime); break;
				case Question.QUESTION_IMAGE: if (txtQuestion_img.text == Question.QUESTION_DEFAULT_DISPLAY_NAME) EditorEditSurveys.TextAreaSelectField(txtQuestion_img); break;
			}
    	}
    	else if (event.type == flash.events.FocusEvent.FOCUS_OUT)
    	{
    		switch (nIndex)
			{
				case 0: if (StringUtil.trim(txtCategory.text).length == 0) txtCategory.text = Category.CATEGORY_DEFAULT_DISPLAY_NAME; break;
				case Question.QUESTION_STRING: if (StringUtil.trim(txtQuestion.text).length == 0) txtQuestion.text = Question.QUESTION_DEFAULT_DISPLAY_NAME; break;
				case Question.QUESTION_CHOICE: if (StringUtil.trim(txtQuestionExclusive.text).length == 0) txtQuestionExclusive.text = Question.QUESTION_DEFAULT_DISPLAY_NAME; break;
				case Question.QUESTION_INTEGER: if (StringUtil.trim(txtQuestionInteger.text).length == 0) txtQuestionInteger.text = Question.QUESTION_DEFAULT_DISPLAY_NAME; break;
				case Question.QUESTION_DATE: if (StringUtil.trim(txtQuestionDate.text).length == 0) txtQuestionDate.text = Question.QUESTION_DEFAULT_DISPLAY_NAME; break;
				case Question.QUESTION_TIME: if (StringUtil.trim(txtQuestionTime.text).length == 0) txtQuestionTime.text = Question.QUESTION_DEFAULT_DISPLAY_NAME; break;
				case Question.QUESTION_IMAGE: if (StringUtil.trim(txtQuestion_img.text).length == 0) txtQuestion_img.text = Question.QUESTION_DEFAULT_DISPLAY_NAME; break;
			}
    	}
    }
    
	
	private function isQuestionValid(type:String):Boolean
	{
		var result:Boolean = false;
		var valResult:ValidationResultEvent;
        if (type == Question.STRING_TYPE)
        {
            _strValidator_question.validate();
            if (btnOk_StringQuestion.enabled)
            {
            	_strValidator_length.validate();
            	if (btnOk_StringQuestion.enabled)
            	{
            		result = true;
            	}
            }
        }
        else if ( (type == Question.CHOICE_EXCLUSIVE_TYPE) || (type == Question.CHOICE_TYPE) )
        {
            _choiceValidator_question.validate();
            if (btnOK_EditChoiceQuestion.enabled)
            {
                result = true;
            }
        }
        else if ( (type == Question.INTEGER_TYPE) || (type == Question.DECIMAL_TYPE) )
        {
            _intValidator_question.validate()
            if (btnOk_IntegerQuestion.enabled)
            {
                _intValidator_length.validate();
                if (btnOk_IntegerQuestion.enabled)
                {
                    if (radioInt.selected == true) // Validate using Integers validators
                    {
	                    _intValidator_minRange.validate();
		                if (btnOk_IntegerQuestion.enabled)
		                {
		                	_intValidator_maxRange.validate();
		                    if (btnOk_IntegerQuestion.enabled)
		                    {
		                    	result = true;
		                    }
		                }
                   }
                   else if (radioDecimal.selected == true) // Validate using Decimals validators
                   {
                        _intValidator_minRange_Decimal.validate();
                        if (btnOk_IntegerQuestion.enabled)
                        {
                            _intValidator_maxRange_Decimal.validate();
                            if (btnOk_IntegerQuestion.enabled)
                            {
                                result = true;
                            }
                        }
                   }
                }
            }
        }
        else if (type == Question.DATE_TYPE)
        {
            _dateValidator_question.validate();
            
            if (btnOk_DateQuestion.enabled)
            {
                result = true;
            }
        }
        else if (type == Question.TIME_TYPE)
        {
            _timeValidator_question.validate();
            
            if (btnOk_TimeQuestion.enabled)
            {
                result = true;
            }
        }
        else if (type == Question.IMAGE_TYPE)
        {
            _imageValidator_question.validate();
            
            if (btnOk_ImageQuestion.enabled)
            {
            	result = true;
            }
        }
		
		return result;
	}
	
    private function handleNumberTypeChange(event:ItemClickEvent):void
    {
    	if (event.currentTarget.selectedValue == "int") 
    	   isQuestionValid(Question.INTEGER_TYPE)
    	else
    	   isQuestionValid(Question.DECIMAL_TYPE);
    }
    
    public function cmbDeviceOnChange():void
	{
		changeDevicePreview(true);
	}
	
	private function changeDevicePreview(isMiniPreview: Boolean):void {
		if (isMiniPreview){
			if (cmbDevice.selectedLabel == "E61"){
				previewQuestion.imgMobileE61.visible = true;
				previewQuestion.imgMobileE71.visible = false;
				adjustPreviewItems(isMiniPreview, 1, 0, 0);
			} else if (cmbDevice.selectedLabel == "E71") {
				previewQuestion.imgMobileE61.visible = false;
				previewQuestion.imgMobileE71.visible = true;
				adjustPreviewItems(isMiniPreview, 0.84, 27, 23);
			}
		} else if (win != null) { 
			if (cmbDevice.selectedLabel == "E61"){
				win.previewQuestion.imgMobileE61.visible = true;
				win.previewQuestion.imgMobileE71.visible = false;
				adjustPreviewItems(isMiniPreview, 1, 0, 0);
			} else if (cmbDevice.selectedLabel == "E71") {
				win.previewQuestion.imgMobileE61.visible = false;
				win.previewQuestion.imgMobileE71.visible = true;
				adjustPreviewItems(isMiniPreview, 0.84, 27, 23);
			}
		}
	}
	
	private function adjustPreviewItems(isMiniPreview: Boolean, scale: Number, positionX: Number, positionY: Number):void{
		if (isMiniPreview){
			// scale items
			previewQuestion.cScreen.scaleX = scale;
			previewQuestion.cScreen.scaleY = scale;
			// move items
			previewQuestion.cScreen.move(positionX, positionY);
		} else if (win != null)	{
			// scale items
			win.previewQuestion.cScreen.scaleX = scale;
			win.previewQuestion.cScreen.scaleY = scale;
			// move items
			win.previewQuestion.cScreen.move(positionX, positionY);
		}
	}

	private function selectInputCategory(): void {
		var payload:Payload = new Payload();
		payload.setCategoryText(txtCategory.text);
		payload.setView(this);

		dispatchControllerEvent(EventTypes.CLONE_CATEGORY_EVENT, payload);
		cmbDeviceOnChange();
	}
	

	private function conditionalCategoryClickHandler(event:ItemClickEvent):void
	{
		conditionQuestion.enabled = conditionEnabledRadioButton.selected;
	}
