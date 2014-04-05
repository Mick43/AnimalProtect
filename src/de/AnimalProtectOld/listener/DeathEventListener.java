package de.AnimalProtectOld.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import de.AnimalProtectOld.Main;
import de.AnimalProtectOld.MySQL;
import de.AnimalProtectOld.structs.EntityList;
import de.AnimalProtectOld.utility.APLogger;

public class DeathEventListener implements Listener {
	
	private Main plugin;
	private MySQL database;
	private EntityList list;
	
	public DeathEventListener(Main plugin) {
		this.plugin = plugin;
		this.database = plugin.database;
		this.list = plugin.list;
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		/* Datenbank-Verbindung aufbauen, falls nicht vorhanden. */
		if (!database.checkConnection()) { database.openConnection(); }
		
		if (plugin.isEnabled() && database.checkConnection() && isAnimal(event.getEntity())) {
			/* Variablen bereitstellen */
			EntityDamageByEntityEvent e = null;
			String deathCause = event.getEntity().getLastDamageCause().getCause().toString();
			Entity entity = event.getEntity();
			Player damager = null;
			
			/* Prüfen ob das Tier ermordet wurde */
			try { e = (EntityDamageByEntityEvent) event.getEntity().getLastDamageCause(); }
			catch (Exception exception) { }
			
			/* Prüfen ob der Mörder ein Spieler ist */
			if (e != null) {
				if (e.getDamager() instanceof Player) {
					try {
						damager = (Player) e.getDamager();
						
						/* Prüfen ob der Mörder der Owner ist */
						if (list.getPlayer(entity.getUniqueId()).equalsIgnoreCase(damager.getName())) {
							list.unlock(entity.getUniqueId());
							return;
						}
						else {
							APLogger.warn("Warnung: Ein gelocktes Entity wurde gerade von einem Spieler getötet, der nicht der Owner ist.");
							APLogger.warn("Eventuell wird irgendein Damage-Event nicht abgefangen! DamageCause: " + e.getCause().toString() + " | Damager: " + e.getDamager().getType().toString());
						}
					}
					catch (Exception exception) { }
				}
			}
			
			/* Der Mörder ist kein Spieler, also updaten */
			database.execute("UPDATE ap_entities SET deathcause='"+deathCause+"' WHERE uuid='"+event.getEntity().getUniqueId()+"';", true);
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
