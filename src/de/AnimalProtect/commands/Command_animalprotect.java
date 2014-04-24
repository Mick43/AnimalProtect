package de.AnimalProtect.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.AnimalProtect.AnimalProtect;
import de.AnimalProtect.Messenger;

public class Command_animalprotect implements CommandExecutor {
	
	private AnimalProtect plugin;
	
	public Command_animalprotect(AnimalProtect plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if (!plugin.isEnabled()) { return false; }
		
		if (cmd.getName().equalsIgnoreCase("ap") || cmd.getName().equalsIgnoreCase("animalprotect")) { /*  /ap <args0>  */
			String[] newArgs = args;
			if (args.length > 0) {
				newArgs = new String[args.length-1];
				for (int i=0; i<newArgs.length; i++) { newArgs[i] = args[i+1]; }
			}
			
			if (args.length < 1) { Command_animalprotect.Command_ShowHelp(cs, newArgs); }
			else if (args[0].equalsIgnoreCase("help")) { Command_animalprotect.Command_ShowHelp(cs, newArgs); }
			else if (args[0].equalsIgnoreCase("animaldebug")) { Command_debug.runCommand(cs, newArgs); }
			else if (args[0].equalsIgnoreCase("listanimals")) { Command_list.runCommand(cs, newArgs); }
			else if (args[0].equalsIgnoreCase("lockanimal")) { Command_lock.runCommand(cs, newArgs); }
			else if (args[0].equalsIgnoreCase("lockedanimals")) { Command_limit.runCommand(cs, newArgs); }
			else if (args[0].equalsIgnoreCase("animalinfo")) { Command_info.runCommand(cs, newArgs); }
			else if (args[0].equalsIgnoreCase("respawnanimal")) { Command_respawn.runCommand(cs, newArgs); }
			else if (args[0].equalsIgnoreCase("tpanimal")) { Command_teleport.runCommand(cs, newArgs); }
			else if (args[0].equalsIgnoreCase("unlockanimal")) { Command_unlock.runCommand(cs, newArgs); }
			else if (args[0].equalsIgnoreCase("debug")) { Command_debug.runCommand(cs, newArgs); }
			else if (args[0].equalsIgnoreCase("list")) { Command_list.runCommand(cs, newArgs); }
			else if (args[0].equalsIgnoreCase("lock")) { Command_lock.runCommand(cs, newArgs); }
			else if (args[0].equalsIgnoreCase("limit")) { Command_limit.runCommand(cs, newArgs); }
			else if (args[0].equalsIgnoreCase("info")) { Command_info.runCommand(cs, newArgs); }
			else if (args[0].equalsIgnoreCase("respawn")) { Command_respawn.runCommand(cs, newArgs); }
			else if (args[0].equalsIgnoreCase("tp")) { Command_teleport.runCommand(cs, newArgs); }
			else if (args[0].equalsIgnoreCase("unlock")) { Command_unlock.runCommand(cs, newArgs); }
			else if (args[0].equalsIgnoreCase("reload")) { Command_animalprotect.Command_Reload(cs, newArgs); }
			else { Messenger.sendMessage(cs, "UNKNOWN_COMMAND"); }
		}
		return true;
	}
	
