<%@page import="sma.gps.cal.Calibration"%>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<link type="text/css" rel="stylesheet" href="../stylesheets/main.css" />
<title>Convert OZI map file To TTQV Cal file</title>
</head>
<body>
<p>
<h2>Utility to convert a set of calibration files from OZI format to TTQV format</h2>
</p>
	<form ENCTYPE="multipart/form-data" method="POST" action="../ConvertOziMapToTtqv">
		<input type="checkbox" name="useOnly4Corners" value="1" checked="checked"/> useOnly4Corners <br> 
		1) Create a ZIP file with all the map files you want to convert,<br>
		2) Select the ZIP file you created: <br> 
		<input TYPE="file"	NAME="zipFile"> <br> 
		3) Click on Convert button:
		 <input TYPE="submit" VALUE="Convert to cal file">
	</form>
<p>
1) Créer un fichier ZIP avec dedans les fichiers map que vous souhaitez convertire<BR>
2) Sélectionner le fichier ZIP<BR>
3) et cliquez sur le boutton Convert. Vous receverez un nouveau fichier Zip contenant les fichiers de calibration pour TTQV.<BR>
Attention, il semble que cela ne marche pas avec des caractères accentués dans les noms de carte.
</p>
<p>
Converter V<%=Calibration.VERSION %>
</p>
</body>
</html>