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

import java.io.Serializable;

/**
 * Represents the possibles types on a Survey.xml file
 * @author mfaleiros
 * @version 1.0
 * @created 05-Jun-2007 9:15:02 AM
 */
public enum FieldType implements Serializable{
	STR, INT, CHOICE, DATE, TIME, EXCLUSIVE, MULTIPLE, DECIMAL, IMAGE
}
