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

package br.org.indt.ndg.server.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Properties;
import java.util.HashMap;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;

import br.org.indt.ndg.common.Deploy;
import br.org.indt.ndg.common.ResultParser;
import br.org.indt.ndg.common.ResultXml;
import br.org.indt.ndg.common.SurveyXML;
import br.org.indt.ndg.common.exception.DeviceHasRelationshipException;
import br.org.indt.ndg.common.exception.EmailNotSentException;
import br.org.indt.ndg.common.exception.ImeiHasRelationshipException;
import br.org.indt.ndg.common.exception.ImeiLimitReachedException;
import br.org.indt.ndg.common.exception.JadDownloadException;
import br.org.indt.ndg.common.exception.MSMApplicationException;
import br.org.indt.ndg.common.exception.MSMSystemException;
import br.org.indt.ndg.common.exception.ModemException;
import br.org.indt.ndg.common.exception.ResultNotParsedException;
import br.org.indt.ndg.common.exception.ResultReadException;
import br.org.indt.ndg.common.exception.SurveyFileAlreadyExistsException;
import br.org.indt.ndg.common.exception.SurveyHasRelationshipException;
import br.org.indt.ndg.common.exception.SurveyNotFoundException;
import br.org.indt.ndg.common.exception.UserEmailNotValidatedException;
import br.org.indt.ndg.common.exception.UserHasRelationshipException;
import br.org.indt.ndg.common.exception.UserLimitReachedException;
import br.org.indt.ndg.common.exception.UserNotFoundException;
import br.org.indt.ndg.common.exception.UserNullException;
import br.org.indt.ndg.common.exception.UserUnknownException;
import br.org.indt.ndg.server.authentication.UserManager;
import br.org.indt.ndg.server.controller.QueryInputOutputVO;
import br.org.indt.ndg.server.controller.UserBalanceVO;
import br.org.indt.ndg.server.imei.DeviceManager;
import br.org.indt.ndg.server.imei.IMEIManager;
import br.org.indt.ndg.server.mail.MessageManager;
import br.org.indt.ndg.server.mail.SendTextSms;
import br.org.indt.ndg.server.pojo.NdgUser;
import br.org.indt.ndg.server.pojo.Survey;
import br.org.indt.ndg.server.result.ResultHandler;
import br.org.indt.ndg.server.sms.SMSModemHandler;
import br.org.indt.ndg.server.sms.SmsHandlerFactory;
import br.org.indt.ndg.server.sms.vo.SMSMessageVO;
import br.org.indt.ndg.server.survey.SurveyHandler;
import br.org.indt.ndg.server.transaction.TransactionLogManager;
import br.org.indt.ndg.server.language.LanguageManager;
import br.org.indt.ndg.server.util.FileUtil;
import br.org.indt.ndg.server.util.PropertiesUtil;

public class MSMBusinessDelegate {
	private InitialContext initialContext;

	private UserManager userManager;
	private IMEIManager imeiManager;
	private DeviceManager deviceManager;
	private SurveyHandler surveyHandler;
	private ResultHandler resultHandler;
	private MessageManager messageManager;
	private TransactionLogManager transactionlogManager;
    private LanguageManager languageManager;

	private static final int EMAIL_SUBJECT_RECOVERY_PASSWORD = 0;
	private static final int EMAIL_SUBJECT_REQUEST_ACCESS = 1;
	private static final String SMS_NEW_SURVEY_NOTIFICATION = "You have to download the Survey:";

	private static final Logger log = Logger.getLogger("MSMBD");

