package de.AnimalProtect.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.AnimalProtect.AnimalProtect;
import de.AnimalProtect.Messenger;

public class Command_queue implements CommandExecutor {

	private static AnimalProtect plugin;
	
	public Command_queue(AnimalProtect plugin) {
		Command_queue.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if (!plugin.isEnabled()) { return true; }
		Command_queue.runCommand(cs, args);
		return true;
	}

	public static void runCommand(CommandSender cs, String[] args) {
		if (plugin == null) { Messenger.sendMessage(cs, "§cFehler: Der Befehl konnte nicht ausgeführt werden."); return; }
		
		/* Datenbank-Verbindung aufbauen, falls nicht vorhanden. */
		if (plugin.getDatenbank().isConnected())
		{ plugin.getDatenbank().connect(); }
		
		/* Permissions des Spielers überprüfen */
		if (!cs.hasPermission("animalprotect.admin")) { Messenger.sendMessage(cs, "NO_PERMISSION"); }
		
		/* Die Argumente überprüfen */
		if (args.length == 0) {
			if (plugin.getQueue().isRunning()) { Messenger.sendMessage(cs, "QUEUE_STARTED"); }
			else { Messenger.sendMessage(cs, "QUEUE_STOPPED"); }
		}
		else if (args.length == 1) {
			if (args[0].equalsIgnoreCase("start") || args[0].equalsIgnoreCase("run")) {
				if (!plugin.getQueue().isRunning()) { Messenger.sendMessage(cs, "QUEUE_START"); plugin.getQueue().start(); }
				else { Messenger.sendMessage(cs, "QUEUE_ALREADY_STARTED"); }
			}
			else if (args[0].equalsIgnoreCase("stop") || args[0].equalsIgnoreCase("pause")) {
				if (plugin.getQueue().isRunning()) { Messenger.sendMessage(cs, "QUEUE_STOP"); plugin.getQueue().stop(); }
				else { Messenger.sendMessage(cs, "QUEUE_ALREADY_STOPPED"); }
			}
			else if (args[0].equalsIgnoreCase("size")) {
				Messenger.sendMessage(cs, "§eDie Größe der aktuellen AnimalProtect-Queue beträgt " + plugin.getQueue().getSize() + ".");
			}
			else { Messenger.sendMessage(cs, "UNKNOWN_COMMAND"); }
		}
		else { Messenger.sendMessage(cs, "TOO_MANY_ARGUMENTS"); }
	}
}
