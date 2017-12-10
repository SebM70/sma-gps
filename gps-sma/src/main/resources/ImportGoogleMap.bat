CALL SET_CLASSPATH.bat
REM -z6 : download map with zoom level 6. 3 is good enough in sand dunes. 7 is for global maps
REM -tmin500 -tmax2000 : random downloads between 500 ms to 2000 ms in order not to be black listed by Google.
REM D:/map/Java/CartesTunisie.wpt : a path relative or absolute to PCX-5 Garmin file of waypoint of the delimited zone to download
REM -tmpD:/map/Java/tmp : a path to a temporary directory where all small images are downloaded
REM -saveD:/map/Java/googleMap : save path where you want to put new map files for TTQV
REM -t:S : Satelitte, -t:P: Plan, -t:R: relief
REM -calozi : generate calibration file for OziExplorer
REM java -Xms128m -Xmx512m -verbosegc sma.ImportGoogleMap D:/map/Java/CartesTunisie.wpt -z1 -tmin100 -tmax300 -tmpD:/map/Java/tmp -saveD:/map/Java/googleMap
java %JVM_ARG% sma.ImportGoogleMap D:/map/Java/CartesTunisie.wpt -z2 -t:S -tmp:D:/map/Java/tmp -saveD:\map\sync\GM_Libye
pause