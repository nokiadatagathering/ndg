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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import br.org.indt.ndg.common.exception.ImeiFileNotFoundException;
import br.org.indt.ndg.common.exception.MSMApplicationException;
import br.org.indt.ndg.common.exception.ResultNotParsedException;
import br.org.indt.ndg.common.exception.SurveyNotFoundException;
import br.org.indt.ndg.server.client.ImeiVO;
import br.org.indt.ndg.server.client.MSMBusinessDelegate;
import br.org.indt.ndg.server.client.TransactionLogVO;

public class Deploy 
{
	public static ArrayList<ResultXml> getResultsFromDevice(String deviceDrive, String userName, String surveyId) throws MSMApplicationException  
	{
		String pathDevice = deviceDrive + Resources.SURVEY_PATH.substring(1) + surveyId + Resources.SEPARATOR;
		String[] results = Resources.getResultFiles(pathDevice);

		ResultParser parser = new ResultParser();
		ArrayList<ResultXml> resultXMLList = new ArrayList<ResultXml>();

		for (int i = 0; i < results.length; i++)
		{
			ResultXml resultXml = null;

			try
			{
				resultXml = parser.parseResult(pathDevice + results[i]);
			}
			catch (SAXException e) 
			{
				e.printStackTrace();
				throw new ResultNotParsedException();
			}
			catch (ParserConfigurationException e) 
			{
				e.printStackTrace();
				throw new ResultNotParsedException();
				
			} catch (IOException e) {
				e.printStackTrace();
				throw new ResultNotParsedException();
			}

			resultXMLList.add(resultXml);
		}

		return resultXMLList;
	}

	public static void copySurveyToDevice(String deviceDrive, String userName, String surveyId,
			String imei) throws FileNotFoundException, IOException, SurveyNotFoundException,
			MSMApplicationException{
		MSMBusinessDelegate msmbd = new MSMBusinessDelegate();

		String surveyContent = msmbd.getSurveyObject(surveyId).getSurveyXml();

		String dirDevice = deviceDrive + Resources.SEPARATOR + Resources.SURVEY_PATH + surveyId + Resources.SEPARATOR;
		
		if ((surveyContent == "") || (surveyContent == null)){
			throw new SurveyNotFoundException();
		} else {
			File dir = new File(dirDevice);
			if (!dir.exists()) dir.mkdir();
			
			Deploy.copySurveyFromDBToDevice(surveyContent, dirDevice + Resources.SURVEY_FILE_NAME);
			
			TransactionLogVO t = new TransactionLogVO();
			t.setTransmissionMode(TransactionLogVO.MODE_CABLE);
			t.setTransactionType(TransactionLogVO.TYPE_SEND_SURVEY);
			t.setStatus(TransactionLogVO.STATUS_SUCCESS);
			t.setSurveyId(surveyId);
			t.setDtLog(new Timestamp(new Date().getTime()));
			t.setImei(imei);
			t.setUser(userName);
			msmbd.logTransaction(t);
		}
	}
	
	public static void copySurveyFromDBToDevice(String surveyContent, String dtFile) throws FileNotFoundException, IOException 
	{
		File f2 = new File(dtFile);
		
		InputStream in = new ByteArrayInputStream(surveyContent.getBytes("UTF-8"));
		OutputStream out = new FileOutputStream(f2);

		byte[] buf = new byte[1024];
		int len;
		
		while ((len = in.read(buf)) > 0)
		{
			out.write(buf, 0, len);
		}
		
		in.close();
		out.close();
	}
	
	public static boolean checkIMEI(String deviceDrive, String imei) throws ImeiFileNotFoundException, IOException 
	{
		String imeiPath = deviceDrive + Resources.SEPARATOR + Resources.DEVICE_ROOT + Resources.SEPARATOR;
		File imeiFile = new File(imeiPath + Resources.IMEI_FILE_NAME);
		
		if (!imeiFile.exists())
		{	
			throw new ImeiFileNotFoundException();
		}
		else 
		{
			InputStreamReader is = new InputStreamReader(new FileInputStream(imeiFile));
			BufferedReader br = new BufferedReader(is);
			
			String deviceIMEI = (String) br.readLine();
			
			br.close();
			is.close();
			
			if (deviceIMEI != null) 
			{
				if (deviceIMEI.equals(imei))
				{
					return true;
				}
				else
				{
					return false;
				}
			}
			else 
			{
				return false;
			}
		}
	}
	
	public static ImeiVO getIMEIFromDeviceDrive(String deviceDrive) throws ImeiFileNotFoundException, IOException 
	{
		ImeiVO imeiVO = null;
		
		String imeiPath = deviceDrive + Resources.SEPARATOR + Resources.DEVICE_ROOT + Resources.SEPARATOR;
		File imeiFile = new File(imeiPath + Resources.IMEI_FILE_NAME);
		
		if (!imeiFile.exists())
		{	
			throw new ImeiFileNotFoundException();
		}
		else 
		{
			InputStreamReader is = new InputStreamReader(new FileInputStream(imeiFile));
			BufferedReader br = new BufferedReader(is);
			
			String deviceIMEI = (String) br.readLine();
			
			br.close();
			is.close();
			
			if (deviceIMEI != null) 
			{
				MSMBusinessDelegate msmbd = new MSMBusinessDelegate();

				try 
				{
					imeiVO = msmbd.getImei(deviceIMEI);
				}
				catch (MSMApplicationException e)
				{
					e.printStackTrace();
				}
			}
			else 
			{
				throw new ImeiFileNotFoundException();
			}
		}
		
		return imeiVO;
	}
}