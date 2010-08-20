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

package br.org.indt.ndg.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import br.org.indt.ndg.client.dto.surveys.SCategory;
import br.org.indt.ndg.client.dto.surveys.SField;
import br.org.indt.ndg.client.dto.surveys.SPreview;
import br.org.indt.ndg.client.dto.surveys.SResult;
import br.org.indt.ndg.client.dto.surveys.SSurvey;
import br.org.indt.ndg.client.transformer.CSVTransformer;
import br.org.indt.ndg.client.transformer.DevicesTransformer;
import br.org.indt.ndg.client.transformer.ExcelTransformer;
import br.org.indt.ndg.common.Category;
import br.org.indt.ndg.common.CreateXml;
import br.org.indt.ndg.common.Deploy;
import br.org.indt.ndg.common.Field;
import br.org.indt.ndg.common.FieldType;
import br.org.indt.ndg.common.Item;
import br.org.indt.ndg.common.Resources;
import br.org.indt.ndg.common.ResultXml;
import br.org.indt.ndg.common.SurveyXML;
import br.org.indt.ndg.common.exception.ImeiFileNotFoundException;
import br.org.indt.ndg.common.exception.ImeiNotMatchDevice;
import br.org.indt.ndg.common.exception.MSMApplicationException;
import br.org.indt.ndg.common.exception.MSMSystemException;
import br.org.indt.ndg.common.exception.ModemException;
import br.org.indt.ndg.exception.NDGServerException;
import br.org.indt.ndg.server.client.DeviceVO;
import br.org.indt.ndg.server.client.ImeiVO;
import br.org.indt.ndg.server.client.MSMBusinessDelegate;
import br.org.indt.ndg.server.client.PreferenceVO;
import br.org.indt.ndg.server.client.RoleVO;
import br.org.indt.ndg.server.client.TransactionLogVO;
import br.org.indt.ndg.server.client.UserVO;
import br.org.indt.ndg.server.controller.QueryInputOutputVO;
import br.org.indt.ndg.server.controller.UserBalanceVO;
import br.org.indt.ndg.util.Base64Encode;

public class Service {
	
	
	private MSMBusinessDelegate msmBD = new MSMBusinessDelegate();
	public static final String UNEXPECTED_SERVER_EXCEPTION = "UNEXPECTED_SERVER_EXCEPTION";
	public static final String CSV = ".csv";
	public static final String XLS = ".xls";
	public static final String ZIP = ".zip";
	
	
	// EDITOR - BEGIN
	public void saveSurveyFromEditorToServer(String userName, String surveyContent)
			throws NDGServerException{
		try{
			msmBD.saveSurveyFromEditorToServer(userName, surveyContent);
		} catch (MSMApplicationException e) {
			e.printStackTrace();
			throw new NDGServerException(e.getErrorCode());
		} catch (Exception e){
			e.printStackTrace();
			throw new NDGServerException(UNEXPECTED_SERVER_EXCEPTION);
		}
	}

	public String loadSurveyFromServerToEditor(String userName, String surveyID)
			throws NDGServerException {
		
		String surveyContent;
		try{
			surveyContent = msmBD.loadSurveyFromServerToEditor(userName, surveyID);
		} catch (MSMApplicationException e) {
			e.printStackTrace();
			throw new NDGServerException(e.getErrorCode());
		} catch (Exception e){
			e.printStackTrace();
			throw new NDGServerException(UNEXPECTED_SERVER_EXCEPTION);
		}
		
		return surveyContent;
	}

	public void deleteSurveyFromServer(String surveyID) throws NDGServerException{
		try{
			msmBD.deleteSurveyFromServer(surveyID);
		} catch (MSMApplicationException e) {
			e.printStackTrace();
			throw new NDGServerException(e.getErrorCode());
		} catch (Exception e){
			e.printStackTrace();
			throw new NDGServerException(UNEXPECTED_SERVER_EXCEPTION);
		}
	}
	
	public void uploadSurveyFromEditorToServer(String surveyID)
			throws NDGServerException{
		try{
			msmBD.uploadSurveyFromEditorToServer(surveyID);
		} catch (MSMApplicationException e) {
			e.printStackTrace();
			throw new NDGServerException(e.getErrorCode());
		} catch (Exception e){
			e.printStackTrace();
			throw new NDGServerException(UNEXPECTED_SERVER_EXCEPTION);
		}
	}

	public void saveSettingsFromEditorToServer(String userName, String settingsContent)
			throws MSMApplicationException{
		msmBD.saveSettingsFromEditorToServer(userName, settingsContent);
	}

	public String loadSettingsFromServerToEditor(String userName) throws MSMApplicationException{
		String settingsContent = msmBD.loadSettingsFromServerToEditor(userName);
		return settingsContent;
	}

	public ArrayList<String> loadSurveysFromServerToEditor(String userName)
			throws MSMApplicationException{
		ArrayList<String> surveyContent = msmBD.loadSurveysFromServerToEditor(userName);
		return surveyContent;
	}	
	// EDITOR - END
	

	
	
