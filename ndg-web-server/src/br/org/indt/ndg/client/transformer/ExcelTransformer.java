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

package br.org.indt.ndg.client.transformer;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import br.org.indt.ndg.common.Category;
import br.org.indt.ndg.common.CategoryAnswer;
import br.org.indt.ndg.common.Field;
import br.org.indt.ndg.common.FieldType;
import br.org.indt.ndg.common.Item;
import br.org.indt.ndg.common.Resources;
import br.org.indt.ndg.common.ResultXml;
import br.org.indt.ndg.common.SurveyXML;

public class ExcelTransformer extends ResultsTransformer {
	
	public ExcelTransformer(SurveyXML survey, Boolean exportWithImages) {
		super(survey, exportWithImages);
	}

	public byte[] getBytes(){
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ArrayList<ResultXml> results = survey.getResults();
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("Sheet1");

		DateFormat dateFormat = new SimpleDateFormat("K:mm a, z");
		Date date = new Date();

		/** Header **/
		HSSFRow row = sheet.createRow((short)0);
		int fieldcounter = 0;
		row.createCell((short)fieldcounter++).setCellValue("ResultId");
		row.createCell((short)fieldcounter++).setCellValue("SurveyId");
		row.createCell((short)fieldcounter++).setCellValue("Title");
		row.createCell((short)fieldcounter++).setCellValue("Date Saved");
//		row.createCell((short)fieldcounter++).setCellValue("Time Saved");
		row.createCell((short)fieldcounter++).setCellValue("Date Sent");
		row.createCell((short)fieldcounter++).setCellValue("User");
		row.createCell((short)fieldcounter++).setCellValue("Imei");
		row.createCell((short)fieldcounter++).setCellValue("PhoneNumber");
		row.createCell((short)fieldcounter++).setCellValue("Lat");
		row.createCell((short)fieldcounter++).setCellValue("Lon");

		/** Header - Fields **/
		TreeMap<Integer,Category> categories = survey.getCategories();
		for (Category category : categories.values()) {
			for (Field field : category.getFields()) {
				row.createCell((short)fieldcounter++).setCellValue(field.getDescription());
			}
		}

		int countrow = 0;
		row = sheet.createRow((short)++countrow);

		/** Content **/
		for (ResultXml result : results) {
			fieldcounter = 0;
			row.createCell((short)fieldcounter++).setCellValue(result.getResultId());
			row.createCell((short)fieldcounter++).setCellValue(result.getSurveyId());
			row.createCell((short)fieldcounter++).setCellValue(result.getTitle());
			//date saved
			row.createCell((short)fieldcounter++).setCellValue(new Date(Long.parseLong(result.getTime())).toString());
//			date.setTime(Long.parseLong(result.getTime()));
//			row.createCell((short)fieldcounter++).setCellValue(dateFormat.format(date));

			//date sent
			row.createCell((short)fieldcounter++).setCellValue(result.getTimeSent());

			row.createCell((short)fieldcounter++).setCellValue(result.getUser());
			row.createCell((short)fieldcounter++).setCellValue(result.getImei());
			row.createCell((short)fieldcounter++).setCellValue(result.getPhoneNumber());
			row.createCell((short)fieldcounter++).setCellValue(result.getLatitude());
			row.createCell((short)fieldcounter++).setCellValue(result.getLongitude());
			int categoryBegin = fieldcounter;
			int fieldOffset = 0;
			for (CategoryAnswer category : result.getCategories().values()) {
				if( category.getSubCategories().values().size() == 0 ) {
					int skip = categories.get(category.getId()).getFields().size();
					categoryBegin+=skip;
					continue;
				}
				for( Vector<Field> subCategory: category.getSubCategories().values()) {
					fieldOffset = 0;
					for (Field field : subCategory ) {
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
							int countitem = 0;
							for(Item item : field.getChoice().getItems()) {
								if (item.getValue() != null) {
									if(item.getOtr() != null) {
										if (item.getOtr().equals("1")) {
											String s = survey.getItemValue(field.getCategoryId(), field.getId(), item.getIndex());
											tmp.append(s + " " + item.getValue());
											tmp.append( (++countitem < field.getChoice().getItems().size() ? ", " : "") );
										}
									} else {
										tmp.append(item.getValue());
									}
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
						value = value.trim().replaceAll("\n", "");
						HSSFCell currentCell = row.getCell((short)(categoryBegin +  fieldOffset));
						if( currentCell == null ) {
							currentCell = row.createCell((short)(categoryBegin + fieldOffset));
							currentCell.setCellValue(value);
						} else {
							String currentValue = currentCell.getStringCellValue();
							currentCell.setCellValue(currentValue + "##" + value + "##");
						}
						++fieldOffset;

					}//field
				}//subcategory
				categoryBegin+=fieldOffset;
			}//category
			row = sheet.createRow((short)++countrow);
		}
		try {
			wb.write(out);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out.toByteArray();
	}
	
	
	
	
	protected void processResults(String path, Collection<ResultXml> results){
		String file = path;
		HSSFWorkbook wb = new HSSFWorkbook();
	    HSSFSheet sheet = wb.createSheet("Sheet1");
	    FileOutputStream out = null;

	    DateFormat dateFormat = new SimpleDateFormat("K:mm a, z");
		Date date = new Date();

		try {
			out = new FileOutputStream(file);
			/** Header **/
			HSSFRow row = sheet.createRow((short)0);
			int fieldcounter = 0;
			row.createCell((short)fieldcounter++).setCellValue("ResultId");
			row.createCell((short)fieldcounter++).setCellValue("SurveyId");
			row.createCell((short)fieldcounter++).setCellValue("Title");
			row.createCell((short)fieldcounter++).setCellValue("Date Saved");
//			row.createCell((short)fieldcounter++).setCellValue("Time Saved");
			row.createCell((short)fieldcounter++).setCellValue("Date Sent");
			row.createCell((short)fieldcounter++).setCellValue("User");
			row.createCell((short)fieldcounter++).setCellValue("Imei");
			row.createCell((short)fieldcounter++).setCellValue("PhoneNumber");
			row.createCell((short)fieldcounter++).setCellValue("Lat");
			row.createCell((short)fieldcounter++).setCellValue("Lon");
			
			/** Header - Fields **/
			TreeMap<Integer,Category> categories = survey.getCategories();
			
			for (Category category : categories.values()) {
				
				for (Field field : category.getFields()) {
					row.createCell((short)fieldcounter++).setCellValue(field.getDescription());
				}
			}
						
			int countrow = 0;
			row = sheet.createRow((short)++countrow);
			
			/** Content **/
			
			for (ResultXml result : results) {
				fieldcounter = 0;
				row.createCell((short)fieldcounter++).setCellValue(result.getResultId());
				row.createCell((short)fieldcounter++).setCellValue(result.getSurveyId());
				row.createCell((short)fieldcounter++).setCellValue(result.getTitle());
				//date saved
				row.createCell((short)fieldcounter++).setCellValue(new Date(Long.parseLong(result.getTime())));
//				date.setTime(Long.parseLong(result.getTime()));
//				row.createCell((short)fieldcounter++).setCellValue(dateFormat.format(date));

				//date sent
				row.createCell((short)fieldcounter++).setCellValue(result.getTimeSent());

				row.createCell((short)fieldcounter++).setCellValue(result.getUser());
				row.createCell((short)fieldcounter++).setCellValue(result.getImei());
				row.createCell((short)fieldcounter++).setCellValue(result.getPhoneNumber());
				row.createCell((short)fieldcounter++).setCellValue(result.getLatitude());
				row.createCell((short)fieldcounter++).setCellValue(result.getLongitude());

				int categoryBegin = fieldcounter;
				int fieldOffset = 0;
				for (CategoryAnswer category : result.getCategories().values()) {
					if( category.getSubCategories().values().size() == 0 ) {
						int skip = categories.get(category.getId()).getFields().size();
						categoryBegin+=skip;
						continue;
					}
					for( Vector<Field> subCategory: category.getSubCategories().values()) {
						fieldOffset = 0;
						for (Field field : subCategory ) {
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
								int countitem = 0;
								for(Item item : field.getChoice().getItems()) {
									if (item.getValue() != null) {
										if(item.getOtr() != null) {
											if (item.getOtr().equals("1")) {
												String s = survey.getItemValue(field.getCategoryId(), field.getId(), item.getIndex());
												tmp.append(s + " " + item.getValue());
												tmp.append( (++countitem < field.getChoice().getItems().size() ? ", " : "") );
											}
										} else {
											tmp.append(item.getValue());
										}
									} else {
										String s = survey.getItemValue(field.getCategoryId(), field.getId(), item.getIndex());
										tmp.append(s);
										tmp.append(" ");
									}
								}
								value = tmp.toString() == null ? "" : tmp.toString();
							}

							HSSFCell currentCell = row.getCell((short)(categoryBegin +  fieldOffset));
							if( currentCell == null ) {
								currentCell = row.createCell((short)(categoryBegin + fieldOffset));
								currentCell.setCellValue(value);
							} else {
								String currentValue = currentCell.getStringCellValue();
								currentCell.setCellValue(currentValue + "##" + value + "##");
							}
							++fieldOffset;
						}
					}//subcategories
					categoryBegin+=fieldOffset;
				}
				row = sheet.createRow((short)++countrow);
			}
			wb.write(out);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(out != null) out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}	
		}
	}
}
