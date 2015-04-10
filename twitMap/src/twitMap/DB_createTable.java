package twitMap;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DB_createTable {
	public static void main(String[] args) {
		// Read RDS Connection Information from the Environment
		String dbName = "yuanfengDB";
		String userName = "yuanfeng";
		String password = "fy19920311";
		String hostname = "yuanfengdb.csvb7qajvni6.us-east-1.rds.amazonaws.com";
		String port = "3306";
		String jdbcUrl = "jdbc:mysql://" + hostname + ":" + port + "/" + dbName
				+ "?user=" + userName + "&password=" + password;

		// Load the JDBC Driver
		try {
			System.out.println("Loading driver...");
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("Driver loaded!");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(
					"Cannot find the driver in the classpath!", e);
		}

		Connection conn = null;
		Statement setupStatement = null;
		Statement readStatement = null;
		ResultSet resultSet = null;
		String results = "";
		int numresults = 0;
		String statement = null;

		try {
			// Create connection to RDS instance
			conn = DriverManager.getConnection(jdbcUrl);

			// Create a table and write two rows
			setupStatement = conn.createStatement();
			String createTwitRec = "CREATE TABLE twit_rec (twit_id varchar(20) not null primary key,"
					+ "twit_time datetime not null,"
					+ "lati varchar(20) not null,"
					+ "longi varchar(20) not null);";
			String createTwitKW = "CREATE TABLE twit_kw (kw_id int not null primary key auto_increment,"
					+ "kw varchar(50) not null, unique(kw));";
			String createTwitRel = "CREATE TABLE twit_rel (twit_id varchar(20) references twit_rec (twit_id),"
					+ "kw_id int references twit_kw(kw_id));";

			setupStatement.addBatch(createTwitRec);
			setupStatement.addBatch(createTwitKW);
			setupStatement.addBatch(createTwitRel);
			setupStatement.executeBatch();
			setupStatement.close();

		} catch (SQLException ex) {
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		} finally {
			System.out.println("Closing the connection.");
			if (conn != null)
				try {
					conn.close();
				} catch (SQLException ignore) {
				}
			System.out.println("Connection closed!");
		}
	}
}