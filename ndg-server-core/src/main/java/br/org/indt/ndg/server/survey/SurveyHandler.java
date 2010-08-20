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

package br.org.indt.ndg.server.survey;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import javax.ejb.Remote;

import br.org.indt.ndg.common.ResultXml;
import br.org.indt.ndg.common.SurveyXML;
import br.org.indt.ndg.common.exception.MSMApplicationException;
import br.org.indt.ndg.common.exception.MSMSystemException;
import br.org.indt.ndg.common.exception.SurveyFileAlreadyExistsException;
import br.org.indt.ndg.server.client.SurveyVO;
import br.org.indt.ndg.server.client.TransactionLogVO;
import br.org.indt.ndg.server.controller.QueryInputOutputVO;
import br.org.indt.ndg.server.pojo.NdgUser;
import br.org.indt.ndg.server.pojo.Survey;

@Remote
public interface SurveyHandler {

	public Properties getProperties() throws MSMApplicationException;

	public Properties getSettings() throws MSMApplicationException;

	public String getSpecificPropertySetting(String propertySetting)
			throws MSMApplicationException;

	public void updateSettings(Properties settings)
			throws MSMApplicationException;

	public QueryInputOutputVO getImeisBySurvey(String surveyId, String status,
			QueryInputOutputVO queryIOVO) throws MSMApplicationException;

	public QueryInputOutputVO getAllImeisBySurvey(String surveyId,
			QueryInputOutputVO queryIOVO) throws MSMApplicationException;

	public void sendSlicedSurvey(NdgUser userLogged, String idSurvey,
			ArrayList<String> listoOfDevices) throws MSMApplicationException,
			MSMSystemException;

	public QueryInputOutputVO listSurveysDB(NdgUser user,
			QueryInputOutputVO queryIOVO, Boolean isUploaded) throws MSMApplicationException,
			MSMSystemException;

	public Collection<SurveyVO> listSurveysByImeiDB(String imei, String status)
			throws MSMApplicationException, MSMSystemException;
	
	public QueryInputOutputVO listSurveysByImeiDBWithoutResults(String imei, String status,
			QueryInputOutputVO queryIOVO)
	throws MSMApplicationException, MSMSystemException;

	public void updateStatusSendingSurvey(String imei, String status)
			throws MSMApplicationException, MSMSystemException;

	public ArrayList<String> rationSurveybyGPRS(NdgUser userlogged,
			String idSurvey, ArrayList<String> listOfDevices)
			throws MSMApplicationException, MSMSystemException;

	public Properties getModemProperties() throws MSMApplicationException;

	public void postSurvey(NdgUser user, StringBuffer surveyBuffered,
			TransactionLogVO postSurveyTransaction)
			throws MSMApplicationException, MSMSystemException,
			SurveyFileAlreadyExistsException;

	public SurveyXML loadSurveyDB(String username, String idSurvey)
			throws MSMApplicationException, MSMSystemException;

	public SurveyXML loadSurveyAndResultsDB(String username, String idSurvey)
			throws MSMApplicationException, MSMSystemException;

	public ArrayList<SurveyXML> loadSurveysDB(NdgUser user)
			throws MSMApplicationException, MSMSystemException;

	public ArrayList<ResultXml> loadResultsDB(String idSurvey)
			throws MSMApplicationException, MSMSystemException;

	public Survey getUserBySurvey(String idSurvey)
			throws MSMApplicationException;

	public void saveSurveyFromEditorToServer(String userName,
			String surveyContent) throws MSMApplicationException;

	public String loadSurveyFromServerToEditor(String userName, String surveyID)
			throws MSMApplicationException;

	public ArrayList<String> loadSurveysFromServerToEditor(String userName)
			throws MSMApplicationException;

	public void deleteSurveyFromServer(String surveyID)
			throws MSMApplicationException;

	public void uploadSurveyFromEditorToServer(String surveyID)
			throws MSMApplicationException;

	public Survey getSurveyObject(String surveyId);

	public void detachImeiFromSurvey(String surveyID, String imeiNumber)
			throws MSMApplicationException;
}
