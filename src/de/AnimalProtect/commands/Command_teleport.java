package de.AnimalProtect.commands;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import craftoplugin.core.database.CraftoPlayer;
import de.AnimalProtect.AnimalProtect;
import de.AnimalProtect.Messenger;
import de.AnimalProtect.structs.Animal;

public class Command_teleport implements CommandExecutor {
	
	private static AnimalProtect plugin;
	
	public Command_teleport(AnimalProtect plugin) {
		Command_teleport.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if (!plugin.isEnabled()) { return false; }
		Command_teleport.runCommand(cs, args);
		return true;
	}
	
	public static void runCommand(CommandSender cs, String[] args) {
		/* Datenbank-Verbindung aufbauen, falls nicht vorhanden. */
		if (plugin.getDatenbank().isConnected())
		{ plugin.getDatenbank().connect(); }
		
		if (!(cs instanceof Player)) {
			Messenger.sendMessage(cs, "SENDER_NOT_PLAYER");
			return;
		}
		
		if (args.length < 2) {
			Messenger.sendMessage(cs, "TOO_FEW_ARGUMENTS");
			return;
		}
		
		Player sender = (Player)cs;
		CraftoPlayer player = null;
		Animal animal = null;
		
		if (isUUID(args[0])) { player = CraftoPlayer.getPlayer(UUID.fromString(args[0])); }
		else { player = CraftoPlayer.getPlayer(args[0]); }
		
		if (player == null) { Messenger.sendMessage(cs, "PLAYER_NOT_FOUND"); return; }
		if (!isNumber(args[1])) { Messenger.sendMessage(cs, "ID_NOT_NUMBER"); return; }
		if (plugin.getDatenbank().getAnimals(player.getUniqueId()).size() <= Integer.parseInt(args[1]))
		{ Messenger.sendMessage(cs, "ANIMAL_NOT_FOUND"); return; }
		
		animal = plugin.getDatenbank().getAnimals(player.getUniqueId()).get(Integer.parseInt(args[1]));
		
		if (animal == null) { Messenger.sendMessage(cs, "ANIMAL_NOT_FOUND"); return; }
		
		Location loc = new Location(sender.getWorld(), animal.getLast_x(), animal.getLast_y(), animal.getLast_z());
		
		for (Entity entity : sender.getWorld().getEntities()) {
			if (entity.getUniqueId().equals(animal.getUniqueId())) {
				loc = entity.getLocation();
			}
		}
		
		sender.teleport(loc);
		Messenger.sendMessage(cs, "§eDu hast dich zu den Koordinaten §6"+loc.getBlockX()+"§e, §6"+loc.getBlockY()+"§e, §6"+loc.getBlockZ()+"§e teleportiert.");
	}
	
	private static boolean isUUID(String value) {
		return value.matches(".*-.*-.*-.*-.*");
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
