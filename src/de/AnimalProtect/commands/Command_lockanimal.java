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
	
	private static AnimalProtect plugin;
	
	public Command_lockanimal(AnimalProtect plugin) {
		Command_lockanimal.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if (!plugin.isEnabled()) { return true; }
		if (cmd.getName().equalsIgnoreCase("lockanimal")) { Command_lockanimal.runCommand(cs, args); }
		return true;
	}
	
	public static void runCommand(CommandSender cs, String[] args) {
		/* Datenbank-Verbindung aufbauen, falls nicht vorhanden. */
		if (plugin.getDatenbank().isConnected())
		{ plugin.getDatenbank().connect(); }
		
		/* Prüfen ob der Sender ein Spieler ist */
		if (!(cs instanceof Player)) {
			Messenger.sendMessage(cs, "SENDER_NOT_PLAYER");
			return;
		}
		
		/* Variablen bereitstellen */
		Player sender = (Player)cs;
		CraftoPlayer player = CraftoPlayer.getPlayer(sender.getUniqueId());
		Entity entity = plugin.getSelectedAnimal(sender.getUniqueId());
		
		/* Variablen überprüfen */
		if (entity == null) {
			Messenger.sendMessage(cs, "SELECTED_NONE");
			return;
		}
		else if (player == null) {
			Messenger.sendMessage(cs, "PLAYEROBJECT_NOT_FOUND");
			return;
		}
		
		/* Das Animal-Objekt laden */
		Animal animal = plugin.getDatenbank().getAnimal(entity.getUniqueId());
		
		if (animal != null) {
			Messenger.sendMessage(cs, "ANIMAL_ALREADY_PROTECTED");
			return;
		}
		else {
			if (plugin.getDatenbank().getAnimals(player.getUniqueId()).size() <= plugin.getConfig().getInt("settings.max_entities_for_player")) {
				animal = new Animal(AnimalProtect.plugin, player, entity);
				if(animal.saveToDatabase(true)) 
				{ Messenger.sendMessage(cs, "LOCK_SUCCESS"); }
				else { Messenger.sendMessage(cs, "LOCK_FAILED"); }
			}
			else { Messenger.sendMessage(cs, "MAX_LOCKS_EXCEEDED"); }
		}
	}
}