	/**
	 * 
	 * @param vo
	 * @throws NDGServerException
	 */
	public void requestAccount(UserVO vo) throws NDGServerException{
		try {
			msmBD.requestAccess(vo);
		} catch (MSMApplicationException e) {
			e.printStackTrace();
			throw new NDGServerException(e.getErrorCode());
		} catch (MSMSystemException e){
			e.printStackTrace();
			throw new NDGServerException(e.getErrorCode());
		} catch (Exception e){
			e.printStackTrace();
			throw new NDGServerException(UNEXPECTED_SERVER_EXCEPTION);
		}
	}
	
	
	/**
	 * 
	 * @param username
	 * @param password
	 * @return
	 * @throws NDGServerException
	 */
	public UserVO validateLogin(String username, String password) throws NDGServerException{
		UserVO vo = null;
		try {
			vo = msmBD.validateLogin(username, password);
			if (vo != null && !vo.getRetCode().equals(UserVO.AUTHENTICATED)){
				vo = null;
			}
		} catch (MSMApplicationException e) {
			e.printStackTrace();
			throw new NDGServerException(e.getErrorCode());
		} catch (Exception e){
			e.printStackTrace();
			throw new NDGServerException(UNEXPECTED_SERVER_EXCEPTION);
		}

		return vo;
	}
	
	public UserBalanceVO getUserBalanceDTO(String username) throws NDGServerException{
		UserBalanceVO vo = null;
		try {
			vo = msmBD.findUserBalanceByUser(username);
		} catch (MSMApplicationException e) {
			e.printStackTrace();
			throw new NDGServerException(e.getErrorCode());
		} catch (Exception e){
			e.printStackTrace();
			throw new NDGServerException(UNEXPECTED_SERVER_EXCEPTION);
		}
		return vo;
	}
	
	public boolean isHostedMode() throws NDGServerException{
		Boolean boo = Boolean.FALSE;
		try {
			boo = msmBD.isHostedMode();
		} catch (MSMApplicationException e) {
			e.printStackTrace();
			throw new NDGServerException(e.getErrorCode());
		} catch (Exception e){
			e.printStackTrace();
			throw new NDGServerException(UNEXPECTED_SERVER_EXCEPTION);
		}
		return boo.booleanValue();
	}
	
	public QueryInputOutputVO listUsers(String userName, int page, int pageSize,
			String filterText, ArrayList<String> filterFields, 
			String sortField, boolean isDescending) throws NDGServerException{
		
		QueryInputOutputVO inputVo = new QueryInputOutputVO();
		inputVo.setPageNumber(page);
		inputVo.setRecordsPerPage(pageSize);
		inputVo.setFilterText(filterText);
		inputVo.setFilterFields(filterFields);
		inputVo.setSortField(sortField);
		inputVo.setIsDescending(isDescending);
		
		QueryInputOutputVO outputVo = null;
		try {
			outputVo = msmBD.listAllUsers(userName, inputVo);
			outputVo.setPageCount(outputVo.getPageCountByRecordCount());
			
			ArrayList<Object> users = outputVo.getQueryResult();
			UserVO vo = null;
			for (Object obj : users) {
				vo = (UserVO)obj;
				vo.setCountryCode(vo.getCountryCode().replaceAll("\\+", ""));
			}
		} catch (MSMApplicationException e) {
			e.printStackTrace();
			throw new NDGServerException(e.getErrorCode());
		} catch (Exception e){
			e.printStackTrace();
			throw new NDGServerException(UNEXPECTED_SERVER_EXCEPTION);
		}
		
		return outputVo;
	}
	
	public void saveUser(String username, UserVO userVo, boolean update) throws NDGServerException{
		try {
			if (update){
				if (!userVo.getCountryCode().startsWith("+")){
					userVo.setCountryCode("+" + userVo.getCountryCode());
				}
				if (userVo.getPassword() == ""){
					userVo.setPassword(null);
				}
				msmBD.updateUser(userVo);
			} else{
				userVo.setFirstTimeUse('N');
				userVo.setEmailPreferences('N');
				userVo.setNdgNewsLetter('N');
				userVo.setHasFullPermissions('N');
				userVo.setCountryCode("+" + userVo.getCountryCode());
				msmBD.createUser(username, userVo);
			}
		} catch (MSMApplicationException e) {
			e.printStackTrace();
			throw new NDGServerException(e.getErrorCode());
		} catch (MSMSystemException e){
			e.printStackTrace();
			throw new NDGServerException(e.getErrorCode());
		} catch (Exception e){
			e.printStackTrace();
			throw new NDGServerException(UNEXPECTED_SERVER_EXCEPTION);
		}
	}
	
	
	public void deleteUser(UserVO userVo) throws NDGServerException{
		try {
			msmBD.deleteUser(userVo.getUsername());
		} catch (MSMApplicationException e) {
			e.printStackTrace();
			throw new NDGServerException(e.getErrorCode());
		} catch (Exception e){
			e.printStackTrace();
			throw new NDGServerException(UNEXPECTED_SERVER_EXCEPTION);
		}
	}
	
	public Collection<RoleVO> listRoles() throws NDGServerException{
		Collection<RoleVO> roles = null;
		try {
			roles = msmBD.listAllRoles();
		} catch (MSMApplicationException e) {
			e.printStackTrace();
			throw new NDGServerException(e.getErrorCode());
		} catch (Exception e){
			e.printStackTrace();
			throw new NDGServerException(UNEXPECTED_SERVER_EXCEPTION);
		}
		
		return roles;
	}
	

