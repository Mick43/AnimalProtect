package de.Fear837.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityUnleashEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;

import de.Fear837.Main;
import de.Fear837.MySQL;
import de.Fear837.structs.EntityList;

public class EntityLeashListener implements Listener {
	
	private Main plugin;
	private MySQL database;
	private EntityList list;
	
	public EntityLeashListener(Main plugin, MySQL database, EntityList list) {
		this.plugin = plugin;
		this.database = database;
		this.list = list;
	}
	

	@EventHandler
	public void onEntityLeash(PlayerLeashEntityEvent event) {
		if (database==null || event.isCancelled()) { return; }
		if (plugin.getConfig().getBoolean("settings.debug-messages")) {
			plugin.getLogger().info("onEntityLeash Event called. [getPlayer:" + event.getPlayer().getName() + "]");
		}
		if (database == null || event.isCancelled()) { plugin.getLogger().warning("EntityListener.onEntityLeash: event cancelled or sql=null!"); return; }
		
		String entityOwner = null;
		try { entityOwner = list.getPlayer(event.getEntity()); }
		catch (Exception e) { }
		
		if (entityOwner == null || entityOwner.isEmpty())
		{ plugin.getLogger().warning("EntityListener.onEntityLeash: entityOwner is null or empty!"); return; }
		
		if (!event.getPlayer().getName().equalsIgnoreCase(entityOwner)){
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onEntityUnleash(EntityUnleashEvent  event) {
		if (database == null) { return; }
		if (isAnimal(event.getEntity())) {
			if (list.containsEntity(event.getEntity())) {
				list.updateEntity(event.getEntity(), false);
			}
		}
	}
	
	public boolean isAnimal(Entity entity) {
		if (entity ==  null) { return false; }
		else { 
			if (entity.getType() == EntityType.COW 
					|| entity.getType() == EntityType.PIG
					|| entity.getType() == EntityType.SHEEP
					|| entity.getType() == EntityType.CHICKEN
					|| entity.getType() == EntityType.HORSE
					|| entity.getType() == EntityType.WOLF) {
				return true;
			}
		}
		return false;
	}
}