	public MSMBusinessDelegate() {
		try {
			initialContext = new InitialContext();

			userManager = (UserManager) initialContext
					.lookup("ndg-core/UserManagerBean/remote");
			imeiManager = (IMEIManager) initialContext
					.lookup("ndg-core/IMEIManagerBean/remote");
			surveyHandler = (SurveyHandler) initialContext
					.lookup("ndg-core/SurveyHandlerBean/remote");
			resultHandler = (ResultHandler) initialContext
					.lookup("ndg-core/ResultHandlerBean/remote");
			deviceManager = (DeviceManager) initialContext
					.lookup("ndg-core/DeviceManagerBean/remote");
			messageManager = (MessageManager) initialContext
					.lookup("ndg-core/MessageManagerBean/remote");
			transactionlogManager = (TransactionLogManager) initialContext
					.lookup("ndg-core/TransactionLogManagerBean/remote");
            languageManager = (LanguageManager) initialContext
					.lookup("ndg-core/LanguageManagerBean/remote");

			System.out.println("InitialContext - using property file");
		} catch (NamingException e1) {
			System.out.println("InitialContext Hard Coded");

			Hashtable<String, String> env = new Hashtable<String, String>();
			env.put(Context.INITIAL_CONTEXT_FACTORY,
					"org.jnp.interfaces.NamingContextFactory");
			env.put(Context.PROVIDER_URL, "localhost:1099");

			try {
				initialContext = new InitialContext(env);

				userManager = (UserManager) initialContext
						.lookup("ndg-core/UserManagerBean/remote");
				imeiManager = (IMEIManager) initialContext
						.lookup("ndg-core/IMEIManagerBean/remote");
				surveyHandler = (SurveyHandler) initialContext
						.lookup("ndg-core/SurveyHandlerBean/remote");
				resultHandler = (ResultHandler) initialContext
						.lookup("ndg-core/ResultHandlerBean/remote");
				deviceManager = (DeviceManager) initialContext
						.lookup("ndg-core/DeviceManagerBean/remote");
				messageManager = (MessageManager) initialContext
						.lookup("ndg-core/MessageManagerBean/remote");
				transactionlogManager = (TransactionLogManager) initialContext
						.lookup("ndg-core/TransactionLogManagerBean/remote");
                languageManager = (LanguageManager) initialContext
					.lookup("ndg-core/LanguageManagerBean/remote");

				System.err.println("InitialContext - using Hashtable");
			} catch (NamingException e2) {
				e2.printStackTrace();
			}
		}

		String v = getNDGVersion();
	}

	public Boolean isHostedMode() throws MSMApplicationException,
			MSMSystemException {
		String ndgMode = null;

		Properties properties = PropertiesUtil
				.loadFileProperty(PropertiesUtil.PROPERTIES_CORE_FILE);

		if (properties.containsKey("NDG_MODE")) {
			ndgMode = properties.getProperty("NDG_MODE");
		}

		Boolean isHostedMode = ("hosted".equals(ndgMode));

		return isHostedMode;
	}

	public QueryInputOutputVO listSurveysDB(String username,
			QueryInputOutputVO queryIOVO, Boolean isUploaded) throws MSMApplicationException,
			MSMSystemException {
		log.info("listSurveyDB(): " + username);

		NdgUser userLogged = findNdgUserByName(username);

		return surveyHandler.listSurveysDB(userLogged, queryIOVO, isUploaded);
	}

	// status of sending survey: SUCCESS, AVAILABLE, PENDING, ERROR
	public Collection<SurveyVO> listSurveysByImeiDB(String imei, String status)
			throws MSMApplicationException, MSMSystemException {
		log.info("listSurveysByImeiDB(String imei, String status): " + imei
				+ "/" + status);

		return surveyHandler.listSurveysByImeiDB(imei, status);
	}

	// status of sending survey: SUCCESS, AVAILABLE, PENDING, ERROR
	public QueryInputOutputVO listSurveysByImeiDBWithoutResults(String imei,
			String status, QueryInputOutputVO queryIOVO)
			throws MSMApplicationException, MSMSystemException {
		log
				.info("listSurveysByImeiDBWithoutResults(String imei, String status): "
						+ imei + "/" + status);

		return surveyHandler.listSurveysByImeiDBWithoutResults(imei, status,
				queryIOVO);
	}

	public UserVO validateLogin(String username, String password)
			throws MSMApplicationException {
		log
				.info("validateLogin(String username, String password): "
						+ username);
		UserVO user = userManager.validateLogin(username, password);
		return user;
	}

