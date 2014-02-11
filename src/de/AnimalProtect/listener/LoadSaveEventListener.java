package de.AnimalProtect.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import de.AnimalProtect.Main;
import de.AnimalProtect.MySQL;
import de.AnimalProtect.structs.EntityList;
import de.AnimalProtect.utility.APLogger;

public class LoadSaveEventListener implements Listener {
	
	private Main plugin;
	private MySQL database;
	private EntityList list;
	
	public LoadSaveEventListener(Main plugin) {
		this.plugin = plugin;
		this.database = plugin.database;
		this.list = plugin.list;
	}
	

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (plugin.isEnabled() && database.checkConnection()) {
			list.connect(event.getPlayer().getName(), false, true);
			APLogger.info("Loading player "
							+ event.getPlayer().getName() + ". => "
							+ (list.lastActionSucceeded() ? "Success."
									: "Failed!"));
		}
		else if (!database.checkConnection()) {
			if (event.getPlayer().hasPermission("animalprotect.admin")) {
				event.getPlayer().sendMessage("§c[!] §7Warnung: Die Datenbankverbindung ist nicht aktiv!");
			}
		}
	}
}
