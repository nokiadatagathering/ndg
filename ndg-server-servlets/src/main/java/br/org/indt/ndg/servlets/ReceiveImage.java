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

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ReceiveImage extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	
	private static Log log = LogFactory.getLog(ReceiveImage.class);
	
	private final String SUCCESS = "1";
	private final String FAILURE = "-1";
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)	throws ServletException, IOException 
	{
		PrintWriter writer = response.getWriter();
		writer.println("ReceiveImage Servlet is up and running...");
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		log.debug("RECEIVE IMAGE: executing doPost method.");
		
		response.reset();
		response.setContentType("image/jpg");
		
		InputStream inputStream = request.getInputStream();
		DataOutputStream dataOutputStream = new DataOutputStream(response.getOutputStream());
			
		if (inputStream != null)
		{
			log.debug("RECEIVE IMAGE: creating file ImageFromClient.jpg...");
			
			FileOutputStream fileOutputStream = new FileOutputStream("ImageFromClient.jpg");

			byte buffer[] = new byte[1024*128];
			int i = 0;
			
			while ((i = inputStream.read(buffer)) != -1)
			{
				fileOutputStream.write(buffer, 0, i);
			}
			 
			fileOutputStream.close();
			inputStream.close();
			
			dataOutputStream.writeBytes(SUCCESS);
			dataOutputStream.close();
			
			log.debug("RECEIVE IMAGE: SUCCESS");
		}
		else 
		{
			dataOutputStream.writeBytes(FAILURE);
			
			log.debug("RECEIVE IMAGE: FAILURE");
		}
	}
}