	public void createUser(String username, UserVO vo)
			throws MSMApplicationException, MSMSystemException {
		NdgUser userLogged = findNdgUserByName(username);

		if (userManager.userAdminHasPositiveBalance(UserVO.USER_LIMIT,
				userLogged.getUserAdmin())) {
			vo.setUserAdmin(userLogged.getUserAdmin());

			log.info("createUser(String username, UserVO vo): " + username
					+ " / " + vo.getUsername());

			userManager.createUser(vo);
			userManager.updateUserAdminBalance(UserVO.USER_LIMIT, userLogged
					.getUserAdmin());
		} else {
			log
					.info("UserLimitReachedException: createUser(String username, UserVO vo): "
							+ username + " / " + vo.getUsername());

			throw new UserLimitReachedException();
		}
	}

	public void deleteUser(String _username) throws MSMApplicationException {
		log.info("deleteUser(String _username): " + _username);
		try {
			userManager.deleteUser(_username);
		} catch (RuntimeException e) {
			Throwable cause = e.getCause();
			for (int i = 0; i < 3 && cause != null; i++) {
				cause = cause.getCause();
				if (cause instanceof ConstraintViolationException) {
					throw new UserHasRelationshipException();
				}
			}
			throw e;
		}
	}

	public void updateUser(UserVO user) throws MSMSystemException,
			MSMApplicationException {
		log.info("updateUser(UserVO user): " + user.getUsername());
		userManager.updateUser(user);
	}

	public UserVO updatePassword(UserVO user) throws MSMApplicationException {
		UserVO userVO = userManager.checkValidationKey(user.getValidationKey());
		if (userVO != null) {
			userVO.setPassword(user.getPassword());
			userManager.updateUserPassword(userVO);
		} else {
			throw new UserNotFoundException();
		}
		return userVO;
	}

	public QueryInputOutputVO listAllUsers(String username,
			QueryInputOutputVO queryIOVO) throws MSMApplicationException {
		QueryInputOutputVO queryIOVOOutput = new QueryInputOutputVO();

		if (username != null) {
			NdgUser userLogged = findNdgUserByName(username);

			if (userLogged != null) {
				queryIOVOOutput = userManager.listAllUsers(userLogged
						.getUserAdmin(), queryIOVO);
			}
		} else {
			throw new UserUnknownException();
		}

		return queryIOVOOutput;
	}

	public Collection<RoleVO> listAllRoles() throws MSMApplicationException {
		return userManager.listAllRoles();
	}

	public void createIMEI(ImeiVO imeiVO) throws MSMApplicationException {
		NdgUser userLogged = findNdgUserByName(imeiVO.getUserName());
		if (userManager.userAdminHasPositiveBalance(UserVO.IMEI_LIMIT,
				userLogged.getUserAdmin())) {
			imeiManager.createIMEI(imeiVO);
			sendDynamicJad(imeiVO.getMsisdn(), imeiVO.getImei());
		} else {
			throw new ImeiLimitReachedException();
		}
		userManager.updateUserAdminBalance(UserVO.IMEI_LIMIT, userLogged
				.getUserAdmin());
	}

	public void updateIMEI(ImeiVO imeiVO) throws MSMApplicationException {
		imeiManager.updateIMEI(imeiVO);
	}

	public void createDevice(DeviceVO deviceVO) throws MSMApplicationException {
		deviceManager.createDevice(deviceVO);
	}

	public void updateDevice(DeviceVO deviceVO) throws MSMApplicationException {
		deviceManager.updateDevice(deviceVO);
	}

	public WizardVO wizardImei(ArrayList<ImeiVO> listOfImeiVO)
			throws MSMApplicationException, MSMSystemException {
		Properties properties = getSettings();
		String smsMessage = "You are able to download NDG client here: ";
		String link = properties.getProperty("urlServer")
				+ "/ndg-servlets/GetClient?to=";
		log.debug("wizardImei: " + link);

		WizardVO wizardVo = new WizardVO();
		for (ImeiVO imeiVO : listOfImeiVO) {
			if (imeiManager.hasImei(imeiVO)) {
				wizardVo.addImei(imeiVO.getImei());
			}
			if (imeiManager.hasMsisdn(imeiVO)) {
				wizardVo.addPhone(imeiVO.getMsisdn());
			}
		}
		if (!wizardVo.isFilled()) {
			for (ImeiVO imeiVO : listOfImeiVO) {
				createIMEI(imeiVO);
				log.debug("newIMEI: " + imeiVO.getImei());
			}
			log.debug("sendLinkSMS: " + smsMessage + link);
			sendLinkSMS(smsMessage + link, listOfImeiVO);
		}

		return wizardVo;
	}

