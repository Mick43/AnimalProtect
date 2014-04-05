package de.AnimalProtectOld.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;

import de.AnimalProtectOld.Main;
import de.AnimalProtectOld.MySQL;
import de.AnimalProtectOld.structs.EntityList;

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
		/* Datenbank-Verbindung aufbauen, falls nicht vorhanden. */
		if (!database.checkConnection()) { database.openConnection(); }
		
		if (plugin.isEnabled() && database.checkConnection() && !event.isCancelled()) {
			if (!isAnimal(event.getVehicle())) { return; }
			Player player = (Player) event.getEntered();
			Entity entity = (Entity) event.getVehicle();
			
			/* Pr�fen ob der Spieler alles darf. */
			if (player.hasPermission("animalprotect.bypass")) {
				return;
			}
			
			String owner = list.getPlayer(entity.getUniqueId());
			
			if (owner != null) {
				if (!owner.equals(player.getName())) {
					event.setCancelled(true);
				}
			}
			
			return;
		}
	}
	
	@EventHandler
	public void onVehicleExit(VehicleExitEvent event) {
		if (plugin.isEnabled() && database.checkConnection() && !event.isCancelled()) {
			if (!isAnimal(event.getVehicle())) { return; }
			list.updateEntity(event.getVehicle(), false);
		}
	}
	
	private boolean isAnimal(Entity entity) {
		EntityType type = entity.getType();
		if (type == EntityType.SHEEP
		||  type == EntityType.PIG
		||  type == EntityType.COW
		||  type == EntityType.CHICKEN
		||  type == EntityType.HORSE
		||  type == EntityType.WOLF
		||  type == EntityType.IRON_GOLEM
		||  type == EntityType.SNOWMAN
		||  type == EntityType.VILLAGER
		||  type == EntityType.OCELOT)
		{ return true; }
		return false;
	}
}