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

package br.org.indt.ndg.server.sms;

import java.io.File;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import br.org.indt.ndg.server.util.PropertiesUtil;

public class NewSMSInFileListener implements IObservableSMS {

	private Timer timer;
	
	private static String FILE_RESULTS = "\\messages";
	
	private static String FILE_SYNC = "\\sync";

	private File directory;

	private static final Logger log = Logger.getLogger("INFOSERVICES");

	private int fileNumber = 0;

	private static NewSMSInFileListener singleton = null;

	private IObserverSMS observerSMS;

	protected NewSMSInFileListener(IObserverSMS observerSMS) {
		Properties properties = PropertiesUtil.loadFileProperty(PropertiesUtil.PROPERTIES_CORE_FILE);
		
		directory = new File(properties.getProperty("SURVEY_ROOT") + "\\Inbox");
		register(observerSMS);
		timer = new Timer();
		timer.schedule(new SMSNewListenerInner(), 1000, 10000);
	}

	public static NewSMSInFileListener getInstance(IObserverSMS observerSMS) {
		if (singleton == null) {
			synchronized (NewSMSInFileListener.class) {
				if (singleton == null) {
					singleton = new NewSMSInFileListener(observerSMS);
				}
			}
		}
		return singleton;
	}

	class SMSNewListenerInner extends TimerTask {

		public void run() {
			watchDirectory();
		}

		private void watchDirectory() {
			File fileInboxMessages = new File(directory + FILE_RESULTS);
			File fileSyncInboxMessages = new File(directory + FILE_SYNC);			
			if (directory.list().length>0) {
				notifyObservers();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.org.indt.mobisus.msm.sms.IObservableSMS#notifyObservers()
	 */
	@Override
	public void notifyObservers() {
		observerSMS.update(true, directory);
		// fileNumber = directory.list().length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.org.indt.mobisus.msm.sms.IObservableSMS#register(br.org.indt.mobisus.msm.sms.IObserverSMS)
	 */
	@Override
	public void register(IObserverSMS observerSMS) {
		this.observerSMS = observerSMS;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.org.indt.mobisus.msm.sms.IObservableSMS#removeObserver(br.org.indt.mobisus.msm.sms.IObserverSMS)
	 */
	@Override
	public void removeObserver(IObserverSMS observerSMS) {
		// TODO Auto-generated method stub

	}

	public static void main(String[] args) {
		ObserverSMSFile observerSMSFile = new ObserverSMSFile();
		NewSMSInFileListener newMessageListener = new NewSMSInFileListener(
				observerSMSFile);
		System.out.println("Task scheduled.");
	}
}
