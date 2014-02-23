package de.AnimalProtect.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import de.AnimalProtect.Main;
import de.AnimalProtect.MySQL;
import de.AnimalProtect.structs.EntityList;
import de.AnimalProtect.utility.APLogger;

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
			
			/* Pr�fen ob das Tier ermordet wurde */
			try { e = (EntityDamageByEntityEvent) event.getEntity().getLastDamageCause(); }
			catch (Exception exception) { }
			
			/* Pr�fen ob der M�rder ein Spieler ist */
			if (e != null) {
				if (e.getDamager() instanceof Player) {
					try {
						/* Pr�fen ob der M�rder der Owner ist */
						if (list.getPlayer(entity.getUniqueId()).equalsIgnoreCase(damager.getName())) {
							list.unlock(entity.getUniqueId());
							return;
						}
						else {
							APLogger.warn("Warnung: Ein gelocktes Entity wurde gerade von einem Spieler get�tet, der nicht der Owner ist.");
							APLogger.warn("Eventuell wird irgendein Damage-Event nicht abgefangen!");
						}
					}
					catch (Exception exception) { }
				}
			}
			
			/* Der M�rder ist kein Spieler, also updaten */
			database.write("UPDATE ap_entities SET deathcause='"+deathCause+"' WHERE uuid='"+event.getEntity().getUniqueId()+"';", true);
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
		||  type == EntityType.OCELOT)
		{ return true; }
		return false;
	}
}
