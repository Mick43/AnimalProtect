package de.AnimalProtect.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.AnimalProtect.AnimalProtect;
import de.AnimalProtect.Messenger;

public class Command_debug implements CommandExecutor {
	
	private final AnimalProtect plugin;
	
	public Command_debug(final AnimalProtect plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(final CommandSender cs, final Command cmd, final String label, final String[] args) {
		if (this.plugin == null || !this.plugin.isEnabled()) { Messenger.sendMessage(cs, "§cFehler: Der Befehl konnte nicht ausgeführt werden."); return true; }
		
		if (args.length == 0) {
			Messenger.messageHeader(cs, "Debug-Informationen:");
			if (AnimalProtect.plugin.isEnabled()) { Messenger.messageList(cs, "Plugin aktiviert", "Ja"); }
			else { Messenger.messageList(cs, "Plugin aktiviert", "Nein"); }
			
			Messenger.messageList(cs, "Gesicherte Tiere", "" + this.plugin.getDatenbank().getLockedAnimals());
			
			if (this.plugin.getDatenbank().isConnected())
			{ Messenger.messageList(cs, "Datenbank-Verbindung", "Aktiv"); }
			else { Messenger.messageList(cs, "Datenbank-Verbindung", "Nicht aktiv"); }
			
			Messenger.messageList(cs, "Anzahl an fehlgeschlagenen Queries", ""+this.plugin.getDatenbank().getFailedQueries().size());
			
			if (this.plugin.getQueue().isRunning()) { Messenger.messageList(cs, "Status der Queue", "Aktiv."); }
			else { Messenger.messageList(cs, "Status der Queue", "Inaktiv."); }
			
			Messenger.messageList(cs, "Größe der Queue", this.plugin.getQueue().getSize() + " Queries.");
			Messenger.messageList(cs, "Maximale Protections eines Spielers", this.plugin.getConfig().getInt("settings.max_entities_for_player") + " Locks.");
		}
		else {
			if (this.isNumber(args[0])) 
			{ Messenger.sendMessage(cs, "§7[§f"+this.plugin.getDatenbank().getFailedQueries().get(Integer.parseInt(args[0])) + "§7]"); }
		}
		
		return true;
	}
	
	private boolean isNumber(final String value) {
		try { Integer.parseInt(value); return true; }
		catch (final Exception e) { return false; }
	}
}
