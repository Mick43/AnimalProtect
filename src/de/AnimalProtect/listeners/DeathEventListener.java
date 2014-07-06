package de.AnimalProtect.listeners;

/* Bukkit Imports */
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;


import craftoplugin.core.database.CraftoPlayer;
/* AnimalProtect Imports */
import de.AnimalProtect.AnimalProtect;
import de.AnimalProtect.Database;
import de.AnimalProtect.Messenger;
import de.AnimalProtect.structs.Animal;

public class DeathEventListener implements Listener {

	private AnimalProtect plugin;
	private Database database;
	
	public DeathEventListener(AnimalProtect plugin) {
		this.plugin = plugin;
		this.database = plugin.getDatenbank();
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		try {
			/* Prüfen ob das Plugin aktiviert ist */
			if (!plugin.isEnabled()) { return; }
			
			/* Datenbank-Verbindung aufbauen, falls nicht vorhanden. */
			if (!database.isConnected()) { database.connect(); }
			
			if (plugin.getDatenbank().containsAnimal(event.getEntity().getUniqueId())) {
				/* Variablen bereitstellen */
				EntityDamageByEntityEvent damageEvent = null;
				Entity entity = event.getEntity();
				Player damager = null;
				
				/* Prüfen ob das Tier ermordet wurde */
				try { damageEvent = (EntityDamageByEntityEvent) event.getEntity().getLastDamageCause(); }
				catch (Exception exception) { }
				
				/* Prüfen ob der Mörder ein Spieler ist */
				if (damageEvent != null)
				{ 
					if (damageEvent.getDamager() instanceof Player)  {
						damager = (Player) damageEvent.getDamager();
						
						/* Prüfen ob der Mörder der Owner ist */
						if (database.getOwner(entity.getUniqueId()).getUniqueId().equals(damager.getUniqueId())) {
							Animal animal = database.getAnimal(entity.getUniqueId());
							database.unlockAnimal(animal);
							return;
						}
						else {
							Messenger.error("Warnung: Ein gesichertes Entity wurde von einem Spieler, der nicht der Owner ist, getötet.");
							Messenger.error("Weitere Informationen: DamageCause="+damageEvent.getCause().toString()+ ", Damager="+damager.getName());
						}
					}
				}
				
				/* Der Mörder ist kein Spieler, also wird das Tier geupdated */
				Animal animal = database.getAnimal(entity.getUniqueId());
				animal.updateAnimal(entity);
				animal.saveToDatabase(true);
				
				CraftoPlayer owner = CraftoPlayer.getPlayer(animal.getOwner());
				
				if (owner!=null) {
					Messenger.log("[AnimalProtect] Ein " +animal.getAnimaltype().toString()+" von " +owner.getName() + " wurde getötet. ("
							+ ""+entity.getLocation().getBlockX()+", "+entity.getLocation().getBlockY()+", "+entity.getLocation().getBlockZ()+")");
				}
				else {
					Messenger.log("[AnimalProtect] Ein " +animal.getAnimaltype().toString()+" von einem unbekannten Owner wurde getötet. ("
							+ ""+entity.getLocation().getBlockX()+", "+entity.getLocation().getBlockY()+", "+entity.getLocation().getBlockZ()+")");
				}
			}
		}
		catch (Exception e) { Messenger.exception("DeathEventListener/onEntityDeath", "Unknown Exception.", e); }
	}
}
