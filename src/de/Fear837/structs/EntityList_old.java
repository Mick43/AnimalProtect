package de.Fear837.structs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.ItemStack;

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
public class EntityList_old {

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
	public EntityList_old(Main plugin) {
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
	public EntityList_old(Main plugin, boolean empty) {
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
		if (contains(player)) {
			return keys.get(player);
		}
		
		else { // Wenn nicht im RAM, dann suche in der Datenbank
			ResultSet result_PlayerID = database.get("SELECT id FROM ap_owners WHERE name='" + player.getName() + "';", true, true);
			Integer ownerid = null;
			if (result_PlayerID != null) {
				try { ownerid = result_PlayerID.getInt("id"); }
				catch (SQLException e1) { e1.printStackTrace(); }
			}
			else { return null; }
			
			ResultSet result_Entities = database.get("SELECT entity_id FROM ap_locks WHERE owner_id=" + ownerid + ";", false, true);
			if (result_Entities != null) {
				try {
					ArrayList<UUID> returnList = new ArrayList<UUID>();
					long rows = database.getRowCount("SELECT COUNT(*) FROM ap_locks WHERE owner_id=" + ownerid);
					
					for (int i = 0; i<rows; i++) {
						if (result_Entities.next()) {
							int id = result_Entities.getInt("entity_id");
							ResultSet result = database.get("SELECT uuid FROM ap_entities WHERE id=" + id + ";", true, false);
							// TODO INNER-JOIN anwenden um sich die vielen SQL-Befehle zu ersparen
							if (result != null) { returnList.add(UUID.fromString(result.getString("uuid"))); }
						}
					}
					if (!returnList.isEmpty()) {
						connect(player); // Spieler war nicht in der Liste, also wird er hinzugefuegt.
						return returnList;
					}
				}
				catch (SQLException e) { e.printStackTrace(); }
			}
			else { }
		}
		return null;
	}

	/**
	 * Returns the player, who locked a given Entity
	 * 
	 * @param entity
	 *            The entity searching for its owner
	 * @return Player, who locked the entity
	 */
	public Player get(Entity entity) {
		if (contains(entity))  {
			return reverseKeys.get(entity.getUniqueId());
		}
		else {
			ResultSet result_EntityID = database.get("SELECT id FROM ap_entities WHERE uuid='" + entity.getUniqueId() + "';", true, true);
			if (result_EntityID == null) { return null; }
			
			Integer entityID = null;
			try { entityID = result_EntityID.getInt("id"); } catch (SQLException e) { return null; }
			
			if (entityID != null) {
				ResultSet result_OwnerID = database.get("SELECT owner_id WHERE entity_id =" + entityID + ";", true, true);
				if (result_OwnerID == null) { plugin.getLogger().warning("Fehler: EntityList.get.ownerID == null"); return null; }
				
				Integer ownerID = null;
				try { ownerID = result_OwnerID.getInt("owner_id"); } catch (SQLException e) { return null; }
				
				if (ownerID != null) {
					Player player = null;
					
					ResultSet result = database.get("SELECT name WHERE id =" + ownerID + ";", true, true);
					// TODO INNER-JOIN anwenden um sich die vielen SQL-Befehle zu ersparen
					if (result == null) { plugin.getLogger().warning("Fehler: EntityList.get.result == null"); return null; }
					
					String playerName = null;
					try { playerName = result.getString("name"); } catch (SQLException e) { return null; }
					
					if (plugin.getServer().getPlayer(playerName) != null) {
						player = plugin.getServer().getPlayer(playerName);
					}
					else { 
						player = (Player) plugin.getServer().getOfflinePlayer(playerName); 
						}
					return player;
				}
			}
		}
		
		return null;
	}

	/**
	 * Returns some information about an entity
	 * 
	 * @param id
	 *            The UUID of the entity
	 * @return ArrayList<String>
	 */
	public ArrayList<Object> get(UUID id) {
		ArrayList<Object> returnList = new ArrayList<Object>();
		returnList.add(0); // last_x
		returnList.add(0); // last_y
		returnList.add(0); // last_z
		returnList.add("UnkownType"); // type
		returnList.add(""); // nametag
		returnList.add("maxhp"); // last_x
		returnList.add(false); // alive
		
		for (Entity entity : plugin.getServer().getWorld(plugin.getConfig().getString("settings.worldname")).getEntities()) {
			if (entity.getUniqueId().equals(id)) {
				if (plugin.getConfig().getBoolean("settings.debug-messages")) { plugin.getLogger().info("/locklist: Found an entity in ram!"); }
				returnList.set(0, entity.getLocation().getBlockX());
				returnList.set(1, entity.getLocation().getBlockY());
				returnList.set(2, entity.getLocation().getBlockZ());
				returnList.set(3, entity.getType().toString());
				if (!entity.isDead()) { returnList.set(4, (((LivingEntity) entity).getCustomName())); }
				if (!entity.isDead()) { returnList.set(5, (((LivingEntity) entity).getMaxHealth())); }
				returnList.set(6, !entity.isDead());
				return returnList;
			}
		}
		
		ResultSet rs = database.get("SELECT * FROM ap_entities WHERE uuid='" + id + "';", true, true);
		if (rs != null) {
			try {
				returnList.set(0, rs.getInt("last_x"));
				returnList.set(1, rs.getInt("last_y"));
				returnList.set(2, rs.getInt("last_z"));
				returnList.set(3, rs.getString("animaltype"));
				returnList.set(4, rs.getString("nametag"));
				returnList.set(5, rs.getDouble("maxhp"));
				returnList.set(6, false);
				
				database.write("UPDATE ap_entities SET alive=FALSE WHERE uuid='" + id + "';");
			} 
			catch (SQLException e) { e.printStackTrace(); }
			return returnList;
		}
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
	public EntityList_old lock(Player player, Entity entity) {
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
	    	
	    	UUID id = entity.getUniqueId();
	    	Integer x = entity.getLocation().getBlockX();
	    	Integer y = entity.getLocation().getBlockY();
	    	Integer z = entity.getLocation().getBlockZ();
	    	String type = entity.getType().toString();
	    	String nametag = "";
	    	Double maxhp = 10.0;
	    	Boolean alive = true;
	    	String color = "";
	    	String armor = "";
	    	Double jumpstrength = 10.0;
	    	String style = "";
	    	String variant = "NONE";
	    	
	    	try { nametag = ((LivingEntity) entity).getCustomName(); } catch (Exception e) { }
	    	try { maxhp = ((LivingEntity) entity).getMaxHealth(); } catch (Exception e)  { }
	    	try { alive = !entity.isDead(); } catch (Exception e)  { }
	    	try { color = ((Horse) entity).getColor().toString(); } catch (Exception e)  { }
	    	try { color = ((Wolf) entity).getCollarColor().toString(); } catch (Exception e)  { }
	    	try { color = ((Sheep) entity).getColor().toString(); } catch (Exception e) { }
	    	try {
	    		ItemStack itemArmor = ((Horse) entity).getInventory().getArmor();
	    		if (itemArmor.getType() == Material.DIAMOND_BARDING) { armor = "diamond"; }
	    		else if (itemArmor.getType() == Material.IRON_BARDING) { armor = "iron"; }
	    		else if (itemArmor.getType() == Material.GOLD_BARDING) { armor = "gold"; }
	    		else { armor = "unknown"; }
	    	} catch (Exception e)  { }
	    	try { jumpstrength = ((Horse) entity).getJumpStrength(); } catch (Exception e)  { }
	    	try { style = ((Horse) entity).getStyle().toString(); } catch (Exception e)  { }
	    	try { variant = ((Horse) entity).getVariant().toString(); } catch (Exception e)  { }
	    	
	    	try { nametag = nametag.replaceAll("'", ""); } catch (Exception e1) { }
	    	
	    	database.write("INSERT INTO ap_entities (`uuid`, `last_x`, `last_y`, `last_z`, `animaltype`, `nametag`, "
	    			+ "`maxhp`, `alive`, `color`, `armor`, `horse_jumpstrength`, `horse_style`, `horse_variant`) "
	    			+ "VALUES ('"
	    			+ id + "', "
	    			+ x + ", "
	    			+ y + ", "
	    			+ z + ", '"
	    			+ type + "', '"
	    			+ nametag + "', "
	    			+ maxhp + ", "
	    			+ alive + ", '"
	    			+ color + "', '"
	    			+ armor + "', "
	    			+ jumpstrength + ", '"
	    			+ style + "', '"
	    			+ variant + "'"
	    			+ ");");
			
			Integer animalid = null;
			ResultSet result_AnimalID = database.get("SELECT id FROM ap_entities WHERE uuid='" + entity.getUniqueId() + "';", true, true);
			if (result_AnimalID != null) {
				try { animalid = result_AnimalID.getInt("id"); }
				catch (SQLException e) {
					plugin.getLogger().info("Exception at EntityList.lock().animalid = rs.getInt(id)");
					e.printStackTrace();
					animalid = null;
				}
			}
			else {
				plugin.getLogger().warning("Warning: Die ID des gerade eingetragenen Tieres konnte nicht gefunden werden.");
				plugin.getLogger().warning("EntityList.lock().rs == null");
			}
			
			Integer playerid = null;
			ResultSet result_PlayerID = database.get("SELECT id FROM ap_owners WHERE name='" + player.getName() + "';", true, true);
			if (result_PlayerID != null) {
				try { playerid = result_PlayerID.getInt("id"); }
				catch (SQLException e) {
					plugin.getLogger().info("Exception at EntityList.lock().animalid = rs.getInt(id)");
					e.printStackTrace();
					playerid = null;
				}
			}
			else {
				plugin.getLogger().warning("Warning: Die ID des gerade eingetragenen Spielers konnte nicht gefunden werden.");
				plugin.getLogger().warning("EntityList.lock().rs == null");
			}
			
			if (playerid != null && animalid != null) {
				database.write("INSERT INTO ap_locks (`entity_id`, `owner_id`) VALUES (" + animalid + ", " + playerid + ");");
				// TODO INNER-JOIN Zaubertrick hier anwenden um Animalid und playerid zu bekommen.
			}
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
	public EntityList_old unlock(Entity entity) {
		this.lastActionSuccess = false;
		if (contains(entity)) {
			Player owner = get(entity);

			reverseKeys.remove(entity.getUniqueId());
			keys.get(owner).remove(entity.getUniqueId());
			
			database.write("DELETE FROM ap_locks WHERE entity_id='" + entity.getUniqueId() +"';");
			database.write("DELETE FROM ap_entities WHERE uuid='" + entity.getUniqueId() + "';");
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
	public EntityList_old connect(Player player) {
		int sizeE = sizeOfEntities();
		int sizeP = sizeOfPlayers();
		this.lastActionSuccess = true;
		if (contains(player)) {
			this.lastActionSuccess = false;
			return this;
		}
		
		if (database == null) { this.lastActionSuccess = false; return this; }
		
		keys.put(player, new ArrayList<UUID>()); // Der Spieler ist nicht in der keysliste. Also hinzufügen.
		
		ResultSet result_PlayerID = database.get("SELECT id FROM ap_owners WHERE name='" + player.getName() + "';", true, true);
		Integer ownerid = null;
		if (result_PlayerID != null) {
			try { ownerid = result_PlayerID.getInt("id"); }
			catch (SQLException e1) { e1.printStackTrace(); }
		}
		else { database.write("INSERT INTO ap_owners (`name`) VALUES ('" + player.getName() + "')"); }
		
		ResultSet result_Entities = database.get("SELECT entity_id FROM ap_locks WHERE owner_id=" + ownerid + ";", false, true);
		if (result_Entities != null) {
			try {
				long rows = database.getRowCount("SELECT COUNT(*) FROM ap_locks WHERE owner_id=" + ownerid);
				
				for (int i = 0; i<rows; i++) {
					if (result_Entities.next()) {
						int id = result_Entities.getInt("entity_id");
						ResultSet result = database.get("SELECT uuid FROM ap_entities WHERE id=" + id + ";", true, false);
						// TODO INNER-JOIN anwenden um sich die vielen SQL-Befehle zu ersparen
						if (result != null) { 
							UUID uuid = UUID.fromString(result.getString("uuid"));
							keys.get(player).add(uuid);
							reverseKeys.put(uuid, player);
						}
					}
				}
			}
			catch (SQLException e) { e.printStackTrace(); }
		}
		
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
	public EntityList_old disconnect(Player player) {
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
	public EntityList_old disconnectAll() {
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
		if (!keys.containsKey(player)) {
			this.lastActionSuccess = false;
			return;
		}
		
		ArrayList<UUID> list = keys.get(player);
		for (Entity ent : player.getWorld().getEntities()) {
			for (UUID id : list) {
				if (ent.getUniqueId() == id) {
					// TODO Alle UPDATES in einem SQL-Befehl schreiben
					database.write("UPDATE ap_entities SET last_x=" + ent.getLocation().getBlockX() + " WHERE uuid='" + id + "')");
					database.write("UPDATE ap_entities SET last_y=" + ent.getLocation().getBlockY() + " WHERE uuid='" + id + "')");
					database.write("UPDATE ap_entities SET last_z=" + ent.getLocation().getBlockZ() + " WHERE uuid='" + id + "')");
					database.write("UPDATE ap_entities SET alive=" + ent.isDead() + " WHERE uuid='" + id + "')");
				}
			}
		}
		
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
