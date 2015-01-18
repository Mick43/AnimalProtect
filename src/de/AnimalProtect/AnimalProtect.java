package de.AnimalProtect;

/* Java Imports */
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

/* Bukkit Imports */
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

/* CraftoPlugin Imports */
import craftoplugin.core.CraftoMessenger;
import craftoplugin.core.database.CraftoPlayer;
/* AnimalProtect - Command Imports */
import de.AnimalProtect.commands.Command_animalprotect;
import de.AnimalProtect.commands.Command_debug;
import de.AnimalProtect.commands.Command_info;
import de.AnimalProtect.commands.Command_limit;
import de.AnimalProtect.commands.Command_list;
import de.AnimalProtect.commands.Command_lock;
import de.AnimalProtect.commands.Command_queue;
import de.AnimalProtect.commands.Command_respawn;
import de.AnimalProtect.commands.Command_teleport;
import de.AnimalProtect.commands.Command_unlock;
import de.AnimalProtect.listeners.ChunkEventListener;
import de.AnimalProtect.listeners.DamageEventListener;
import de.AnimalProtect.listeners.DeathEventListener;
/* AnimalProtect - Listener Imports */
import de.AnimalProtect.listeners.InteractEventListener;
import de.AnimalProtect.listeners.LeashEventListener;
import de.AnimalProtect.listeners.SpawnEventListener;
import de.AnimalProtect.listeners.VehicleEventListener;
import de.AnimalProtect.structs.Animal;
import de.AnimalProtect.structs.AnimalType;

/**
 * Das AnimalProtect Plugin.
 *
 * @author Fear837, Pingebam
 * @version 1.2
 */
public class AnimalProtect extends JavaPlugin {

	/** Die Datenbank-Klasse um mit der Datenbank von AnimalProtect zu interagieren. */
	private Database database;
	/** Der Task, in dem die Queries in eine Queue geschrieben werden. */
	private QueueTask task;
	/** True, wenn der Debugmodus aktiv sein soll. */
	private Boolean debugmode;
	/** Eine Map an Tieren die von bestimmten Spielern ausgewählt wurden. */
	private HashMap<UUID, Entity> selectedList;
	/** Eine Map an Zeitpunkten, wann die Spieler ihr letztes Tier ausgewählt haben. */
	private HashMap<UUID, Long> selectedTime;

	/** Eine statische Instanz des Plugins. */
	public static AnimalProtect plugin;

	@Override
	public void onEnable() {
		/* Konsole benachrichtigen */
		Messenger.log("Initializing AnimalProtect...");

		/* Statische Instanz intialisieren */
		AnimalProtect.plugin = this;

		/* Die Config laden */
		this.initializeConfig();

		/* Die Datenbank laden */
		this.initializeDatabase();

		/* Den Task laden */
		this.initializeTask();

		/* Die Listener initialisieren */
		this.initializeListeners();

		/* Die Commands laden */
		this.initializeCommands();

		/* Konsole benachrichtigen */
		Messenger.log("AnimalProtect v" + this.getDescription().getVersion() + " has been enabled!");
	}

	@Override
	public void onDisable() {
		this.getDatenbank().closeConnection();
		this.getDatenbank().clear();
		this.selectedList.clear();
		this.selectedTime.clear();
	}

	/**
	 * Initialisiert die Config des Plugins.
	 */
	private void initializeConfig() {
		Messenger.log("Loading config...");

		try {
			this.getConfig().options().copyDefaults(true);
			this.saveConfig();

			this.debugmode = this.getConfig().getBoolean("settings.debug-messages");
		}
		catch (final Exception e) { Messenger.exception("AnimalProtect.java/initializeConfig", "Failed to load the config file!", e); }
	}

	/**
	 * Initialisiert die Datenbank.
	 */
	private void initializeDatabase() {
		Messenger.log("Loading database...");

		this.database = new Database(this);
	}

