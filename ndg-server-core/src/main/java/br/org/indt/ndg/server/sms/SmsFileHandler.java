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

import java.util.ArrayList;

import br.org.indt.jms.JMSSenderHelper;
import br.org.indt.ndg.server.sms.vo.SMSMessageVO;
import br.org.indt.ndg.server.util.SmsFileWriter;

/**
 * @author samourao
 * 
 */
public class SmsFileHandler extends SmsHandlerAbs {
	private static SmsFileHandler smsHandle;

	public static SmsFileHandler getInstance() {
		if (smsHandle == null) {
			synchronized (SmsFileHandler.class) {
				if (smsHandle == null) {
					smsHandle = new SmsFileHandler();
				}
			}
		}
		return smsHandle;
	}

	/**
	 * 
	 */
	public SmsFileHandler() {
		super();
		ObserverSMSFile observerSMSFile = new ObserverSMSFile();
		NewSMSInFileListener newMessageListener = new NewSMSInFileListener(
				observerSMSFile);
	}

	public void sendSMS(String dest, String textMsg) throws SMSSenderException {
		String typeOfMsg = "";
		String nameOfFile = "";
		switch (textMsg.charAt(0)) {
		case SMSMessageVO.ACK_RESULT_MSG:
			typeOfMsg = SmsFileWriter.ACK;
			nameOfFile = textMsg.substring(0, 21);
			break;
		case SMSMessageVO.SEND_SURVEY_MSG:
			typeOfMsg = SmsFileWriter.SURVEY;
			nameOfFile = textMsg.substring(0, 13);
			break;
		default:
			throw new SMSSenderException("Message not recognized");
		}
		SmsFileWriter smsFileWriter = new SmsFileWriter(typeOfMsg,nameOfFile);
		smsFileWriter.saveSmsFile(dest, textMsg.getBytes());
	}
	
	public void processSMS(ArrayList<Object> listOfInboundMessage) {
		JMSSenderHelper jmsHelper;
		ArrayList<SMSMessageVO> retMsgs = new ArrayList<SMSMessageVO>();
		try {
			for (int i = 0; i < listOfInboundMessage.size(); i++) {
				InboundMessageFile im = (InboundMessageFile)listOfInboundMessage.get(i);
				SMSMessageVO vo = new SMSMessageVO();
				vo.message = im.getText();
				
				vo.from = im.getOriginator();

				retMsgs.add(vo);

				byte[] byte_array = im.getMsgBytes();
				byte[] message_complete = new byte[byte_array.length
						- OVERHEAD_CARRIER];
				vo.payload = new byte[message_complete.length - 21];
				for (int f = OVERHEAD_CARRIER; f < byte_array.length; f++) {
					message_complete[f - OVERHEAD_CARRIER] = byte_array[f];
					if (f >= HEADER + OVERHEAD_CARRIER) {
						vo.payload[f - HEADER - OVERHEAD_CARRIER] = byte_array[f];
					}
				}
				vo.message = new String(message_complete);

				jmsHelper = new JMSSenderHelper("queue/MSM_SMSReceiver");

				jmsHelper.queue(vo);
			}
		} catch (Exception e) {
			System.out.println("Oops, some bad happened...");
			e.printStackTrace();
		}
	}

	@Override
	public void sendTextSMS(String dest, String textMsg, int port)
			throws SMSSenderException {
		System.out.println("Does nothing ...");
		
	}
}