	/**
	 * 
	 * @param userName
	 * @return
	 * @throws NDGServerException
	 */
	public QueryInputOutputVO listSurveys(String userName, int page, int pageSize,
			String filterText, ArrayList<String> filterFields, 
			String sortField, boolean isDescending, Boolean isUploaded) throws NDGServerException {

		QueryInputOutputVO inputVo = new QueryInputOutputVO();
		inputVo.setPageNumber(page);
		inputVo.setRecordsPerPage(pageSize);
		inputVo.setFilterText(filterText);
		inputVo.setFilterFields(filterFields);
		inputVo.setSortField(sortField);
		inputVo.setIsDescending(isDescending);
		
		QueryInputOutputVO outputVo = null;
		try {
			outputVo = msmBD.listSurveysDB(userName, inputVo, isUploaded);
			outputVo.setPageCount(outputVo.getPageCountByRecordCount());
		} catch (MSMApplicationException e) {
			e.printStackTrace();
			throw new NDGServerException(e.getErrorCode());
		} catch (MSMSystemException e){
			e.printStackTrace();
			throw new NDGServerException(e.getErrorCode());
		} catch (Exception e){
			e.printStackTrace();
			throw new NDGServerException(UNEXPECTED_SERVER_EXCEPTION);
		}
		return outputVo;
	}
	
	public QueryInputOutputVO getAssocietedImeisBySurvey(String surveyId) throws NDGServerException {

		QueryInputOutputVO outputVo = null;
		
		try {
			outputVo = msmBD.getAllImeisSentAndUnsentBySurvey(surveyId);
			outputVo.setPageCount(outputVo.getPageCountByRecordCount());
		} catch (MSMApplicationException e) {
			e.printStackTrace();
			throw new NDGServerException(e.getErrorCode());
		} catch (Exception e){
			e.printStackTrace();
			throw new NDGServerException(UNEXPECTED_SERVER_EXCEPTION);
		}
		return outputVo;
	}	
	
	
	/**
	 * 
	 * @return
	 * @throws MSMApplicationException
	 * @throws MSMSystemException
	 */
	public QueryInputOutputVO listResultsBySurvey(String username, String surveyId, Integer page,
			Integer pageSize, String filterText, ArrayList<String> filterFields, 
			String sortField, boolean isDescending)
			throws NDGServerException{
		
		QueryInputOutputVO inputVo = new QueryInputOutputVO();
		inputVo.setPageNumber(page);
		inputVo.setRecordsPerPage(pageSize);
		inputVo.setFilterText(filterText);
		inputVo.setFilterFields(filterFields);
		inputVo.setSortField(sortField);
		inputVo.setIsDescending(isDescending);
		
		QueryInputOutputVO outputVo = null;
		try{
			outputVo = msmBD.getResultListDB(username, surveyId, inputVo);
			outputVo.setPageCount(outputVo.getPageCountByRecordCount());
		} catch (MSMApplicationException e){
			e.printStackTrace();
			throw new NDGServerException(e.getErrorCode());
		} catch (MSMSystemException e){
			e.printStackTrace();
			throw new NDGServerException(e.getErrorCode());
		} catch (Exception e){
			e.printStackTrace();
			throw new NDGServerException(UNEXPECTED_SERVER_EXCEPTION);
		}
		return outputVo;
	}
	
	/**
	 * 
	 * @return
	 * @throws MSMApplicationException
	 * @throws MSMSystemException
	 */
	public QueryInputOutputVO listImeisBySurvey(String surveyId, int page,
			int pageSize, String filterText, ArrayList<String> filterFields, 
			String sortField, boolean isDescending)
			throws NDGServerException{
		
		QueryInputOutputVO inputVo = new QueryInputOutputVO();
		inputVo.setPageNumber(page);
		inputVo.setRecordsPerPage(pageSize);
		inputVo.setFilterText(filterText);
		inputVo.setFilterFields(filterFields);
		inputVo.setSortField(sortField);
		inputVo.setIsDescending(isDescending);
		QueryInputOutputVO outputVo = null;
		
		try{
			outputVo = msmBD.getAllImeisBySurvey(surveyId, inputVo);
			outputVo.setPageCount(outputVo.getPageCountByRecordCount());
		} catch (MSMApplicationException e){
			e.printStackTrace();
			throw new NDGServerException(e.getErrorCode());
		} catch (Exception e){
			e.printStackTrace();
			throw new NDGServerException(UNEXPECTED_SERVER_EXCEPTION);
		}
		
		return outputVo;
	}
	
	

