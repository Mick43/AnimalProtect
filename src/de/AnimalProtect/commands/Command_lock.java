package de.AnimalProtect.commands;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
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
import de.AnimalProtect.listeners.PrismEventListener;
import de.AnimalProtect.structs.Animal;

public class Command_lock implements CommandExecutor {
	
	private AnimalProtect plugin;
	private HashMap<UUID, Long> lockTimes;
	
	public Command_lock(AnimalProtect plugin) {
		this.plugin = plugin;
		this.lockTimes = new HashMap<UUID, Long>();
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if (plugin == null || !plugin.isEnabled()) { Messenger.sendMessage(cs, "§cFehler: Der Befehl konnte nicht ausgeführt werden."); return true; }
		
		/* Datenbank-Verbindung aufbauen, falls nicht vorhanden. */
		if (!plugin.getDatenbank().isConnected())
		{ plugin.getDatenbank().connect(); }
		
		/* Prüfen ob der Sender ein Spieler ist */
		if (!(cs instanceof Player)) { Messenger.sendMessage(cs, "SENDER_NOT_PLAYER"); return true; }
		
		if (lockTimes == null) { lockTimes = new HashMap<UUID, Long>(); }
		
		/* Variablen bereitstellen */
		Player sender = (Player)cs;
		CraftoPlayer player = CraftoPlayer.getPlayer(sender.getUniqueId());
		Entity entity = plugin.getSelectedAnimal(sender.getUniqueId());
		
		/* Variablen überprüfen */
		if (entity == null) { Messenger.sendMessage(cs, "SELECTED_NONE"); return true; }
		else if (player == null) { Messenger.sendMessage(cs, "PLAYEROBJECT_NOT_FOUND"); return true; }
		
		/* Das Animal-Objekt laden */
		Animal animal = plugin.getDatenbank().getAnimal(entity.getUniqueId());
		
		if (animal != null) { Messenger.sendMessage(cs, "ANIMAL_ALREADY_PROTECTED"); return true; }
		else {
			try {
				if (plugin.getDatenbank().countAnimals(player.getUniqueId()) <= plugin.getConfig().getInt("settings.max_entities_for_player")) {
					if (lockTimes.containsKey(sender.getUniqueId())) {
						if (lockTimes.get(sender.getUniqueId()) + 5000 < System.currentTimeMillis()) {
							animal = new Animal(AnimalProtect.plugin, player, entity);
							if(animal.saveToDatabase(true)) { 
								Messenger.sendMessage(cs, "LOCK_SUCCESS"); 
								entity.getWorld().playEffect(entity.getLocation(), Effect.SMOKE, 4);
								entity.getWorld().playEffect(entity.getLocation(), Effect.SMOKE, 1);
								entity.getWorld().playEffect(entity.getLocation(), Effect.SMOKE, 3);
								entity.getWorld().playEffect(entity.getLocation(), Effect.SMOKE, 5);
								entity.getWorld().playEffect(entity.getLocation(), Effect.SMOKE, 7);
								entity.getWorld().playSound(entity.getLocation(), Sound.DOOR_CLOSE, 0.5f, 1.75f);
								entity.getWorld().playSound(entity.getLocation(), Sound.DOOR_CLOSE, 0.5f, 1.75f);
								entity.getWorld().playSound(entity.getLocation(), Sound.DOOR_CLOSE, 0.5f, 1.75f);
								sender.playSound(sender.getLocation(), Sound.DOOR_CLOSE, 0.75f, 1.75f);
								lockTimes.put(sender.getUniqueId(), System.currentTimeMillis());
								
								if (Bukkit.getServer().getPluginManager().isPluginEnabled("Prism"))
								{ PrismEventListener.logEvent(animal, sender, plugin); }
							}
							else { Messenger.sendMessage(cs, "LOCK_FAILED"); }
						}
						else {
							Integer wait = (int) Math.floor((lockTimes.get(sender.getUniqueId()) + 5000 - System.currentTimeMillis()));
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
							entity.getWorld().playSound(entity.getLocation(), Sound.DOOR_CLOSE, 0.5f, 1.75f);
							entity.getWorld().playSound(entity.getLocation(), Sound.DOOR_CLOSE, 0.5f, 1.75f);
							entity.getWorld().playSound(entity.getLocation(), Sound.DOOR_CLOSE, 0.5f, 1.75f);
							sender.playSound(sender.getLocation(), Sound.DOOR_CLOSE, 0.75f, 1.75f);
							lockTimes.put(sender.getUniqueId(), System.currentTimeMillis());
						}
						else { Messenger.sendMessage(cs, "LOCK_FAILED"); }
					}
				}
				else { Messenger.sendMessage(cs, "MAX_LOCKS_EXCEEDED"); }
			}
			catch (Exception e) {
				Messenger.exception("Command_lockanimal/runCommand", "No Information available.", e);
				Messenger.sendMessage(cs, "LOCK_FAILED");
			}
		}
		return true;
	}
}
