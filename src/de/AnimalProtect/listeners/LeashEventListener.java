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
	
	private final AnimalProtect plugin;
	private final Database database;
	
	public LeashEventListener(final AnimalProtect plugin) {
		this.plugin = plugin;
		this.database = plugin.getDatenbank();
	}
	
	@EventHandler
	public void onEntityLeash(final PlayerLeashEntityEvent event) {
		try { 
			if (!this.plugin.isEnabled() || event.isCancelled()) { return; }
			
			/* Datenbank-Verbindung aufbauen, falls nicht vorhanden. */
			if (!this.database.isConnected()) { this.database.connect(); }
			
			/* Prüfen ob das Entity ein Tier ist */
			if (!this.plugin.isAnimal(event.getEntity())) { return; }
			
			/* Prüfen ob das Entity gesichert wurde */
			if (!this.database.containsAnimal(event.getEntity().getUniqueId())) { return; }
			
			/* Prüfen ob der Spieler die 'AnimalProtect-Bypass'-Permission hat */
			if (event.getPlayer().hasPermission("animalprotect.bypass")) { return; }
			
			/* Prüfen ob der Spieler der Owner ist */
			if (!event.getPlayer().getUniqueId().equals(this.database.getOwner(event.getEntity().getUniqueId()).getUniqueId())) {
				event.setCancelled(true);
			}
		}
		catch (final Exception e) { Messenger.exception("LeashEventListener/onEntityLeash", "Unknown Exception.", e); }
	}
	
	@EventHandler
	public void onEntityUnleash(final PlayerUnleashEntityEvent  event) {
		try { 
			if (!this.plugin.isEnabled() || event.isCancelled()) { return; }
			
			/* Datenbank-Verbindung aufbauen, falls nicht vorhanden. */
			if (!this.database.isConnected()) { this.database.connect(); }
			
			/* Prüfen ob das Entity ein Tier ist */
			if (!this.plugin.isAnimal(event.getEntity())) { return; }
			
			/* Prüfen ob das Entity gesichert wurde */
			if (!this.database.containsAnimal(event.getEntity().getUniqueId())) { return; }
			
			/* Prüfen ob der Spieler die 'AnimalProtect-Bypass'-Permission hat */
			if (event.getPlayer().hasPermission("animalprotect.bypass")) { return; }
			
			/* Prüfen ob der Spieler der Owner ist */
			if (!event.getPlayer().getUniqueId().equals(this.database.getOwner(event.getEntity().getUniqueId()).getUniqueId())) {
				event.setCancelled(true);
			}
		}
		catch (final Exception e) { Messenger.exception("LeashEventListener/onEntityUnleash", "Unknown Exception.", e); }
	}
}
