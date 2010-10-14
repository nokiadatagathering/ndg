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

package br.org.indt.ndg.common;

import java.io.File;
import java.io.FilenameFilter;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Resources implements Serializable{

	private static final long serialVersionUID = 1L;

	public final static String SEPARATOR = System.getProperty("file.separator");
	public final static String RESULTS_STARTS_WITH = "r_"; 
	public final static String SURVEYS_STARTS_WITH = "survey";
	public final static String ENCODING = "ISO-8859-1";
	public final static String DEVICE_ROOT = SEPARATOR + "ndg";
	public final static String SURVEY_PATH = DEVICE_ROOT + SEPARATOR + "survey";
	public final static String SURVEY_FILE_NAME = "survey.xml";
	public final static String IMEI_FILE_NAME = "imei";
	
	private final static HashMap<String, String> xmlEscape = new HashMap<String, String>();
	
	static 
	{
		xmlEscape.put("&amp;", "&");
		xmlEscape.put("&lt;", "<");
		xmlEscape.put("&gt;", ">");
		xmlEscape.put("&quot;", "\"");
		xmlEscape.put("&apos;", "\'");
	}

	public static String substituteXmlEscape(String line)
	{
		StringBuffer result = new StringBuffer(line);
		
		for (Iterator<String> iterator = xmlEscape.keySet().iterator(); iterator.hasNext();)
		{
			String key = iterator.next();
			Pattern pattern = Pattern.compile(key);
			Matcher matcher = pattern.matcher(result);
			result = new StringBuffer(matcher.replaceAll(xmlEscape.get(key)));
		}
		
		return result.toString();
	}
	
	private static Resources INSTANCE = null;	
	
	private Resources() 
	{
		// empty
	}
	
	public static Resources getInstance()
	{
		if (INSTANCE == null) 
		{
			INSTANCE = new Resources();
		}
		
		return INSTANCE;
	}
		
	public static String[] getResultFiles(String path)
	{
	    File dir = new File(path);
	    FilenameFilter filter = new ResultFileFilter(); 
	    
	    return dir.list(filter);
	}
	
	public static String[] getSurveyFiles(String path)
	{
	    File dir = new File(path);
	    FilenameFilter filter = new SurveyFileFilter();
	    
	    return dir.list(filter);
	}
	
	public static String toDate(Date d)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(d);
		calendar.add(Calendar.MONTH, 1);
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		String fieldDate = calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH)) + "/" + calendar.get(Calendar.YEAR);
		
		return fieldDate;
	}

	public static String toDate(long d)
	{
		Date date = new Date(d);

		return Resources.toDate(date);
	}
	
	public static String toTime(String szTime, String szConvention)
	{
		if (szConvention.toLowerCase().contains("pm"))
		{
			szTime = szTime + " PM";
		}
		else if (szConvention.toLowerCase().contains("am"))
		{
			szTime = szTime + " AM";
		}
			
		return szTime;
	}
}