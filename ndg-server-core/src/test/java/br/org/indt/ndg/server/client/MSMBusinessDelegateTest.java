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


public class MSMBusinessDelegateTest {
	
//	/**
//	 * Validate login based in an existent and valid instance.
//	 * @assertTrue if user was authenticated.
//	 */
//	
//	@SuppressWarnings("static-access")
//	@Test
//	public void validateLoginExistentInstance() throws MSMApplicationException{
//		try {
//			MSMBusinessDelegate msmBD = new MSMBusinessDelegate() ;
//			UserVO vo;
//			vo = msmBD.validateLogin("admin", "ndg");
//			assertTrue(vo.getRetCode().equalsIgnoreCase(vo.AUTHENTICATED)) ;
//		} catch (MSMApplicationException e) {
//			System.out.println(e);
//		}
//	}
//	
//	/**
//	 * Validate login based in an incorrect password.
//	 * @assertTrue if user wasn't authenticated.
//	 */
//	
//	@SuppressWarnings("static-access")
//	@Test
//	public void validateLoginInvalidPassword() throws MSMApplicationException{
//		try {
//			MSMBusinessDelegate msmBD = new MSMBusinessDelegate() ;
//			UserVO vo;
//			vo = msmBD.validateLogin("admin", "xxx");
//			assertTrue(vo.getRetCode().equalsIgnoreCase(vo.INVALID_PASSWORD)) ;
//		} catch (MSMApplicationException e) {
//			System.out.println(e);
//		}
//	}
//	
//	/**
//	 * Validate login based in an incorrect user.
//	 * @assertTrue if user wasn't authenticated.
//	 */
//	
//	@SuppressWarnings("static-access")
//	@Test
//	public void validateLoginInvalidUser() throws MSMApplicationException{
//		try {
//			MSMBusinessDelegate msmBD = new MSMBusinessDelegate() ;
//			UserVO vo;
//			vo = msmBD.validateLogin("xxx", "xxx");
//			assertTrue(vo.getRetCode().equalsIgnoreCase(vo.INVALID_USERNAME)) ;
//		} catch (MSMApplicationException e) {
//			System.out.println(e);
//		}
//	}
//	
//	/**
//	 * Validate login based in a null instance.
//	 * @assertTrue if user wasn't authenticated.
//	 */
//	
//	@SuppressWarnings("static-access")
//	@Test
//	public void validateLoginNull() throws MSMApplicationException{
//		try {
//			MSMBusinessDelegate msmBD = new MSMBusinessDelegate() ;
//			UserVO vo;
//			vo = msmBD.validateLogin(null, null);
//			assertTrue(vo.getRetCode().equalsIgnoreCase(vo.INVALID_USERNAME)) ;
//		} catch (MSMApplicationException e) {
//			System.out.println(e);
//		} 
//	}
//	
//	/**
//	 * Create user based in an existent instance
//	 * @throws if has exception
//	 */
//	
//	@Test
//	public void createAnExistentUser() throws MSMApplicationException, MSMSystemException {
//		try {
//			MSMBusinessDelegate msmBD = new MSMBusinessDelegate();
//			UserVO vo = new UserVO();
//			UserInstance inst = new UserInstance();
//			
//			UserData userLogged = inst.getLoggedUser();
//			
//			UserData user = inst.getExistentUserBean(); 
//			
//			RoleVO role = new RoleVO();
//			role.setName(user.getPermission());
//			
//			vo.setFirstName(user.getFirstName());
//			vo.setLastName(user.getLastName());
//			vo.setEmail(user.getEmail());
//			vo.setRole(role);
//			vo.setUsername(user.getUserName());
//			vo.setCountryCode(user.getCountryCode());
//			vo.setAreaCode(user.getAreaCode());
//			vo.setPhoneNumber(user.getPhoneNumber());
//			vo.setPassword(user.getPassword());
//			vo.setFirstTimeUse('N');
//			vo.setEmailPreferences('N');
//			vo.setNdgNewsLetter('N');
//								
//			msmBD.createUser(userLogged.getUserAdmin(),vo);
//			System.out.println("Creating user: " + user.getUserName());
//			
//		} catch (MSMApplicationException e) {
//			System.out.println(e);
//		} catch (MSMSystemException e) {
//			System.out.println(e);
//		} 
//	}
//	
//	/**
//	 * Create user.
//	 * @throws if has exception
//	 */
//	
//	@Test
//	public void createUser() throws MSMApplicationException, MSMSystemException {
//		try {
//			MSMBusinessDelegate msmBD = new MSMBusinessDelegate();
//			UserVO vo = new UserVO();
//			UserInstance inst = new UserInstance();
//			
//			UserData userLogged = inst.getLoggedUser();
//			
//			UserData user = inst.getUserBean(); 
//			
//			RoleVO role = new RoleVO();
//			role.setName(user.getPermission());
//			
//			vo.setFirstName(user.getFirstName());
//			vo.setLastName(user.getLastName());
//			vo.setEmail(user.getEmail());
//			vo.setRole(role);
//			vo.setUsername(user.getUserName());
//			vo.setCountryCode(user.getCountryCode());
//			vo.setAreaCode(user.getAreaCode());
//			vo.setPhoneNumber(user.getPhoneNumber());
//			vo.setPassword(user.getPassword());
//			vo.setFirstTimeUse('N');
//			vo.setEmailPreferences('N');
//			vo.setNdgNewsLetter('N');
//								
//			msmBD.createUser(userLogged.getUserAdmin(),vo);
//			System.out.println("Creating user: " + user.getUserName());
//			
//		} catch (MSMApplicationException e) {
//			System.out.println(e);
//		} catch (MSMSystemException e) {
//			System.out.println(e);
//		} 
//	} 
//	
//	/**
//	 * Update user.
//	 * @throws if has exception
//	 */
//	
//	@Test
//	public void updateUser() throws MSMSystemException,	MSMApplicationException  {
//		try {
//			MSMBusinessDelegate msmBD = new MSMBusinessDelegate();
//			UserVO vo = new UserVO();
//			UserInstance inst = new UserInstance();
//			
//			UserData user = inst.getUpdateUserBean(); 
//			
//			RoleVO role = new RoleVO();
//			role.setName(user.getPermission());
//			
//			vo.setFirstName(user.getFirstName());
//			vo.setLastName(user.getLastName());
//			vo.setEmail(user.getEmail());
//			vo.setRole(role);
//			vo.setUsername(user.getUserName());
//			vo.setCountryCode(user.getCountryCode());
//			vo.setAreaCode(user.getAreaCode());
//			vo.setPhoneNumber(user.getPhoneNumber());
//			vo.setPassword(user.getPassword());
//			vo.setFirstTimeUse('N');
//			vo.setEmailPreferences('N');
//			vo.setNdgNewsLetter('N');
//								
//			msmBD.updateUser(vo);
//			System.out.println("Update user: " + user.getFirstName());
//			
//		} catch (MSMSystemException e) {
//			System.out.println(e);
//		} catch (MSMApplicationException e) {
//			System.out.println(e);
//		}
//	} 	
//	
//	/**
//	 * Delete user.
//	 * @throws if has exception
//	 */
//	
//	@Test
//	public void deleteUser() throws MSMApplicationException {
//		try {
//			MSMBusinessDelegate msmBD = new MSMBusinessDelegate();
//			UserInstance inst = new UserInstance();
//			UserData user = inst.getUserBean();
//			msmBD.deleteUser(user.getUserName());
//			System.out.println("Deleting user: " + user.getUserName());
//			
//		} catch (MSMApplicationException e) {
//			System.out.println(e);
//		}
//				
//	}
//	
//	/**
//	 * Delete user that has relation with another entity.
//	 * @throws if has exception
//	 */
//	
//	@Test
//	public void deleteUserHasRelation() throws MSMApplicationException {
//		try {
//			MSMBusinessDelegate msmBD = new MSMBusinessDelegate();
//			UserInstance inst = new UserInstance();
//			UserData user = inst.getExistentUserBean();
//			msmBD.deleteUser(user.getUserName());
//			System.out.println("Deleting user: " + user.getUserName());
//			
//		} catch (MSMApplicationException e) {
//			System.out.println(e);
//		}
//				
//	}
//	
//	/**
//	 * List users.
//	 * @throws if has exception
//	 */
//	
//	@Test
//	public void listUsers() throws MSMApplicationException {
//		try {
//			MSMBusinessDelegate msmBD = new MSMBusinessDelegate();
//			Collection<UserVO> c;
//			
//			UserInstance inst = new UserInstance();
//			
//			UserData userLogged = inst.getLoggedUser();
//			c = msmBD.listAllUsers(userLogged.getUserName());
//			ArrayList<UserData> list = new ArrayList<UserData>();
//			for (UserVO user : c) {
//				System.out.println("Listing user: " + user.getUsername());
//				UserData bean = new UserData();
//				bean.setUserName(user.getUsername());
//				bean.setFirstName(user.getFirstName());
//				bean.setLastName(user.getLastName());
//				bean.setEmail(user.getEmail());
//				bean.setPermission(user.getRole().getName());
//				bean.setCountryCode(user.getCountryCode());
//				bean.setAreaCode(user.getAreaCode());
//				bean.setPhoneNumber(user.getPhoneNumber());
//				bean.setCompanyName(user.getCompany().getCompanyName());
//				bean.setUserAdmin(user.getUserAdmin());
//				bean.setPassword(user.getPassword());
//				list.add(bean);
//			}
//			
//		} catch (MSMApplicationException e) {
//			System.out.println(e);
//		}
//	}
//	
//	/**
//	 * Create Imei.
//	 * @throws if has exception
//	 */
//	
//	@Test
//	public void createImei() throws MSMApplicationException {
//		try {
//			MSMBusinessDelegate msmBD = new MSMBusinessDelegate();
//			ImeiVO vo = new ImeiVO();
//			NdgDeviceVO device = new NdgDeviceVO();
//			
//			ImeiInstance inst = new ImeiInstance();
//			ImeiData imei = inst.getImeiBean();
//			
//			vo.setImei(imei.getImei());
//			vo.setMsisdn(imei.getMsisdn());
//			vo.setUserName(imei.getName());
//			device.setId(imei.getDeviceData().getId());
//			device.setDeviceModel(imei.getDeviceData().getDevice());
//			vo.setDevice(device);
//			msmBD.createIMEI(vo);
//			System.out.println("Creating IMEI: " + imei.getImei());
//		} catch (MSMApplicationException e) {
//			System.out.println(e);
//		}
//	}
//	
//	/**
//	 * Delete imei.
//	 * @throws if has exception
//	 */
//	
//	@Test
//	public void deleteImei() throws MSMApplicationException {
//		try{
//			MSMBusinessDelegate msmBD = new MSMBusinessDelegate();
//			ImeiInstance inst = new ImeiInstance();
//			ImeiData imei = inst.getImeiBean();
//			msmBD.deleteIMEI(imei.getImei());
//			System.out.println("Deleting IMEI: " + imei.getImei());
//		} catch (MSMApplicationException e) {
//			System.out.println(e);
//		}
//	}
//	
//	/**
//	 * List imeis.
//	 * @throws if has exception
//	 */
//	
//	@Test
//	public void listImei() throws MSMApplicationException {
//		try {
//			MSMBusinessDelegate msmBD = new MSMBusinessDelegate();
//			
//			UserInstance inst = new UserInstance();
//			UserData userLogged = inst.getLoggedUser();
//			
//			Collection<ImeiVO> c = msmBD.listAllImeis(userLogged.getUserName());
//			ArrayList<ImeiData> list = new ArrayList<ImeiData>();
//			for (ImeiVO imei : c) {
//				System.out.println("Listing IMEI: " + imei.getImei());
//				ImeiData bean = new ImeiData(imei.getImei(), imei.getMsisdn(), imei.getUserName(), imei.getDevice().getDeviceModel());
//				list.add(bean);
//			}
//		} catch (MSMApplicationException e) {
//			System.out.println(e);
//		} 
//	}
//	
//	/**
//	 * Create device.
//	 * @throws if has exception
//	 */
//	
//	@Test
//	public void createDevice() throws MSMApplicationException {
//		try {
//			MSMBusinessDelegate msmBD = new MSMBusinessDelegate();
//			NdgDeviceVO deviceVO = new NdgDeviceVO();
//			
//			DeviceInstance inst = new DeviceInstance();
//			DeviceData device = inst.getDeviceBean();
//			
//			deviceVO.setDeviceModel(device.getDevice());
//			deviceVO.setId(device.getId());
//			msmBD.createDevice(deviceVO);
//			System.out.println("Creating Device: " + device.getDevice());
//		} catch (MSMApplicationException e) {
//			System.out.println(e);
//		}
//	}
//	
//	/**
//	 * Delete device.
//	 * @throws if has exception
//	 */
//	
//	@Test
//	public void deleteDevice() throws MSMApplicationException {
//		try{
//			MSMBusinessDelegate msmBD = new MSMBusinessDelegate();
//			DeviceInstance inst = new DeviceInstance();
//			DeviceData device = inst.getDeviceBean();
//			msmBD.deleteDevice(device.getDevice());
//			System.out.println("Deleting Device: " + device.getDevice());
//		} catch (MSMApplicationException e) {
//			System.out.println(e);
//		}
//	}
//	
//	/**
//	 * List devices.
//	 * @throws if has exception
//	 */
//	
//	@Test
//	public void listDevice() throws MSMApplicationException {
//		try {
//			MSMBusinessDelegate msmBD = new MSMBusinessDelegate();
//			
//			Collection<NdgDeviceVO> c = msmBD.listAllDevices();
//			ArrayList<DeviceData> list = new ArrayList<DeviceData>();
//			for (NdgDeviceVO device : c) {
//				System.out.println("Listing Device: " + device.getDeviceModel());
//				DeviceData bean = new DeviceData(device.getIdDevice(), device.getDeviceModel());
//				list.add(bean);
//			}
//		} catch (MSMApplicationException e) {
//			System.out.println(e);
//		} 
//	}
//	
//	/**
//	 * List surveys from data base.
//	 * @throws if has exception
//	 */
//	
//	@Test
//	public void listSurveyDB() throws MSMApplicationException, MSMSystemException {
//		try {
//			
//			MSMBusinessDelegate msmBD = new MSMBusinessDelegate();
//			ArrayList<SurveyData> surveyList = new ArrayList<SurveyData>();
//			UserInstance inst = new UserInstance();
//			UserData userLogged = inst.getLoggedUser();
//			SurveyVO[] surveys = msmBD.listSurveysDB(userLogged.getUserName());
//
//			for(SurveyVO survey : surveys) {
//				SurveyData bean = new SurveyData();
//				bean.setTitle(survey.getTitle());
//				bean.setId(survey.getSurveyId());
//				bean.setResults(survey.getResults());
//				bean.setDevices(survey.getDevice());
//				/** getting survey log information */
//				TransactionLogVO logs = msmBD.getSurveyReceived(survey.getSurveyId());
//				if (logs != null) {
//					bean.setUser(logs.getUser());
//					bean.setDate(SystemPropertiesInstance.toDate(logs.getDtLog()));
//				}
//				surveyList.add(bean);
//				System.out.println("Survey: " + survey.getTitle());
//			}
//		} catch (MSMApplicationException e) {
//			System.out.println(e);
//		} catch (MSMSystemException e){
//			System.out.println(e);
//		}
//	}
//	
//	/**
//	 * Pass invalid logged user and check a survey list
//	 * @throws if has exception
//	 */
//	
//	@Test
//	public void listSurveyByInvalidLoggedUser() throws MSMApplicationException, MSMSystemException {
//		try {
//			
//			MSMBusinessDelegate msmBD = new MSMBusinessDelegate();
//			ArrayList<SurveyData> surveyList = new ArrayList<SurveyData>();
//			UserInstance inst = new UserInstance();
//			UserData userLogged = inst.getInvalidUser();
//			SurveyVO[] surveys = msmBD.listSurveysDB(userLogged.getUserName());
//
//			for(SurveyVO survey : surveys) {
//				SurveyData bean = new SurveyData();
//				bean.setTitle(survey.getTitle());
//				bean.setId(survey.getSurveyId());
//				bean.setResults(survey.getResults());
//				bean.setDevices(survey.getDevice());
//				/** getting survey log information */
//				TransactionLogVO logs = msmBD.getSurveyReceived(survey.getSurveyId());
//				if (logs != null) {
//					bean.setUser(logs.getUser());
//					bean.setDate(SystemPropertiesInstance.toDate(logs.getDtLog()));
//				}
//				surveyList.add(bean);
//				System.out.println("Survey: " + survey.getTitle());
//			}
//		} catch (MSMApplicationException e) {
//			System.out.println("Invalid User " + e);
//		} catch (MSMSystemException e){
//			System.out.println(e);
//		}
//	}
//	
//	/**
//	 * List data base results from a specific survey. 
//	 * In this case, all results associated with the first survey of the list.
//	 * @throws if has exception
//	 */
//	
//	@Test
//	public void listResultBySurvey() throws MSMApplicationException, MSMSystemException {
//		try {
//			MSMBusinessDelegate msmBD = new MSMBusinessDelegate();
//			UserInstance inst = new UserInstance();
//			UserData userLogged = inst.getLoggedUser();
//			SurveyVO[] surveys = msmBD.listSurveysDB(userLogged.getUserName());
//			
//			SurveyData beanSurvey = new SurveyData();
//			beanSurvey.setTitle(surveys[0].getTitle());
//			beanSurvey.setId(surveys[0].getSurveyId());
//			
//			ArrayList<ResultData> resultList = new ArrayList<ResultData>();
//			Collection<ResultVO> results = msmBD.getResultListDB(userLogged.getUserName(), beanSurvey.getId());
//			
//			for(ResultVO result : results) {
//				ResultData beanResult = new ResultData();
//				beanResult.setResultId(result.getResultId());
//				beanResult.setSurveyId(result.getSurveyId());
//				beanResult.setImei(result.getImei());
//				beanResult.setTitle(result.getTitle());
//				beanResult.setLat(result.getLat());
//				beanResult.setLon(result.getLon());
//				beanResult.setDate(result.getDate());
//				beanResult.setUser(result.getUser());
//				
//				resultList.add(beanResult);
//				System.out.println("Result(s) by Survey (" + beanSurvey.getTitle() + "): " + result.getTitle());
//			}
//		} catch (MSMApplicationException e) {
//			System.out.println(e.getErrorCode());
//		} catch (MSMSystemException e) {
//			System.out.println(e);
//		}
//	}
//	
//	/**
//	 * List preferences
//	 * @throws if has exception
//	 */
//	@Test
//	public void listPreference() throws MSMApplicationException {
//		try {
//			MSMBusinessDelegate msmBD = new MSMBusinessDelegate();
//			Properties p =	msmBD.getSettings();
//			
//			//Get Hashtable Enumeration to get key and value
//			Enumeration keyEnum = p.keys();
//			
//			ArrayList<PreferenceData> list = new ArrayList<PreferenceData>();
//			while(keyEnum.hasMoreElements()) {
//				//nextElement is used to get key of hashtable
//				String key = (String)keyEnum.nextElement();
//				
//				//get is used to get value of key in hashtable
//				String value = (String)p.get(key);
//				PreferenceData bean = new PreferenceData();
//				bean.setPreference(key);
//				bean.setValue(value);
//				list.add(bean);
//				System.out.println("Preference: " + bean.getPreference() + "   ,   " + bean.getValue());
//			}
//		} catch (MSMApplicationException e) {
//			System.out.println(e);
//		}
//	}
//	
//	/**
//	 * Edit preferences
//	 * @throws if has exception
//	 */
//	
//	@Test
//	public void editPreference() throws MSMApplicationException {
//		try {
//			MSMBusinessDelegate msmBD = new MSMBusinessDelegate();
//			Properties p =	msmBD.getSettings();
//			
//			if (p.containsKey("mail.smtp.user")){
//				p.setProperty("mail.smtp.user", "***teste***");
//			}
//			msmBD.updateSettings(p);
//			
//			//Get Hashtable Enumeration to get key and value
//			Enumeration keyEnum = p.keys();
//			
//			while(keyEnum.hasMoreElements()) {
//				//nextElement is used to get key of hashtable
//				String key = (String)keyEnum.nextElement();
//				
//				//get is used to get value of key in hashtable
//				String value = (String)p.get(key);
//				
//				System.out.println("Property after update: " + key + "  ,  " + value);
//	
//			}
//		
//		} catch (MSMApplicationException e) {
//			System.out.println(e);
//		}
//		
//	}
//		
}
