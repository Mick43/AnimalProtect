package de.AnimalProtect.listeners;

/* Bukkit Imports */
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/* AnimalProtect Imports */
import de.AnimalProtect.AnimalProtect;
import de.AnimalProtect.Database;
import de.AnimalProtect.Messenger;

public class PlayerEventListener implements Listener {
	
	private final AnimalProtect plugin;
	private final Database database;
	
	public PlayerEventListener(final AnimalProtect plugin) {
		this.plugin = plugin;
		this.database = plugin.getDatenbank();
	}
	
	@EventHandler
	public void onPlayerJoin(final PlayerJoinEvent event) {
		try {
			if (!this.plugin.isEnabled()) { return; }
			
			if (this.isAdmin(event.getPlayer()) && !this.database.isConnected()) 
			{ Messenger.messageStaff("§c[!] §7Warnung: AnimalProtect konnte keine Verbindung zur Datenbank herstellen!"); }
			
			this.database.insertPlayer(event.getPlayer().getUniqueId());
		}
		catch (final Exception e) { Messenger.exception("PlayerEventListener/onPlayerJoin", "Unknown Exception.", e); }
	}
	
	public boolean isAdmin(final Player player) {
		if (player.hasPermission("animalprotect.admin") || player.hasPermission("craftoplugin.admin") || player.isOp())
		{ return true; }
		return false;
	}
}
