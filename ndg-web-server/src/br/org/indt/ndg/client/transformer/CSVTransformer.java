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
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.TreeMap;
import java.util.Vector;

import br.org.indt.ndg.common.Category;
import br.org.indt.ndg.common.CategoryAnswer;
import br.org.indt.ndg.common.Field;
import br.org.indt.ndg.common.FieldType;
import br.org.indt.ndg.common.Item;
import br.org.indt.ndg.common.Resources;
import br.org.indt.ndg.common.ResultXml;
import br.org.indt.ndg.common.SurveyXML;


public class CSVTransformer extends ResultsTransformer {
	private String sep = "|";
	
	public CSVTransformer(SurveyXML survey, Boolean exportWithImages) {
		super(survey, exportWithImages);
	}

	protected void processResults(String path, Collection<ResultXml> results){
		String file = path;
		FileWriter fstream = null;
		BufferedWriter out = null;

		DateFormat dateFormat = new SimpleDateFormat("K:mm a, z");
		Date time = new Date();

		try {
			fstream = new FileWriter(file, true);
			out = new BufferedWriter(fstream);
			/** Header **/
			out.write("ResultId" + sep + "SurveyId" + sep + "Title" + sep + "Date" + sep + "Time" + sep +
					"User" + sep + "Imei" + sep + "PhoneNumber" + sep + "Lat" + sep + "Lon" + sep);

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
				time.setTime(Long.parseLong(result.getTime()));
				out.write(result.getResultId() + sep + result.getSurveyId() + sep + result.getTitle() + sep +
						result.getDate() + sep + dateFormat.format(time) + sep + result.getUser() + sep + result.getImei() + sep +
						result.getPhoneNumber() + sep + result.getLatitude() + sep + result.getLongitude() + sep);
				categorycounter++;
				int fieldcounter = 0;
				for (CategoryAnswer category : result.getCategories().values()) {
					categorycounter++;

					int subCatSize = category.getSubCategories().values().size();

					Object[] subcategories = category.getSubCategories().values().toArray();

					if( subCatSize > 0 ) {
						StringBuffer localBuffer = new StringBuffer();
						for (int fieldIndex = 0; fieldIndex < categories.get( category.getId()).getFields().size(); fieldIndex++ ) {
							for( int subCatIndex = 0; subCatIndex< category.getSubCategories().size(); subCatIndex++ ) {
								Field field = ((Vector<Field>)(subcategories[subCatIndex])).get(fieldIndex);
								String value = getStringValue(result, category, field );
								value = value.trim().replaceAll("\n", "");
								localBuffer.append(value);
								out.write(value);
								if( subCatSize > 1 ) {
									localBuffer.append("##");
								}
							}
							localBuffer.append(sep);
						}
						out.append(localBuffer.toString());
					} else {
						for( int fieldIndex = 0; fieldIndex < categories.get( category.getId()).getFields().size(); fieldIndex++ ) {
							out.append(sep);
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

		DateFormat dateFormat = new SimpleDateFormat("K:mm a, z");
		Date time = new Date();

		/** Header **/
		buffer.append("ResultId").append(sep).append("SurveyId").append(sep).append("Title")
		.append(sep).append("Date").append(sep).append("Time").append(sep).append("User")
		.append(sep).append("Imei").append(sep).append("PhoneNumber").append(sep).append("Lat")
		.append(sep).append("Lon").append(sep);

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
			time.setTime(Long.parseLong(result.getTime()));
			buffer.append(result.getResultId()).append(sep).append(result.getSurveyId()).append(sep)
					.append(result.getTitle()).append(sep).append(result.getDate()).append(sep)
					.append(dateFormat.format(time)).append(sep).append(result.getUser()).append(sep)
					.append(result.getImei()).append(sep).append(result.getPhoneNumber()).append(sep)
					.append(result.getLatitude()).append(sep).append(result.getLongitude()).append(sep);
			categorycounter = 0;
			for (CategoryAnswer category : result.getCategories().values()) {
				categorycounter++;

				int subCatSize = category.getSubCategories().values().size();

				Object[] subcategories = category.getSubCategories().values().toArray();

				if( subCatSize > 0 ) {
					StringBuffer localBuffer = new StringBuffer();
					for (int fieldIndex = 0; fieldIndex < categories.get( category.getId()).getFields().size(); fieldIndex++ ) {
						for( int subCatIndex = 0; subCatIndex< category.getSubCategories().size(); subCatIndex++ ) {
							Field field = ((Vector<Field>)(subcategories[subCatIndex])).get(fieldIndex);
							String value = getStringValue(result, category, field );
							value = value.trim().replaceAll("\n", "");
							localBuffer.append(value);
							if( subCatSize > 1 ) {
								localBuffer.append("##");
							}
						}
						localBuffer.append(sep);
					}
					buffer.append(localBuffer.toString());
				} else {
					for( int fieldIndex = 0; fieldIndex < categories.get( category.getId()).getFields().size(); fieldIndex++ ) {
						buffer.append(sep);
					}
				}
			}
			buffer.append("\n");
		}
		return buffer.toString().getBytes();
	}

	private String getStringValue(ResultXml result, CategoryAnswer category, Field field) {
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
			value = storeImagesAndGetValueToExport(result.getSurveyId(), category.getId(),
												result.getResultId(), field.getId(), field.getImages());
		}
		return value;
	}
}
