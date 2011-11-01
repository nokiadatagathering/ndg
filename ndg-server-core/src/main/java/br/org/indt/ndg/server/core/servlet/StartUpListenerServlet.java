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

package br.org.indt.ndg.server.core.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import br.org.indt.ndg.server.sms.SMSModemHandler;
import br.org.indt.ndg.server.sms.SmsHandlerAbs;

public class StartUpListenerServlet extends HttpServlet 
{
	private static final long serialVersionUID = 1L;
	private SmsHandlerAbs smsHandle;
	
	public void init() throws ServletException 
	{
		smsHandle = SMSModemHandler.getInstance();
	}
}
