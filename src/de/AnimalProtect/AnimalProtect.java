package de.AnimalProtect;

/* Java Imports */
import java.util.UUID;


/* Bukkit Imports */
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;


/* AnimalProtect - Command Imports */
import de.AnimalProtect.commands.Command_AnimalProtect;
import de.AnimalProtect.commands.Command_debuganimal;
import de.AnimalProtect.commands.Command_listanimals;
import de.AnimalProtect.commands.Command_lockanimal;
import de.AnimalProtect.commands.Command_lockedanimals;
import de.AnimalProtect.commands.Command_respawnanimal;
import de.AnimalProtect.commands.Command_tpanimal;
import de.AnimalProtect.commands.Command_unlockanimal;

/* AnimalProtect - Listener Imports */
import de.AnimalProtect.listeners.InteractEventListener;
import de.AnimalProtect.listeners.DamageEventListener;

public class AnimalProtect extends JavaPlugin {
	
	private static Database database;
	private static Boolean debugmode;
	
	public static AnimalProtect plugin;
	
	@Override
	public void onEnable() {
		/* Konsole benachrichtigen */
		Messenger.log("Initializing AnimalProtect...");
		
		/* Statische Instanz intialisieren */
		AnimalProtect.plugin = this;
		
		/* Die Config laden */
		initializeConfig();
		
		/* Die Datenbank laden */
		initializeDatabase();
		
		/* Die Listener initialisieren */
		initializeListeners();
		
		/* Die Commands laden */
		initializeCommands();
	}
	
	@Override
	public void onDisable() {
		AnimalProtect.getDatenbank().closeConnection();
	}
	
	private void initializeConfig() {
		Messenger.log("  Loading Config...");
		
		try {
			getConfig().options().copyDefaults(true);
			saveConfig();
			
			AnimalProtect.debugmode = getConfig().getBoolean("settings.debug-messages");
		}
		catch (Exception e) { 
			Messenger.exception("AnimalProtect.java/initializeConfig", "Failed to load the config file!", e);
		}
	}
	
	private void initializeDatabase() {
		Messenger.log(" Loading Database...");
		
		AnimalProtect.database = new Database(this);
	}
	
	private void initializeListeners() {
		try {
			this.getServer().getPluginManager().registerEvents(new InteractEventListener(this), this);
			this.getServer().getPluginManager().registerEvents(new DamageEventListener(this), this);
		}
		catch (Exception e) {
			
		}
		// TODO: InitializeListeners
	}
	
	private void initializeCommands() {
		try {
			this.getCommand("ap").setExecutor(new Command_AnimalProtect(this));
			this.getCommand("debuganimal").setExecutor(new Command_debuganimal(this));
			this.getCommand("listanimals").setExecutor(new Command_listanimals(this));
			this.getCommand("lockanimal").setExecutor(new Command_lockanimal(this));
			this.getCommand("lockedanimals").setExecutor(new Command_lockedanimals(this));
			this.getCommand("respawnanimal").setExecutor(new Command_respawnanimal(this));
			this.getCommand("tpanimal").setExecutor(new Command_tpanimal(this));
			this.getCommand("unlockanimal").setExecutor(new Command_unlockanimal(this));
		}
		catch (Exception e) {
			Messenger.exception("AnimalProtect.java/initializeCommands", "Failed to initialize some commands.", e);
		}
	}
	
	/**
	 * Gibt aus, ob das Plugin im Debug-Modus ist, oder nicht.
	 * @return True, falls Debugging aktiviert ist.
	 */
	public static boolean isDebugging() {
		return debugmode;
	}
	
	/**
	 * Gibt aus, ob das übergebene Entity ein Tier ist, welches man mit AnimalProtect sichern kann.
	 * @param entity - Das Entity welches überprüft wird.
	 * @return True, falls der EntityType ein AnimalType ist.
	 */
	public static boolean isAnimal(Entity entity) {
		EntityType type = entity.getType();
		if (type == EntityType.SHEEP
		||  type == EntityType.PIG
		||  type == EntityType.COW
		||  type == EntityType.CHICKEN
		||  type == EntityType.HORSE
		||  type == EntityType.WOLF
		||  type == EntityType.IRON_GOLEM
		||  type == EntityType.SNOWMAN
		||  type == EntityType.VILLAGER
		||  type == EntityType.OCELOT)
		{ return true; }
		return false;
	}
	
	/**
	 * Gibt das Entity zurück, welches von dem Spieler ausgewählt wurde.
	 * @param uuid - Die UniqueID des Spielers.
	 * @return Das Entity, welches ausgewählt wurde, oder null, falls keins ausgewählt wurde.
	 */
	public static Entity getSelectedAnimal(UUID uuid) {
		return InteractEventListener.getSelected(uuid);
	}
	
	/**
	 * Gibt die Datenbank von AnimalProtect wieder.
	 * @return Das Database-Objekt von AnimalProtect.
	 */
	public static Database getDatenbank() {
		return database;
	}
}
