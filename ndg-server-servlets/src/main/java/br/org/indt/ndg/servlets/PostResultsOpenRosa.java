package br.org.indt.ndg.servlets;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.org.indt.ndg.server.client.TemporaryOpenRosaBussinessDelegate;

public class PostResultsOpenRosa extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private TemporaryOpenRosaBussinessDelegate m_openRosaBD;
	// indicates to the device that the stream has been written
	private final int SUCCESS = 1;
	// indicates to the device that the stream hasn't been written
	private final int FAILURE = -1;

	private static final String UPLOAD_FROM_WEB = "webupload";

	private static Log log = LogFactory.getLog(PostResultsOpenRosa.class);

	public void init() {
		m_openRosaBD = new TemporaryOpenRosaBussinessDelegate();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// not supported
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		m_openRosaBD.setPortAndAddress(request.getLocalAddr(), request.getLocalPort());

		InputStreamReader inputStreamReader = new InputStreamReader(request.getInputStream(), "UTF-8");
		boolean success = m_openRosaBD.parseAndPersistResult(inputStreamReader, request.getContentType());

		DataOutputStream dataOutputStream = new DataOutputStream(response.getOutputStream());
		if ( success ) {
			dataOutputStream.writeInt(SUCCESS);
			log.info("Successfully processed result stream from " + request.getRemoteAddr());
		} else {
			dataOutputStream.writeInt(FAILURE);
			log.error("Failed processing result stream from " + request.getRemoteAddr());
		}
		dataOutputStream.close();
	}
}
