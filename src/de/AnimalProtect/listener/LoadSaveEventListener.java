package de.AnimalProtect.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import de.AnimalProtect.Main;
import de.AnimalProtect.MySQL;
import de.AnimalProtect.structs.EntityList;

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
			// TODO: Listener -> onPlayerJoin
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (plugin.isEnabled() && database.checkConnection()) {
			// TODO: Listener -> onPlayerLeave
		}
	}
}
