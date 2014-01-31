package de.Fear837.listener;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityUnleashEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;

import de.Fear837.Main;
import de.Fear837.MySQL;
import de.Fear837.structs.EntityList_old;

public final class EntityListener implements Listener {

	private Main plugin;
	private MySQL sql;
	private EntityList_old list;
	
	private static HashMap<Player, Entity> selectedList;

	/* Der Entity-Listener */
	public EntityListener(MySQL sql, Main plugin, EntityList_old list) {
		this.plugin = plugin;
		this.sql = sql;
		this.list = list;
		
		selectedList = new HashMap<Player, Entity>();
	}

	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		if (sql == null || event.isCancelled()) { return; }
		
		if (isAnimal(event.getEntity())) {

			Entity entity = event.getEntity();
			Entity damager = event.getDamager();
			String entityOwner = null;
			try { entityOwner = list.get(entity).getName(); }
			catch (Exception e) { }
			
			if (entityOwner == null || entityOwner.isEmpty())
			{ return; }

			if (plugin.getConfig().getBoolean("settings.debug-messages")) {
				plugin.getServer().broadcastMessage("DEBUG::EntityType: "+damager.getType() + " | Owner:" + entityOwner);
			}
			
			switch (damager.getType()) {
			case PLAYER:
				if (((Player) damager).hasPermission("animalprotect.bypass")) { return; }
				if (!((Player) damager).getName().equalsIgnoreCase(entityOwner))
				{
					((Player) damager).sendMessage("Das Tier ist von " + entityOwner + " gesichert!");
					event.setCancelled(true); return;
				}
				break;
			case ARROW:
			    Arrow projectile = (Arrow)damager;
			    Entity shooter = null;
			    try { shooter = projectile.getShooter(); }
			    catch (Exception e) { return; }
				
				if (shooter != null) {
					if (shooter instanceof Player) {
						Player player = (Player)shooter;
						if (player.hasPermission("animalprotect.bypass")) { return; }
						if (!player.getName().equalsIgnoreCase(entityOwner))
						{ event.setCancelled(true); return; }
					}
				}
				break;
			case EGG:
				Egg projectile2 = (Egg)damager;
			    Entity shooter2 = null;
			    try { shooter2 = projectile2.getShooter(); }
			    catch (Exception e) { return; }
				
				if (shooter2 != null) {
					if (shooter2 instanceof Player) {
						Player player = (Player)shooter2;
						if (player.hasPermission("animalprotect.bypass")) { return; }
						if (!player.getName().equalsIgnoreCase(entityOwner))
						{ event.setCancelled(true); return; }
					}
				}
				break;
			case SNOWBALL:
				Snowball projectile3 = (Snowball)damager;
			    Entity shooter3 = null;
			    try { shooter3 = projectile3.getShooter(); }
			    catch (Exception e) { return; }
				
				if (shooter3 != null) {
					if (shooter3 instanceof Player) {
						Player player = (Player)shooter3;
						if (player.hasPermission("animalprotect.bypass")) { return; }
						if (!player.getName().equalsIgnoreCase(entityOwner))
						{ event.setCancelled(true); return; }
					}
				}
				break;
			case SPLASH_POTION:
				ThrownPotion projectile4 = (ThrownPotion)damager;
			    Entity shooter4 = null;
			    try { shooter4 = projectile4.getShooter(); }
			    catch (Exception e) { return; }
				
				if (shooter4 != null) {
					if (shooter4 instanceof Player) {
						Player player = (Player)shooter4;
						if (player.hasPermission("animalprotect.bypass")) { return; }
						if (!player.getName().equalsIgnoreCase(entityOwner))
						{ event.setCancelled(true); return; }
					}
				}
				break;
			default:
				if (plugin.getConfig().getBoolean("settings.debug-messages")) {
					plugin.getLogger().warning("DEBUG::Unknown Damager detected: " + event.getDamager().getType());
				}
				
				break;
			}
		}
	}

	@EventHandler
	public void onEntityLeash(PlayerLeashEntityEvent event) {
		if (sql==null || event.isCancelled()) { return; }
		if (plugin.getConfig().getBoolean("settings.debug-messages")) {
			plugin.getLogger().info("onEntityLeash Event called. [getPlayer:" + event.getPlayer().getName() + "]");
		}
		if (sql == null || event.isCancelled()) { plugin.getLogger().warning("EntityListener.onEntityLeash: event cancelled or sql=null!"); return; }
		
		String entityOwner = null;
		try { entityOwner = list.get(event.getEntity()).getName(); }
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
			if (list.contains(event.getEntity())) {
				updateEntity(event.getEntity());
			}
		}
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		if (sql==null) { return; }
		
		if (isAnimal(event.getEntity())) {
			if (list.contains(event.getEntity())) {
				updateEntity(event.getEntity());
				sql.write("UPDATE ap_entities SET alive=FALSE WHERE uuid='" + event.getEntity().getUniqueId() + "';");
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
						if (list.contains(entity)) {
							if (!list.get(entity).getName().equals(player.getName())) {
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
				if (list.contains(event.getVehicle())) {
					updateEntity(event.getVehicle());
				}
			}
		}
	}
	
	
	
	private void updateEntity(Entity entity)
	{
		UUID id = entity.getUniqueId();
		LivingEntity e = (LivingEntity)entity;
		
		sql.write("UPDATE ap_entities SET last_x=" + entity.getLocation().getBlockX() + " WHERE uuid='" + id + "';");
		sql.write("UPDATE ap_entities SET last_y=" + entity.getLocation().getBlockY() + " WHERE uuid='" + id + "';");
		sql.write("UPDATE ap_entities SET last_z=" + entity.getLocation().getBlockZ() + " WHERE uuid='" + id + "';");
		// TODO Alle 3 UPDATES in einem SQL-Befehl
		
		if (entity.isDead()) {
			sql.write("UPDATE ap_entities SET alive=FALSE WHERE uuid='" + id + "';");
		}
		
		if (e.getCustomName() != null) {
			String nametag = e.getCustomName();
			try { nametag = nametag.replaceAll("'", ""); } catch (Exception e1) { }
			sql.write("UPDATE ap_entities SET nametag='" + e.getCustomName() + "' WHERE uuid='" + id + "';");
		}
		
		if (e.getType() == EntityType.HORSE) {
			Horse h = (Horse) entity;
			String armor = "";
			if (h.getInventory().getArmor() != null) {
				String armorString = ((Horse) entity).getInventory().getArmor().toString();
	    		if (armorString == "ItemStack{DIAMOND_BARDING x 1}") { armor = "diamond"; }
	    		else if (armorString == "ItemStack{IRON_BARDING x 1}") { armor = "iron"; }
	    		else if (armorString == "ItemStack{GOLD_BARDING x 1}") { armor = "gold"; }
	    		else { armor = "unknown"; }
	    		sql.write("UPODATE ap_entities SET armor='" + armor + "' WHERE uuid='" + id + "';");
			}
		}
	}

	private void addSelected(Player player, Entity entity) {
		if (!selectedList.containsKey(player)) {
			selectedList.put(player, entity);
		}
		else {
			selectedList.remove(player);
			selectedList.put(player, entity);
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