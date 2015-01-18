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

/**
 * Die Infocommand-Klasse. {@code /animalinfo}
 * 
 * @author Fear837, Pingebam
 * @version 1.0
 * @see CommandExecutor
 */
public class Command_info implements CommandExecutor {

	/** Die AnimalProtect-Instanz. */
	private final AnimalProtect plugin;

	/**
	 * Initialisiert die Commandklasse.
	 * @param plugin - Das AnimalProtect-Plugin.
	 */
	public Command_info(final AnimalProtect plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(final CommandSender cs, final Command cmd, final String label, final String[] args) {
		if (!this.plugin.isEnabled()) { Messenger.sendMessage(cs, "§cFehler: Der Befehl konnte nicht ausgeführt werden."); return true; }

		/* Datenbank-Verbindung aufbauen, falls nicht vorhanden. */
		if (!this.plugin.getDatenbank().isConnected())
		{ this.plugin.getDatenbank().connect(); }

		/* Prüfen ob der Sender ein Spieler ist */
		if (!(cs instanceof Player)) 
		{ Messenger.sendMessage(cs, "SENDER_NOT_PLAYER"); return true; }

		/* Variablen bereitstellen */
		final Player sender = (Player)cs;
		final CraftoPlayer player = CraftoPlayer.getPlayer(sender.getUniqueId());
		final Entity entity = this.plugin.getSelectedAnimal(sender.getUniqueId());

		/* Variablen überprüfen */
		if (entity == null) { Messenger.sendMessage(cs, "SELECTED_NONE"); return true; }
		else if (player == null) { Messenger.sendMessage(cs, "PLAYEROBJECT_NOT_FOUND"); return true; }

		/* Das Animal-Objekt laden */
		final Animal animal = this.plugin.getDatenbank().getAnimal(entity.getUniqueId());

		if (animal != null) {
			final CraftoPlayer owner = this.plugin.getDatenbank().getOwner(animal.getUniqueId());
			if (owner != null)
			{ Messenger.sendMessage(sender, "§eDas Tier wurde von §6"+owner.getName()+"§e am §6"+CraftoTime.getTime(animal.getCreated_at(), "dd.MM.yyyy")+"§e gesichert."); }
			else { 
				Messenger.sendMessage(cs, "ANIMAL_OWNER_UNKNOWN");
				Messenger.error("Error: Failed to find the owner of an entity! (Command_animalinfo.java/runCommand) (AnimalId="+animal.getId()+")");
			}
		}
		else { Messenger.sendMessage(sender, "ANIMAL_NOT_PROTECTED"); }
		return true;
	}
}