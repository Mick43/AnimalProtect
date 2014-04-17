package de.AnimalProtect.commands;

import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import craftoplugin.core.database.CraftoPlayer;
import de.AnimalProtect.AnimalProtect;
import de.AnimalProtect.Messenger;

public class Command_lockedanimals implements CommandExecutor {
	
	private static AnimalProtect plugin;
	
	public Command_lockedanimals(AnimalProtect plugin) {
		Command_lockedanimals.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if (!plugin.isEnabled()) { return false; }
		Command_lockedanimals.runCommand(cs, args);
		return true;
	}
	
	public static void runCommand(CommandSender cs, String[] args) {
		/* Datenbank-Verbindung aufbauen, falls nicht vorhanden. */
		if (plugin.getDatenbank().isConnected())
		{ plugin.getDatenbank().connect(); }
		
		CraftoPlayer player = null;
		if (args.length == 0) {
			if (cs instanceof Player) {
				player = CraftoPlayer.getPlayer(((Player)cs).getUniqueId());
			}
			else { Messenger.sendMessage(cs, "§cFehler: Es wurde kein Spieler angegeben!"); }
		}
		else if (args.length == 1) {
			if (isUUID(args[0])) {
				player = CraftoPlayer.getPlayer(UUID.fromString(args[0]));
			}
			else { player = CraftoPlayer.getPlayer(args[0]); }
		}
		else { Messenger.sendMessage(cs, "§cFehler: Zu viele Argumente angegeben!"); }
		
		if (player == null) { Messenger.sendMessage(cs, "§cFehler: Der Spieler konnte nicht gefunden werden!"); }
		
		Integer count = plugin.getDatenbank().getAnimals(player.getUniqueId()).size();
		if (cs.getName().equalsIgnoreCase(player.getName())) {
			Messenger.sendMessage(cs, "Du hast insgesamt §6"+count+"§e von §6"+plugin.getConfig().getInt("settings.max_entities_for_player")+"§e Tieren gesichert.");
		}
		else { Messenger.sendMessage(cs, "Der Spieler §6"+player.getName()+"§e hat insgesamt §6"+count+"§e von §6"+plugin.getConfig().getInt("settings.max_entities_for_player")+"§e Tieren gesichert."); }
	}
	
	private static boolean isUUID(String value) {
		//TODO: isUUID
		return false;
	}
}
