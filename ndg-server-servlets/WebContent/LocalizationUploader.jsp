<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Upload OpenRosa survey</title>
<style media="screen" type="text/css">
	table { margin: 1em; }
	td, th { padding: .3em; border: 1px #ccc solid; }
</style>
</head>
<body>
   <form ENCTYPE="multipart/form-data" ACTION="" METHOD="POST">
   <br><br><br>
   <center>
       <table border="0" >
           <tr><td colspan="3"><p align="center"><b>Choose a localization file to upload.</b></td></tr>
           <tr><td>Name</td><td><p align="center"><input NAME="locale_name" TYPE="text" /></p></td><td>Localization name e.g. Polish</td></tr>
           <tr><td>Locale name</td><td><p align="center"><input NAME="locale_str" TYPE="text" /></p></td><td>Locale string e.g. pl-PL</td></tr>
           <tr><td>Locale file</td><td><p align="center"><input NAME="locale_file" TYPE="file" /></p></td><td>Localization file with specified strings.</br> File should be named messages_xx.properties </br>e.g. messages_pl.properties</td></tr>
           <tr><td>Font file</td><td><p align="center"><input NAME="font_file" TYPE="file" /></p></td><td>Not requierd. Resource file with specified fonts.</br> File should be named fonts_xx.res </br>e.g. fonts_pl.res</td></tr>
           <tr><td colspan="3"><p align="center"><input TYPE="submit" VALUE="Upload files" /></p></td></tr>
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
               <tr><td>Localization file successfully uploaded</td></tr>
       <% } else { %>
           <tr><td><p style="color:red;" >Upload failed</p></td></tr>
       <% } %>
       </table>
       </center>
   <% } %>
</body>
</html>
