package de.Fear837;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public final class EntityListener implements Listener {

	private Main plugin;
	private MySQL sql;

	/*
	 * // Still unused variables private DamageCause entityAttack =
	 * DamageCause.ENTITY_ATTACK; private DamageCause entityProjectile =
	 * DamageCause.PROJECTILE;
	 */

	/* Der Entity-Listener */
	public EntityListener(MySQL sql, Main plugin) {
		this.plugin = plugin;
		this.sql = sql;
	}

	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		if (sql == null && event.isCancelled()) {
			return;
		}
		EntityType entityType = event.getEntityType();
		if (entityType == EntityType.COW 
				|| entityType == EntityType.PIG
				|| entityType == EntityType.SHEEP
				|| entityType == EntityType.CHICKEN
				|| entityType == EntityType.HORSE
				|| entityType == EntityType.WOLF) {

			Entity entity = event.getEntity();
			Entity damager = event.getDamager();

			plugin.getServer().broadcastMessage("DEBUG::EntityType: "+damager.getType());
			
			switch (event.getDamager().getType()) {
			case PLAYER:
				String entityOwner = null;
				try {
					entityOwner = Commands.getEntityOwner(entity.getUniqueId());
				} catch (Exception e) {
					e.printStackTrace();
				}

				plugin.getServer().broadcastMessage("Entity-Damage-Event::EntityOwner: " + entityOwner);

				if ((entityOwner != null && !entityOwner.isEmpty() && !((Player) damager)
						.getName().equalsIgnoreCase(entityOwner))) {
					((Player) damager).sendMessage("Das Tier ist von "
							+ entityOwner + " gesichert!");
					event.setCancelled(true);
					return;
				}
				break;
			case ARROW:
				plugin.getServer().broadcastMessage("Arrow detected...");
				String entityOwner1 = null;
				Arrow projectile = (Arrow)entity;
				try {
					entityOwner1 = Commands.getEntityOwner(projectile.getShooter().getUniqueId());
				} catch (Exception e) { e.printStackTrace(); }
				
				if ((entityOwner1 != null && !entityOwner1.isEmpty())) {
					if (!((Player) projectile.getShooter()).getName().equalsIgnoreCase(entityOwner1)) {
						event.setCancelled(true);
						
						return;
					}
				}
				
				break;
			case EGG:
				plugin.getServer().broadcastMessage("Egg detected...");
				break;
			default:
				plugin.getServer().broadcastMessage(
						"Damager detected: " + event.getDamager().getType());
				break;
			}
		}
	}

}