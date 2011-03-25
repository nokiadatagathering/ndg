<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Upload OpenRosa survey</title>
</head>
<body>
   <form ENCTYPE="multipart/form-data" ACTION="" METHOD="POST">
   <br><br><br>
   <center>
       <table border="0" >
           <tr><td colspan="2"><p align="center"><b>Choose a survey to upload.<br/>Note: It won't be validated so make sure it is a valid OpenRosa document'</b></td></tr>
               <tr><td><p align="center"><input NAME="filename" TYPE="file" /></p></td></tr>
           <tr><td colspan="2"><p align="center"><input TYPE="submit" VALUE="Send File" /></p></td></tr>
       </table>
   </center>
   </form>

	<%
	Boolean uploadSuccess = (Boolean)request.getAttribute("uploadResult");
	if (uploadSuccess != null ) {
	%>
		<center>
       <table border="0" >
       <%
       if (uploadSuccess) {
       %>
               <tr><td>Survey successfully uploaded</td></tr>
       <% } else { %>
           <tr><td>Survey upload failed</td></tr>
       <% } %>
       </table>
       </center>
   <% } %>
</body>
</html>
