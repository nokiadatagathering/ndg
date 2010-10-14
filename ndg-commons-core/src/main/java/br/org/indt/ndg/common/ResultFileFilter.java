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

/**
 * Class to filter results according to RESULT_STARTS_WITH constant.
 * @author faleiros
 *
 */
public class ResultFileFilter implements FilenameFilter {
	/**
	 * @see accept method in FilenameFiler superclass.
	 */
    public boolean accept(File dir, String name) {
        return (name.startsWith(Resources.RESULTS_STARTS_WITH));
    }
}
