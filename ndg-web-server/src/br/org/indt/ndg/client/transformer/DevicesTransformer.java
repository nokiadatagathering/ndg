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

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import br.org.indt.ndg.server.client.ImeiVO;

public class DevicesTransformer extends Transformer {

	Collection<ImeiVO> imeis;
	String[] header;
	
	public DevicesTransformer(Collection<ImeiVO> imeis, String[] header){
		this.imeis = imeis;
		this.header = header;
	}
	
	public byte[] getBytes() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
	    HSSFWorkbook wb = new HSSFWorkbook();
	    HSSFSheet sheet = wb.createSheet("Sheet1");

		HSSFRow row = sheet.createRow((short)0);
		/** Header **/
		int fieldcounter = 0;
		for(int i=0; i<header.length; i++){
			row.createCell((short)fieldcounter++).setCellValue(header[i]);
		}
		
		/************/
		int countrow = 0;
		row = sheet.createRow((short)++countrow);
		/** Content **/
		
		for (ImeiVO imei : imeis) {
			fieldcounter = 0;
			row.createCell((short)fieldcounter++).setCellValue(imei.getImei());
			row.createCell((short)fieldcounter++).setCellValue(imei.getMsisdn());
			row.createCell((short)fieldcounter++).setCellValue(imei.getUserName());
			row.createCell((short)fieldcounter++).setCellValue(imei.getDevice().getDeviceModel());
			row = sheet.createRow((short)++countrow);
		}
//		return wb.getBytes();
		try {
			wb.write(out);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return out.toByteArray();
		
	}


	@Override
	public void write(String path) {
		String file = path;
		
	    HSSFWorkbook wb = new HSSFWorkbook();
	    HSSFSheet sheet = wb.createSheet("Sheet1");
	    FileOutputStream out = null; 
			
		try {
			out = new FileOutputStream(file);
			
			HSSFRow row = sheet.createRow((short)0);
			/** Header **/
			int fieldcounter = 0;
			
			for(int i=0; i<header.length; i++){
				row.createCell((short)fieldcounter++).setCellValue(header[i]);
			}

			/************/
			int countrow = 0;
			row = sheet.createRow((short)++countrow);
			/** Content **/
			
			for (ImeiVO imei : imeis) {
				
				fieldcounter = 0;
				row.createCell((short)fieldcounter++).setCellValue(imei.getImei());
				row.createCell((short)fieldcounter++).setCellValue(imei.getMsisdn());
				row.createCell((short)fieldcounter++).setCellValue(imei.getUserName());
				row.createCell((short)fieldcounter++).setCellValue(imei.getDevice().getDeviceModel());
				
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
