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

package br.org.indt.ndg.server.sms;

import java.util.ArrayList;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import br.org.indt.ndg.common.SurveyXML;
import br.org.indt.ndg.common.exception.MSMApplicationException;
import br.org.indt.ndg.common.exception.MSMSystemException;
import br.org.indt.ndg.common.exception.SurveyNotFoundException;
import br.org.indt.ndg.common.exception.SurveyReadException;
import br.org.indt.ndg.server.sms.vo.SMSMessageVO;
import br.org.indt.ndg.server.survey.SurveyHandler;
import br.org.indt.ndg.server.survey.SurveyProtocolParser;
import br.org.indt.ndg.server.survey.SurveyReader;
import br.org.indt.ndg.server.util.PropertiesUtil;

public class SurveySmsSlicer
{
	private StringBuffer surveyEncoded = null;
	private int sms_length = 100;
	private ArrayList<SMSMessageVO> arrayOfSMS = new ArrayList<SMSMessageVO>();
	private String smsMobileTo = "";
	private static String SMS_LENGTH = "SMS_LENGTH";
	private String surveyId = "";
	private static String TYPE_OF_SMS = "3";
	private static final Logger log = Logger.getLogger("smslog");

	public SurveySmsSlicer(String userAdmin, String idSurvey, String deviceNumber) throws SurveyReadException
	{
		Properties properties = PropertiesUtil.loadFileProperty(PropertiesUtil.PROPERTIES_CORE_FILE);
		
		SurveyXML survey = null;
		
		try 
		{
			InitialContext initialContext = new InitialContext();
			SurveyHandler surveyHandler = (SurveyHandler) initialContext.lookup("ndg-core/SurveyHandlerBean/remote");
			survey = surveyHandler.loadSurveyAndResultsDB(userAdmin, idSurvey);
		}
		catch (NamingException e) 
		{
			e.printStackTrace();
		}
		catch (MSMApplicationException e) 
		{
			e.printStackTrace();
		} 
		catch (MSMSystemException e) 
		{
			e.printStackTrace();
		}
		
		surveyId = survey.getId();

		SurveyReader sr = new SurveyReader();

		SurveyProtocolParser regexParser = null;

		try 
		{
			regexParser = new SurveyProtocolParser(sr.getSurveyString(userAdmin, idSurvey));
		}
		catch (SurveyNotFoundException e) 
		{
			e.printStackTrace();
		}

		Properties protocol = PropertiesUtil.loadFileProperty(PropertiesUtil.SURVEY_PROTOCOL_FILE);
		
		surveyEncoded = regexParser.encodeSurvey(protocol, true);
		sms_length = Integer.parseInt(properties.getProperty(SMS_LENGTH));

		this.smsMobileTo = deviceNumber;
	}

	public ArrayList<SMSMessageVO> getSlicedSurvey() 
	{
		for (int start = 0, end = 0, order = 1; end < surveyEncoded.length(); order++) 
		{
			String seq = Integer.toString(order);

			String smsHeader = TYPE_OF_SMS + surveyId + (seq.length() > 1 ? seq : "0" + seq);
			end = (start + sms_length - smsHeader.length()) <= surveyEncoded.length() ? start + sms_length - smsHeader.length() : surveyEncoded.length();

			SMSMessageVO sMessageVO = new SMSMessageVO();
			String strMessage = "";
			
			if (end == surveyEncoded.length()) 
			{
				strMessage = smsHeader + surveyEncoded.substring(start, end) + "#";
			} 
			else 
			{
				strMessage = smsHeader + surveyEncoded.substring(start, end);
			}
			
			byte[] b = new byte[strMessage.length()];

			for (int j = 0; j < b.length; j++) 
			{
				byte bSms = new Byte((byte) strMessage.charAt(j));
				b[j] = bSms;
			}
			
			log.info("Survey message #" + arrayOfSMS.size() +" :" + strMessage);
			
			sMessageVO.message = strMessage;
			sMessageVO.to = this.smsMobileTo;
			sMessageVO.from = this.smsMobileTo;
			arrayOfSMS.add(sMessageVO);
			
			start += sms_length - smsHeader.length();
		}
		
		return arrayOfSMS;
	}
}
