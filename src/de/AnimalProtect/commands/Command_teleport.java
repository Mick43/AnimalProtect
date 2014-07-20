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
	
	private final AnimalProtect plugin;
	
	public Command_teleport(AnimalProtect plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if (plugin == null || !plugin.isEnabled()) { Messenger.sendMessage(cs, "�cFehler: Der Befehl konnte nicht ausgef�hrt werden."); return true; }
		
		/* Datenbank-Verbindung aufbauen, falls nicht vorhanden. */
		if (plugin.getDatenbank().isConnected())
		{ plugin.getDatenbank().connect(); }
		
		if (!(cs instanceof Player)) { Messenger.sendMessage(cs, "SENDER_NOT_PLAYER"); return true; }
		if (args.length < 2) { Messenger.sendMessage(cs, "TOO_FEW_ARGUMENTS"); return true; }
		
		Player sender = (Player)cs;
		CraftoPlayer player = null;
		Animal animal = null;
		
		if (isUUID(args[0])) { player = CraftoPlayer.getPlayer(UUID.fromString(args[0])); }
		else { player = CraftoPlayer.getPlayer(args[0]); }
		
		if (player == null) { Messenger.sendMessage(cs, "PLAYER_NOT_FOUND"); return true; }
		if (!isNumber(args[1])) { Messenger.sendMessage(cs, "ID_NOT_NUMBER"); return true; }
		if (plugin.getDatenbank().getAnimals(player.getUniqueId()).size() <= Integer.parseInt(args[1]))
		{ Messenger.sendMessage(cs, "ANIMAL_NOT_FOUND"); return true; }
		
		animal = plugin.getDatenbank().getAnimals(player.getUniqueId()).get(Integer.parseInt(args[1]));
		
		if (animal == null) { Messenger.sendMessage(cs, "ANIMAL_NOT_FOUND"); return true; }
		
		Location loc = new Location(sender.getWorld(), animal.getX(), animal.getY(), animal.getZ());
		
		for (Entity entity : sender.getWorld().getEntities()) {
			if (entity.getUniqueId().equals(animal.getUniqueId())) {
				loc = entity.getLocation();
			}
		}
		
		sender.teleport(loc);
		Messenger.sendMessage(cs, "�eDu hast dich zu den Koordinaten �6"+loc.getBlockX()+"�e, �6"+loc.getBlockY()+"�e, �6"+loc.getBlockZ()+"�e teleportiert.");
		return true;
	}
	
	private boolean isUUID(String value) {
		if (value.length() != 36) { return false; }
		return value.matches(".*-.*-.*-.*-.*");
	}
	
	private boolean isNumber(String value) {
		try { Integer.parseInt(value); return true; }
		catch (Exception e) { return false; }
	}
}
