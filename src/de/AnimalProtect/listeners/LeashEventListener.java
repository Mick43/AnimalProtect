package de.AnimalProtect.listeners;

/* Bukkit Imports */
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;


/* AnimalProtect Imports */
import de.AnimalProtect.AnimalProtect;
import de.AnimalProtect.Database;
import de.AnimalProtect.Messenger;

public class LeashEventListener implements Listener {
	
	private AnimalProtect plugin;
	private Database database;
	
	public LeashEventListener(AnimalProtect plugin) {
		this.plugin = plugin;
		this.database = plugin.getDatenbank();
	}
	
	@EventHandler
	public void onEntityLeash(PlayerLeashEntityEvent event) {
		try { 
			if (!plugin.isEnabled() || event.isCancelled()) { return; }
			
			/* Datenbank-Verbindung aufbauen, falls nicht vorhanden. */
			if (!database.isConnected()) { database.connect(); }
			
			/* Prüfen ob das Entity ein Tier ist */
			if (!plugin.isAnimal(event.getEntity())) { return; }
			
			/* Prüfen ob das Entity gesichert wurde */
			if (!database.containsAnimal(event.getEntity().getUniqueId())) { return; }
			
			/* Prüfen ob der Spieler die 'AnimalProtect-Bypass'-Permission hat */
			if (event.getPlayer().hasPermission("animalprotect.bypass")) { return; }
			
			/* Prüfen ob der Spieler der Owner ist */
			if (!event.getPlayer().getUniqueId().equals(database.getOwner(event.getEntity().getUniqueId()).getUniqueId())) {
				event.setCancelled(true);
			}
		}
		catch (Exception e) { Messenger.exception("LeashEventListener/onEntityLeash", "Unknown Exception.", e); }
	}
	
	@EventHandler
	public void onEntityUnleash(PlayerUnleashEntityEvent  event) {
		try { 
			if (!plugin.isEnabled()) { return; }
			
			/* Datenbank-Verbindung aufbauen, falls nicht vorhanden. */
			if (!database.isConnected()) { database.connect(); }
			
			/* Prüfen ob das Entity ein Tier ist */
			if (!plugin.isAnimal(event.getEntity())) { return; }
			
			/* Prüfen ob das Entity gesichert wurde */
			if (!database.containsAnimal(event.getEntity().getUniqueId())) { return; }
			
			/* Prüfen ob der Spieler die 'AnimalProtect-Bypass'-Permission hat */
			if (event.getPlayer().hasPermission("animalprotect.bypass")) { return; }
			
			/* Prüfen ob der Spieler der Owner ist */
			if (!event.getPlayer().getUniqueId().equals(database.getOwner(event.getEntity().getUniqueId()).getUniqueId())) {
				event.setCancelled(true);
			}
		}
		catch (Exception e) { Messenger.exception("LeashEventListener/onEntityUnleash", "Unknown Exception.", e); }
	}
}
