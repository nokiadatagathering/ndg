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

package br.org.indt.ndg.server.result;

import java.io.IOException;
import java.util.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import br.org.indt.ndg.common.ResultParser;
import br.org.indt.ndg.common.ResultXml;
import br.org.indt.ndg.common.SystemUtils;
import br.org.indt.ndg.common.exception.MSMApplicationException;
import br.org.indt.ndg.common.exception.MSMSystemException;
import br.org.indt.ndg.common.exception.ResultNotParsedException;
import br.org.indt.ndg.server.client.MSMBusinessDelegate;
import br.org.indt.ndg.server.client.ResultVO;
import br.org.indt.ndg.server.client.TransactionLogVO;
import br.org.indt.ndg.server.controller.QueryInputOutputVO;
import br.org.indt.ndg.server.pojo.Imei;
import br.org.indt.ndg.server.pojo.NdgUser;
import br.org.indt.ndg.server.pojo.Result;
import br.org.indt.ndg.server.pojo.Survey;
import br.org.indt.ndg.server.util.SqlUtil;


@Stateless
public class ResultHandlerBean implements ResultHandler {
	
	@PersistenceContext(name = "MobisusPersistence")
	private EntityManager manager;
	private static Log log = LogFactory.getLog(ResultHandlerBean.class);

	@Override
	public void postResult(StringBuffer resultBuffered, TransactionLogVO postResultTransaction)
			throws MSMApplicationException, MSMSystemException {
		
		ResultXml resultBean = null;
		try {
			ResultParser parser = new ResultParser();
			resultBean = parser.parseResult(resultBuffered, "UTF-8");
		} catch (Exception e) {
			throw new ResultNotParsedException();
		}
		
		if (resultBean != null){
			Imei imei = manager.find(Imei.class, resultBean.getImei());
			persistResult(imei.getUser(), resultBean, resultBuffered);
			if (setResultReceived(imei.getUser(), resultBean, postResultTransaction))	{
				log.info("Result " + resultBean.getResultId() + " successfully logged!");
			}			
		}
	}

	/**
	 * Call log operation to register the result received by server.
	 */
	private boolean setResultReceived(NdgUser user, ResultXml result,
			TransactionLogVO postResultTransaction) {
		boolean valid = false;
		MSMBusinessDelegate msmBD = new MSMBusinessDelegate();

		postResultTransaction.setDtLog(new Timestamp(System.currentTimeMillis()));
		postResultTransaction.setStatus(TransactionLogVO.STATUS_SUCCESS);
		postResultTransaction.setTransactionType(TransactionLogVO.TYPE_RECEIVE_RESULT);
		postResultTransaction.setSurveyId(result.getSurveyId());
		postResultTransaction.setResultId(result.getResultId());
		postResultTransaction.setImei(result.getImei());
		postResultTransaction.setUser(user.getUsername());
		
		try {
			log.info("Logging result received via http id:"	+ result.getResultId());
			msmBD.logTransaction(postResultTransaction);
			valid = true;
		} catch (MSMApplicationException e) {
			log.error(e.getErrorCode());
			log.error(e);
		}

		return valid;
	}

	private void persistResult(NdgUser user, ResultXml resultXml, StringBuffer buffer) {
		Result resultToPersist = new Result();
		resultToPersist.setIdResult(resultXml.getResultId());

		Result result = manager.find(Result.class, resultToPersist.getIdResult());
		resultToPersist = (result!=null) ? result : resultToPersist;

		Imei imei = new Imei();
		imei.setImei(resultXml.getImei());
		Survey survey = new Survey();
		survey.setIdSurvey(resultXml.getSurveyId());

		resultToPersist.setResultXml(buffer.toString());
		resultToPersist.setSurvey(survey);
		resultToPersist.setImei(imei);
		resultToPersist.setLatitude(resultXml.getLatitude());
		resultToPersist.setLongitude(resultXml.getLongitude());
		resultToPersist.setTitle(resultXml.getTitle());
		resultToPersist.setDateSaved(new Date(Long.parseLong(resultXml.getTime())));

		manager.persist(resultToPersist);
	}

