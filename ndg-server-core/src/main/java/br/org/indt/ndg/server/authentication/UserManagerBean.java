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

package br.org.indt.ndg.server.authentication;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import br.org.indt.ndg.common.MD5;
import br.org.indt.ndg.common.SystemUtils;
import br.org.indt.ndg.common.exception.DirectoryNotCreatedException;
import br.org.indt.ndg.common.exception.EmailAlreadyExistException;
import br.org.indt.ndg.common.exception.InvalidCompanyException;
import br.org.indt.ndg.common.exception.InvalidPasswordException;
import br.org.indt.ndg.common.exception.InvalidPasswordGenerationException;
import br.org.indt.ndg.common.exception.InvalidRoleException;
import br.org.indt.ndg.common.exception.InvalidUsernameException;
import br.org.indt.ndg.common.exception.MSMApplicationException;
import br.org.indt.ndg.common.exception.MSMSystemException;
import br.org.indt.ndg.common.exception.SurveyFileAlreadyExistsException;
import br.org.indt.ndg.common.exception.UserAlreadyExistException;
import br.org.indt.ndg.common.exception.UserEmailNotValidatedException;
import br.org.indt.ndg.common.exception.UserNotFoundException;
import br.org.indt.ndg.server.client.CompanyVO;
import br.org.indt.ndg.server.client.MSMBusinessDelegate;
import br.org.indt.ndg.server.client.RoleVO;
import br.org.indt.ndg.server.client.TransactionLogVO;
import br.org.indt.ndg.server.client.UserVO;
import br.org.indt.ndg.server.controller.QueryInputOutputVO;
import br.org.indt.ndg.server.controller.UserBalanceVO;
import br.org.indt.ndg.server.imei.IMEIManager;
import br.org.indt.ndg.server.pojo.Company;
import br.org.indt.ndg.server.pojo.Imei;
import br.org.indt.ndg.server.pojo.NdgRole;
import br.org.indt.ndg.server.pojo.NdgUser;
import br.org.indt.ndg.server.pojo.Survey;
import br.org.indt.ndg.server.pojo.Transactionlog;
import br.org.indt.ndg.server.pojo.UserBalance;
import br.org.indt.ndg.server.util.PropertiesUtil;
import br.org.indt.ndg.server.util.SqlUtil;

public @Stateless
class UserManagerBean implements UserManager {

	@PersistenceContext(name = "MobisusPersistence")
	private EntityManager manager;
	private static final Logger log = Logger.getLogger("user");
	private Properties properties = PropertiesUtil.loadFileProperty(PropertiesUtil.PROPERTIES_CORE_FILE);
	private String idSimpleSurvey;
	
	private String userDirectories;

