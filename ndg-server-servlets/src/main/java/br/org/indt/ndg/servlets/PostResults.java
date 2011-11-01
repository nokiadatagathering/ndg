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

package br.org.indt.ndg.servlets;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.org.indt.ndg.server.client.MSMBusinessDelegate;
import br.org.indt.ndg.server.client.TransactionLogVO;

import com.jcraft.jzlib.ZInputStream;

public class PostResults extends HttpServlet
{
	// indicates to the device that the stream has been written
	private final int SUCCESS = 1;
	
	// indicates to the device that the stream hasn't been written
	private final int FAILURE = -1;
	
	// indicates that some error during session execution
	private boolean servletError = true;
	
	private PrintWriter writer = null;
	private MSMBusinessDelegate msmBD;
	private static final String ENCODING = "UTF-8";
	private static Log log = LogFactory.getLog(PostResults.class);

	public void init()
	{
		msmBD = new MSMBusinessDelegate();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		writer = response.getWriter();
		writer.println(this.htmlBegin());
		
		if (request.getParameter("do") != null) 
		{
			if (request.getParameter("do").equals("config"))
			{
				htmlPrint("Server Configuration");
				htmlPrint("-------------------------------------------------------------------------------");
				htmlPrint("Surveys Home = " + SystemProperties.getInstance().getSurveysHome());
				htmlPrint("JBoss Home = " + SystemProperties.getInstance().getJbossHome());
				htmlPrint("Properties File = " + SystemProperties.getInstance().getPropertiesFile());
			}
		}
		else
		{
			writer.println("PostResults Servlet is up and running...");
		}
		
		writer.println(this.htmlEnd());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		log.info("Trying to process result stream from " + request.getRemoteAddr());
		
		DataOutputStream dataOutputStream = new DataOutputStream(response.getOutputStream());
		
		servletError = false;
		
		String results = Decompress(request);
		
		if (servletError)
		{
			dataOutputStream.writeInt(FAILURE);
			log.error("Failed processing result stream from " + request.getRemoteAddr());
		}
		else
		{
			if (results != null)
			{
				StringReader stringReader = new StringReader(results);
				BufferedReader bufferedReader = new BufferedReader(stringReader);
				StringBuffer stringBuffer = new StringBuffer();

				String line = bufferedReader.readLine();
				
				while ((line != null) && (!servletError))
				{
					stringBuffer.append(line + '\n');
					
					if (line.trim().equals("</result>"))
					{
						log.info("============= Result received by GPRS ============");
						log.info(stringBuffer);
						
						try
						{
							msmBD.postResult(stringBuffer, createTransactionLogVO(request));
							stringBuffer = new StringBuffer();
							
							servletError = false;
						}
						catch (Exception e)
						{
							servletError = true;
						}
					}
					
					line = bufferedReader.readLine();
				}
				
				if (servletError)
				{
					dataOutputStream.writeInt(FAILURE);
					log.error("Failed processing result stream from " + request.getRemoteAddr());
				}
				else
				{
					dataOutputStream.writeInt(SUCCESS);
					log.info("Successfully processed result stream from " + request.getRemoteAddr());
				}
	
				bufferedReader.close();
			} 
			else 
			{
				dataOutputStream.writeInt(SUCCESS);
				log.error("Failed processing stream from " + request.getRemoteAddr());
			}
		}
		
		dataOutputStream.close();
	}
	
	/*
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		servletError = false;
		DataOutputStream dos = new DataOutputStream(response.getOutputStream());
		String results = Decompress(request);

		if (results != null)
		{
			StringReader sreader = null;
			BufferedReader reader = null;
			StringBuffer tempBuffer = null;
			sreader = new StringReader(results);
			reader = new BufferedReader(sreader);
			tempBuffer = new StringBuffer();
			String line = reader.readLine();
			
			while (line != null)
			{
				tempBuffer.append(line + '\n');
				
				if (line.trim().equals("</result>"))
				{
					log.info("============= Result received by GPRS ============");
					log.info(tempBuffer);
					
					try
					{
						msmBD.postResult(tempBuffer, createTransactionLogVO(request));
						tempBuffer = new StringBuffer();
						servletError = false;
					}
					catch (MSMApplicationException e)
					{
						getServletConfig().getServletContext().getRequestDispatcher("/failure.html").forward(request, response);
					}
					catch (MSMSystemException e)
					{
						getServletConfig().getServletContext().getRequestDispatcher("/failure.html").forward(request, response);
					}
				}
				
				line = reader.readLine();
			}
			
			if (servletError)
			{
				dos.writeInt(FAILURE);
				log.info("Failed processing stream from " + request.getRemoteAddr());
			}
			else
			{
				getServletConfig().getServletContext().getRequestDispatcher("/success.html").forward(request, response);
				log.info("Success processing stream from " + request.getRemoteAddr());
			}
			
			reader.close();
			dos.close();
		} 
		else 
		{
			getServletConfig().getServletContext().getRequestDispatcher("/success.html").forward(request, response);
			log.info("Failed processing stream from " + request.getRemoteAddr());
		}
	}
	*/
	
