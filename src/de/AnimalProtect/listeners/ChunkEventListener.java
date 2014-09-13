package de.AnimalProtect.listeners;

import org.bukkit.World.Environment;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

import craftoplugin.core.CraftoMessenger;
import de.AnimalProtect.AnimalProtect;
import de.AnimalProtect.structs.Animal;

/**
 * Der ChunkEventListener fängt das {@link ChunkUnloadEvent} ab
 * und prüft ob sich ein gesichertes Tier in dem Chunk befindet.
 * 
 * @author Fear837, Pingebam
 * @version 1.0
 * @see Listener
 */
public class ChunkEventListener implements Listener {

	/** Die AnimalProtect-Instanz. */
	private final AnimalProtect plugin;

	/**
	 * Initialisiert den EventListener.
	 * @param plugin - Das AnimalProtect-Plugin.
	 */
	public ChunkEventListener(final AnimalProtect plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onChunkUnload(final ChunkUnloadEvent event) {
		if (event.getWorld().getEnvironment().equals(Environment.NORMAL)) {
			try {
				if (this.plugin.getQueue() != null) {
					synchronized (this.plugin.getQueue()) {
						if (this.plugin.getQueue().isRunning()) {
							for (final Entity e : event.getChunk().getEntities()) {
								if (this.plugin.getDatenbank().containsAnimal(e.getUniqueId())) {
									final Animal animal = this.plugin.getDatenbank().getAnimal(e.getUniqueId());
									animal.updateAnimal(e);
									animal.saveToDatabase(true);
								}
							}
						}
					}
				}
			}
			catch (final Exception e) { CraftoMessenger.exception("ChunkEventListener.java/onChunkUnload()", "Failed to handle ChunkUnloadEvent.", e); }
		}
	}
}