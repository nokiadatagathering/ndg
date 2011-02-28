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

package br.org.indt.ndg.server.survey;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import br.org.indt.ndg.common.CreateXml;
import br.org.indt.ndg.common.MD5;
import br.org.indt.ndg.common.ResultParser;
import br.org.indt.ndg.common.ResultXml;
import br.org.indt.ndg.common.SurveyParser;
import br.org.indt.ndg.common.SurveyXML;
import br.org.indt.ndg.common.SystemUtils;
import br.org.indt.ndg.common.exception.MSMApplicationException;
import br.org.indt.ndg.common.exception.MSMSystemException;
import br.org.indt.ndg.common.exception.ModemException;
import br.org.indt.ndg.common.exception.PostSurveyException;
import br.org.indt.ndg.common.exception.PropertyNotExistExcpetion;
import br.org.indt.ndg.common.exception.ResultNotParsedException;
import br.org.indt.ndg.common.exception.SurveyFileAlreadyExistsException;
import br.org.indt.ndg.common.exception.SurveyNotFoundException;
import br.org.indt.ndg.common.exception.SurveyNotParsedException;
import br.org.indt.ndg.common.exception.SurveyNotRecordedException;
import br.org.indt.ndg.common.exception.SurveyRationException;
import br.org.indt.ndg.common.exception.TransactionLogNotFoundException;
import br.org.indt.ndg.server.authentication.UserManager;
import br.org.indt.ndg.server.client.DeviceVO;
import br.org.indt.ndg.server.client.ImeiVO;
import br.org.indt.ndg.server.client.MSMBusinessDelegate;
import br.org.indt.ndg.server.client.SurveyVO;
import br.org.indt.ndg.server.client.TransactionLogVO;
import br.org.indt.ndg.server.controller.QueryInputOutputVO;
import br.org.indt.ndg.server.imei.IMEIManager;
import br.org.indt.ndg.server.pojo.Imei;
import br.org.indt.ndg.server.pojo.NdgDevice;
import br.org.indt.ndg.server.pojo.NdgUser;
import br.org.indt.ndg.server.pojo.Result;
import br.org.indt.ndg.server.pojo.Survey;
import br.org.indt.ndg.server.pojo.Transactionlog;
import br.org.indt.ndg.server.sms.SMSModemHandler;
import br.org.indt.ndg.server.sms.SmsHandlerAbs;
import br.org.indt.ndg.server.sms.SmsHandlerFactory;
import br.org.indt.ndg.server.sms.SurveySmsSlicer;
import br.org.indt.ndg.server.sms.vo.SMSMessageVO;
import br.org.indt.ndg.server.transaction.TransactionLogManager;
import br.org.indt.ndg.server.util.PropertiesUtil;
import br.org.indt.ndg.server.util.SqlUtil;

public @Stateless
class SurveyHandlerBean implements SurveyHandler 
{
	@PersistenceContext(name = "MobisusPersistence")
	
	private EntityManager manager;
	private static Log log = LogFactory.getLog(SurveyHandlerBean.class);
	
	MSMBusinessDelegate businessDelegate = new MSMBusinessDelegate();

	public Properties getSettings() throws MSMApplicationException 
	{
		return PropertiesUtil.loadFileProperty(PropertiesUtil.SETTINGS_FILE);
	}

	public void updateSettings(Properties settings)
			throws MSMApplicationException {
		PropertiesUtil.updateSettings(settings);
	}
	
