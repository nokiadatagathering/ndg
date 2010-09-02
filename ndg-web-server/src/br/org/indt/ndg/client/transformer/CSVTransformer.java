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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;

import org.jboss.util.Base64;

import br.org.indt.ndg.common.Category;
import br.org.indt.ndg.common.Field;
import br.org.indt.ndg.common.FieldType;
import br.org.indt.ndg.common.Item;
import br.org.indt.ndg.common.Resources;
import br.org.indt.ndg.common.ResultXml;
import br.org.indt.ndg.common.SurveyXML;


public class CSVTransformer extends Transformer {
	private String sep = "|";
	private Boolean exportWithImages;
	
	public CSVTransformer(SurveyXML survey, Boolean exportWithImages) {
		super(survey);
		this.exportWithImages = exportWithImages;
	}

	public void write(String path) {
		ArrayList<ResultXml> results = survey.getResults();
		processResults(path, results);	
	}

	public void write(String path, Collection<ResultXml> results) {
		processResults(path, results);
	}
	
	private void processResults(String path, Collection<ResultXml> results){
		String file = path;
		FileWriter fstream = null;
		BufferedWriter out = null;
		try {
			fstream = new FileWriter(file, true);
			out = new BufferedWriter(fstream);
			/** Header **/
			out.write("ResultId" + sep + "SurveyId" + sep + "Imei" + sep + "PhoneNumber" + sep + "Lat" + sep + "Lon" + sep);
						
			/** Header Fields**/
			TreeMap<Integer,Category> categories = survey.getCategories();
			int categorycounter = 0;
			
			for (Category category : categories.values()) {
				categorycounter++;
				int fieldcounter = 0;
				for (Field field : category.getFields()) {
					out.write(field.getDescription());
					if(categorycounter < categories.size()) {
						out.write(sep);
					} else {
						out.write( (++fieldcounter < category.getFields().size() ? sep : "") );
					}
				}
			}
			out.newLine();
			
			/** Content **/
			categorycounter = 0;
			for (ResultXml result : results) {
				out.write(result.getResultId() + sep + result.getSurveyId() + sep + result.getImei() + sep +
						result.getPhoneNumber() + sep + result.getLatitude() + sep + result.getLongitude() + sep);
				categorycounter++;
				int fieldcounter = 0;
				for (Category category : result.getCategories().values()) {
					for (Field field : category.getFields()) {
						String value = null;
						if (field.getFieldType() == FieldType.STR) {
							value = field.getValue() == null ? "" : field.getValue();
						} else if (field.getFieldType() == FieldType.DATE){
							value = (field.getValue() == null || field.getValue() == "") ? "" : Resources.toDate(Long.parseLong(field.getValue()));	
						} else if (field.getFieldType() == FieldType.TIME){
							value = (field.getValue() == null || field.getValue() == "") ? "" : Resources.toTime(field.getValue(),field.getConvention());							
						} else if (field.getFieldType() == FieldType.INT){
							value = (field.getValue() == null || field.getValue() == "") ? "0" : field.getValue();	
						} else if (field.getFieldType() == FieldType.DECIMAL){
							value = (field.getValue() == null || field.getValue() == "") ? "0" : field.getValue();	
						} else if (field.getFieldType() == FieldType.CHOICE){
							StringBuffer tmp = new StringBuffer();
							for(Item item : field.getChoice().getItems()) {
								if (item.getValue() != null) {
									tmp.append(item.getValue());
								} else {
									String s = survey.getItemValue(field.getCategoryId(), field.getId(), item.getIndex());
									tmp.append(s);
									tmp.append(" ");
								}
							}
							value = tmp.toString() == null ? "" : tmp.toString();
						}
						out.write(value);
						if(categorycounter < categories.size()) {
							out.write(sep);
						} else {
							out.write( (++fieldcounter < category.getFields().size() ? sep : "") );
						}
					}
				}
								
				out.newLine();
			}	
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(out != null) out.close();
				if(fstream != null) fstream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}	
		}
	}

	@Override
	public byte[] getBytes() {
		StringBuilder buffer = new StringBuilder();
		ArrayList<ResultXml> results = survey.getResults();

		/** Header **/
		buffer.append("ResultId").append(sep).append("SurveyId").append(sep).append("Imei").append(sep)
				.append("PhoneNumber").append(sep).append("Lat").append(sep).append("Lon").append(sep);
			
		/** Header Fields**/
		TreeMap<Integer,Category> categories = survey.getCategories();
		int categorycounter = 0;
			
		for (Category category : categories.values()) {
			categorycounter++;
			int fieldcounter = 0;
			for (Field field : category.getFields()) {
				buffer.append(field.getDescription());
				if(categorycounter < categories.size()) {
					buffer.append(sep);
				} else {
					buffer.append((++fieldcounter < category.getFields().size() ? sep : "") );
				}
			}
		}
		buffer.append("\n");
			
		/** Content **/
		for (ResultXml result : results) {
			buffer.append(result.getResultId()).append(sep).append(result.getSurveyId()).append(sep)
					.append(result.getImei()).append(sep).append(result.getPhoneNumber()).append(sep)
					.append(result.getLatitude()).append(sep).append(result.getLongitude()).append(sep);
			categorycounter = 0;
			for (Category category : result.getCategories().values()) {
				categorycounter++;
				int fieldcounter = 0;
				for (Field field : category.getFields()) {
					String value = null;
					if (field.getFieldType() == FieldType.STR) {
						value = field.getValue() == null ? "" : field.getValue();
					} else if (field.getFieldType() == FieldType.DATE){
						value = (field.getValue() == null || field.getValue() == "") ? "" : Resources.toDate(Long.parseLong(field.getValue()));	
					} else if (field.getFieldType() == FieldType.TIME){
						value = (field.getValue() == null || field.getValue() == "") ? "" : Resources.toTime(field.getValue(),field.getConvention());						
					} else if (field.getFieldType() == FieldType.INT){
						value = (field.getValue() == null || field.getValue() == "") ? "0" : field.getValue();	
					} else if (field.getFieldType() == FieldType.DECIMAL){
						value = (field.getValue() == null || field.getValue() == "") ? "0" : field.getValue();	
					} else if (field.getFieldType() == FieldType.CHOICE){
						StringBuffer tmp = new StringBuffer();
						for(Item item : field.getChoice().getItems()) {
							if (item.getValue() != null) {
								tmp.append(item.getValue());
							} else {
								String s = survey.getItemValue(field.getCategoryId(), field.getId(), item.getIndex());
								tmp.append(s);
								tmp.append(" ");
							}
						}
						value = tmp.toString() == null ? "" : tmp.toString();
					} else if (field.getFieldType() == FieldType.IMAGE) {
						String imagePath = File.separator + PHOTOS_DIR + File.separator + 
										   result.getResultId() + 
						   				   UNDERLINE_SEPARATOR + category.getId() + 
						   				   UNDERLINE_SEPARATOR + field.getId() + JPG_EXTENSION;
		
						value = "<img>";
						
						if (exportWithImages == true && !field.getValue().equals("")) {

							value = imagePath;
							
							try
							{
								FileOutputStream arqImg = new FileOutputStream(result.getSurveyId() + imagePath);
								
								arqImg.write(Base64.decode(field.getValue()));
								arqImg.close();
							}
							catch (Exception e)
							{
								e.printStackTrace();
							}
						}
					}
					value = value.trim().replaceAll("\n", "");
					buffer.append(value);
					if(categorycounter < categories.size()) {
						buffer.append(sep);
					} else {
						buffer.append((++fieldcounter < category.getFields().size() ? sep : ""));
					}
				}
			}
			buffer.append("\n");
		}	
		return buffer.toString().getBytes();
	}

}
