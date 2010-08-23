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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MD5 {
	private static Log logger = LogFactory.getLog(MD5.class);

	public static boolean checkMD5Survey(StringBuffer input, String md5)
			throws NoSuchAlgorithmException, IOException {
		String surveyFileMD5 = getMD5FromSurvey(input);

		logger.info("MD5 from survey file: " + md5);
		logger.info("MD5 generated by server application: " + surveyFileMD5);

		return md5.equalsIgnoreCase(surveyFileMD5) ? true : false;
	}

	public static String getMD5FromSurvey(StringBuffer input) throws IOException,
			NoSuchAlgorithmException {

		StringBuffer inputMD5 = new StringBuffer(input);

		// remove XML header
		String xmlStartFlag = "<?";
		String xmlEndFlag = "?>";

		int xmlFlagStartPosition = inputMD5.indexOf(xmlStartFlag);
		int xmlFlagEndPosition = inputMD5.indexOf(xmlEndFlag);

		if ((xmlFlagStartPosition >= 0) && (xmlFlagEndPosition >= 0)) {
			inputMD5.delete(xmlFlagStartPosition, xmlFlagEndPosition
					+ xmlEndFlag.length() + 1);
		}

		// remove checksum key
		String checksumFlag = "checksum=\"";
		int checksumFlagStartPosition = inputMD5.indexOf(checksumFlag)
				+ checksumFlag.length();
		int checksumKeySize = 32;

		inputMD5.replace(checksumFlagStartPosition, checksumFlagStartPosition
				+ checksumKeySize, "");

		// we need to remove last '\n' once NDG editor calculates its md5
		// checksum without it (both input and inputMD5)
		if (input.charAt(input.toString().length() - 1) == '\n')
		{
			input.deleteCharAt(input.toString().length() - 1);
		}
		
		if (inputMD5.charAt(inputMD5.toString().length() - 1) == '\n')
		{
			inputMD5.deleteCharAt(inputMD5.toString().length() - 1);
		}		

		MessageDigest messageDigest = java.security.MessageDigest
				.getInstance("MD5");
		messageDigest.update(inputMD5.toString().getBytes("UTF-8"));
		byte[] byteArrayMD5 = messageDigest.digest();

		String surveyFileMD5 = getByteArrayAsString(byteArrayMD5);

		return surveyFileMD5;
	}

	public static String getByteArrayAsString(byte hash[]) {
		final char[] HEX_CHARS = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
				'9', 'a', 'b', 'c', 'd', 'e', 'f', };

		char buf[] = new char[hash.length * 2];

		for (int i = 0, x = 0; i < hash.length; i++) {
			buf[x++] = HEX_CHARS[(hash[i] >>> 4) & 0xf];
			buf[x++] = HEX_CHARS[hash[i] & 0xf];
		}

		return new String(buf);
	}

	public static String createMD5(String key) throws NoSuchAlgorithmException 
	{
		MessageDigest messageDigest = java.security.MessageDigest.getInstance("MD5");
		
		try 
		{
			messageDigest.update(key.getBytes("UTF-8"));
		}
		catch (UnsupportedEncodingException e) 
		{
			e.printStackTrace();
		}
		
		byte[] byteArrayMD5 = messageDigest.digest();
		
		String surveyFileMD5 = getByteArrayAsString(byteArrayMD5);
		
		return surveyFileMD5;
	}

	public static String createIdSurvey() {
		Long l = System.currentTimeMillis() / 1000;
		String idSurvey = Long.toString(l);

		return idSurvey;
	}
}