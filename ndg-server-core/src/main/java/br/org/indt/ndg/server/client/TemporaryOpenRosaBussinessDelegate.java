package br.org.indt.ndg.server.client;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This class is only a temporary solution to provide some fake surveys in XForms
 * MSMBussinessDelegate should probably be involved in later full-featured solution
 */
public class TemporaryOpenRosaBussinessDelegate {

	private static Log log = LogFactory.getLog(TemporaryOpenRosaBussinessDelegate.class);
	private String m_surveysServerAddress = ""; // address that should be used to download surveys
	private int m_surveysServerPort = 8080; // port that should be used to download surveys
	private String m_deviceId = null;

	private static final String INSERT_SURVEY_STATEMENT = "INSERT INTO surveysopenrosa (idSurvey, idSurveyOriginal, surveyXML) VALUES(?, ?, ?)";
	private static final String INSERT_RESULT_STATEMENT = "INSERT INTO resultsopenrosa (idResult, resultXML, idSurvey, idDevice ) VALUES(?, ?, ?, ?) ON DUPLICATE KEY UPDATE resultXML=VALUES(resultXML);";
	private static final String SELECT_ALL_SURVEYS_STATEMENT =  "SELECT * FROM surveysopenrosa";
	private static final String SELECT_SURVEYS_AVAILABLE_FOR_USER_STATEMENT =  "SELECT * FROM surveysstatusopenrosa WHERE idDevice=?";
	private static final String SELECT_SURVEY_WITH_ID_STATEMENT =  "SELECT * FROM surveysopenrosa WHERE idSurvey=?";
	private static final String SELECT_ALL_USERS_STATEMENT =  "SELECT * FROM imei";
	private static final String SELECT_ALL_RESULTS_FOR_USER_STATEMENT =  "SELECT * FROM resultsopenrosa WHERE idDevice=?";
	private static final String INSERT_SURVEYS_FOR_IMEI = "INSERT INTO surveysstatusopenrosa (idSurvey, idDevice ) VALUES(?, ?) ON DUPLICATE KEY UPDATE idSurvey=VALUES(idSurvey), idDevice=VALUES(idDevice);";
	private static final String DELETE_SURVEYS_FOR_IMEI = "DELETE FROM surveysstatusopenrosa WHERE idSurvey=? and idDevice=?";

	private static final String SURVEY_CONTENT_COLUMN = "surveyXML";
	private static final String SURVEY_ID_COLUMN = "idSurvey";
	private static final String SURVEY_ORIGINAL_ID_COLUMN = "idSurveyOriginal";
	private static final String RESULT_CONTENT_COLUMN = "resultXML";
	private static final String RESULT_ID_COLUMN = "idResult";
	private static final String IMEI_COLUMN = "imei";

	public TemporaryOpenRosaBussinessDelegate() {
		setPortAndAddress("127.0.0.1", 8080);
		setDeviceId("1");
	}

	public void setPortAndAddress(String thisServerAddress, int thisServerPort) {
		m_surveysServerAddress = thisServerAddress;
		m_surveysServerPort = thisServerPort;
	}

	public void setDeviceId(String deviceId) {
		m_deviceId = deviceId;
	}

	public String[] getUserList() {
		String[] userImeis = null;
		List<String> tempResults = new ArrayList<String>();
		PreparedStatement listUsersStmt = null;
       Connection conn = null;
       try {
           conn = getDbConnection();
           listUsersStmt = conn.prepareStatement(
                   SELECT_ALL_USERS_STATEMENT,
                   ResultSet.TYPE_SCROLL_INSENSITIVE,
                   ResultSet.CONCUR_READ_ONLY );
           ResultSet surveysSet = listUsersStmt.executeQuery();
           boolean isValidRow = surveysSet.first();
           while( isValidRow )
           {
               tempResults.add( surveysSet.getString(IMEI_COLUMN) );
				isValidRow = surveysSet.next();
			}
       } catch ( SQLException e) {
           e.printStackTrace();
       }
       finally {
           try {
               listUsersStmt.close();
               conn.close();
           } catch (Exception e) {}
       }
       userImeis = tempResults.toArray(new String[tempResults.size()]);
       return userImeis;
	}

