package de.Fear837.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import de.Fear837.structs.EntityList;

public class EntityUnloadListener implements Listener{
	
	private EntityList list;
	
	public EntityUnloadListener(EntityList list) {
		this.list = list;
	}
	
	@EventHandler
	public void onEntityUnload(ChunkUnloadEvent event) {
		/*Entity[] entityArray = event.getChunk().getEntities();
		
		for (Entity e : entityArray) {
			if (list.containsEntity(e)) {
				list.updateEntity(e, false);
			}
		}*/
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		if (isAnimal(event.getEntity())) {
			if (list.containsEntity(event.getEntity())) {
				list.updateEntity(event.getEntity(), false);
			}
		}
	}
	
	public boolean isAnimal(Entity entity) {
		if (entity ==  null) { return false; }
		else { 
			if (entity.getType() == EntityType.COW 
					|| entity.getType() == EntityType.PIG
					|| entity.getType() == EntityType.SHEEP
					|| entity.getType() == EntityType.CHICKEN
					|| entity.getType() == EntityType.HORSE
					|| entity.getType() == EntityType.WOLF) {
				return true;
			}
		}
		return false;
	}
}
