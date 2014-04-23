package de.AnimalProtect.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.AnimalProtect.AnimalProtect;
import de.AnimalProtect.Messenger;

public class Command_debug implements CommandExecutor {
	
	private static AnimalProtect plugin;
	
	public Command_debug(AnimalProtect plugin) {
		Command_debug.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		Command_debug.runCommand(cs, args);
		return true;
	}
	
	public static void runCommand(CommandSender cs, String[] args) {
		if (args.length == 0) {
			Messenger.messageHeader(cs, "Debug-Informationen:");
			if (AnimalProtect.plugin.isEnabled()) { Messenger.messageList(cs, "Plugin aktiviert", "Ja"); }
			else { Messenger.messageList(cs, "Plugin aktiviert", "Nein"); }
			
			Messenger.messageList(cs, "Gesicherte Tiere", "" + plugin.getDatenbank().getLockedAnimals());
			
			if (plugin.getDatenbank().isConnected())
			{ Messenger.messageList(cs, "Datenbank-Verbindung", "Aktiv"); }
			else { Messenger.messageList(cs, "Datenbank-Verbindung", "Nicht aktiv"); }
			
			Messenger.messageList(cs, "Anzahl an fehlgeschlagenen Queries", ""+plugin.getDatenbank().getFailedQueries().size());
		}
		else {
			if (isNumber(args[0])) {
				Messenger.sendMessage(cs, "§7[§f"+plugin.getDatenbank().getFailedQueries().get(Integer.parseInt(args[0])) + "§7]");
			}
		}
	}
	
	private static boolean isNumber(String value) {
		try {
			Integer.parseInt(value);
			return true;
		}
		catch (Exception e) { }
		return false;
	}
}
