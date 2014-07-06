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
			/* Pr�fen ob das Plugin aktiviert ist */
			if (!plugin.isEnabled()) { return; }
			
			/* Datenbank-Verbindung aufbauen, falls nicht vorhanden. */
			if (!database.isConnected()) { database.connect(); }
			
			if (plugin.getDatenbank().containsAnimal(event.getEntity().getUniqueId())) {
				/* Variablen bereitstellen */
				EntityDamageByEntityEvent damageEvent = null;
				Entity entity = event.getEntity();
				Player damager = null;
				
				/* Pr�fen ob das Tier ermordet wurde */
				try { damageEvent = (EntityDamageByEntityEvent) event.getEntity().getLastDamageCause(); }
				catch (Exception exception) { }
				
				/* Pr�fen ob der M�rder ein Spieler ist */
				if (damageEvent != null)
				{ 
					if (damageEvent.getDamager() instanceof Player)  {
						damager = (Player) damageEvent.getDamager();
						
						/* Pr�fen ob der M�rder der Owner ist */
						if (database.getOwner(entity.getUniqueId()).getUniqueId().equals(damager.getUniqueId())) {
							Animal animal = database.getAnimal(entity.getUniqueId());
							database.unlockAnimal(animal);
							return;
						}
						else {
							Messenger.error("Warnung: Ein gesichertes Entity wurde von einem Spieler, der nicht der Owner ist, get�tet.");
							Messenger.error("Weitere Informationen: DamageCause="+damageEvent.getCause().toString()+ ", Damager="+damager.getName());
						}
					}
				}
				
				/* Der M�rder ist kein Spieler, also wird das Tier geupdated */
				Animal animal = database.getAnimal(entity.getUniqueId());
				animal.updateAnimal(entity);
				animal.saveToDatabase(true);
				
				CraftoPlayer owner = CraftoPlayer.getPlayer(animal.getOwner());
				
				if (owner!=null) {
					Messenger.log("[AnimalProtect] Ein " +animal.getAnimaltype().toString()+" von " +owner.getName() + " wurde get�tet. ("
							+ ""+entity.getLocation().getBlockX()+", "+entity.getLocation().getBlockY()+", "+entity.getLocation().getBlockZ()+")");
				}
				else {
					Messenger.log("[AnimalProtect] Ein " +animal.getAnimaltype().toString()+" von einem unbekannten Owner wurde get�tet. ("
							+ ""+entity.getLocation().getBlockX()+", "+entity.getLocation().getBlockY()+", "+entity.getLocation().getBlockZ()+")");
				}
			}
		}
		catch (Exception e) { Messenger.exception("DeathEventListener/onEntityDeath", "Unknown Exception.", e); }
	}
}
