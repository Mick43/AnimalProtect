package de.AnimalProtect.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import craftoplugin.core.database.CraftoPlayer;
import de.AnimalProtect.AnimalProtect;
import de.AnimalProtect.Messenger;
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
		
		/* Prüfen ob der Sender ein Spieler ist */
		if (!(cs instanceof Player)) {
			Messenger.sendMessage(cs, "§cFehler: Dieser Befehl kann nur von einem Spieler ausgeführt werden!");
			return;
		}
		
		/* Variablen bereitstellen */
		Player sender = (Player)cs;
		CraftoPlayer player = CraftoPlayer.getPlayer(sender.getUniqueId());
		Entity entity = AnimalProtect.plugin.getSelectedAnimal(sender.getUniqueId());
		
		/* Variablen überprüfen */
		if (entity == null) {
			Messenger.sendMessage(cs, "§cFehler: Du hast zurzeit noch kein Tier ausgewählt!");
			return;
		}
		else if (player == null) {
			Messenger.sendMessage(cs, "§cFehler: Dein Spielerobjekt wurde nicht gefunden! Bitte kontaktiere einen Administrator.");
			return;
		}
		
		/* Das Animal-Objekt laden */
		Animal animal = AnimalProtect.plugin.getDatenbank().getAnimal(entity.getUniqueId());
		
		if (animal != null) {
			Messenger.sendMessage(cs, "§cFehler: Das Tier ist bereits protected!");
			return;
		}
		else {
			animal = new Animal(AnimalProtect.plugin, player, entity);
			if(animal.saveToDatabase(true)) 
			{ Messenger.sendMessage(cs, "§aDas Tier wurde erfolgreich gesichert!"); }
			else { Messenger.sendMessage(cs, "§cFehler: Das Tier konnte nicht gesichert werden!"); }
		}
	}
}
