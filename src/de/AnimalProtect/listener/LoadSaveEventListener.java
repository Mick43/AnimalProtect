package de.AnimalProtect.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import de.AnimalProtect.Main;
import de.AnimalProtect.MySQL;

public class LoadSaveEventListener implements Listener {
	
	private MySQL database;
	
	public LoadSaveEventListener(Main plugin) {
		this.database = plugin.database;
	}
	

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		try {
			if (!database.checkConnection() || database.getConnection().isClosed()) {
				if (event.getPlayer().hasPermission("animalprotect.admin")) {
					event.getPlayer().sendMessage("§c[!] §7Warnung: Die Datenbankverbindung ist nicht aktiv!");
				}
			}
		}
		catch (Exception e) { event.getPlayer().sendMessage("§c[!] §7Warnung: Die Datenbankverbindung ist nicht aktiv/null!"); }
	}
}
