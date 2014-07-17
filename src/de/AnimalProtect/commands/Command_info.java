package de.AnimalProtect.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import craftoplugin.core.database.CraftoPlayer;
import craftoplugin.utility.CraftoTime;
import de.AnimalProtect.AnimalProtect;
import de.AnimalProtect.Messenger;
import de.AnimalProtect.structs.Animal;

public class Command_info implements CommandExecutor {

	private static AnimalProtect plugin;
	
	public Command_info(AnimalProtect plugin) {
		Command_info.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if (!plugin.isEnabled()) { return true; }
		Command_info.runCommand(cs, args);
		return true;
	}

	public static void runCommand(CommandSender cs, String[] args) {
		if (plugin == null) { Messenger.sendMessage(cs, "§cFehler: Der Befehl konnte nicht ausgeführt werden."); return; }
		
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
			CraftoPlayer owner = plugin.getDatenbank().getOwner(animal.getUniqueId());
			if (owner != null)
			{ Messenger.sendMessage(sender, "§eDas Tier wurde von §6"+owner.getName()+"§e am §6"+CraftoTime.getTime("dd.MM.yyyy", animal.getCreated_at())+" gesichert."); }
			else { 
				Messenger.sendMessage(cs, "ANIMAL_OWNER_UNKNOWN");
				Messenger.error("Error: Failed to find the owner of an entity! (Command_animalinfo.java/runCommand) (AnimalId="+animal.getId()+")");
			}
		}
		else {
			Messenger.sendMessage(sender, "ANIMAL_NOT_PROTECTED");
		}
	}
}
