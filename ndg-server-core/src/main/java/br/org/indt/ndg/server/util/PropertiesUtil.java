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

package br.org.indt.ndg.server.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import br.org.indt.ndg.common.SystemUtils;
import br.org.indt.ndg.common.exception.MSMApplicationException;
import br.org.indt.ndg.server.survey.SurveyHandler;

public class PropertiesUtil {
	public static String PROPERTIES_CORE_FILE = "msm-core.properties";

	public static String SETTINGS_FILE = "msm-settings.properties";

	public static String SURVEY_PROTOCOL_FILE = "survey_protocol.properties";

	public static String JNDI_PROPERTIES = "jndi.properties";
	
	public static String PROPERTIES_VERSION_FILE = "version.properties";

	public static String SURVEY_ROOT = "SURVEY_ROOT";

	private static Properties core_properties = null;

	private static Properties jndi_properties = null;
	
	private static Properties version_properties = null;

	private static Properties settings_properties = null;

	private static Properties property_protocol_survey = null;

	private static final Logger log = Logger.getLogger("ndg");

	public static Properties loadFileProperty(String propName) {
		Properties result = null;
		if (core_properties == null) {
			try {
				core_properties = new Configuration(PROPERTIES_CORE_FILE);
			} catch (InstantiationException e) {
				e.printStackTrace();
				return null;
			}
		}
		if (jndi_properties == null) {
			try {
				jndi_properties = new Configuration(JNDI_PROPERTIES);
			} catch (InstantiationException e) {
				e.printStackTrace();
				return null;
			}
		}
		if (version_properties == null) {
			try {
				version_properties = new Configuration(PROPERTIES_VERSION_FILE);
			} catch (InstantiationException e) {
				e.printStackTrace();
				return null;
			}
		}
		try {
			settings_properties = new Configuration(SETTINGS_FILE);
		} catch (InstantiationException e) {
			e.printStackTrace();
			return null;
		}
		if (property_protocol_survey == null) {
			try {
				property_protocol_survey = new Configuration(
						SURVEY_PROTOCOL_FILE);
			} catch (InstantiationException e) {
				e.printStackTrace();
				return null;
			}
		}
		
		if (propName.equals(PROPERTIES_CORE_FILE)) 
		{
			result = core_properties;
		}
		else if (propName.equals(SETTINGS_FILE))
		{
			result = settings_properties;
		}
		else if (propName.equals(SURVEY_PROTOCOL_FILE))
		{
			result = property_protocol_survey;
		}
		else if (propName.equals(JNDI_PROPERTIES))
		{
			result = jndi_properties;
		}
		else if (propName.equals(PROPERTIES_VERSION_FILE))
		{
			result = version_properties;
		}
		
		return result;
	}

	public static Properties loadProperty() {
		InitialContext ctx;
		SurveyHandler surveyHandler = null;
		try {
			ctx = new InitialContext();
			surveyHandler = (SurveyHandler) ctx
					.lookup("ndg-core/SurveyHandlerBean/remote");
			core_properties = surveyHandler.getProperties();
		} catch (NamingException e) {
			log.error("Error while loading Properties.", e);
			// e.printStackTrace();
		} catch (MSMApplicationException e) {
			log.error("Error while loading Properties.", e);
		}

		/*
		 * try { property = new Configuration(FILE); } catch
		 * (InstantiationException e) { e.printStackTrace(); return null; }
		 */
		return core_properties;
	}

	public static String getString(String key) {
		if (core_properties == null) {
			core_properties = loadProperty();

			if (core_properties == null) {
				return null;
			}
		}
		return core_properties.getProperty(key);
	}

	public static String[] getStringsComaSepareted(String key) {
		if (core_properties == null) {
			core_properties = loadProperty();

			if (core_properties == null) {
				return null;
			}
		}
		// String minhaStr
		String tmp = core_properties.getProperty(key);
		StringTokenizer st = new StringTokenizer(tmp, ",");
		String[] strs = new String[st.countTokens()];
		int i = 0;
		while (st.hasMoreTokens()) {
			strs[i] = st.nextToken();
			i++;
		}
		return strs;
	}

	public static void updateSettings(Properties settingsUpdated)
	{
		FileOutputStream fileOutputStream = null;

		try 
		{
			fileOutputStream = new FileOutputStream(System.getProperty("jboss.server.home.dir")
			+ SystemUtils.FILE_SEP + "conf" + SystemUtils.FILE_SEP + SETTINGS_FILE);

			settingsUpdated.store(fileOutputStream, "");
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		finally 
		{
			try 
			{
				fileOutputStream.flush();
				fileOutputStream.close();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}
}