	public Map<String,String> getSurveyIdToUrlMap() {
		Map<String, String> surveyIdsToUrlMap = new HashMap<String, String>();
		PreparedStatement listSurveysStmt = null;
       Connection conn = null;
       try {
           conn = getDbConnection();
           listSurveysStmt = conn.prepareStatement(
                   SELECT_ALL_SURVEYS_STATEMENT,
                   ResultSet.TYPE_SCROLL_INSENSITIVE,
                   ResultSet.CONCUR_READ_ONLY );
           ResultSet surveysSet = listSurveysStmt.executeQuery();
           boolean isValidRow = surveysSet.first();
           final String FAKE_DEVICE_ID = "0";
           while( isValidRow )
           {
				String surveyId = surveysSet.getString(SURVEY_ID_COLUMN);
				surveyIdsToUrlMap.put( surveyId, getSurveyDownloadUrl(FAKE_DEVICE_ID, surveyId) );
				isValidRow = surveysSet.next();
           }
       } catch ( SQLException e) {
           e.printStackTrace();
       }
       finally {
           try {
               listSurveysStmt.close();
               conn.close();
           } catch (Exception e) {}
       }
       return surveyIdsToUrlMap;
	}

	public String exportZippedResultsForUser(String deviceId) throws FileNotFoundException
	{
		String resultsDir = deviceId + File.separator;
		String zipFilename = deviceId + ".zip";
		new File(resultsDir).mkdir();
		saveResultsToFilesForDeviceId(deviceId, resultsDir);

		ZipOutputStream zipOutputStream = null;
		try {
			zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFilename));
			File zipDir = new File(resultsDir);
	        String[] dirList = zipDir.list();
	        byte[] readBuffer = new byte[4096];
	        int bytesIn = 0;

