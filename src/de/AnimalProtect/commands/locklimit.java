package de.AnimalProtect.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.AnimalProtect.Main;
import de.AnimalProtect.MySQL;
import de.AnimalProtect.structs.EntityList;

public class locklimit implements CommandExecutor {
	
	Main plugin;
	MySQL database;
	EntityList list;
	
	public locklimit(Main plugin) {
		this.plugin = plugin;
		this.database = plugin.database;
		this.list = plugin.list;
	}
	
	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if (!database.checkConnection()) { database.openConnection(); }
		if (plugin.isEnabled() && database.checkConnection() && cs instanceof Player) {
			if (args.length == 0) {
				String player = ((Player) cs).getName();
				Long count = list.sizeOfEntities(player);
				cs.sendMessage("§eDu hast §6"+count+"§e von "+list.MAX_ENTITIES_FOR_PLAYER+" Tieren protectet.");
			}
			else if (args.length == 1) {
				String player = args[0];
				Long count = list.sizeOfEntities(player);
				cs.sendMessage("§eDer Spieler "+player+" hat §6"+count+"§e von "+list.MAX_ENTITIES_FOR_PLAYER+" Tieren protectet.");
			}
			else {
				cs.sendMessage("§cFehler: Zu viele Argumente! (/locklimit <name>)");
			}
		}
		return true;
	}
}