	public QueryInputOutputVO getImeisBySurvey(String surveyId, String status, QueryInputOutputVO queryIOVO) throws MSMApplicationException 
	{
		if (queryIOVO == null)
		{
			queryIOVO = new QueryInputOutputVO();
		}
		
		String sQuery = "from Transactionlog where transactionType = ";
		sQuery += "\'";
		sQuery += TransactionLogVO.TYPE_SEND_SURVEY;
		sQuery += "\'";
		sQuery += " and survey.idSurvey = :surveyId and transactionStatus = :status";

		// PENDING is all that was not downloaded yet (may be PENDING or
		// AVAILABLE, any status that is not SUCCESS)
		if ((status != null) && status.equals(TransactionLogVO.STATUS_PENDING)) 
		{
			sQuery = "";
			sQuery += "from Transactionlog where transactionType = ";
			sQuery += "\'";
			sQuery += TransactionLogVO.TYPE_SEND_SURVEY;
			sQuery += "\'";
			sQuery += " and survey.idSurvey = :surveyId and NOT(transactionStatus = " + "\'" + TransactionLogVO.STATUS_SUCCESS + "\')";
		}
		
		if ((queryIOVO.getFilterText() != null) && (queryIOVO.getFilterFields() != null))
		{
			sQuery += SqlUtil.getFilterCondition(queryIOVO.getFilterText(), queryIOVO.getFilterFields());
		}
		
		if ((queryIOVO.getSortField() != null) && (queryIOVO.getIsDescending() != null))
		{
			sQuery += SqlUtil.getSortCondition(queryIOVO.getSortField(), queryIOVO.getIsDescending());
		}

		Query q = manager.createQuery(sQuery);
		
		q.setParameter("surveyId", surveyId);

		// if it is not PENDING 'status' parameter is used
		if ((status != null) && (!status.equals(TransactionLogVO.STATUS_PENDING))) 
		{
			q.setParameter("status", status);
		}

		queryIOVO.setRecordCount(q.getResultList().size());
					
		if ((queryIOVO.getPageNumber() != null) && (queryIOVO.getRecordsPerPage() != null))
		{
			q.setFirstResult((queryIOVO.getPageNumber() - 1) * queryIOVO.getRecordsPerPage());
			q.setMaxResults(queryIOVO.getRecordsPerPage());
		}
		
		ArrayList<Object> ret = new ArrayList<Object>();
		ArrayList<Transactionlog> al = (ArrayList<Transactionlog>) q.getResultList();
		
		Iterator<Transactionlog> it = al.iterator();

		while (it.hasNext()) 
		{
			Transactionlog surveyTransactionLog = (Transactionlog) it.next();
			
			Imei imei = null;
			
			if (surveyTransactionLog.getImei() != null) {
				imei = manager.find(Imei.class, surveyTransactionLog.getImei().getImei());
				System.out.println("####### SurveyTransactionLog: IMEI = " + surveyTransactionLog.getImei());
			}

			if (imei != null) 
			{
				ImeiVO vo = new ImeiVO();
				vo.setImei(imei.getImei());
				vo.setMsisdn(imei.getMsisdn());
				vo.setUserName(imei.getUser().getUsername());
				NdgDevice device = imei.getDevice();
				DeviceVO devVO = new DeviceVO();
				devVO.setIdDevice(device.getIdDevice());
				devVO.setDeviceModel(device.getDeviceModel());
				vo.setDevice(devVO);
				vo.setRealImei(imei.getRealImei());

				if (!surveyTransactionLog.getTransactionStatus().equals(TransactionLogVO.STATUS_SUCCESS)) 
				{
					vo.setStatus(TransactionLogVO.STATUS_PENDING);
				}
				else 
				{
					vo.setStatus(surveyTransactionLog.getTransactionStatus());
				}

				ret.add(vo);
			}
		
		}

		queryIOVO.setQueryResult(ret);
		
		return queryIOVO;
	}
	
	public QueryInputOutputVO getAllImeisBySurvey(String surveyId, QueryInputOutputVO queryIOVO)
			throws MSMApplicationException {
		
		if (queryIOVO == null){
			queryIOVO = new QueryInputOutputVO();
		}
		
		String sQuery = "from Transactionlog where transactionType = ";
		sQuery += "\'";
		sQuery += TransactionLogVO.TYPE_SEND_SURVEY;
		sQuery += "\'";
		sQuery += " and survey.idSurvey = :surveyId";

		if ((queryIOVO.getFilterText() != null) && (queryIOVO.getFilterFields() != null)){
			sQuery += SqlUtil.getFilterCondition(queryIOVO.getFilterText(), queryIOVO.getFilterFields());
		}
		if ((queryIOVO.getSortField() != null) && (queryIOVO.getIsDescending() != null)){
			sQuery += SqlUtil.getSortCondition(queryIOVO.getSortField(), queryIOVO.getIsDescending());
		}

		Query q = manager.createQuery(sQuery);
		q.setParameter("surveyId", surveyId);
		if ((queryIOVO.getPageNumber() != null) && (queryIOVO.getRecordsPerPage() != null)){
			q.setFirstResult((queryIOVO.getPageNumber() - 1) * queryIOVO.getRecordsPerPage());
			q.setMaxResults(queryIOVO.getRecordsPerPage());
		}
		
		ArrayList<Object> ret = new ArrayList<Object>();
		ArrayList<Transactionlog> al = (ArrayList<Transactionlog>) q.getResultList();
		Iterator<Transactionlog> it = al.iterator();
		ArrayList<String> imeiIdListAux = new ArrayList<String>();
		
		while (it.hasNext()){
			Transactionlog surveyTransactionLog = (Transactionlog) it.next();
			if (imeiIdListAux.contains(surveyTransactionLog.getImei().getImei())){
				continue;
			}
			imeiIdListAux.add(surveyTransactionLog.getImei().getImei());
			
			Imei imei = null;
			if (surveyTransactionLog.getImei() != null){
				imei = manager.find(Imei.class, surveyTransactionLog.getImei().getImei());
			}
			if (imei != null) {
				ImeiVO vo = new ImeiVO();
				vo.setImei(imei.getImei());
				vo.setMsisdn(imei.getMsisdn());
				vo.setUserName(imei.getUser().getUsername());
				NdgDevice device = imei.getDevice();
				DeviceVO devVO = new DeviceVO();
				devVO.setIdDevice(device.getIdDevice());
				devVO.setDeviceModel(device.getDeviceModel());
				vo.setDevice(devVO);
				vo.setRealImei(imei.getRealImei());

				if (!surveyTransactionLog.getTransactionStatus().equals(TransactionLogVO.STATUS_SUCCESS)){
					vo.setStatus(TransactionLogVO.STATUS_PENDING);
				} else{
					vo.setStatus(surveyTransactionLog.getTransactionStatus());
				}
				
				StringBuilder builder = new StringBuilder();
				builder.append("from Result where idSurvey = '");
				builder.append(surveyId);
				builder.append("' and imei = '");
				builder.append(vo.getImei());
				builder.append("'");
				Query query = manager.createQuery(builder.toString());
				
				vo.setQtdeResults(query.getResultList().size());
				ret.add(vo);
			}
		}

		queryIOVO.setRecordCount(ret.size());
		queryIOVO.setQueryResult(ret);
		
		return queryIOVO;
	}

