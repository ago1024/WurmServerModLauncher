package org.gotti.wurmunlimited.modsupport.properties;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.gotti.wurmunlimited.modsupport.IdFactory;
import org.gotti.wurmunlimited.modsupport.IdType;
import org.gotti.wurmunlimited.modsupport.ModSupportDb;

public class ModPlayerProperties {
	
	private static ModPlayerProperties instance;
	private Map<String, Integer> propertyIds = new ConcurrentHashMap<>();
	
	private ModPlayerProperties() {
		init();
	}
	
	private void init() {
		try (Connection dbcon = ModSupportDb.getModSupportDb()) {
			if (!ModSupportDb.hasTable(dbcon, "PLAYERPROPS")) {
				try (Statement statement = dbcon.createStatement()) {
					statement.execute("CREATE TABLE PLAYERPROPS ("
							+ "ID INTEGER PRIMARY KEY AUTOINCREMENT,"
							+ "PLAYERID INT8 NOT NULL,"
							+ "PROPID INT NOT NULL,"
							+ "PROPVAL INT8,"
							+ "PROPSTR TEXT,"
							+ "PROPNUM REAL,"
							+ "CREATED INT8 NOT NULL,"
							+ "EXPIRES INT8)");
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static synchronized ModPlayerProperties getInstance() {
		if (instance == null) {
			instance = new ModPlayerProperties();
		}
		return instance;
	}

	private int getPropertyId(String property) {
		return propertyIds.computeIfAbsent(property, key -> IdFactory.getIdFor(key, IdType.PLAYERPROPERTY));
	}
	
	public List<Property> getPlayerProperties(String property, long playerId) {
		return getPlayerProperties(property, playerId, false);
	}
	
	public List<Property> getPlayerProperties(String property, long playerId, boolean includeExpired) {
		final long currentTimeMillis = System.currentTimeMillis();
		List<Property> properties = new ArrayList<>();
		try (Connection dbcon = ModSupportDb.getModSupportDb()) {
			try (PreparedStatement ps2 = dbcon.prepareStatement("SELECT ID, PROPVAL, PROPSTR, PROPNUM, CREATED, EXPIRES FROM PLAYERPROPS WHERE PROPID=? AND PLAYERID=?")) {
				
				ps2.setInt(1, getPropertyId(property));
				ps2.setLong(2, playerId);
				try (ResultSet rs = ps2.executeQuery()) {
					while (rs.next()) {
						Property p = new Property();
						p.setId(rs.getLong(1));
						if (rs.getObject(2) != null) {
							p.setIntValue(rs.getLong(2));
						}
						if (rs.getObject(3) != null) {
							p.setStrValue(rs.getString(3));
						}
						if (rs.getObject(4) != null) {
							p.setNumValue(rs.getFloat(4));
						}
						p.setCreated(rs.getInt(5));
						if (rs.getObject(6) != null) {
							p.setExpires(rs.getLong(6));
						}
						if (currentTimeMillis < p.getExpires() || includeExpired) {
							properties.add(p);
						}
					}
					return properties;
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void setPlayerProperty(String property, long playerId, String value) {
		try (Connection dbcon = ModSupportDb.getModSupportDb()) {
			try (PreparedStatement ps2 = dbcon.prepareStatement("INSERT INTO PLAYERPROPS (PROPID, PLAYERID, PROPSTR, CREATED) VALUES (?,?,?,?)")) {
				ps2.setInt(1, getPropertyId(property));
				ps2.setLong(2, playerId);
				ps2.setString(3, value);
				ps2.setLong(4, System.currentTimeMillis());
				ps2.execute();
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void setPlayerProperty(String property, long playerId, long value) {
		try (Connection dbcon = ModSupportDb.getModSupportDb()) {
			try (PreparedStatement ps2 = dbcon.prepareStatement("INSERT INTO PLAYERPROPS (PROPID, PLAYERID, PROPVAL, CREATED) VALUES (?,?,?,?)")) {
				ps2.setInt(1, getPropertyId(property));
				ps2.setLong(2, playerId);
				ps2.setLong(3, value);
				ps2.setLong(4, System.currentTimeMillis());
				ps2.execute();
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void setPlayerProperty(String property, long playerId, float value) {
		try (Connection dbcon = ModSupportDb.getModSupportDb()) {
			try (PreparedStatement ps2 = dbcon.prepareStatement("INSERT INTO PLAYERPROPS (PROPID, PLAYERID, PROPNUM, CREATED) VALUES (?,?,?,?)")) {
				ps2.setInt(1, getPropertyId(property));
				ps2.setLong(2, playerId);
				ps2.setFloat(3, value);
				ps2.setLong(4, System.currentTimeMillis());
				ps2.execute();
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void deletePlayerProperties(String property, long playerId) {
		try (Connection dbcon = ModSupportDb.getModSupportDb()) {
			try (PreparedStatement ps2 = dbcon.prepareStatement("DELETE FROM PLAYERPROPS WHERE PROPID=? AND PLAYERID=?")) {
				ps2.setInt(1, getPropertyId(property));
				ps2.setLong(2, playerId);
				ps2.execute();
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
