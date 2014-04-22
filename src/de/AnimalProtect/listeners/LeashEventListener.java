package de.AnimalProtect.listeners;

/* Bukkit Imports */
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;

/* AnimalProtect Imports */
import de.AnimalProtect.AnimalProtect;
import de.AnimalProtect.Database;

public class LeashEventListener implements Listener {
	
	private AnimalProtect plugin;
	private Database database;
	
	public LeashEventListener(AnimalProtect plugin) {
		this.plugin = plugin;
		this.database = plugin.getDatenbank();
	}
	
	@EventHandler
	public void onEntityLeash(PlayerLeashEntityEvent event) {
		if (!plugin.isEnabled() || event.isCancelled()) { return; }
		
		/* Datenbank-Verbindung aufbauen, falls nicht vorhanden. */
		if (!database.isConnected()) { database.connect(); }
		
		/* Prüfen ob das Entity ein Tier ist */
		if (!plugin.isAnimal(event.getEntity())) { return; }
		
		/* Prüfen ob das Entity gesichert wurde */
		if (database.containsAnimal(event.getEntity().getUniqueId())) { return; }
		
		/* Prüfen ob der Spieler die 'AnimalProtect-Bypass'-Permission hat */
		if (event.getPlayer().hasPermission("animalprotect.bypass")) { return; }
		
		/* Prüfen ob der Spieler der Owner ist */
		if (!event.getPlayer().getUniqueId().equals(database.getOwner(event.getEntity().getUniqueId()))) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onEntityUnleash(PlayerUnleashEntityEvent  event) {
		if (!plugin.isEnabled()) { return; }
		
		/* Datenbank-Verbindung aufbauen, falls nicht vorhanden. */
		if (!database.isConnected()) { database.connect(); }
		
		/* Prüfen ob das Entity ein Tier ist */
		if (!plugin.isAnimal(event.getEntity())) { return; }
		
		/* Prüfen ob das Entity gesichert wurde */
		if (database.containsAnimal(event.getEntity().getUniqueId())) { return; }
		
		/* Prüfen ob der Spieler die 'AnimalProtect-Bypass'-Permission hat */
		if (event.getPlayer().hasPermission("animalprotect.bypass")) { return; }
		
		/* Prüfen ob der Spieler der Owner ist */
		if (!event.getPlayer().getUniqueId().equals(database.getOwner(event.getEntity().getUniqueId()))) {
			event.setCancelled(true);
		}
	}
}