	public void detachImeiFromSurvey(String surveyID, String imeiNumber) throws MSMApplicationException
	{
		String sQuery = "from Transactionlog t where t.transactionType = ";
		sQuery += "\'";
		sQuery += TransactionLogVO.TYPE_SEND_SURVEY;
		sQuery += "\'";
		sQuery += " and survey.idSurvey = :surveyID";
		sQuery += " and t.imei.imei = :imeiNumber";

		Query q = manager.createQuery(sQuery);
		
		q.setParameter("surveyID", surveyID);
		q.setParameter("imeiNumber", imeiNumber);

		ArrayList<Transactionlog> queryResult = (ArrayList<Transactionlog>) q.getResultList();
		Iterator<Transactionlog> queryIterator = queryResult.iterator();

		while (queryIterator.hasNext()) 
		{
			Transactionlog transactionlog = (Transactionlog) queryIterator.next();
			manager.remove(transactionlog);
		}
	}

	public void sendSlicedSurvey(NdgUser userLogged, String idSurvey, ArrayList<String> listOfDevices) throws MSMApplicationException, MSMSystemException 
	{
		for (String deviceNumber : listOfDevices) 
		{
			SurveySmsSlicer surveySMSSlicer = new SurveySmsSlicer(userLogged.getUserAdmin(), idSurvey, deviceNumber);
			ArrayList<SMSMessageVO> arrayOfSMS = surveySMSSlicer.getSlicedSurvey();

			for (SMSMessageVO smsMessageVO : arrayOfSMS) 
			{
				smsMessageVO.port = SMSModemHandler.SMS_NDG_PORT;
				businessDelegate.sendSMS(smsMessageVO);
			}
			
			TransactionLogVO tl = new TransactionLogVO();
			tl.setDtLog(new Timestamp(System.currentTimeMillis()));

			InitialContext initialContext = null;
			IMEIManager imeiManager = null;

			try 
			{
				initialContext = new InitialContext();
				imeiManager = (IMEIManager) initialContext.lookup("ndg-core/IMEIManagerBean/remote");
			}
			catch (NamingException e) 
			{
				e.printStackTrace();
			}

			ImeiVO imeivo = imeiManager.findImeiByMsisdn(deviceNumber);

			tl.setUser(userLogged.getUsername());
			tl.setImei(imeivo.getImei());
			tl.setSurveyId(idSurvey);
			tl.setStatus(TransactionLogVO.STATUS_SUCCESS);
			tl.setTransactionType(TransactionLogVO.TYPE_SEND_SURVEY);
			tl.setTransmissionMode(TransactionLogVO.MODE_SMS);

			// log the transaction status, as SUCCESS, when sending a survey by SMS
			TransactionLogManager transactionlogManager = null;

			try 
			{
				transactionlogManager = (TransactionLogManager) initialContext.lookup("ndg-core/TransactionLogManagerBean/remote");
			}
			catch (NamingException e) 
			{
				e.printStackTrace();
			}

			if (transactionlogManager != null) 
			{
				transactionlogManager.logTransaction(tl);
			}
		}
	}