	/**
	 * 
	 * @param username
	 * @return
	 * @throws NDGServerException
	 */
	public QueryInputOutputVO listImeisByUser(String username, Integer page,
			Integer pageSize, String filterText, ArrayList<String> filterFields, 
			String sortField, boolean isDescending, boolean withFakeImeis,
			boolean allImeisByAdmin)
			throws NDGServerException{

		QueryInputOutputVO inputVo = new QueryInputOutputVO();
		inputVo.setPageNumber(page);
		inputVo.setRecordsPerPage(pageSize);
		inputVo.setFilterText(filterText);
		inputVo.setFilterFields(filterFields);
		inputVo.setSortField(sortField);
		inputVo.setIsDescending(isDescending);
		QueryInputOutputVO outputVo = null;
		try{
			if (allImeisByAdmin){
				outputVo = msmBD.listAllImeis(username, inputVo, withFakeImeis);
			} else{
				outputVo = msmBD.getImeisByUser(username, inputVo, withFakeImeis);
			}
			outputVo.setPageCount(outputVo.getPageCountByRecordCount());
			
			//ArrayList<Object> imeis = outputVo.getQueryResult();
			//ImeiVO vo = null;
			//for (Object obj : imeis) {
			//	vo = (ImeiVO)obj;
			//	vo.setMsisdn(vo.getMsisdn().replaceAll("\\+", ""));
			//}
		} catch (MSMApplicationException e){
			e.printStackTrace();
			throw new NDGServerException(e.getErrorCode());
		} catch (Exception e){
			e.printStackTrace();
			throw new NDGServerException(UNEXPECTED_SERVER_EXCEPTION);
		}

		return outputVo;
	}
	
	public QueryInputOutputVO listImeisByUserWithoutPlus(String username, Integer page,
			Integer pageSize, String filterText, ArrayList<String> filterFields, 
			String sortField, boolean isDescending, boolean withFakeImeis,
			boolean allImeisByAdmin)
			throws NDGServerException{
		
		QueryInputOutputVO out = this.listImeisByUser(username, page, pageSize, filterText,
				filterFields, sortField, isDescending, withFakeImeis, allImeisByAdmin);
		ArrayList<Object> imeis = out.getQueryResult();
		ImeiVO vo = null;
		for (Object obj : imeis) {
			vo = (ImeiVO)obj;
			vo.setMsisdn(vo.getMsisdn().replaceAll("\\+", ""));
		}
		return out;
	}
	
	public void saveImei(ImeiVO imeiVO, boolean update) throws NDGServerException{
		try {
			imeiVO.setMsisdn("+" + imeiVO.getMsisdn());
			if (update){
				msmBD.updateIMEI(imeiVO);
			} else{
				String str = "z" + new Long(new Date().getTime()).toString();
				imeiVO.setImei(str);
				imeiVO.setRealImei('N');
				msmBD.createIMEI(imeiVO);
			}
		} catch (MSMApplicationException e) {
			e.printStackTrace();
			throw new NDGServerException(e.getErrorCode());
		} catch (Exception e){
			e.printStackTrace();
			throw new NDGServerException(UNEXPECTED_SERVER_EXCEPTION);
		}
	}	
	
	public ArrayList<String> saveImeis(ArrayList<ImeiVO> arrayImei, ArrayList<Boolean> arrayImeisSaved){
		
		ArrayList<String> imeisSaved = new ArrayList<String>();
		for(int i=0; i < arrayImei.size(); i++){
			try {
				if ((arrayImei.get(i) != null) && !(arrayImeisSaved.get(i))) {
					saveImei(arrayImei.get(i), false);		
					imeisSaved.add("ok");
				} else { 
					imeisSaved.add("not ok");
				}
			} catch (NDGServerException e) {
				imeisSaved.add(e.getMessage());
			}
		}
		return imeisSaved;
	}
	
	public void deleteImei(ImeiVO imei) throws NDGServerException{
		try {
			msmBD.deleteIMEI(imei.getImei());
		} catch (MSMApplicationException e){
			e.printStackTrace();
			throw new NDGServerException(e.getErrorCode());
		} catch (Exception e){
			e.printStackTrace();
			throw new NDGServerException(UNEXPECTED_SERVER_EXCEPTION);
		}
	}
	
	public Collection listDevices() throws NDGServerException{
		ArrayList devices = null;
		try {
			QueryInputOutputVO output = msmBD.listAllDevices(null);
			devices = output.getQueryResult();
		} catch (MSMApplicationException e) {
			e.printStackTrace();
			throw new NDGServerException(e.getErrorCode());
		} catch (Exception e){
			e.printStackTrace();
			throw new NDGServerException(UNEXPECTED_SERVER_EXCEPTION);
		}
		
		return devices;
	}	
	
	public void saveDevice(DeviceVO device, boolean update) throws NDGServerException{
		try {
			if (update){
				msmBD.updateDevice(device);
			} else{
				msmBD.createDevice(device);
			}
		} catch (MSMApplicationException e){
			e.printStackTrace();
			throw new NDGServerException(e.getErrorCode());
		} catch (Exception e){
			e.printStackTrace();
			throw new NDGServerException(UNEXPECTED_SERVER_EXCEPTION);
		}
	}	
	