	public void deleteIMEI(String _imei) throws MSMApplicationException {
		try {
			ImeiVO imeiVO = getImei(_imei);
			if (imeiVO.getRealImei() == 'N'){
				deleteJadDir(imeiVO.getMsisdn());
			}
			imeiManager.deleteIMEI(_imei);
		} catch (RuntimeException e) {
			Throwable cause = e.getCause();
			for (int i = 0; i < 3 && cause != null; i++) {
				cause = cause.getCause();
				if (cause instanceof ConstraintViolationException) {
					throw new ImeiHasRelationshipException();
				}
			}
			throw e;
		}
	}

	public void deleteDevice(String _device) throws MSMApplicationException {
		try {
			deviceManager.deleteDevice(_device);
		} catch (RuntimeException e) {
			Throwable cause = e.getCause();
			for (int i = 0; i < 3 && cause != null; i++) {
				cause = cause.getCause();
				if (cause instanceof ConstraintViolationException) {
					throw new DeviceHasRelationshipException();
				}
			}
			throw e;
		}
	}

	public QueryInputOutputVO listAllDevices(QueryInputOutputVO queryIOVO)
			throws MSMApplicationException {
		QueryInputOutputVO queryIOVOOutput = deviceManager
				.listAllDevices(queryIOVO);

		return queryIOVOOutput;
	}

	public void sendSMS(SMSMessageVO vo) throws MSMApplicationException,
			MSMSystemException {
		messageManager.sendSMS(vo);
	}

	public ImeiVO findImeiByMsisdn(String msisdn)
			throws MSMApplicationException {
		return imeiManager.findImeiByMsisdn(msisdn);
	}

	public Properties getSettings() throws MSMApplicationException {
		return surveyHandler.getSettings();
	}

	public String getSpecificPropertySetting(String propertySetting)
			throws MSMApplicationException {
		return surveyHandler.getSpecificPropertySetting(propertySetting);
	}

	public void updateSettings(Properties settings)
			throws MSMApplicationException {
		surveyHandler.updateSettings(settings);
	}

	public void logTransaction(TransactionLogVO vo)
			throws MSMApplicationException {
		transactionlogManager.logTransaction(vo);
	}

	public ImeiVO getImei(String imei) throws MSMApplicationException {
		return imeiManager.getIMEI(imei);
	}

	// Get all IMEIs (devices) that successfully have received the survey
	public QueryInputOutputVO getImeisSentBySurvey(String surveyId,
			QueryInputOutputVO queryIOVO) throws MSMApplicationException {
		return surveyHandler.getImeisBySurvey(surveyId,
				TransactionLogVO.STATUS_SUCCESS, queryIOVO);
	}

	// Get all IMEIs (devices) that haven't received the survey yet
	public QueryInputOutputVO getImeisUnsentBySurvey(String surveyId,
			QueryInputOutputVO queryIOVO) throws MSMApplicationException {
		return surveyHandler.getImeisBySurvey(surveyId,
				TransactionLogVO.STATUS_PENDING, queryIOVO);
	}

	public void sendTextSMS(String message, ArrayList<String> listoOfDevices,
			int port, String loggedUser) throws ModemException,
			MSMApplicationException {
		if (SmsHandlerFactory.getInstance().hasSmsSupport()){
			for (int i = 0; i < listoOfDevices.size(); i++) {
				ArrayList<String> currentDevice = new ArrayList<String>();
				currentDevice.add(listoOfDevices.toArray()[i].toString());
	
				if (userManager.userAdminHasPositiveBalance(UserVO.ALERT_LIMIT,
						loggedUser)) {
					Thread sendNotificationSms = new SendTextSms(message,
							currentDevice, port);
					sendNotificationSms.start();
				} else {
					throw new UserLimitReachedException();
				}
	
				userManager.updateUserAdminBalance(UserVO.ALERT_LIMIT, loggedUser);
			}
		}
	}

	public void sendLinkSMS(String message, ArrayList<ImeiVO> listoOfDevices)
			throws ModemException, MSMApplicationException {
		Thread sendNotificationSms = new SendTextSms(message, listoOfDevices);
		sendNotificationSms.start();
	}

