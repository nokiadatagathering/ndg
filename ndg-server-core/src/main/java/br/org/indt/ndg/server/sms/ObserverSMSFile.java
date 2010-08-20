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

package br.org.indt.ndg.server.sms;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;

import br.org.indt.jms.JMSSenderHelper;

/**
 * @author samourao
 * 
 */
public class ObserverSMSFile implements IObserverSMS {

	private static String FILE_RESULTS = "\\messages";

	private static String FILE_SYNC = "\\sync";

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.org.indt.mobisus.msm.sms.IObserverSMS#update(boolean,
	 *      java.io.File)
	 */
	@Override
	public void update(boolean hasSMS, File directory) {
		processSMSFile(directory);

	}

	private void processSMSFile(File directory) {
		JMSSenderHelper jmsHelper;
		FileInputStream fis;
		String[] fileList = directory.list();
		ArrayList<Object> msgList = new ArrayList<Object>();
		for (String file : fileList) {
			try {
				StringBuffer textMessage = new StringBuffer();
				fis = new FileInputStream(directory + "\\" + file);
				InputStreamReader in = new InputStreamReader(fis, "UTF-8");
				BufferedReader inputFile = new BufferedReader(in);
				ArrayList<String> list = new ArrayList<String>();
				String line = null;
				int memIndex = 0;
				Date dtime = new Date(System.currentTimeMillis());
				while ((line = inputFile.readLine()) != null) {
//					if (i > 21) {
						list.add(line);
/*					}
					i++;*/
				}

				byte[] b = new byte[list.size()];

				for (int j=0; j < b.length; j++) {
					b[j] = new Byte(list.get(j)).byteValue();
				}

				String from = file.substring(1, file.indexOf('_'));
				String message = new String(b);
					InboundMessageFile inboundMessage = new InboundMessageFile(dtime, from,
						message, memIndex++, directory.getPath(), b);

				msgList.add(inboundMessage);
				inputFile.close();
				File messageFile = new File(directory + "\\" + file);
				boolean success = messageFile.delete();
				if (success) {
					System.out.println("File deleted");
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		SmsFileHandler.getInstance().processSMS(msgList);
	}

}
