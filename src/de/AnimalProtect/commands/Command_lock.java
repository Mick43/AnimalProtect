package de.AnimalProtect.commands;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Effect;
import org.bukkit.Sound;
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
 * Die Lockcommand-Klasse. {@code /lockanimal}
 * 
 * @author Fear837, Pingebam
 * @version 1.0
 * @see CommandExecutor
 */
public class Command_lock implements CommandExecutor {

	/** Die AnimalProtect-Instanz. */
	private final AnimalProtect plugin;
	/** Eine Map in der gespeichert wird, wann ein Spieler zuletzt ein Tier gesichert hat. */
	private final HashMap<UUID, Long> lockTimes;

	/**
	 * Initialisiert die Commandklasse.
	 * @param plugin - Das AnimalProtect-Plugin.
	 */
	public Command_lock(final AnimalProtect plugin) {
		this.plugin = plugin;
		this.lockTimes = new HashMap<UUID, Long>();
	}

	@Override
	public boolean onCommand(final CommandSender cs, final Command cmd, final String label, final String[] args) {
		if (!this.plugin.isEnabled()) { Messenger.sendMessage(cs, "§cFehler: Der Befehl konnte nicht ausgeführt werden."); return true; }

		/* Datenbank-Verbindung aufbauen, falls nicht vorhanden. */
		if (!this.plugin.getDatenbank().isConnected())
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

		/* Das Animal-Objekt laden */
		Animal animal = this.plugin.getDatenbank().getAnimal(entity.getUniqueId());

		if (animal != null) { Messenger.sendMessage(cs, "ANIMAL_ALREADY_PROTECTED"); return true; }
		else {
			try {
				if (this.plugin.getDatenbank().countAnimals(player.getUniqueId()) <= this.plugin.getConfig().getInt("settings.max_entities_for_player")) {
					if (this.lockTimes.containsKey(sender.getUniqueId())) {
						if (this.lockTimes.get(sender.getUniqueId()) + 5000 < System.currentTimeMillis()) {
							animal = new Animal(AnimalProtect.plugin, player, entity);
							if(animal.saveToDatabase(true)) { 
								Messenger.sendMessage(cs, "LOCK_SUCCESS"); 
								entity.getWorld().playEffect(entity.getLocation(), Effect.SMOKE, 4);
								entity.getWorld().playEffect(entity.getLocation(), Effect.SMOKE, 1);
								entity.getWorld().playEffect(entity.getLocation(), Effect.SMOKE, 3);
								entity.getWorld().playEffect(entity.getLocation(), Effect.SMOKE, 5);
								entity.getWorld().playEffect(entity.getLocation(), Effect.SMOKE, 7);
								entity.getWorld().playSound(entity.getLocation(), Sound.DOOR_CLOSE, 0.25f, 1.75f);
								entity.getWorld().playSound(entity.getLocation(), Sound.DOOR_CLOSE, 0.25f, 1.75f);
								entity.getWorld().playSound(entity.getLocation(), Sound.DOOR_CLOSE, 0.25f, 1.75f);
								sender.playSound(sender.getLocation(), Sound.DOOR_CLOSE, 0.75f, 1.75f);
								this.lockTimes.put(sender.getUniqueId(), System.currentTimeMillis());
							}
							else { Messenger.sendMessage(cs, "LOCK_FAILED"); }
						}
						else {
							Integer wait = (int) Math.floor((this.lockTimes.get(sender.getUniqueId()) + 5000 - System.currentTimeMillis()));
							wait = wait / 1000;
							Messenger.sendMessageFailed(cs, "Fehler: Warte noch "+wait+" Sekunden, bevor du das nächste Tier protectest.");
						}
					}
					else {
						animal = new Animal(AnimalProtect.plugin, player, entity);
						if(animal.saveToDatabase(true)) { 
							Messenger.sendMessage(cs, "LOCK_SUCCESS"); 
							entity.getWorld().playEffect(entity.getLocation(), Effect.SMOKE, 4);
							entity.getWorld().playEffect(entity.getLocation(), Effect.SMOKE, 1);
							entity.getWorld().playEffect(entity.getLocation(), Effect.SMOKE, 3);
							entity.getWorld().playEffect(entity.getLocation(), Effect.SMOKE, 5);
							entity.getWorld().playEffect(entity.getLocation(), Effect.SMOKE, 7);
							entity.getWorld().playSound(entity.getLocation(), Sound.DOOR_CLOSE, 0.25f, 1.75f);
							entity.getWorld().playSound(entity.getLocation(), Sound.DOOR_CLOSE, 0.25f, 1.75f);
							entity.getWorld().playSound(entity.getLocation(), Sound.DOOR_CLOSE, 0.25f, 1.75f);
							sender.playSound(sender.getLocation(), Sound.DOOR_CLOSE, 0.75f, 1.75f);
							this.lockTimes.put(sender.getUniqueId(), System.currentTimeMillis());
						}
						else { Messenger.sendMessage(cs, "LOCK_FAILED"); }
					}
				}
				else { Messenger.sendMessage(cs, "MAX_LOCKS_EXCEEDED"); }
			}
			catch (final Exception e) {
				Messenger.exception("Command_lockanimal/runCommand", "No Information available.", e);
				Messenger.sendMessage(cs, "LOCK_FAILED");
			}
		}
		return true;
	}
}