	// Get all IMEIs (devices)that are associated that survey
	public QueryInputOutputVO getAllImeisBySurvey(String surveyId,
			QueryInputOutputVO queryIOVO) throws MSMApplicationException {
		QueryInputOutputVO outputVo = null;

		outputVo = surveyHandler.getAllImeisBySurvey(surveyId, queryIOVO);

		return outputVo;
	}

	public QueryInputOutputVO getAllImeisSentAndUnsentBySurvey(String surveyId)
			throws MSMApplicationException {
		QueryInputOutputVO queryIOVO = new QueryInputOutputVO();
		queryIOVO.setQueryResult(new ArrayList<Object>());

		queryIOVO.getQueryResult().add(
				getImeisSentBySurvey(surveyId, null).getQueryResult());
		queryIOVO.getQueryResult().add(
				getImeisUnsentBySurvey(surveyId, null).getQueryResult());

		queryIOVO.setRecordCount(queryIOVO.getQueryResult().size());

		return queryIOVO;
	}

	public Collection<TransactionLogVO> getResultReceived(String surveyId,
			String resultId) throws MSMApplicationException {
		return transactionlogManager.findTransactionLogBySurveyId(
				TransactionLogVO.TYPE_RECEIVE_RESULT, surveyId,
				TransactionLogVO.STATUS_SUCCESS, resultId);
	}

	public QueryInputOutputVO getResultListDB(String username, String surveyId,
			QueryInputOutputVO queryIOVO) throws MSMApplicationException,
			MSMSystemException {
		Survey survey = surveyHandler.getSurveyObject(surveyId);

		QueryInputOutputVO result = null;

		if (survey != null) {
			result = resultHandler.getResultListDB(surveyId, queryIOVO);
		} else {
			throw new SurveyNotFoundException();
		}

		return result;
	}

	public ResultXml getResultDB(String idResult)
			throws MSMApplicationException, MSMSystemException {
		ResultXml result = resultHandler.getResultDB(idResult);
		return result;
	}

	// Get information about a survey that was received.
	public TransactionLogVO getSurveyReceived(String surveyId)
			throws MSMApplicationException {
		Collection<TransactionLogVO> c = transactionlogManager
				.findTransactionLogBySurveyId(
						TransactionLogVO.TYPE_RECEIVE_SURVEY, surveyId,
						TransactionLogVO.STATUS_SUCCESS, null);
		TransactionLogVO t = new TransactionLogVO();
		if (c.iterator().hasNext()) {
			t = (TransactionLogVO) c.iterator().next();
			return t;
		} else {
			return null;
		}
	}

	public QueryInputOutputVO getImeisByUser(String username,
			QueryInputOutputVO queryIOVO, boolean withFakeImeis)
			throws MSMApplicationException {
		return imeiManager.findImeiByUser(username, queryIOVO, withFakeImeis);
	}

	public QueryInputOutputVO listAllImeis(String username,
			QueryInputOutputVO queryIOVO, boolean withFakeImeis)
			throws MSMApplicationException {
		NdgUser userLogged = findNdgUserByName(username);
		QueryInputOutputVO queryIOVOOutput = imeiManager.listAllImeis(
				userLogged.getUserAdmin(), queryIOVO, withFakeImeis);
		return queryIOVOOutput;
	}

	// Send one Survey to N mobiles
	public void sendSurveybySMS(String username, String idSurvey,
			ArrayList<String> listOfDevices) throws MSMApplicationException,
			MSMSystemException {
		if (SmsHandlerFactory.getInstance().hasSmsSupport()){
			NdgUser userLogged = findNdgUserByName(username);
			surveyHandler.sendSlicedSurvey(userLogged, idSurvey, listOfDevices);
		}
	}