	public Collection<SurveyVO> listSurveysByImeiDB(String imei, String status) throws MSMApplicationException, MSMSystemException 
	{
		NdgUser user = businessDelegate.getUserByImei(imei);

		ArrayList<SurveyVO> ret = new ArrayList<SurveyVO>();

		Query q = manager.createQuery("from Transactionlog t where transactionType = "
		+ "\'" + TransactionLogVO.TYPE_SEND_SURVEY + "\' "
		+ "and imei.imei = :imei "
		+ "and transactionStatus = :status "
		+ "group by t.survey.idSurvey");
		
		q.setParameter("imei", imei);
		q.setParameter("status", status);

		ArrayList<Transactionlog> al = (ArrayList<Transactionlog>) q.getResultList();
		Iterator<Transactionlog> it = al.iterator();

		while (it.hasNext()) 
		{
			SurveyXML survey = null;
			Transactionlog surveyTransactionLog = (Transactionlog) it.next();
			SurveyVO vo = new SurveyVO();
			vo.setIdSurvey(surveyTransactionLog.getSurvey().getIdSurvey());

			survey = loadSurveyAndResultsDB(user.getUserAdmin(), surveyTransactionLog.getSurvey().getIdSurvey());
			CreateXml.xmlToString(survey.getXmldoc());
			Survey surveyPojo = manager.find(Survey.class, surveyTransactionLog.getSurvey().getIdSurvey());

			try 
			{
				byte[] stringXMLByte = surveyPojo.getSurveyXml().getBytes("UTF-8");
				vo.setSurvey(new String(stringXMLByte, "UTF-8"));

			}
			catch (UnsupportedEncodingException e) 
			{
				e.printStackTrace();
			}
			
			vo.setTitle(survey.getTitle());
			vo.setResultsSent(survey.getResultsSize());
			vo.setStatus(surveyTransactionLog.getTransactionStatus());

			ret.add(vo);

		}
		
		return ret;
	}
	
	public QueryInputOutputVO listSurveysByImeiDBWithoutResults(String imei, String status, QueryInputOutputVO queryIOVO) throws MSMApplicationException, MSMSystemException 
	{
		if (queryIOVO == null)
		{
			queryIOVO = new QueryInputOutputVO();
		}
		
		NdgUser user = businessDelegate.getUserByImei(imei);

		String sqlCommand = "from Transactionlog t where transactionType = " + "\'" + TransactionLogVO.TYPE_SEND_SURVEY + "\' "
		+ "and imei.imei = :imei "
		+ "and transactionStatus = :status ";
		
		if ((queryIOVO.getFilterText() != null) && (queryIOVO.getFilterFields() != null))
		{
			sqlCommand += SqlUtil.getFilterCondition(queryIOVO.getFilterText(), queryIOVO.getFilterFields());
		}

		sqlCommand += " group by t.survey.idSurvey ";
		
		if ((queryIOVO.getSortField() != null) && (queryIOVO.getIsDescending() != null))
		{
			sqlCommand += SqlUtil.getSortCondition(queryIOVO.getSortField(), queryIOVO.getIsDescending());
		}
		
		Query q = manager.createQuery(sqlCommand);
		
		q.setParameter("imei", imei);
		q.setParameter("status", status);
		
		queryIOVO.setRecordCount(q.getResultList().size());
		
		if ((queryIOVO.getPageNumber() != null) && (queryIOVO.getRecordsPerPage() != null))
		{
			q.setFirstResult((queryIOVO.getPageNumber() - 1) * queryIOVO.getRecordsPerPage());
			q.setMaxResults(queryIOVO.getRecordsPerPage());
		}

		ArrayList<Transactionlog> al = (ArrayList<Transactionlog>) q.getResultList();
		Iterator<Transactionlog> it = al.iterator();

		ArrayList<Object> surveyList = new ArrayList<Object>();
		
		while (it.hasNext()) 
		{
			SurveyXML survey = null;
			Transactionlog surveyTransactionLog = (Transactionlog) it.next();
			SurveyVO vo = new SurveyVO();
			vo.setIdSurvey(surveyTransactionLog.getSurvey().getIdSurvey());

			survey = loadSurveyDB(user.getUserAdmin(), surveyTransactionLog.getSurvey().getIdSurvey());
			CreateXml.xmlToString(survey.getXmldoc());
			Survey surveyPojo = manager.find(Survey.class, surveyTransactionLog.getSurvey().getIdSurvey());

			try 
			{
				byte[] stringXMLByte = surveyPojo.getSurveyXml().getBytes("UTF-8");
				vo.setSurvey(new String(stringXMLByte, "UTF-8"));

			}
			catch (UnsupportedEncodingException e) 
			{
				e.printStackTrace();
			}
			
			vo.setTitle(survey.getTitle());
			vo.setResultsSent(survey.getResultsSize());
			vo.setStatus(surveyTransactionLog.getTransactionStatus());

			surveyList.add(vo);
		}
		
		queryIOVO.setQueryResult(surveyList);
		
		return queryIOVO;
	}

