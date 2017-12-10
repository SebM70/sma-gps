package sma;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectTTQV {

	static void log(String arg) {
		System.out.println(arg);
	}

	static void logRS(ResultSet rs) throws SQLException {
		if (rs != null) {
			ResultSetMetaData metarst = rs.getMetaData();
			int colCount = metarst.getColumnCount();
			log("getColumnCount()=" + colCount);
			String columns = "";
			for (int i = 1; i <= colCount; i++) {
				columns += metarst.getColumnName(i) + " | ";
			}
			log(columns);

			while (rs.next()) {
				//log(rs.getString(1) + " " + rs.getString(3));
				columns = "";
				for (int i = 1; i <= colCount; i++) {
					columns += rs.getString(i) + " | ";
				}
				log(columns);
			}

		}
	}

	public static void main(String args[]) {
		log("d�but  ");

		// String filename = "d:/java/mdbTEST.mdb";
		String filename = "C:/Product/SMa/GPS/java/TestDB/Grece.qu3";
		// String filename = "C:/Product/SMa/GPS/java/TestDB/db1.mdb";

		String database = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=";
		database += filename.trim() + ";DriverID=22;READONLY=true}";
		// add // on to // the
		// end

		System.out.println("database=" + database);

		try {
			// load JDBC class
			Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");

			/*
			 * Enumeration lesDrivers = DriverManager.getDrivers(); while
			 * (lesDrivers.hasMoreElements()) { Driver dri = (Driver)
			 * lesDrivers.nextElement(); log(dri.toString()); }
			 */

			// now we can get the connection from the DriverManager
			Connection con = DriverManager.getConnection(database, "", "");

			log("con.getCatalog()=" + con.getCatalog());
			DatabaseMetaData meta = con.getMetaData();
			ResultSet rst = meta.getTables(null, null, null, null);
			logRS(rst);

			// try and create a java.sql.Statement so we can run queries
			Statement s = con.createStatement();
			s.execute("Select name,lat,lon,description From 2005SMa Where (del=0)"); // select
			// the
			// data
			// from
			// the
			// table
			ResultSet rs = s.getResultSet(); // get any ResultSet that came
			// from our query
			if (rs != null) // if rs == null, then there is no ResultSet to view
				while (rs.next()) // this will step through our data
				// row-by-row

				{
					/*
					 * the next line will get the first column in our current
					 * row's ResultSet as a String ( getString( columnNumber) )
					 * and output it to the screen
					 */
					// System.out.println(rs.getString(1) + " - " +
					// rs.getString("description"));
				}
			s.close(); // close the Statement to let the database know we're
			// done with it
			con.close(); // close the Connection to let the database know
			// we're done with it

		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("fin");

	}
}
