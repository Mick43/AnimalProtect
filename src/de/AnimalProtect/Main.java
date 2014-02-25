package de.AnimalProtect;

import java.sql.Connection;
import java.sql.SQLException;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import de.AnimalProtect.structs.EntityList;
import de.AnimalProtect.commands.*;
import de.AnimalProtect.listener.*;
import de.AnimalProtect.utility.*;

public class Main extends JavaPlugin {
	
	public static boolean isEnabled = false;
	public static boolean DEBUGMODE = false;
	
	public MySQL database;
	public Connection connection;
	public EntityList list;
	
	private String hostname;
	private String username;
	private String dbname;
	private String password;
	private String port;
	
	@Override
	public void onEnable() {
		/* Das Plugin initialisieren */
		APLogger.setPlugin(this);
		APLogger.info("Initialising Plugin...");
		isEnabled = true;
		
		/* Die Config initialisieren */
		APLogger.info("> Loading Config...");
		initializeConfig();
		
		/* Die Datenbank initialisieren */
		APLogger.info("> Loading Database...");
		initializeDatabase();
		
		/* Die EntityList initialisieren */
		APLogger.info("> Loading EntityList...");
		list = new EntityList(this, false);
		
		/* Die Befehle initialisieren */
		APLogger.info("> Loading Commands...");
		this.getCommand("lockanimal").setExecutor(new lockanimal(this));
		this.getCommand("unlockanimal").setExecutor(new unlockanimal(this));
		this.getCommand("lockinfo").setExecutor(new lockinfo(this));
		this.getCommand("locklist").setExecutor(new locklist(this));
		this.getCommand("lockrespawn").setExecutor(new lockrespawn(this));
		this.getCommand("locktp").setExecutor(new locktp(this));
		this.getCommand("lockdebug").setExecutor(new lockdebug(this));
		this.getCommand("locklimit").setExecutor(new locklimit(this));
		this.getCommand("ap").setExecutor(new ap(this));
		
		/* Die Listener registrieren */
		APLogger.info("> Loading Listeners...");
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new InteractEventListener(this), this);
		pm.registerEvents(new DamageEventListener(this), this);
		pm.registerEvents(new LeashEventListener(this), this);
		pm.registerEvents(new LoadSaveEventListener(this), this);
		pm.registerEvents(new DeathEventListener(this), this);
		pm.registerEvents(new VehicleEventListener(this), this);
		pm.registerEvents(new TargetEventListener(this), this);
		
		/* Den Server informieren */
		APLogger.info("Plugin initialized successfully.");
	}
	
	@Override
	public void onDisable() {
		if (list != null) { list.unload(); }
		if (database != null) { database.closeConnection(); }
		if (connection != null) { try { connection.close(); } 
		catch (SQLException e) { e.printStackTrace(); } }
		
		this.list = null;
		this.database = null;
		this.connection = null;
		
		APLogger.info("Plugin has been disabled.");
	}
	
	public void initializeConfig() {
		try {
			getConfig().options().copyDefaults(true);
			saveConfig();
			
			hostname = getConfig().getString("database.hostname");
			username = getConfig().getString("database.username");
			dbname = getConfig().getString("database.dbname");
			password = getConfig().getString("database.password");
			port = getConfig().getString("database.port");
			DEBUGMODE = getConfig().getBoolean("settings.debug-messages");
		} catch (Exception e) {
			APLogger.warn("Warnung: Die Config konnte nicht geladen werden!");
			e.printStackTrace();
			isEnabled = false;
		}
	}
	
	public void initializeDatabase() {
		try {
			try { database.closeConnection(); } catch (Exception e) { }
			database = new MySQL(this, hostname, port, username, dbname, password, true);
			connection = database.openConnection();
		} catch (Exception e) {
			APLogger.warn("Warnung: Die Datenbank konnte nicht initialisiert werden!");
			e.printStackTrace();
			isEnabled = false;
		}
		
		if (database != null) {
			if (database.checkConnection()) {
				String Query = "";
				
				/* Erstelle die Tabelle 'ap_owners' */
				Query = "CREATE TABLE IF NOT EXISTS ap_owners ("
						+ "id INT AUTO_INCREMENT PRIMARY KEY, "
						+ "name varchar(255));";
				database.write(Query, false);
				
				/* Erstelle die Tabelle 'ap_locks' */
				Query = "CREATE TABLE IF NOT EXISTS ap_locks ("
						+ "id INT AUTO_INCREMENT PRIMARY KEY, "
						+ "owner_id INT, "
						+ "entity_id INT, "
						+ "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
				database.write(Query, false);
				
				/* Erstelle die Tabelle 'ap_entities' */
				Query = "CREATE TABLE IF NOT EXISTS ap_entities ("
						+ "id INT AUTO_INCREMENT PRIMARY KEY, "
						+ "uuid VARCHAR(40), "
						+ "last_x SMALLINT(5) NOT NULL, "
						+ "last_y SMALLINT(3) UNSIGNED NOT NULL, "
						+ "last_z SMALLINT(5) NOT NULL, "
						+ "animaltype ENUM('cow', 'chicken', 'pig', 'sheep', 'horse', 'wolf'), "
						+ "nametag TEXT, "
						+ "maxhp DOUBLE, "
						+ "alive BOOL, "
						+ "deathcause TEXT, "
						+ "color TEXT, "
						+ "armor ENUM('DIAMOND', 'GOLD', 'IRON', 'UNKNOWN'), "
						+ "horse_jumpstrength DOUBLE, "
						+ "horse_style TEXT, "
						+ "horse_variant ENUM('NONE', 'HORSE', 'DONKEY', 'MULE', 'SKELETON_HORSE', 'UNDEAD_HORSE'));";
				database.write(Query, false);
			}
		}
	}


} 
