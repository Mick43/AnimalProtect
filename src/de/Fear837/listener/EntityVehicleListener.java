package de.Fear837.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;

import de.Fear837.structs.EntityList;

public class EntityVehicleListener implements Listener {

	private EntityList list;
	
	public EntityVehicleListener(EntityList list) {
		this.list = list;
	}

	@EventHandler
	public void onEntityEnter(VehicleEnterEvent event) {
		if (!event.isCancelled()) {
			if (event.getVehicle().getType() == EntityType.HORSE || event.getVehicle().getType() == EntityType.PIG) {
				if (event.getEntered().getType() == EntityType.PLAYER) {
					Player player = (Player) event.getEntered();
					Entity entity = (Entity) event.getVehicle();
					
					if (player.isSneaking()) { 
						event.setCancelled(true); 
						return; }
					else {
						if (list.containsEntity(entity)) {
							if (!list.getPlayer(entity).equals(player.getName())) {
								event.setCancelled(true);
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onEntityExit(VehicleExitEvent event) {
		if (!event.isCancelled()) {
			if (event.getVehicle().getType() == EntityType.HORSE || event.getVehicle().getType() == EntityType.PIG) {
				if (list.containsEntity(event.getVehicle())) {
					list.updateEntity(event.getVehicle(), false);
				}
			}
		}
	}
	
}
