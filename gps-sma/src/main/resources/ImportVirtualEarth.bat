REM SET CLASSPATH=.;..\lib\log4j-1.2.8.jar;..\lib\geotransform.jar
CALL SET_CLASSPATH.bat
REM -Xms160m -Xmx256m -verbosegc : specific to Java memory settings
REM -z6 : download map with zoom level 6. 3 is good enough in sand dunes. 7 is for global maps
REM -tmin500 -tmax2000 : random downloads between 500 ms to 2000 ms in order not to be black listed by Google.
REM D:/map/Java/CartesTunisie.wpt : a path relative or absolute to PCX-5 Garmin file of waypoint of the delimited zone to download
REM -tmpD:/map/Java/tmp : a path to a temporary directory where all small images are downloaded
REM -saveD:/map/Java/googleMap : save path where you want to put new map files for TTQV
REM -t:S : Satellite, -t:P: Plan, -t:R: relief, MS: Virtual Earth Satellite, MP: Virtual Earth Plan relief
REM java -Xms128m -Xmx512m -verbosegc sma.ImportGoogleMap D:/map/Java/CartesTunisie.wpt -z1 -tmin100 -tmax300 -tmpD:/map/Java/tmp -saveD:/map/Java/googleMap
java -Xms64m -Xmx512m -XX:NewRatio=3 -verbosegc sma.ImportGoogleMap D:/map/Java/CartesTunisie.wpt -z11 -t:MP -calozi -saveD:\map\sync\virtualEarth -tmp:D:/map/Java/tmp -tmin100 -tmax300
echo %CD%
pause