	// Send one Survey to N mobiles
	public void rationSurveybyGPRS(String username,
			ArrayList<String> listOfSurveys, ArrayList<String> listOfDevices, ArrayList<String> listOfTitles)
			throws MSMApplicationException, MSMSystemException {
		log.debug("listOfSurveys :" + listOfSurveys.toString());

		NdgUser userlogged = findNdgUserByName(username);

		for (int i = 0; i < listOfSurveys.size(); i++) {
			log.debug(">>> Provisionando Survey: " + listOfSurveys.get(i) + " - " + listOfTitles.get(i));

			ArrayList<String> devicesToGetNewSurvey = new ArrayList<String>();
			devicesToGetNewSurvey = surveyHandler.rationSurveybyGPRS(
					userlogged, listOfSurveys.get(i), listOfDevices);

			if (devicesToGetNewSurvey.size() > 0) {
				Thread sendNotificationSms = new SendTextSms(
						SMS_NEW_SURVEY_NOTIFICATION + listOfTitles.get(i),
						devicesToGetNewSurvey, SMSModemHandler.SMS_NORMAL_PORT);
				sendNotificationSms.setName(listOfTitles.get(i));
				sendNotificationSms.start();
			}
		}

	}

	public void updateStatusSendingSurvey(String imei, String status)
			throws MSMApplicationException, MSMSystemException {
		surveyHandler.updateStatusSendingSurvey(imei, status);
	}

	public boolean forgotYourPassword(String email)
			throws MSMApplicationException, MSMSystemException {
		boolean result = false;

		UserVO userVo = userManager.validateLoginByEmail(email);

		if (userVo.getRetCode().equals(UserVO.AUTHENTICATED)) {
			if ((userVo.getUserValidated() == 'n')
					|| (userVo.getUserValidated() == 'N')) {
				throw new UserEmailNotValidatedException();
			} else {
				messageManager.sendTextEmail(userVo,
						EMAIL_SUBJECT_RECOVERY_PASSWORD);
				result = true;
			}
		}

		return result;
	}

	public void requestAccess(UserVO userPending) throws MSMSystemException,
			MSMApplicationException {
		UserVO newUser = userManager.requestAccess(userPending);

		if (newUser != null) {
			try {
				// sending validation email
				messageManager.sendTextEmail(newUser,
						EMAIL_SUBJECT_REQUEST_ACCESS);
			} catch (EmailNotSentException e) {
				userManager.deleteNotValidatedUser(newUser.getUsername());

				throw e;
			}
		}
	}

	public UserVO validateLoginByEmail(String email) throws MSMSystemException,
			MSMApplicationException {
		UserVO user = userManager.validateLoginByEmail(email);
		if (user.getRetCode().equals(UserVO.AUTHENTICATED)) {
			messageManager.sendTextEmail(user, EMAIL_SUBJECT_RECOVERY_PASSWORD);
		}
		return user;
	}

	public UserVO checkValidationKey(String validationKey)
			throws MSMSystemException, MSMApplicationException {
		UserVO userVO = new UserVO();
		userVO = userManager.checkValidationKey(validationKey);
		return userVO;
	}

	public Properties getModemProperties() throws MSMApplicationException {
		return surveyHandler.getModemProperties();
	}

	public void postSurvey(String user, StringBuffer surveyBuffered,
			TransactionLogVO postSurveyTransaction, Boolean isValidateLogin)
			throws MSMApplicationException, MSMSystemException,
			SurveyFileAlreadyExistsException {
		NdgUser userLogged = findNdgUserByName(user);

		if (!isValidateLogin) {
			if (userManager.userAdminHasPositiveBalance(UserVO.SURVEY_LIMIT,
					userLogged.getUserAdmin())) {
				surveyHandler.postSurvey(userLogged, surveyBuffered,
						postSurveyTransaction);
			} else {
				throw new UserLimitReachedException();
			}

			userManager.updateUserAdminBalance(UserVO.SURVEY_LIMIT, userLogged
					.getUserAdmin());
		} else {
			surveyHandler.postSurvey(userLogged, surveyBuffered,
					postSurveyTransaction);
		}

	}

	public void postResult(StringBuffer resultBuffered,	TransactionLogVO postResultTransaction)
			throws MSMApplicationException, MSMSystemException {
		
		ResultParser parser = new ResultParser();
		ResultXml resultBean = null;
		NdgUser userLogged = null;

		try {
			resultBean = parser.parseResult(resultBuffered, "UTF-8");
		} catch (Exception e) {
			throw new ResultNotParsedException();
		}

		if (resultBean != null) {
			ImeiVO imei = imeiManager.getIMEI(resultBean.getImei());
			userLogged = findNdgUserByName(imei.getUserName());
			if (userManager.userAdminHasPositiveBalance(UserVO.RESULT_LIMIT, userLogged.getUserAdmin())) {
				resultHandler.postResult(resultBuffered, postResultTransaction);
				userManager.updateUserAdminBalance(UserVO.RESULT_LIMIT, userLogged.getUserAdmin());
			} else {
				throw new UserLimitReachedException();
			}
		}
	}

