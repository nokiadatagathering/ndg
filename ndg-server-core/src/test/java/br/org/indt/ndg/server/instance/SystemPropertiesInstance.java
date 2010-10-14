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

package br.org.indt.ndg.server.instance;

import java.io.File;
import java.io.FileInputStream;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SystemPropertiesInstance {
	private SystemPropertiesInstance(){

	}

	private static SystemPropertiesInstance instance = null;
	private static String fileSeparator = System.getProperty("file.separator");

	private static String errorFound = null;

	private static String surveysHome = null;
	private static String propertiesFile = null;
	private static String deviceDrive = null;
	private static String sessionTimeout = null;
	private static String jbosstHome = System.getenv("JBOSS_HOME");
	private static String jbosstHomeDir = System.getProperty("jboss.server.home.dir");
	private static String realPath = null;
	public static ServletContext context;
	private static HttpSession reqSession = null;
	public static HttpServletRequest request;
	public static HttpServletResponse response;
	private static Log log = LogFactory.getLog(SystemPropertiesInstance.class);
	
	private static String user = "";
	private static String role = "";

	public static SystemPropertiesInstance getInstance() {
		if (instance == null) {
			instance = new SystemPropertiesInstance();
			try {
				errorFound = null;
				instance.loadProperties();
			} catch (Exception e) {
				instance = null;
				errorFound = e.getMessage();
				log.error(e);
			} 
			log.info("ErrorFound = " + errorFound );
			log.info("Surveys Home = " + surveysHome);
			log.info("Jboss Home = "+ jbosstHome);
			log.info("Device Drive = "+ deviceDrive);
		}
		return instance;
	}

	private void loadProperties() throws Exception {

		//propertiesFile = jbosstHome + fileSeparator + "server" + fileSeparator + "default" + fileSeparator + "conf" + fileSeparator + "mobisus.properties";
		propertiesFile = jbosstHomeDir + fileSeparator + "conf" + fileSeparator + "mobisus.properties";
		
		log.info("Propeties File = " + propertiesFile);
		Properties properties = new Properties();
		if(!new File(propertiesFile).exists()) {
			throw new Exception(propertiesFile);
		} else {
			properties.load(new FileInputStream(propertiesFile));
			if (properties.getProperty("surveysHome") == null) {
				throw new Exception();
			} else {
				surveysHome = properties.getProperty("surveysHome");
			}

			deviceDrive = properties.getProperty("deviceDrive");
			sessionTimeout = properties.getProperty("sessionTimeout");
		}
	}

	public static String getVersion() {
		String version = "0.0.0";
		//propertiesFile = jbosstHome + fileSeparator + "server" + fileSeparator + "ndg" + fileSeparator + "conf" + fileSeparator + "mobisus.properties";
		Properties p = new Properties();
		try {
			log.info("Getting version " + context);
			//p.load(new FileInputStream(propertiesFile));
			p.load(context.getResourceAsStream("/version.properties"));
			version = (p.getProperty("version") != null ? p.getProperty("version") : version);
			log.info("Version " + version);
		} catch (IOException e) {
			log.error("version.properties "+ e);
		}
		return version;
	}
	
	public String getSurveysHome() {
		return surveysHome;
	}

	public String getPropertiesFile() {
		return propertiesFile;
	}

	public String getJbossHome() {
		return jbosstHome;
	}

	public String getTempDir() {
		return SystemPropertiesInstance.getInstance().getJbossHome() + "temp" + SystemPropertiesInstance.fileSeparator;
	}

	public String getDeviceDrive() {
		return deviceDrive;
	}

	public static String getErrorFound() {
		return errorFound;
	}

	public static String getRealPath() {
		//SystemProperties.context must be set on login servlet
		realPath = context.getRealPath(fileSeparator);
		log.info("RealPath= " + realPath);
		return realPath;
	}

	public static String getTimestamp() {
		StringBuffer s = new StringBuffer();
		Calendar c = Calendar.getInstance();
		s.append(c.get(Calendar.HOUR));
		s.append(":");
		s.append(c.get(Calendar.MINUTE));
		s.append(":");
		s.append(c.get(Calendar.SECOND));
		s.append(" ");
		s.append(c.get(Calendar.DAY_OF_MONTH));
		s.append("-");
		s.append(c.get(Calendar.MONTH));
		s.append("-");
		s.append(c.get(Calendar.YEAR));

		return s.toString();
	}

	public static String toDate(Date d) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(d);
		String fieldDate = 	calendar.get(Calendar.DAY_OF_MONTH) + "/" + 
		(calendar.get(Calendar.MONTH) + 1)  + "/" + 
		calendar.get(Calendar.YEAR) + " " +
		calendar.get(Calendar.HOUR) + ":" +
		calendar.get(Calendar.MINUTE) + ":" +
		calendar.get(Calendar.SECOND);
		return fieldDate;
	}

	public static String getUser() {
		if (user.equals("")) {
			if(request != null){
				reqSession = request.getSession();
				if(reqSession != null)
					user = (String) reqSession.getAttribute("user");
			}
				
		}
		return (user == null ? "" : user);
	}
	
	public static String getRole() {
		if (role.equals("")) {
			if(request != null){
				reqSession = request.getSession();
				if(reqSession != null)
					role = (String) reqSession.getAttribute("role");	
			}
		}
		return (role == null ? "" : role);
	}
	
	public static int getSessionTimeout() {
		if(sessionTimeout==null){
			sessionTimeout = "30";
		}
		return Integer.valueOf(sessionTimeout);
	}
}
