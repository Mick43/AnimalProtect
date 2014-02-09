package de.AnimalProtect.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import de.AnimalProtect.Main;
import de.AnimalProtect.MySQL;
import de.AnimalProtect.structs.EntityList;

public class DeathEventListener implements Listener {
	
	private Main plugin;
	private MySQL database;
	private EntityList list;
	
	public DeathEventListener(Main plugin) {
		this.plugin = plugin;
		this.database = plugin.database;
		this.list = plugin.list;
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		if (!database.checkConnection()) { database.openConnection(); }
		if (plugin.isEnabled() && database.checkConnection() && isAnimal(event.getEntity())) {
			
			String deathCause = event.getEntity().getLastDamageCause().toString();
			database.write("UPDATE ap_entities SET deathcause='"+deathCause+"' WHERE uuid='"+event.getEntity().getUniqueId()+"';", true);
			
			list.updateEntity(event.getEntity(), false);
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
		||  type == EntityType.OCELOT)
		{ return true; }
		return false;
	}
}
