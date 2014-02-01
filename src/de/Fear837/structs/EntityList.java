package de.Fear837.structs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.ItemStack;

import de.Fear837.Main;
import de.Fear837.MySQL;
import de.Fear837.utility.APLogger;

public class EntityList {
	
	/** The plugin using this list */
	private Main plugin;
	/** The database to get data from */
	private MySQL database;

	/** All loaded Entities of the Database **/
	//private ArrayList<EntityObject> entities;
	
	/** Reverse Maps an entity to his owner */
	private HashMap<UUID, String> reverseKeys;
	/** Maps player to all his entities */
	private HashMap<String, ArrayList<EntityObject>> keys;
	/** Maps player to his ID **/
	private HashMap<String, Integer> players;
	/** Maps entity to his ID **/
	private HashMap<EntityObject, Integer> entities;
	
	/** The maximum allowed saved entities for one player */
	private long MAX_ENTITIES_FOR_PLAYER = 0;
	/** If true, debug-messages will be displayed at the console. */
	private boolean DEBUGGING = false;
	/** Returns the current World **/
	private World world = null;
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
		this.entities = new HashMap<EntityObject, Integer>();
		this.keys = new HashMap<String, ArrayList<EntityObject>>();
		this.reverseKeys = new HashMap<UUID, String>();
		this.players = new HashMap<String, Integer>();

		loadFromDatabase();
		
		try {
			world = plugin.getServer().getWorld(plugin.getConfig().getString("settings.worldname"));
		}
		catch (Exception e) { }

