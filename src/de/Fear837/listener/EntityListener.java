package de.Fear837.listener;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
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
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import de.Fear837.Commands;
import de.Fear837.Main;
import de.Fear837.MySQL;

public final class EntityListener implements Listener {

	private Main plugin;
	private MySQL sql;
	
	private static HashMap<Player, Entity> list;

	/* Der Entity-Listener */
	public EntityListener(MySQL sql, Main plugin) {
		this.plugin = plugin;
		this.sql = sql;
		
		list = new HashMap<Player, Entity>();
	}
	
	@EventHandler
	public void onEntityInteract(PlayerInteractEntityEvent event) {
		if (event.getPlayer().isSneaking()) {
			Entity entity = event.getRightClicked();
			if (entity.getType() == EntityType.COW 
						|| entity.getType() == EntityType.PIG
						|| entity.getType() == EntityType.SHEEP
						|| entity.getType() == EntityType.CHICKEN
						|| entity.getType() == EntityType.HORSE
						|| entity.getType() == EntityType.WOLF) {
				Player player = event.getPlayer();
				if (list.get(player) == entity) {
					player.sendMessage(ChatColor.YELLOW + "Du hast das Tier bereits ausgewählt."); 
					player.playSound(player.getLocation(), Sound.CLICK, 0.4f, 0.8f);
					return;
				}
				String entityOwner = Commands.getEntityOwner(entity.getUniqueId());
				
				player.playSound(player.getLocation(), Sound.CLICK, 0.75f, 0.8f);
				addSelected(player, event.getRightClicked());
				
				if (entityOwner == null) {
					switch (entity.getType()) {
					case COW: player.sendMessage(ChatColor.YELLOW + "Du hast eine §6Kuh§e ausgewählt. §7(/lockanimal um sie zu protecten.)"); 
						break;
					case PIG: player.sendMessage(ChatColor.YELLOW + "Du hast ein §6Schwein§e ausgewählt. §7(/lockanimal um es zu protecten.)");
						break;
					case SHEEP: player.sendMessage(ChatColor.YELLOW + "Du hast ein §6Schaf§e ausgewählt. §7(/lockanimal um es zu protecten.)");
						break;
					case CHICKEN: player.sendMessage(ChatColor.YELLOW + "Du hast ein §6Huhn§e ausgewählt. §7(/lockanimal um es zu protecten.)");
						break;
					case HORSE: player.sendMessage(ChatColor.YELLOW + "Du hast ein §6Pferd§e ausgewählt. §7(/lockanimal um es zu protecten.)");
						break;
					case WOLF: player.sendMessage(ChatColor.YELLOW + "Du hast einen §6Wolf§e ausgewählt. §7(/lockanimal um ihn zu protecten.)");
						break;
					default: player.sendMessage(ChatColor.YELLOW + "Du hast ein §6unbekanntes§e Tier ausgewählt. §7(/lockanimal um es zu protecten.)");
						break;
					}
				}
				else { 
					player.sendMessage(ChatColor.YELLOW + "Du hast das Tier von §6" + entityOwner + "§e ausgewählt."); 
				}
			}
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent event) { // TODO animalprotect.bypass darf alles
		if (sql == null || event.isCancelled()) { return; }
		EntityType entityType = event.getEntityType();
		
		if (entityType == EntityType.COW 
				|| entityType == EntityType.PIG
				|| entityType == EntityType.SHEEP
				|| entityType == EntityType.CHICKEN
				|| entityType == EntityType.HORSE
				|| entityType == EntityType.WOLF) {

			Entity entity = event.getEntity();
			Entity damager = event.getDamager();
			String entityOwner = null;
			
			try { entityOwner = Commands.getEntityOwner(entity.getUniqueId()); }
			catch (Exception e) { e.printStackTrace(); }
			if (entityOwner == null || entityOwner.isEmpty())
			{ return; }

			if (plugin.getConfig().getBoolean("settings.debug-messages")) {
				plugin.getServer().broadcastMessage("DEBUG::EntityType: "+damager.getType() + " | Owner:" + entityOwner);
			}
			
			switch (event.getDamager().getType()) {
			case PLAYER:

				if (!((Player) damager).getName().equalsIgnoreCase(entityOwner))
				{
					((Player) damager).sendMessage("Das Tier ist von " + entityOwner + " gesichert!");
					event.setCancelled(true); return;
				}
				break;
			case ARROW:
				plugin.getServer().broadcastMessage("Arrow detected...");
			    Arrow projectile = (Arrow)damager;
			    Entity shooter = null;
			    try { shooter = projectile.getShooter(); }
			    catch (Exception e) { return; }
				
				if (shooter != null) {
					if (shooter instanceof Player) {
						Player player = (Player)shooter;
						if (!player.getName().equalsIgnoreCase(entityOwner))
						{ event.setCancelled(true); return; }
					}
				}
				break;
			case EGG:
				plugin.getServer().broadcastMessage("Egg detected...");
				Egg projectile2 = (Egg)damager;
			    Entity shooter2 = null;
			    try { shooter2 = projectile2.getShooter(); }
			    catch (Exception e) { return; }
				
				if (shooter2 != null) {
					if (shooter2 instanceof Player) {
						Player player = (Player)shooter2;
						if (!player.getName().equalsIgnoreCase(entityOwner))
						{ event.setCancelled(true); return; }
					}
				}
				break;
			case SNOWBALL:
				plugin.getServer().broadcastMessage("Snowball detected...");
				Snowball projectile3 = (Snowball)damager;
			    Entity shooter3 = null;
			    try { shooter3 = projectile3.getShooter(); }
			    catch (Exception e) { return; }
				
				if (shooter3 != null) {
					if (shooter3 instanceof Player) {
						Player player = (Player)shooter3;
						if (!player.getName().equalsIgnoreCase(entityOwner))
						{ event.setCancelled(true); return; }
					}
				}
				break;
			case SPLASH_POTION:
				plugin.getServer().broadcastMessage("Potion detected...");
				ThrownPotion projectile4 = (ThrownPotion)damager;
			    Entity shooter4 = null;
			    try { shooter4 = projectile4.getShooter(); }
			    catch (Exception e) { return; }
				
				if (shooter4 != null) {
					if (shooter4 instanceof Player) {
						Player player = (Player)shooter4;
						if (!player.getName().equalsIgnoreCase(entityOwner))
						{ event.setCancelled(true); return; }
					}
				}
				break;
			default:
				plugin.getServer().broadcastMessage("Unknown Damager detected: " + event.getDamager().getType());
				break;
			}
		}
	}

	@EventHandler
	public void onEntityLeash(PlayerLeashEntityEvent event) {
		if (plugin.getConfig().getBoolean("settings.debug-messages")) {
			plugin.getLogger().info("onEntityLeash Event called. [getPlayer:" + event.getPlayer().getName() + "]");
		}
		if (sql == null || event.isCancelled()) { plugin.getLogger().warning("EntityListener.onEntityLeash: event cancelled or sql=null!"); return; }
		
		String entityOwner = null;
		try { entityOwner = Commands.getEntityOwner(event.getEntity().getUniqueId()); }
		catch (Exception e) { e.printStackTrace(); }
		if (entityOwner == null || entityOwner.isEmpty())
		{ plugin.getLogger().warning("EntityListener.onEntityLeash: entityOwner is null or empty!"); return; }
		
		if (!event.getPlayer().getName().equalsIgnoreCase(entityOwner)){
			event.setCancelled(true);
		}
	}

	private void addSelected(Player player, Entity entity) {
		if (!list.containsKey(player)) {
			list.put(player, entity);
		}
		else {
			list.remove(player);
			list.put(player, entity);
		}
	}
	
	public static Entity getSelected(Player player) {
		if (list.containsKey(player)) {
			return list.get(player);
		}
		else { return null; }
	}
	
}