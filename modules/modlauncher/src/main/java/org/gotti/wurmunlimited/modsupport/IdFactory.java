package org.gotti.wurmunlimited.modsupport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Create unique ids for named templates and other custom objects in the database.
 */
public class IdFactory {
	
	private static Logger logger = Logger.getLogger(IdFactory.class.getName());
	private static IdFactory instance;
	
	private IdFactory() {
		init();
	}
	
	private void init() {
		try (Connection dbcon = ModSupportDb.getModSupportDb()) {
			if (!ModSupportDb.hasTable(dbcon, "IDS")) {
				try (Statement statement = dbcon.createStatement()) {
					statement.execute("CREATE TABLE IDS (ID INT, TYPE VARCHAR(40), NAME VARCHAR(256))");
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static synchronized IdFactory getInstance() {
		if (instance == null) {
			instance = new IdFactory();
		}
		return instance;
	}
	
	public int getId(String identifier, IIdType idType) {
		
		try (Connection dbcon = ModSupportDb.getModSupportDb()) {
			try (PreparedStatement ps1 = dbcon.prepareStatement("INSERT into IDS (ID, TYPE, NAME) SELECT (SELECT IFNULL(CASE ? WHEN 0 THEN MAX(ID) + 1 ELSE MIN(ID) - 1 END, ?)  FROM ids WHERE TYPE=?), ?, ? WHERE NOT EXISTS (SELECT * FROM IDS WHERE TYPE=? AND NAME=?)");
					PreparedStatement ps2 = dbcon.prepareStatement("SELECT ID FROM IDS WHERE TYPE=? AND NAME=?")) {
				ps1.setInt(1, idType.isCountingDown() ? 1 : 0);
				ps1.setInt(2, idType.startValue());
				ps1.setString(3, idType.typeName());
				ps1.setString(4, idType.typeName());
				ps1.setString(5, identifier);
				ps1.setString(6, idType.typeName());
				ps1.setString(7, identifier);
				
				ps2.setString(1, idType.typeName());
				ps2.setString(2, identifier);
				ps1.execute();
				try (ResultSet rs = ps2.executeQuery()) {
					if (rs.next()) {
						return rs.getInt(1);
					}
					logger.log(Level.WARNING, "Resultset is empty");
					throw new RuntimeException("Failed to retrieve new id for " + identifier);
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static int getIdFor(String identifier, IdType idType) {
		int id = getInstance().getId(identifier, idType);
		logger.log(Level.INFO, String.format("Using id %d for %s %s", id, idType.name().toLowerCase(Locale.ROOT), identifier));
		return id;
	}
}