	        for ( int i=0; i<dirList.length; i++ )
	        {
               FileInputStream fis = null;
               try {
                   File f = new File(zipDir, dirList[i]);
                   fis = new FileInputStream(f);
                   ZipEntry anEntry = new ZipEntry(f.getPath());
                   zipOutputStream.putNextEntry(anEntry);
                   while((bytesIn = fis.read(readBuffer)) != -1) {
                       zipOutputStream.write(readBuffer, 0, bytesIn);
                   }
               } catch (IOException ex) {
                   ex.printStackTrace();
               } finally {
                   try {
                       fis.close();
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
               }
           }
       } finally {
           if ( zipOutputStream != null ) {
				try {
					zipOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		removeDir(resultsDir);
		return zipFilename;
	}

	private void removeDir(String resultsDir) {
		File dir = new File(resultsDir);
		if ( dir.isDirectory() ) {
			String[] files = dir.list();
			for (int i=0; i<files.length; i++) {
				new File(resultsDir, files[i]).delete();
			}
		}
		dir.delete();
	}

	private void saveResultsToFilesForDeviceId( String deviceId, String saveDir ) {
		ResultSet resultsSet = null;
		PreparedStatement listUsersStmt = null;
       Connection conn = null;
       try {
           conn = getDbConnection();
           listUsersStmt = conn.prepareStatement(
                  SELECT_ALL_RESULTS_FOR_USER_STATEMENT,
                   ResultSet.TYPE_SCROLL_INSENSITIVE,
                   ResultSet.CONCUR_READ_ONLY );
           listUsersStmt.setString(1, deviceId);
			resultsSet = listUsersStmt.executeQuery();
			try {
				boolean isValidRow = resultsSet.first();
				while( isValidRow )
				{
					String resultId = resultsSet.getString(RESULT_ID_COLUMN);
					String resultContent = resultsSet.getString(RESULT_CONTENT_COLUMN);
					FileOutputStream fileOutputStream = null;
					try {
						fileOutputStream = new FileOutputStream(saveDir + resultId + ".xml");
						fileOutputStream.write(resultContent.getBytes());
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if ( fileOutputStream != null)
							try {
								fileOutputStream.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
					}
					isValidRow = resultsSet.next();
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
       } catch ( SQLException e) {
           e.printStackTrace();
       }
       finally {
           try {
               listUsersStmt.close();
               conn.close();
           } catch (Exception e) {}
       }
   }

	/********** Downloading OpenRosa Surveys List and specific surveys **********/

	public String getFormattedSurveyAvailableToDownloadList(String imei) {
		String result = null;
		PreparedStatement listSurveysToDownloadStmt = null;
       Connection conn = null;
       try {
           conn = getDbConnection();
           listSurveysToDownloadStmt = conn.prepareStatement(
                   SELECT_SURVEYS_AVAILABLE_FOR_USER_STATEMENT,
                   ResultSet.TYPE_SCROLL_INSENSITIVE,
                   ResultSet.CONCUR_READ_ONLY );
           listSurveysToDownloadStmt.setString(1, imei);
           ResultSet results = listSurveysToDownloadStmt.executeQuery();
           OpenRosaSurveysList list = new OpenRosaSurveysList(results);
           result = list.toString();
       } catch ( SQLException e) {
           e.printStackTrace();
       }
       finally {
           try {
               listSurveysToDownloadStmt.close();
               conn.close();
           } catch (Exception e) {}
       }
       return result;
   }

	public String getFormattedSurvey( String formId, String imei ) {
		String result = null;
		PreparedStatement selectSurveyWithIdStmt = null;
		PreparedStatement deleteSurveyForImeiStmt = null;
       Connection conn = null;
       try {
           conn = getDbConnection();
           selectSurveyWithIdStmt = conn.prepareStatement(
                   SELECT_SURVEY_WITH_ID_STATEMENT,
                   ResultSet.TYPE_SCROLL_INSENSITIVE,
                   ResultSet.CONCUR_READ_ONLY );
           selectSurveyWithIdStmt.setString(1, formId);
           ResultSet results = selectSurveyWithIdStmt.executeQuery();
           if (results.first()) {
               result = results.getString(SURVEY_CONTENT_COLUMN );
               // remove survey from available to download for this user
               deleteSurveyForImeiStmt = conn.prepareStatement(DELETE_SURVEYS_FOR_IMEI);
               deleteSurveyForImeiStmt.setString(1, formId);
               deleteSurveyForImeiStmt.setString(2, imei);
               deleteSurveyForImeiStmt.executeUpdate();
           }
       } catch ( SQLException e) {
           e.printStackTrace();
       }
       finally {
           try {
               selectSurveyWithIdStmt.close();
               conn.close();
           } catch (Exception e) {}
       }
       return result;
   }

	/********** Uploading OpenRosa Surveys and Results **********/

	public boolean parseAndPersistSurvey(InputStreamReader inputStreamReader, String contentType) throws IOException {
		String surveyString = parseMultipartEncodedFile(inputStreamReader, contentType, "filename");
		String surveyId = null;
		String surveyIdOriginal = null;

		Document surveyDomDocument = null;
		ByteArrayInputStream streamToParse = new ByteArrayInputStream(surveyString.getBytes("UTF-8"));
		try {
			surveyDomDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(streamToParse);
		} catch (SAXException e) {
			e.printStackTrace();
			return false;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return false;
		} finally {
			streamToParse.close();
		}

		NodeList dataNodeList = surveyDomDocument.getElementsByTagName("data");
		if ( dataNodeList.getLength() != 1 ) {
			return false; // there MUST be exactly 1 <data> node
		} else {
			Element dataElement = (Element)dataNodeList.item(0);
			Random rand = new Random(System.currentTimeMillis());
			int newId = rand.nextInt(Integer.MAX_VALUE);
			surveyId = String.valueOf(newId);
			surveyIdOriginal = dataElement.getAttribute("id");
			dataElement.setAttribute("id", String.valueOf(newId));
			StringWriter stringWriter = null;
	        try {
	            Source source = new DOMSource(surveyDomDocument);
	            stringWriter = new StringWriter();
	            Result result = new StreamResult(stringWriter);
	            TransformerFactory factory = TransformerFactory.newInstance();
	            Transformer transformer = factory.newTransformer();
	            transformer.setOutputProperty( OutputKeys.INDENT, "no" );
	            transformer.setOutputProperty( OutputKeys.ENCODING, "UTF-8" );
	            transformer.setOutputProperty( OutputKeys.OMIT_XML_DECLARATION, "yes" );
	            transformer.setOutputProperty( OutputKeys.METHOD, "xml" );
	            transformer.transform(source, result);
	            surveyString = stringWriter.getBuffer().toString();
	        } catch (Exception e) {
				e.printStackTrace();
				return false;
	        } finally {
	            stringWriter.close();
	        }
			log.info("========================");
			log.info("Original Survey Id: " + surveyIdOriginal);
			log.info("New Survey Id: " + surveyId);
			log.info("========================");
		}
		return persistSurvey(surveyString, surveyId, surveyIdOriginal);
	}

	public boolean parseAndPersistResult(InputStreamReader inputStreamReader, String contentType) throws IOException {
		String resultString = parseMultipartEncodedFile(inputStreamReader, contentType, "filename");
		SAXParserFactory factory = SAXParserFactory.newInstance();
		OpenRosaResultsHandler handler = new OpenRosaResultsHandler();
		try {
			ByteArrayInputStream streamToParse = new ByteArrayInputStream(resultString.getBytes());
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse( streamToParse, handler );
			streamToParse.close();
		} catch (Throwable err) {
			err.printStackTrace ();
		}
		String resultId = handler.getResultId();
		String deviceId = handler.getDeviceId();
		String surveyId = handler.getSurveyId();
		if (resultId == null || deviceId == null || surveyId == null) {
			return false;
		} else {
			return persistResult(resultString, surveyId, resultId, deviceId);
		}
	}

	/**
	 * Persistance model used in the rest of application is skipped due to to much design/coding overhead compared to demo-nature of this feature
	 * Assumed that database will be on the same host as servlets
	 * @throws SQLException
	 */
	private boolean persistSurvey( String surveyString, String surveyId, String surveyIdOriginal ) {
       if (surveyString == null || surveyId == null )
           return false;

		boolean result = false;
		PreparedStatement insertSurveyStmt = null;
       Connection conn = null;
       try {
           conn = getDbConnection();
           insertSurveyStmt = conn.prepareStatement(INSERT_SURVEY_STATEMENT);
           insertSurveyStmt.setString(1, surveyId );
           insertSurveyStmt.setString(2, surveyIdOriginal );
           insertSurveyStmt.setBytes(3, surveyString.getBytes("UTF-8"));
           insertSurveyStmt.executeUpdate();
           result = true;
       } catch (UnsupportedEncodingException e) {
           e.printStackTrace();
           result = false;
       } catch ( SQLException e) {
           e.printStackTrace();
           result = false;
       }
       finally {
           try {
               insertSurveyStmt.close();
               conn.close();
           } catch (Exception e) {}
       }
       return result;
   }

	private boolean persistResult( String resultString, String surveyId, String resultId, String deviceId ) {
       boolean result = false;
       PreparedStatement insertSurveyStmt = null;
       Connection conn = null;
       try {
           conn = getDbConnection();

           insertSurveyStmt = conn.prepareStatement(INSERT_RESULT_STATEMENT);
           insertSurveyStmt.setString(1, resultId );
           insertSurveyStmt.setString(2, resultString);
           insertSurveyStmt.setString(3, surveyId );
           insertSurveyStmt.setString(4, deviceId );
           insertSurveyStmt.executeUpdate();
           result = true;
       } catch ( SQLException e) {
           e.printStackTrace();
           result = false;
       }
       finally {
           try {
               insertSurveyStmt.close();
               conn.close();
           } catch (Exception e) {}
       }
       return result;
   }

	/********** Survey Management **********/

	public boolean makeSurveysAvailableForImei(String selectedImei, String[] selectedSurveyIds) {
		boolean result = false;
		Connection conn = null;
		PreparedStatement stmt = null;
       try {
           conn = getDbConnection();
           for ( int i = 0; i < selectedSurveyIds.length; i++ ) {
               try {
                   stmt = conn.prepareStatement(INSERT_SURVEYS_FOR_IMEI);
                   stmt.setString(1, selectedSurveyIds[i]);
                   stmt.setString(2, selectedImei);
                   stmt.executeUpdate();
                   result = true;
               } catch ( SQLException e) {
                   e.printStackTrace();
                   result = false;
               }
           }
       } catch ( SQLException e) {
           e.printStackTrace();
           result = false;
       }
       return result;
   }

	/********** Helpers **********/

	private String getSurveyDownloadUrl( String deviceId, String formId ) {
		return "http://" + m_surveysServerAddress +":" + m_surveysServerPort + "/ndg-servlets/ReceiveSurveys?do=download&deviceID=" + deviceId + "&formID=" + formId;
	}

	private Connection getDbConnection() throws SQLException {
		String url = "jdbc:mysql://" + m_surveysServerAddress + "/ndg"; //assuming that database runs on the same host
       String dbUser = "ndg";
       String dbPwd = "ndg";
       return DriverManager.getConnection(url, dbUser, dbPwd);
   }

	/**
	 * This method parses file contents from the POST stream encoded with ENCTYPE="multipart/form-data"
	 * It assumes that response is "multipart/form-data" encoded and contents are single file with name as 'fileTagName'
	 * Servlet 3.0 API could be used for more convinient way to handle "multipart/form-data"
	 */
	public String parseMultipartEncodedFile(InputStreamReader inputStreamReader, String contentType, String fileTagName ) throws IOException {

		BufferedReader reader = new BufferedReader(inputStreamReader);
		StringBuffer tempBuffer = new StringBuffer();
		String line = null;

		while ((line = reader.readLine()) != null) {
			tempBuffer.append(line + '\n');
		}
		String multipartContents = new String(tempBuffer.toString());
//		// extract filename
		int filenameBeginIndex = multipartContents.indexOf(fileTagName + "=\"") + (fileTagName + "=\"").length();
//		NOTE: uncomment lines below if uploaded file real name is needed
//		int filenameLineEndIndex = multipartContents.indexOf("\n", filenameBeginIndex);
//		String filename = multipartContents.substring(filenameBeginIndex, filenameLineEndIndex);
//		filenameBeginIndex = filename.lastIndexOf("\\") + 1;
//		int filenameEndIndex = filename.indexOf("\"", filenameBeginIndex);
//		filename = filename.substring(filenameBeginIndex, filenameEndIndex);;

		// extract content
		// 1) find bounding strings
		int lastIndex = contentType.lastIndexOf("=");
		String boundary = contentType.substring(lastIndex + 1,contentType.length());
		// 2) drop Content-type and similar stuff
		int pos;
        pos = multipartContents.indexOf(fileTagName + "=\"");
		pos = multipartContents.indexOf("\n", pos) + 1;
		pos = multipartContents.indexOf("\n", pos) + 1;
		pos = multipartContents.indexOf("\n", pos) + 1;
		// 3) extract data
		int boundaryLocation = multipartContents.indexOf(boundary, pos) - 3; // magic number indeed...
		String fileContentsString = new String(multipartContents.substring(pos, boundaryLocation));

		log.info(fileContentsString);
		return fileContentsString;
	}


	class OpenRosaSurveysList {

		private final Document m_surveysXml;

		public OpenRosaSurveysList( ResultSet surveysSet ) {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = null;
			try {
				builder = factory.newDocumentBuilder();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			}
			DOMImplementation impl = builder.getDOMImplementation();
			m_surveysXml = impl.createDocument("http://openrosa.org/xforms/xformsList", "xforms", null);
			parseSurveyList( surveysSet );
		}

		private void parseSurveyList( ResultSet surveysSet ) {
			Element root = m_surveysXml.getDocumentElement();
			boolean isValidRow = false;
			try {
				isValidRow = surveysSet.first();
				while( isValidRow )
				{
					try {
						Element xformElement = m_surveysXml.createElementNS(null, "xform");
						Element surveIdElement = m_surveysXml.createElementNS(null, "formID");
						Element nameElement = m_surveysXml.createElementNS(null, "name");
						Element majorMinorVersionElement = m_surveysXml.createElementNS(null, "majorMinorVersion");
						Element downloadUrlElement = m_surveysXml.createElementNS(null, "downloadUrl");
						surveIdElement.appendChild(m_surveysXml.createTextNode(surveysSet.getString(SURVEY_ID_COLUMN)));
						nameElement.appendChild(m_surveysXml.createTextNode(surveysSet.getString(SURVEY_ID_COLUMN))); //set to the same as id
						majorMinorVersionElement.appendChild(m_surveysXml.createTextNode("1.0")); // fake
						downloadUrlElement.appendChild(m_surveysXml.createTextNode(getSurveyDownloadUrl(m_deviceId, surveysSet.getString(SURVEY_ID_COLUMN))));
						xformElement.appendChild(surveIdElement);
						xformElement.appendChild(nameElement);
						xformElement.appendChild(majorMinorVersionElement);
						xformElement.appendChild(downloadUrlElement);
						root.appendChild(xformElement);
					} catch (DOMException e) { // skipping element
						e.printStackTrace();
					} catch (SQLException e) { // skipping element
						e.printStackTrace();
					}
					isValidRow = surveysSet.next();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
	    }

		@Override
		public String toString() {
			String surveyXml = null;
	        try {
	            Source source = new DOMSource(m_surveysXml);
	            StringWriter stringWriter = new StringWriter();
	            Result result = new StreamResult(stringWriter);
	            TransformerFactory transformFactory = TransformerFactory.newInstance();
	            Transformer transformer = transformFactory.newTransformer();
	            transformer.transform(source, result);
	            surveyXml = stringWriter.getBuffer().toString();
	        } catch (TransformerConfigurationException ex) {
	            ex.printStackTrace();
	        } catch (TransformerException ex) {
	            ex.printStackTrace();
	        }
	        return surveyXml;
		}

	}

	class OpenRosaResultsHandler extends DefaultHandler {

		private static final String RESULT_ID_TAG = "orx:instanceID";
		private static final String DEVICE_ID_TAG = "orx:deviceID";
		private static final String DATA_TAG = "data";
		private static final String SURVEY_ID_ATTRIBUTE = "id";

		private String m_surveyId = null;
		private String m_resultId = null;
		private String m_deviceId = null;

		private String m_currentTag = "";

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			m_currentTag = qName;
			if (m_currentTag.equals(DATA_TAG)) {
				m_surveyId = attributes.getValue(SURVEY_ID_ATTRIBUTE);
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			m_currentTag = "";
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
	        String value = new String(ch, start, length).trim();
	        if ( value.length() > 0 ) {
		        if ( RESULT_ID_TAG.equals(m_currentTag) ) {
                   m_resultId = value;
               } else if ( DEVICE_ID_TAG.equals(m_currentTag)) {
                   m_deviceId = value;
		        }
	        }
		}

		public String getSurveyId() {
			return m_surveyId;
		}

		public String getResultId() {
			return m_resultId;
		}

		public String getDeviceId() {
			return m_deviceId;
		}
	}
}
