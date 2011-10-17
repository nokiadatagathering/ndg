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

// ActionScript file


	import flash.display.DisplayObject;
	import flash.events.Event;
	import flash.events.MouseEvent;

	import main.br.org.indt.ndg.controller.access.SessionClass;
	import main.br.org.indt.ndg.controller.access.SessionTimer;
	import main.br.org.indt.ndg.controller.main.resultimport.CloseResultImportEvent;
	import main.br.org.indt.ndg.controller.util.ExceptionUtil;
	import main.br.org.indt.ndg.i18n.ConfigI18n;
	import main.br.org.indt.ndg.model.ResultDTO;
	import main.br.org.indt.ndg.model.SurveyDTO;
	import main.br.org.indt.ndg.model.SurveyPreviewDTO;
	import main.br.org.indt.ndg.ui.component.image.ImageViewer;
	import main.br.org.indt.ndg.ui.component.powerdatagrid.ChangePageEvent;
	import main.br.org.indt.ndg.ui.view.main.resultMap.ResultMap;
	import main.br.org.indt.ndg.ui.view.main.resultexport.ResultExport;
	import main.br.org.indt.ndg.ui.view.main.resultimport.ResultImport;

	import mx.collections.ArrayCollection;
	import mx.containers.VBox;
	import mx.containers.ViewStack;
	import mx.controls.Alert;
	import mx.controls.Image;
