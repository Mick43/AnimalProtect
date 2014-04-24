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
import de.AnimalProtect.structs.Animal;

public class VehicleEventListener implements Listener {
	
	private AnimalProtect plugin;
	private Database database;
	
	private HashMap<UUID, Long> exitedAnimals;
	
	public VehicleEventListener(AnimalProtect plugin) {
		this.plugin = plugin;
		this.database = plugin.getDatenbank();
		this.exitedAnimals = new HashMap<UUID, Long>();
	}
	
	@EventHandler
	public void onVehicleEnter(VehicleEnterEvent event) {
		if (!plugin.isEnabled() || event.isCancelled()) { return; }
		
		/* Datenbank-Verbindung aufbauen, falls nicht vorhanden */
		if (!database.isConnected()) { database.connect(); }
		
		/* Prüfen ob das Entity ein Tier ist */
		if (!plugin.isAnimal(event.getVehicle())) { return; }
		
		/* Prüfen ob das Entity, welches auf das Tier steigt, ein Spieler ist */
		if (!(event.getEntered() instanceof Player)) { return; }
		
		/* Variablen bereitstellen */
		Player player = (Player) event.getEntered();
		Entity entity = (Entity) event.getVehicle();
		
		/* Prüfen ob das Tier gesichert wurde */
		if (!database.containsAnimal(entity.getUniqueId())) { return; }
		
		/* Prüfen ob der Spieler die 'AnimalProtect.Bypass'-Permission hat */
		if (player.hasPermission("animalprotect.bypass")) { return; }
		
		/* Prüfen ob der Spieler der Owner ist */
		if (!database.getOwner(entity.getUniqueId()).getUniqueId().equals(player.getUniqueId())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onVehicleExit(VehicleExitEvent event) {
		if (!plugin.isEnabled() || event.isCancelled()) { return; }
		
		/* Datenbank-Verbindung aufbauen, falls nicht vorhanden */
		if (!database.isConnected()) { database.connect(); }
		
		/* Prüfen ob das Entity ein Tier ist */
		if (!plugin.isAnimal(event.getVehicle())) { return; }
		
		/* Prüfen ob das Tier gesichert ist */
		if (!database.containsAnimal(event.getVehicle().getUniqueId())) { return; }
		
		/* Das Tier updaten, falls 30 Sekunden vergangen sind */
		if (exitedAnimals.containsKey(event.getVehicle().getUniqueId())) {
			if (exitedAnimals.get(event.getVehicle().getUniqueId()) + 30000 > System.currentTimeMillis()) {
				Animal animal = database.getAnimal(event.getVehicle().getUniqueId());
				animal.updateAnimal(event.getVehicle());
				animal.saveToDatabase(true);
				
				exitedAnimals.put(event.getVehicle().getUniqueId(), System.currentTimeMillis());
			}
		}
		else { exitedAnimals.put(event.getVehicle().getUniqueId(), System.currentTimeMillis()); }
	}
}
