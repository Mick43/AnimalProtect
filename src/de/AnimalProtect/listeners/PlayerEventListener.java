package de.AnimalProtect.listeners;

import org.bukkit.event.Listener;

import de.AnimalProtect.AnimalProtect;
import de.AnimalProtect.Database;

public class PlayerEventListener implements Listener {
	
	private AnimalProtect plugin;
	private Database database;
	
	public PlayerEventListener(AnimalProtect plugin) {
		this.plugin = plugin;
		//this.database = AnimalProtect.getDatenbank();
	}
}