	public void deleteDevice(DeviceVO device) throws NDGServerException{
		try {
			msmBD.deleteDevice(device.getDeviceModel());
		} catch (MSMApplicationException e){
			e.printStackTrace();
			throw new NDGServerException(e.getErrorCode());
		} catch (Exception e){
			e.printStackTrace();
			throw new NDGServerException(UNEXPECTED_SERVER_EXCEPTION);
		}
	}
	
	
	public QueryInputOutputVO listSurveysByImei(String imei, int page,
			int pageSize, String filterText, ArrayList<String> filterFields, 
			String sortField, boolean isDescending) throws NDGServerException{
		
		QueryInputOutputVO inputVo = new QueryInputOutputVO();
		inputVo.setPageNumber(page);
		inputVo.setRecordsPerPage(pageSize);
		inputVo.setFilterText(filterText);
		inputVo.setFilterFields(filterFields);
		inputVo.setSortField(sortField);
		inputVo.setIsDescending(isDescending);
		
		QueryInputOutputVO outputVo = null;		
		try {
			outputVo = msmBD.listSurveysByImeiDBWithoutResults(imei, "SUCCESS", inputVo);
			outputVo.setPageCount(outputVo.getPageCountByRecordCount());
		} catch (MSMApplicationException e){
			e.printStackTrace();
			throw new NDGServerException(e.getErrorCode());
		} catch (MSMSystemException e){
			e.printStackTrace();
			throw new NDGServerException(e.getErrorCode());
		} catch (Exception e){
			e.printStackTrace();
			throw new NDGServerException(UNEXPECTED_SERVER_EXCEPTION);
		}
		return outputVo;
	}
	
	
	public void sendSurveyCable(String deviceDrive, String username, ArrayList<String> surveys, String imei)
		throws NDGServerException{
		
		try {
			if (Deploy.checkIMEI(deviceDrive, imei)) {
				for (String surveyId : surveys) {
					try {
						msmBD.copySurveyToDevice(deviceDrive, username,
								surveyId, imei);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
						throw new NDGServerException(UNEXPECTED_SERVER_EXCEPTION);
					} catch (IOException e) {
						e.printStackTrace();
						throw new NDGServerException(UNEXPECTED_SERVER_EXCEPTION);
					} catch (MSMApplicationException e) {
						e.printStackTrace();
						throw new NDGServerException(e.getErrorCode());
					}
				}
			} else {
				throw new NDGServerException(new ImeiNotMatchDevice().getErrorCode());
			}
		} catch (IOException e1) {
			e1.printStackTrace();
			throw new NDGServerException(UNEXPECTED_SERVER_EXCEPTION);
		} catch (ImeiFileNotFoundException e) {
			e.printStackTrace();
			throw new NDGServerException(e.getErrorCode());
		}
	}
	
	public void sendSurveySMS(String username, ArrayList<String> surveys, ArrayList<String> devices) 
		throws NDGServerException{
		
		for (String surveyId:surveys) {
			try {
				msmBD.sendSurveybySMS(username, surveyId, devices);
			} catch (MSMApplicationException e) {
				e.printStackTrace();
				throw new NDGServerException(e.getErrorCode());
			} catch (MSMSystemException e) {
				e.printStackTrace();
				throw new NDGServerException(UNEXPECTED_SERVER_EXCEPTION);				
			}
		}
	}
	
	public void sendSurveyGPRS(String username, ArrayList<String> surveys, ArrayList<String> devices) 
		throws NDGServerException{ 
	
		try {
			msmBD.rationSurveybyGPRS(username, surveys, devices);
		} catch (MSMApplicationException e) {
			e.printStackTrace();
			throw new NDGServerException(e.getErrorCode());
		} catch (MSMSystemException e) {
			e.printStackTrace();
			throw new NDGServerException(UNEXPECTED_SERVER_EXCEPTION);				
		}
	}
	
	public ImeiVO getImeiFromDrive(String drive) throws NDGServerException{ 
		
		ImeiVO imei = null;		
		try {
			imei = Deploy.getIMEIFromDeviceDrive(drive);
		} catch (ImeiFileNotFoundException e) {
			e.printStackTrace();
			throw new NDGServerException(e.getErrorCode());
		} catch (IOException e) {
			e.printStackTrace();
			throw new NDGServerException(UNEXPECTED_SERVER_EXCEPTION);
		}	
		return imei;
	}
	
	/**
	 * @param username
	 * @param surveyId
	 * @param resultId
	 * @return
	 * @throws NDGServerException
	 */
	
	public ArrayList<SPreview> getPreview(String username, String surveyId, String resultId)
		throws NDGServerException{
		
		SSurvey survey = this.getSSurvey(username, surveyId, resultId);
		String finalString = "";
		ArrayList<SPreview> list = new ArrayList<SPreview>();
		
		Iterator iCategory = survey.getCategories().values().iterator();
		
		while(iCategory.hasNext()){	
			SCategory sc = (SCategory) iCategory.next();
			
			finalString = "<span id='txt-list-top'><b>" + sc.getId()+ " - "
					+ sc.getName().toUpperCase() + "</b></span>";
			
			list.add(new SPreview(finalString, false));
			
			Iterator iFields = sc.getFields().iterator();
			while(iFields.hasNext()) {
				SField sf = (SField) iFields.next();
				finalString = "<span id='txt-list-down'><b>" + sc.getId() + "." + sf.getId()
						+ "&nbsp;" + sf.getDescription() + "</b></span>";
				
				list.add(new SPreview(finalString, false));
				
				if (survey.getResultsSize() > 0) {
					SResult r = (SResult) survey.getResults().get(0);
					SCategory rc = (SCategory) r.getCategories().get(new Integer(sc.getId()));
					SField rf = rc.getFieldById(sf.getId());
					String value = "----";
					boolean emptyValue = true;
					if (rf.getValue() != null && !rf.getValue().trim().equals("")){
						value = rf.getValue();
						emptyValue = false;
					}
					
					if (emptyValue || !sf.getElementName().equals("img_data")) {
						finalString = "<span id = 'txt-list-answer'><i>" + value + "</i></span>";
						list.add(new SPreview(finalString, false));
					} else {
						finalString = value;
						list.add(new SPreview(finalString, true));	
					}
				}
			}
			finalString = "<br><br>";
			list.add(new SPreview(finalString, false));
			
		}
		return list;
	}	
	
