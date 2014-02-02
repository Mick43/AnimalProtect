package de.AnimalProtect.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.AnimalProtect.Main;
import de.AnimalProtect.MySQL;
import de.AnimalProtect.structs.EntityList;

public class lockdebug implements CommandExecutor {

	Main plugin;
	MySQL database;
	EntityList list;
	
	public lockdebug(Main plugin) {
		this.plugin = plugin;
		this.database = plugin.database;
		this.list = plugin.list;
	}
	
	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if (plugin.isEnabled() && database.checkConnection()) {
			cs.sendMessage("Current Entities in RAM: " + list.sizeOfEntitiesInRam());
			cs.sendMessage("Current Players in RAM: " + list.sizeOfPlayers());
			cs.sendMessage("Current locks in RAM: " + list.sizeOfLocks());
			cs.sendMessage("Database connection stable: " + database.checkConnection());
		}
		
		return false;
	}
}
