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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SystemUtils
{
	public final static String FILE_SEP = System.getProperty("file.separator");

	public static String getTimestamp() 
	{
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

	public static String toDate(Date d) 
	{
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		 
        StringBuilder fieldDate = new StringBuilder(simpleDateFormat.format(d));

		return fieldDate.toString();
	}
}