	/*
	 * 
	 */
	private SSurvey getSSurvey(String username, String surveyId, String resultId)
			throws NDGServerException{
		
		SSurvey survey = new SSurvey();
		try {
			SurveyXML surveyXml = msmBD.loadSurveyDB(username, surveyId);
			survey = getSSurvey(surveyXml);
			ResultXml resultXml = msmBD.getResultDB(resultId);
			SResult sResult = this.getSResult(surveyXml, resultXml);
			survey.addResult(sResult);
		} catch (MSMApplicationException e){
			e.printStackTrace();
			throw new NDGServerException(e.getErrorCode());
		} catch (MSMSystemException e){
			e.printStackTrace();
			throw new NDGServerException(e.getErrorCode());
		} catch (Exception e){
			e.printStackTrace();
			throw new NDGServerException(UNEXPECTED_SERVER_EXCEPTION);
		}
		
		return survey;
	}
	
	/*
	 * 
	 */
	private SSurvey getSSurvey(SurveyXML surveyXml){
		SSurvey survey = new SSurvey();
		for (Category c : surveyXml.getCategories().values()) {
			SCategory sCategory = new SCategory();
			sCategory.setId(c.getId());
			sCategory.setName(c.getName());
			for (Field f : c.getFields()){
				SField sField = new SField();
				sField.setCategoryId(f.getCategoryId());
				sField.setId(f.getId());
				sField.setDescription(f.getDescription());
				sField.setXmlType(f.getXmlType());
				sCategory.addField(sField);
			}
			survey.addCategory(new Integer(c.getId()), sCategory);
		}
		return survey;
	}
	
	/*
	 * 
	 */
	private SResult getSResult(SurveyXML surveyXml, ResultXml resultXml){
		SResult sResult = new SResult();
		for (Category c : resultXml.getCategories().values()) {
			SCategory sCategory = new SCategory();
			sCategory.setId(c.getId());
			sCategory.setName(c.getName());
			for (Field field : c.getFields()){
				SField sField = new SField();
				sField.setCategoryId(field.getCategoryId());
				sField.setId(field.getId());
				sField.setDescription(field.getDescription());
				sField.setXmlType(field.getXmlType());
				String value = null;
				if (field.getFieldType() == FieldType.STR) {
					value = field.getValue() == null ? "" : field.getValue();
				} else if (field.getFieldType() == FieldType.DATE){
					value = field.getValue() == null ? "" : Resources.toDate(Long.parseLong(field.getValue()));	
				} else if (field.getFieldType() == FieldType.INT){
					value = field.getValue() == null ? "0" : field.getValue();	
				} else if (field.getFieldType() == FieldType.DECIMAL){
					value = field.getValue() == null ? "0" : field.getValue();	
				} else if (field.getFieldType() == FieldType.CHOICE){
					StringBuffer tmp = new StringBuffer();
					for(Item item : field.getChoice().getItems()) {
						String s = surveyXml.getItemValue(field.getCategoryId(), field.getId(), item.getIndex());
						if (s != null) tmp.append(s.trim());
						
						if (item.getValue() != null) {
							if (s != null) tmp.append(": ");
							tmp.append(item.getValue());
						}
						tmp.append("\n");
					}
					value = tmp.toString() == null ? "" : tmp.toString().trim();
				} else if (field.getFieldType() == FieldType.IMAGE) {
					value = field.getValue() == null ? "" : field.getValue();
				}
				sField.setValue(value);
				sCategory.addField(sField);
			}
			sResult.addCategory(new Integer(c.getId()), sCategory);
		}
		return sResult;
	}
	
