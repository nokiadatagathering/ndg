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

package br.org.indt.ndg.server.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author samourao
 * 
 */
public class SmsFileWriter {

	public static String ACK = "ACK";

	public static String SURVEY = "SURVEY";

	private static String OUT_DIR = "Outbox\\";

	private String toPhone = null;

	private String typeOfFile = null;

	private String nameOfFileInit = null;

	private String nameOfFileFinal = null;

	private Properties properties;

	private String directory;

	// private String directory = "C:\\home\\tomcat5\\mobisus\\" + OUT_DIR;

	private File outFile;

	/**
	 * 
	 */
	public SmsFileWriter(String typeOfFile, String nameOfFile) {
		properties = PropertiesUtil.loadFileProperty(PropertiesUtil.PROPERTIES_CORE_FILE);
		directory = properties.getProperty("SURVEY_ROOT") + OUT_DIR;
		System.out.println("Name Of File: " + nameOfFile);
		this.typeOfFile = typeOfFile;
		if (typeOfFile.equals(ACK)) {
			this.nameOfFileInit = "P_" + ACK + "_" + nameOfFile + "_";
			this.nameOfFileFinal = "M_" + ACK + "_" + nameOfFile;
		} else {
			this.nameOfFileInit = "P_" + SURVEY + "_" + nameOfFile + "_";
			this.nameOfFileFinal = "M_" + SURVEY + "_" + nameOfFile;
		}
		File directoryF = new File(directory);

		if (!directoryF.isDirectory()) {
			directoryF.mkdir();
		}
		outFile = new File(directory + this.nameOfFileInit);
	}

	public boolean saveSmsFile(String toPhone, byte[] buffer) {
		boolean result = false;
		this.toPhone = toPhone;
		try {
			FileOutputStream fos = new FileOutputStream(outFile);
			fos.write(buffer);
			fos.close();
			result = renameFile();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	private boolean renameFile() {
		boolean result = false;
		long timeStamp = System.currentTimeMillis();
		File fileRenamed = new File(directory + nameOfFileFinal + "_" + toPhone
				+ "_" + timeStamp);
		if (outFile.renameTo(fileRenamed)) {
			result = outFile.delete();
		}
		return result;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String content = "Conteudo do arquivo";
		String nameFile = "1234567890";
		SmsFileWriter smsFileWriter = new SmsFileWriter(SmsFileWriter.ACK,
				nameFile);
		smsFileWriter.saveSmsFile("559281263000", content.getBytes());
	}

}
