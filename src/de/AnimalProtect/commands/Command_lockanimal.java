package de.AnimalProtect.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import craftoplugin.core.CraftoMessenger;
import craftoplugin.core.database.CraftoPlayer;
import de.AnimalProtect.AnimalProtect;
import de.AnimalProtect.structs.Animal;

public class Command_lockanimal implements CommandExecutor {
	
	private AnimalProtect plugin;
	
	public Command_lockanimal(AnimalProtect plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if (!plugin.isEnabled()) { return false; }
		if (cmd.getName().equalsIgnoreCase("lockanimal")) { Command_lockanimal.runCommand(cs, args); }
		
		return true;
	}
	
	public static void runCommand(CommandSender cs, String[] args) {
		/* Datenbank-Verbindung aufbauen, falls nicht vorhanden. */
		if (AnimalProtect.plugin.getDatenbank().isConnected())
		{ AnimalProtect.plugin.getDatenbank().connect(); }
		
		if (!(cs instanceof Player)) {
			CraftoMessenger.message(cs, "§cFehler: Dieser Befehl kann nur von einem Spieler ausgeführt werden!");
			return;
		}
		
		Player sender = (Player)cs;
		CraftoPlayer player = CraftoPlayer.getPlayer(sender.getUniqueId());
		Entity entity = AnimalProtect.plugin.getSelectedAnimal(sender.getUniqueId());
		
		if (entity == null) {
			CraftoMessenger.message(cs, "§cFehler: Du hast zurzeit noch kein Tier ausgewählt!");
			return;
		}
		else if (player == null) {
			CraftoMessenger.message(cs, "§cFehler: Dein Spielerobjekt wurde nicht gefunden! Bitte kontaktiere einen Administrator.");
		}
		
		Animal animal = AnimalProtect.plugin.getDatenbank().getAnimal(entity.getUniqueId().toString());
		
		if (animal != null) {
			CraftoMessenger.message(cs, "§cFehler: Das Tier ist bereits protected!");
			return;
		}
		else {
			animal = new Animal(AnimalProtect.plugin, player, entity);
			if(animal.saveToDatabase(true)) {
				CraftoMessenger.message(cs, "§aDas Tier wurde erfolgreich gesichert!");
			}
			else {
				CraftoMessenger.message(cs, "§cFehler: Das Tier konnte nicht gesichert werden!");
			}
		}
		
		
	}
}
