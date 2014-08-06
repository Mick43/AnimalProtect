package de.AnimalProtect.commands;

import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import craftoplugin.core.database.CraftoPlayer;
import de.AnimalProtect.AnimalProtect;
import de.AnimalProtect.Messenger;

public class Command_limit implements CommandExecutor {

	private final AnimalProtect plugin;

	public Command_limit(final AnimalProtect plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(final CommandSender cs, final Command cmd, final String label, final String[] args) {
		if (!this.plugin.isEnabled()) { Messenger.sendMessage(cs, "§cFehler: Der Befehl konnte nicht ausgeführt werden."); return true; }

		/* Datenbank-Verbindung aufbauen, falls nicht vorhanden. */
		if (!this.plugin.getDatenbank().isConnected())
		{ this.plugin.getDatenbank().connect(); }

		CraftoPlayer player = null;
		if (args.length == 0) {
			if (cs instanceof Player) { player = CraftoPlayer.getPlayer(((Player)cs).getUniqueId()); }
			else { Messenger.sendMessage(cs, "NO_GIVEN_PLAYER"); }
		}
		else if (args.length == 1) {
			if (this.isUUID(args[0])) { player = CraftoPlayer.getPlayer(UUID.fromString(args[0])); }
			else { player = CraftoPlayer.getPlayer(args[0]); }
		}
		else { Messenger.sendMessage(cs, "TOO_MANY_ARGUMENTS"); }

		if (player == null) { Messenger.sendMessage(cs, "PLAYER_NOT_FOUND"); return true; }

		final Integer count = this.plugin.getDatenbank().countAnimals(player.getUniqueId());
		final Integer max = this.plugin.getConfig().getInt("settings.max_entities_for_player");
		if (cs.getName().equalsIgnoreCase(player.getName())) 
		{ Messenger.sendMessage(cs, "Du hast insgesamt §6"+count+"§e von §6"+max+"§e Tieren gesichert."); }
		else { Messenger.sendMessage(cs, "Der Spieler §6"+player.getName()+"§e hat insgesamt §6"+count+"§e von §6"+max+"§e Tieren gesichert."); }
		return true;
	}

	private boolean isUUID(final String value) {
		return value.matches(".*-.*-.*-.*-.*");
	}
}
