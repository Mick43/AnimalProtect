package de.Fear837.structs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import de.Fear837.Main;
import de.Fear837.MySQL;

/**
 * Represents a list of entities (of predefined type: animal) foreach player and
 * stores the data into the database configurated in the bukkit-config.
 * 
 * @author Pingebam
 * @author Fear837
 * @version 0.2
 */
public class EntityList {

	/** The plugin using this list */
	private Main plugin;
	/** The database to get data from */
	private MySQL database;

	// TODO Fill on queries, SELECTS, INSERTS, UPDATES, DELETES!
	/**
	 * All loaded data as stored in database, to check quicker for changes.
	 * Object[n] is the complete data for one player:<br>
	 * 0 - Primary key for player<br>
	 * 1 - Name of player
	 */
	private ArrayList<Object[]> memoryPlayers;
	/**
	 * All loaded data as stored in database, to check quicker for changes.
	 * Object[n] is the complete data for one entity:<br>
	 * 0 - Primary key for entity<br>
	 * 1 - UUID of entity<br>
	 * 2 - last_x<br>
	 * 3 - last_y<br>
	 * 4 - last_z
	 */
	private ArrayList<Object[]> memoryEntities;
	/**
	 * All loaded data as stored in database, to check quicker for changes.
	 * Object[n] is the complete data for one lock:<br>
	 * 0 - Primary key for lock<br>
	 * 1 - player_id<br>
	 * 2 - entity_id<br>
	 * 3 - created_at
	 */
	private ArrayList<Object[]> memoryLocks;

	/** Maps player to all his entities */
	private HashMap<Player, ArrayList<UUID>> keys;
	/** Reverse Maps an entity to his owner */
	private HashMap<UUID, Player> reverseKeys;

	/** The maximum allowed saved entities for one player */
	private static int MAX_ENTITIES_FOR_PLAYER = 0;
	/** If true, debug-messages will be displayed at the console. */
	private static boolean DEBUGGING = false;
	/** Stores the last Method call success status */
	private boolean lastActionSuccess;

	/**
	 * Constructor initializing the maps. The list is empty on start.
	 * 
	 * @param plugin
	 *            The plugin using this list.
	 */
	public EntityList(Main plugin) {
		this(plugin, true);
	}

	/**
	 * Constructor initializing the maps. The list will be filled with online
	 * players' data, if <tt>empty</tt> is </tt>true</tt>.
	 * 
	 * @param plugin
	 *            The plugin using this list.
	 * @param empty
	 *            If <tt>false</tt> the list will be filled at start with the
	 *            players, who are online, otherwise it's empty.
	 */
	public EntityList(Main plugin, boolean empty) {
		this.plugin = plugin;
		this.database = plugin.getMySQL();
		this.memoryPlayers = new ArrayList<Object[]>();
		this.memoryEntities = new ArrayList<Object[]>();
		this.memoryLocks = new ArrayList<Object[]>();
		this.keys = new HashMap<Player, ArrayList<UUID>>();
		this.reverseKeys = new HashMap<UUID, Player>();

		if (!empty) {
			for (Player player : plugin.getServer().getOnlinePlayers()) {
				connect(player);
			}
		}

		MAX_ENTITIES_FOR_PLAYER = plugin.getConfig().getInt(
				"settings.max_entities_for_player");
		DEBUGGING = plugin.getConfig().getBoolean("settings.debug-messages");
		this.lastActionSuccess = false;
		// TODO
	}

	/**
	 * Returns the amount of entities, which are active in RAM. Alias for
	 * sizeOfEntites().
	 * 
	 * @return Amount of entities in RAM.
	 */
	public int size() {
		return reverseKeys.size();
	}

	/**
	 * Returns the amount of entities, which are active in RAM and locked.
	 * 
	 * @return Amount of entities in RAM.
	 */
	public int sizeOfEntities() {
		return size();
	}

	/**
	 * Returns the amount of entities locked by a player.
	 * 
	 * @param player
	 *            The player as the owner
	 * @return Amount of entities for a player, returns 0 if player isn't active
	 *         in RAM.
	 */
	public int sizeOfEntities(Player player) {
		if (!containsPlayer(player)) {
			return 0;
		}
		return keys.get(player).size();
	}

	/**
	 * Returns the amount of players, which are active in RAM.
	 * 
	 * @return Amount of players in RAM.
	 */
	public int sizeOfPlayers() {
		return keys.size();
	}

	/**
	 * Checks if a player is already active in RAM.
	 * 
	 * @param player
	 *            The player to check for
	 * @return <tt>true</tt> if <tt>player</tt> is active in RAM.
	 */
	public boolean containsPlayer(Player player) {
		return keys.containsKey(player);
	}

	/**
	 * Checks if an entity is already active in RAM and locked.
	 * 
	 * @param entity
	 *            The entity to check for
	 * @return <tt>true</tt> if <tt>entity</tt> is active in RAM and locked.
	 */
	public boolean contains(Entity entity) {
		if (entity instanceof Player)
			return containsPlayer((Player) entity);
		return reverseKeys.containsKey(entity.getUniqueId());
	}

	/**
	 * Returns the list of entity-UUIDs of a given player
	 * 
	 * @param player
	 *            The player searching for as owner
	 * @return ArrayList&lt;UUID&gt; of entities locked by the player.
	 */
	public ArrayList<UUID> get(Player player) {
		if (!contains(player))
			return new ArrayList<UUID>();
		// TODO if not containing player, check for database
		return keys.get(player);
	}

