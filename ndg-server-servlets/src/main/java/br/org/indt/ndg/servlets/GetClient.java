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

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.org.indt.ndg.common.exception.MSMApplicationException;
import br.org.indt.ndg.server.client.ImeiVO;
import br.org.indt.ndg.server.client.MSMBusinessDelegate;
import br.org.indt.ndg.server.client.TransactionLogVO;
import br.org.indt.ndg.server.util.SettingsProperties;

/**
 * @author samourao
 */
public class GetClient extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** Indicates to the device that the stream has been written without errors */
	private final String SUCCESS = "1";
	/** Indicates to the device that the stream hasn't been written. */
	private final String FAILURE = "-1";
	/** Indicates user or password invalid */
	private final String USERINVALID = "-2";
	/** Indicates md5 key invalid */
	private final String MD5INVALID = "-3";
	/** Indicates that some error during session execution. */
	private boolean servletError = false;
	/** surveys root */
	private ImeiVO imeiVo = null;
	private PrintWriter writer = null;

	private static Log log = LogFactory.getLog(GetClient.class);

	private MSMBusinessDelegate msmBD;

	/**
	 * Executed once when the Servlet is initialized.
	 */
	public void init() {
		msmBD = new MSMBusinessDelegate();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		writer = response.getWriter();
		if (request.getParameter("to") != null) {
			imeiVo = validateUser(request.getParameter("to"));
			if (imeiVo != null) {
				String clientOtaUrl = "";
				try {
					clientOtaUrl = msmBD
							.getSpecificPropertySetting(SettingsProperties.CLIENT_OTA);
				} catch (MSMApplicationException e) {
					e.printStackTrace();
				}
				response.sendRedirect(clientOtaUrl);
				try {
					createTransactionLogVO(request);
				} catch (MSMApplicationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else {
			writer.println(this.htmlBegin());
			htmlPrint("Servlet to download NDG Client is up and running...");
			writer.println(this.htmlEnd());
		}

	}

	private void createTransactionLogVO(HttpServletRequest request)
			throws MSMApplicationException {
		TransactionLogVO t = new TransactionLogVO();
		t.setTransmissionMode(TransactionLogVO.MODE_HTTP);
		t.setAddress(request.getRemoteAddr());
		t.setTransactionType(TransactionLogVO.DOWNLOAD_CLIENT);
		t.setUser(imeiVo.getUserName());
		t.setStatus(SUCCESS);
		t.setDtLog(new Timestamp(System.currentTimeMillis()));
		t.setImei(imeiVo.getImei());
		msmBD.logTransaction(t);
	}

	/**
	 * Validade User
	 */
	private ImeiVO validateUser(String imei) {
		msmBD = new MSMBusinessDelegate();
		ImeiVO imeiVO = null;
		try {
			imeiVO = msmBD.getImei(imei);
		} catch (MSMApplicationException e) {
			log.error(e.getErrorCode());
			log.error(e);
		}
		return imeiVO;
	}

	/**
	 * Write reponse in plain html to PC browser.
	 * 
	 * @return
	 */
	private StringBuffer htmlBegin() {
		StringBuffer html = new StringBuffer();
		html.append("<html>");
		html.append("<head>");
		html
				.append("<meta http-equiv='Content-Type' content='text/html; charset=ISO-8859-1'>");
		html.append("<title>Nokia Data Gathering Server</title>");
		html.append("</head>");
		html.append("<body>");
		return html;
	}

	/**
	 * Write reponse in plain html to PC browser.
	 * 
	 * @return
	 */
	private StringBuffer htmlEnd() {
		StringBuffer html = new StringBuffer();
		html.append("</body>");
		html.append("</html>");
		return html;
	}

	/**
	 * Write reponse in plain html to PC browser.
	 * 
	 * @return
	 */
	private void htmlPrint(String s) {
		writer.println("<p>");
		writer.println(s);
		writer.println("</p>");
	}

}
