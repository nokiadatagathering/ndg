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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import br.org.indt.ndg.common.exception.SurveyNotFoundException;
import br.org.indt.ndg.common.exception.SurveyReadException;
import br.org.indt.ndg.server.client.MSMBusinessDelegate;

public class SurveyReader 
{
	private StringBuffer surveyString = null;

	public SurveyReader() 
	{
		// empty
	}

	public StringBuffer getSurveyString(String username, String idSurvey) throws SurveyNotFoundException, SurveyReadException  
	{
		if (surveyString == null) 
		{
			surveyString = new StringBuffer();

			try 
			{				
				MSMBusinessDelegate msmbd = new MSMBusinessDelegate();
				
				BufferedReader bufferedReader = new BufferedReader(new StringReader(msmbd.getSurveyObject(idSurvey).getSurveyXml()));
		
				String line = null;
				
				while ((line = bufferedReader.readLine()) != null) 
				{
					if (!line.equals("")) 
					{
						surveyString.append(line.trim());
					}
				}
			}
			catch (FileNotFoundException e)
			{
				throw new SurveyNotFoundException(e);
			}
			catch (UnsupportedEncodingException e)
			{
				throw new SurveyReadException(e);
			}
			catch (Exception e) 
			{
				throw new SurveyReadException(e);
			}
		}
		
		return surveyString;
	}
}