	/*
	private String decompressFVS(HttpServletRequest request)
	{
		DataInputStream dis = null;
		DataInputStream objIn = null;
		ByteArrayOutputStream baos = null;
		String result = null;
		
		try 
		{
			dis = new DataInputStream(request.getInputStream());
			baos = new ByteArrayOutputStream();

			int length, uncomplength = 0;
			int data = 0;

		//	uncomplength = dis.readInt();
			
			length = request.getContentLength();

			for (int i = 0; i < length; i++)
			{
				data = dis.read();
				baos.write((byte) data);
			}
			
			result = new String(baos.toByteArray(), ENCODING);
			
		//	ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			
		//	ZInputStream zIn = new ZInputStream(bais);
		//	objIn = new DataInputStream(zIn);

		//	byte[] bytes = new byte[uncomplength];
		//	objIn.readFully(bytes);

		//	result = new String(bytes, ENCODING);

			log.info("Compressed length: " + length + " bytes");
			log.info("Decompressed length: " + result.getBytes().length + " bytes");
			
		//	zIn.close();
			
			dis.close();
			baos.close();
			
		//	objIn.close();
		}
		catch (EOFException e)
		{
			servletError = true;
			log.error(e);
		}
		catch (IOException e)
		{
			servletError = true;
			log.error(e);
		}
		catch (Exception e)
		{
			servletError = true;
			log.error(e);
		}
		
		return result;
	}
	*/
	
	private String Decompress(HttpServletRequest request)
	{
		DataInputStream dis = null;
		DataInputStream objIn = null;
		ByteArrayOutputStream baos = null;
		String result = null;
		
		try 
		{
			dis = new DataInputStream(request.getInputStream());
			baos = new ByteArrayOutputStream();

			int length, uncomplength = 0;
			int data = 0;

			uncomplength = dis.readInt();
			length = dis.readInt();

			for (int i = 0; i < length; i++)
			{
				data = dis.read();
				baos.write((byte) data);
			}

			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ZInputStream zIn = new ZInputStream(bais);
			objIn = new DataInputStream(zIn);

			byte[] bytes = new byte[uncomplength];
			objIn.readFully(bytes);

			result = new String(bytes, ENCODING);

			log.info("Compressed length: " + length + " bytes");
			log.info("Decompressed length: " + result.getBytes().length	+ " bytes");

			zIn.close();
			dis.close();
			baos.close();
			objIn.close();
		}
		catch (EOFException e) 
		{
			servletError = true;
			log.error(e);
		}
		catch (IOException e) 
		{
			servletError = true;
			log.error(e);
		}
		catch (Exception e)
		{
			servletError = true;
			log.error(e);
		}
		
		return result;
	}

	private TransactionLogVO createTransactionLogVO(HttpServletRequest request)
	{
		TransactionLogVO t = new TransactionLogVO();
		t.setTransmissionMode(TransactionLogVO.MODE_HTTP);
		t.setAddress(request.getRemoteAddr());
		t.setTransactionType(TransactionLogVO.TYPE_RECEIVE_RESULT);
		
		return t;
	}

	private StringBuffer htmlBegin()
	{
		StringBuffer html = new StringBuffer();
		html.append("<html>");
		html.append("<head>");
		html.append("<meta http-equiv='Content-Type' content='text/html; charset=ISO-8859-1'>");
		html.append("<title>Nokia Data Gathering Server</title>");
		html.append("</head>");
		html.append("<body>");
		
		return html;
	}

	private StringBuffer htmlEnd() 
	{
		StringBuffer html = new StringBuffer();
		html.append("</body>");
		html.append("</html>");
		
		return html;
	}

	private void htmlPrint(String s)
	{
		writer.println("<p>");
		writer.println(s);
		writer.println("</p>");
	}
}