	/**
	 * 
	 * @param format
	 * @param surveyId
	 * @param exportWithImages
	 * @return
	 * @throws IOException 
	 */
	public String exportResults(String username, String format, String surveyId,
								Boolean exportWithImages)
			throws NDGServerException{
		
		final String SURVEY = "survey";
		String fileType = "";
		String strFileContent = null;
		byte[] fileContent = null;
		
		if (exportWithImages == true) {
			new File(surveyId).mkdir();
			new File(surveyId + File.separator + "photos").mkdir();
		}
		
		try{
			SurveyXML survey = msmBD.loadSurveyAndResultsDB(username, surveyId);
			
			if (CSV.equalsIgnoreCase(format)){
				CSVTransformer transformer = new CSVTransformer(survey, exportWithImages);
				fileContent = transformer.getBytes();
				fileType = CSV; 
			} else if (XLS.equalsIgnoreCase(format)){
				ExcelTransformer transformer = new ExcelTransformer(survey, exportWithImages);
				fileContent = transformer.getBytes();
				fileType = XLS;
			}
			strFileContent = Base64Encode.base64Encode(fileContent);	
		} catch (MSMApplicationException e){
			e.printStackTrace();
			throw new NDGServerException(e.getErrorCode());
		} catch (MSMSystemException e){
			e.printStackTrace();
			throw new NDGServerException(e.getErrorCode());
		} catch (Exception e){
			e.printStackTrace();
			throw new NDGServerException(UNEXPECTED_SERVER_EXCEPTION);
		}
		
		if (exportWithImages == true)
		{
			zipSurvey(surveyId, fileContent, fileType);
			File zipFile = new File(SURVEY + surveyId + ZIP);
			File zipDir =  new File(surveyId);
			try {
				fileContent = getBytesFromFile(zipFile);
				strFileContent = Base64Encode.base64Encode(fileContent);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			zipFile.delete();
			deleteDir(zipDir);
		}
		
		return  strFileContent;
	}

	private static boolean deleteDir(File dir) {
		if (dir.isDirectory()) { 
			String[] children = dir.list();
			for (int i=0; i<children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				
				if (!success) {
					return false;
				}
			}
		} 
		return dir.delete();
	} 	
	
	
	private static byte[] getBytesFromFile(File file) throws IOException {
		InputStream is = new FileInputStream(file);
		
		long length = file.length(); 
		
		byte[] bytes = new byte[(int)length]; 
		
		int offset = 0;
		int numRead = 0;
		
		while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
			offset += numRead;
		} 
		
		if (offset < bytes.length) {
			throw new IOException("Could not completely read file "+file.getName());
		} 
		
		is.close();
		
		return bytes; 	
	}
	
