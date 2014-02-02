package de.AnimalProtect.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import de.AnimalProtect.Main;
import de.AnimalProtect.MySQL;
import de.AnimalProtect.structs.EntityList;

public class DeathEventListener implements Listener {
	
	private Main plugin;
	private MySQL database;
	private EntityList list;
	
	public DeathEventListener(Main plugin) {
		this.plugin = plugin;
		this.database = plugin.database;
		this.list = plugin.list;
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		if (plugin.isEnabled() && database.checkConnection()) {
			// TODO: Listener -> onEntityDeath
		}
	}
}
