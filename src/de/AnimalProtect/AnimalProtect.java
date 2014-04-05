package de.AnimalProtect;

import java.util.UUID;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

import de.AnimalProtect.listeners.InteractEventListener;

public class AnimalProtect extends JavaPlugin {
	
	private Database database;
	private boolean debugmode;
	
	@Override
	public void onEnable() {
		/* Konsole benachrichtigen */
		Messenger.log("Initializing AnimalProtect...");
		
		/* Die Config laden */
		initializeConfig();
		
		/* Die Datenbank laden */
		initializeDatabase();
	}
	
	@Override
	public void onDisable() {
		
	}
	
	private void initializeConfig() {
		Messenger.log("  Loading Config...");
		
		try {
			getConfig().options().copyDefaults(true);
			saveConfig();
		}
		catch (Exception e) { 
			Messenger.warn("Failed to load the config file!");
		}
	}
	
	private void initializeDatabase() {
		Messenger.log(" Loading Database...");
		
		this.database = new Database(this);
	}
	
	public boolean isDebugging() {
		return debugmode;
	}
	
	public boolean isAnimal(Entity entity) {
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
	
	public Entity getSelectedAnimal(UUID uuid) {
		return InteractEventListener.getSelected(uuid);
	}
	
	public Database getDatenbank() {
		return database;
	}
}
