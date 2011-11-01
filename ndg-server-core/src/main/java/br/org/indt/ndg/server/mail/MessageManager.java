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

package br.org.indt.ndg.server.mail;

import java.util.ArrayList;

import javax.ejb.Remote;

import br.org.indt.ndg.common.exception.MSMSystemException;
import br.org.indt.ndg.common.exception.ModemException;
import br.org.indt.ndg.server.client.ImeiVO;
import br.org.indt.ndg.server.client.UserVO;
import br.org.indt.ndg.server.sms.vo.SMSMessageVO;

@Remote
public interface MessageManager {
	public void sendTextEmail(UserVO userPending,
			int typeOfEmail) throws MSMSystemException;
	
	public void sendSMS(SMSMessageVO vo) throws MSMSystemException;

	void sendTextSMS(String message, ArrayList<String> listoOfDevices, int port)
			throws ModemException;

	void sendLinkSMS(String message, ArrayList<ImeiVO> listoOfDevices)
			throws ModemException;
}
