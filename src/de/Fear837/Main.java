package de.Fear837;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.volcanicplaza.BukkitDev.AnimalSelector.AnimalSelector;

public class Main extends JavaPlugin{

	AnimalSelector animSel = getAnimalSelector();
	
	private String Hostname;
	private String port;
	private String user;
	private String password;
	private String database;
	
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
			
			initializeTable();
			
			Commands commands = new Commands(this, c, animSel);
			pm.registerEvents(commands, this);
			this.getCommand("lockanimal").setExecutor(commands);
			
			getLogger().info("[AnimalLock] Loading finished!");
			//pm.registerEvents(new EntityListener(sql, c), this);
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
	
	public void initializeTable()
	{
		Statement statement = null;
		try {
			statement = c.createStatement();
			try {
				statement.executeUpdate("CREATE TABLE IF NOT EXISTS animalprotect ("
						+ "id INT AUTO_INCREMENT PRIMARY KEY, "
						+ "entityid INT, owner TEXT, "
						+ "last_x INT,"
						+ "last_y INT,"
						+ "last_z INT,"
						+ "date TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
			} catch (SQLException e) { e.printStackTrace(); }
		} catch (SQLException e1) { e1.printStackTrace(); }
		
	}
	
	
	public static AnimalSelector getAnimalSelector() {
		//Get AnimalSelector plugin for later on use.
		AnimalSelector plugin = (AnimalSelector) Bukkit.getServer().getPluginManager().getPlugin("AnimalSelector");
		
		if (plugin == null || !(plugin instanceof AnimalSelector)) {
	        Bukkit.getLogger().info("[WARNING] AnimalSelector isn't loaded yet.");
	        return null;
	    }
	    return (AnimalSelector) plugin;
	}

}