	/**
	 * Initialisiert alle Listener.
	 */
	private void initializeListeners() {
		Messenger.log("Loading listeners...");

		try {
			this.selectedList = new HashMap<UUID, Entity>();
			this.selectedTime = new HashMap<UUID, Long>();
			this.getServer().getPluginManager().registerEvents(new InteractEventListener(this, this.selectedList, this.selectedTime), this);
			this.getServer().getPluginManager().registerEvents(new DamageEventListener(this), this);
			this.getServer().getPluginManager().registerEvents(new DeathEventListener(this), this);
			this.getServer().getPluginManager().registerEvents(new LeashEventListener(this), this);
			this.getServer().getPluginManager().registerEvents(new VehicleEventListener(this), this);
			this.getServer().getPluginManager().registerEvents(new ChunkEventListener(this), this);

			if (this.getConfig().getBoolean("settings.enable-spawn-limit"))
			{ this.getServer().getPluginManager().registerEvents(new SpawnEventListener(this), this); }
		}
		catch (final Exception e) { Messenger.exception("AnimalProtect.java/initializeListeners", "Failed to initialize some listeners!", e); }
	}

	/**
	 * Initialisiert alle Kommandos.
	 */
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

			this.getCommand("ap").setPermissionMessage(this.getCommand("ap").getPermissionMessage().replaceAll("&", "§"));
			this.getCommand("animaldebug").setPermissionMessage(this.getCommand("animaldebug").getPermissionMessage().replaceAll("&", "§"));
			this.getCommand("animalqueue").setPermissionMessage(this.getCommand("animalqueue").getPermissionMessage().replaceAll("&", "§"));
			this.getCommand("listanimals").setPermissionMessage(this.getCommand("listanimals").getPermissionMessage().replaceAll("&", "§"));
			this.getCommand("lockanimal").setPermissionMessage(this.getCommand("lockanimal").getPermissionMessage().replaceAll("&", "§"));
			this.getCommand("lockedanimals").setPermissionMessage(this.getCommand("lockedanimals").getPermissionMessage().replaceAll("&", "§"));
			this.getCommand("respawnanimal").setPermissionMessage(this.getCommand("respawnanimal").getPermissionMessage().replaceAll("&", "§"));
			this.getCommand("tpanimal").setPermissionMessage(this.getCommand("tpanimal").getPermissionMessage().replaceAll("&", "§"));
			this.getCommand("listanimals").setPermissionMessage(this.getCommand("listanimals").getPermissionMessage().replaceAll("&", "§"));
			this.getCommand("unlockanimal").setPermissionMessage(this.getCommand("unlockanimal").getPermissionMessage().replaceAll("&", "§"));
			this.getCommand("animalinfo").setPermissionMessage(this.getCommand("animalinfo").getPermissionMessage().replaceAll("&", "§"));

