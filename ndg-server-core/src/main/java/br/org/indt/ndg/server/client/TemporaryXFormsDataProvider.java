package br.org.indt.ndg.server.client;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is only a temporary solution to provide some fake surveys in XForms
 * MSMBussinessDelegate should probably be involved in later full-featured solution
 */

public class TemporaryXFormsDataProvider {

	private String m_surveysServerAddress = ""; // address that should be used to download surveys
	private int m_surveysServerPort = 8080; // port that should be used to download surveys
	private static final String SERVER_ADDRESS_PLACEHOLDER = "===ndg-server-address===";
	private static final Map<String, String> m_surveyFilesList;
	static {
		Map<String, String> tempMap = new HashMap<String, String>();
		tempMap.put("1", "resources/openXdataTest.xml");
		tempMap.put("2", "resources/openXdataTest2.xml");
		tempMap.put("3", "resources/openXdataTest3.xml");
		m_surveyFilesList = Collections.unmodifiableMap(tempMap);
	}


	public TemporaryXFormsDataProvider( String surveysServerAddress, int surveysServerPort ) {
		m_surveysServerAddress = surveysServerAddress;
		m_surveysServerPort = surveysServerPort;
	}

	public String getFormattedSurveyAvailableToDownloadList() {
		String result = readFile("resources/surveyList.xml");
		// set valid server address for downloading surveys
		result = result.replace(SERVER_ADDRESS_PLACEHOLDER, getSurveysServerAddress());
		return result;
	}

	public String getFormattedSurvey( String formId ) throws FileNotFoundException {
		String result = null;
		if ( m_surveyFilesList.containsKey(formId)) {
			result = readFile(m_surveyFilesList.get(formId));
		} else {
			throw new FileNotFoundException();
		}
		return result;
	}

	public String getSurveysServerAddress() {
		return "http://" + m_surveysServerAddress + ":" + m_surveysServerPort;
	}

	private String readFile( String filePathInJar ) {
		StringBuilder stringBuilder = new StringBuilder();
		try {
			URL url = this.getClass().getClassLoader().getResource(filePathInJar);
			InputStreamReader stream = new InputStreamReader(url.openStream(), "UTF-8");
			BufferedReader reader = new BufferedReader(stream);

			String line = null;
			try {
				while ((line = reader.readLine()) != null) {
					stringBuilder.append(line /*+ '\n'*/);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			reader.close();
			stream.close();
		} catch (FileNotFoundException e) {
		  e.printStackTrace();
		} catch (IOException e) {
		  e.printStackTrace();
		}
		return stringBuilder.toString();
	}

}
