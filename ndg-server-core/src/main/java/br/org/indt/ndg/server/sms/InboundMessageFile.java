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

package br.org.indt.ndg.server.sms;

import org.smslib.InboundMessage;

/**
 * @author rdecarva
 *
 */
public class InboundMessageFile extends InboundMessage {
	
	private byte[] msgBytes;

	/**
	 * 
	 */
	public InboundMessageFile(java.util.Date date, java.lang.String originator, java.lang.String text, int memIndex, java.lang.String memLocation, byte[] msgBytes) {
		super(date, originator, text, memIndex, memLocation);
		this.msgBytes = msgBytes;
	}

	public byte[] getMsgBytes() {
		return msgBytes;
	}
	
	

}