		MAX_ENTITIES_FOR_PLAYER = plugin.getConfig().getInt("settings.max_entities_for_player");
		DEBUGGING = plugin.getConfig().getBoolean("settings.debug-messages");
		this.lastActionSuccess = false;
	}
	
	/**
	 * Returns the amount of entities, which are active in RAM. 
	 * 
	 * @return Amount of entities in RAM.
	 */
	public int sizeOfEntitiesInRam() {
		return entities.size();
	}
	
	/**
	 * Returns the amount of entities locked by a player.
	 * 
	 * @param player
	 *            The player as the owner
	 * @return Amount of entities for a player, returns 0 if player isn't in Database
	 */
	public long sizeOfEntities(String player) {
		if (!containsPlayer(player)) { // Wenn der Spieler nicht in der Datenbank ist, dann return 0;
			return 0;
		}
		else if (keys.containsKey(player)) { // Wenn der Spieler in der Liste im RAM ist, dann return get(player).size();
			return keys.get(player).size();
		}
		else { // Wenn der Spieler nicht im RAM, sondern in der Datenbank ist, dann aus der Datenbank lesen.
			String query = "SELECT COUNT(id) FROM ap_locks WHERE owner_id=(" + players.get(player) + ");";
			Long count = (Long) database.getValue(query, "COUNT(ID)", true);
			if (count != null) {
				return count;
			}
		}
		
		return 0;
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
	 * Checks if a player is in database.
	 * 
	 * @param player
	 *            The player to check for
	 * @return <tt>true</tt> if <tt>player</tt> is in the database.
	 */
	public boolean containsPlayer(String player) {
		if (players.containsKey(player)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Checks if an entity is in the database.
	 * 
	 * @param entity
	 *            The entity to check for
	 * @return <tt>true</tt> if <tt>entity</tt> is in the database.
	 */
	public boolean containsEntity(Entity entity) {
		if (entity instanceof Player) {
			return containsPlayer(((Player) entity).getName()); }
		
		if (containsEntity(entity.getUniqueId())) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Checks if an entity is in the database.
	 * 
	 * @param id
	 *            The uniqueID of the entity.
	 * @return <tt>true</tt> if <tt>entity</tt> is in the database.
	 */
	public boolean containsEntity(UUID id) {
		if (reverseKeys.containsKey(id)) { // Wenn Spieler bereits im RAM, dann return true;
			return true;
		}
		else { // Wenn nicht, dann in der Datenbank nachschauen
			if (database != null) {
				if (database.checkConnection()) {
					String query = "SELECT COUNT(1) FROM ap_entities WHERE uuid='" + id + "' LIMIT 1;";
					Integer i = (Integer) database.getValue(query, "id", true);
					if (i != null) {
						if (i != 0) {
							return true;
						}
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Checks if an EntityObject is in the database
	 * 
	 * @param entity
	 *            The EntityObject to check for
	 * @return <tt>true</tt> if <tt>entity</tt> is in the database.
	 */
	public boolean containsEntity(EntityObject entity) {
		if (entities.containsKey(entity)) { return true; }
		else if (containsEntity(UUID.fromString(entity.getUniqueID()))) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Returns the player, who locked a given Entity
	 * 
	 * @param entity
	 *            The entity searching for its owner
	 * @return Player, who locked the entity
	 */
	public String getPlayer(Entity entity) {
		if (reverseKeys.containsKey(entity.getUniqueId())) {
			return reverseKeys.get(entity.getUniqueId());
		}
		else {
			String query = "SELECT name FROM ap_owners WHERE id=(SELECT owner_id FROM ap_locks WHERE entity_id=("
					+ "SELECT id FROM ap_entities WHERE uuid='" + entity.getUniqueId() + "'));";
			String playerName = (String) database.getValue(query, "name", true);
			if (playerName != null) {
				return playerName;
			}
		}
		return null;
	}
	
	/**
	 * Returns an EntityObject
	 * 
	 * @param entity
	 *            The Entity
	 * @return ArrayList<String>
	 */
	public EntityObject getEntityObject(UUID uniqueID) {
		if (!containsEntity(uniqueID)) { return null; }
		for (EntityObject e : entities.keySet()) {
			if (e.getUniqueID().equals(uniqueID)) {
				return e;
			}
		}
		
		String query = "SELECT id FROM ap_entities WHERE uuid='" + uniqueID + "';";
		Integer id = (Integer) database.getValue(query, "id", true);
		
		if (id != null) {
			EntityObject ent = new EntityObject(plugin, database, uniqueID, true);
			if (ent.isConnected()) {
				addToList(ent);
				return ent;
			}
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
	public EntityList lock(String player, Entity entity) {
		this.lastActionSuccess = true;
		if (reverseKeys.containsKey(entity)) {
			this.lastActionSuccess = false;
			return this;
		}
		
		int entitySize = (int) sizeOfEntities(player);
		
		if (entitySize >= MAX_ENTITIES_FOR_PLAYER) {
			this.lastActionSuccess = false;
			return this;
		}
		
		if (entitySize == 0) {
			connect(player);
		}
		
		if (database != null) {
			if (database.checkConnection()) {
				/* Das Entity in die Tabelle ap_entities schreiben */
				UUID id = entity.getUniqueId();
		    	Integer x = entity.getLocation().getBlockX();
		    	Integer y = entity.getLocation().getBlockY();
		    	Integer z = entity.getLocation().getBlockZ();
		    	String type = entity.getType().toString();
		    	String nametag = "";
		    	Double maxhp = 10.0;
		    	Boolean alive = true;
		    	String color = "";
		    	String armor = "UNKNOWN";
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
		    		if (itemArmor.getType() == Material.DIAMOND_BARDING) { armor = "DIAMOND"; }
		    		else if (itemArmor.getType() == Material.IRON_BARDING) { armor = "IRON"; }
		    		else if (itemArmor.getType() == Material.GOLD_BARDING) { armor = "GOLD"; }
		    		else { armor = "UNKNOWN"; }
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
		    	
		    	/* Nun den Eintrag in ap_locks erstellen */
		    	String query = "INSERT INTO ap_locks (`owner_id`, `entity_id`) VALUES ("
		    			+ "(" + players.get(player) + "), "
		    			+ "(SELECT id FROM ap_entities WHERE uuid='" + entity.getUniqueId().toString() + "'));";
		    	database.write(query);
		    	
		    	this.lastActionSuccess = true;
		    	if (DEBUGGING) {
		    		APLogger.info("Inserted new Entity: [ID:"+id+"] [Owner:"+player+"] [EntityType:"+type+"] [x:"+x+", y:"+y+", z:"+z+"]");
		    	}
				return this;
			}
		}
		
    	
    	this.lastActionSuccess = false;
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
	public EntityList unlock (Entity entity) {
		this.lastActionSuccess = false;
		if (containsEntity(entity)) {
			String owner = getPlayer(entity);
			
			for (EntityObject e : entities.keySet()) {
				if (e.equals(entity)) { entities.remove(e); }
			}
			reverseKeys.remove(entity.getUniqueId());
			keys.get(owner).remove(entity.getUniqueId());
			
			String query = "REMOVE FROM ap_locks WHERE entity_id=(SELECT id FROM ap_entities WHERE uuid='" + entity.getUniqueId() + "');";
			database.write(query);
		    query = "REMOVE FROM ap_entities WHERE uuid='" + entity.getUniqueId() + "';";
		    database.write(query);
			
			this.lastActionSuccess = true;
		}
		else { this.lastActionSuccess = false; }
		return this;
	}
	
	public void saveToDatabase() {
		// TODO saveToDatabase();
	}
	
	public void loadFromDatabase() {
		plugin.getLogger().info("Loading all players from Database... ");
		
		Long playerCounter = (Long) database.getValue("SELECT COUNT(1) FROM ap_owners;", 1, true);
		
		String query = "SELECT * FROM ap_owners;";
		ResultSet result = database.get(query, false, true);
		
		if (playerCounter != 0) {
			for (int i=0; i<playerCounter; i++) {
				try {
					if (result.next()) {
						String name = result.getString("name");
						Integer id = result.getInt("id");
						
						if (name != null && id != null) {
							if (!players.containsKey(name)) {
								players.put(name, id);
								
								if (!keys.containsKey(name)) {
									keys.put(name, new ArrayList<EntityObject>());
								}
							}
							else {
								plugin.getLogger().info("Warnung: Beim laden eines Spielers aus der Datenbank ist ein Fehler aufgetreten!");
								plugin.getLogger().info("Weitere Informationen: Spieler bereits in der Liste! [Name: " + name + "]");
							}
						}
					}
				} 
				catch (SQLException e) { }
			}
		}
	    
		plugin.getLogger().info("Loading finished! Added " + playerCounter + " players to the list.");
	}
	
	public EntityList connect(String player) {
		if (keys.containsKey(player)) { 
			this.lastActionSuccess = false;
			return this;
		}
		
		if (database != null) {
			if (database.checkConnection()) {
				if (containsPlayer(player)) { // Wenn der Spieler in der Datenbank existiert, dann alle locked Entities von ihm laden.
					 String query = "SELECT COUNT(*) FROM ap_locks WHERE owner_id=(" + players.get(player) + "');";
					 Long count = (Long) database.getValue(query, "COUNT(*)", true);
					 if (count != null) {
						 for (int i=0; i<count; i++) {
							 query = "SELECT uuid FROM ap_entities WHERE id=(SELECT entity_id FROM ap_locks WHERE owner_id=("+players.get(player)+") LIMIT " + i + ", 1);";
							 ResultSet result = database.get(query, false, true);
							 if (result != null) {
								 try {
									if (result.next()) {
										 UUID uniqueID = UUID.fromString(result.getString("uuid"));
										 EntityObject ent = new EntityObject(plugin, database, uniqueID, true);
										 addToList(ent);
									 }
								} catch (SQLException e) { }
							 }
						 }
						 
						 this.lastActionSuccess = true;
						 return this;
					 }
				 }
				 else { // Wenn der Spieler nicht in der Datenbank existiert, dann erstelle den Spieler und füge ihn der Liste hinzu.
					 String query = "INSERT INTO ap_owners (`name`) VALUES ('" + player + "');";
					 database.write(query);
					 
					 
					 if (!players.containsKey(player)) {
						 Integer id = (Integer) database.getValue("SELECT id FROM ap_owners WHERE name='" + player + "';", "id", true);
						 if (id != null) { 
							 players.put(player, id); 
						 	 keys.put(player, new ArrayList<EntityObject>());
						 }
						 else {
							 plugin.getLogger().info("Fehler: Ein Spieler konnte nicht in die Datenbank geschrieben werden.");
							 plugin.getLogger().info("Weitere Informationen: ID des Spielers ist null. [Name:" + player + "]");
						 }
					 }
					 
					 this.lastActionSuccess = true;
					 return this;
				 }
			}
		}
		this.lastActionSuccess = false;
		return this;
	}
	
	public EntityList disconnect(String player) {
		// TODO disconnect(String player);
		return this;
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
	
	public EntityList updateEntity(Entity e, boolean onlyLocation) {
		if (database != null) {
			if (database.checkConnection()) {
				int x = e.getLocation().getBlockX();
				int y = e.getLocation().getBlockY();
				int z = e.getLocation().getBlockZ();
				String uuid = e.getUniqueId().toString();
				if (onlyLocation) {
					String query = "UPDATE ap_entities SET last_x="+z+", last_y="+y+", last_z="+z+" WHERE uuid='"+uuid+"';";
				}
				else {
					Boolean alive = !e.isDead();
					String customName = "";
					try { customName = (((LivingEntity) e).getCustomName()); } catch (Exception ex) { }
					String armor = "UNKNOWN";
					try {
						Horse horse = (Horse)e;
						if (horse.getInventory().getArmor().equals(new ItemStack(Material.DIAMOND_BARDING))) {
							armor = "DIAMOND";
						}
						else if (horse.getInventory().getArmor().equals(new ItemStack(Material.GOLD_BARDING))) {
							armor = "GOLD";
						}
						else if (horse.getInventory().getArmor().equals(new ItemStack(Material.IRON_BARDING))) {
							armor = "IRON";
						}
					} catch (Exception ex) { }
					String color = "";
					try { color = (((Horse) e).getColor().toString()); } catch (Exception ex) { }
					try { color = (((Sheep) e).getColor().toString()); } catch (Exception ex) { }
					try { color = (((Wolf) e).getCollarColor().toString()); } catch (Exception ex) { }
					
					color.toUpperCase();
					
					String query = "UPDATE ap_entities SET last_x="+z+", last_y="+y+", last_z="+z+", "
							+ "alive="+alive+", "
							+ "nametag='"+customName+"', "
							+ "armor='"+armor+"', "
							+ "color='"+color+"' "
							+ "WHERE uuid='"+uuid+"';";
					
					database.write(query);
				}
				
				this.lastActionSuccess = true;
				return this;
			}
		}
		this.lastActionSuccess = false;
		return this;
	}
	
	private void addToList(EntityObject entity) {
		if (entity.isConnected()) {
			reverseKeys.put(UUID.fromString(entity.getUniqueID()), entity.getOwner());
			entities.put(entity, entity.getId());
			
			if (keys.containsKey(entity.getOwner())) {
				keys.get(entity.getOwner()).add(entity);
			} 
			else { 
				ArrayList<EntityObject> l = new ArrayList<EntityObject>();
				l.add(entity);
				keys.put(entity.getOwner(), l); 
			}
		}
	}
}
