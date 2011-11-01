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

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.org.indt.ndg.common.exception.MSMApplicationException;
import br.org.indt.ndg.common.exception.MSMSystemException;
import br.org.indt.ndg.server.client.MSMBusinessDelegate;
import br.org.indt.ndg.server.client.TemporaryOpenRosaBussinessDelegate;
import br.org.indt.ndg.server.client.TransactionLogVO;
import br.org.indt.ndg.server.client.UserVO;

public class PostSurveys extends HttpServlet
{
	// indicates to the device that the stream has been written without errors
	private final String SUCCESS = "1";

	// Indicates to the device that the stream hasn't been written
	private final String FAILURE = "-1";

	// indicates user or password invalid
	private final String USERINVALID = "-2";

	private PrintWriter writer = null;
	private MSMBusinessDelegate msmBD;
	private TemporaryOpenRosaBussinessDelegate openRosaBD;
	private static Log log = LogFactory.getLog(PostSurveys.class);
	private static final String sep = System.getProperty("file.separator");
	
	public void init()
	{
		msmBD = new MSMBusinessDelegate();
		openRosaBD = new TemporaryOpenRosaBussinessDelegate();
	}

	/**
	 * 1) Call "http://server-address:8080/ndg-servlets/PostSurveys?do=config to get server config
	 * 2) Call "http://server-address:8080/ndg-servlets/PostSurveys?do=uploadOpenRosa to get upload page for OpenRosa surveys
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		writer = response.getWriter();
		writer.println(this.htmlBegin());
		
		if (request.getParameter("do") != null) 
		{
			if (request.getParameter("do").equals("config")) 
			{
				htmlPrint("Server Configuration");
				htmlPrint("-------------------------------------------------------------------------------");
				htmlPrint("Surveys Home = " + SystemProperties.getInstance().getSurveysHome());
				htmlPrint("JBoss Home = " + SystemProperties.getInstance().getJbossHome());
				htmlPrint("Properties File = " + SystemProperties.getInstance().getPropertiesFile());
				htmlPrint("Real Path = " + getServletContext().getRealPath(sep));
				htmlPrint("Context = " + getServletContext().getContext(""));
				htmlPrint("Context Name = " + getServletContext().getServletContextName());
				
				Enumeration e = this.getServletContext().getAttributeNames();

				while (e.hasMoreElements())
				{
					String attr = (String) e.nextElement();
					htmlPrint("Attribute: " + attr + this.getServletContext().getAttribute(attr));
				}
			} else if (request.getParameter("do").equals("uploadOpenRosa")) {
				getServletConfig().getServletContext().getRequestDispatcher("/OpenRosaSurveyUploader.jsp").forward(request, response);
			} else {
				writer.println("PostSurveys Servlet is up and running...");
			}
		} else {
			writer.println("PostSurveys Servlet is up and running...");
		}
		writer.println(this.htmlEnd());
	}

	/**
	 * 1) POST with parameters 'user' and 'psw' set to post NDG proprietary surveys, survey as POST body (?)
	 * 2) POST without parameters and to post OpenRosa surveys, survey as body encoded with "multipart/form-data"
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		InputStreamReader inputStreamReader = new InputStreamReader(request.getInputStream(), "UTF-8");

		if ( request.getParameter("user") == null ) {
			openRosaBD.setPortAndAddress(SystemProperties.getServerAddress());
			boolean success = openRosaBD.parseAndPersistSurvey(inputStreamReader, request.getContentType());
			request.setAttribute("uploadResult", success);
			request.getRequestDispatcher("/OpenRosaSurveyUploader.jsp").forward(request, response);
		} else {
			DataOutputStream dataOutputStream = new DataOutputStream(response.getOutputStream());
			BufferedReader reader = new BufferedReader(inputStreamReader);
			StringBuffer tempBuffer = new StringBuffer();
			String line = null;
			
			if (validateUser(request.getParameter("user"), request.getParameter("psw")))
			{
				while ((line = reader.readLine()) != null)
				{
					tempBuffer.append(line + '\n');
				}
				
				try
				{
					msmBD.postSurvey(request.getParameter("user"), tempBuffer, createTransactionLogVO(request), false);
					dataOutputStream.writeBytes(SUCCESS);
				}
				catch (MSMApplicationException e) 
				{
					dataOutputStream.writeBytes(FAILURE);
					e.printStackTrace();
				}
				catch (MSMSystemException e) 
				{
					dataOutputStream.writeBytes(FAILURE);
					e.printStackTrace();
				}
			}
			else
			{
				dataOutputStream.writeBytes(USERINVALID);
			}
			
			reader.close();
			dataOutputStream.close();
		}
	}

	private TransactionLogVO createTransactionLogVO(HttpServletRequest request)
	{
		TransactionLogVO t = new TransactionLogVO();
		t.setTransmissionMode(TransactionLogVO.MODE_HTTP);
		t.setAddress(request.getRemoteAddr());
		t.setTransactionType(TransactionLogVO.TYPE_RECEIVE_SURVEY);
		t.setUser(request.getParameter("user"));
		
		return t;
	}

	private boolean validateUser(String username, String password)
	{
		boolean valid = false;
		
		MSMBusinessDelegate msmBD = new MSMBusinessDelegate();
		UserVO user;
		
		try
		{
			user = msmBD.validateLogin(username, password);
			log.info("validating user " + username);
			valid = user.getRetCode().equals(UserVO.AUTHENTICATED);
		}
		catch (MSMApplicationException e)
		{
			log.error(e.getErrorCode());
			log.error(e);
		}
		
		return valid;
	}

	private StringBuffer htmlBegin()
	{
		StringBuffer html = new StringBuffer();
		html.append("<html>");
		html.append("<head>");
		html.append("<meta http-equiv='Content-Type' content='text/html; charset=ISO-8859-1'>");
		html.append("<title>Nokia Data Gathering Server</title>");
		html.append("</head>");
		html.append("<body>");
		
		return html;
	}

	private StringBuffer htmlEnd()
	{
		StringBuffer html = new StringBuffer();
		html.append("</body>");
		html.append("</html>");
		
		return html;
	}

	private void htmlPrint(String s)
	{
		writer.println("<p>");
		writer.println(s);
		writer.println("</p>");
	}
}
