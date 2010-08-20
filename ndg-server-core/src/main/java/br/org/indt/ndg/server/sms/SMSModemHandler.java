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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.smslib.AGateway;
import org.smslib.GatewayException;
import org.smslib.IInboundMessageNotification;
import org.smslib.IOutboundMessageNotification;
import org.smslib.InboundMessage;
import org.smslib.OutboundBinaryMessage;
import org.smslib.OutboundMessage;
import org.smslib.SMSLibException;
import org.smslib.TimeoutException;
import org.smslib.Message.MessageEncodings;
import org.smslib.Message.MessageTypes;
import org.smslib.OutboundMessage.MessagePriorities;
import org.smslib.modem.SerialModemGateway;

import br.org.indt.jms.JMSSenderHelper;
import br.org.indt.ndg.common.exception.ModemNotRepondingException;
import br.org.indt.ndg.server.sms.vo.SMSMessageVO;
import br.org.indt.ndg.server.util.PropertiesUtil;
import br.org.indt.smslib.GatewayFactory;
import br.org.indt.smslib.SMSService;

public class SMSModemHandler extends SmsHandlerAbs {

	private static SMSModemHandler smsHandle;

	protected static SMSService srv;

	// protected static String gatewayId;

	protected SerialModemGateway modemGateway = null;

	protected AGateway brokerGateway = null;

	private InboundNotification inboundNotification;

	private OutboundNotification outboundNotification;

	private Properties modemProperties, settingsProperties;

	private static final Logger log = Logger.getLogger("smslog");

	private static final String MODEM_MANUFACTURE = "modem.manufacture";

	private static final String MODEM_MODEL = "modem.model";

	private static final String MODEM_IMEI = "modem.imei";

	private static final String MODEM_SIGNAL_LEVEL = "modem.signal.level";

	private static final String MODEM_BATERY_LEVEL = "modem.batery.level";

	private static final String MODEM_IS_STARTED = "modem.is.started";

	public static final int SMS_NDG_PORT = 50001;

	public static final int SMS_NORMAL_PORT = 0;

	private String sendGatewayId;

	private String receiveGatewayId;

	private int rxtx = 0;

