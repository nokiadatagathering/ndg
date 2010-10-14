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

package br.org.indt.ndg.server.imei;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import br.org.indt.ndg.common.exception.ImeiAlreadyExistException;
import br.org.indt.ndg.common.exception.ImeiNotFoundException;
import br.org.indt.ndg.common.exception.ImeiNotRecordedException;
import br.org.indt.ndg.common.exception.MSMApplicationException;
import br.org.indt.ndg.common.exception.MsisdnAlreadyExistException;
import br.org.indt.ndg.common.exception.MsisdnAlreadyRegisteredException;
import br.org.indt.ndg.common.exception.MsisdnNotFoundException;
import br.org.indt.ndg.server.authentication.UserManager;
import br.org.indt.ndg.server.client.DeviceVO;
import br.org.indt.ndg.server.client.ImeiVO;
import br.org.indt.ndg.server.controller.QueryInputOutputVO;
import br.org.indt.ndg.server.pojo.Imei;
import br.org.indt.ndg.server.pojo.NdgDevice;
import br.org.indt.ndg.server.pojo.NdgUser;
import br.org.indt.ndg.server.util.SqlUtil;

public @Stateless class IMEIManagerBean implements IMEIManager 
{
	@PersistenceContext(name="MobisusPersistence")
    private EntityManager manager;

	public boolean hasImei(ImeiVO imeiVO) throws MSMApplicationException 
	{
		Imei imei = manager.find(Imei.class, imeiVO.getImei());
		
		return imei != null;
	}
	
	public boolean hasMsisdn(ImeiVO imeiVO) throws MSMApplicationException 
	{
		Query q = manager.createQuery("from Imei where msisdn = :msisdn");
		q.setParameter("msisdn", imeiVO.getMsisdn());
		List result = q.getResultList();
		
		return (result == null || result.isEmpty()) ? false : true;
	}
	
	public void createIMEI(ImeiVO imeiVO) throws MSMApplicationException 
	{
		Imei imei = manager.find(Imei.class, imeiVO.getImei());
		
		if (imei == null) 
		{
			ImeiVO foundMsisdn = null;
			
			try 
			{
				foundMsisdn = this.findImeiByMsisdn(imeiVO.getMsisdn());
			}
			catch (MSMApplicationException e)	
			{
				// empty
			}
			
			if (foundMsisdn == null) 
			{
				imei = new Imei();
				imei.setImei(imeiVO.getImei());
				imei.setMsisdn(imeiVO.getMsisdn());
				imei.setRealImei(imeiVO.getRealImei());
				setUserAndDevice(imei, imeiVO);
				
				try	
				{
					manager.persist(imei);
				}
				catch (Exception e) 
				{
					e.printStackTrace();
					
					throw new ImeiNotRecordedException();
				}
			}
			else 
			{
				throw new MsisdnAlreadyExistException();
			}
		}
		else 
		{
			throw new ImeiAlreadyExistException();
		}
	}
	

	public void registerIMEI(String msisdn, String newImei) throws MSMApplicationException {
		ImeiVO foundMsisdn = null;
		System.out.println("[IMEIManagerBean.registerIMEI] Msisdn: " + msisdn +
				"; Imei: " + newImei); 
		try{
			foundMsisdn = this.findImeiByMsisdn(msisdn);
			System.out.println("[IMEIManagerBean.registerIMEI] Msisdn " + msisdn + " was found");
		} catch (Exception e){}
		if (foundMsisdn == null){
			System.out.println("[IMEIManagerBean.registerIMEI] Msisdn " + msisdn + " not found");
			throw new MsisdnNotFoundException();
		}
		
		if (foundMsisdn.getRealImei() == 'Y'){
			System.out.println("[IMEIManagerBean.registerIMEI] Msisdn " + msisdn + " already registered");
			throw new MsisdnAlreadyRegisteredException();
		}
		
		Imei imei = manager.find(Imei.class, newImei);
		if (imei != null){
			System.out.println("[IMEIManagerBean.registerIMEI] IMEI " + newImei + " already registered");
			throw new ImeiAlreadyExistException();
		}		
		
		imei = manager.find(Imei.class, foundMsisdn.getImei());
		if (imei == null){
			System.out.println("[IMEIManagerBean.registerIMEI] Old IMEI " + imei + " not found");
			throw new ImeiNotFoundException();
		}
		
		Imei imeiToPersist = new Imei();
		imeiToPersist.setDevice(imei.getDevice());
		imeiToPersist.setImei(newImei);
		imeiToPersist.setMsisdn(imei.getMsisdn());
		imeiToPersist.setQtdeResults(imei.getQtdeResults());
		imeiToPersist.setRealImei('Y');
		imeiToPersist.setUser(imei.getUser());
		System.out.println("[IMEIManagerBean.registerIMEI] Persisting Msisdn " + msisdn +
			"; Imei: " + newImei);
		
		manager.remove(imei);
		manager.flush();
		manager.persist(imeiToPersist);
		manager.flush();
		
		System.out.println("[IMEIManagerBean.registerIMEI] Msisdn " + msisdn + "; Imei: " +
			newImei + " were persisted!");
	}
	
	
	public void updateIMEI(ImeiVO imeiVO) throws MSMApplicationException {
		Imei imei = manager.find(Imei.class, imeiVO.getImei());
		if (imei != null) {
			ImeiVO foundMsisdn = null;
			try {
				foundMsisdn = this.findImeiByMsisdn(imeiVO.getMsisdn());
			} catch (MSMApplicationException e) {
			}
			
			if (foundMsisdn == null || foundMsisdn.getImei().equals(imeiVO.getImei())){
				imei.setImei(imeiVO.getImei());
				imei.setMsisdn(imeiVO.getMsisdn());
				imei.setRealImei(imeiVO.getRealImei());
				this.setUserAndDevice(imei, imeiVO);

				try	{
					manager.persist(imei);
				} catch (Exception e) {
					e.printStackTrace();
					throw new ImeiNotRecordedException();
				}
			} else{
				throw new MsisdnAlreadyExistException();
			}
		} else{
			throw new ImeiNotFoundException();
		}
	}
	
	private UserManager setUserAndDevice(Imei imei, ImeiVO imeiVO){
		NdgUser user = null;
		NdgDevice device = null;
		UserManager userManager = null;
		DeviceManager deviceManager = null;
		try {
			InitialContext initialContext = new InitialContext();
			userManager = (UserManager) initialContext.lookup("ndg-core/UserManagerBean/remote");
			if (userManager != null){
				user = userManager.findNdgUserByName(imeiVO.getUserName());
			}
			deviceManager = (DeviceManager) initialContext.lookup("ndg-core/DeviceManagerBean/remote");
			if (deviceManager != null){
				device = deviceManager.findNdgDeviceByModel(imeiVO.getDevice().getDeviceModel());	
			}
		} catch (MSMApplicationException e) {
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		
		imei.setUser(user);
		imei.setDevice(device);
		return userManager;
	}
	
	public ImeiVO getIMEI(String _imei) throws MSMApplicationException{
		Imei imei = manager.find(Imei.class, _imei);
		ImeiVO vo = new ImeiVO();
		if (imei != null) {
			vo.setImei(imei.getImei());
			vo.setMsisdn(imei.getMsisdn());
			vo.setUserName(imei.getUser().getUsername());
			NdgDevice device = imei.getDevice();
			DeviceVO devVO = new DeviceVO();
			devVO.setIdDevice(device.getIdDevice());
			devVO.setDeviceModel(device.getDeviceModel());
			vo.setDevice(devVO);
			vo.setRealImei(imei.getRealImei());
		} else {
			throw new ImeiNotFoundException();
		}		
		return vo;
	}

	public void deleteIMEI(String _imei) throws MSMApplicationException {
		Imei imei = manager.find(Imei.class, _imei);
		if (imei != null) {
   		   manager.remove(imei);
		} else {
			throw new ImeiNotFoundException();
		}
	}
	
	public boolean existIMEI(String _imei) throws MSMApplicationException {
		Imei imei = manager.find(Imei.class, _imei);
		if (imei == null) {
			throw new ImeiNotFoundException();
		} 

		return true;
	}
	
	public boolean existMsisdn(String _msisdn) throws MSMApplicationException {
		Imei imei = null;
		Query q = manager.createQuery("from Imei where msisdn = :msisdn");
		q.setParameter("msisdn", _msisdn);
		try {
			imei = (Imei)q.getSingleResult();
			return true;
		} catch (NoResultException e) {
			throw new MsisdnNotFoundException();
		}
	}
	
	public ImeiVO findImeiByMsisdn(String msisdn) throws MSMApplicationException {
		Imei imei = null;
		Query q = manager.createQuery("from Imei where msisdn = :msisdn");
		q.setParameter("msisdn", msisdn);
		try {
			imei = (Imei)q.getSingleResult();
		} catch (NoResultException e) {
			throw new ImeiNotFoundException();
		}
		
		if (imei == null) {
			throw new ImeiNotFoundException();
		}
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
		System.out.println(">>> findImeiByMsisdn");
		return vo;
	}
	
	public QueryInputOutputVO findImeiByUser(String username, QueryInputOutputVO queryIOVO,
			boolean withFakeImeis) throws MSMApplicationException {
		
		if (queryIOVO == null){
			queryIOVO = new QueryInputOutputVO();
		}
		String sqlCommand = "SELECT U FROM Imei U WHERE user.username = '" + username + "'";
		return this.getImeis(sqlCommand, queryIOVO, withFakeImeis);	
	}
	
	public QueryInputOutputVO listAllImeis(String userAdmin, QueryInputOutputVO queryIOVO,
			boolean withFakeImeis) throws MSMApplicationException {
		if (queryIOVO == null){
			queryIOVO = new QueryInputOutputVO();
		}
		String sqlCommand = "SELECT I FROM Imei I WHERE user.userAdmin = '" + userAdmin + "'";
		return this.getImeis(sqlCommand, queryIOVO, withFakeImeis);
	}	
	
	
	private QueryInputOutputVO getImeis(String sqlCommand, QueryInputOutputVO queryIOVO,
			boolean withFakeImeis) throws MSMApplicationException {

		if (!withFakeImeis){
			sqlCommand += " AND realImei = 'Y'";
		}

		if ((queryIOVO.getFilterText() != null) && (queryIOVO.getFilterFields() != null)){
			sqlCommand += SqlUtil.getFilterCondition(queryIOVO.getFilterText(), queryIOVO.getFilterFields());
		}

		//ArrayList<String> sortFields = new ArrayList<String>();
		//ArrayList<Boolean> sortIsDescending = new ArrayList<Boolean>();
		//sortFields.add("realImei");
		//sortIsDescending.add(Boolean.TRUE);
		if ((queryIOVO.getSortField() != null) && (queryIOVO.getIsDescending() != null)){
			//sortFields.add(queryIOVO.getSortField());
			//sortIsDescending.add(queryIOVO.getIsDescending());
			sqlCommand += SqlUtil.getSortCondition(queryIOVO.getSortField(), queryIOVO.getIsDescending());
		}
		//sqlCommand += SqlUtil.getSortCondition(sortFields, sortIsDescending);
		
		Query q = manager.createQuery(sqlCommand);
		queryIOVO.setRecordCount(q.getResultList().size());
					
		if ((queryIOVO.getPageNumber() != null) && (queryIOVO.getRecordsPerPage() != null)){
			q.setFirstResult((queryIOVO.getPageNumber() - 1) * queryIOVO.getRecordsPerPage());
			q.setMaxResults(queryIOVO.getRecordsPerPage());
		}

		ArrayList<Object> ret = new ArrayList<Object>();
		ArrayList<Imei> al = (ArrayList<Imei>)q.getResultList();
		Iterator<Imei> it = al.iterator();
		while (it.hasNext()){
			Imei imei = (Imei)it.next();
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
			ret.add(vo);
		}
		
		queryIOVO.setQueryResult(ret);
		return queryIOVO;
	}	
	
	
}