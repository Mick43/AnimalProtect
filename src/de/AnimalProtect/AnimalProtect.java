package de.AnimalProtect;

/* Java Imports */
import java.util.UUID;

/* Bukkit Imports */
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

/* AnimalProtect - Command Imports */
import de.AnimalProtect.commands.Command_animalprotect;
import de.AnimalProtect.commands.Command_debug;
import de.AnimalProtect.commands.Command_info;
import de.AnimalProtect.commands.Command_list;
import de.AnimalProtect.commands.Command_lock;
import de.AnimalProtect.commands.Command_limit;
import de.AnimalProtect.commands.Command_queue;
import de.AnimalProtect.commands.Command_respawn;
import de.AnimalProtect.commands.Command_teleport;
import de.AnimalProtect.commands.Command_unlock;
import de.AnimalProtect.listeners.DeathEventListener;

/* AnimalProtect - Listener Imports */
import de.AnimalProtect.listeners.InteractEventListener;
import de.AnimalProtect.listeners.DamageEventListener;
import de.AnimalProtect.listeners.LeashEventListener;
import de.AnimalProtect.listeners.PrismEventListener;
import de.AnimalProtect.listeners.VehicleEventListener;

/**
 * Das AnimalProtect Plugin
 *
 * @author Fear837, Pingebam
 * @version 1.2
 */
public class AnimalProtect extends JavaPlugin {

	private Database database;
	private QueueTask task;
	private Boolean debugmode;

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
		
		/* Den Task laden */
		initializeTask();

		/* Konsole benachrichtigen */
		Messenger.log("AnimalProtect v" + getDescription().getVersion() + " has been enabled!");
	}

	@Override
	public void onDisable() {
		this.getDatenbank().closeConnection();
		this.getDatenbank().clear();
		try { task.stop(); } catch (Exception e) { Messenger.log("Failed to stop the task."); }
		InteractEventListener.clearSelections();
	}

	private void initializeConfig() {
		Messenger.log("Loading config...");

		try {
			getConfig().options().copyDefaults(true);
			saveConfig();

			this.debugmode = getConfig().getBoolean("settings.debug-messages");
		}
		catch (Exception e) { Messenger.exception("AnimalProtect.java/initializeConfig", "Failed to load the config file!", e); }
	}

	private void initializeDatabase() {
		Messenger.log("Loading database...");

		this.database = new Database(this);
	}

	private void initializeListeners() {
		Messenger.log("Loading listeners...");

		try {
			this.getServer().getPluginManager().registerEvents(new DamageEventListener(this), this);
			this.getServer().getPluginManager().registerEvents(new DeathEventListener(this), this);
			this.getServer().getPluginManager().registerEvents(new InteractEventListener(this), this);
			this.getServer().getPluginManager().registerEvents(new LeashEventListener(this), this);
			this.getServer().getPluginManager().registerEvents(new VehicleEventListener(this), this);
		}
		catch (Exception e) { Messenger.exception("AnimalProtect.java/initializeListeners", "Failed to initialize some listeners!", e); }
	}

	private void initializeCommands() {
		Messenger.log("Loading commands...");

		try {
			this.getCommand("ap").setExecutor(new Command_animalprotect(this));
			this.getCommand("animaldebug").setExecutor(new Command_debug(this));
			this.getCommand("animalqueue").setExecutor(new Command_queue(this));
			this.getCommand("listanimals").setExecutor(new Command_list(this));
			this.getCommand("lockanimal").setExecutor(new Command_lock(this));
			this.getCommand("lockedanimals").setExecutor(new Command_limit(this));
			this.getCommand("respawnanimal").setExecutor(new Command_respawn(this));
			this.getCommand("tpanimal").setExecutor(new Command_teleport(this));
			this.getCommand("unlockanimal").setExecutor(new Command_unlock(this));
			this.getCommand("animalinfo").setExecutor(new Command_info(this));
			
			if (Bukkit.getServer().getPluginManager().isPluginEnabled("Prism")) {
				new PrismEventListener(this);
			}
		}
		catch (Exception e) { Messenger.exception("AnimalProtect.java/initializeCommands", "Failed to initialize some commands.", e); }
	}
	
	private void initializeTask() {
		if (this.getConfig().getBoolean("settings.use-queue-task")) {
			Messenger.log("Loading task...");
			
			try {
				this.task = new QueueTask(this);
				this.task.start();
			}
			catch (Exception e) { 
				Messenger.exception("AnimalProtect.java/initializeTask", "Failed to initialize the task.", e);
				try { this.task = new QueueTask(this); } catch (Exception e1) { }
			}
		}
	}

	/**
	 * Lï¿½dt die komplette Datenbank von AnimalProtect neu in den RAM.
	 */
	public void reloadDatabase() {
		this.getDatenbank().closeConnection();
		this.getDatenbank().clear();
		this.initializeDatabase();
	}

	/**
	 * Lï¿½dt die Config neu.
	 */
	public void reloadSettings() {
		this.initializeConfig();
	}

	/**
	 * Gibt aus, ob das Plugin im Debug-Modus ist, oder nicht.
	 * @return True, falls Debugging aktiviert ist.
	 */
	public boolean isDebugging() {
		return debugmode;
	}

	/**
	 * Gibt aus, ob das ï¿½bergebene Entity ein Tier ist, welches man mit AnimalProtect sichern kann.
	 * @param entity - Das Entity welches ï¿½berprï¿½ft wird.
	 * @return True, falls der EntityType ein AnimalType ist.
	 */
	public boolean isAnimal(Entity entity) {
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
	 * Gibt das Entity zurï¿½ck, welches von dem Spieler ausgewï¿½hlt wurde.
	 * @param uuid - Die UniqueID des Spielers.
	 * @return Das Entity, welches ausgewï¿½hlt wurde, oder null, falls keins ausgewï¿½hlt wurde.
	 */
	public Entity getSelectedAnimal(UUID uuid) {
		return InteractEventListener.getSelected(uuid);
	}

	/**
	 * Gibt die Datenbank von AnimalProtect wieder.
	 * @return Das Database-Objekt von AnimalProtect.
	 */
	public Database getDatenbank() {
		return database;
	}
	
	/**
	 * Gibt den QueueTask zurück, der das Inserten/Updaten/Deleten von Tieren verarbeitet.
	 */
	public QueueTask getQueue() {
		return task;
	}
}
