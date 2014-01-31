package de.Fear837.listener;

import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

import de.Fear837.structs.EntityList;

public class EntityUnloadListener implements Listener{
	
	private EntityList list;
	
	public EntityUnloadListener(EntityList list) {
		this.list = list;
	}
	
	@EventHandler
	public void onEntityUnload(ChunkUnloadEvent event) {
		Entity[] entityArray = event.getChunk().getEntities();
		
		for (Entity e : entityArray) {
			if (list.containsEntity(e)) {
				// TODO e.UpdateEntity(e);
			}
		}
	}
}
