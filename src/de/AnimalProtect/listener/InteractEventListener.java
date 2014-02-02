package de.AnimalProtect.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityInteractEvent;

import de.AnimalProtect.Main;
import de.AnimalProtect.MySQL;
import de.AnimalProtect.structs.EntityList;

public class InteractEventListener implements Listener {
	
	private Main plugin;
	private MySQL database;
	private EntityList list;
	
	public InteractEventListener(Main plugin) {
		this.plugin = plugin;
		this.database = plugin.database;
		this.list = plugin.list;
	}
	
	@EventHandler
	public void onEntityEvent(EntityInteractEvent event) {
		if (plugin.isEnabled() && database.checkConnection()) {
			// TODO: Listener -> onEntityDamage
		}
	}
}
