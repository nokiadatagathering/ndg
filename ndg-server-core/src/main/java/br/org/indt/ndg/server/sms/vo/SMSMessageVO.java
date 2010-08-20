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

package br.org.indt.ndg.server.sms.vo;

import java.io.Serializable;
import java.util.Enumeration;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;

public class SMSMessageVO implements Serializable, Message {

	public static final char RESULT_MSG = '1';
	
	public static final char ACK_RESULT_MSG = '2';
	
	public static final char SEND_SURVEY_MSG = '3';
	
	public static final String SMS_BROADCASTING = "4";
	
	public static final String CHECK_SMS_CONNECTION = "5";
	
	public static final String ACK_SMS_CONNECTION_TO_CLIENT = "6";
	
	public static final String ACK_SMS_CONNECTION_TO_SERVER = "7";
	
	public String from;

	public String to;

	public String message;

	public byte[] payload;
	
	public int port;

	public void acknowledge() throws JMSException {
		// TODO Auto-generated method stub

	}

	public void clearBody() throws JMSException {
		// TODO Auto-generated method stub

	}

	public void clearProperties() throws JMSException {
		// TODO Auto-generated method stub

	}

	public boolean getBooleanProperty(String arg0) throws JMSException {
		// TODO Auto-generated method stub
		return false;
	}

	public byte getByteProperty(String arg0) throws JMSException {
		// TODO Auto-generated method stub
		return 0;
	}

	public double getDoubleProperty(String arg0) throws JMSException {
		// TODO Auto-generated method stub
		return 0;
	}

	public float getFloatProperty(String arg0) throws JMSException {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getIntProperty(String arg0) throws JMSException {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getJMSCorrelationID() throws JMSException {
		// TODO Auto-generated method stub
		return null;
	}

	public byte[] getJMSCorrelationIDAsBytes() throws JMSException {
		// TODO Auto-generated method stub
		return null;
	}

	public int getJMSDeliveryMode() throws JMSException {
		// TODO Auto-generated method stub
		return 0;
	}

	public Destination getJMSDestination() throws JMSException {
		// TODO Auto-generated method stub
		return null;
	}

	public long getJMSExpiration() throws JMSException {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getJMSMessageID() throws JMSException {
		// TODO Auto-generated method stub
		return null;
	}

	public int getJMSPriority() throws JMSException {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean getJMSRedelivered() throws JMSException {
		// TODO Auto-generated method stub
		return false;
	}

	public Destination getJMSReplyTo() throws JMSException {
		// TODO Auto-generated method stub
		return null;
	}

	public long getJMSTimestamp() throws JMSException {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getJMSType() throws JMSException {
		// TODO Auto-generated method stub
		return null;
	}

	public long getLongProperty(String arg0) throws JMSException {
		// TODO Auto-generated method stub
		return 0;
	}

	public Object getObjectProperty(String arg0) throws JMSException {
		// TODO Auto-generated method stub
		return null;
	}

	public Enumeration getPropertyNames() throws JMSException {
		// TODO Auto-generated method stub
		return null;
	}

	public short getShortProperty(String arg0) throws JMSException {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getStringProperty(String arg0) throws JMSException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean propertyExists(String arg0) throws JMSException {
		// TODO Auto-generated method stub
		return false;
	}

	public void setBooleanProperty(String arg0, boolean arg1)
			throws JMSException {
		// TODO Auto-generated method stub

	}

	public void setByteProperty(String arg0, byte arg1) throws JMSException {
		// TODO Auto-generated method stub

	}

	public void setDoubleProperty(String arg0, double arg1) throws JMSException {
		// TODO Auto-generated method stub

	}

	public void setFloatProperty(String arg0, float arg1) throws JMSException {
		// TODO Auto-generated method stub

	}

	public void setIntProperty(String arg0, int arg1) throws JMSException {
		// TODO Auto-generated method stub

	}

	public void setJMSCorrelationID(String arg0) throws JMSException {
		// TODO Auto-generated method stub

	}

	public void setJMSCorrelationIDAsBytes(byte[] arg0) throws JMSException {
		// TODO Auto-generated method stub

	}

	public void setJMSDeliveryMode(int arg0) throws JMSException {
		// TODO Auto-generated method stub

	}

	public void setJMSDestination(Destination arg0) throws JMSException {
		// TODO Auto-generated method stub

	}

	public void setJMSExpiration(long arg0) throws JMSException {
		// TODO Auto-generated method stub

	}

	public void setJMSMessageID(String arg0) throws JMSException {
		// TODO Auto-generated method stub

	}

	public void setJMSPriority(int arg0) throws JMSException {
		// TODO Auto-generated method stub

	}

	public void setJMSRedelivered(boolean arg0) throws JMSException {
		// TODO Auto-generated method stub

	}

	public void setJMSReplyTo(Destination arg0) throws JMSException {
		// TODO Auto-generated method stub

	}

	public void setJMSTimestamp(long arg0) throws JMSException {
		// TODO Auto-generated method stub

	}

	public void setJMSType(String arg0) throws JMSException {
		// TODO Auto-generated method stub

	}

	public void setLongProperty(String arg0, long arg1) throws JMSException {
		// TODO Auto-generated method stub

	}

	public void setObjectProperty(String arg0, Object arg1) throws JMSException {
		// TODO Auto-generated method stub

	}

	public void setShortProperty(String arg0, short arg1) throws JMSException {
		// TODO Auto-generated method stub

	}

	public void setStringProperty(String arg0, String arg1) throws JMSException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String smsMessageVO = "\t>>>>>>>>>>>> SMS <<<<<<<<<<<<\n\t To: " + to + "\n\t From: "
				+ from + "\n\t Message: " + new String(message);
		return smsMessageVO;
	}

}
