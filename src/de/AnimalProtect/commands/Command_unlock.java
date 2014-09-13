package de.AnimalProtect.commands;

import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import craftoplugin.core.database.CraftoPlayer;
import de.AnimalProtect.AnimalProtect;
import de.AnimalProtect.Messenger;
import de.AnimalProtect.structs.Animal;

/**
 * Die Unlockcommand-Klasse. {@code /unlockanimal}
 * 
 * @author Fear837, Pingebam
 * @version 1.0
 * @see CommandExecutor
 */
public class Command_unlock implements CommandExecutor {

	/** Die AnimalProtect-Instanz. */
	private final AnimalProtect plugin;

	/**
	 * Initialisiert die Commandklasse.
	 * @param plugin - Das AnimalProtect-Plugin.
	 */
	public Command_unlock(final AnimalProtect plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(final CommandSender cs, final Command cmd, final String label, final String[] args) {
		if (!this.plugin.isEnabled()) { Messenger.sendMessage(cs, "§cFehler: Der Befehl konnte nicht ausgeführt werden."); return true; }

		/* Datenbank-Verbindung aufbauen, falls nicht vorhanden. */
		if (this.plugin.getDatenbank().isConnected())
		{ this.plugin.getDatenbank().connect(); }

		/* Prüfen ob der Sender ein Spieler ist */
		if (!(cs instanceof Player)) { Messenger.sendMessage(cs, "SENDER_NOT_PLAYER"); return true; }

		/* Argumente überprüfen */
		if (args.length < 1) {
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
				if (player.getId().equals(animal.getOwner()) || sender.hasPermission("animalprotect.admin")) {
					if (this.plugin.getDatenbank().unlockAnimal(animal)) 
					{ Messenger.sendMessage(cs, "ANIMAL_SUCESS_UNPROTECT"); }
					else { Messenger.sendMessage(cs, "ANIMAL_FAILED_UNPROTECT"); }
				}
				else { Messenger.sendMessage(cs, "ANIMAL_LOCKED_ANOTHER"); }
			}
			return true;
		}
		else if (cs.hasPermission("animalprotect.admin")) {
			final ArrayList<Animal> list = this.plugin.parseAnimal(cs, args, false);
			if (list==null) { return true; }
			else if (list.isEmpty()) { Messenger.sendMessage(cs, "ANIMALS_NOT_FOUND"); }

			int failed = 0;
			for (final Animal animal : list) {
				if (!this.plugin.getDatenbank().unlockAnimal(animal))
				{ failed += 1; }
			}

			Messenger.sendMessage(cs, "§aEs wurden "+(list.size()-failed)+" von "+list.size()+" Tieren entsichert.");
			return true;
		}
		else { Messenger.sendMessage(cs, "TOO_MANY_ARGUMENTS"); return true; }
	}
}