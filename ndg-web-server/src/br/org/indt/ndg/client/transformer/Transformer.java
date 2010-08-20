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

package br.org.indt.ndg.client.transformer;

import br.org.indt.ndg.common.SurveyXML;

public abstract class Transformer {
	protected SurveyXML survey;

	public static final String PHOTOS_DIR = "photos";
	public static final String UNDERLINE_SEPARATOR = "_";
	public static final String JPG_EXTENSION = ".jpg";	
	
	public Transformer(SurveyXML survey) {
		this.survey = survey;
	}
	
	public Transformer(){};
	
	public void setSurvey(SurveyXML survey) {
		this.survey = survey;
	}
	
	public abstract void write(String path);
	
	public abstract byte[] getBytes();

}