	public UserVO validateLogin(String username, String password)
			throws MSMApplicationException {
		NdgUser user = findNdgUserByName(username);
		UserVO vo = new UserVO();

		if (user != null) {
			try {
				if (user.getPassword().equals(MD5.createMD5(password))) {
					if ((user.getUserValidated() == 'y' || user.getUserValidated() == 'Y')) {
						vo = userPojoToVo(user);
						vo.setRetCode(UserVO.AUTHENTICATED);
						vo.setFirstTimeUse(user.getFirstTimeUse());
						
						try{
							NdgUser userAdmin = findNdgUserByName(user.getUserAdmin());
							vo.setHasFullPermissions(userAdmin.getHasFullPermissions());
						} catch(Exception e){
							log.error("validateLogin: userAdmin not found - admin = " + user.getUserAdmin());
						}
						
						Query query = manager.createNamedQuery("userbalance.findByUserAdmin");
						query.setParameter("useradmin", user.getUserAdmin());
						UserBalance userAdminBalance = null;
						try {
							userAdminBalance = (UserBalance) query.getSingleResult();
						} catch (Exception e) {
							log.info("New user: " + user.getUsername());
						}

						if (userAdminBalance == null) {
							UserBalance initialUserBalance = new UserBalance();
							try {
								initialUserBalance.setImeis(Integer.parseInt(properties
										.getProperty("LIMIT_IMEIS")));
								initialUserBalance.setResults(Integer.parseInt(properties
										.getProperty("LIMIT_RESULTS")));
								initialUserBalance.setSendAlerts(Integer.parseInt(properties
										.getProperty("LIMIT_SEND_ALERTS")));
								initialUserBalance.setSurveys(Integer
										.parseInt(properties.getProperty("LIMIT_SURVEYS")));
								initialUserBalance.setUser(findNdgUserByName(user
										.getUserAdmin()));
								initialUserBalance.setUsers(Integer.parseInt(properties
										.getProperty("LIMIT_USER")));
							} catch (Exception e) {
								initialUserBalance.setImeis(0);
								initialUserBalance.setResults(0);
								initialUserBalance.setSendAlerts(0);
								initialUserBalance.setSurveys(0);
								initialUserBalance.setUser(findNdgUserByName(user.getUserAdmin()));
								initialUserBalance.setUsers(0);
							}
							UserBalanceVO balanceVO = new UserBalanceVO();
							balanceVO.setImeis(initialUserBalance.getImeis());
							balanceVO.setResults(initialUserBalance.getResults());
							balanceVO.setSendAlerts(initialUserBalance.getSendAlerts());
							balanceVO.setSurveys(initialUserBalance.getSurveys());
							balanceVO.setUsers(initialUserBalance.getUsers());
							vo.setUserBalance(balanceVO);
							manager.persist(initialUserBalance);
						} else {
							UserBalanceVO balanceVO = new UserBalanceVO();
							balanceVO.setImeis(userAdminBalance.getImeis());
							balanceVO.setResults(userAdminBalance.getResults());
							balanceVO.setSendAlerts(userAdminBalance.getSendAlerts());
							balanceVO.setSurveys(userAdminBalance.getSurveys());
							balanceVO.setUsers(userAdminBalance.getUsers());
							vo.setUserBalance(balanceVO);
						}
					} else {
						vo.setRetCode(UserVO.EMAIL_NOT_VALIDATED);
						throw new UserEmailNotValidatedException();
					}
				} else {
					vo.setRetCode(UserVO.INVALID_PASSWORD);
					throw new InvalidPasswordException();
				}
			} catch (NoSuchAlgorithmException e) {
				throw new InvalidPasswordGenerationException();
			}
		} else {
			vo.setRetCode(UserVO.INVALID_USERNAME);
			throw new InvalidUsernameException();
		}
		return vo;
	}

	@Override
	public UserVO requestAccess(UserVO vo) throws MSMApplicationException, MSMSystemException 
	{
		idSimpleSurvey = MD5.createIdSurvey();
		
		userDirectories = properties.getProperty(PropertiesUtil.SURVEY_ROOT)
		+ SystemUtils.FILE_SEP + vo.getUsername() + SystemUtils.FILE_SEP
		+ "survey" + idSimpleSurvey;
		
		UserVO newUser = null;
		
		NdgUser userPending = findNdgUserByName(vo.getUsername());
		if (userPending != null){
			throw new UserAlreadyExistException();
		}
		UserVO userByEmail = getUserByEmail(vo.getEmail());
		if (userByEmail != null){
			throw new EmailAlreadyExistException();
		}
		
		userPending = new NdgUser();
		userPending.setUsername(vo.getUsername());
		try {
			userPending.setPassword(MD5.createMD5(vo.getPassword()));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			userPending.setPassword(vo.getPassword());
		}

		userPending.setFirstName(vo.getFirstName());
		userPending.setLastName(vo.getLastName());
		userPending.setEmail(vo.getEmail());
		userPending.setWhoUseIt(vo.getWhoUseIt());
		userPending.setPhoneNumber(vo.getPhoneNumber());

		NdgRole role = findNdgRoleByName(vo.getRole().getName());
		if (role != null) {
			userPending.setRole(role);
		}

		Company company = findCompanyByName(vo.getCompany().getCompanyName());
		if (company != null) {
			userPending.setCompany(company);
		} else {
			Company newCompany = new Company();
			newCompany.setCompanyName(vo.getCompany().getCompanyName());
			newCompany.setCompanyType(vo.getCompany().getCompanyType());
			newCompany.setCompanyCountry(vo.getCompany().getCompanyCountry());
			newCompany.setCompanyIndustry(vo.getCompany().getCompanyIndustry());
			newCompany.setCompanySize(vo.getCompany().getCompanySize());
			userPending.setCompany(newCompany);
		}

		userPending.setAreaCode(vo.getAreaCode());
		userPending.setCountryCode(vo.getCountryCode());
		userPending.setEmailPreferences(vo.getEmailPreferences());
		userPending.setHowDoYouPlanUseNdg(vo.getHowDoYouPlanUseNdg());
		userPending.setValidationKey(generateValidationKey(vo.getEmail()));
		userPending.setUserValidated(vo.getUserValidated());
		userPending.setUserAdmin(userPending.getUsername());
		userPending.setFirstTimeUse(vo.getFirstTimeUse());
		userPending.setHasFullPermissions(vo.getHasFullPermissions());

		manager.persist(userPending);
		createUserFile(vo.getUsername());
		newUser = userPojoToVo(userPending);

		return newUser;
	}