	@Override
	public void updateStatusSendingSurvey(String imei, String status)
			throws MSMApplicationException, MSMSystemException {
		Query q = manager
				.createQuery("from Transactionlog where transactionType = "
						+ "\'" + TransactionLogVO.TYPE_SEND_SURVEY + "\' "
						+ "and imei.imei = :imei "
						+ "and transactionStatus = \'"
						+ TransactionLogVO.STATUS_AVAILABLE + "\'");

		q.setParameter("imei", imei);
		ArrayList<Transactionlog> arraySurveyTransactionLog = (ArrayList<Transactionlog>) q
				.getResultList();
		Iterator<Transactionlog> it = arraySurveyTransactionLog.iterator();
		while (it.hasNext()) {

			// SurveyTransactionLog log = (SurveyTransactionLog) it.next();
			Transactionlog log = manager.find(Transactionlog.class,
					((Transactionlog) it.next()).getIdTransactionLog());
			if (log == null) {
				throw new TransactionLogNotFoundException();
			}
			System.out.println(">>>>> SurveyTransactioneLog: " + log.getImei()
					+ " - " + log.getTransactionStatus() + " - "
					+ log.getSurvey().getIdSurvey());
			log.setTransactionStatus(status);
			manager.persist(log);
		}

	}

	@Override
	public ArrayList<String> rationSurveybyGPRS(NdgUser userlogged,
			String idSurvey, ArrayList<String> listOfDevices)
			throws MSMApplicationException, MSMSystemException {
		ArrayList<String> devicesToGetNewSurvey = new ArrayList<String>();
		for (Iterator<String> iterator = listOfDevices.iterator(); iterator
				.hasNext();) {

			String deviceIMEI = iterator.next();

			InitialContext initialContext = null;

			TransactionLogManager transactionlogManager = null;

			try 
			{
				initialContext = new InitialContext();
				transactionlogManager = (TransactionLogManager) initialContext.lookup("ndg-core/TransactionLogManagerBean/remote");
			}
			catch (NamingException e) 
			{
				new SurveyRationException();
			}

			if (!transactionlogManager.existTransactionLog(
					TransactionLogVO.TYPE_SEND_SURVEY, idSurvey,
					TransactionLogVO.STATUS_AVAILABLE,
					TransactionLogVO.MODE_GPRS, deviceIMEI)) {
				TransactionLogVO t = new TransactionLogVO();
				t.setUser(userlogged.getUsername());
				t.setDtLog(new Timestamp(System.currentTimeMillis()));
				t.setTransmissionMode(TransactionLogVO.MODE_GPRS);
				t.setStatus(TransactionLogVO.STATUS_AVAILABLE);

				t.setTransactionType(TransactionLogVO.TYPE_SEND_SURVEY);
				t.setImei(deviceIMEI);
				t.setSurveyId(idSurvey);

				if (transactionlogManager != null) {
					transactionlogManager.logTransaction(t);
				}
				devicesToGetNewSurvey.add(deviceIMEI);
			}
		}
		return devicesToGetNewSurvey;
	}

	@Override
	public Properties getProperties() throws MSMApplicationException {
		return null;
	}

	public Properties getModemProperties() throws MSMApplicationException {
		SmsHandlerAbs smsModemHandler = SmsHandlerFactory.getInstance()
				.getSmsHandler();
		Properties modemProperties;
		if (smsModemHandler instanceof SMSModemHandler) {
			modemProperties = ((SMSModemHandler) smsModemHandler)
					.getModemProperties();
		} else {
			throw new ModemException();
		}
		return modemProperties;
	}

