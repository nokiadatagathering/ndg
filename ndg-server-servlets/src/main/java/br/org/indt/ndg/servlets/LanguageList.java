/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.org.indt.ndg.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.org.indt.ndg.server.client.LocalizationBussinessDelegate;

/**
 *
 * @author wojciech.luczkow
 */
public class LanguageList extends HttpServlet {

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
        final String root = this.getServletConfig().getInitParameter("localesFileDir");
        
        LocalizationBussinessDelegate localeDelegate = new LocalizationBussinessDelegate();
        HashMap<String, String> langMap = localeDelegate.getLanguageList();

//        File languageList = new File(root + "/" + "Languages.txt");
//        response.setContentLength((int) languageList.length());
//        FileInputStream fileInput = null;
        
        PrintWriter out = response.getWriter();
        BufferedReader reader = null;
        try {
        	for(Map.Entry<String, String> entry : langMap.entrySet()){
        		out.write(entry.getKey() + " " + entry.getValue() + "\n");
        	}
	        out.flush();
        } finally {
            out.close();
            if(reader != null){
            	reader.close();
            }
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

}
