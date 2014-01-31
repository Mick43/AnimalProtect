package de.Fear837.structs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import de.Fear837.Main;
import de.Fear837.MySQL;

public class EntityList {
	
	/** The plugin using this list */
	private Main plugin;
	/** The database to get data from */
	private MySQL database;

	/** All loaded Entities of the Database **/
	private ArrayList<EntityObject> EntityList;
	
	/** Maps player to all his entities */
	private HashMap<Player, ArrayList<EntityObject>> keys;
	
	/** The maximum allowed saved entities for one player */
	private int MAX_ENTITIES_FOR_PLAYER = 0;
	/** If true, debug-messages will be displayed at the console. */
	private boolean DEBUGGING = false;
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
		this.EntityList = new ArrayList<EntityObject>();
		this.keys = new HashMap<Player, ArrayList<EntityObject>>();

		if (!empty) {
			for (Player player : plugin.getServer().getOnlinePlayers()) {
				// TODO connect(player);
			}
		}

		MAX_ENTITIES_FOR_PLAYER = plugin.getConfig().getInt("settings.max_entities_for_player");
		DEBUGGING = plugin.getConfig().getBoolean("settings.debug-messages");
		this.lastActionSuccess = false;
	}
	
	/**
	 * Returns the amount of entities, which are active in RAM. 
	 * 
	 * @return Amount of entities in RAM.
	 */
	public int sizeOfEntities() {
		return EntityList.size();
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
	public boolean containsEntity(Entity entity) {
		if (entity instanceof Player) {
			return containsPlayer((Player) entity); }
		else {
			EntityObject e = new EntityObject(plugin, database, entity.getUniqueId(), false);
			for (EntityObject item : EntityList) {
				if (item.getEntity_uuid() == entity.getUniqueId().toString()) {
					return true;
				}
			}
		}
		return false;
	}
}
