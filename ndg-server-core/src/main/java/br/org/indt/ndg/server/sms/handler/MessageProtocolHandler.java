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

package br.org.indt.ndg.server.sms.handler;

import java.io.BufferedReader;
import java.io.StringReader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import br.org.indt.jms.JMSSenderHelper;
import br.org.indt.jms.JMSSenderHelperException;
import br.org.indt.ndg.common.Category;
import br.org.indt.ndg.common.Choice;
import br.org.indt.ndg.common.Field;
import br.org.indt.ndg.common.FieldType;
import br.org.indt.ndg.common.ResultWriter;
import br.org.indt.ndg.common.ResultXml;
import br.org.indt.ndg.common.SurveyXML;
import br.org.indt.ndg.common.exception.ImeiNotFoundException;
import br.org.indt.ndg.common.exception.MSMApplicationException;
import br.org.indt.ndg.server.authentication.UserManager;
import br.org.indt.ndg.server.client.ImeiVO;
import br.org.indt.ndg.server.client.MSMBusinessDelegate;
import br.org.indt.ndg.server.client.TransactionLogVO;
import br.org.indt.ndg.server.client.UserVO;
import br.org.indt.ndg.server.imei.IMEIManager;
import br.org.indt.ndg.server.pojo.Survey;
import br.org.indt.ndg.server.result.ResultHandler;
import br.org.indt.ndg.server.sms.InvalidMessageTypeException;
import br.org.indt.ndg.server.sms.SMSModemHandler;
import br.org.indt.ndg.server.sms.vo.MessageHeaderVO;
import br.org.indt.ndg.server.sms.vo.ResultMessageHeaderVO;
import br.org.indt.ndg.server.sms.vo.SMSCommMessageHeaderVO;
import br.org.indt.ndg.server.sms.vo.SMSMessageVO;
import br.org.indt.ndg.server.survey.SurveyHandler;
import br.org.indt.ndg.server.util.PropertiesUtil;

public class MessageProtocolHandler 
{
	private static Hashtable<String, MessageProtocolHandler> map = new Hashtable<String, MessageProtocolHandler>();
	private static String surveyId;
	private String mphandlerid;
	private Hashtable<String, String> messages;
	private Set<String> checkComm;
	private Hashtable<String, byte[]> payloads;
	private Properties properties;
	private boolean sendACK = true;
	private ArrayList<Byte> body_message = new ArrayList<Byte>();
	public int messageNumberTotal;
	private static final Logger log = Logger.getLogger("smslog");

	public static MessageProtocolHandler getInstance(String id) {
		MessageProtocolHandler messageProtocolHandler = (MessageProtocolHandler) map
				.get(id);

		if (messageProtocolHandler == null) {
			synchronized (MessageProtocolHandler.class) {
				if (messageProtocolHandler == null) {
					messageProtocolHandler = new MessageProtocolHandler(id);
					map.put(id, messageProtocolHandler);
				}
			}
		}
		return messageProtocolHandler;
	}

	protected MessageProtocolHandler(String id) {
		this.mphandlerid = id;
		messages = new Hashtable<String, String>();
		payloads = new Hashtable<String, byte[]>();
		checkComm = new HashSet<String>();
		properties = PropertiesUtil
				.loadFileProperty(PropertiesUtil.PROPERTIES_CORE_FILE);
	}

	public void preHandleMessage(MessageHeaderVO ph, String msg,
			SMSMessageVO smsMessage) {
		if (ph instanceof ResultMessageHeaderVO) {
			handleMessage((ResultMessageHeaderVO) ph, msg, smsMessage);

		} else if (ph instanceof SMSCommMessageHeaderVO) {
			SMSCommMessageHeaderVO smsCommMessageHeaderVO = (SMSCommMessageHeaderVO) ph;
			switch (smsCommMessageHeaderVO.getMessageType()) {
			case 5:
				checkComm.add(smsCommMessageHeaderVO.getPayload());
				smsCommMessageHeaderVO.setMessageType(6);
				responseSMSComm(smsCommMessageHeaderVO, smsMessage);
				log.info("Processing Message Type: "
						+ smsCommMessageHeaderVO.getMessageType()
						+ " Payload: " + smsCommMessageHeaderVO.toString());
				break;
			case 7:
				checkComm.remove(smsCommMessageHeaderVO.getPayload());
				log.info(">>>>> checkComm Size: " + checkComm.size());
				break;
			default:
				break;
			}
		}
		// handleMessage(ph, msg, smsMessage);
	}

