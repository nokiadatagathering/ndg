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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import br.org.indt.ndg.common.exception.DeviceAlreadyExistException;
import br.org.indt.ndg.common.exception.DeviceNotFoundException;
import br.org.indt.ndg.common.exception.MSMApplicationException;
import br.org.indt.ndg.server.client.DeviceVO;
import br.org.indt.ndg.server.controller.QueryInputOutputVO;
import br.org.indt.ndg.server.pojo.NdgDevice;
import br.org.indt.ndg.server.util.PropertiesUtil;
import br.org.indt.ndg.server.util.SqlUtil;

public @Stateless
class DeviceManagerBean implements DeviceManager {

	// private static final Logger log = Logger.getLogger("ndgdevice");
	Properties settingsProperties = PropertiesUtil
			.loadFileProperty(PropertiesUtil.SETTINGS_FILE);

	@PersistenceContext(name = "MobisusPersistence")
	private EntityManager manager;

	public void createDevice(DeviceVO deviceVO) throws MSMApplicationException {
		NdgDevice device = findNdgDeviceByModel(deviceVO.getDeviceModel());
		if (device == null) {
			device = new NdgDevice();
			device.setDeviceModel(deviceVO.getDeviceModel());
			manager.persist(device);
		} else {
			throw new DeviceAlreadyExistException();
		}
	}

	public void updateDevice(DeviceVO deviceVO) throws MSMApplicationException {
		NdgDevice device = findNdgDeviceByIdDevice(deviceVO);
		if (device != null) {
			NdgDevice foundDevice = findNdgDeviceByModel(deviceVO
					.getDeviceModel());
			if (foundDevice == null
					|| foundDevice.getIdDevice() == device.getIdDevice()) {
				device.setDeviceModel(deviceVO.getDeviceModel());
				manager.persist(device);
			} else {
				throw new DeviceAlreadyExistException();
			}
		} else {
			throw new DeviceNotFoundException();
		}
	}

	public void deleteDevice(String deviceName) throws MSMApplicationException {
		NdgDevice device = findNdgDeviceByModel(deviceName);
		if (device != null) {
			manager.remove(device);
		} else {
			throw new DeviceNotFoundException();
		}
	}

	public NdgDevice findNdgDeviceByModel(String deviceModel)
			throws MSMApplicationException {
		Query query = manager.createNamedQuery("device.findByDeviceModel");
		query.setParameter("deviceModel", deviceModel);
		NdgDevice device = null;
		try {
			device = (NdgDevice) query.getSingleResult();
		} catch (NoResultException e) {
			// empty
		}
		return device;
	}

	public NdgDevice findNdgDeviceByIdDevice(DeviceVO deviceVO)
			throws MSMApplicationException {
		Query query = manager.createNamedQuery("device.findByIdDevice");
		query.setParameter("idDevice", deviceVO.getIdDevice());
		NdgDevice device = null;
		try {
			device = (NdgDevice) query.getSingleResult();
		} catch (NoResultException e) {
			// empty
		}
		return device;
	}

	public QueryInputOutputVO listAllDevices(QueryInputOutputVO queryIOVO)
			throws MSMApplicationException {
		if (queryIOVO == null) {
			queryIOVO = new QueryInputOutputVO();
		}

		String sqlCommand = "SELECT I FROM NdgDevice I WHERE I.idDevice > 0 ";

		if ((queryIOVO.getFilterText() != null)
				&& (queryIOVO.getFilterFields() != null)) {
			sqlCommand += SqlUtil.getFilterCondition(queryIOVO.getFilterText(),
					queryIOVO.getFilterFields());
		}

		if ((queryIOVO.getSortField() != null)
				&& (queryIOVO.getIsDescending() != null)) {
			sqlCommand += SqlUtil.getSortCondition(queryIOVO.getSortField(),
					queryIOVO.getIsDescending());
		}

		Query q = manager.createQuery(sqlCommand);
		queryIOVO.setRecordCount(q.getResultList().size());

		if ((queryIOVO.getPageNumber() != null)
				&& (queryIOVO.getRecordsPerPage() != null)) {
			q.setFirstResult((queryIOVO.getPageNumber() - 1)
					* queryIOVO.getRecordsPerPage());
			q.setMaxResults(queryIOVO.getRecordsPerPage());
		}

		ArrayList<Object> ret = new ArrayList<Object>();
		ArrayList<NdgDevice> al = (ArrayList<NdgDevice>) q.getResultList();

		Iterator<NdgDevice> it = al.iterator();

		while (it.hasNext()) {
			NdgDevice device = (NdgDevice) it.next();
			DeviceVO vo = new DeviceVO();
			vo.setIdDevice(device.getIdDevice());
			vo.setDeviceModel(device.getDeviceModel());

			ret.add(vo);
		}

		queryIOVO.setQueryResult(ret);

		return queryIOVO;
	}

	@Override
	public String createDynamicJad(String msisdn)
			throws MSMApplicationException {
		String msisdn_property = "app-msisdn:" + msisdn;
		String jboss_home_dir = System.getProperty("jboss.server.home.dir");

		if (msisdn.contains("+")) {
			msisdn = msisdn.replace("+", "");
		}
		String server_url = settingsProperties.getProperty("urlServer");
		String server_url_property = "server-url:" + server_url;
		String midlet_jar_url_property = "MIDlet-Jar-URL:"
				+ settingsProperties.getProperty("client.ota");

		midlet_jar_url_property = midlet_jar_url_property.replace("jad", "jar");

		String jadContent = server_url_property + "\n"
				+ midlet_jar_url_property + "\n" + msisdn_property + "\n";

		String deployJadDir = jboss_home_dir
				+ "/deploy/ndg-ota.war/client/dyn/" + msisdn;

		String urlToJad = null;
		InputStreamReader in = null;
		/*
		 * URL url = this.getClass().getClassLoader().getResource(
		 * "META-INF/ndg_jad.txt");
		 */
		try {
			FileReader url = new FileReader(jboss_home_dir + "/conf/ndg.jad");
			BufferedReader bufferedReader = new BufferedReader(url);

			StringBuffer sb = new StringBuffer();
			String line = null;
			try {
				while ((line = bufferedReader.readLine()) != null) {
					sb.append(line + "\n");
				}
				sb.append(jadContent);
			} catch (IOException e) {
				e.printStackTrace();
			}
			writeJadContent(sb.toString(), deployJadDir);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		urlToJad = settingsProperties.getProperty("urlServer")
				+ "/ndg-ota/client/dyn/" + msisdn + "/ndg.jad";
		return urlToJad;
	}

	private void writeJadContent(String jadContent, String deployJadDir) {
		Writer writer = null;
		String dynamicJad = "/ndg.jad";
		try {
			File folder = new File(deployJadDir);
			folder.mkdirs();
			File file = new File(deployJadDir + dynamicJad);
			writer = new BufferedWriter(new FileWriter(file));
			writer.write(jadContent);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void deleteJadDir(String msisdn) throws MSMApplicationException {
		String jboss_home_dir = System.getProperty("jboss.server.home.dir");
		if (msisdn.contains("+")) {
			msisdn = msisdn.replace("+", "");
		}
		String deployJadDir = jboss_home_dir
				+ "/deploy/ndg-ota.war/client/dyn/" + msisdn;
		String dynamicJad = "/ndg.jad";

		File file = new File(deployJadDir + dynamicJad);
		if (file.exists()) {
			file.delete();
			file = new File(deployJadDir);
			file.delete();
		}
	}

}