	/**
	 * Returns the player, who locked a given Entity
	 * 
	 * @param entity
	 *            The entity searching for its owner
	 * @return Player, who locked the entity
	 */
	public Player get(Entity entity) {
		if (contains(entity))
			return reverseKeys.get(entity);
		// TODO if not containing, check for database
		return null;
	}

	/**
	 * Locks an entity for a player
	 * 
	 * @param player
	 *            The player to lock as owner
	 * @param entity
	 *            The entity to be locked
	 * @return EntityList after locking, if locking failed, returns an
	 *         unmodified version of the list.
	 * @see de.Fear837.structs.EntityList.lastActionSucceeded()
	 */
	public EntityList lock(Player player, Entity entity) {
		this.lastActionSuccess = true;
		if (reverseKeys.containsKey(entity.getUniqueId())) {
			this.lastActionSuccess = false;
			return this;
		}
		if (!contains(player)) {
			connect(player);
		}
		if (sizeOfEntities(player) >= MAX_ENTITIES_FOR_PLAYER) {
			this.lastActionSuccess = false;
		} else {
			keys.get(player).add(entity.getUniqueId());
			reverseKeys.put(entity.getUniqueId(), player);
		}
		return this;
	}

	/**
	 * Unlocks an entity
	 * 
	 * @param entity
	 *            The entity to be unlocked
	 * @return EntityList after unlocking, if unlocking failed, returns an
	 *         unmodified version of the list.
	 * @see de.Fear837.structs.EntityList.lastActionSucceeded()
	 */
	public EntityList unlock(Entity entity) {
		this.lastActionSuccess = false;
		if (contains(entity)) {
			Player owner = get(entity);

			reverseKeys.remove(entity.getUniqueId());
			keys.get(owner).remove(entity.getUniqueId());

			// TODO Einträge aus der Datenbank löschen
			// Dafür wird ein MySQL-Object in der Klasse benötigt.

			this.lastActionSuccess = true;
		}
		return this;
	}

	/**
	 * Loads a player and his locked entities from the database into RAM.
	 * 
	 * @param player
	 *            The player to be loaded.
	 * @return EntityList after loading player, returns the unmodified list if
	 *         the player was already loaded.
	 * @see de.Fear837.structs.EntityList.lastActionSucceeded()
	 */
	public EntityList connect(Player player) {
		int sizeE = sizeOfEntities();
		int sizeP = sizeOfPlayers();
		this.lastActionSuccess = true;
		if (contains(player)) {
			this.lastActionSuccess = false;
			return this;
		}

		// TODO Load from db and insert all entities.
		// Check if respawning is needed.
		// Minimal create new ArrayList<UUID> in HashMap for player.

		if (sizeOfEntities() == sizeE && sizeOfPlayers() == sizeP) {
			this.lastActionSuccess = false;
		}
		return this;
	}

	/**
	 * Saves a player and his locked entities to the database and removes the
	 * player from RAM.
	 * 
	 * @param player
	 *            The player to save and remove
	 * @return EntityList after removing the player, returns the unmodified
	 *         list, if the player wasn't loaded.
	 * @see de.Fear837.structs.EntityList.lastActionSucceeded()
	 */
	public EntityList disconnect(Player player) {
		int sizeE = sizeOfEntities();
		int sizeP = sizeOfPlayers();
		this.lastActionSuccess = true;
		if (!contains(player)) {
			this.lastActionSuccess = false;
			return this;
		}
		saveToDatabase(player);
		for (UUID id : keys.get(player)) {
			reverseKeys.remove(id);
		}
		keys.remove(player);
		if (sizeOfEntities() == sizeE && sizeOfPlayers() == sizeP) {
			this.lastActionSuccess = false;
		}
		return this;
	}

	/**
	 * Saves the list and all entities to the database and removes all players
	 * from RAM.
	 * 
	 * @return EntityList after removing all players, if list isn't empty
	 *         afterwards an error occured.
	 * @see de.Fear837.structs.EntityList.lastActionSucceeded()
	 */
	public EntityList disconnectAll() {
		this.lastActionSuccess = true;
		for (Entry<Player, ArrayList<UUID>> entry : keys.entrySet()) {
			disconnect(entry.getKey());
			if (DEBUGGING && !lastActionSucceeded()) {
				plugin.getLogger()
						.warning(
								"Disconnecting "
										+ entry.getKey().getName()
										+ " from EntityList failed, some data may be lost.");
				this.lastActionSuccess = false;
			}
		}
		if (sizeOfEntities() > 0 || sizeOfPlayers() > 0) {
			this.lastActionSuccess = false;
		}
		return this;
	}

	/**
	 * Saves all entity-locks for all players to the database.
	 * 
	 * @see de.Fear837.structs.EntityList.lastActionSucceeded()
	 */
	public void saveToDatabase() {
		// TODO
	}

	/**
	 * Saves the entity-locks for a player to the database.
	 * 
	 * @param player
	 *            The player to be saved
	 * @see de.Fear837.structs.EntityList.lastActionSucceeded()
	 */
	public void saveToDatabase(Player player) {
		// TODO
	}

	/**
	 * Returns if the last method call was successful.
	 * 
	 * @return <tt>true</tt> if last method call was successful, otherwise
	 *         <tt>false</tt>.<br>
	 *         If no method was called yet, returns <tt>false</tt>.
	 */
	public boolean lastActionSucceeded() {
		return this.lastActionSuccess;
	}

}