	@Override
	public QueryInputOutputVO listSurveysDB(NdgUser user, QueryInputOutputVO queryIOVO, Boolean isUploaded) throws MSMApplicationException, MSMSystemException 
	{
		if (queryIOVO == null)
		{
			queryIOVO = new QueryInputOutputVO();
		}

		try
		{
			MSMBusinessDelegate msmBD = new MSMBusinessDelegate();

			String sqlCommand = "SELECT U FROM Survey U WHERE idUser LIKE " + msmBD.findNdgUserByName(user.getUserAdmin()).getIdUser();

			if (isUploaded)
			{
				sqlCommand += " AND upper(U.isUploaded) <> 'N' ";
			}
			
			if ((queryIOVO.getFilterText() != null) && (queryIOVO.getFilterFields() != null))
			{
				sqlCommand += SqlUtil.getFilterCondition(queryIOVO.getFilterText(), queryIOVO.getFilterFields());
			}

			if ((queryIOVO.getSortField() != null) && (queryIOVO.getIsDescending() != null))
			{
				sqlCommand += SqlUtil.getSortCondition(queryIOVO.getSortField(), queryIOVO.getIsDescending());
			}

			Query q = manager.createQuery(sqlCommand);
			queryIOVO.setRecordCount(q.getResultList().size());

			if ((queryIOVO.getPageNumber() != null) && (queryIOVO.getRecordsPerPage() != null))
			{
				q.setFirstResult((queryIOVO.getPageNumber() - 1) * queryIOVO.getRecordsPerPage());
				q.setMaxResults(queryIOVO.getRecordsPerPage());
			}

			ArrayList<Object> surveyList = new ArrayList<Object>();
			ArrayList<Survey> surveysListDB = (ArrayList<Survey>) q.getResultList();

			SurveyParser surveyParser = new SurveyParser();

			for (Survey survey : surveysListDB) 
			{
				SurveyVO bean = new SurveyVO();

				SurveyXML surveyXml = surveyParser.parseSurvey(new StringBuffer(survey.getSurveyXml()), "UTF-8");
				
				/*
				surveyXml.setResults(loadResultsDB(surveyXml.getId()));
				*/
				
				bean.setIdSurvey(surveyXml.getId());
				bean.setCheck("false");
				bean.setTitle(surveyXml.getTitle());
				bean.setResults(getQtResultsBySurvey(surveyXml.getId()));
				
				
				if ((survey.getIsUploaded() != 'n') && (survey.getIsUploaded() != 'N')){
					bean.setIsUploaded('Y');
				} else {
					bean.setIsUploaded(survey.getIsUploaded());
				}
				
				if (!isUploaded){
					bean.setSurvey(survey.getSurveyXml());
				}
				
				/*
				Collection<ImeiVO> devices = msmBD.getImeisSentBySurvey(surveyXml.getId());
				*/
				
				Integer qtImeisSuccess = getQtImeisBySurvey(surveyXml.getId(), TransactionLogVO.STATUS_SUCCESS);
				bean.setDevice("" + qtImeisSuccess);

				/* 
				Collection<ImeiVO> pending = msmBD.getImeisUnsentBySurvey(surveyXml.getId());
				*/
				
				Integer qtImeisPending = getQtImeisBySurvey(surveyXml.getId(), TransactionLogVO.STATUS_PENDING);
				bean.setPending("" + qtImeisPending);

				TransactionLogVO logs = msmBD.getSurveyReceived(surveyXml.getId());
				
				if (logs != null) 
				{
					bean.setUser(logs.getUser());
					bean.setDate(SystemUtils.toDate(logs.getDtLog()));
				}
				
				surveyList.add(bean);
				
				log.info("Survey: " + surveyXml.getTitle());
			}
			
			queryIOVO.setQueryResult(surveyList);
		}
		catch (Exception e) 
		{
			throw new SurveyNotParsedException();
		}

		return queryIOVO;
	}

	@Override
	public void postSurvey(NdgUser user, StringBuffer surveyBuffered,
			TransactionLogVO postSurveyTransaction)
			throws MSMApplicationException, MSMSystemException, SurveyFileAlreadyExistsException {
		SurveyParser parser = new SurveyParser();
		SurveyXML survey = null;

		try {
			survey = parser.parseSurvey(surveyBuffered, "UTF-8");

			if (survey != null) {
				if (MD5.checkMD5Survey(surveyBuffered, survey.getChecksum())) {
					saveSurvey(user, survey, surveyBuffered);
					postSurveyTransaction
							.setStatus(TransactionLogVO.STATUS_SUCCESS);
				}
			} else {
				postSurveyTransaction.setStatus(TransactionLogVO.STATUS_ERROR);
			}

			postSurveyTransaction.setUser(user.getUsername());
			postSurveyTransaction.setSurveyId(survey.getId());
			postSurveyTransaction.setDtLog(new Timestamp(System
					.currentTimeMillis()));
			businessDelegate.logTransaction(postSurveyTransaction);
		}
		catch (SurveyFileAlreadyExistsException e) 
		{
			throw new SurveyFileAlreadyExistsException(e.getMessage());
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			
			throw new PostSurveyException();
		}
	}

	public void saveSurvey(NdgUser user, SurveyXML survey, StringBuffer buffer) throws IOException,
		SurveyFileAlreadyExistsException, MSMApplicationException {
		persistSurvey(user, survey, buffer);
	}


	public SurveyXML loadSurveyDB(String username, String idSurvey) throws MSMApplicationException, MSMSystemException 
	{
		SurveyXML surveyXML = null;
		SurveyParser parser = new SurveyParser();

		try 
		{
			Survey survey = manager.find(Survey.class, idSurvey);
			surveyXML = parser.parseSurvey(new StringBuffer(survey.getSurveyXml()), "UTF-8");
		}
		catch (Exception e) 
		{
			throw new SurveyNotParsedException();
		}

		return surveyXML;
	}
	
	public SurveyXML loadSurveyAndResultsDB(String username, String idSurvey) throws MSMApplicationException, MSMSystemException 
	{
		SurveyXML surveyXML = null;
		SurveyParser parser = new SurveyParser();
		
		try 
		{
			Survey survey = manager.find(Survey.class, idSurvey);
			surveyXML = parser.parseSurvey(new StringBuffer(survey.getSurveyXml()), "UTF-8");
			surveyXML.setResults(loadResultsDB(idSurvey));
		}
		catch (ResultNotParsedException e)
		{
			throw new ResultNotParsedException();
		}
		catch (Exception e)
		{
			throw new SurveyNotParsedException();
		}

		return surveyXML;
	}

