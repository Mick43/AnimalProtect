package de.AnimalProtect.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import de.AnimalProtect.Main;
import de.AnimalProtect.MySQL;
import de.AnimalProtect.structs.EntityList;

public class DamageEventListener implements Listener {
	
	private Main plugin;
	private MySQL database;
	private EntityList list;
	
	public DamageEventListener(Main plugin) {
		this.plugin = plugin;
		this.database = plugin.database;
		this.list = plugin.list;
	}
	
	@EventHandler
	public void onEntityEvent(EntityDamageByEntityEvent event) {
		if (plugin.isEnabled() && database.checkConnection()) {
			// TODO: Listener -> onEntityDamage
		}
	}
}
