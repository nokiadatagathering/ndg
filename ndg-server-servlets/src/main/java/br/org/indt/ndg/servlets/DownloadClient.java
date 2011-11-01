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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.org.indt.ndg.common.exception.MSMApplicationException;
import br.org.indt.ndg.common.exception.MSMSystemException;
import br.org.indt.ndg.common.exception.SurveyFileAlreadyExistsException;
import br.org.indt.ndg.server.client.MSMBusinessDelegate;
import br.org.indt.ndg.server.client.TransactionLogVO;
import br.org.indt.ndg.server.client.UserVO;

public class DownloadClient extends HttpServlet
{
	// indicates to the device that the stream has been written without errors
	private final String SUCCESS = "1";
	
	// indicates to the device that the stream hasn't been written
	private final String FAILURE = "-1";
	
	// indicates user or password invalid
	private final String USERINVALID = "-2";

	private PrintWriter writer = null;
	private MSMBusinessDelegate msmBD;
	private static Log log = LogFactory.getLog(DownloadClient.class);

	public void init() 
	{
		msmBD = new MSMBusinessDelegate();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		writer = response.getWriter();
		writer.println(this.htmlBegin());
		
		if (request.getParameter("do") != null) 
		{
			if (request.getParameter("do").equals("download")) 
			{
				htmlPrint("<center><a href=\"client/1_0_0/NDG.jad\">Click to install NDG Client!</a></center>");
			}
		}
		else 
		{
			writer.println("PostSurveys Servlet is up and running...");
		}
		
		writer.println(this.htmlEnd());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		log.info("Started processing stream from " + request.getRemoteAddr());
		log.info("User: " + request.getParameter("user"));

		InputStreamReader dis = new InputStreamReader(request.getInputStream(), "UTF-8");
		DataOutputStream dos = new DataOutputStream(response.getOutputStream());
		
		if (dis != null)
		{
			BufferedReader reader = new BufferedReader(dis);
			StringBuffer tempBuffer = new StringBuffer();
			String line = null;
			
			if (validateUser(request.getParameter("user"), request.getParameter("psw")))
			{
				while ((line = reader.readLine()) != null)
				{
					tempBuffer.append(line + '\n');
					log.info(line);
				}
			
				try
				{
					msmBD.postSurvey(request.getParameter("user"), tempBuffer, createTransactionLogVO(request), false);
					dos.writeBytes(SUCCESS);
				}
				catch (MSMApplicationException e) 
				{
					dos.writeBytes(FAILURE);
					e.printStackTrace();
				}
				catch (MSMSystemException e) 
				{
					dos.writeBytes(FAILURE);
					e.printStackTrace();
				} 
			}
			else 
			{
				dos.writeBytes(USERINVALID);
				log.info("User invalid, returning " + USERINVALID + " to " + request.getRemoteAddr());
			}
			
			reader.close();
			dos.close();
		} 
		else 
		{
			dos.writeBytes(FAILURE);
			log.info("Failed processing stream from " + request.getRemoteAddr());
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
		html.append("<title>Nokia Data Gathering Client - Installation OTA</title>");
		html.append("</head>");
		html.append("<br>");
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
