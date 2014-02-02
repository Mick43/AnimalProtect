package de.AnimalProtect;

import java.sql.Connection;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import de.AnimalProtect.commands.lockanimal;
import de.AnimalProtect.commands.lockdebug;
import de.AnimalProtect.commands.lockinfo;
import de.AnimalProtect.commands.locklist;
import de.AnimalProtect.commands.lockrespawn;
import de.AnimalProtect.commands.locktp;
import de.AnimalProtect.listener.DamageEventListener;
import de.AnimalProtect.listener.DeathEventListener;
import de.AnimalProtect.listener.InteractEventListener;
import de.AnimalProtect.listener.LeashEventListener;
import de.AnimalProtect.listener.LoadSaveEventListener;
import de.AnimalProtect.listener.VehicleEventListener;
import de.AnimalProtect.structs.EntityList;
import de.AnimalProtect.utility.APLogger;

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
		initializeConfig();
		
		/* Die Datenbank initialisieren */
		initializeDatabase();
		
		/* Die EntityList initialisieren */
		list = new EntityList(this);
		
		/* Die Befehle initialisieren */
		this.getCommand("lockanimal").setExecutor(new lockanimal(this));
		this.getCommand("lockinfo").setExecutor(new lockinfo(this));
		this.getCommand("locklist").setExecutor(new locklist(this));
		this.getCommand("lockrespawn").setExecutor(new lockrespawn(this));
		this.getCommand("locktp").setExecutor(new locktp(this));
		this.getCommand("lockdebug").setExecutor(new lockdebug(this));
		
		/* Die Listener registrieren */
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new InteractEventListener(this), this);
		pm.registerEvents(new DamageEventListener(this), this);
		pm.registerEvents(new LeashEventListener(this), this);
		pm.registerEvents(new LoadSaveEventListener(this), this);
		pm.registerEvents(new DeathEventListener(this), this);
		pm.registerEvents(new VehicleEventListener(this), this);
		
		/* Den Server informieren */
		APLogger.info("Plugin initialized successfully.");
	}
	
	@Override
	public void onDisable() {
		if (list != null) { list.saveToDatabase(); }
		if (database != null) { database.closeConnection(); }
		APLogger.info("Plugin has been disabled.");
	}
	
	private void initializeConfig() {
		try {
			getConfig().options().copyDefaults(true);
			saveConfig();
			
			hostname = getConfig().getString("database.hostname");
			username = getConfig().getString("database.username");
			dbname = getConfig().getString("database.dbname");
			password = getConfig().getString("database.password");
			port = getConfig().getString("database.port");
		} catch (Exception e) {
			APLogger.warn("Warnung: Die Config konnte nicht geladen werden!");
			e.printStackTrace();
			isEnabled = false;
		}
	}
	
	private void initializeDatabase() {
		try {
			database = new MySQL(this, hostname, port, username, dbname, password);
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
				database.write(Query);
				
				/* Erstelle die Tabelle 'ap_locks' */
				Query = "CREATE TABLE IF NOT EXISTS ap_locks ("
						+ "id INT AUTO_INCREMENT PRIMARY KEY, "
						+ "owner_id INT, "
						+ "entity_id INT, "
						+ "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
				database.write(Query);
				
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
						+ "color TEXT, "
						+ "armor ENUM('DIAMOND', 'GOLD', 'IRON', 'UNKNOWN'), "
						+ "horse_jumpstrength DOUBLE, "
						+ "horse_style TEXT, "
						+ "horse_variant ENUM('NONE', 'HORSE', 'DONKEY', 'MULE', 'SKELETON_HORSE', 'UNDEAD_HORSE'));";
				database.write(Query);
			}
		}
	}
}
