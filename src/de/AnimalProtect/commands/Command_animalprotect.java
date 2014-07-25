package de.AnimalProtect.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.AnimalProtect.AnimalProtect;
import de.AnimalProtect.Messenger;

public class Command_animalprotect implements CommandExecutor {
	
	private final AnimalProtect plugin;
	
	public Command_animalprotect(final AnimalProtect plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(final CommandSender cs, final Command cmd, final String label, final String[] args) {
		if (this.plugin == null || !this.plugin.isEnabled()) { return false; }
		
		if (cmd.getName().equalsIgnoreCase("ap") || cmd.getName().equalsIgnoreCase("animalprotect")) { /*  /ap <args0>  */
			String[] newArgs = args;
			if (args.length > 0) {
				newArgs = new String[args.length-1];
				for (int i=0; i<newArgs.length; i++) { newArgs[i] = args[i+1]; }
			}
			
			try {
				if (args.length < 1) { this.Command_ShowHelp(cs, newArgs); }
				else if (args[0].equalsIgnoreCase("help")) { this.Command_ShowHelp(cs, newArgs); }
				else if (args[0].equalsIgnoreCase("animaldebug")) { this.plugin.getCommand("animaldebug").getExecutor().onCommand(cs, cmd, label, newArgs); }
				else if (args[0].equalsIgnoreCase("listanimals")) { this.plugin.getCommand("listanimals").getExecutor().onCommand(cs, cmd, label, newArgs); }
				else if (args[0].equalsIgnoreCase("lockanimal")) { this.plugin.getCommand("lockanimal").getExecutor().onCommand(cs, cmd, label, newArgs); }
				else if (args[0].equalsIgnoreCase("lockedanimals")) {this.plugin.getCommand("lockedanimals").getExecutor().onCommand(cs, cmd, label, newArgs); }
				else if (args[0].equalsIgnoreCase("animalinfo")) { this.plugin.getCommand("animalinfo").getExecutor().onCommand(cs, cmd, label, newArgs); }
				else if (args[0].equalsIgnoreCase("respawnanimal")) { this.plugin.getCommand("respawnanimal").getExecutor().onCommand(cs, cmd, label, newArgs); }
				else if (args[0].equalsIgnoreCase("tpanimal")) { this.plugin.getCommand("tpanimal").getExecutor().onCommand(cs, cmd, label, newArgs); }
				else if (args[0].equalsIgnoreCase("unlockanimal")) { this.plugin.getCommand("unlockanimal").getExecutor().onCommand(cs, cmd, label, newArgs); }
				else if (args[0].equalsIgnoreCase("debug")) { this.plugin.getCommand("animaldebug").getExecutor().onCommand(cs, cmd, label, newArgs); }
				else if (args[0].equalsIgnoreCase("queue")) { this.plugin.getCommand("animalqueue").getExecutor().onCommand(cs, cmd, label, newArgs); }
				else if (args[0].equalsIgnoreCase("list")) { this.plugin.getCommand("listanimals").getExecutor().onCommand(cs, cmd, label, newArgs); }
				else if (args[0].equalsIgnoreCase("lock")) { this.plugin.getCommand("lockanimal").getExecutor().onCommand(cs, cmd, label, newArgs); }
				else if (args[0].equalsIgnoreCase("limit")) { this.plugin.getCommand("lockedanimals").getExecutor().onCommand(cs, cmd, label, newArgs); }
				else if (args[0].equalsIgnoreCase("info")) { this.plugin.getCommand("animalinfo").getExecutor().onCommand(cs, cmd, label, newArgs); }
				else if (args[0].equalsIgnoreCase("respawn")) { this.plugin.getCommand("respawnanimal").getExecutor().onCommand(cs, cmd, label, newArgs); }
				else if (args[0].equalsIgnoreCase("tp")) { this.plugin.getCommand("tpanimal").getExecutor().onCommand(cs, cmd, label, newArgs); }
				else if (args[0].equalsIgnoreCase("unlock")) { this.plugin.getCommand("unlockanimal").getExecutor().onCommand(cs, cmd, label, newArgs); }
				else if (args[0].equalsIgnoreCase("reload")) { this.Command_Reload(cs, newArgs); }
				else { Messenger.sendMessage(cs, "UNKNOWN_COMMAND"); }
			}
			catch (final Exception e) { 
				Messenger.sendMessage(cs, "UNKNOWN_COMMAND"); 
				Messenger.exception("Command_animalprotect.java/onCommand()", "Failed to parse command", e); 
			}
		}
		return true;
	}
	
	public void Command_ShowHelp(final CommandSender cs, final String[] args) {
		try {
			Messenger.help(cs, "HELP_HEADER");
			Messenger.sendMessage(cs, "HELP_DESC");
			if (this.hasPerm(cs, "animalprotect.protect")) { Messenger.sendMessage(cs, "§6/ap help: §fRuft diese Hilfe auf."); }
			if (this.hasPerm(cs, "animalprotect.lock"))    { Messenger.sendMessage(cs, "§6/ap lock: §f" + AnimalProtect.plugin.getCommand("lockanimal").getDescription());       }
			if (this.hasPerm(cs, "animalprotect.protect")) { Messenger.sendMessage(cs, "§6/ap unlock: §f" + AnimalProtect.plugin.getCommand("unlockanimal").getDescription());   }
			if (this.hasPerm(cs, "animalprotect.admin"))   { Messenger.sendMessage(cs, "§6/ap list: §f" + AnimalProtect.plugin.getCommand("listanimals").getDescription());      }
			if (this.hasPerm(cs, "animalprotect.protect")) { Messenger.sendMessage(cs, "§6/ap limit: §f" + AnimalProtect.plugin.getCommand("lockedanimals").getDescription());   }
			if (this.hasPerm(cs, "animalprotect.protect")) { Messenger.sendMessage(cs, "§6/ap info: §f" + AnimalProtect.plugin.getCommand("animalinfo").getDescription());   	 }
			if (this.hasPerm(cs, "animalprotect.admin"))   { Messenger.sendMessage(cs, "§6/ap respawn: §f" + AnimalProtect.plugin.getCommand("respawnanimal").getDescription()); }
			if (this.hasPerm(cs, "animalprotect.admin"))   { Messenger.sendMessage(cs, "§6/ap tp: §f" + AnimalProtect.plugin.getCommand("tpanimal").getDescription());           }
			if (this.hasPerm(cs, "animalprotect.admin"))   { Messenger.sendMessage(cs, "§6/ap debug: §f" + AnimalProtect.plugin.getCommand("animaldebug").getDescription());     }
			if (this.hasPerm(cs, "animalprotect.admin"))   { Messenger.sendMessage(cs, "§6/ap queue: §f" + AnimalProtect.plugin.getCommand("animalqueue").getDescription());     }
			if (this.hasPerm(cs, "animalprotect.admin"))   { Messenger.sendMessage(cs, "§6/ap reload: §f" + "Lädt das gesamte Plugin neu.");                                     }
		}
		catch (final Exception e) {
			Messenger.sendMessage(cs, "HELP_NOT_AVAILABLE");
			Messenger.exception("Command_AnimalProtect.java/Command_ShowHelp()", "Caught an exception while trying to show someone the help page.", e);
		}
	}
	
	public void Command_Reload(final CommandSender cs, final String[] args) {
		if (cs.hasPermission("animalprotect.admin")) {
			if (args.length == 0) {
				Bukkit.getServer().getPluginManager().disablePlugin(AnimalProtect.plugin);
				Bukkit.getServer().getPluginManager().enablePlugin(AnimalProtect.plugin);
				
				Messenger.sendMessage(cs, "RELOAD_SUCCESS_PLUGIN");
			}
			else if (args.length == 1) {
				if (args[0].equalsIgnoreCase("config")) {
					this.plugin.reloadSettings();
					
					Messenger.sendMessage(cs, "RELOAD_SUCCESS_CONFIG");
				}
				else if (args[0].equalsIgnoreCase("database")) {
					this.plugin.reloadDatabase();
					
					if (this.plugin.getDatenbank().isConnected()) 
					{ Messenger.sendMessage(cs, "RELOAD_SUCCESS_DATABASE"); }
					else { Messenger.sendMessage(cs, "RELOAD_FAILED_DATABASE"); }
				}
				else if (args[0].equalsIgnoreCase("connection")) {
					this.plugin.getDatenbank().closeConnection();
					this.plugin.getDatenbank().connect();
					this.plugin.getQueue().reloadConnection();
					
					if (this.plugin.getDatenbank().isConnected()) 
					{ Messenger.sendMessage(cs, "RELOAD_SUCCESS_CONNECTION"); }
					else { Messenger.sendMessage(cs, "RELOAD_FAILED_CONNECTION"); }
				}
			}
			else if (args.length > 1) { Messenger.sendMessage(cs, "TOO_MANY_ARGUMENTS"); }
		}
		else { Messenger.sendMessage(cs, "NO_PERMISSION"); }
	}
	
	private boolean hasPerm(final CommandSender cs, final String permission) {
		if (cs instanceof Player) {
			if (cs.hasPermission(permission)) { return true; }
			return false;
		}
		else { return true; }
	}
}
