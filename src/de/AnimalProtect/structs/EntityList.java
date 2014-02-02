package de.AnimalProtect.structs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.ItemStack;

import de.AnimalProtect.Main;
import de.AnimalProtect.MySQL;
import de.AnimalProtect.utility.APLogger;

public class EntityList {
	
	/** The plugin using this list */
	private Main plugin;
	/** The database to get data from */
	private MySQL database;
	
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
		this.database = plugin.database;
		
		this.entities = new HashMap<EntityObject, Integer>();
		this.keys = new HashMap<String, ArrayList<EntityObject>>();
		this.reverseKeys = new HashMap<UUID, String>();
		this.players = new HashMap<String, Integer>();

		loadFromDatabase();

		MAX_ENTITIES_FOR_PLAYER = plugin.getConfig().getInt("settings.max_entities_for_player");
		DEBUGGING = Main.DEBUGMODE;
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
	public int sizeOfEntities(String player) {
		/* Erst schauen ob der Spieler im RAM ist */
		if (keys.containsKey(player)) {
			return keys.get(player).size();
		}
		/* Wenn nicht, dann soll in der Datenbank gesucht werden. */
		else { 
			String query = "SELECT count(id) FROM ap_locks WHERE owner_id=("+players.get(player)+ ");";
			Integer count = (Integer)database.getValue(query, "COUNT(ID)", true);
			if (count != null) { return count; }
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
		/* Da alle Spieler von der Datenbank in die Liste geladen werden, */
		/* muss nicht mehr in der Datenbank geschaut werden ob es ihn gibt. */
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
		/* Wenn das angegebene Entity ein Spieler ist, */ 
		/* dann muss nur noch im RAM geschaut werden.  */
		if (entity instanceof Player) {
			return containsPlayer(((Player) entity).getName());
		}
		/* Sollte es kein Spieler sein, dann wird auf die */
		/* containsEntity(UUID id)-Methode verwiesen.     */
		else if (containsEntity(entity.getUniqueId())) {
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
		/* Wenn das Entity bereits im RAM ist, */
		/* dann einfach true returnen.         */
		if (reverseKeys.containsKey(id)) {
			return true;
		}
		/* Ist das Entity nicht im RAM, dann wird in der */
		/* Datenbank nach der UUID des Entities gesucht. */
		else if (database != null && database.checkConnection()) {
			String query = "SELECT count(1) FROM ap_entities WHERE uuid='"+id.toString()+"' LIMIT 1;";
			Integer i = (Integer)database.getValue(query, "id", true);
			if (i == null) { return false; }
			else if (i == 0) { return false; }
			else { return true; }
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
		/* Erst schauen ob das Entity in der 'entities'-Liste ist. */
		if (entities.containsKey(entity)) { return true; }
		/* Wenn nicht, dann auf die containsEntity-Methode verweisen. */
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
		/* Wenn das Entity in der reverseKeys-Liste ist,    */
		/* dann wird Entity einfach aus der Liste returned. */
		if (reverseKeys.containsKey(entity.getUniqueId())) {
			return reverseKeys.get(entity.getUniqueId());
		}
		/* Wenn das Entity nicht im RAM ist, dann wird in der Datenbank geschaut. */
		else {
			String query = "Select name FROM ap_owners WHERE id=("
					+ "SELECT owner_id FROM ap_locks WHERE entity_id=("
					+ "SELECT id FROM ap_entities WHERE uuid='"+entity.getUniqueId()+"'));";
			String owner = (String) database.getValue(query, "name", true);
			if (owner != null) { return owner; }
		}
		return null;
	}
	
	/**
	 * Returns an EntityObject
	 * 
	 * @param entity
	 *            The Entity
	 * @return EntityObject, or null if the entity is not in the database.
	 */
	public EntityObject getEntityObject(UUID uniqueID) {
		/* Zuerst schauen ob das Entity im RAM gespeichert ist. */
		for (EntityObject e: entities.keySet()) {
			if (e.getUniqueID().equals(uniqueID)) {
				return e;
			}
		}
		
		/* Wenn nicht im RAM, dann wird in der Datenbank geschaut. */
		EntityObject entity = new EntityObject(plugin, database, uniqueID, true);
		if (entity.isConnected()) {
			addToList(entity);
			return entity;
		}
		
		return null;
	}
	
	/**
	 * Returns a list of Entities, locked by a given player
	 * 
	 * @param player
	 *            The name of the player
	 * @return ArrayList<EntityObject> or an empty list if player is not in the database.
	 */
	public ArrayList<EntityObject> getEntities(String player) {
		/* Zuerst schauen ob die Entities im RAM sind. */
		if (keys.containsKey(player)) {
			return keys.get(player);
		}
		else {
			/* Spieler ist nicht im RAM, also wird er aus der DB geladen. */
			connect(player);
			ArrayList<EntityObject> list = keys.get(player);
			
			/* Wenn die ArrayList aus irgendeinem Grund null ist, wird sie leer erstellt. */
			if (list == null) { list = new ArrayList<EntityObject>(); }
			
			return list;
		}
	}
	
	
	/** Locks an entity
	 * 
	 * @param entity
	 *            The entity to be unlocked
	 * @return EntityList after unlocking, if unlocking failed, returns an
	 *         unmodified version of the list.
	 * @see de.Fear837.structs.EntityList.lastActionSucceeded()
	 */
	public EntityList lock(String player, Entity entity) {
		this.lastActionSuccess = false;
		
		/* Wenn das Entity bereits im RAM ist, dann */
		/* kann es nicht nochmal gelockt werden.    */
		if (reverseKeys.containsKey(entity)) {
			return this;
		}
		
		/* Wenn das Entity nicht im RAM ist, soll erst */
		/* geschaut werden ob es den Spieler bereits   */
		/* in der Datenbank gibt und wie viele Tiere   */
		/* er bereits gelockt hat.                     */
		Integer entitySize = (Integer) sizeOfEntities(player);
		
		/* Wenn der Spieler nicht in der DB ist, wird er hinzugefuegt. */
		if (entitySize == 0) { connect(player); }
		
		/* Jetzt wird das Entity un der Lock in die Datenbank eingetragen */
		if (database != null && database.checkConnection()) {
			/* Zuerst werden die Eigenschaften des Entities erstellt */
			UUID uuid = entity.getUniqueId();
			Integer x = entity.getLocation().getBlockX();
			Integer y = entity.getLocation().getBlockY();
			Integer z = entity.getLocation().getBlockZ();
			String type = entity.getType().toString();
			String nametag = "";
			Double maxhp = 10.0;
			Boolean alive = !entity.isDead();
			String color = "";
			String armor = "UNKNOWN";
			Double jumpstrength = 10.0;
			String style = "";
			String variant = "NONE";
			
			/* Jetzt werden die restlichen Eigenschaften, die nicht bei jedem */
			/* Entity vorhanden sind, in die Variablen eingetragen            */
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
	    	
	    	/* Jetzt wird die Query zusammengestellt. */
	    	String Query = "INSERT INTO ap_entities (`uuid`, `last_x`, `last_y`, `last_z`, `animaltype`, `nametag`, "
	    			+ "`maxhp`, `alive`, `color`, `armor`, `horse_jumpstrength`, `horse_style`, `horse_variant`) "
	    			+ "VALUES ('"
	    			+ uuid.toString() + "', "
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
	    			+ ");";
	    	
	    	/* Die Query wird abgeschickt. */
	    	database.write(Query);
	    	
	    	/* Jetzt muss der Eintrag in 'ap_locks' erstellt werden. */
	        Query = "INSERT INTO ap_locks (`owner_id`, `entity_id`) VALUES ("
	    			+ "(" + players.get(player) + "), "
	    			+ "(SELECT id FROM ap_entities WHERE uuid='" + uuid + "'));";
	    	database.write(Query);
	    	
	    	/* Zum Schluss wird das EntityObject erstellt und direkt im RAM eingetragen. */
	    	EntityObject ent = new EntityObject(plugin, database, uuid, true);
	    	if (ent.isConnected()) {
	    		this.addToList(ent);
	    		this.lastActionSuccess = true;
	    		APLogger.info("Inserted new Entity: [ID:"+uuid+"] [Owner:"+player+"] [EntityType:"+type+"] [x:"+x+", y:"+y+", z:"+z+"]");
	    	}
	    	/* Wenn das Entity sich nicht mit der Datenbank verbinden konnte, */
	    	/* dann sind die INSERTS fehlgeschlagen. */
	    	else {
	    		this.lastActionSuccess = false;
	    	}
		}
		else { this.lastActionSuccess = false; }
		return this;
	}
	
	
	/** Unlocks an entity
	 * 
	 * @param entity
	 *            The entity to be unlocked
	 * @return EntityList after unlocking, if unlocking failed, returns an
	 *         unmodified version of the list.
	 * @see de.Fear837.structs.EntityList.lastActionSucceeded()
	 */
	public EntityList unlock (Entity entity) {
		return null;
	}
	
	public void saveToDatabase() {
		return;
	}
	
	public void loadFromDatabase() {
		return;
	}
	
	public EntityList connect(String player) {
		return null;
	}
	
	public EntityList disconnect(String player) {
		return null;
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
		return null;
	}
	
	private void addToList(EntityObject entity) {
		return;
	}
}
