<%@ page import="java.util.Map,java.util.Iterator,javax.servlet.jsp.*" language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>OpenRosa Management</title>
</head>
<body>
	<%
	if ( request.getAttribute("printCommands") != null ) {
       out.print("<table border=\"0\" >");
		out.print("<tr><td>Command</td><td>Description</td></tr>");
		out.print("<tr><td>/OpenRosaManagement?action=setSurveysForUser</td><td>Allows to set survey available for user</td></tr>");
		out.print("<tr><td>/OpenRosaManagement?action=exportResultsForUser</td><td>Provides .zip file with all survey results for user</td></tr>");
		out.print("<tr><td>/OpenRosaManagement</td><td>This page</td></tr>");
		out.print("</table>");
	} else if ( request.getAttribute("surveys") != null && request.getAttribute("imeiList") != null) {
		Map<String,String> surveys = (Map<String,String>)request.getAttribute("surveys");
		String[] imeiList = (String[])request.getAttribute("imeiList");
		out.print("<form action=\"\" method=\"POST\">");
		out.print("<input type=\"hidden\" name=\"action\" value=\"setSurveysForUser\">");
		out.print("IMEI: <select name=\"selectedImei\">");
		for (int i = 0; i < imeiList.length; i++) {
			out.print("<option value=\"" + imeiList[i] + "\">" + imeiList[i] +"</option>");
		}
		out.print("</select><br /><br />");
		out.print("Available survey IDs:<br />");
	    Iterator it = surveys.entrySet().iterator();
	    while (it.hasNext()) {
           Map.Entry pairs = (Map.Entry)it.next();
           out.print("<INPUT TYPE=\"checkbox\" NAME=\"selectedSurveyId\" VALUE=\"" + pairs.getKey() +"\">" + pairs.getKey() + " ");
			out.print("<a href=\"" + pairs.getValue() + "\" target=\"_blank\">Link</a><br />");
	    }
		for (int i = 0; i < surveys.size(); i++) {

		}
		out.print("<INPUT type=\"submit\" name=\"submit\" value=\"Submit\"><br />");
		out.print("</form>");
		if ( request.getAttribute("surveysForUserResult") != null && ((Boolean)request.getAttribute("surveysForUserResult")).booleanValue()) {
			out.print("<br /><br />Surveys are now available to download for selected user<br /><br />");
		}
	} else if ( request.getAttribute("exportResultsForUser") != null && request.getAttribute("imeiList") != null) {
		String[] imeiList = (String[])request.getAttribute("imeiList");
		out.print("Download results for selected IMEI:<br /><br />");
		out.print("<form action=\"\" method=\"POST\">");
		out.print("<input type=\"hidden\" name=\"action\" value=\"exportResultsForUser\">");
		out.print("<select name=\"selectedImei\">");
		for (int i = 0; i < imeiList.length; i++) {
			out.print("<option value=\"" + imeiList[i] + "\">" + imeiList[i] +"</option>");
		}
		out.print("</select><br /><br />");
		out.print("<INPUT type=\"submit\" name=\"submit\" value=\"Submit\"><br />");
		out.print("</form>");
	}
	%>
</body>
</html>
