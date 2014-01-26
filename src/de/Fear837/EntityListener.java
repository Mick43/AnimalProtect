package de.Fear837;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public final class EntityListener implements Listener {

	private Main plugin; // TODO wird spaeter benutzt
	private MySQL sql;
	private Connection c;
	// TODO Still unused variables
	private DamageCause entityAttack = DamageCause.ENTITY_ATTACK;
	private DamageCause entityProjectile = DamageCause.PROJECTILE;

	/* Der Entity-Listener */
	public EntityListener(MySQL sql, Connection c, Main plugin) {
		this.plugin = plugin;
		this.sql = sql;
		this.c = c;
	}

	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		if (sql == null && event.isCancelled()) {
			return;
		}
		EntityType entityType = event.getEntityType();
		if (entityType == EntityType.COW || entityType == EntityType.PIG
				|| entityType == EntityType.SHEEP
				|| entityType == EntityType.CHICKEN
				|| entityType == EntityType.HORSE
				|| entityType == EntityType.WOLF) {
			if (event.getDamager() instanceof Player) {
				Entity entity = event.getEntity();
				Player damager = (Player) event.getDamager();

				String entityOwner = null;
				try {
					entityOwner = Commands.getEntityOwner(entity.getUniqueId());
				} catch (Exception e) {
					e.printStackTrace();
				}

				for (Player p : plugin.getServer().getOnlinePlayers()) {
					p.sendMessage("Entity-Damage-Event::EntityOwner: "
							+ entityOwner);
				}

				if (entityOwner != null && !entityOwner.isEmpty()
						&& !damager.getName().equalsIgnoreCase(entityOwner)) {
					event.setDamage(0); // TODO anders: deprecated
					damager.sendMessage("Das Tier ist von " + entityOwner
							+ " gesichert!");
					event.setCancelled(true);
				}
			}
		}
	}

}