	private void zipSurvey(String surveyId, byte[] fileContent, String fileType)
	{
		final String SURVEY = "survey";
		FileOutputStream arqExport;
		try
		{
			if (fileType.equals(XLS)) {
				arqExport = new FileOutputStream(surveyId + File.separator + SURVEY + surveyId + XLS);
				arqExport.write(fileContent);
				arqExport.close();
			}
			else if (fileType.equals(CSV)) {
				arqExport = new FileOutputStream(surveyId + File.separator + SURVEY + surveyId + CSV);
				arqExport.write(fileContent);
				arqExport.close();
			}
			
			ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(SURVEY + surveyId + ZIP));
			
			zipDir(surveyId, zos);
			
			zos.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}		
	}
	
	private void zipDir(String dir2zip, ZipOutputStream zos) 
	{ 
		try 
		{ 
	     
           File zipDir = new File(dir2zip); 
	     
	        String[] dirList = zipDir.list(); 
	        byte[] readBuffer = new byte[2156]; 
	        int bytesIn = 0; 
	     
	        for(int i=0; i<dirList.length; i++) 
	        { 
	            File f = new File(zipDir, dirList[i]); 
		        if(f.isDirectory()) 
		        { 
		            String filePath = f.getPath(); 
		            zipDir(filePath, zos); 
		            continue; 
		        } 

	            FileInputStream fis = new FileInputStream(f); 
 
	            ZipEntry anEntry = new ZipEntry(f.getPath()); 
	             
	            zos.putNextEntry(anEntry); 
	             
	            while((bytesIn = fis.read(readBuffer)) != -1) 
	            { 
	                zos.write(readBuffer, 0, bytesIn); 
	            } 

	           fis.close(); 
	        } 
		} 
		catch(Exception e) 
		{
			e.printStackTrace();
		}
	} 

	
	public ArrayList<String> listRoots() {
		File[] roots = File.listRoots();
		ArrayList<String> list = new ArrayList<String>();
		
		for(int i=0; i < roots.length; i++) {
			list.add( roots[i].toString() ); 
		}
		
		return list;
	}
	
	public void readResultsFromDevice(String userName, String surveyId,
			String deviceDrive) throws NDGServerException {

		ArrayList<ResultXml> results;
		try {
			results = Deploy.getResultsFromDevice(deviceDrive, userName,surveyId);
			for (ResultXml result : results) {
				TransactionLogVO t = new TransactionLogVO();
				t.setTransmissionMode(TransactionLogVO.MODE_CABLE);
				t.setAddress(result.getImei());
				t.setUser(userName);				
				
				Document resultDoc = (Document) result.getXmldoc();
				String xmlDoc = CreateXml.xmlToString((Node) resultDoc);
				StringBuffer xmlSB = new StringBuffer(xmlDoc);
				msmBD.postResult(xmlSB, t);
			}
		} catch (MSMApplicationException e) {
			e.printStackTrace();
			throw new NDGServerException(e.getErrorCode());
		} catch (MSMSystemException e) {
			e.printStackTrace();
			throw new NDGServerException(e.getErrorCode());
		} catch (Exception e){
			e.printStackTrace();
			throw new NDGServerException(UNEXPECTED_SERVER_EXCEPTION);
		}
	}
	
	/**
	 * @param surveyId
	 * @param format
	 * @param filterText
	 * @param filterFields
	 * @param sortField
	 * @param isDescending
	 * @return
	 * @throws NDGServerException
	 */
	public String exportSurveyDevices(String surveyId, String[] header, String format) throws NDGServerException {
		String XLS = ".xls";
		String strFileContent = null;
		byte[] fileContent = null;

		try {
			QueryInputOutputVO inputVo = null;
			QueryInputOutputVO  outputVo = msmBD.getAllImeisBySurvey(surveyId, inputVo);
			
			ArrayList<Object> imeisArray = outputVo.getQueryResult();
			ArrayList<ImeiVO> imeis = new ArrayList<ImeiVO>();
			
			for (Object imei : imeisArray){
				 imeis.add((ImeiVO)imei);
			}
			
			if (XLS.equalsIgnoreCase(format)){
				DevicesTransformer transformer = new DevicesTransformer(imeis, header);
				fileContent = transformer.getBytes();
			}
			
			strFileContent = Base64Encode.base64Encode(fileContent);
			
		} catch (Exception e){
			e.printStackTrace();
			throw new NDGServerException(UNEXPECTED_SERVER_EXCEPTION);
		}
		
		return  strFileContent;
	}
	
	public void sendAlert(String message, ArrayList<String> listoOfDevices, String userName) throws NDGServerException {
		try {
			msmBD.sendTextSMS(message, listoOfDevices, 0, userName);
		} catch (ModemException e) {
			throw new NDGServerException(UNEXPECTED_SERVER_EXCEPTION);
		} catch (MSMApplicationException e) {
			throw new NDGServerException(UNEXPECTED_SERVER_EXCEPTION);
		}
	}
	
	/**
	 * @param surveyID
	 * @param imeis
	 * @return
	 * @throws NDGServerException
	 */
	public void detachImeiFromSurvey(String surveyID, ArrayList<String> imeis)
		throws NDGServerException {
		
		for (String imeiNumber:imeis) {
			try {
				msmBD.detachImeiFromSurvey(surveyID, imeiNumber);
			} catch (MSMApplicationException e){
				e.printStackTrace();
				throw new NDGServerException(e.getErrorCode());
			} catch (Exception e){
				e.printStackTrace();
				throw new NDGServerException(UNEXPECTED_SERVER_EXCEPTION);
			}
		}
	}

	public boolean requestPassword(String email) throws NDGServerException {
		try {
			if (!msmBD.forgotYourPassword(email))
				return false;
			else
				return true;
		} catch (MSMApplicationException e) {
			e.printStackTrace();
			throw new NDGServerException(e.getErrorCode());			
		} catch (MSMSystemException e) {
			e.printStackTrace();
			throw new NDGServerException(UNEXPECTED_SERVER_EXCEPTION);				
		}
	}	

	public UserVO validateKey(String key) throws NDGServerException {
		UserVO vo = null;
		
		try {
			vo = msmBD.checkValidationKey(key);
		} catch (MSMSystemException e) {
			e.printStackTrace();
			throw new NDGServerException(UNEXPECTED_SERVER_EXCEPTION);				
		} catch (MSMApplicationException e) {
			e.printStackTrace();
			throw new NDGServerException(e.getErrorCode());			
		}
		
		return vo;
	}
	
	public void updatePassword(UserVO user) throws NDGServerException {
		try {
			msmBD.updatePassword(user);
		} catch (MSMApplicationException e) {
			e.printStackTrace();
			throw new NDGServerException(e.getErrorCode());
		}
	}
	
	public String getVersion() {
		MSMBusinessDelegate msmBD = new MSMBusinessDelegate();
		return msmBD.getNDGVersion();
	}
	
	public boolean hasSmsSupport(){
		return msmBD.hasSmsSupport();
	}
	
	public Collection <PreferenceVO> listPreferences() throws NDGServerException {
		try {
			MSMBusinessDelegate msmBD = new MSMBusinessDelegate();
			Properties p =	msmBD.getSettings();
			
			//Get Hashtable Enumeration to get key and value
			Enumeration keyEnum = p.keys();
			
			ArrayList<PreferenceVO> list = new ArrayList<PreferenceVO>();
			while(keyEnum.hasMoreElements()) {
				//nextElement is used to get key of hashtable
				String key = (String)keyEnum.nextElement();
				
				//get is used to get value of key in hashtable
				String value = (String)p.get(key);
				PreferenceVO bean = new PreferenceVO();
				bean.setPreference(key);
				bean.setValue(value);
				list.add(bean);
			}
			return list;
		} catch (MSMApplicationException e) {
			e.printStackTrace();
			throw new NDGServerException(e.getErrorCode());
		}
	}
	
	public void editPreference(ArrayList<PreferenceVO> preference) throws NDGServerException {
		MSMBusinessDelegate msmBD = new MSMBusinessDelegate();
		try {
			Properties p = new Properties();
			for( int i=0; i < preference.size() ; i++ ){
	            PreferenceVO pref = preference.get(i);
	            p.put(pref.getPreference(), pref.getValue());
	        }
											
			msmBD.updateSettings(p);
		} catch (MSMApplicationException e) {
			e.printStackTrace();
			throw new NDGServerException(e.getErrorCode());
		} 
	}
	
	public String jadDownload(String msisdn) throws NDGServerException{
		try {
			msisdn = "+" + msisdn;
			byte[] byteArray = msmBD.jadDownload(msisdn);
			String fileContent = Base64Encode.base64Encode(byteArray);
			return fileContent;
		} catch (MSMApplicationException e) {
			e.printStackTrace();
			throw new NDGServerException(e.getErrorCode());
		} catch (Exception e){
			e.printStackTrace();
			throw new NDGServerException(UNEXPECTED_SERVER_EXCEPTION);
		}
	}
	
	
	
}