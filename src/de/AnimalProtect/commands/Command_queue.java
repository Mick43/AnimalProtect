package de.AnimalProtect.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.AnimalProtect.AnimalProtect;
import de.AnimalProtect.Messenger;

public class Command_queue implements CommandExecutor {

	private final AnimalProtect plugin;

	public Command_queue(final AnimalProtect plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(final CommandSender cs, final Command cmd, final String label, final String[] args) {
		if (!this.plugin.isEnabled()) { Messenger.sendMessage(cs, "§cFehler: Der Befehl konnte nicht ausgeführt werden."); return true; }

		/* Datenbank-Verbindung aufbauen, falls nicht vorhanden. */
		if (!this.plugin.getDatenbank().isConnected())
		{ this.plugin.getDatenbank().connect(); }

		/* Permissions des Spielers überprüfen */
		if (!cs.hasPermission("animalprotect.admin")) { Messenger.sendMessage(cs, "NO_PERMISSION"); }

		/* Die Argumente überprüfen */
		if (args.length == 0) {
			if (this.plugin.getQueue().isRunning()) { Messenger.sendMessage(cs, "QUEUE_STARTED"); }
			else { Messenger.sendMessage(cs, "QUEUE_STOPPED"); }
		}
		else if (args.length == 1) {
			if (args[0].equalsIgnoreCase("start") || args[0].equalsIgnoreCase("run")) {
				if (!this.plugin.getQueue().isRunning()) { Messenger.sendMessage(cs, "QUEUE_START"); this.plugin.getQueue().start(); }
				else { Messenger.sendMessage(cs, "QUEUE_ALREADY_STARTED"); }
			}
			else if (args[0].equalsIgnoreCase("stop") || args[0].equalsIgnoreCase("pause")) {
				if (this.plugin.getQueue().isRunning()) { Messenger.sendMessage(cs, "QUEUE_STOP"); this.plugin.getQueue().stop(); }
				else { Messenger.sendMessage(cs, "QUEUE_ALREADY_STOPPED"); }
			}
			else if (args[0].equalsIgnoreCase("size")) {
				Messenger.sendMessage(cs, "§eDie Größe der aktuellen AnimalProtect-Queue beträgt " + this.plugin.getQueue().getSize() + ".");
			}
			else { Messenger.sendMessage(cs, "UNKNOWN_COMMAND"); }
		}
		else { Messenger.sendMessage(cs, "TOO_MANY_ARGUMENTS"); }
		return true;
	}
}
