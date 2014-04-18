package de.AnimalProtect.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.AnimalProtect.AnimalProtect;
import de.AnimalProtect.Messenger;

public class Command_AnimalProtect implements CommandExecutor {
	
	private AnimalProtect plugin;
	
	public Command_AnimalProtect(AnimalProtect plugin) {
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
			
			if (args.length < 1) { Command_AnimalProtect.Command_ShowHelp(cs, newArgs); }
			else if (args[0].equalsIgnoreCase("help")) { Command_AnimalProtect.Command_ShowHelp(cs, newArgs); }
			else if (args[0].equalsIgnoreCase("animaldebug")) { Command_animaldebug.runCommand(cs, newArgs); }
			else if (args[0].equalsIgnoreCase("listanimals")) { Command_listanimals.runCommand(cs, newArgs); }
			else if (args[0].equalsIgnoreCase("lockanimal")) { Command_lockanimal.runCommand(cs, newArgs); }
			else if (args[0].equalsIgnoreCase("lockedanimals")) { Command_lockedanimals.runCommand(cs, newArgs); }
			else if (args[0].equalsIgnoreCase("respawnanimal")) { Command_respawnanimal.runCommand(cs, newArgs); }
			else if (args[0].equalsIgnoreCase("tpanimal")) { Command_tpanimal.runCommand(cs, newArgs); }
			else if (args[0].equalsIgnoreCase("unlockanimal")) { Command_unlockanimal.runCommand(cs, newArgs); }
			else if (args[0].equalsIgnoreCase("debug")) { Command_animaldebug.runCommand(cs, newArgs); }
			else if (args[0].equalsIgnoreCase("list")) { Command_listanimals.runCommand(cs, newArgs); }
			else if (args[0].equalsIgnoreCase("lock")) { Command_lockanimal.runCommand(cs, newArgs); }
			else if (args[0].equalsIgnoreCase("limit")) { Command_lockedanimals.runCommand(cs, newArgs); }
			else if (args[0].equalsIgnoreCase("respawn")) { Command_respawnanimal.runCommand(cs, newArgs); }
			else if (args[0].equalsIgnoreCase("tp")) { Command_tpanimal.runCommand(cs, newArgs); }
			else if (args[0].equalsIgnoreCase("unlock")) { Command_unlockanimal.runCommand(cs, newArgs); }
			else { Messenger.sendMessage(cs, "§cUnbekannter Befehl. (Schreibe /ap help für eine Übersicht aller Kommandos.)"); }
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
			if (hasPerm(cs, "animalprotect.admin"))   { Messenger.sendMessage(cs, "§6/ap respawn: §f" + AnimalProtect.plugin.getCommand("respawnanimal").getDescription()); }
			if (hasPerm(cs, "animalprotect.admin"))   { Messenger.sendMessage(cs, "§6/ap tp: §f" + AnimalProtect.plugin.getCommand("tpanimal").getDescription());           }
			if (hasPerm(cs, "animalprotect.admin"))   { Messenger.sendMessage(cs, "§6/ap debug: §f" + AnimalProtect.plugin.getCommand("animaldebug").getDescription());     }
		}
		catch (Exception e) {
			Messenger.sendMessage(cs, "§cDie Hilfe von AnimalProtect ist zurzeit nicht verfügbar.");
			Messenger.exception("Command_AnimalProtect/Command_ShowHelp", "Caught an exception while trying to show someone the help page.", e);
		}
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
