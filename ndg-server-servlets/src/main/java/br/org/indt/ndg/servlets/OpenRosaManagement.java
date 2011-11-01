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

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.org.indt.ndg.server.client.TemporaryOpenRosaBussinessDelegate;

public class OpenRosaManagement extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private TemporaryOpenRosaBussinessDelegate m_openRosaBD;
	private static Log log = LogFactory.getLog(OpenRosaManagement.class);

	private static final String FORWARD_FILEPATH = "/OpenRosaManagement.jsp";
	private static final String ACTION_PARAM = "action";
	// NOTE: be aware while making changes that values below are also used on JSP side
	private static final String SET_AVAILABLE_FOR_USER = "setSurveysForUser";
	private static final String EXPORT_RESULTS_FOR_USER = "exportResultsForUser";

	private static final String PRINT_AVAILABLE_COMMANDS_ATTR = "printCommands";
	private static final String SURVEYS_ATTR = "surveys";
	private static final String USER_IMEI_LIST_ATTR = "imeiList";
	private static final String SELECTED_IMEI_PARAM = "selectedImei";
	private static final String SELECTED_SURVEY_ID_PARAM  = "selectedSurveyId";
	private static final String RESULT_ATTR = "surveysForUserResult";

	public void init() {
		m_openRosaBD = new TemporaryOpenRosaBussinessDelegate();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		m_openRosaBD.setPortAndAddress(SystemProperties.getServerAddress());
		String action = request.getParameter(ACTION_PARAM);
		if ( action != null ) {
			if ( SET_AVAILABLE_FOR_USER.equals(action) ) {
				dispatchSurveysForUserSelectionPage(request, response);
			} else if ( EXPORT_RESULTS_FOR_USER.equals(action) ) {
				dispatchExportResultsForUserPage(request, response);
			} else {
				request.setAttribute(PRINT_AVAILABLE_COMMANDS_ATTR, "printAll");
				request.getRequestDispatcher(FORWARD_FILEPATH).forward(request, response);
			}
		} else {
			request.setAttribute(PRINT_AVAILABLE_COMMANDS_ATTR, "printAll");
			request.getRequestDispatcher(FORWARD_FILEPATH).forward(request, response);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		m_openRosaBD.setPortAndAddress(SystemProperties.getServerAddress());

		String action = request.getParameter(ACTION_PARAM);

		if ( SET_AVAILABLE_FOR_USER.equals(action) ) {
			String selectedImei = request.getParameter(SELECTED_IMEI_PARAM);
			String[] selectedSurveyIds = request.getParameterValues(SELECTED_SURVEY_ID_PARAM);
			if ( selectedImei != null && selectedSurveyIds != null ) {
				log.info("IMEI: " + selectedImei);
				for (int i =0; i<selectedSurveyIds.length; i++) {
					log.info("Survey id:" + selectedSurveyIds[i]);
				}
			}
			boolean result = m_openRosaBD.makeSurveysAvailableForImei(selectedImei, selectedSurveyIds);
			request.setAttribute(RESULT_ATTR , result);
			dispatchSurveysForUserSelectionPage(request, response);
		} else if ( EXPORT_RESULTS_FOR_USER.equals(action) ) {
			ServletOutputStream outputStream = null;
			FileInputStream fileInputStream = null;
			DataInputStream dataInputStream = null;
			File file = null;
			try {
				String zipFilename = m_openRosaBD.exportZippedResultsForUser("223344556677");
				file = new File(zipFilename);
		        int length = 0;
		        outputStream = response.getOutputStream();
		        ServletContext context = getServletConfig().getServletContext();
		        String mimetype = context.getMimeType( "application/octet-stream" );

		        response.setContentType( mimetype  );
		        response.setContentLength( (int)file.length() );
		        response.setHeader( "Content-Disposition", "attachment; filename=\"" + zipFilename + "\"" );

		        byte[] bbuf = new byte[1024];
		        fileInputStream = new FileInputStream(file);
		        dataInputStream = new DataInputStream(fileInputStream);

		        while ((dataInputStream != null) && ((length = dataInputStream.read(bbuf)) != -1)) {
		            outputStream.write(bbuf,0,length);
		        }
		        outputStream.flush();
			} catch (FileNotFoundException ex) {
				ex.printStackTrace();
			} finally {
				try {
					if ( fileInputStream != null )
						fileInputStream.close();
					if ( dataInputStream != null )
						dataInputStream.close();
					if ( fileInputStream != null )
						fileInputStream.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
		        file.delete();
			}
		}
	}

	private void dispatchSurveysForUserSelectionPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Map<String,String> surveyIdList = m_openRosaBD.getSurveyIdToUrlMap();
		String[] userList = m_openRosaBD.getUserList();
		request.setAttribute(SURVEYS_ATTR, surveyIdList);
		request.setAttribute(USER_IMEI_LIST_ATTR, userList);
		request.getRequestDispatcher(FORWARD_FILEPATH).forward(request, response);
	}

	private void dispatchExportResultsForUserPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String[] userList = m_openRosaBD.getUserList();
		request.setAttribute(USER_IMEI_LIST_ATTR, userList);
		request.setAttribute(EXPORT_RESULTS_FOR_USER, "export");
		request.getRequestDispatcher(FORWARD_FILEPATH).forward(request, response);
	}

}
