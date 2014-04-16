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

public class Command_tpanimal implements CommandExecutor {
	
	private AnimalProtect plugin;
	
	public Command_tpanimal(AnimalProtect plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if (!plugin.isEnabled()) { return false; }
		Command_tpanimal.runCommand(cs, args);
		return true;
	}
	
	public static void runCommand(CommandSender cs, String[] args) {
		/* Datenbank-Verbindung aufbauen, falls nicht vorhanden. */
		if (AnimalProtect.plugin.getDatenbank().isConnected())
		{ AnimalProtect.plugin.getDatenbank().connect(); }
		
		if (!(cs instanceof Player)) {
			Messenger.sendMessage(cs, "§cFehler: Um diesen Befehl auszuführen musst du ein Spieler sein.");
			return;
		}
		
		if (args.length < 2) {
			Messenger.sendMessage(cs, "§cFehler: Es wurden zu wenig Argumente angegeben!");
			return;
		}
		
		Player sender = (Player)cs;
		CraftoPlayer player = null;
		Animal animal = null;
		
		if (isUUID(args[0])) { player = CraftoPlayer.getPlayer(UUID.fromString(args[0])); }
		else { player = CraftoPlayer.getPlayer(args[0]); }
		
		if (player == null) { Messenger.sendMessage(cs, "§cFehler: Der Spieler konnte nicht gefunden werden."); return; }
		if (!isNumber(args[1])) { Messenger.sendMessage(cs, "§cFehler: Die angegebene ID ist keine Zahl!"); return; }
		
		animal = AnimalProtect.plugin.getDatenbank().getAnimals(player.getUniqueId()).get(Integer.parseInt(args[1]));
		
		if (animal == null) { Messenger.sendMessage(cs, "§cFehler: Das Tier konnte nicht gefunden werden."); return; }
		
		Location loc = new Location(sender.getWorld(), animal.getLast_x(), animal.getLast_y(), animal.getLast_z());
		
		for (Entity entity : sender.getWorld().getEntities()) {
			if (entity.getUniqueId().equals(animal.getUniqueId())) {
				loc = entity.getLocation();
			}
		}
		
		sender.teleport(loc);
		Messenger.sendMessage(cs, "§eDu hast dich zu den Koordinaten §6"+loc.getX()+"§e, §6"+loc.getY()+"§e, §6"+loc.getZ()+"§e teleportiert.");
	}
	
	private static boolean isUUID(String value) {
		//TODO: isUUID
		return false;
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
