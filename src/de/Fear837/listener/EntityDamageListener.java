package de.Fear837.listener;

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

import de.Fear837.Main;
import de.Fear837.MySQL;
import de.Fear837.structs.EntityList;

public class EntityDamageListener implements Listener {
	
	private EntityList list;
	private MySQL database;
	private Main plugin;
	
	public EntityDamageListener(EntityList list, MySQL database, Main plugin) {
		this.list = list;
		this.database = database;
		this.plugin = plugin;
	}
	

	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		if (database == null || event.isCancelled() || !database.checkConnection()) { return; }
		
		if (isAnimal(event.getEntity())) {

			Entity entity = event.getEntity();
			Entity damager = event.getDamager();
			String entityOwner = null;
			try { entityOwner = list.getPlayer(entity); }
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
