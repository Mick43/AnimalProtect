package de.Fear837;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin{

	boolean isEnabled = false;
	
	private MySQL sql;
	Connection c = null;
	
	public void onEnable()
	{
		getLogger().info("[AnimalLock] Loading Plugin...");
		
		PluginManager pm = getServer().getPluginManager();
		
		try {
			this.sql = new MySQL(this, "localhost", "3306", "ni2923_5_DB", "ni2923_5_DB", "je7gjA5E");
			c = sql.openConnection();
			
			initializeTables();
			
			Commands commands = new Commands(sql);
			this.getCommand("lockanimal").setExecutor(commands);
			
			pm.registerEvents(new EntityListener(sql, c), this);
			
			getLogger().info("[AnimalLock] Loading finished!");
		} catch (Exception e) {
			getLogger().info("Failed to connect to MySQL-Database");
			getLogger().info(e.getMessage());
		}
	}
	
	public void onDisable()
	{
		if (sql.checkConnection())
		{
			sql.closeConnection();
		}
		isEnabled = false;
	}
	
	public void initializeTables()
	{
		Statement statement = null;
		try {
			statement = c.createStatement();
			try {
				statement.executeUpdate("CREATE TABLE IF NOT EXISTS ap_owners ("
						+ "id INT AUTO_INCREMENT PRIMARY KEY, "
						+ "name TEXT)");
			} catch (SQLException e) { e.printStackTrace(); }
		} catch (SQLException e1) { e1.printStackTrace(); }
		
	    statement = null;
		try {
			statement = c.createStatement();
			try {
				statement.executeUpdate("CREATE TABLE IF NOT EXISTS ap_entities ("
						+ "id INT AUTO_INCREMENT PRIMARY KEY, "
						+ "uuid VARCHAR(40), "
						+ "last_x UNSIGNED SMALLINT(5),"
						+ "last_y UNSIGNED SMALLINT(3),"
						+ "last_z UNSIGNED SMALLINT(5)");
			} catch (SQLException e) { e.printStackTrace(); }
		} catch (SQLException e1) { e1.printStackTrace(); }
		
		statement = null;
		try {
			statement = c.createStatement();
			try {
				statement.executeUpdate("CREATE TABLE IF NOT EXISTS ap_locks ("
						+ "id INT AUTO_INCREMENT PRIMARY KEY, "
						+ "owner_id INT, "
						+ "entity_id INT,"
						+ "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);");
			} catch (SQLException e) { e.printStackTrace(); }
		} catch (SQLException e1) { e1.printStackTrace(); }
	}
}
