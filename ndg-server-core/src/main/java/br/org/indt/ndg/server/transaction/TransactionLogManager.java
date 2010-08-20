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

package br.org.indt.ndg.server.transaction;

import java.util.Collection;

import javax.ejb.Remote;

import br.org.indt.ndg.server.client.TransactionLogVO;

@Remote
public interface TransactionLogManager 
{
	public void logTransaction(TransactionLogVO vo);
	
	public Collection<TransactionLogVO> findTransactionLogBySurveyId(String transactionType, String surveyId, String status, String resultId);
	
	public Collection<TransactionLogVO> findTransactionLogByImeiAndType(String imei, String transactionType);
	
	public boolean existTransactionLog(String transactionType, String surveyId, String status, String transmissionMode, String imei);
}
