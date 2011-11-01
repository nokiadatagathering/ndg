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

package br.org.indt.ndg.servlets;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import br.org.indt.ndg.common.exception.MSMApplicationException;
import br.org.indt.ndg.common.exception.MSMSystemException;
import br.org.indt.ndg.common.exception.UserNotFoundException;
import br.org.indt.ndg.server.client.MSMBusinessDelegate;
import br.org.indt.ndg.server.client.SurveyVO;
import br.org.indt.ndg.server.client.TemporaryOpenRosaBussinessDelegate;
import br.org.indt.ndg.server.client.TransactionLogVO;
import br.org.indt.ndg.server.pojo.NdgUser;

public class ProcessDownloadSurvey {
	// NDG protocol specific query params
	private static String IMEI_PARAM = "imei";
	// XForms protocol specific query params as specified by:
	// https://bitbucket.org/javarosa/javarosa/wiki/FormListAPI
	private static String DEVICE_ID_PARAM = "deviceID"; // mandatory
//	private static String FORM_ID_PARAM = "formID"; // optional
//	private static String VERBOSE_PARAM = "verbose"; // optional
	private static String FORM_ID_PARAM = "formID"; // non-standard

	private String m_imei = null;
	Map<String, String[]> m_parameterMap = null;
	private final boolean m_isNdgProtocolRequest;

	MSMBusinessDelegate mb = null;
	TemporaryOpenRosaBussinessDelegate xformsDataProvider = null;

	private static final Logger log = Logger.getLogger("client");

	public ProcessDownloadSurvey(String imei) {
		m_imei = imei;
		m_isNdgProtocolRequest = true;
		mb = new MSMBusinessDelegate();
	}

	public ProcessDownloadSurvey( String thisServerAddress, Map<String, String[]> parameterMap ) {
		// determine which kind of request is it - NDG protocol or XForms protocol
		m_parameterMap = parameterMap;
		if ( parameterMap.containsKey(IMEI_PARAM) ) {
			m_isNdgProtocolRequest = true;
			m_imei = parameterMap.get(IMEI_PARAM)[0];
			mb = new MSMBusinessDelegate();
		} else if ( parameterMap.containsKey(DEVICE_ID_PARAM) ) {
			m_isNdgProtocolRequest = false;
			m_imei = parameterMap.get(DEVICE_ID_PARAM)[0];
			xformsDataProvider = new TemporaryOpenRosaBussinessDelegate();
			xformsDataProvider.setPortAndAddress(thisServerAddress);
			xformsDataProvider.setDeviceId(m_imei);
		} else {
			throw new InvalidParameterException();
		}
	}

	/**
	 * Temporary method to determine which protocol is currently processed
	 * @return	true if it is NDG protocol survey request, false means XForms related survey request
	 */
	private boolean isNdgProtocolRequest() {
		return m_isNdgProtocolRequest;
	}

	public String processAckCommand() {
		StringBuffer result = new StringBuffer();

		result.append("<html>");
		result.append("<head><title>GuestBookServlet</title></head>");
		result.append("<body>");
		result.append("Param Do: " + "Ack");
		result.append("Param Imei: " + m_imei);
		result.append("</body>");
		result.append("</html>");

		updateStatusSendingSurvey(TransactionLogVO.STATUS_SUCCESS);

		return new String(result);
	}

	public String processDownloadCommand() {
		StringBuffer result = new StringBuffer();
		if ( isNdgProtocolRequest() ) {
			try {
				for (Iterator iterator = mb.listSurveysByImeiDB(m_imei,
						TransactionLogVO.STATUS_AVAILABLE).iterator(); iterator
						.hasNext();) {
					NdgUser userlogged = mb.getUserByImei(m_imei);

					if (userlogged != null) {
						SurveyVO surveyVO = (SurveyVO) iterator.next();

						StringBuffer currentResult = new StringBuffer(surveyVO
								.getSurvey());

						// we need to remove last '\n'
						if (currentResult
								.charAt(currentResult.toString().length() - 1) == '\n') {
							currentResult.deleteCharAt(currentResult.toString()
									.length() - 1);
						}

						result.append(currentResult);
					} else {
						new UserNotFoundException();
					}
				}
			} catch (MSMApplicationException e) {
				e.printStackTrace();
			} catch (MSMSystemException e) {
				e.printStackTrace();
			}
		} else {
			try {
				result.append( xformsDataProvider.getFormattedSurvey(m_parameterMap.get(FORM_ID_PARAM)[0], m_imei) );
			} catch (Exception e) {
				// add some default behavior when getting survey was unsuccessful
				result.append( new String() );
			}
		}
		return new String(result);
	}

	public String processListCommand() {
		StringBuffer result = new StringBuffer();
		if ( isNdgProtocolRequest() ) {
			result.append("<surveys>\n");
			try {
				ArrayList<SurveyVO> listOfSurveys = (ArrayList<SurveyVO>) mb
						.listSurveysByImeiDB(m_imei,
								TransactionLogVO.STATUS_AVAILABLE);

				for (Iterator<SurveyVO> iterator = listOfSurveys.iterator(); iterator
						.hasNext();) {
					SurveyVO surveyVO = iterator.next();
					String linha = "<survey id=\"" + surveyVO.getIdSurvey() + "\""
							+ " title=\"" + surveyVO.getTitle() + "\"/>";
					result.append(linha + "\n");
				}
				result.append("</surveys>");
			} catch (MSMApplicationException e) {
				e.printStackTrace();
			} catch (MSMSystemException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			result.append( xformsDataProvider.getFormattedSurveyAvailableToDownloadList(m_imei) );
		}
		return new String(result);
	}

	private void updateStatusSendingSurvey(String status) {
		try {
			mb.updateStatusSendingSurvey(m_imei, status);

			log.debug("Processing Ack Command to Imei " + m_imei);
			log
					.debug("############################################################");
		} catch (MSMApplicationException e) {
			e.printStackTrace();
		} catch (MSMSystemException e) {
			e.printStackTrace();
		}
	}
}
