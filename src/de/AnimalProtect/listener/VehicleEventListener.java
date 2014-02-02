package de.AnimalProtect.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;

import de.AnimalProtect.Main;
import de.AnimalProtect.MySQL;
import de.AnimalProtect.structs.EntityList;

public class VehicleEventListener implements Listener {
	
	private Main plugin;
	private MySQL database;
	private EntityList list;
	
	public VehicleEventListener(Main plugin) {
		this.plugin = plugin;
		this.database = plugin.database;
		this.list = plugin.list;
	}
	
	@EventHandler
	public void onVehicleEnter(VehicleEnterEvent event) {
		if (plugin.isEnabled() && database.checkConnection()) {
			// TODO: Listener -> onVehicleEnter
		}
	}
	
	@EventHandler
	public void onVehicleExit(VehicleExitEvent event) {
		if (plugin.isEnabled() && database.checkConnection()) {
			// TODO: Listener -> onVehicleExit
		}
	}
}
