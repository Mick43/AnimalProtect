package de.AnimalProtect.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

import de.AnimalProtect.Main;
import de.AnimalProtect.MySQL;
import de.AnimalProtect.structs.EntityList;

public class TargetEventListener implements Listener {

	private Main plugin;
	private MySQL database;
	private EntityList list;
	
	public TargetEventListener(Main plugin) {
		this.plugin = plugin;
		this.database = plugin.database;
		this.list = plugin.list;
	}
	
	@EventHandler
	public void onEntityEvent(EntityTargetLivingEntityEvent  event) {
		if (!database.checkConnection()) { database.openConnection(); }
		if (plugin.isEnabled() && database.checkConnection() && !event.isCancelled()) {
			// TODO: EntityTargetLivingEntityEvent!
			// Das Event soll gecancelled werden wenn das Target nicht der Owner ist!
		}
	}
}
