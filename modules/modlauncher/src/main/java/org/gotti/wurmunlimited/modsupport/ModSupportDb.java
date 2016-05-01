package org.gotti.wurmunlimited.modsupport;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

import com.wurmonline.server.Constants;
import com.wurmonline.server.DbConnector;

public class ModSupportDb {

	private static final String LITE_DB_DRIVER = "org.sqlite.JDBC";

	public static String getDbConnectionString(String host, String db) {
		checkSqlite();

		return "jdbc:sqlite:" + host + "/sqlite/" + db.toLowerCase(Locale.ENGLISH) + ".db";
	}

	public static Connection getModSupportDb() {
		checkSqlite();

		String dbConnection = getDbConnectionString(Constants.dbHost, "modsupport");

		try {
			Class.forName(LITE_DB_DRIVER);
			return DriverManager.getConnection(dbConnection);
		} catch (SQLException | ClassNotFoundException e) {
			throw new RuntimeException("Failed to initialize modsupport db connection", e);
		}
	}

	private static void checkSqlite() {
		if (!DbConnector.isUseSqlite()) {
			throw new RuntimeException("Only Sqlite is currently supported");
		}
	}

	public static boolean hasTable(Connection dbcon, String tableName) throws SQLException {
		try (PreparedStatement statement = dbcon.prepareStatement("SELECT name FROM sqlite_master WHERE type='table' AND name=?")) {
			statement.setString(1, tableName);
			try (ResultSet rs = statement.executeQuery()) {
				if (rs.next()) {
					return true;
				}
			}
		}
		return false;
	}

}
