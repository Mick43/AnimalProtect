package de.Fear837.structs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import de.Fear837.Main;

public class EntityList {

	private Main plugin;
	private HashMap<Player, ArrayList<UUID>> keys;
	private HashMap<UUID, Player> reverseKeys;
	private static int MAX_ENTITIES_FOR_PLAYER = 0;
	private static boolean DEBUGGING = false;

	public EntityList(Main plugin) {
		this.plugin = plugin;
		this.keys = new HashMap<Player, ArrayList<UUID>>();
		this.reverseKeys = new HashMap<UUID, Player>();
		// TODO ?
		MAX_ENTITIES_FOR_PLAYER = plugin.getConfig().getInt("settings.max_entities_for_player");
		DEBUGGING = plugin.getConfig().getBoolean("settings.debug-messages");
		// TODO
	}

	public int size() {
		return reverseKeys.size();
	}
	
	public int sizeOfEntities(){
		return size();
	}
	
	public int sizeOfPlayers(){
		return keys.size();
	}

	private boolean containsPlayer(Player player) {
		return keys.containsKey(player);
	}

	public boolean contains(Entity entity) {
		if(entity instanceof Player) return containsPlayer((Player) entity);
		return reverseKeys.containsKey(entity.getUniqueId());
	}

	public ArrayList<UUID> get(Player player) {
		if (!contains(player))
			return new ArrayList<UUID>();
		return keys.get(player);
	}
	
	public Player get(Entity entity){
		if(contains(entity)) return reverseKeys.get(entity);
		return null; // TODO ?
	}

	public EntityList lock(Player player, Entity entity){
		if(reverseKeys.containsKey(entity.getUniqueId())) return this;
		if(!contains(player)) connect(player);
		keys.get(player).add(entity.getUniqueId());
		reverseKeys.put(entity.getUniqueId(), player);
		return this;
	}

	public boolean unlock(Entity entity) {
		// TODO
		return false;
	}

	public EntityList connect(Player player) {
		if (contains(player))
			return this;
		// TODO Load from db and insert all entities.
		// Check if respawning is needed.
		// Minimal create new ArrayList<UUID> in HashMap for player.
		return this;
	}

	public EntityList disconnect(Player player) {
		if (contains(player))
			return this;
		// TODO s. connect (reverse)
		return this;
	}

	public EntityList disconnectAll() {
		for (Entry<Player, ArrayList<UUID>> entry : keys.entrySet()) {
			if (DEBUGGING && size() == disconnect(entry.getKey()).size()) {
				plugin.getLogger()
						.warning("Disconnecting " + entry.getKey().getName()
						+ " from EntityList failed, some data may be lost.");
			}
		}
		return this;
	}
	
	public void saveToDatabase(){
		// TODO
	}
	
	public void saveToDatabase(Player player) {
		// TODO
	}
	
}
