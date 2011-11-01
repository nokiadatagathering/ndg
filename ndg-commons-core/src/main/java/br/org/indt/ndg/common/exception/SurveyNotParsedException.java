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

package br.org.indt.ndg.common.exception;

public class SurveyNotParsedException extends MSMApplicationException
{
	private static final long serialVersionUID = 1L;

	private static final String _ERROR_CODE = "MSM_CORE_MSG_UNABLE_TO_PARSER_SURVEYS";
	
	public SurveyNotParsedException() 
	{
		super();
		setErrorCode(_ERROR_CODE);
	}
	
	public SurveyNotParsedException(Exception e)
    {
		super(e);
		setErrorCode(_ERROR_CODE);
    }
}