package de.AnimalProtect.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.AnimalProtect.Main;
import de.AnimalProtect.MySQL;
import de.AnimalProtect.structs.EntityList;

public class ap implements CommandExecutor {
	
	Main plugin;
	MySQL database;
	
	public ap(Main plugin) {
		this.plugin = plugin;
		this.database = plugin.database;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if (database.checkConnection()) { database.openConnection(); }
		
		if (plugin.isEnabled() && database.checkConnection() && cs instanceof Player) {
			Player player = (Player)cs;
			
			/* Argumente überprüfen */
			if (args.length == 0) { help(player); }
			
			else {
				String[] newArgs = new String[args.length -1];
				for (int i = 1; i<newArgs.length; i++) { newArgs[i-1] = args[i]; }
				
				if (args[0].equalsIgnoreCase("help"))
				{ help(player); }
				else if (args[0].equalsIgnoreCase("lock"))
				{ new lockanimal(plugin).onCommand(cs, cmd, label, newArgs); }
				else if (args[0].equalsIgnoreCase("debug"))
				{ new lockdebug(plugin).onCommand(cs, cmd, label, newArgs); }
				else if (args[0].equalsIgnoreCase("info")) 
				{ new lockinfo(plugin).onCommand(cs, cmd, label, newArgs); }
				else if (args[0].equalsIgnoreCase("limit"))
				{ new locklimit(plugin).onCommand(cs, cmd, label, newArgs); }
				else if (args[0].equalsIgnoreCase("list"))
				{ new locklist(plugin).onCommand(cs, cmd, label, newArgs); }
				else if (args[0].equalsIgnoreCase("respawn")) 
				{ new lockrespawn(plugin).onCommand(cs, cmd, label, newArgs); }
				else if (args[0].equalsIgnoreCase("tp"))
				{ new locktp(plugin).onCommand(cs, cmd, label, newArgs); }
				else if (args[0].equalsIgnoreCase("unlock"))
				{ new unlockanimal(plugin).onCommand(cs, cmd, label, newArgs); }
				else if (args[0].equalsIgnoreCase("reload"))
				{
					plugin.reloadConfig();
					plugin.initializeDatabase();
					plugin.list = new EntityList(plugin, false);
					player.sendMessage("§aDas Plugin wurde erfolgreich reloaded!");
				}
			}
		}
		
		return true;
	}
	
	private void help(Player p) {
		p.sendMessage("§e--------- §fHelp: AnimalProtect §e-------------");
		p.sendMessage("§7Below is a list of all AnimalProtect commands:");
		p.sendMessage("§6/ap lock: §f"+plugin.getCommand("lockanimal").getDescription());
		p.sendMessage("§6/ap debug: §f"+plugin.getCommand("lockdebug").getDescription());
		p.sendMessage("§6/ap info: §f"+plugin.getCommand("lockinfo").getDescription());
		p.sendMessage("§6/ap limit: §f"+plugin.getCommand("locklimit").getDescription());
		p.sendMessage("§6/ap list: §f"+plugin.getCommand("locklist").getDescription());
		p.sendMessage("§6/ap respawn: §f"+plugin.getCommand("lockrespawn").getDescription());
		p.sendMessage("§6/ap tp: §f"+plugin.getCommand("locktp").getDescription());
		p.sendMessage("§6/ap unlock: §f"+plugin.getCommand("unlockanimal").getDescription());
		p.sendMessage("§6/ap reload: §fReloaded das Plugin.");
	}
}
