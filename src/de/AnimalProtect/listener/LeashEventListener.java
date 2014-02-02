package de.AnimalProtect.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityUnleashEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;

import de.AnimalProtect.Main;
import de.AnimalProtect.MySQL;
import de.AnimalProtect.structs.EntityList;

public class LeashEventListener implements Listener {
	
	private Main plugin;
	private MySQL database;
	private EntityList list;
	
	public LeashEventListener(Main plugin) {
		this.plugin = plugin;
		this.database = plugin.database;
		this.list = plugin.list;
	}
	
	@EventHandler
	public void onEntityLeash(PlayerLeashEntityEvent event) {
		if (plugin.isEnabled() && database.checkConnection()) {
			// TODO: Listener -> onEntityLeash
		}
	}
	
	@EventHandler
	public void onEntityUnleash(EntityUnleashEvent event) {
		if (plugin.isEnabled() && database.checkConnection()) {
			// TODO: Listener -> onEntityUnleash
		}
	}
}
