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

public class Command_unlock implements CommandExecutor {
	
	private final AnimalProtect plugin;
	
	public Command_unlock(final AnimalProtect plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(final CommandSender cs, final Command cmd, final String label, final String[] args) {
		if (this.plugin == null || !this.plugin.isEnabled()) { Messenger.sendMessage(cs, "§cFehler: Der Befehl konnte nicht ausgeführt werden."); return true; }
		
		/* Datenbank-Verbindung aufbauen, falls nicht vorhanden. */
		if (this.plugin.getDatenbank().isConnected())
		{ this.plugin.getDatenbank().connect(); }
		
		/* Prüfen ob der Sender ein Spieler ist */
		if (!(cs instanceof Player)) { Messenger.sendMessage(cs, "SENDER_NOT_PLAYER"); return true; }
		
		/* Variablen bereitstellen */
		final Player sender = (Player)cs;
		final CraftoPlayer player = CraftoPlayer.getPlayer(sender.getUniqueId());
		final Entity entity = this.plugin.getSelectedAnimal(sender.getUniqueId());
		
		/* Variablen überprüfen */
		if (entity == null) { Messenger.sendMessage(cs, "SELECTED_NONE"); return true; }
		else if (player == null) { Messenger.sendMessage(cs, "PLAYEROBJECT_NOT_FOUND"); return true; }
		
		final Animal animal = this.plugin.getDatenbank().getAnimal(entity.getUniqueId());
		
		if (animal == null) { Messenger.sendMessage(cs, "ANIMAL_NOT_FOUND"); }
		else {
			if (this.plugin.getDatenbank().unlockAnimal(animal)) 
			{ Messenger.sendMessage(cs, "ANIMAL_SUCESS_UNPROTECT"); }
			else { Messenger.sendMessage(cs, "ANIMAL_FAILED_UNPROTECT"); }
		}
		return true;
	}
}