	public ArrayList<SurveyXML> loadSurveysDB(NdgUser user) throws MSMApplicationException, MSMSystemException 
	{
		ArrayList<SurveyXML> surveyXMLList = new ArrayList<SurveyXML>();

		Query q = manager.createNamedQuery("survey.findByUserAdmin");
		q.setParameter("userAdmin", user);

		ArrayList<Survey> surveysListDB = null;
		surveysListDB = (ArrayList<Survey>) q.getResultList();

		SurveyParser parser = new SurveyParser();

		for (Survey survey : surveysListDB) 
		{
			SurveyXML surveyXml;
			
			try 
			{
				surveyXml = parser.parseSurvey(new StringBuffer(survey.getSurveyXml()), "UTF-8");
				surveyXml.setResults(loadResultsDB(survey.getIdSurvey()));
				surveyXMLList.add(surveyXml);
			}
			catch (SAXException e) 
			{
				e.printStackTrace();
			}
			catch (ParserConfigurationException e) 
			{
				e.printStackTrace();
			}
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}

		return surveyXMLList;
	}

	public ArrayList<ResultXml> loadResultsDB(String idSurvey) throws MSMApplicationException, MSMSystemException 
	{
		ArrayList<ResultXml> resultXMLList = new ArrayList<ResultXml>();
		
		Query q = manager.createNamedQuery("result.findBySurvey");
		q.setParameter("survey", idSurvey);
		
		ArrayList<Result> resultsListDB = (ArrayList<Result>) q.getResultList();

		ResultParser parser = new ResultParser();

		for (Result result : resultsListDB) 
		{
			ResultXml resultXml = null;
			
			try 
			{
				resultXml = parser.parseResult(new StringBuffer(result.getResultXml()), "UTF-8");
				ImeiVO imei = businessDelegate.getImei(resultXml.getImei());
				resultXml.setPhoneNumber(imei.getMsisdn());
				resultXMLList.add(resultXml);
			}
			catch (Exception e) 
			{
				throw new ResultNotParsedException();
			}
		}

		return resultXMLList;
	}

	@Override
	public Survey getUserBySurvey(String idSurvey)
			throws MSMApplicationException {
		Query q = manager.createNamedQuery("survey.getUserBySurvey");
		q.setParameter("IDSurvey", idSurvey);
		Survey survey = null;
		survey = (Survey) q.getSingleResult();
		return survey;
	}

	public void saveSurveyFromEditorToServer(String userName,
			String surveyContent) throws MSMApplicationException {
		
		SurveyParser parser = new SurveyParser();
		StringBuffer buffer = new StringBuffer(surveyContent);

		InitialContext initialContext = null;
		UserManager userManager = null;
		NdgUser user = null;
		SurveyXML survey = null;
		
		try {
			initialContext = new InitialContext();
			userManager = (UserManager) initialContext.lookup("ndg-core/UserManagerBean/remote");
			if (userManager != null) {
				user = userManager.findNdgUserByName(userName);
			}
			survey = parser.parseSurvey(buffer, "UTF-8");
		} catch (NamingException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
			throw new SurveyNotParsedException();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			throw new SurveyNotParsedException();
		} catch (IOException e) {
			e.printStackTrace();
			throw new SurveyNotParsedException();
		}

		if ((survey != null) && (user != null)) {
			try {
				if (MD5.checkMD5Survey(buffer, survey.getChecksum())) {
					Survey currentSurvey = manager.find(Survey.class, survey.getId());
					if (currentSurvey != null) {
						updateSurvey(user, survey, buffer);
					} else {
						persistSurvey(user, survey, buffer);
					}
				}
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
				throw new SurveyNotParsedException();
			} catch (IOException e) {
				e.printStackTrace();
				throw new SurveyNotParsedException();
			}
		} else{
			throw new SurveyNotRecordedException();
		}
	}
	
	private void persistSurvey(NdgUser user, SurveyXML survey,
			StringBuffer buffer)throws MSMApplicationException {
		
		br.org.indt.ndg.server.pojo.Survey surveyToPersist = new br.org.indt.ndg.server.pojo.Survey();
		surveyToPersist.setIdSurvey(survey.getId());
		
		NdgUser userAdmin = businessDelegate.findNdgUserByName(user.getUserAdmin());

		surveyToPersist.setIdUserAdmin(userAdmin);
		surveyToPersist.setSurveyXml(buffer.toString());
		char deployed = survey.getDeployed().equalsIgnoreCase("true") ? 'Y' : 'N';
		surveyToPersist.setIsUploaded(deployed);
		
		manager.persist(surveyToPersist);
		
		TransactionLogVO t = new TransactionLogVO();
		t.setTransmissionMode(TransactionLogVO.MODE_HTTP);
		t.setTransactionType(TransactionLogVO.TYPE_RECEIVE_SURVEY);
		t.setStatus(TransactionLogVO.STATUS_SUCCESS);
		t.setUser(user.getUsername());
		t.setSurveyId(survey.getId());
		t.setDtLog(new Timestamp(System.currentTimeMillis()));
		
		businessDelegate.logTransaction(t);
	}