			//if (Bukkit.getServer().getPluginManager().isPluginEnabled("Prism")) 
			//{ new PrismEventListener(this); }
		}
		catch (final Exception e) { Messenger.exception("AnimalProtect.java/initializeCommands", "Failed to initialize some commands.", e); }
	}

	/**
	 * Initialisiert den Queue-Task.
	 */
	private void initializeTask() {
		try {
			if (this.getConfig().getBoolean("settings.use-queue-task")) {
				Messenger.log("Loading task...");

				try {
					this.task = new QueueTask(this);
					this.task.start();
				}
				catch (final Exception e) { 
					Messenger.exception("AnimalProtect.java/initializeTask", "Failed to initialize the task.", e);
					try { this.task = new QueueTask(this); } catch (final Exception e1) { }
				}
			}
		}
		catch (final Exception e) { 
			Messenger.exception("AnimalProtect.java/initializeTask()", "Failed to initialize the queue task.", e); 
			try { this.task = new QueueTask(this); } catch (final Exception e1) { }
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
		return this.debugmode;
	}

	/**
	 * Gibt aus, ob das ï¿½bergebene Entity ein Tier ist, welches man mit AnimalProtect sichern kann.
	 * @param entity - Das Entity welches ï¿½berprï¿½ft wird.
	 * @return True, falls der EntityType ein AnimalType ist.
	 */
	public boolean isAnimal(final Entity entity) {
		final EntityType type = entity.getType();
		if (type == EntityType.SHEEP
				||  type == EntityType.PIG
				||  type == EntityType.COW
				||  type == EntityType.CHICKEN
				||  type == EntityType.HORSE
				||  type == EntityType.WOLF
				||  type == EntityType.IRON_GOLEM
				||  type == EntityType.SNOWMAN
				||  type == EntityType.VILLAGER
				||  type == EntityType.OCELOT
				||  type == EntityType.RABBIT)
		{ return true; }
		return false;
	}

	/**
	 * Gibt das Entity zurï¿½ck, welches von dem Spieler ausgewï¿½hlt wurde.
	 * @param uuid - Die UniqueID des Spielers.
	 * @return Das Entity, welches ausgewï¿½hlt wurde, oder null, falls keins ausgewï¿½hlt wurde.
	 */
	public Entity getSelectedAnimal(final UUID uuid) {
		return this.selectedList.get(uuid);
	}

	/**
	 * Prüft, ob der Spieler bereits ein Tier ausgewählt hat.
	 * @param uuid - Die UniqueID des Spielers.
	 * @return True, wenn der Spieler ein Tier ausgewählt hat.
	 */
	public Boolean playerHasSelection(final UUID uuid) {
		return this.selectedList.containsKey(uuid);
	}

	/**
	 * Gibt den Zeitpunkt wieder, an welchem der angegebene Spieler zuletzt ein Tier ausgewählt hat.
	 * @param uuid - Die UniqueID des Spielers.
	 * @return Den Zeitpunkt der letzten Selection als Long.
	 */
	public Long getLastSelection(final UUID uuid) {
		return this.selectedTime.get(uuid);
	}

	/**
	 * Gibt die Datenbank von AnimalProtect wieder.
	 * @return Das Database-Objekt von AnimalProtect.
	 */
	public Database getDatenbank() {
		return this.database;
	}

	/**
	 * Gibt den QueueTask zurück, der das Inserten/Updaten/Deleten von Tieren verarbeitet.
	 */
	public QueueTask getQueue() {
		return this.task;
	}

	/**
	 * Filtert Tiere, die den angegebenen Parametern entsprechen, raus und gibt sie zurück. </br>
	 * @param cs - Der CommandSender, der die Auflistung gestartet hat. </br>
	 * @param needsPlayer - True, wenn ein Spieler angegeben werden muss. </br>
	 * @param args - Die Parametern mit denen die Tiere gesucht werden. <br>
	 * Tiere die nicht den angegebenen Parametern entsprechen werden ignoriert. <br>
	 * </br>
	 * <b> Verfügbare Parameter: </b> <br>
	 * @param '<tt>p:[text]</tt>' - Der Name des Spielers dem die Tiere gehören. </br>
	 * @param '<tt>id:[zahl]</tt>' - Die ID des Tieres, oder wenn ein Spieler angegeben wurde das Tier mit der angegebenen Platzierung. </br>
	 * @param '<tt>type:[text]</tt>' - Die Art des Tieres. Alle erlaubte Tiertypen: @see AnimalType </br>
	 * @param '<tt>name:[text]</tt>' - Der Nametag des Tieres. </br>
	 * @param '<tt>-missing</tt>' - Filtert Tiere heraus die in der Datenbank als "Lebend" angegeben sind, aber in der Welt nicht gefunden werden konnte. </br>
	 * @param '<tt>-dead</tt>' - Filtert alle Tiere heraus die in der Datenbank bereits tot sind. </br>
	 * </br>
	 * @return {@literal ArrayList<Animal>} - Gibt eine sortierte Liste der herausgefilterten Tiere wieder. </br>
	 * </br>
	 * @see Animal
	 * @see AnimalType
	 */
	public ArrayList<Animal> parseAnimal(final CommandSender cs, final String[] args, final boolean needsPlayer) {
		if (args.length == 0) {
			Messenger.sendMessage(cs, "§cFehler: Keine Argumente angegeben!");
			Messenger.sendMessage(cs, "§cVerfügbare Parameter: 'p:', 't:', 'id:', 'name:', '-missing', '-dead'");
			return null;
		}

		String playerFlag = null;
		Integer idFlag = null;
		AnimalType typeFlag = null;
		String nameFlag = null;
		Boolean missingFlag = false;
		Boolean deadFlag = false;
		final ArrayList<Animal> returnList = new ArrayList<Animal>();

		for (final String arg : args) {
			if (arg.startsWith("p:"))
			{ playerFlag = arg.substring(2, arg.length()); }
			else if (arg.startsWith("id:")) {
				if (this.isNumber(arg.substring(3, arg.length())))
				{ idFlag = Integer.parseInt(arg.substring(3, arg.length())); }
			}
			else if (arg.startsWith("type:")) 
			{ typeFlag = AnimalType.valueOf(arg.substring(5, arg.length())); }
			else if (arg.startsWith("t:")) { 
				final String type = arg.substring(2, arg.length());
				if (AnimalType.contains(type)) { typeFlag = AnimalType.valueOf(type); }
			}
			else if (arg.startsWith("name:"))
			{ nameFlag = arg.substring(5, arg.length()); }
			else if (arg.startsWith("nametag:"))
			{ nameFlag = arg.substring(8, arg.length()); }
			else if (arg.startsWith("-missing"))
			{ missingFlag = true; }
			else if (arg.startsWith("-dead"))
			{ deadFlag = true; }
		}

		if (playerFlag == null) {
			if (idFlag != null && !needsPlayer) { returnList.add(this.database.getAnimal(idFlag)); }
			else if (needsPlayer) { Messenger.sendMessage(cs, "NO_GIVEN_PLAYER"); }
			else { CraftoMessenger.sendMessage(cs, "§cFehler: Es wurde kein Spieler und keine Tierid angegeben."); }
			return null;
		}
		else {
			final CraftoPlayer player = CraftoPlayer.getPlayer(playerFlag);

			if (player == null) { CraftoMessenger.sendMessage(cs, "PLAYER_NOT_FOUND"); return null; }
			else {
				final ArrayList<Animal> animals = this.database.getAnimals(player.getUniqueId());
				if (animals==null || animals.isEmpty()) { Messenger.sendMessage(cs, "ANIMALS_NOT_FOUND"); return null; }

				if (idFlag != null) {
					if (animals != null && !animals.isEmpty() && returnList != null) { returnList.add(animals.get(idFlag)); }
					else { Messenger.sendMessage(cs, "ANIMAL_NOT_FOUND"); return null; }
				}
				else {
					for (final Animal animal : animals) {
						boolean passed = true;
						if (typeFlag != null) 
						{ if (!animal.getAnimaltype().equals(typeFlag)) { passed = false; } }
						else if (nameFlag != null)
						{ if (!animal.getNametag().equals(nameFlag)) { passed = false; } }
						else if (missingFlag) {
							passed = true;
							for (final Entity e : Bukkit.getServer().getWorlds().get(0).getEntities()) {
								for (final Animal a : animals) {
									if (a.getUniqueId().equals(e.getUniqueId())) {
										passed = false;
									}
								}
							}
						}
						else if (deadFlag)
						{ if (animal.isAlive()) { passed = false; } }
						if (passed) { returnList.add(animal); }
					}
				}

				Collections.sort(returnList);
				return returnList;
			}
		}
	}

	/**
	 * @param value - Der übergebene Wert.
	 * @return True, wenn der übergebene Wert eine Zahl ist.
	 */
	private boolean isNumber(final String value) {
		try { Integer.parseInt(value); return true; }
		catch (final Exception e) { return false; }
	}
}