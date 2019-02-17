package org.gotti.wurmunlimited.modsupport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
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
						int id = rs.getInt(1);
						idType.updateLastUsedId(id);
						return id;
					}
					logger.log(Level.WARNING, "Resultset is empty");
					throw new RuntimeException("Failed to retrieve new id for " + identifier);
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Retrieve an existing id or -10
	 * @param identifier Identifier
	 * @param idType Id Type
	 * @return id or -10
	 */
	public int getExistingId(String identifier, IIdType idType) {
		try (Connection dbcon = ModSupportDb.getModSupportDb()) {
			try (PreparedStatement ps2 = dbcon.prepareStatement("SELECT ID FROM IDS WHERE TYPE=? AND NAME=?")) {
				ps2.setString(1, idType.typeName());
				ps2.setString(2, identifier);
				try (ResultSet rs = ps2.executeQuery()) {
					if (rs.next()) {
						int id = rs.getInt(1);
						idType.updateLastUsedId(id);
						return id;
					}
					return -10;
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Get all known ids for a type
	 * @param idType Id type
	 * @return list of known ids
	 */
	public List<Map.Entry<String, Integer>> getIds(IIdType idType) {
		List<Map.Entry<String, Integer>> results = new ArrayList<>();
		try (Connection dbcon = ModSupportDb.getModSupportDb()) {
			try (PreparedStatement ps2 = dbcon.prepareStatement("SELECT ID, NAME FROM IDS WHERE TYPE=? ORDER BY ID")) {
				ps2.setString(1, idType.typeName());
				try (ResultSet rs = ps2.executeQuery()) {
					while (rs.next()) {
						int id = rs.getInt(1);
						String name = rs.getString(2);
						results.add(new SimpleEntry<>(name, id));
					}
					return results;
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
	
	public static int getExistingIdFor(String identifier, IdType idType) {
		int id = getInstance().getExistingId(identifier, idType);
		if (id != -10)
			logger.log(Level.INFO, String.format("Using id %d for %s %s", id, idType.name().toLowerCase(Locale.ROOT), identifier));
		return id;
	}
	
	/**
	 * Get all known ids for a type
	 * @param idType Id type
	 * @return list of known ids
	 */
	public static List<Entry<String, Integer>> getIdsFor(IdType idType) {
		return getInstance().getIds(idType);
	}
}
