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

package br.org.indt.ndg.server.survey;

import java.util.Enumeration;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class SurveyProtocolParser {

	private StringBuffer stringBuffer = null;
	
	private static final Logger log = Logger.getLogger("smslog");

	public SurveyProtocolParser(StringBuffer stringBuffer) {
		this.stringBuffer = stringBuffer;
	}

	public StringBuffer encodeSurvey(Properties protocol) {
		Enumeration<Object> keys = protocol.keys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			Pattern pattern = Pattern.compile(key.replace("__", "="));
			Matcher matcher = pattern.matcher(stringBuffer);
			stringBuffer = new StringBuffer(matcher.replaceAll(protocol.getProperty(key.replace("__", "="))));
		}
		log.info("Survey: "+stringBuffer);
		return stringBuffer;
	}
	
	public StringBuffer decoddeSurvey(Properties protocol) {
		Enumeration<Object> keys = protocol.keys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			Pattern pattern = Pattern.compile(protocol.getProperty(key));
			Matcher matcher = pattern.matcher(stringBuffer);
			stringBuffer = new StringBuffer(matcher.replaceAll(key));
		}
		return stringBuffer;
	}
	
	/**
	 * @param protocol
	 * @param direction if true Compress if false Decompress
	 * @return
	 */
	public StringBuffer encodeSurvey(Properties protocol, boolean direction) {
		Enumeration<Object> keys = protocol.keys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			if(direction){
				Pattern pattern = Pattern.compile(key.replace("__", "="));
				Matcher matcher = pattern.matcher(stringBuffer);
				stringBuffer = new StringBuffer(matcher.replaceAll(protocol.getProperty(key)));
			}else{
				Pattern pattern = Pattern.compile(protocol.getProperty(key));
				Matcher matcher = pattern.matcher(stringBuffer);
				stringBuffer = new StringBuffer(matcher.replaceAll(key));
			}
		}
		log.info("Survey: "+stringBuffer);
		return stringBuffer;
	}

}