	public static void Command_ShowHelp(CommandSender cs, String[] args) {
		try {
			Messenger.help(cs, "AnimalProtect Help (1/1)");
			Messenger.sendMessage(cs, "§7§oEine Übersicht aller AnimalProtect-Kommandos");
			if (hasPerm(cs, "animalprotect.protect")) { Messenger.sendMessage(cs, "§6/ap help: §fRuft diese Hilfe auf."); }
			if (hasPerm(cs, "animalprotect.protect")) { Messenger.sendMessage(cs, "§6/ap lock: §f" + AnimalProtect.plugin.getCommand("lockanimal").getDescription());       }
			if (hasPerm(cs, "animalprotect.protect")) { Messenger.sendMessage(cs, "§6/ap unlock: §f" + AnimalProtect.plugin.getCommand("unlockanimal").getDescription());   }
			if (hasPerm(cs, "animalprotect.admin"))   { Messenger.sendMessage(cs, "§6/ap list: §f" + AnimalProtect.plugin.getCommand("listanimals").getDescription());      }
			if (hasPerm(cs, "animalprotect.protect")) { Messenger.sendMessage(cs, "§6/ap limit: §f" + AnimalProtect.plugin.getCommand("lockedanimals").getDescription());   }
			if (hasPerm(cs, "animalprotect.protect")) { Messenger.sendMessage(cs, "§6/ap info: §f" + AnimalProtect.plugin.getCommand("animalinfo").getDescription());   	}
			if (hasPerm(cs, "animalprotect.admin"))   { Messenger.sendMessage(cs, "§6/ap respawn: §f" + AnimalProtect.plugin.getCommand("respawnanimal").getDescription()); }
			if (hasPerm(cs, "animalprotect.admin"))   { Messenger.sendMessage(cs, "§6/ap tp: §f" + AnimalProtect.plugin.getCommand("tpanimal").getDescription());           }
			if (hasPerm(cs, "animalprotect.admin"))   { Messenger.sendMessage(cs, "§6/ap debug: §f" + AnimalProtect.plugin.getCommand("animaldebug").getDescription());     }
			if (hasPerm(cs, "animalprotect.admin"))   { Messenger.sendMessage(cs, "§6/ap reload: §f" + "Lädt das gesamte Plugin neu.");                                     }
		}
		catch (Exception e) {
			Messenger.sendMessage(cs, "§cDie Hilfe von AnimalProtect ist zurzeit nicht verfügbar.");
			Messenger.exception("Command_AnimalProtect/Command_ShowHelp", "Caught an exception while trying to show someone the help page.", e);
		}
	}
	
	public static void Command_Reload(CommandSender cs, String[] args) {
		if (cs.hasPermission("animalprotect.admin")) {
			if (args.length == 0) {
				Bukkit.getServer().getPluginManager().disablePlugin(AnimalProtect.plugin);
				Bukkit.getServer().getPluginManager().enablePlugin(AnimalProtect.plugin);
				
				Messenger.sendMessage(cs, "RELOAD_SUCCESS_PLUGIN");
			}
			else if (args.length == 1) {
				if (args[0].equalsIgnoreCase("config")) {
					AnimalProtect.plugin.reloadConfig();
					
					Messenger.sendMessage(cs, "RELOAD_SUCCESS_CONFIG");
				}
				else if (args[0].equalsIgnoreCase("database")) {
					AnimalProtect.plugin.reloadDatabase();
					
					if (AnimalProtect.plugin.getDatenbank().isConnected()) 
					{ Messenger.sendMessage(cs, "RELOAD_SUCCESS_DATABASE"); }
					else { Messenger.sendMessage(cs, "RELOAD_FAILED_DATABASE"); }
				}
				else if (args[0].equalsIgnoreCase("connection")) {
					AnimalProtect.plugin.getDatenbank().closeConnection();
					AnimalProtect.plugin.getDatenbank().connect();
					
					if (AnimalProtect.plugin.getDatenbank().isConnected()) 
					{ Messenger.sendMessage(cs, "RELOAD_SUCCESS_CONNECTION"); }
					else { Messenger.sendMessage(cs, "RELOAD_FAILED_CONNECTION"); }
				}
			}
			else if (args.length > 1) { Messenger.sendMessage(cs, "TOO_MANY_ARGUMENTS"); }
		}
		else { Messenger.sendMessage(cs, "NO_PERMISSION"); }
	}
	
	private static boolean hasPerm(CommandSender cs, String permission) {
		if (cs instanceof Player) {
			if (((Player)cs).hasPermission(permission)) {
				return true;
			}
			return false;
		}
		else { return true; }
	}
}
