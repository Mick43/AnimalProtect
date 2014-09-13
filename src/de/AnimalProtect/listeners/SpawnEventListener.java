package de.AnimalProtect.listeners;

import org.bukkit.Effect;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import de.AnimalProtect.AnimalProtect;

/**
 * Der SpawnEventListener fängt das {@link CreatureSpawnEvent} ab
 * und prüft ob schon genug Tiere in dem jeweiligen Chunk existieren.
 * 
 * @author Fear837, Pingebam
 * @version 1.0
 * @see Listener
 */
public class SpawnEventListener implements Listener {

	/** Die AnimalProtect-Instanz. */
	private final AnimalProtect plugin;
	/** Wieviele Entities maximal pro Chunk existieren dürfen. 
	 * Wird in der Config von {@code settings.max-entities-per-chunk} festgelegt. */
	private final int maxEntitiesPerChunk;

	/**
	 * Initialisiert den EventListener.
	 * @param plugin - Das AnimalProtect-Plugin.
	 */
	public SpawnEventListener(final AnimalProtect plugin) {
		this.plugin = plugin;
		this.maxEntitiesPerChunk = this.plugin.getConfig().getInt("settings.max-entities-per-chunk");
	}

	@EventHandler
	public void onCreatureSpawn(final CreatureSpawnEvent event) {
		final SpawnReason reason = event.getSpawnReason();
		if (reason.equals(SpawnReason.BREEDING) || reason.equals(SpawnReason.EGG) || reason.equals(SpawnReason.SPAWNER_EGG)) {
			int counter = 0;
			for (final Entity e : event.getEntity().getNearbyEntities(25, 25, 25)) {
				if (e.getType().equals(event.getEntity().getType()))
				{ counter++; }
			}

			if (counter >= this.maxEntitiesPerChunk && this.maxEntitiesPerChunk > 2) 
			{ event.setCancelled(true); event.getLocation().getWorld().playEffect(event.getLocation(), Effect.SMOKE, 4); }
		}
	}
}