	public NdgUser getUserByImei(String imei) throws MSMApplicationException {
		NdgUser userLogged = userManager.getUserByImei(imei);
		return userLogged;
	}

	public UserVO getUserByEmail(String email) throws MSMSystemException,
			MSMApplicationException {
		UserVO user = userManager.getUserByEmail(email);
		return user;
	}

	public SurveyXML loadSurvey(String username, String idSurvey)
			throws MSMApplicationException, MSMSystemException {
		NdgUser userLogged = findNdgUserByName(username);

		SurveyXML surveyXML = surveyHandler.loadSurveyAndResultsDB(userLogged
				.getUserAdmin(), idSurvey);

		return surveyXML;
	}

	public SurveyXML loadSurveyDB(String username, String idSurvey)
			throws MSMApplicationException, MSMSystemException {
		NdgUser userLogged = findNdgUserByName(username);

		SurveyXML surveyXML = surveyHandler.loadSurveyDB(userLogged
				.getUserAdmin(), idSurvey);

		return surveyXML;
	}

	public SurveyXML loadSurveyAndResultsDB(String username, String idSurvey)
			throws MSMApplicationException, MSMSystemException {
		NdgUser userLogged = findNdgUserByName(username);

		SurveyXML surveyXML = surveyHandler.loadSurveyAndResultsDB(userLogged
				.getUserAdmin(), idSurvey);

		return surveyXML;
	}

	public ArrayList<SurveyXML> loadSurveysDB(NdgUser user)
			throws MSMApplicationException, MSMSystemException {
		ArrayList<SurveyXML> surveyXMLList = surveyHandler.loadSurveysDB(user);

		return surveyXMLList;
	}

	public ArrayList<ResultXml> loadResultsDB(String idSurvey)
			throws MSMApplicationException, MSMSystemException {
		ArrayList<ResultXml> resultXMLList = surveyHandler.loadResultsDB(idSurvey);

		return resultXMLList;
	}

	public void copySurveyToDevice(String deviceDrive, String loggedUser,
			String surveyId, String imei) throws FileNotFoundException, IOException,
			SurveyNotFoundException, MSMApplicationException {
		Deploy.copySurveyToDevice(deviceDrive, loggedUser, surveyId, imei);
	}

	public UserBalanceVO findUserBalanceByUser(String username)
			throws MSMApplicationException {
		return userManager.findUserBalanceByUser(findNdgUserByName(username).getUserAdmin());
	}

	public void saveSurveyFromEditorToServer(String userName, String surveyContent)
			throws MSMApplicationException {
		surveyHandler.saveSurveyFromEditorToServer(userName, surveyContent);
	}

	public String loadSurveyFromServerToEditor(String userName, String surveyID)
			throws MSMApplicationException{
		String surveyContent = surveyHandler.loadSurveyFromServerToEditor(userName, surveyID);
		return surveyContent;
	}

	public ArrayList<String> loadSurveysFromServerToEditor(String userName) {
		ArrayList<String> surveyList = new ArrayList<String>();

		try {
			surveyList = surveyHandler.loadSurveysFromServerToEditor(userName);
		} catch (MSMApplicationException e) {
			e.printStackTrace();
		}

		return surveyList;
	}

	public void deleteSurveyFromServer(String surveyID) 
			throws MSMApplicationException{
		
		try {
			surveyHandler.deleteSurveyFromServer(surveyID);
		} catch (RuntimeException e) {
			Throwable cause = e.getCause();
			for (int i = 0; i < 3 && cause != null; i++) {
				cause = cause.getCause();
				if (cause instanceof ConstraintViolationException) {
					throw new SurveyHasRelationshipException();
				}
			}
			throw e;
		}
	}

	public void uploadSurveyFromEditorToServer(String surveyID) 
			throws MSMApplicationException{
		surveyHandler.uploadSurveyFromEditorToServer(surveyID);
	}

