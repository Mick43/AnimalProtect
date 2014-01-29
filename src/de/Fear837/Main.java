package de.Fear837;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import de.Fear837.listener.EntityListener;
import de.Fear837.listener.EntityLoadSaveListener;
import de.Fear837.structs.EntityList;

public class Main extends JavaPlugin {

	boolean isEnabled = false;

	private MySQL sql;
	Connection c = null;
	
	private static String hostname;
	private static String username;
	private static String dbname;
	private static String password;
	private static String port;

	private static EntityList entitylist;
	
	public void onEnable() {
		getLogger().info("[AnimalLock] Loading Plugin...");

		PluginManager pm = getServer().getPluginManager();

		try {
			/* Config initialisieren */
			initializeConfig();
			
			/* MySQL-Datenbank initialisieren */
			getLogger().info("Connecting with database: " + hostname + "/" + username);
			this.sql = new MySQL(this, hostname, port, username, dbname, password);
			c = sql.openConnection();

			initializeTables();

			/* Die EntityList initialisieren */
			entitylist = new EntityList(this);

			/* Commands intialisieren */
			Commands commands = new Commands(this, sql, entitylist);
			this.getCommand("lockanimal").setExecutor(commands);
			this.getCommand("lockinfo").setExecutor(commands);
			this.getCommand("locklist").setExecutor(commands);

			/* Die Listener registrieren */
			pm.registerEvents(new EntityListener(sql, this, entitylist), this);
			pm.registerEvents(new EntityLoadSaveListener(this), this);			

			getLogger().info("[AnimalLock] Loading finished!");
		} catch (Exception e) {
			getLogger().info("Failed to connect to MySQL-Database");
			getLogger().info(e.getMessage());
		}
	}

	public void onDisable() {
		if (sql.checkConnection()) {
			entitylist.saveToDatabase();
			sql.closeConnection();
		}
		isEnabled = false;
	}
	
	public void initializeConfig() {
		getConfig().options().copyDefaults(true);
		saveConfig();
		
		hostname = getConfig().getString("database.hostname");
		username = getConfig().getString("database.username");
		dbname = getConfig().getString("database.dbname");
		password = getConfig().getString("database.password");
		port = getConfig().getString("database.port");
	}

	public void initializeTables() {
		/* Erstelle ap_owners */
		Statement statement = null;
		try {
			statement = c.createStatement();
			try {
				statement
						.executeUpdate("CREATE TABLE IF NOT EXISTS ap_owners ("
								+ "id INT AUTO_INCREMENT PRIMARY KEY, "
								+ "name TEXT);");
			} catch (SQLException e) { e.printStackTrace(); }
		} catch (SQLException e1) { e1.printStackTrace(); }

		/* Erstelle ap_entities */
		statement = null;
		try {
			statement = c.createStatement();
			try {
				statement
						.executeUpdate("CREATE TABLE IF NOT EXISTS ap_entities ("
								+ "id INT AUTO_INCREMENT PRIMARY KEY, "
								+ "uuid VARCHAR(40), "
								+ "last_x SMALLINT(5) NOT NULL, "
								+ "last_y SMALLINT(3) UNSIGNED NOT NULL, "
								+ "last_z SMALLINT(5) NOT NULL, "
								+ "animaltype ENUM('cow', 'chicken', 'pig', 'sheep', 'horse', 'wolf'), "
								+ "nametag TEXT, "
								+ "maxhp DOUBLE, "
								+ "alive BOOL, "
								+ "color TEXT, "
								+ "armor TEXT, "
								+ "horse_jumpstrength DOUBLE, "
								+ "horse_style TEXT, "
								+ "horse_variant ENUM('NONE', 'HORSE', 'DONKEY', 'MULE', 'SKELETON_HORSE', 'UNDEAD_HORSE'));");
			} catch (SQLException e) { e.printStackTrace(); }
		} catch (SQLException e1) { e1.printStackTrace(); }

		/* Erstelle ap_locks */
		statement = null;
		try {
			statement = c.createStatement();
			try {
				statement.executeUpdate("CREATE TABLE IF NOT EXISTS ap_locks ("
						+ "id INT AUTO_INCREMENT PRIMARY KEY, "
						+ "owner_id INT, " 
						+ "entity_id INT, "
						+ "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);");
			} catch (SQLException e) { e.printStackTrace(); }
		} catch (SQLException e1) { e1.printStackTrace(); }
	}
	
	public MySQL getMySQL(){
		return this.sql;
	}
	
	public EntityList getEntityList(){
		return entitylist;
	}
}
