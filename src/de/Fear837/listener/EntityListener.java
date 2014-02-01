package de.Fear837.listener;

import java.util.HashMap;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityUnleashEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;

import de.Fear837.Main;
import de.Fear837.MySQL;
import de.Fear837.structs.EntityList;

public final class EntityListener implements Listener {

	private Main plugin;
	private MySQL sql;
	private EntityList list;
	
	private static HashMap<Player, Entity> selectedList;

	/* Der Entity-Listener */
	public EntityListener(MySQL sql, Main plugin, EntityList list) {
		this.plugin = plugin;
		this.sql = sql;
		this.list = list;
		
		selectedList = new HashMap<Player, Entity>();
	}

	@EventHandler
	public void onEntityLeash(PlayerLeashEntityEvent event) {
		if (sql==null || event.isCancelled()) { return; }
		if (plugin.getConfig().getBoolean("settings.debug-messages")) {
			plugin.getLogger().info("onEntityLeash Event called. [getPlayer:" + event.getPlayer().getName() + "]");
		}
		if (sql == null || event.isCancelled()) { plugin.getLogger().warning("EntityListener.onEntityLeash: event cancelled or sql=null!"); return; }
		
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
		if (sql == null) { return; }
		if (isAnimal(event.getEntity())) {
			if (list.containsEntity(event.getEntity())) {
				list.updateEntity(event.getEntity(), false);
			}
		}
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		if (sql==null) { return; }
		
		if (isAnimal(event.getEntity())) {
			if (list.containsEntity(event.getEntity())) {
				list.updateEntity(event.getEntity(), false);
				// TODO Den Grund des Todes vom Entity auch in die Datenbank eintragen.
			}
		}
	}

	@EventHandler
	public void onEntityEnter(VehicleEnterEvent event) {
		if (!event.isCancelled()) {
			if (event.getVehicle().getType() == EntityType.HORSE || event.getVehicle().getType() == EntityType.PIG) {
				if (event.getEntered().getType() == EntityType.PLAYER) {
					Player player = (Player) event.getEntered();
					Entity entity = (Entity) event.getVehicle();
					
					if (player.isSneaking()) { 
						event.setCancelled(true); 
						return; }
					else {
						if (list.containsEntity(entity)) {
							if (!list.getPlayer(entity).equals(player.getName())) {
								event.setCancelled(true);
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onEntityExit(VehicleExitEvent event) {
		if (!event.isCancelled()) {
			if (event.getVehicle().getType() == EntityType.HORSE || event.getVehicle().getType() == EntityType.PIG) {
				if (list.containsEntity(event.getVehicle())) {
					list.updateEntity(event.getVehicle(), false);
				}
			}
		}
	}
	
	public static Entity getSelected(Player player) {
		if (selectedList.containsKey(player)) {
			return selectedList.get(player);
		}
		else { return null; }
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