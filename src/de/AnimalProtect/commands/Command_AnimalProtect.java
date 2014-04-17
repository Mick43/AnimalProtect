package de.AnimalProtect.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

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
			else if (args[0].equalsIgnoreCase("debuganimal")) { Command_animaldebug.runCommand(cs, newArgs); }
			else if (args[0].equalsIgnoreCase("listanimals")) { Command_listanimals.runCommand(cs, newArgs); }
			else if (args[0].equalsIgnoreCase("lockanimal")) { Command_lockanimal.runCommand(cs, newArgs); }
			else if (args[0].equalsIgnoreCase("lockedanimals")) { Command_lockedanimals.runCommand(cs, newArgs); }
			else if (args[0].equalsIgnoreCase("respawnanimal")) { Command_respawnanimal.runCommand(cs, newArgs); }
			else if (args[0].equalsIgnoreCase("tpanimal")) { Command_tpanimal.runCommand(cs, newArgs); }
			else if (args[0].equalsIgnoreCase("unlockanimal")) { Command_unlockanimal.runCommand(cs, newArgs); }
			else { Messenger.sendMessage(cs, "§cUnbekannter Befehl. (Schreibe /ap help für eine Übersicht aller Kommandos.)"); }
		}
		return true;
	}
	
	public static void Command_ShowHelp(CommandSender cs, String[] args) {
		Bukkit.getServer().dispatchCommand(cs, "help animalprotect");
	}
}