	private void updateSurvey(NdgUser user, SurveyXML survey, StringBuffer buffer)
			throws MSMApplicationException {
		Survey surveyToPersist = manager.find(Survey.class, survey.getId());

		if (surveyToPersist != null) {
			NdgUser userAdmin = businessDelegate.findNdgUserByName(user.getUserAdmin());

			surveyToPersist.setIdUserAdmin(userAdmin);
			surveyToPersist.setSurveyXml(buffer.toString());
			char deployed = survey.getDeployed().equalsIgnoreCase("true") ? 'Y' : 'N';
			surveyToPersist.setIsUploaded(deployed);

			manager.persist(surveyToPersist);
		} else{
			throw new SurveyNotFoundException();
		}
	}
	
	public String loadSurveyFromServerToEditor(String userName, String surveyID)
			throws MSMApplicationException {

		Survey survey = manager.find(Survey.class, surveyID);
		if (survey == null){
			throw new SurveyNotFoundException();
		}
		return survey.getSurveyXml();
	}

	public ArrayList<String> loadSurveysFromServerToEditor(String userName)
			throws MSMApplicationException {
		ArrayList<String> surveyList = new ArrayList<String>();

		InitialContext initialContext = null;

		try {
			initialContext = new InitialContext();
		} catch (NamingException e) {
			e.printStackTrace();
		}

		UserManager userManager = null;

		try {
			userManager = (UserManager) initialContext
					.lookup("ndg-core/UserManagerBean/remote");
		} catch (NamingException e) {
			e.printStackTrace();
		}

		NdgUser user = null;

		try {
			try {
				if (userManager != null) {
					user = userManager.findNdgUserByName(userName);
				}
			} catch (MSMApplicationException e) {
				e.printStackTrace();
			}
		} catch (NoResultException e) {
			// empty
		}

		ArrayList<SurveyXML> userSurveyList = new ArrayList<SurveyXML>();

		try {
			userSurveyList = loadSurveysDB(user);
		} catch (MSMSystemException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < userSurveyList.size(); i++) {
			Survey survey = manager.find(Survey.class,
					((SurveyXML) userSurveyList.toArray()[i]).getId());

			surveyList.add(survey.getSurveyXml().toString());
		}

		return surveyList;
	}

	public void deleteSurveyFromServer(String surveyID)
			throws MSMApplicationException {
		Survey survey = manager.find(Survey.class, surveyID);
		if (survey != null){
			manager.remove(survey);	
		} else{
			throw new SurveyNotFoundException();
		}
	}

	public void uploadSurveyFromEditorToServer(String surveyID) 
			throws MSMApplicationException{
		Survey surveyToUpdate = manager.find(Survey.class, surveyID);
		if (surveyToUpdate != null) {
			surveyToUpdate.setIsUploaded('Y');
			manager.persist(surveyToUpdate);
		} else{
			throw new SurveyNotFoundException();
		}
	}

	private Integer getQtResultsBySurvey(String idSurvey) throws MSMApplicationException 
	{
		Query q = manager.createNamedQuery("result.getQtResultsBySurvey");
		q.setParameter("IDSurvey", idSurvey);
		Long qt = (Long) q.getSingleResult();

		return qt.intValue();
	}

	private Integer getQtImeisBySurvey(String idSurvey, String status) {

		String sQuery = "select count(*) ";
		sQuery += "from Transactionlog where transactionType = ";
		sQuery += "\'";
		sQuery += TransactionLogVO.TYPE_SEND_SURVEY;
		sQuery += "\'";
		sQuery += " and survey.idSurvey = :surveyId";

		/** PENDING = all not equal SUCCESS * */
		if ((status != null) && status.equals(TransactionLogVO.STATUS_PENDING)) {
			sQuery += " and NOT(transactionStatus = " + "\'"
					+ TransactionLogVO.STATUS_SUCCESS + "\')";
		} else {
			sQuery += "	and transactionStatus = :status";
		}

		Query q = manager.createQuery(sQuery);
		q.setParameter("surveyId", idSurvey);
		if ((status != null) && !status.equals(TransactionLogVO.STATUS_PENDING)) {
			q.setParameter("status", status);
		}

		Long qtImeis = (Long) q.getSingleResult();

		return qtImeis.intValue();
	}

	@Override
	public String getSpecificPropertySetting(String propertySetting)
			throws MSMApplicationException {
		Properties settings = getSettings();
		String result = null;
		if (settings.containsKey(propertySetting)){
			result  = settings.getProperty(propertySetting);
		}else{
			throw new PropertyNotExistExcpetion();
		}
		return result;
	}
	
	public Survey getSurveyObject(String surveyId)
	{
		return manager.find(Survey.class, surveyId);
	}
}
