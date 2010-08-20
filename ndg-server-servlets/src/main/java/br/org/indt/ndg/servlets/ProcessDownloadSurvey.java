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

package br.org.indt.ndg.servlets;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

import br.org.indt.ndg.common.exception.MSMApplicationException;
import br.org.indt.ndg.common.exception.MSMSystemException;
import br.org.indt.ndg.common.exception.UserNotFoundException;
import br.org.indt.ndg.server.client.MSMBusinessDelegate;
import br.org.indt.ndg.server.client.SurveyVO;
import br.org.indt.ndg.server.client.TransactionLogVO;
import br.org.indt.ndg.server.pojo.NdgUser;

public class ProcessDownloadSurvey {
	private String imei = null;

	MSMBusinessDelegate mb = null;

	private static final Logger log = Logger.getLogger("client");

	public ProcessDownloadSurvey(String imei) {
		this.imei = imei;

		mb = new MSMBusinessDelegate();
	}

	public String processAckCommand() {
		StringBuffer result = new StringBuffer();

		result.append("<html>");
		result.append("<head><title>GuestBookServlet</title></head>");
		result.append("<body>");
		result.append("Param Do: " + "Ack");
		result.append("Param Imei: " + imei);
		result.append("</body>");
		result.append("</html>");

		updateStatusSendingSurvey(TransactionLogVO.STATUS_SUCCESS);

		return new String(result);
	}

	public String processDownloadCommand() {
		StringBuffer result = new StringBuffer();

		try {
			for (Iterator iterator = mb.listSurveysByImeiDB(imei,
					TransactionLogVO.STATUS_AVAILABLE).iterator(); iterator
					.hasNext();) {
				NdgUser userlogged = mb.getUserByImei(imei);

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

		return new String(result);
	}

	public String processListCommand() {
		String result = new String();
		StringBuffer stb = new StringBuffer();

		stb.append("<surveys>\n");

		try {
			ArrayList<SurveyVO> listOfSurveys = (ArrayList<SurveyVO>) mb
					.listSurveysByImeiDB(imei,
							TransactionLogVO.STATUS_AVAILABLE);

			for (Iterator iterator = listOfSurveys.iterator(); iterator
					.hasNext();) {
				SurveyVO surveyVO = (SurveyVO) iterator.next();
				String linha = "<survey id=\"" + surveyVO.getIdSurvey() + "\""
						+ " title=\"" + surveyVO.getTitle() + "\"/>";
				stb.append(linha + "\n");
			}

			stb.append("</surveys>");

			result = stb.toString();
		} catch (MSMApplicationException e) {
			e.printStackTrace();
		} catch (MSMSystemException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	private void updateStatusSendingSurvey(String status) {
		try {
			mb.updateStatusSendingSurvey(imei, status);

			log.debug("Processing Ack Command to Imei " + imei);
			log
					.debug("############################################################");
		} catch (MSMApplicationException e) {
			e.printStackTrace();
		} catch (MSMSystemException e) {
			e.printStackTrace();
		}
	}
}
