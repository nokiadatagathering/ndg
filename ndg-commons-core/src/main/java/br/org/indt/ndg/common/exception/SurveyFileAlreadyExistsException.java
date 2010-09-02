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

package br.org.indt.ndg.common.exception;

import br.org.indt.ndg.common.exception.MSMApplicationException;

public class SurveyFileAlreadyExistsException extends MSMApplicationException
{
	private static final String _ERROR_CODE = "MSM_CORE_MSG_SURVEY_ALREADY_EXIST";
	
	public SurveyFileAlreadyExistsException(String path)
	{
		super();
		setErrorCode(_ERROR_CODE);
//		super("Survey already exists, deploy not allowed: " + path);
	}

	public SurveyFileAlreadyExistsException(Exception e)
	{
		super(e);
		setErrorCode(_ERROR_CODE);
	}
	
    private static final long serialVersionUID = 3249038272819809156L;
}