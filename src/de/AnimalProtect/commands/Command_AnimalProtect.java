package de.AnimalProtect.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.AnimalProtect.AnimalProtect;

public class Command_AnimalProtect implements CommandExecutor {
	
	private AnimalProtect plugin;
	
	public Command_AnimalProtect(AnimalProtect plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if (!plugin.isEnabled()) { return false; }
		
		if (cmd.getName().equalsIgnoreCase("ap") || cmd.getName().equalsIgnoreCase("animalprotect")) {
			if (args.length < 1) { Command_AnimalProtect.Command_ShowHelp(cs, args); }
			else if (args[0].equalsIgnoreCase("help")) { Command_AnimalProtect.Command_ShowHelp(cs, args); }
		}
		return true;
	}
	
	public static void Command_ShowHelp(CommandSender cs, String[] args) {
		Bukkit.getServer().dispatchCommand(cs, "help animalprotect");
	}
}
