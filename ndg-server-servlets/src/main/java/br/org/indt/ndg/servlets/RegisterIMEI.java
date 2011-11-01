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

import java.io.DataOutputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.org.indt.ndg.common.exception.ImeiAlreadyExistException;
import br.org.indt.ndg.common.exception.MsisdnAlreadyRegisteredException;
import br.org.indt.ndg.common.exception.MsisdnNotFoundException;
import br.org.indt.ndg.server.client.MSMBusinessDelegate;

public class RegisterIMEI extends HttpServlet{


	private static final long serialVersionUID = 1L;
	private MSMBusinessDelegate msmBD;
	
	private static final int SUCCESS = 1;
	private static final int MSISDN_ALREADY_REGISTERED = 2;
	private static final int FAILURE = -1;
	private static final int MSISDN_NOT_FOUND = -2;
	private static final int IMEI_ALREADY_EXISTS = -3;
	
	private static final String MSISDN = "msisdn";
	private static final String IMEI = "imei";
	
	
	
	public void init(){
		msmBD = new MSMBusinessDelegate();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		//PrintWriter writer = response.getWriter();
		//writer.println("RegisterIMEI Servlet is up and running...\n");
		processRequest(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}
	
	private void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws IOException{
		DataOutputStream dataOutputStream = new DataOutputStream(response.getOutputStream());
		//PrintWriter writer = response.getWriter();
		//writer.println("Parameters");
		//writer.println(MSISDN + ": " + request.getParameter(MSISDN));
		//writer.println(IMEI + ": " + request.getParameter(IMEI));
		//writer.println("\nResult");
		
		String msisdn = request.getParameter(MSISDN);
		String imei = request.getParameter(IMEI);
		
		if (msisdn != null && imei != null){
			try {
				msmBD.registerIMEI(msisdn, imei);
				dataOutputStream.writeInt(SUCCESS);
				//writer.println(SUCCESS + ": success");
			} catch (MsisdnAlreadyRegisteredException e){
				dataOutputStream.writeInt(MSISDN_ALREADY_REGISTERED);
				e.printStackTrace();
			} catch (ImeiAlreadyExistException e){
				dataOutputStream.writeInt(IMEI_ALREADY_EXISTS);
				//writer.println(IMEI_ALREADY_EXISTS + "imei already exists");
				e.printStackTrace();
			} catch (MsisdnNotFoundException e){
				dataOutputStream.writeInt(MSISDN_NOT_FOUND);
				//writer.println(MSISDN_NOT_FOUND + ": msisdn not found");
				e.printStackTrace();
			} catch (Exception e) {
				dataOutputStream.writeInt(FAILURE);
				//writer.println(FAILURE + ": fail");
				e.printStackTrace();
			} finally{
				dataOutputStream.close();
			}
		} else{
			dataOutputStream.writeInt(FAILURE);
			dataOutputStream.close();
			//writer.println(FAILURE + ": failure");
		}
		
	}
	
}
