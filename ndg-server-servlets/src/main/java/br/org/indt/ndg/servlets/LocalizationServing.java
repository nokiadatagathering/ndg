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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.org.indt.ndg.common.exception.MSMApplicationException;
import br.org.indt.ndg.server.client.LocalizationBussinessDelegate;
import br.org.indt.ndg.server.client.MSMBusinessDelegate;
import br.org.indt.ndg.server.util.SettingsProperties;

/**
 *
 * @author wojciech.luczkow
 */
public class LocalizationServing extends HttpServlet {

    /**
	 * 
	 */
	private static final long serialVersionUID = 5826100786544899427L;
	private static final String FILE_TYPE_TEXTS = "Texts";
    private static final String FILE_TYPE_FONTS = "Fonts";
    private static final String FILE_TYPE_HEADER = "File-Type";
    private static final String LOCALE_HEADER = "locale";
	
	private MSMBusinessDelegate msmBD = null;
	public void init() {
		msmBD = new MSMBusinessDelegate();
	}
    
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String locale = request.getHeader(LOCALE_HEADER);
        String type = request.getHeader(FILE_TYPE_HEADER);

        File file = getFile(locale, type);
        if(file == null || !file.exists()){
        	response.sendError(HttpServletResponse.SC_NO_CONTENT);
        	return;
        }
        
        OutputStream out = response.getOutputStream();
        try {
            printFile(out, file);
        }finally {
            out.close();
        }        	
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private boolean printFile(OutputStream out, File file) throws IOException {
    	
        FileInputStream input = new FileInputStream(file);
        int byteRead;
        while ((byteRead = input.read()) != -1) {
            out.write(byteRead);
        }
        out.flush();
        input.close();
        
        return true;
    }
    
    private File getFile(String locale, String type){
    	String localeFile = "";
    	LocalizationBussinessDelegate localeDelegate = new LocalizationBussinessDelegate();
    	if(type.equals(FILE_TYPE_TEXTS)){
    		localeFile = localeDelegate.getLanguageFileName(locale);
    	}else{
    		localeFile = localeDelegate.getFontFileName(locale);
    	}
    	if(localeFile == null || localeFile.isEmpty()){
    		return null;
    	}
    	
		String localeOtaDirectory = "";
		String jbossDirectory = "";
		File file = null;
		try {
			localeOtaDirectory = msmBD.getSpecificPropertySetting(SettingsProperties.LOCALE_OTA);
			jbossDirectory = System.getProperty("jboss.home.dir");
	    	file = new File(jbossDirectory + localeOtaDirectory + localeFile);
		} catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
			e.printStackTrace();
		}       
        
    	return file;
    }
}
