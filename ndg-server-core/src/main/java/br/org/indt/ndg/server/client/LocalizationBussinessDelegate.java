package br.org.indt.ndg.server.client;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LocalizationBussinessDelegate {
	private static Log log = LogFactory.getLog(TemporaryOpenRosaBussinessDelegate.class);
	private static String SELECT_LANG_LIST = "SELECT name, localeString FROM languages;";
	private static String SELECT_LANGUAGE = "SELECT %1$s FROM languages WHERE localeString LIKE '%2$s%%';";
	private static String INSERT_LANGUAGE = "INSERT INTO languages (name, localeString, translationFilePath, fontFilePath) VALUES( ?, ?, ?, ? );";
	private static String UPDATE_LANGUAGE = "UPDATE languages SET  name  = ?, translationFilePath  = ?, fontFilePath  = ? WHERE localeString = ?;";
		
	
	
	private static String LANG_NAME_COL = "name";
	private static String LANG_LOCALE_COL = "localeString";
	private static String LANG_FILE_PATH_COL = "translationFilePath";
	private static String LANG_FONT_PATH_COL = "fontFilePath";
	
	private static final String FILE_TYPE_TEXTS = "Texts";
    private static final String FILE_TYPE_FONTS = "Fonts";
	
	private Connection getDbConnection() throws SQLException {
		String url = "jdbc:mysql://localhost:3306/ndg"; //assuming that database runs on the same host"jdbc:mysql://localhost:3306/ndg"
		String dbUser = "ndg";
		String dbPwd = "ndg";
		return DriverManager.getConnection(url, dbUser, dbPwd);
   }
	
	/**
	 * 
	 * @return map <language name, locale>
	 */
	public HashMap<String, String> getLanguageList(){
		HashMap<String, String> localeMap = new HashMap<String, String>();
		
		PreparedStatement languageStatement = null;
		Connection conn = null;
		try {
	       conn = getDbConnection();
	       languageStatement = conn.prepareStatement(
	    		   SELECT_LANG_LIST,
	               ResultSet.TYPE_SCROLL_INSENSITIVE,
	               ResultSet.CONCUR_READ_ONLY );
			ResultSet surveysSet = languageStatement.executeQuery();
			boolean isValidRow = surveysSet.first();
			while( isValidRow ){
				String langName = surveysSet.getString(LANG_NAME_COL);
				String langLocale = surveysSet.getString(LANG_LOCALE_COL);
				
				localeMap.put(langName, langLocale);
				isValidRow = surveysSet.next();
			}
		} catch ( SQLException e) {
		    e.printStackTrace();
		}
		finally {
		    try {
		    	if(languageStatement != null){
		    		languageStatement.close();
		    	}
		    	if(conn != null){
		    		conn.close();
		    	}
		    } catch (Exception e) {}
		}
		return localeMap;
	}
	
	public String getFontFileName(String locale){
		return getPath(locale, LANG_FONT_PATH_COL);
	}
	
	public String getLanguageFileName(String locale){
		return getPath(locale, LANG_FILE_PATH_COL);
	}
	
	private String getPath(String locale, String columnName ){
		PreparedStatement languageStatement = null;
		Connection conn = null;
		String retPath = "";
		
		try {
			conn = getDbConnection();
			
			String query = String.format(SELECT_LANGUAGE, columnName, locale);
	              
			languageStatement = conn.prepareStatement(
					    		   query,
					               ResultSet.TYPE_SCROLL_INSENSITIVE,
					               ResultSet.CONCUR_READ_ONLY );
			ResultSet surveysSet = languageStatement.executeQuery();
		
			if( surveysSet.first() ){
				retPath = surveysSet.getString(columnName);
			}
		} catch ( SQLException e) {
		    e.printStackTrace();
		}
		finally {
		    try {
		    	if(languageStatement != null){
		    		languageStatement.close();
		    	}
		    	if(conn != null){
		    		conn.close();
		    	}
		    } catch (Exception e) {}
		}
		return retPath;
	}	
	
	public boolean insertOrUpdateLocalization(String name, String locale, String localeFile, String fontFile){
		String selectTestStr = getPath(locale, LANG_FILE_PATH_COL);
		
		System.out.println("Selected str:" + selectTestStr);
		if(selectTestStr == null || selectTestStr.isEmpty()){
			return insertNewLocalization(name, locale, localeFile, fontFile);
		}else{
			return updateNewLocalization(name, locale, localeFile, fontFile);
		}
	}	
	
	private boolean insertNewLocalization(String name, String locale, String localeFile, String fontFile){
		PreparedStatement insertLocaleStatement = null;
		Connection conn = null;
		String retPath = "";
		boolean retVal = true;
		try {
			conn = getDbConnection();
			insertLocaleStatement = conn.prepareStatement(INSERT_LANGUAGE); 
			insertLocaleStatement.setString(1, name);
			insertLocaleStatement.setString(2, locale);
			insertLocaleStatement.setString(3, localeFile);
			insertLocaleStatement.setString(4, fontFile);
			insertLocaleStatement.executeUpdate();		
			
			System.out.println("Query" + insertLocaleStatement);
			retVal = true;
		} catch ( SQLException e) {
		    e.printStackTrace();
		    retVal = false;
		}
		finally {
		    try {
		    	if(insertLocaleStatement != null){
		    		insertLocaleStatement.close();
		    	}
		    	if(conn != null){
		    		conn.close();
		    	}
		    } catch (Exception e) {}//ignore
		}
		return retVal;
	}
	
	
	private boolean updateNewLocalization(String name, String locale, String localeFile, String fontFile){
		PreparedStatement updateLocaleStatement = null;
		Connection conn = null;
		String retPath = "";
		boolean retVal = true;
		try {
			conn = getDbConnection();
			updateLocaleStatement = conn.prepareStatement(UPDATE_LANGUAGE); 
			updateLocaleStatement.setString(1, name);			
			updateLocaleStatement.setString(2, localeFile);
			updateLocaleStatement.setString(3, fontFile);
			updateLocaleStatement.setString(4, locale);
			updateLocaleStatement.executeUpdate();		
			
			System.out.println("Query" + updateLocaleStatement);
			retVal = true;
		} catch ( SQLException e) {
		    e.printStackTrace();
		    retVal = false;
		}
		finally {
		    try {
		    	if(updateLocaleStatement != null){
		    		updateLocaleStatement.close();
		    	}
		    	if(conn != null){
		    		conn.close();
		    	}
		    } catch (Exception e) {}//ignore
		}
		return retVal;
	}
	
}