//	import mx.controls.Text;    // Old preview
    import spark.components.RichText;
	import mx.managers.PopUpManager;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	import mx.rpc.remoting.mxml.RemoteObject;
	import mx.utils.Base64Decoder;

    import flashx.textLayout.conversion.TextConverter;

        [Bindable] public var resultList:ArrayCollection = new ArrayCollection();
        [Bindable] public var remoteListResults:RemoteObject = new RemoteObject(REMOTE_SERVICE);
        [Bindable] private var searchOptionsLabels:ArrayCollection = null;
        [Bindable] private var searchOptionsFields:ArrayCollection = null;
        public var myStack:ViewStack = null;

        //private var imageZoom:Image;
	private var selectedSurveyDTO:SurveyDTO = null;
	private var surveyHasImage:Boolean = false;
	private static const REMOTE_SERVICE:String = "myService";
	private static const RESULT_PAGE_SIZE:int = 13;

	private function init():void{
		searchOptionsLabels = new ArrayCollection();
		searchOptionsLabels.addItem(ConfigI18n.getInstance().getString("comboSearchAll"));
		searchOptionsLabels.addItem(ConfigI18n.getInstance().getString("colResultId"));
		searchOptionsLabels.addItem(ConfigI18n.getInstance().getString("colImei"));

		searchOptionsFields = new ArrayCollection();
		searchOptionsFields.addItem(new Array("idResult", "imei"));
		searchOptionsFields.addItem(new Array("idResult"));
		searchOptionsFields.addItem(new Array("imei"));

		customCheck.addEventListener(MouseEvent.CLICK, selectedAll);
	}

	public function listResultsFirstPage(survey:SurveyDTO):void{
		selectedSurveyDTO = survey;
		resetView(selectedSurveyDTO);
		pagination.refresh();
	}

	private function listResults(event:ChangePageEvent):void{
		preview.removeAllChildren();
		resultList.source = new Array();
		remoteListResults.showBusyCursor = true;
		remoteListResults.listResultsBySurvey(SessionClass.getInstance().loggedUser.username,
				selectedSurveyDTO.idSurvey, event.page, event.pageSize,
				event.filterText, event.filterFields, event.sortField, event.sortDescending);
		SessionTimer.getInstance().resetTimer();
	}

	private function resetView(survey:SurveyDTO):void{
		surveyTitle.text = survey.title;
		search.clearUI();
		pagination.reset();
		resultList.source = new Array();
	}

	private function showSurveys(event:MouseEvent):void{
		myStack.selectedIndex = 0;
	}

	private function selectedAll(event:MouseEvent):void{
		pagination.selectAll(customCheck.selected);
	}

	private function markAll(event:MouseEvent):void{
		pagination.selectAll(true);
	}

	private function unmarkAll(event:MouseEvent):void{
		pagination.selectAll(false);
	}

	private function showPreview(event:Event):void{
		var result:ResultDTO = resultsGrid.selectedItem as ResultDTO;
		if (result != null){
			var remoteObject:RemoteObject = new RemoteObject(REMOTE_SERVICE);
			remoteObject.showBusyCursor = true;
			remoteObject.addEventListener(FaultEvent.FAULT, onFault);
			remoteObject.addEventListener(ResultEvent.RESULT, onSuccess);
			remoteObject.getPreview(SessionClass.getInstance().loggedUser.username,
							selectedSurveyDTO.idSurvey, result.idResult);
		} else{
			preview.removeAllChildren();
		}

		function onSuccess(event:ResultEvent):void {
			var i:int;
			var previewContent:SurveyPreviewDTO;
			preview.removeAllChildren();
			surveyHasImage = false;

			if (event.result != null) {
				var arrayPreview:ArrayCollection = new ArrayCollection();
				arrayPreview = event.result as ArrayCollection;

				var surveyPreviewDTO:SurveyPreviewDTO;
	            var byteArr:ByteArray;
				var base64Dec:Base64Decoder = new Base64Decoder();
//				var txtHtmlText:Text;   // Old preview
                var txtHtmlText:RichText;
				var photoImage:Image;
				var panel:VBox;

				for (i = 0; i<arrayPreview.length; i++){
	                surveyPreviewDTO = arrayPreview.getItemAt(i) as SurveyPreviewDTO;

					if (!surveyPreviewDTO.isImage) {
//						txtHtmlText = new Text();
//						txtHtmlText.width = 220;
//						txtHtmlText.htmlText = surveyPreviewDTO.htmlText;   // Old preview
//						preview.addChild(txtHtmlText);
                        txtHtmlText = new RichText();
                        txtHtmlText.styleName = "preview";
                        txtHtmlText.textFlow = TextConverter.importToFlow(surveyPreviewDTO.htmlText, TextConverter.TEXT_FIELD_HTML_FORMAT)
                        txtHtmlText.width = 220;
                        preview.addChild(txtHtmlText);
					} else {
						photoImage = new Image();
						photoImage.width = 96;
						photoImage.height = 72;
						photoImage.useHandCursor = true;
						photoImage.buttonMode = true;
						photoImage.toolTip = ConfigI18n.getInstance().getString("imageTooltip");
						photoImage.addEventListener(MouseEvent.CLICK, zoomImage);
						panel = new VBox();
						panel.styleName = "imagePanel";
						panel.addChild(photoImage);

			            base64Dec.decode(surveyPreviewDTO.htmlText);
			            byteArr = base64Dec.toByteArray();
						photoImage.load(byteArr);
						preview.addChild(panel);
						surveyHasImage = true;
					}
				}
			}
		}

		function onFault(event:FaultEvent):void	{
			Alert.show(ExceptionUtil.getMessage(event.fault.faultString),
					ConfigI18n.getInstance().getString("lblError"));
		}
	}

	private function zoomImage(event:MouseEvent):void{
        var imageViewer:ImageViewer = new ImageViewer();
        imageViewer.imageSource = (event.currentTarget as Image).source;

        PopUpManager.addPopUp(imageViewer, this.parentApplication as DisplayObject, true);
        PopUpManager.centerPopUp(imageViewer);
	}

	private function export(isSelected:Boolean):void{
		if (resultList.length > 0){
			var resultExport:ResultExport = new ResultExport();
			resultExport.surveyDTO = selectedSurveyDTO;
			PopUpManager.addPopUp(resultExport, this.parentApplication as DisplayObject, true);
			PopUpManager.centerPopUp(resultExport);
			resultExport.renderasExportImages(surveyHasImage);
                        if(isSelected && pagination.selectedAllItems.length > 0)
                            resultExport.setSelectedResultList(pagination.selectedAllItems);
		} else {
			Alert.show(ConfigI18n.getInstance().getString("noResultsToExport"),
					ConfigI18n.getInstance().getString("lblWarning"));
		}
	}

	private function showImport():void {
		var resultImport:ResultImport = new ResultImport();
		resultImport.surveyDTO = selectedSurveyDTO;
		resultImport.addEventListener(CloseResultImportEvent.EVENT_NAME, closePopup);

		PopUpManager.addPopUp(resultImport, this.parentApplication as DisplayObject, true);
		PopUpManager.centerPopUp(resultImport);

		function closePopup(event:CloseResultImportEvent):void{
			pagination.refresh();
		}
	}

	private function showMap():void {
		if (pagination.selectedAllItems.length > 0) {
			var mapPosition:ResultMap = new ResultMap();
			mapPosition.setSelectedResultList(pagination.selectedAllItems);
			PopUpManager.addPopUp(mapPosition, this.parentApplication as DisplayObject, true);
			PopUpManager.centerPopUp(mapPosition);
		} else {
			Alert.show(ConfigI18n.getInstance().getString("noResultsSelected"),
					ConfigI18n.getInstance().getString("lblWarning"));
		}
	}

	private function showMapAll():void {
		var mapPosition:ResultMap = new ResultMap();
		mapPosition.setSurvey(selectedSurveyDTO.idSurvey);
		PopUpManager.addPopUp(mapPosition, this.parentApplication as DisplayObject , true);
		PopUpManager.centerPopUp(mapPosition);
	}
