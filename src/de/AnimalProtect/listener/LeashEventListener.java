package de.AnimalProtect.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityUnleashEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;

import de.AnimalProtect.Main;
import de.AnimalProtect.MySQL;
import de.AnimalProtect.structs.EntityList;

public class LeashEventListener implements Listener {
	
	private Main plugin;
	private MySQL database;
	private EntityList list;
	
	public LeashEventListener(Main plugin) {
		this.plugin = plugin;
		this.database = plugin.database;
		this.list = plugin.list;
	}
	
	@EventHandler
	public void onEntityLeash(PlayerLeashEntityEvent event) {
		/* Datenbank-Verbindung aufbauen, falls nicht vorhanden. */
		if (!database.checkConnection()) { database.openConnection(); }
		
		if (plugin.isEnabled() && database.checkConnection() && !event.isCancelled()) {
			/* Prüfen ob der Spieler alles darf */
			if (event.getPlayer().hasPermission("animalprotect.bypass")) {
				return;
			}
			
			/* Prüfen ob eine Datenbank-Verbindung besteht und ob das Entity ein Tier ist */
			if (database == null) { return; }
			if (!database.checkConnection()) { return; }
			if (!isAnimal(event.getEntity())) { return; }
			
			/* Den Owner des Entities bekommen, null falls entity nicht locked. */
			String owner = list.getPlayer(event.getEntity().getUniqueId());
			
			if (owner != null) {
				/* Wenn dem Spieler das Tier nicht gehört */
				if (!owner.equals(event.getPlayer().getName()))
				{ event.setCancelled(true); }
			}
		}
	}
	
	@EventHandler
	public void onEntityUnleash(EntityUnleashEvent event) {
		if (plugin.isEnabled() && database.checkConnection()) {
			/* Prüfen ob das Entity ein Tier ist */
			if (!isAnimal(event.getEntity())) { return; }
			
			list.updateEntity(event.getEntity(), false);
		}
	}
	
	private boolean isAnimal(Entity entity) {
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
}
