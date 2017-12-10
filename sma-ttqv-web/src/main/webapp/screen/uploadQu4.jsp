<%@page import="sma.gps.cal.Calibration"%>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<link type="text/css" rel="stylesheet" href="../stylesheets/main.css" />
<title>Upload a QU4</title>
</head>
<body>
<p>
<h2>Upload a QU4</h2>
</p>
	<form ENCTYPE="multipart/form-data" method="POST" action="../UploadQu4">
		<input TYPE="file"	NAME="qu4File"> <br> 
		3) Click on button:
		 <input TYPE="submit" VALUE="Uplooad file">
	</form>
<p>

</p>
<p>
Converter V<%=Calibration.VERSION %>
</p>
</body>
</html>