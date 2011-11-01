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

package br.org.indt.ndg.server.sms;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.jcraft.jzlib.ZInputStream;

/**
 * @author samourao
 * 
 */
public abstract class SmsHandlerAbs {

	protected static final Logger log = Logger.getLogger("smslog");

	public static int OVERHEAD_CARRIER = 4;

	public static int HEADER = 36;

	/**
	 * Decompress received stream.
	 * 
	 * @param request
	 *            stream to be decompressed.
	 * @return decompressed stream
	 */
	public static String decompress(byte[] request) {
		BufferedInputStream objIn = null;
		ByteArrayOutputStream baos = null;
		String result = null;
		try {
			baos = new ByteArrayOutputStream();

			ByteArrayInputStream bais = new ByteArrayInputStream(request);
			ZInputStream zIn = new ZInputStream(bais);
			objIn = new BufferedInputStream(zIn);

			int i = 0;
			while ((i = objIn.read()) != -1) {
				baos.write((byte) i);
			}
			result = new String(baos.toByteArray());
			zIn.close();
			objIn.close();
		} catch (EOFException e) {
			System.out.println(e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println(e.toString());
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
		}
		return result;
	}

	protected static byte[] convertHexToBinary(String hexData) throws Exception {
		System.out.println("Length: " + (hexData.length() / 2));
		if (hexData.length() % 2 != 0) {
			throw new Exception("Must be an even number of hex digits");
		}
		byte[] binaryData = new byte[hexData.length() / 2];
		for (int i = 0; i < binaryData.length; ++i) {
			String byteStr = hexData.substring(i * 2, i * 2 + 2);
			int value;
			try {
				value = Integer.parseInt(byteStr, 16);
			} catch (NumberFormatException e) {
				e.printStackTrace();
				throw new Exception("");
			}
			binaryData[i] = (byte) value;
		}
		return binaryData;
	}

	public abstract void sendSMS(String dest, String textMsg) throws SMSSenderException ;
	
	public abstract void sendTextSMS(String dest, String textMsg, int port)throws SMSSenderException; 

}
