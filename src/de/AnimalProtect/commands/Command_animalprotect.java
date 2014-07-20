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
		if (plugin == null || !plugin.isEnabled()) { return false; }
		
		if (cmd.getName().equalsIgnoreCase("ap") || cmd.getName().equalsIgnoreCase("animalprotect")) { /*  /ap <args0>  */
			String[] newArgs = args;
			if (args.length > 0) {
				newArgs = new String[args.length-1];
				for (int i=0; i<newArgs.length; i++) { newArgs[i] = args[i+1]; }
			}
			
			try {
				if (args.length < 1) { this.Command_ShowHelp(cs, newArgs); }
				else if (args[0].equalsIgnoreCase("help")) { this.Command_ShowHelp(cs, newArgs); }
				else if (args[0].equalsIgnoreCase("animaldebug")) { plugin.getCommand("animaldebug").getExecutor().onCommand(cs, cmd, label, newArgs); }
				else if (args[0].equalsIgnoreCase("listanimals")) { plugin.getCommand("listanimals").getExecutor().onCommand(cs, cmd, label, newArgs); }
				else if (args[0].equalsIgnoreCase("lockanimal")) { plugin.getCommand("lockanimal").getExecutor().onCommand(cs, cmd, label, newArgs); }
				else if (args[0].equalsIgnoreCase("lockedanimals")) {plugin.getCommand("lockedanimals").getExecutor().onCommand(cs, cmd, label, newArgs); }
				else if (args[0].equalsIgnoreCase("animalinfo")) { plugin.getCommand("animalinfo").getExecutor().onCommand(cs, cmd, label, newArgs); }
				else if (args[0].equalsIgnoreCase("respawnanimal")) { plugin.getCommand("respawnanimal").getExecutor().onCommand(cs, cmd, label, newArgs); }
				else if (args[0].equalsIgnoreCase("tpanimal")) { plugin.getCommand("tpanimal").getExecutor().onCommand(cs, cmd, label, newArgs); }
				else if (args[0].equalsIgnoreCase("unlockanimal")) { plugin.getCommand("unlockanimal").getExecutor().onCommand(cs, cmd, label, newArgs); }
				else if (args[0].equalsIgnoreCase("debug")) { plugin.getCommand("animaldebug").getExecutor().onCommand(cs, cmd, label, newArgs); }
				else if (args[0].equalsIgnoreCase("queue")) { plugin.getCommand("animalqueue").getExecutor().onCommand(cs, cmd, label, newArgs); }
				else if (args[0].equalsIgnoreCase("list")) { plugin.getCommand("listnanimals").getExecutor().onCommand(cs, cmd, label, newArgs); }
				else if (args[0].equalsIgnoreCase("lock")) { plugin.getCommand("lockanimal").getExecutor().onCommand(cs, cmd, label, newArgs); }
				else if (args[0].equalsIgnoreCase("limit")) { plugin.getCommand("lockedanimals").getExecutor().onCommand(cs, cmd, label, newArgs); }
				else if (args[0].equalsIgnoreCase("info")) { plugin.getCommand("animalinfo").getExecutor().onCommand(cs, cmd, label, newArgs); }
				else if (args[0].equalsIgnoreCase("respawn")) { plugin.getCommand("respawnanimal").getExecutor().onCommand(cs, cmd, label, newArgs); }
				else if (args[0].equalsIgnoreCase("tp")) { plugin.getCommand("tpanimal").getExecutor().onCommand(cs, cmd, label, newArgs); }
				else if (args[0].equalsIgnoreCase("unlock")) { plugin.getCommand("unlockanimal").getExecutor().onCommand(cs, cmd, label, newArgs); }
				else if (args[0].equalsIgnoreCase("reload")) { this.Command_Reload(cs, newArgs); }
				else { Messenger.sendMessage(cs, "UNKNOWN_COMMAND"); }
			}
			catch (Exception e) { 
				Messenger.sendMessage(cs, "UNKNOWN_COMMAND"); 
				Messenger.exception("Command_animalprotect.java/onCommand()", "Failed to parse command", e); 
			}
		}
		return true;
	}
	
	public void Command_ShowHelp(CommandSender cs, String[] args) {
		try {
			Messenger.help(cs, "HELP_HEADER");
			Messenger.sendMessage(cs, "HELP_DESC");
			if (hasPerm(cs, "animalprotect.protect")) { Messenger.sendMessage(cs, "§6/ap help: §fRuft diese Hilfe auf."); }
			if (hasPerm(cs, "animalprotect.lock")) { Messenger.sendMessage(cs, "§6/ap lock: §f" + AnimalProtect.plugin.getCommand("lockanimal").getDescription());       }
			if (hasPerm(cs, "animalprotect.protect")) { Messenger.sendMessage(cs, "§6/ap unlock: §f" + AnimalProtect.plugin.getCommand("unlockanimal").getDescription());   }
			if (hasPerm(cs, "animalprotect.admin"))   { Messenger.sendMessage(cs, "§6/ap list: §f" + AnimalProtect.plugin.getCommand("listanimals").getDescription());      }
			if (hasPerm(cs, "animalprotect.protect")) { Messenger.sendMessage(cs, "§6/ap limit: §f" + AnimalProtect.plugin.getCommand("lockedanimals").getDescription());   }
			if (hasPerm(cs, "animalprotect.protect")) { Messenger.sendMessage(cs, "§6/ap info: §f" + AnimalProtect.plugin.getCommand("animalinfo").getDescription());   	}
			if (hasPerm(cs, "animalprotect.admin"))   { Messenger.sendMessage(cs, "§6/ap respawn: §f" + AnimalProtect.plugin.getCommand("respawnanimal").getDescription()); }
			if (hasPerm(cs, "animalprotect.admin"))   { Messenger.sendMessage(cs, "§6/ap tp: §f" + AnimalProtect.plugin.getCommand("tpanimal").getDescription());           }
			if (hasPerm(cs, "animalprotect.admin"))   { Messenger.sendMessage(cs, "§6/ap debug: §f" + AnimalProtect.plugin.getCommand("animaldebug").getDescription());     }
			if (hasPerm(cs, "animalprotect.admin"))   { Messenger.sendMessage(cs, "§6/ap queue: §f" + AnimalProtect.plugin.getCommand("animalqueue").getDescription());     }
			if (hasPerm(cs, "animalprotect.admin"))   { Messenger.sendMessage(cs, "§6/ap reload: §f" + "Lädt das gesamte Plugin neu.");                                     }
		}
		catch (Exception e) {
			Messenger.sendMessage(cs, "HELP_NOT_AVAILABLE");
			Messenger.exception("Command_AnimalProtect.java/Command_ShowHelp()", "Caught an exception while trying to show someone the help page.", e);
		}
	}
	
	public void Command_Reload(CommandSender cs, String[] args) {
		if (cs.hasPermission("animalprotect.admin")) {
			if (args.length == 0) {
				Bukkit.getServer().getPluginManager().disablePlugin(AnimalProtect.plugin);
				Bukkit.getServer().getPluginManager().enablePlugin(AnimalProtect.plugin);
				
				Messenger.sendMessage(cs, "RELOAD_SUCCESS_PLUGIN");
			}
			else if (args.length == 1) {
				if (args[0].equalsIgnoreCase("config")) {
					AnimalProtect.plugin.reloadSettings();
					
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
	
	private boolean hasPerm(CommandSender cs, String permission) {
		if (cs instanceof Player) {
			if (cs.hasPermission(permission)) { return true; }
			return false;
		}
		else { return true; }
	}
}