	private void responseSMSComm(SMSCommMessageHeaderVO smsCommMessageHeaderVO,
			SMSMessageVO smsMessageVO) {
		InitialContext ctx = null;
		IMEIManager imeiManager = null;

		try {
			ctx = new InitialContext();
			imeiManager = (IMEIManager) ctx
					.lookup("ndg-core/IMEIManagerBean/remote");
			ImeiVO imei = null;
			if (imeiManager != null) {
				log.debug("SMS received from imei: " + smsCommMessageHeaderVO.getImei());
				imei = imeiManager.getIMEI(smsCommMessageHeaderVO.getImei());
				if (imei != null) {
					sendSMS(smsMessageVO.to, imei.getMsisdn(),
							smsCommMessageHeaderVO.toString());
				} else {
					throw new ImeiNotFoundException();
				}
			}
		} catch (NamingException e) {
			e.printStackTrace();
		} catch (MSMApplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JMSSenderHelperException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void handleMessage(ResultMessageHeaderVO ph, String msg,
			SMSMessageVO smsMessage) {
		messages.put("" + ph.messageNumber, getMessageBody(msg));
		payloads.put("" + ph.messageNumber, smsMessage.payload);

		if (new Integer(messageNumberTotal).intValue() == messages.size()) {
			String message = "";

			for (int i = 1; i <= messages.size(); i++) {
				message = message.concat(messages.get("" + i));
				byte[] bodyPart = payloads.get("" + i);

				for (int j = 0; j < bodyPart.length; j++) {
					body_message.add(bodyPart[j]);
				}
			}

			byte[] payload = new byte[body_message.size()];

			for (int i = 0; i < body_message.size(); i++) {
				payload[i] = body_message.get(i);
			}

			String result = SMSModemHandler.decompress(payload);
			result = result.substring(0, result.length() - 1);

			createResultXML(ph, result, smsMessage);

			map.remove(mphandlerid);
		}
	}

	private String getMessageBody(String s) {
		return s.substring(21);
	}

	public static MessageHeaderVO retrieveProtocolHeader(String message)
			throws InvalidMessageTypeException {
		String header = message;
		int messageType = new Integer(message.substring(0, 1));
		MessageHeaderVO vo = null;
		switch (messageType) {
		case 1:// Result Message
			header = message.substring(0, SMSModemHandler.HEADER);
			ResultMessageHeaderVO voR = new ResultMessageHeaderVO();
			voR.setMessageType(1);
			surveyId = voR.surveyId = header.substring(1, 11);
			voR.resultId = header.substring(13, 21);
			voR.imei = header.substring(21, header.length());
			voR.messageNumber = new Integer(header.substring(11, 13));
			voR.defaultString = "2" + header.substring(1, 21);
			vo = voR;
			break;
		case 5:// Check SMS Connection. Init shake hands
			SMSCommMessageHeaderVO voS5 = new SMSCommMessageHeaderVO();
			voS5.setMessageType(5);
			voS5.setImei(header.substring(header.indexOf('i') + 1, header
					.indexOf('t')));
			voS5.setTimestamp(header.substring(header.indexOf('t') + 1, header
					.length()));
			vo = voS5;
			log.info("Init shake hands: " + voS5.toString());
			break;
		case 7:// Receiving Ack from Client
			SMSCommMessageHeaderVO voS7 = new SMSCommMessageHeaderVO();
			voS7.setMessageType(7);
			voS7.setImei(header.substring(header.indexOf('i') + 1, header
					.indexOf('t')));
			voS7.setTimestamp(header.substring(header.indexOf('t') + 1, header
					.length()));
			vo = voS7;
			break;
		}

		return vo;
	}

	private void createResultXML(ResultMessageHeaderVO header, String body, SMSMessageVO smsMessage) 
	{
		InitialContext ctx = null;
		IMEIManager imeiManager = null;
		SurveyHandler surveyHandler = null;
		ResultHandler resultHandler = null;
		UserManager userManager = null;

		try 
		{
			ctx = new InitialContext();
			imeiManager = (IMEIManager) ctx.lookup("ndg-core/IMEIManagerBean/remote");
			surveyHandler = (SurveyHandler) ctx.lookup("ndg-core/SurveyHandlerBean/remote");
			resultHandler = (ResultHandler) ctx.lookup("ndg-core/ResultHandlerBean/remote");
			userManager = (UserManager) ctx.lookup("ndg-core/UserManagerBean/remote");
		}
		catch (NamingException e1) 
		{
			System.out.println("InitialContext Hard Coded");
			Hashtable<String, String> env = new Hashtable<String, String>();
			env.put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
			env.put(Context.PROVIDER_URL, properties.getProperty("java.naming.provider.url"));
			
			try 
			{
				ctx = new InitialContext(env);
				userManager = (UserManager) ctx.lookup("ndg-core/UserManagerBean/remote");
				surveyHandler = (SurveyHandler) ctx.lookup("ndg-core/SurveyHandlerBean/remote");
				resultHandler = (ResultHandler) ctx.lookup("ndg-core/ResultHandlerBean/remote");
				imeiManager = (IMEIManager) ctx.lookup("ndg-core/IMEIManagerBean/remote");
				System.err.println("InitialContext - using Hashtable");
			}
			catch (NamingException e2) 
			{
				e2.printStackTrace();
			}
		}

		String loggedUserAdmin = "";

		try 
		{
			loggedUserAdmin = surveyHandler.getUserBySurvey(surveyId).getIdUserAdmin().getUsername();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}

		if (userManager.userAdminHasPositiveBalance(UserVO.RESULT_LIMIT, loggedUserAdmin)) 
		{
			String resultString = null;
			SurveyXML survey = null;
			ResultXml resultReceived = new ResultXml();
			resultReceived.setSurveyId(header.surveyId);
			resultReceived.setResultId(header.resultId);
			
			String _imei = header.imei;

			if (body.contains("{")) 
			{
				String latlgt = body.substring(body.indexOf("{") + 1, body.indexOf("}"));
				resultReceived.setLatitude(latlgt.substring(0, latlgt.indexOf(";")));
				resultReceived.setLongitude(latlgt.substring(latlgt.indexOf(";") + 1));
			}

			try 
			{
				Survey surveyToGetUser = null;
				Pattern regex = Pattern.compile("<(.*?)>", Pattern.CANON_EQ);

				surveyToGetUser = surveyHandler.getUserBySurvey(surveyId);

				if (surveyToGetUser != null) 
				{
					resultReceived.setUser(surveyToGetUser.getIdUserAdmin().getUsername());
				}
				else 
				{
					log.debug("surveyToGetUser = null");
				}

				MSMBusinessDelegate msmBD = new MSMBusinessDelegate();
				
				survey = msmBD.loadSurvey(surveyToGetUser.getIdUserAdmin().getUsername(), surveyId);
				resultReceived.setTitle(survey.getTitle());

				ImeiVO imei = null;

				if (imeiManager != null) 
				{
					imei = imeiManager.getIMEI(_imei);
					
					if (imei != null) 
					{
						resultReceived.setImei(imei.getImei());
					}
					else 
					{
						throw new ImeiNotFoundException();
					}
				}

				Matcher regexMatcher = regex.matcher(body);

				while (regexMatcher.find()) 
				{
					resultString = regexMatcher.group();
					Pattern r1 = Pattern.compile("[^<].*[^>]", Pattern.CASE_INSENSITIVE);
					Matcher m1 = r1.matcher(resultString);

					while (m1.find()) 
					{
						resultString = m1.group();
						Pattern r2 = Pattern.compile("[^|]*.", Pattern.CASE_INSENSITIVE);
						Matcher m2 = r2.matcher(resultString);
						Category c = null;

						int catAnswersCount = 0;

						while (m2.find()) 
						{
							resultString = m2.group();
							Pattern r3 = Pattern.compile(".*[^|]", Pattern.CASE_INSENSITIVE);
							Matcher m3 = r3.matcher(resultString);

							while (m3.find()) 
							{
								resultString = m3.group();

								if (!resultString.contains(".")) 
								{
									c = new Category();
									int categoryId = new Integer(resultString).intValue();
									c.setId(categoryId);
									c.setName(survey.getCategories().get(categoryId).getName());
								}
								else 
								{
									if (c != null) 
									{
										String answer = resultString.substring(resultString.indexOf(".") + 1).trim();
										catAnswersCount++;

										int answerId = new Integer(resultString.substring(0, resultString.indexOf(".")).trim()).intValue();

										Field answer1 = new Field();
										answer1.setCategoryId(c.getId());
										Field surveyField = survey.getCategories().get(c.getId()).getFieldById(answerId);
										answer1.setFieldType(surveyField.getFieldType());

										if (surveyField.getFieldType().equals(FieldType.CHOICE)) 
										{
											Choice choice = surveyField.getChoice();
											choice.setItems(surveyField.getChoice().getItems());
											answer1.setChoice(choice);
										}

										answer1.setId(answerId);
										answer1.setValue(answer);
										c.addField(answer1);
									}
								}
							}
						}

						// adiciona a categoria no result
						if (catAnswersCount > 0) 
						{
							resultReceived.addCategory(c.getId(), c);
						}
					}
				}

				StringBuffer surveyString = new StringBuffer();

				String line = null;

				// grava
				ResultWriter writer = new ResultWriter(survey, resultReceived);
				
				String xmlString = writer.write();
				
				BufferedReader bufferedReader = new BufferedReader(new StringReader(xmlString));

				while ((line = bufferedReader.readLine()) != null) 
				{
					surveyString.append(line.trim());
				}

				TransactionLogVO transactionLogVO = createTransactionLogVO(resultReceived);
				transactionLogVO.setAddress(smsMessage.from);
				resultHandler.postResult(surveyString, transactionLogVO);
				resultReceived.setPhoneNumber(smsMessage.from);

				if (sendACK) 
				{
					log.debug("Sending Result Received ACK To: " + smsMessage.to);
					sendSMS(smsMessage.to, imei.getMsisdn(), header.defaultString);
					sendACK = false;
				}
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				setResultReceived(resultReceived, false);
			}

			log.debug(">>> Result: " + resultReceived.getResultId() + " From Imei: " 
			+ resultReceived.getImei() + " : Ok!");
			
			userManager.updateUserAdminBalance(UserVO.RESULT_LIMIT, loggedUserAdmin);
		}
		else 
		{
			log.debug(">>>>>> Result '" + header.resultId + "' from Survey '" + header.surveyId
			+ "' was ignored due trial limitation! <<<<<<");
		}
	}

	public void sendSMS(String from, String to, String text)
			throws JMSSenderHelperException {
		JMSSenderHelper jmsHelper;
		jmsHelper = new JMSSenderHelper("queue/MSM_SMSSender");

		SMSMessageVO vo = new SMSMessageVO();
		vo.from = from;
		vo.to = to;
		vo.message = text;
		vo.port = 50001;

		jmsHelper.queue(vo);
	}

	// call log operation to register the result received by server
	private boolean setResultReceived(ResultXml result, boolean success) {
		boolean valid = false;

		MSMBusinessDelegate msmBD = new MSMBusinessDelegate();
		TransactionLogVO t = new TransactionLogVO();
		t.setDtLog(new Timestamp(System.currentTimeMillis()));
		t.setTransmissionMode(TransactionLogVO.MODE_SMS);

		if (success) {
			t.setStatus(TransactionLogVO.STATUS_SUCCESS);
		} else {
			t.setStatus(TransactionLogVO.STATUS_ERROR);
		}

		t.setTransactionType(TransactionLogVO.TYPE_RECEIVE_RESULT);
		t.setAddress(result.getPhoneNumber());
		t.setSurveyId(surveyId);
		t.setResultId(result.getResultId());

		try {
			if (result.getImei() != null) {
				t.setImei(result.getImei());
			} else {
				ArrayList<Object> listImeis = msmBD.getAllImeisSentAndUnsentBySurvey(surveyId).getQueryResult();

				if (listImeis.size() != 0) {
					ImeiVO imei = (ImeiVO)listImeis.get(0);
					t.setImei(imei.getImei());
				}
			}

			log.debug("Logging result received via SMS id:"
					+ result.getResultId());
			msmBD.logTransaction(t);
			valid = true;
		} catch (MSMApplicationException e) {
			log.error(e.getErrorCode());
			log.error(e);
		}

		return valid;
	}

	private TransactionLogVO createTransactionLogVO(ResultXml result) {
		TransactionLogVO t = new TransactionLogVO();
		t.setTransmissionMode(TransactionLogVO.MODE_HTTP);
		t.setAddress(result.getPhoneNumber());
		t.setTransactionType(TransactionLogVO.TYPE_RECEIVE_RESULT);
		t.setUser(result.getUser());

		return t;
	}

	// the sendACK
	public boolean isSendACK() {
		return sendACK;
	}

	// the sendACK to set
	public void setSendACK(boolean sendACK) {
		this.sendACK = sendACK;
	}
}
