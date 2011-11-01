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

package br.org.indt.ndg.server.transaction;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import br.org.indt.ndg.common.exception.MSMApplicationException;
import br.org.indt.ndg.common.exception.TransactionLogNotFoundException;
import br.org.indt.ndg.server.authentication.UserManager;
import br.org.indt.ndg.server.client.TransactionLogVO;
import br.org.indt.ndg.server.pojo.Imei;
import br.org.indt.ndg.server.pojo.NdgUser;
import br.org.indt.ndg.server.pojo.Result;
import br.org.indt.ndg.server.pojo.Survey;
import br.org.indt.ndg.server.pojo.Transactionlog;

public @Stateless
class TransactionLogManagerBean implements TransactionLogManager {
	@PersistenceContext(name = "MobisusPersistence")
	private EntityManager manager;

	private static final Logger log = Logger.getLogger("ndg");

	private InitialContext initialContext = null;
	private UserManager userManager = null;

	public TransactionLogManagerBean() {
		try {
			initialContext = new InitialContext();
			userManager = (UserManager) initialContext
					.lookup("ndg-core/UserManagerBean/remote");
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	public void logTransaction(TransactionLogVO vo) {
		Transactionlog log = new Transactionlog();

		log.setTransactionType(vo.getTransactionType());
		log.setTransactionDate(vo.getDtLog());

		if (vo.getImei() != null) {
			log.setImei(manager.find(Imei.class, vo.getImei()));
		}

		if (vo.getResultId() != null) {
			log.setResult(manager.find(Result.class, vo.getResultId()));
		}

		if (vo.getSurveyId() != null) {
			log.setSurvey(manager.find(Survey.class, vo.getSurveyId()));
		}

		log.setTransactionStatus(vo.getStatus());

		if (vo.getUser() != null) {
			NdgUser ndgUser = null;

			try {
				ndgUser = userManager.findNdgUserByName(vo.getUser());
			} catch (MSMApplicationException e) {
				e.printStackTrace();
			}

			log.setUser(ndgUser);
		}

		log.setAddress(vo.getAddress());
		log.setTransmissionMode(vo.getTransmissionMode());

		manager.persist(log);
	}

	public void updateTransactionLog(int id, TransactionLogVO vo)
			throws MSMApplicationException {
		Transactionlog log = manager.find(Transactionlog.class, id);

		if (log == null) {
			throw new TransactionLogNotFoundException();
		}

		log.setTransactionType(vo.getTransactionType());
		log.setTransactionDate(new Timestamp(vo.getDtLog().getTime()));
		log.setImei(manager.find(Imei.class, vo.getImei()));
		log.setResult(manager.find(Result.class, vo.getResultId()));
		log.setSurvey(manager.find(Survey.class, vo.getSurveyId()));
		log.setTransactionStatus(vo.getStatus());
		log.setUser(manager.find(NdgUser.class, vo.getUser()));
		log.setAddress(vo.getAddress());
		log.setTransmissionMode(vo.getTransmissionMode());

		manager.persist(log);
	}

	public Collection<TransactionLogVO> findTransactionLogBySurveyId(String transactionType, String surveyId, String status, String resultId) 
	{
		ArrayList<TransactionLogVO> ret = new ArrayList<TransactionLogVO>();
		
		String queryCommand = "from Transactionlog where transactionType = :transactionType and survey.idSurvey = :surveyId and transactionStatus = :status order by transactionDate";
		
		if ((resultId != null) && (resultId.trim().length() > 0))
		{
			queryCommand = "from Transactionlog where transactionType = :transactionType and survey.idSurvey = :surveyId and result.idResult = :resultId and transactionStatus = :status order by transactionDate";
		}
		
		Query q = manager.createQuery(queryCommand);
		q.setParameter("transactionType", transactionType);
		q.setParameter("surveyId", surveyId);
		
		if ((resultId != null) && (resultId.trim().length() > 0))
		{
			q.setParameter("resultId", resultId);
		}
		
		q.setParameter("status", status);

		ArrayList<Transactionlog> al = (ArrayList<Transactionlog>) q.getResultList();
		Iterator<Transactionlog> it = al.iterator();

		while (it.hasNext()) 
		{
			Transactionlog surveyTransactionLog = (Transactionlog) it.next();

			TransactionLogVO tl = new TransactionLogVO();

			if (surveyTransactionLog.getTransactionDate() != null) 
			{
				tl.setDtLog(new Timestamp(surveyTransactionLog.getTransactionDate().getTime()));
			}

			if (surveyTransactionLog.getImei() != null) 
			{
				tl.setImei(surveyTransactionLog.getImei().getImei());
			}

			if (surveyTransactionLog.getResult() != null) 
			{
				tl.setResultId(surveyTransactionLog.getResult().getIdResult());
			}

			if (surveyTransactionLog.getSurvey() != null) 
			{
				tl.setSurveyId(surveyTransactionLog.getSurvey().getIdSurvey());
			}

			tl.setStatus(surveyTransactionLog.getTransactionStatus());
			tl.setTransactionType(surveyTransactionLog.getTransactionType());

			if (surveyTransactionLog.getUser() != null) 
			{
				tl.setUser(surveyTransactionLog.getUser().getUsername());
			}

			tl.setAddress(surveyTransactionLog.getAddress());
			tl.setTransmissionMode(surveyTransactionLog.getTransmissionMode());

			if (surveyTransactionLog.getImei() != null) 
			{
				tl.setMsisdn(surveyTransactionLog.getImei().getMsisdn());
			}

			ret.add(tl);
		}

		return ret;
	}

	public boolean existTransactionLog(String transactionType, String surveyId,
			String status, String transmissionMode, String imei) {
		boolean result = false;

		Query q = manager
				.createQuery("from Transactionlog where transactionType = :transactionType and survey.idSurvey = :surveyId and transactionStatus = :status and transmissionMode = :transmissionMode and imei.imei = :imei");
		q.setParameter("transactionType", transactionType);
		q.setParameter("surveyId", surveyId);
		q.setParameter("status", status);
		q.setParameter("transmissionMode", transmissionMode);
		q.setParameter("imei", imei);
		ArrayList<Transactionlog> al = (ArrayList<Transactionlog>) q
				.getResultList();
		result = al.size() > 0 ? true : false;

		return result;
	}

	@Override
	public Collection<TransactionLogVO> findTransactionLogByImeiAndType(
			String imei, String transactionType) {
		ArrayList<Transactionlog> ret = new ArrayList<Transactionlog>();
		Query q = manager.createNamedQuery("transactionlog.findByImeiType");
		ret = (ArrayList<Transactionlog>) q.getResultList();
		return arrayPojoToVO(ret);
	}

	private ArrayList<TransactionLogVO> arrayPojoToVO(ArrayList<Transactionlog> arrayListPojo) {
		Iterator<Transactionlog> it = arrayListPojo.iterator();
		ArrayList<TransactionLogVO> ret = new ArrayList<TransactionLogVO>();
		while (it.hasNext()) {
			Transactionlog surveyTransactionLog = (Transactionlog) it.next();

			TransactionLogVO tl = new TransactionLogVO();

			if (surveyTransactionLog.getTransactionDate() != null) {
				tl.setDtLog(new Timestamp(surveyTransactionLog
						.getTransactionDate().getTime()));
			}

			if (surveyTransactionLog.getImei() != null) {
				tl.setImei(surveyTransactionLog.getImei().getImei());
			}

			if (surveyTransactionLog.getResult() != null) {
				tl.setResultId(surveyTransactionLog.getResult().getIdResult());
			}

			if (surveyTransactionLog.getSurvey() != null) {
				tl.setSurveyId(surveyTransactionLog.getSurvey().getIdSurvey());
			}

			tl.setStatus(surveyTransactionLog.getTransactionStatus());
			tl.setTransactionType(surveyTransactionLog.getTransactionType());

			if (surveyTransactionLog.getUser() != null) {
				tl.setUser(surveyTransactionLog.getUser().getUsername());
			}

			tl.setAddress(surveyTransactionLog.getAddress());
			tl.setTransmissionMode(surveyTransactionLog.getTransmissionMode());

			if (surveyTransactionLog.getImei() != null) {
				tl.setMsisdn(surveyTransactionLog.getImei().getMsisdn());
			}
			ret.add(tl);
		}
		return ret;
	}
}