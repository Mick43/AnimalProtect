package de.AnimalProtect.listeners;

import org.bukkit.World.Environment;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

import de.AnimalProtect.AnimalProtect;
import de.AnimalProtect.structs.Animal;

public class ChunkEventListener implements Listener {
	
	private final AnimalProtect plugin;
	
	public ChunkEventListener(AnimalProtect plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onChunkLoad(ChunkUnloadEvent event) {
		if (event.getWorld().getEnvironment().equals(Environment.NORMAL)) {
			synchronized (plugin) {
				for (Entity e : event.getChunk().getEntities()) {
					if (plugin.getDatenbank().containsAnimal(e.getUniqueId())) {
						Animal animal = plugin.getDatenbank().getAnimal(e.getUniqueId());
						animal.updateAnimal(e);
						animal.saveToDatabase(true);
					}
				}
			}
		}
	}
}