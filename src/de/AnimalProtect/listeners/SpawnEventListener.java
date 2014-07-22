package de.AnimalProtect.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import de.AnimalProtect.AnimalProtect;

public class SpawnEventListener implements Listener {
	
	private final AnimalProtect plugin;
	private final int maxEntitiesPerChunk;
	
	public SpawnEventListener(final AnimalProtect plugin) {
		this.plugin = plugin;
		this.maxEntitiesPerChunk = this.plugin.getConfig().getInt("settings.max-entities-per-chunk");
	}
	
	@EventHandler
	public void onCreatureSpawn(final CreatureSpawnEvent event) {
		final SpawnReason reason = event.getSpawnReason();
		if (reason.equals(SpawnReason.BREEDING) || reason.equals(SpawnReason.EGG)) {
			int counter = 0;
			for (final Entity e : event.getEntity().getNearbyEntities(25, 25, 25)) {
				if (e.getType().equals(event.getEntity().getType()))
				{ counter++; }
			}
			
			if (counter == this.maxEntitiesPerChunk && this.maxEntitiesPerChunk > 2) { event.setCancelled(true); }
		}
	}
}
