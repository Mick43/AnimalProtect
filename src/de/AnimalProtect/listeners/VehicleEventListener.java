package de.AnimalProtect.listeners;

/* Java Imports */
import java.util.HashMap;
import java.util.UUID;

/* Bukkit Imports */
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;

/* AnimalProtect Imports */
import de.AnimalProtect.AnimalProtect;
import de.AnimalProtect.Database;
import de.AnimalProtect.Messenger;
import de.AnimalProtect.structs.Animal;

public class VehicleEventListener implements Listener {
	
	private final AnimalProtect plugin;
	private final Database database;
	private final HashMap<UUID, Long> exitedAnimals;
	
	public VehicleEventListener(final AnimalProtect plugin) {
		this.plugin = plugin;
		this.database = plugin.getDatenbank();
		this.exitedAnimals = new HashMap<UUID, Long>();
	}
	
	@EventHandler
	public void onVehicleEnter(final VehicleEnterEvent event) {
		try {
			if (!this.plugin.isEnabled() || event.isCancelled()) { return; }
			
			/* Datenbank-Verbindung aufbauen, falls nicht vorhanden */
			if (!this.database.isConnected()) { this.database.connect(); }
			
			/* Prüfen ob das Entity ein Tier ist */
			if (!this.plugin.isAnimal(event.getVehicle())) { return; }
			
			/* Prüfen ob das Entity, welches auf das Tier steigt, ein Spieler ist */
			if (!(event.getEntered() instanceof Player)) { return; }
			
			/* Variablen bereitstellen */
			final Player player = (Player) event.getEntered();
			final Entity entity = event.getVehicle();
			
			/* Prüfen ob das Tier gesichert wurde */
			if (!this.database.containsAnimal(entity.getUniqueId())) { return; }
			
			/* Prüfen ob der Spieler die 'AnimalProtect.Bypass'-Permission hat */
			if (player.hasPermission("animalprotect.bypass")) { return; }
			
			/* Prüfen ob der Spieler der Owner ist */
			if (!this.database.getOwner(entity.getUniqueId()).getUniqueId().equals(player.getUniqueId())) {
				event.setCancelled(true);
			}
		}
		catch (final Exception e) { Messenger.exception("VehicleEventListener/onVehicleEnter", "Unknown Exception.", e); }
	}
	
	@EventHandler
	public void onVehicleExit(final VehicleExitEvent event) {
		try {
			if (!this.plugin.isEnabled() || event.isCancelled()) { return; }
			
			/* Datenbank-Verbindung aufbauen, falls nicht vorhanden */
			if (!this.database.isConnected()) { this.database.connect(); }
			
			/* Prüfen ob das Entity ein Tier ist */
			if (!this.plugin.isAnimal(event.getVehicle())) { return; }
			
			/* Prüfen ob das Tier gesichert ist */
			if (!this.database.containsAnimal(event.getVehicle().getUniqueId())) { return; }
			
			/* Das Tier updaten, falls 30 Sekunden vergangen sind */
			if (this.exitedAnimals.containsKey(event.getVehicle().getUniqueId())) {
				if (this.exitedAnimals.get(event.getVehicle().getUniqueId()) + 30000 > System.currentTimeMillis()) {
					final Animal animal = this.database.getAnimal(event.getVehicle().getUniqueId());
					animal.updateAnimal(event.getVehicle());
					animal.saveToDatabase(true);
					
					this.exitedAnimals.put(event.getVehicle().getUniqueId(), System.currentTimeMillis());
				}
			}
			else { this.exitedAnimals.put(event.getVehicle().getUniqueId(), System.currentTimeMillis()); }
		}
		catch (final Exception e) { Messenger.exception("VehicleEventListener/onVehicleEnter", "Unknown Exception.", e); }
	}
}
