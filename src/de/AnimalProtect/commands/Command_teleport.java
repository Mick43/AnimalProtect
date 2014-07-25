package de.AnimalProtect.commands;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import de.AnimalProtect.AnimalProtect;
import de.AnimalProtect.Messenger;
import de.AnimalProtect.structs.Animal;

public class Command_teleport implements CommandExecutor {
	
	private final AnimalProtect plugin;
	
	public Command_teleport(final AnimalProtect plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(final CommandSender cs, final Command cmd, final String label, final String[] args) {
		if (this.plugin == null || !this.plugin.isEnabled()) { Messenger.sendMessage(cs, "§cFehler: Der Befehl konnte nicht ausgeführt werden."); return true; }
		
		/* Datenbank-Verbindung aufbauen, falls nicht vorhanden. */
		if (this.plugin.getDatenbank().isConnected())
		{ this.plugin.getDatenbank().connect(); }
		
		if (!(cs instanceof Player)) { Messenger.sendMessage(cs, "SENDER_NOT_PLAYER"); return true; }
		if (args.length < 2) { Messenger.sendMessage(cs, "TOO_FEW_ARGUMENTS"); return true; }
		
		final Player sender = (Player)cs;
		
		final ArrayList<Animal> list = this.plugin.parseAnimal(cs, args, true);
		
		if (list == null) { return true; }
		else if (list.isEmpty()) { Messenger.sendMessage(cs, "ANIMAL_NOT_FOUND"); }
						
		Location loc = new Location(sender.getWorld(), list.get(0).getX(), list.get(0).getY(), list.get(0).getZ());
		
		for (final Entity entity : sender.getWorld().getEntities()) {
			if (entity.getUniqueId().equals(list.get(0).getUniqueId())) {
				loc = entity.getLocation();
			}
		}
		
		sender.teleport(loc);
		Messenger.sendMessage(cs, "§eDu hast dich zu den Koordinaten §6"+loc.getBlockX()+"§e, §6"+loc.getBlockY()+"§e, §6"+loc.getBlockZ()+"§e teleportiert.");
		return true;
	}
}