	@Override
	public QueryInputOutputVO getResultListDB(String surveyId, QueryInputOutputVO queryIOVO) throws MSMApplicationException, MSMSystemException 
	{
		if (queryIOVO == null)
		{
			queryIOVO = new QueryInputOutputVO();
		}
		
		MSMBusinessDelegate bd = new MSMBusinessDelegate();
			
		String sqlCommand = "SELECT U FROM Result U WHERE survey.idSurvey = " + surveyId;
		
		if ((queryIOVO.getFilterText() != null) && (queryIOVO.getFilterFields() != null))
		{
			sqlCommand += SqlUtil.getFilterCondition(queryIOVO.getFilterText(), queryIOVO.getFilterFields());
		}
			
		if ((queryIOVO.getSortField() != null) && (queryIOVO.getIsDescending() != null))
		{
			if (!queryIOVO.getSortField().equals("date"))
			{
				sqlCommand += SqlUtil.getSortCondition(queryIOVO.getSortField(), queryIOVO.getIsDescending());
			}
		}
			
		Query q = manager.createQuery(sqlCommand);			
		queryIOVO.setRecordCount(q.getResultList().size());
			
		if ((queryIOVO.getPageNumber() != null) && (queryIOVO.getRecordsPerPage() != null))
		{
			q.setFirstResult((queryIOVO.getPageNumber() - 1) * queryIOVO.getRecordsPerPage());
			q.setMaxResults(queryIOVO.getRecordsPerPage());
		}

		ArrayList<Object> resultList = new ArrayList<Object>();
		ArrayList<Result> resultsListDB = (ArrayList<Result>) q.getResultList();
		ArrayList<ResultVO> notNullResults = new ArrayList<ResultVO>();
		
		for (Result result : resultsListDB)
		{
			Collection<TransactionLogVO> logs = bd.getResultReceived(surveyId, result.getIdResult());
			HashMap<String, TransactionLogVO> map = new HashMap<String, TransactionLogVO>();

			for (TransactionLogVO rlog : logs)
			{
				map.put(rlog.getResultId(), rlog);
			}
			
			ResultVO bean = new ResultVO();
				
			try 
			{
				Result currentResult = manager.find(Result.class, result.getIdResult());

				bean.setIdResult(currentResult.getIdResult());
				bean.setSurveyId(currentResult.getSurvey().getIdSurvey());
				bean.setImei(currentResult.getImei().getImei());
				
				if ((currentResult.getTitle() == null) || (currentResult.getLatitude() == null) || (currentResult.getLongitude() == null))
				{
					ResultParser parser = new ResultParser();
					ResultXml resultXML = parser.parseResult(new StringBuffer(result.getResultXml()), "UTF-8");
					
					bean.setTitle(resultXML.getTitle());
					bean.setLat(resultXML.getLatitude());
					bean.setLon(resultXML.getLongitude());
					
					currentResult.setTitle(resultXML.getTitle());
					currentResult.setLatitude(resultXML.getLatitude());
					currentResult.setLongitude(resultXML.getLongitude());
					
					manager.merge(currentResult);
				}
				else
				{
					bean.setTitle(currentResult.getTitle());
					bean.setLat(currentResult.getLatitude());
					bean.setLon(currentResult.getLongitude());
				}

				if (!logs.isEmpty()) 
				{
					TransactionLogVO t = map.get(result.getIdResult());
						
					if (t != null) 
					{
						bean.setDate(SystemUtils.toDate(t.getDtLog()));
						bean.setUser(t.getUser());
					}
				}
					
				// order results by title - ascending
				if (bean.getTitle() == null)
				{
					resultList.add(bean);
				}
				else
				{
					if (notNullResults.size() == 0)
					{
						notNullResults.add(bean);
					}
					else
					{			
						if (bean.getTitle().compareToIgnoreCase(notNullResults.get(notNullResults.size() - 1).getTitle()) > 0)
						{
							notNullResults.add(bean);
						}
						else
						{
							notNullResults.add(notNullResults.size() - 1, bean);
						}
					}	
				}
			} 
			catch (Exception e) 
			{
				throw new ResultNotParsedException();
			} 
		}
		
		// results without title come first
		resultList.addAll(notNullResults);
		
		queryIOVO.setQueryResult(resultList);
		
		return queryIOVO;
	}
	
	@Override
	public ResultXml getResultDB(String idResult) throws MSMApplicationException, MSMSystemException {		
		Query q = manager.createNamedQuery("result.findByIdResult");
		q.setParameter("idResult", idResult);
		Result result = null;
		result = (Result) q.getSingleResult();
		
		ResultParser parser = new ResultParser();
		ResultXml resultXML = null;
			
		try{
			resultXML = parser.parseResult(new StringBuffer(result.getResultXml()), "UTF-8");
		} catch (SAXException e) {
			throw new ResultNotParsedException();
		} catch (ParserConfigurationException e){
			throw new ResultNotParsedException();
		} catch (IOException e) {
			throw new ResultNotParsedException();
		}
	
		return resultXML;
	}
	
}