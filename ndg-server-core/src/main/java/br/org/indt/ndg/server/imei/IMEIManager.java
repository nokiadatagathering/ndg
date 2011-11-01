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

package br.org.indt.ndg.server.imei;

import javax.ejb.Remote;

import br.org.indt.ndg.common.exception.MSMApplicationException;
import br.org.indt.ndg.server.client.ImeiVO;
import br.org.indt.ndg.server.controller.QueryInputOutputVO;

@Remote
public interface IMEIManager 
{
	public boolean hasImei(ImeiVO imeiVO) throws MSMApplicationException;
	public boolean hasMsisdn(ImeiVO imeiVO) throws MSMApplicationException;
	public void createIMEI(ImeiVO imeiVO) throws MSMApplicationException;
	public void deleteIMEI(String _imei) throws MSMApplicationException;
	public ImeiVO findImeiByMsisdn(String msisdn) throws MSMApplicationException;
	public ImeiVO getIMEI(String _imei) throws MSMApplicationException;
	public void updateIMEI(ImeiVO imeiVO) throws MSMApplicationException;
	public QueryInputOutputVO listAllImeis(String userAdmin, QueryInputOutputVO queryIOVO,
		boolean withFakeImeis) throws MSMApplicationException;
	public QueryInputOutputVO findImeiByUser(String username, QueryInputOutputVO queryIOVO,
		boolean withFakeImeis) throws MSMApplicationException;
	public void registerIMEI(String msisdn, String newImei) throws MSMApplicationException;
}