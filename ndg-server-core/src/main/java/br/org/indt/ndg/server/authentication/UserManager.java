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

import java.util.Collection;

import javax.ejb.Remote;

import br.org.indt.ndg.common.exception.MSMApplicationException;
import br.org.indt.ndg.common.exception.MSMSystemException;
import br.org.indt.ndg.server.client.RoleVO;
import br.org.indt.ndg.server.client.UserVO;
import br.org.indt.ndg.server.controller.QueryInputOutputVO;
import br.org.indt.ndg.server.controller.UserBalanceVO;
import br.org.indt.ndg.server.pojo.NdgUser;

@Remote
public interface UserManager {

	public UserVO validateLogin(String username, String password) throws MSMApplicationException;
	
	public QueryInputOutputVO listAllUsers(String userAdmin, QueryInputOutputVO queryIOVO) throws MSMApplicationException;
	
	public UserVO createUser(UserVO vo) throws MSMApplicationException;
	
	public void updateUser(UserVO vo) throws MSMApplicationException;
	
	public void updateUserPassword(UserVO vo) throws MSMApplicationException;
	
	public void deleteUser(String _username) throws MSMApplicationException;
	
	public void deleteNotValidatedUser(String username) throws MSMApplicationException;
	
	public Collection<RoleVO> listAllRoles() throws MSMApplicationException;
	
	public UserVO validateLoginByEmail(String email) throws MSMApplicationException;
	
	public UserVO requestAccess(UserVO userPending) throws MSMApplicationException, MSMSystemException;
	
	public void deleteUserPending(String userNamePending) throws MSMApplicationException;
	
	public void authorizeUserPending(String userNamePending) throws MSMApplicationException;
	
	public UserVO checkValidationKey(String validationKey) throws MSMApplicationException;
	
	public NdgUser findNdgUserByName(String username) throws MSMApplicationException;
	
	public NdgUser getUserAdmin(String username) throws MSMApplicationException;

	public NdgUser getUserByImei(String imei) throws MSMApplicationException;
	
	public UserVO getUserByEmail(String email) throws MSMApplicationException;
	
	public int getCountOfUsersRecorded(String userAdmin) throws MSMApplicationException; 
	
	public int getCountOfImeisRecorded(String userAdmin) throws MSMApplicationException;
	
	public int getCountOfAlertsSent(String userAdmin) throws MSMApplicationException;
	
	public boolean userAdminHasPositiveBalance(Integer balanceItem, String loggedUser);
	
	public void updateUserAdminBalance(Integer balanceItem, String loggedUser);
	
	public UserBalanceVO findUserBalanceByUser(String username) throws MSMApplicationException;
	
	public void saveSettingsFromEditorToServer(String userName, String settingsContent) throws MSMApplicationException;
	
	public String loadSettingsFromServerToEditor(String userName) throws MSMApplicationException;
	

	
}
