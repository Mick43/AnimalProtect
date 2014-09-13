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

/**
 * Der PlayerEventListener fängt das {@link PlayerJoinEvent} ab
 * und prüft ob der Spieler bereits im Arbeitsspeicher liegt.
 * 
 * @author Fear837, Pingebam
 * @version 1.0
 * @see Listener
 */
public class PlayerEventListener implements Listener {

	/** Die AnimalProtect-Instanz. */
	private final AnimalProtect plugin;
	/** Einen Verweis auf die AnimalProtect-Datenbank. */
	private final Database database;

	/**
	 * Initialisiert den EventListener.
	 * @param plugin - Das AnimalProtect-Plugin.
	 */
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

	/**
	 * @param player - Der angegebene Spieler.
	 * @return True, wenn der angegebene Spieler ein Administrator ist.
	 */
	public boolean isAdmin(final Player player) {
		if (player.hasPermission("animalprotect.admin") || player.hasPermission("craftoplugin.admin") || player.isOp())
		{ return true; }
		return false;
	}
}