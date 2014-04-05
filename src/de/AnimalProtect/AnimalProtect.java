package de.AnimalProtect;

import org.bukkit.plugin.java.JavaPlugin;

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
}