	public UserVO createUser(UserVO vo) throws MSMApplicationException {
		UserVO newUser = null;
		NdgUser userPending = findNdgUserByName(vo.getUsername());
		UserVO userByEmail = getUserByEmail(vo.getEmail());

		if (userPending != null){
			throw new UserAlreadyExistException();
		} else if (userByEmail != null){
			throw new EmailAlreadyExistException();
		} else{
			userPending = new NdgUser();
			try {
				userPending.setPassword(MD5.createMD5(vo.getPassword()));
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			userPending.setUsername(vo.getUsername());
			userPending.setFirstName(vo.getFirstName());
			userPending.setLastName(vo.getLastName());
			userPending.setEmail(vo.getEmail());

			userPending.setPhoneNumber(vo.getPhoneNumber());
			userPending.setAreaCode(vo.getAreaCode());
			userPending.setCountryCode(vo.getCountryCode());

			NdgRole role = findNdgRoleByName(vo.getRole().getName());
			if (role != null) {
				userPending.setRole(role);
			}
			NdgUser userAdmin = findNdgUserByName(vo.getUserAdmin());
			if (userPending != null) {
				userPending.setUserAdmin(userAdmin.getUsername());
				userPending.setCompany(userAdmin.getCompany());
				userPending.setEmailPreferences(userAdmin.getEmailPreferences());
				userPending.setWhoUseIt(userAdmin.getWhoUseIt());
			}
			
			userPending.setValidationKey(generateValidationKey(vo.getEmail()));
			userPending.setHasFullPermissions(vo.getHasFullPermissions());
			userPending.setUserValidated('Y');
			userPending.setHowDoYouPlanUseNdg("");
			userPending.setFirstTimeUse('Y');
			
			manager.persist(userPending);
			
			newUser = userPojoToVo(userPending);
		}
		
		return newUser;
	}

	public void deleteNotValidatedUser(String _username) throws MSMApplicationException {
		NdgUser user = findNdgUserByName(_username);
		if (user != null) {
			try {
				//deleting transactionlog
				Query query = manager.createNamedQuery("transactionlog.findByUser");
				query.setParameter("_user", user);
				Transactionlog t = (Transactionlog) query.getSingleResult();
				manager.remove(t);
				//deleting surveys
				query = manager.createNamedQuery("survey.findByUser");
				query.setParameter("_user", user);
				ArrayList<Survey> surveysListDB = (ArrayList<Survey>) query.getResultList();
				for (Survey survey : surveysListDB) {
					manager.remove(survey);
				}
				//deleting user
				manager.remove(user);
			} catch (NoResultException e) {
				System.err.println(e.getMessage());
			}
		} else {
			throw new UserNotFoundException();
		}
	}
	
	public void deleteUser(String _username) throws MSMApplicationException {
		NdgUser user = findNdgUserByName(_username);
		if (user != null) {
			manager.remove(user);
		} else {
			throw new UserNotFoundException();
		}
	}
	
	public void updateUser(UserVO userToUpdate) throws MSMApplicationException {
		NdgUser user = findNdgUserByName(userToUpdate.getUsername());
		
		if (user != null) {
			if (userToUpdate.getFirstName() != null){
				user.setFirstName(userToUpdate.getFirstName());
			}
			if (userToUpdate.getLastName() != null){
				user.setLastName(userToUpdate.getLastName());
			}
			if (userToUpdate.getCountryCode() != null){
				user.setCountryCode(userToUpdate.getCountryCode());
			}
			if (userToUpdate.getAreaCode() != null){
				user.setAreaCode(userToUpdate.getAreaCode());
			}
			if (userToUpdate.getPhoneNumber() != null){
				user.setPhoneNumber(userToUpdate.getPhoneNumber());
			}
			if (userToUpdate.getRole() != null) {
				NdgRole role = findNdgRoleByName(userToUpdate.getRole().getName());
				if (role != null) {
					user.setRole(role);
				}
			}
			if (userToUpdate.getPassword() != null){
				try {
					user.setPassword(MD5.createMD5(userToUpdate.getPassword()));
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				}
			}
			
			
			user.setFirstTimeUse(userToUpdate.getFirstTimeUse());
			
			//if (userToUpdate.getHasFullPermissions() == 'y' ||
			//		userToUpdate.getHasFullPermissions() == 'Y') {
			//	user.setHasFullPermissions(userToUpdate.getHasFullPermissions());
			//}
			//user.setFirstTimeUse(userToUpdate.getFirstTimeUse());
			
			manager.merge(user);
		} else {
			throw new UserNotFoundException();
		}
	}

	public void updateUserPassword(UserVO userToUpdate)
			throws MSMApplicationException {
		NdgUser user = findNdgUserByName(userToUpdate.getUsername());
		if (userToUpdate.getPassword() != null) {
			try {
				user.setPassword(MD5.createMD5(userToUpdate.getPassword()));
				user.setUserValidated('Y');
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}
	}

	public QueryInputOutputVO listAllUsers(String userAdmin, QueryInputOutputVO queryIOVO) throws MSMApplicationException 
	{
		if (queryIOVO == null)
		{
			queryIOVO = new QueryInputOutputVO();
		}
		
		String sqlCommand = "SELECT U FROM NdgUser U WHERE userAdmin like '" + userAdmin + "'";
		
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

		ArrayList<Object> ret = new ArrayList<Object>();
		ArrayList<NdgUser> al = (ArrayList<NdgUser>) q.getResultList();
		
		if (al.isEmpty()) 
		{
			throw new UserNotFoundException();
		}
		else 
		{
			Iterator<NdgUser> it = al.iterator();
			
			while (it.hasNext()) 
			{
				NdgUser user = it.next();
				UserVO vo = new UserVO();
				RoleVO roleVO = new RoleVO();
				roleVO.setName(user.getRole().getRoleName());
				vo.setRole(roleVO);

				CompanyVO companyVO = new CompanyVO();
				companyVO.setCompanyName(user.getCompany().getCompanyName());
				companyVO.setCompanyCountry(user.getCompany().getCompanyCountry());
				companyVO.setCompanyIndustry(user.getCompany().getCompanyIndustry());
				companyVO.setCompanySize(user.getCompany().getCompanySize());
				companyVO.setCompanyType(user.getCompany().getCompanyType());
				companyVO.setIdCompany(user.getCompany().getIdCompany());
				vo.setCompany(companyVO);

				vo.setUsername(user.getUsername());
				vo.setEmail(user.getEmail());
				vo.setFirstName(user.getFirstName());
				vo.setLastName(user.getLastName());
				vo.setPassword(user.getPassword());
				vo.setCountryCode(user.getCountryCode());
				vo.setAreaCode(user.getAreaCode());
				vo.setPhoneNumber(user.getPhoneNumber());
				vo.setUserAdmin(user.getUserAdmin());
				vo.setUserValidated(user.getUserValidated());

				ret.add(vo);
			}
			
			queryIOVO.setQueryResult(ret);
		}
		
		return queryIOVO;
	}

	public Collection<RoleVO> listAllRoles() throws MSMApplicationException {
		ArrayList<RoleVO> ret = new ArrayList<RoleVO>();
		Query q = manager.createQuery("from NdgRole");
		ArrayList<NdgRole> roles = (ArrayList<NdgRole>) q.getResultList();
		for (NdgRole role : roles) {
			RoleVO vo = new RoleVO();
			vo.setName(role.getRoleName());
			ret.add(vo);
		}
		return ret;
	}

	@SuppressWarnings("finally")
	@Override
	public UserVO validateLoginByEmail(String email)
			throws MSMApplicationException {
		Query query = manager.createNamedQuery("user.findByEmail");
		query.setParameter("email", email);
		NdgUser user = null;
		UserVO vo = new UserVO();
		try {
			user = (NdgUser) query.getSingleResult();
			vo = userPojoToVo(user);
//			vo.setValidationKey(generateValidationKey(email));
//			user.setValidationKey(vo.getValidationKey());
//			manager.merge(user);
			vo.setValidationKey(user.getValidationKey());
		} catch (NoResultException e) {
			vo.setRetCode(UserVO.INVALID_USERNAME);
		} finally {
			return vo;
		}
	}

	public UserVO checkValidationKey(String validationKey)
			throws MSMApplicationException {
		Query query = manager.createNamedQuery("user.findByvalidationKey");
		query.setParameter("userValidationKey", validationKey);
		NdgUser user = null;
		UserVO vo = null;
		try {
			user = (NdgUser) query.getSingleResult();
			if (user.getUserValidated() == 'n'
					|| user.getUserValidated() == 'N') {
				user.setUserValidated('Y');
				manager.merge(user);
			}
			vo = userPojoToVo(user);
		} catch (NoResultException e) {
			throw new UserNotFoundException();
		}
		return vo;
	}

	public void deleteUserPending(String userNamePending)
			throws MSMApplicationException {
		NdgUser userPending = findNdgUserByName(userNamePending);
		if (userPending != null) {
			manager.remove(userPending);
		} else {
			throw new UserNotFoundException();
		}
	}

	@Override
	public void authorizeUserPending(String userNamePending)
			throws MSMApplicationException {
		NdgUser userPending = findNdgUserByName(userNamePending);
		if (userPending != null) {
			manager.merge(userPending);
		} else {
			throw new UserNotFoundException();
		}
	}

	public NdgUser findNdgUserByName(String username)
			throws MSMApplicationException {
		Query query = manager.createNamedQuery("user.findByName");
		query.setParameter("userName", username);
		NdgUser user = null;
		try {
			user = (NdgUser) query.getSingleResult();
		} catch (NoResultException e) {
			System.err.println(e.getMessage());
		}
		return user;
	}

	@Override
	public NdgUser getUserAdmin(String username) throws MSMApplicationException {
		NdgUser user = findNdgUserByName(username);
		NdgUser userAdmin = findNdgUserByName(user.getUserAdmin());
		return userAdmin;
	}

	private NdgRole findNdgRoleByName(String roleName)
			throws InvalidRoleException {
		Query query = manager.createNamedQuery("role.findByName");
		query.setParameter("roleName", roleName);
		NdgRole role = null;
		try {
			role = (NdgRole) query.getSingleResult();
		} catch (NoResultException e) {
			// throw new InvalidRoleException();
		}
		return role;
	}

	private Company findCompanyByName(String companyName)
			throws InvalidCompanyException {
		Query query = manager.createNamedQuery("company.findByName");
		query.setParameter("companyName", companyName);

		Company company = null;
		try {
			company = (Company) query.getSingleResult();
		} catch (NoResultException e) {
			// throw new InvalidCompanyException();
		}
		return company;
	}

	private UserVO userPojoToVo(NdgUser user) {
		UserVO vo = new UserVO();
		RoleVO roleVO = new RoleVO();
		roleVO.setName(user.getRole().getRoleName());
		vo.setRole(roleVO);

		CompanyVO companyVO = new CompanyVO();
		companyVO.setCompanyName(user.getCompany().getCompanyName());
		companyVO.setCompanyType(user.getCompany().getCompanyType());
		companyVO.setCompanyCountry(user.getCompany().getCompanyCountry());
		companyVO.setCompanyIndustry(user.getCompany().getCompanyIndustry());
		companyVO.setCompanySize(user.getCompany().getCompanySize());
		vo.setCompany(companyVO);

		vo.setUsername(user.getUsername());
		vo.setPassword(user.getPassword());
		vo.setFirstName(user.getFirstName());
		vo.setLastName(user.getLastName());
		vo.setEmail(user.getEmail());
		vo.setUserAdmin(user.getUserAdmin());

		vo.setCountryCode(user.getCountryCode());
		vo.setAreaCode(user.getAreaCode());
		vo.setPhoneNumber(user.getPhoneNumber());
		vo.setRetCode(UserVO.AUTHENTICATED);
		vo.setUserValidated(user.getUserValidated());
		vo.setValidationKey(user.getValidationKey());
		
		vo.setHasFullPermissions(user.getHasFullPermissions());
		return vo;
	}

	private String generateValidationKey(String text) {
		StringBuffer password = new StringBuffer();
		password.append(Integer.toBinaryString(new Timestamp(System
				.currentTimeMillis()).hashCode()));
		password.append(Integer.toBinaryString(text.hashCode()));
		return password.toString();
	}

	private void createUserFile(String username) throws MSMSystemException, MSMApplicationException 
	{
		try 
		{
			boolean success = (new File(userDirectories)).mkdirs();
			
			if (success) 
			{
				log.info("User directories: " + userDirectories + "has been created.");
			}
		}
		catch (Exception e) 
		{
			throw new DirectoryNotCreatedException();
		}
		
		createUserSimpleSurvey(username);
	}

	private void createUserSimpleSurvey(String username)
			throws MSMApplicationException, MSMSystemException {
		InputStreamReader in = null;
		String surveyString;
		StringBuffer surveyStringBuffer;
		MSMBusinessDelegate bd = new MSMBusinessDelegate();
		Pattern pattern = Pattern.compile("IDS");
		Matcher matcher;
		try {
			URL url = this.getClass().getClassLoader().getResource(
					"META-INF/survey.xml");

			in = new InputStreamReader(url.openStream(), "UTF-8");

			BufferedReader survey = new BufferedReader(in);

			StringBuilder sb = new StringBuilder();

			String line = null;
			try {
				while ((line = survey.readLine()) != null) {
					sb.append(line + '\n');
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			surveyString = sb.toString();
			matcher = pattern.matcher(surveyString);
			surveyStringBuffer = new StringBuffer(matcher
					.replaceAll(idSimpleSurvey));

			surveyString = surveyStringBuffer.toString();
			pattern = Pattern.compile("CHKS");
			matcher = pattern.matcher(surveyString);

			// 1º - change string "CHKS" by dummy checksum key,
			// once the method getMD5FromSurvey will remove it
			surveyStringBuffer = new StringBuffer(matcher
					.replaceAll("00000000000000000000000000000000"));

			// 2º - generate MD5 from survey
			String md5FromSurvey = MD5.getMD5FromSurvey(surveyStringBuffer);

			// 3º - replace dummy checksum key by the real one
			surveyString = surveyStringBuffer.toString();
			pattern = Pattern.compile("00000000000000000000000000000000");
			matcher = pattern.matcher(surveyString);

			surveyStringBuffer = new StringBuffer(matcher
					.replaceAll(md5FromSurvey));

			// Saving transactionLog and Survey in Database
			TransactionLogVO tVo = new TransactionLogVO();
			tVo.setTransactionType(TransactionLogVO.NEW_USER_ADMIN);
			// tVo.setStatus(TransactionLogVO.STATUS_SUCCESS);
			tVo.setTransmissionMode(TransactionLogVO.MODE_HTTP);
			// tVo.setDtLog(new Timestamp(System.currentTimeMillis()));
			// tVo.setSurveyId(idSimpleSurvey);
			// tVo.setUser(username);

			try 
			{
				bd.postSurvey(username, surveyStringBuffer, tVo, true);
			}
			catch (SurveyFileAlreadyExistsException e) 
			{
				e.printStackTrace();
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

	}

	@Override
	public NdgUser getUserByImei(String imei) {
		Imei imeiFound = null;
		NdgUser userbyImei = null;
		imeiFound = manager.find(Imei.class, imei);

		if (imeiFound != null) {
			userbyImei = imeiFound.getUser();
		}

		return userbyImei;
	}

	@Override
	public UserVO getUserByEmail(String email) throws MSMApplicationException {
		Query query = manager.createNamedQuery("user.findByEmail");
		query.setParameter("email", email);
		NdgUser user = null;
		UserVO vo = null;
		try {
			user = (NdgUser) query.getSingleResult();
		} catch (NoResultException e) {
			System.err.println(e.getMessage());
		}
		if (user != null) {
			vo = userPojoToVo(user);
		}
		return vo;
	}

	@Override
	public int getCountOfAlertsSent(String userAdmin)
			throws MSMApplicationException {
		return 0;
	}

	@Override
	public int getCountOfImeisRecorded(String userAdmin) throws MSMApplicationException 
	{
		IMEIManager imeiManager = null;
		
		int qtde = 0;
		
		try 
		{
			InitialContext initialContext = new InitialContext();
			imeiManager = (IMEIManager) initialContext.lookup("ndg-core/IMEIManagerBean/remote");
		}
		catch (NamingException e) 
		{
			e.printStackTrace();
		}		
		
		ArrayList<Object> queryResult = listAllUsers(userAdmin, null).getQueryResult();
		
		ArrayList<UserVO> listUsers = new ArrayList<UserVO>();
		
		for (Iterator iterator = queryResult.iterator(); iterator.hasNext();) 
		{
			UserVO object = (UserVO) iterator.next();
			listUsers.add(object);
		}

		for (UserVO userVO : listUsers) 
		{
			qtde += imeiManager.findImeiByUser(userVO.getUsername(), null, true).getQueryResult().size();
		}
		
		return qtde;
	}

	@Override
	public int getCountOfUsersRecorded(String userAdmin) throws MSMApplicationException 
	{
		int result = listAllUsers(userAdmin, null).getQueryResult().size();
		
		return result;
	}

	public boolean userAdminHasPositiveBalance(Integer balanceItem, String loggedUser) {
		boolean hasPositiveBalance = false;
		String ndgMode = null;

		if (properties.containsKey("NDG_MODE")){
			ndgMode = properties.getProperty("NDG_MODE");
		}

		char userAdminHasFullPermission = 'N';
		try {
			userAdminHasFullPermission = findNdgUserByName(findNdgUserByName(loggedUser).getUserAdmin()).getHasFullPermissions();
		} catch (MSMApplicationException e) {
			e.printStackTrace();
		}

		if (!("hosted".equals(ndgMode)) || (userAdminHasFullPermission == 'Y') ||
				(userAdminHasFullPermission == 'y')){
			hasPositiveBalance = true;
		} else {
			NdgUser onlineUser = null;
			try {
				onlineUser = findNdgUserByName(loggedUser);
			} catch (MSMApplicationException e) {
				e.printStackTrace();
			}

			Query query = manager.createNamedQuery("userbalance.findByUserAdmin");
			query.setParameter("useradmin", onlineUser.getUserAdmin());

			try {
				UserBalance userBalance = (UserBalance) query.getSingleResult();
				if (balanceItem.intValue() == UserVO.USER_LIMIT.intValue()) {
					hasPositiveBalance = (userBalance.getUsers() > 0);
				} else if (balanceItem.intValue() == UserVO.IMEI_LIMIT.intValue()){
					hasPositiveBalance = (userBalance.getImeis() > 0);
				} else if (balanceItem.intValue() == UserVO.ALERT_LIMIT.intValue()){
					hasPositiveBalance = (userBalance.getSendAlerts() > 0);
				} else if (balanceItem.intValue() == UserVO.RESULT_LIMIT.intValue()){
					hasPositiveBalance = (userBalance.getResults() > 0);
				} else if (balanceItem.intValue() == UserVO.SURVEY_LIMIT.intValue()){
					hasPositiveBalance = (userBalance.getSurveys() > 0);
				}
			} catch (Exception e){
				hasPositiveBalance = false;
			}
		}
		return hasPositiveBalance;
	}
	
	public void updateUserAdminBalance(Integer balanceItem, String loggedUser) {
		String ndgMode = null;
		if (properties.containsKey("NDG_MODE")){
			ndgMode = properties.getProperty("NDG_MODE");
		}

		char userAdminHasFullPermission = 'N';
		try {
			userAdminHasFullPermission = findNdgUserByName(findNdgUserByName(loggedUser).getUserAdmin()).getHasFullPermissions();
		} catch (MSMApplicationException e) {
			e.printStackTrace();
		}
		
		try {
			if ("hosted".equals(ndgMode)) {
				if (!((userAdminHasFullPermission == 'y') || (userAdminHasFullPermission == 'Y'))) {
					NdgUser onlineUser = findNdgUserByName(loggedUser);

					Query query = manager.createNamedQuery("userbalance.findByUserAdmin");
					query.setParameter("useradmin", onlineUser.getUserAdmin());

					UserBalance userBalance = (UserBalance) query.getSingleResult();
					if (balanceItem.intValue() == UserVO.USER_LIMIT.intValue()){					
						userBalance.setUsers(userBalance.getUsers() - 1);
					} else if (balanceItem.intValue() == UserVO.IMEI_LIMIT.intValue()) {					
						userBalance.setImeis(userBalance.getImeis() - 1);
					} else if (balanceItem.intValue() == UserVO.ALERT_LIMIT.intValue())	{					
						userBalance.setSendAlerts(userBalance.getSendAlerts() - 1);
					} else if (balanceItem.intValue() == UserVO.RESULT_LIMIT.intValue()) {			
						userBalance.setResults(userBalance.getResults() - 1);
					} else if (balanceItem.intValue() == UserVO.SURVEY_LIMIT.intValue()){				
						userBalance.setSurveys(userBalance.getSurveys() - 1);
					}

					manager.persist(userBalance);
				}
			}
		} catch (MSMApplicationException e)	{
			e.printStackTrace();
		}
	}

	private UserBalanceVO userBalancePojoToVo(UserBalance userBalance)
	{
		UserBalanceVO vo = new UserBalanceVO();

		vo.setIdUserBalance(userBalance.getIdUSerBalance());
		vo.setImeis(userBalance.getImeis());
		vo.setResults(userBalance.getResults());
		vo.setSendAlerts(userBalance.getSendAlerts());
		vo.setSurveys(userBalance.getSurveys());
		vo.setUser(userPojoToVo(userBalance.getUser()));
		vo.setUsers(userBalance.getUsers());

		return vo;
	}

	public UserBalanceVO findUserBalanceByUser(String username)
			throws MSMApplicationException {
		Query query = manager.createNamedQuery("userbalance.findByUserAdmin");
		query.setParameter("useradmin", username);
		UserBalance userBalance = null;
		UserBalanceVO userBalanceVO = null;
		try {
			userBalance = (UserBalance) query.getSingleResult();
			if(userBalance != null){
				userBalanceVO = userBalancePojoToVo(userBalance);	
			}
		} catch (NoResultException e) {
			System.err.println(e.getMessage());
		}
		return userBalanceVO;
	}
	
	public void saveSettingsFromEditorToServer(String userName, String settingsContent) throws MSMApplicationException 
	{
		NdgUser user = findNdgUserByName(userName);
		
		if (user != null) 
		{
			user.setEditorSettings(settingsContent);
			manager.persist(user);
		}
	}
	
	public String loadSettingsFromServerToEditor(String userName) throws MSMApplicationException
	{
		String settingsContent = "";
		
		NdgUser user = null;
		
		try 
		{
			try 
			{
				user = findNdgUserByName(userName);
			}
			catch (MSMApplicationException e) 
			{
				e.printStackTrace();
			}
		}
		catch (NoResultException e)
		{
			// empty
		}
		
		if (user != null)
		{
			settingsContent = user.getEditorSettings();
		}

		return settingsContent;
	}
	
	
}