	public void saveSettingsFromEditorToServer(String userName,
			String settingsContent) {
		try {
			userManager.saveSettingsFromEditorToServer(userName,
					settingsContent);
		} catch (MSMApplicationException e) {
			e.printStackTrace();
		}
	}

	public String loadSettingsFromServerToEditor(String userName) {
		String settingsContent = "";

		try {
			settingsContent = userManager
					.loadSettingsFromServerToEditor(userName);
		} catch (MSMApplicationException e) {
			e.printStackTrace();
		}

		return settingsContent;
	}

	public NdgUser findNdgUserByName(String username)
			throws MSMApplicationException {
		if (username == null) {
			throw new UserNullException();
		}

		NdgUser result = null;
		result = userManager.findNdgUserByName(username);

		return result;
	}

	public void detachImeiFromSurvey(String surveyID, String imeiNumber)
			throws MSMApplicationException {
		try {
			surveyHandler.detachImeiFromSurvey(surveyID, imeiNumber);
		} catch (MSMApplicationException e) {
			e.printStackTrace();
		}
	}

	public void sendDynamicJad(String msisdn, String imei)
			throws MSMApplicationException {
		String urlToJad = null;
		try {
			urlToJad = deviceManager.createDynamicJad(msisdn);
			ArrayList<ImeiVO> listoOfDevices = new ArrayList<ImeiVO>();
			ImeiVO imeiVo = new ImeiVO();
			imeiVo.setImei(imei);
			imeiVo.setMsisdn(msisdn);
			listoOfDevices.add(imeiVo);
			if (SmsHandlerFactory.getInstance().hasSmsSupport()){
				sendLinkSMS(urlToJad, listoOfDevices);	
			}
		} catch (MSMApplicationException e) {
			e.printStackTrace();
		}
	}

	public byte[] jadDownload(String msisdn) throws MSMApplicationException{
		deviceManager.createDynamicJad(msisdn);

		if (msisdn.contains("+")) {
			msisdn = msisdn.replace("+", "");
		}
		String jboss_home_dir = System.getProperty("jboss.server.home.dir");
		String deployJadDir = jboss_home_dir + "/deploy/ndg-ota.war/client/dyn/" + msisdn + "/ndg.jad";
		
		File file = new File(deployJadDir);
		byte[] byteArray = null;
		try {
			byteArray = FileUtil.getBytesFromFile(file);
		} catch (IOException e) {
			e.printStackTrace();
			throw new JadDownloadException();
		}
		return byteArray;
	}

	public void deleteJadDir(String msisdn) throws MSMApplicationException{
		deviceManager.deleteJadDir(msisdn);
	}

	public Survey getSurveyObject(String surveyId) {
		return surveyHandler.getSurveyObject(surveyId);
	}

	public String getNDGVersion() {
		String ndgVersion = "0.0.0";

		Properties properties = PropertiesUtil
				.loadFileProperty(PropertiesUtil.PROPERTIES_VERSION_FILE);

		if ((properties != null) && (properties.containsKey("version"))) {
			ndgVersion = properties.getProperty("version");
		}

		return ndgVersion;
	}

	public boolean hasSmsSupport(){
		return SmsHandlerFactory.getInstance().hasSmsSupport();
	}

	public void registerIMEI(String msisdn, String newImei) throws MSMApplicationException {
		System.out.println("[MSMBusinessDelegate.registerIMEI] Msisdn " + msisdn +
				"; Imei: " + newImei);
		imeiManager.registerIMEI(msisdn, newImei);
		deleteJadDir(msisdn);
	}

    public SurveyXML loadSelectedResults(ArrayList<String> resultIds, String surveyId)
                     throws MSMApplicationException, MSMSystemException {
    	SurveyXML surveyXML = surveyHandler.loadSelectedResults(resultIds, surveyId);
    	
        return surveyXML;
    }

    /**
	 * 
	 * @return map <language name, locale>
	 */
    public HashMap<String, String> getLanguageList() {
		return languageManager.getLanguageList();
	}

    public String getLanguageFileName(String locale) {
		return languageManager.getLanguagePath(locale);
	}

    public String getFontFileName(String locale) {
        return languageManager.getFontPath(locale);
	}
}
