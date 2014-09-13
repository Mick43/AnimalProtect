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

/**
 * Der DeathEventListener fängt das {@link EntityDeathEvent} ab
 * und prüft ob ein gesichertes Tier gestorben ist.
 * 
 * @author Fear837, Pingebam
 * @version 1.0
 * @see Listener
 */
public class DeathEventListener implements Listener {

	/** Die AnimalProtect-Instanz. */
	private final AnimalProtect plugin;
	/** Ein Verweis auf die AnimalProtect-Datenbank. */
	private final Database database;

	/**
	 * Initialisiert den EventListener.
	 * @param plugin - Das AnimalProtect-Plugin.
	 */
	public DeathEventListener(final AnimalProtect plugin) {
		this.plugin = plugin;
		this.database = plugin.getDatenbank();
	}

	@EventHandler
	public void onEntityDeath(final EntityDeathEvent event) {
		try {
			/* Prüfen ob das Plugin aktiviert ist */
			if (!this.plugin.isEnabled()) { return; }

			/* Datenbank-Verbindung aufbauen, falls nicht vorhanden. */
			if (!this.database.isConnected()) { this.database.connect(); }

			if (this.plugin.getDatenbank().containsAnimal(event.getEntity().getUniqueId())) {
				/* Variablen bereitstellen */
				EntityDamageByEntityEvent damageEvent = null;
				final Entity entity = event.getEntity();
				Player damager = null;

				/* Prüfen ob das Tier ermordet wurde */
				try { damageEvent = (EntityDamageByEntityEvent) event.getEntity().getLastDamageCause(); }
				catch (final Exception exception) { }

				/* Prüfen ob der Mörder ein Spieler ist */
				if (damageEvent != null)
				{ 
					if (damageEvent.getDamager() instanceof Player)  {
						damager = (Player) damageEvent.getDamager();

						/* Prüfen ob der Mörder der Owner ist */
						if (this.database.getOwner(entity.getUniqueId()).getUniqueId().equals(damager.getUniqueId())) {
							final Animal animal = this.database.getAnimal(entity.getUniqueId());
							this.database.unlockAnimal(animal);
							return;
						}
						else {
							Messenger.error("Warnung: Ein gesichertes Entity wurde von einem Spieler, der nicht der Owner ist, getötet.");
							Messenger.error("Weitere Informationen: DamageCause="+damageEvent.getCause().toString()+ ", Damager="+damager.getName());
						}
					}
				}

				/* Der Mörder ist kein Spieler, also wird das Tier geupdated */
				final Animal animal = this.database.getAnimal(entity.getUniqueId());
				animal.updateAnimal(entity);
				animal.saveToDatabase(true);

				final CraftoPlayer owner = CraftoPlayer.getPlayer(animal.getOwner());

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
		catch (final Exception e) { Messenger.exception("DeathEventListener/onEntityDeath", "Unknown Exception.", e); }
	}
}