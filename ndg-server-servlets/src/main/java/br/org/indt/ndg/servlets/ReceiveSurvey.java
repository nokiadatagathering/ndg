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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import br.org.indt.ndg.server.util.PropertiesUtil;

/**
 * @author samourao
 * 
 */
public class ReceiveSurvey extends HttpServlet {
	private static String DO_COMMAND = "do";

	private static String IMEI_PARAM = "imei";

	private PrintWriter printwrite = null;

	private String imei = null;

	private Commands command = null;

	private String result = null;

	Properties properties = null;

	ProcessDownloadSurvey processDownloadSurvey = null;
	
	private static final Logger log = Logger.getLogger("client");

	@Override
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
		super.init(config);
		properties = PropertiesUtil
				.loadFileProperty(PropertiesUtil.PROPERTIES_CORE_FILE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		log.debug("Request(GET): " + request.getQueryString());

		if(!request.getParameterNames().hasMoreElements()) {

			printwrite = new PrintWriter(response.getWriter());
			printwrite.println("<html>");
			printwrite.println("<title>NDG Server</title>");
			printwrite.println("<body>");

			printwrite.println("<center> Receive Surveys up and running...</center>");
			printwrite.println("<br>");
			
			printwrite.println("</body>");
			printwrite.println("</html>");
			printwrite.flush();
			printwrite.close();
		}else {
			processRequest(request, response);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		log.debug("Request(POST): " + request.getQueryString());
		processRequest(request, response);
	}

	protected void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		processDownloadSurvey = new ProcessDownloadSurvey(request
				.getParameter(IMEI_PARAM));
		printwrite = new PrintWriter(response.getOutputStream());
		try {
			command = Commands.valueOf(request.getParameter(DO_COMMAND));
			switch (command) {
			case list:
				result = processDownloadSurvey.processListCommand();
				response.setContentType("text/xml;charset=UTF-8");
				break;
			case download:
				result = processDownloadSurvey.processDownloadCommand();
				response.setContentType("text/xml;charset=UTF-8");

				break;
			case ack:
				result = processDownloadSurvey.processAckCommand();
				response.setContentType("text/html;charset=UTF-8");
				break;
			}
			
			response.setBufferSize(result != null ? result.length() : 0);
			response.setContentLength(result != null ? result.length() : 0);
			
			
			printwrite.write(result);
			printwrite.flush();
			printwrite.close();
		} catch (IllegalArgumentException e) {
			response
					.sendError(response.SC_BAD_REQUEST,
							"Your request cannot be processed. Please check it and send it again .....");
			/* response.sendRedirect("error.html"); */
		}
	}

}
