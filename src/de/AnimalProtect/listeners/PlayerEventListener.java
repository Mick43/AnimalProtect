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
	
	private AnimalProtect plugin;
	private Database database;
	
	public PlayerEventListener(AnimalProtect plugin) {
		this.plugin = plugin;
		this.database = plugin.getDatenbank();
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		try {
			if (!plugin.isEnabled()) { return; }
			
			if (isAdmin(event.getPlayer()) && !database.isConnected()) {
				Messenger.messageStaff("§c[!] §7Warnung: AnimalProtect konnte keine Verbindung zur Datenbank herstellen!");
			}
		}
		catch (Exception e) { Messenger.exception("PlayerEventListener/onPlayerJoin", "Unknown Exception.", e); }
	}
	
	public boolean isAdmin(Player player) {
		if (player.hasPermission("animalprotect.admin") || player.hasPermission("craftoplugin.admin") || player.isOp())
		{ return true; }
		return false;
	}
}