	/**
	 * 
	 */
	protected SMSModemHandler() {
		super();
		settingsProperties = PropertiesUtil
				.loadFileProperty(PropertiesUtil.SETTINGS_FILE);
		modemProperties = PropertiesUtil
				.loadFileProperty(PropertiesUtil.PROPERTIES_CORE_FILE);

		if (modemProperties.getProperty("SMS_MODEM_RECEIVE").equalsIgnoreCase(
				"true")) {
			rxtx += 1;
		}
		if (modemProperties.getProperty("SMS_MODEM_SEND").equalsIgnoreCase(
				"true")) {
			rxtx += 2;
		}
		if (modemProperties.getProperty("SMS_BROKER_RECEIVE").equalsIgnoreCase(
				"true")) {
			rxtx += 4;
		}
		if (modemProperties.getProperty("SMS_BROKER_SEND").equalsIgnoreCase(
				"true")) {
			rxtx += 8;
		}
		boolean useProxy = settingsProperties.getProperty("proxy.set").equals(
				"true");
		if (useProxy) {
			System.getProperties().put("proxySet",
					settingsProperties.getProperty("proxy.set"));
			System.getProperties().put("proxyHost",
					settingsProperties.getProperty("proxy.host"));
			System.getProperties().put("proxyPort",
					settingsProperties.getProperty("proxy.port"));
		}
		inboundNotification = new InboundNotification();
		outboundNotification = new OutboundNotification();
		srv = SMSService.getSmsService();
		setGateways();
		try {
			System.out.println(">>>>> Starting Gateways ...");
			srv.startService();
			log.info(">>>>> ModemGateway: " + modemGateway);
			if (modemGateway != null) {
				log.info(">>>>> ModemGateway (in): " + modemGateway);

				log.info("************** Modem Information ************ ");
				log.info("******** -  Manufacturer: "
						+ modemGateway.getManufacturer());
				log.info("******** -  Model: " + modemGateway.getModel());
				log
						.info("******** -  Serial No: "
								+ modemGateway.getSerialNo());
				log.info("******** -  SIM IMSI: " + modemGateway.getImsi());
				log.info("******** -  Signal Level: "
						+ modemGateway.getSignalLevel() + "%");
				log.info("******** -  Battery Level: "
						+ modemGateway.getBatteryLevel() + "%");
				log.info("***********************************************");
			}
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GatewayException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SMSLibException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setGateways() {
		brokerGateway = GatewayFactory.createNewClickatellGateway(
				settingsProperties.getProperty("sms.gateway.id"),
				settingsProperties.getProperty("sms.api.id"),
				settingsProperties.getProperty("sms.username"),
				settingsProperties.getProperty("sms.password"));

		modemGateway = (SerialModemGateway) GatewayFactory
				.createNewSerialModemGateway(modemProperties
						.getProperty("MODEM_ID"), modemProperties
						.getProperty("MODEM_COM_PORT"), new Integer(
						modemProperties.getProperty("MODEM_BAUD_RATE"))
						.intValue(), modemProperties
						.getProperty("MODEM_MANUFACTURER"), modemProperties
						.getProperty("MODEM_MODEL"));
		modemGateway.setSimPin("0000");

		switch (rxtx) {
		case 1:
			modemGateway.setInbound(true);
			modemGateway.setInboundNotification(inboundNotification);
			srv.addGateway(modemGateway);
			receiveGatewayId = modemGateway.getGatewayId();
			brokerGateway = null;
			break;
		case 2:
			modemGateway.setOutbound(true);
			modemGateway.setOutboundNotification(outboundNotification);
			srv.addGateway(modemGateway);
			sendGatewayId = modemGateway.getGatewayId();
			brokerGateway = null;
			break;
		case 3:
			modemGateway.setInbound(true);
			modemGateway.setOutbound(true);
			modemGateway.setInboundNotification(inboundNotification);
			modemGateway.setOutboundNotification(outboundNotification);
			srv.addGateway(modemGateway);
			receiveGatewayId = modemGateway.getGatewayId();
			sendGatewayId = modemGateway.getGatewayId();
			brokerGateway = null;
			break;
		case 4:
			brokerGateway.setInbound(true);
			brokerGateway.setInboundNotification(inboundNotification);
			receiveGatewayId = brokerGateway.getGatewayId();
			modemGateway = null;
			break;
		case 6:
			brokerGateway.setInbound(true);
			brokerGateway.setInboundNotification(inboundNotification);
			srv.addGateway(brokerGateway);
			sendGatewayId = brokerGateway.getGatewayId();
			modemGateway.setOutbound(true);
			modemGateway.setOutboundNotification(outboundNotification);
			srv.addGateway(modemGateway);
			receiveGatewayId = modemGateway.getGatewayId();
			break;
		case 8:
			brokerGateway.setOutbound(true);
			brokerGateway.setOutboundNotification(outboundNotification);
			srv.addGateway(brokerGateway);
			sendGatewayId = brokerGateway.getGatewayId();
			modemGateway = null;
			break;
		case 9:
			modemGateway.setInbound(true);
			modemGateway.setInboundNotification(inboundNotification);
			srv.addGateway(modemGateway);
			receiveGatewayId = modemGateway.getGatewayId();
			brokerGateway.setOutbound(true);
			brokerGateway.setOutboundNotification(outboundNotification);
			srv.addGateway(brokerGateway);
			sendGatewayId = brokerGateway.getGatewayId();
			break;
		case 12:
			brokerGateway.setInbound(true);
			brokerGateway.setOutbound(true);
			brokerGateway.setInboundNotification(inboundNotification);
			brokerGateway.setOutboundNotification(outboundNotification);
			srv.addGateway(brokerGateway);
			receiveGatewayId = brokerGateway.getGatewayId();
			sendGatewayId = brokerGateway.getGatewayId();
			modemGateway = null;
			break;
		}
	}

	public static SMSModemHandler getInstance() {
		if (smsHandle == null) {
			synchronized (SMSModemHandler.class) {
				if (smsHandle == null) {
					smsHandle = new SMSModemHandler();
				}
			}
		}
		return smsHandle;
	}

	public void sendSMS(String dest, String textMsg) throws SMSSenderException {
		// TODO @saugusto: change this to Ecuador
		if (dest.substring(0, 2).equals("+0")) {
			dest = "+55" + dest.substring(2);
		}
		byte[] msgBytes = null;
		try {
			msgBytes = textMsg.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			new SMSSenderException("Was not possible to encode Sms Message...");
		}
		OutboundBinaryMessage msg = new OutboundBinaryMessage(dest, msgBytes);

		Properties properties = PropertiesUtil
				.loadFileProperty(PropertiesUtil.PROPERTIES_CORE_FILE);

		msg
				.setSrcPort(Integer.parseInt(properties
						.getProperty("MSG_SRC_PORT")));
		msg
				.setDstPort(Integer.parseInt(properties
						.getProperty("MSG_DST_PORT")));
		msg.setPriority(MessagePriorities.HIGH);

		if (sendGatewayId != null) {
			srv.queueMessage(msg, sendGatewayId);
		} else {
			new SMSSenderException(
					"There is no SMS sender Gateway configured...");
		}
	}

	public void sendTextSMS(String dest, String textMsg, int port)
			throws SMSSenderException {
		// TODO @saugusto: change this to Ecuador
		if (dest.substring(0, 2).equals("+0")) {
			dest = "+55" + dest.substring(2);
		}
		OutboundMessage msg = new OutboundMessage(dest, textMsg);
		// msg.setFrom("NDG Server"); 
		msg.setEncoding(MessageEncodings.ENCUCS2);
		if (sendGatewayId != null) {
			srv.queueMessage(msg, sendGatewayId);
		} else {
			new SMSSenderException(
					"There is no SMS sender Gateway configured...");
		}
	}

	public Properties getModemProperties() throws ModemNotRepondingException {
		Properties modemProperties = new Properties();
		try {
			modemProperties.setProperty(MODEM_MANUFACTURE, modemGateway
					.getManufacturer());
			modemProperties.setProperty(MODEM_MODEL, modemGateway.getModel());
			modemProperties.setProperty(MODEM_IMEI, modemGateway.getSerialNo());
			modemProperties.setProperty(MODEM_BATERY_LEVEL, Integer
					.toString(modemGateway.getBatteryLevel())
					+ "%");
			modemProperties.setProperty(MODEM_SIGNAL_LEVEL, Integer
					.toString(modemGateway.getSignalLevel())
					+ "%");
			modemProperties.setProperty(MODEM_IS_STARTED, modemGateway
					.getStarted() ? "YES" : "NOT");

		} catch (TimeoutException e) {
			new ModemNotRepondingException(e);
			e.printStackTrace();
		} catch (GatewayException e) {
			new ModemNotRepondingException(e);
			e.printStackTrace();
		} catch (IOException e) {
			new ModemNotRepondingException(e);
			e.printStackTrace();
		} catch (InterruptedException e) {
			new ModemNotRepondingException(e);
			e.printStackTrace();
		}
		return modemProperties;
	}

	public class OutboundNotification implements IOutboundMessageNotification {
		public void process(String gatewayId, OutboundMessage msg) {
			System.out.println("Outbound handler called from Gateway: "
					+ gatewayId);
			System.out.println(msg);
		}
	}

	public class InboundNotification implements IInboundMessageNotification {

		JMSSenderHelper jmsHelper;

		/*
		 * public void process(String gatewayId, MessageTypes msgType, String
		 * memLoc, int memIndex) { }
		 */

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.smslib.IInboundMessageNotification#process(java.lang.String,
		 * org.smslib.Message.MessageTypes, org.smslib.InboundMessage)
		 */
		// @Override
		public void process(String gatewayId, MessageTypes msgType,
				InboundMessage im) {
			if (msgType == MessageTypes.INBOUND)
				log.info(">>> New Inbound message detected from Gateway: "
						+ gatewayId);
			else if (msgType == MessageTypes.STATUSREPORT)
				log
						.info(">>> New Inbound Status Report message detected from Gateway: "
								+ gatewayId);
			log.info(im);
			try {
				SMSMessageVO vo = new SMSMessageVO();
				vo.from = new String(im.getOriginator().substring(1,
						im.getOriginator().length()));
				vo.message = im.getText();
				log.info(">>> Message Encoding: " + im.getEncoding());
				if (im.getEncoding().equals(MessageEncodings.ENC8BIT)) {
					byte[] byte_array = convertHexToBinary(im.getPduUserData());
					if ((byte_array.length - OVERHEAD_CARRIER) > 0) {
						byte[] message_complete = new byte[byte_array.length
								- OVERHEAD_CARRIER];
						vo.payload = new byte[message_complete.length - HEADER];
						for (int f = OVERHEAD_CARRIER; f < byte_array.length; f++) {
							message_complete[f - OVERHEAD_CARRIER] = byte_array[f];
							if (f >= HEADER + OVERHEAD_CARRIER) {
								vo.payload[f - HEADER - OVERHEAD_CARRIER] = byte_array[f];
							}
						}
						vo.message = new String(message_complete);

					}
				} else if (im.getEncoding().equals(MessageEncodings.ENC7BIT)) {
					vo.message = im.getText();
				}

				jmsHelper = new JMSSenderHelper("queue/MSM_SMSReceiver");

				jmsHelper.queue(vo);
				srv.deleteMessage(im);

			} catch (Exception e) {
				log.info("Oops!!! Something gone bad..." + e.getMessage());
				new SMSSenderException("Something gone bad when receiving SMS");
			}
		}

	}

}
