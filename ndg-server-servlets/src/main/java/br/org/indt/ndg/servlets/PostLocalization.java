package br.org.indt.ndg.servlets;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import br.org.indt.ndg.server.client.LocalizationBussinessDelegate;
import br.org.indt.ndg.server.client.MSMBusinessDelegate;
import br.org.indt.ndg.server.util.SettingsProperties;

public class PostLocalization extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 290610912809360868L;
	
	private static final String NAME_FIELD = "locale_name";
	private static final String LOCALE_FIELD = "locale_str";
	private static final String LOCALE_FILE_FIELD = "locale_file";
	private static final String FONT_FILE_FIELD = "font_file";
	private static final String LOCALE_FILE_PATTERN= "messages_%s.properties";
	private static final String FONT_FILE_PATTERN= "fonts_%s.res";
	
	private MSMBusinessDelegate msmBD = null;
	
	public void init() {
		msmBD = new MSMBusinessDelegate();
	}
		
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		getServletConfig().getServletContext().getRequestDispatcher("/LocalizationUploader.jsp").forward(request, response);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		List<?> items = null;
		Iterator<?> iter = null;
        FileItem item = null;
		FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        
        try {
            items = upload.parseRequest(request);
        } catch (FileUploadException ex) {
            ex.printStackTrace();
        }  
        
        if (items == null) {
            return; //TODO return info about fail
        }

        iter = items.iterator();
        
        LocalizationSructure structure = new LocalizationSructure();

		while (iter.hasNext()) {
			item = (FileItem) iter.next();
			if (item.getFieldName().equals(NAME_FIELD)) {
				structure.setName(item.getString());
			}else if (item.getFieldName().equals(LOCALE_FIELD)) {
				structure.setLocale(item.getString());
			}else if (item.getFieldName().equals(LOCALE_FILE_FIELD)) {
				structure.setLocaleFile(item);
			}else if (item.getFieldName().equals(FONT_FILE_FIELD)) {
				structure.setFontFile(item);
			}
		}

		boolean success = false;
		if(structureCorrect(structure) && uploadFile(structure.getLocaleFile())){
			uploadFile(structure.getFontFile());
			success = addToDatabase(structure);
		}
		
		request.setAttribute("uploadResult", success);
		request.getRequestDispatcher("/LocalizationUploader.jsp").forward(request, response);
	}
	
	private boolean uploadFile(FileItem fileItem){
		if(fileItem == null || fileItem.getName() == null || fileItem.getName().isEmpty()){
			return false;
		}
		
		InputStream uploadedStream = null;
        FileOutputStream fos = null;
        boolean uploadSuccess = false;
		try{
	        File file = preperFile(fileItem.getName());
	        if(file != null){
		        uploadedStream = fileItem.getInputStream();
		        fos = new FileOutputStream(file);
		        byte[] myarray = new byte[1024];
		        int i = 0;
		        while ((i = uploadedStream.read(myarray)) != -1) {
		            fos.write(myarray, 0, i);
		        }
		        fos.flush();
		        uploadSuccess = true;
	        }
		}catch(IOException ex){
			ex.printStackTrace();
		}finally{
			try{
		        if(fos != null){
		        	fos.close();
		        }
		        if(uploadedStream != null){
		        	uploadedStream.close();
		        }
			}catch(IOException ex){//ignore
			}
		}
		return uploadSuccess;
	}
	
	private File preperFile(String fileName){
		String localeOtaDirectory = "";
		String jbossDirectory = "";
		File file = null;
		try {
			localeOtaDirectory = msmBD.getSpecificPropertySetting(SettingsProperties.LOCALE_OTA);
			jbossDirectory = System.getProperty("jboss.home.url");
			if(jbossDirectory.startsWith("file:/"))
			{
				jbossDirectory = jbossDirectory.replace("file:/", "");
			}
	    	file = new File(jbossDirectory + localeOtaDirectory + fileName);
	    	
	    	if(file.exists()){
	    		file.delete();
	    	}
	    	file.createNewFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}
	
	private boolean structureCorrect(LocalizationSructure structure){
		if(structure == null){
			return false;
		}
		
		if(structure.getLocale() == null || structure.getLocale().isEmpty() ||
			structure.getName() == null || structure.getName().isEmpty()){
			return false;
		}
		
		String shortLocale = structure.getLocale().substring(0, 2);
		if(!String.format(LOCALE_FILE_PATTERN, shortLocale).equals(structure.getLocaleFile().getName())){
			return false;
		}
		
		if(structure.getFontFile() != null &&  
				structure.getFontFile().getName() != null && 
				!structure.getFontFile().getName().isEmpty())
		{
			if(!String.format(FONT_FILE_PATTERN, shortLocale).equals(structure.getFontFile().getName())){
				return false;
			}			
		}
		return true;
	}
	
	private boolean addToDatabase(LocalizationSructure structure){
		LocalizationBussinessDelegate localeDelegate = new LocalizationBussinessDelegate();
		return localeDelegate.insertOrUpdateLocalization(
					structure.getName(), 
					structure.getLocale(), 
					structure.getLocaleFile().getName(), 
					structure.getFontFile().getName());
		
	}
}

class LocalizationSructure{
	private String name = "";
	private String locale = "";
	private FileItem localeFile = null;
	private FileItem fontFile = null;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLocale() {
		return locale;
	}
	public void setLocale(String locale) {
		this.locale = locale;
	}
	public FileItem getLocaleFile() {
		return localeFile;
	}
	public void setLocaleFile(FileItem localeFile) {
		this.localeFile = localeFile;
	}
	public FileItem getFontFile() {
		return fontFile;
	}
	public void setFontFile(FileItem fontFile) {
		this.fontFile = fontFile;
	}
	
	
}
