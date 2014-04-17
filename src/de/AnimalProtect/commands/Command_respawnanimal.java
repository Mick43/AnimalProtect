package de.AnimalProtect.commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Horse.Color;
import org.bukkit.inventory.ItemStack;

import craftoplugin.core.database.CraftoPlayer;
import de.AnimalProtect.AnimalProtect;
import de.AnimalProtect.Messenger;
import de.AnimalProtect.structs.Animal;
import de.AnimalProtect.structs.AnimalArmor;

public class Command_respawnanimal implements CommandExecutor {
	
	private static AnimalProtect plugin;
	
	public Command_respawnanimal(AnimalProtect plugin) {
		Command_respawnanimal.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if (!plugin.isEnabled()) { return true; }
		Command_respawnanimal.runCommand(cs, args);
		return true;
	}
	
	public static void runCommand(CommandSender cs, String[] args) {
		/* Datenbank-Verbindung aufbauen, falls nicht vorhanden. */
		if (plugin.getDatenbank().isConnected())
		{ plugin.getDatenbank().connect(); }
		
		if (!(cs instanceof Player)) {
			Messenger.sendMessage(cs, "§cFehler: Dieser Befehl kann nur von einem Spieler ausgeführt werden!");
			return;
		}
		
		if (args.length < 2) {
			Messenger.sendMessage(cs, "§cFehler: Es wurden zu wenig Argumente angegeben!"); 
			return;
		}
		
		Player player = (Player)cs;
		CraftoPlayer cPlayer = null;
		Integer animalId = null;
		Animal animal = null;
		
		if (isUUID(args[0])) { cPlayer = CraftoPlayer.getPlayer(UUID.fromString(args[0])); }
		else { cPlayer = CraftoPlayer.getPlayer(args[0]); }
		
		if (cPlayer == null) { Messenger.sendMessage(cs, "§cFehler: Der Spieler konnte nicht gefunden werden!"); return; }
		
		if (isNumber(args[1])) { animalId = Integer.parseInt(args[1]); }
		else { Messenger.sendMessage(cs, "§cFehler: Die angegebene ID des Tieres ist keine Zahl!"); return; }
		
		animal = plugin.getDatenbank().getAnimals(cPlayer.getUniqueId()).get(animalId);
		
		if (animal == null) { Messenger.sendMessage(cs, "§cFehler: Das angegebene Tier existiert nicht!"); return; }
		
		Entity entity = null;
		entity = player.getWorld().spawnEntity(player.getLocation(), animal.getAnimaltype().getEntity());
		
		if (entity == null)
		{ Messenger.sendMessage(cs, "§cFehler: Das Tier konnte nicht respawned werden!"); return; }
		
		LivingEntity livingEntity = (LivingEntity) entity;
		livingEntity.setCustomName(animal.getNametag());
		
		if (livingEntity.getType().equals(EntityType.SHEEP)) { 
			Sheep sheep = (Sheep) entity; 
			sheep.setColor(DyeColor.valueOf(animal.getColor())); 
		}
		else if (livingEntity.getType().equals(EntityType.HORSE)) {
			Horse horse = (Horse) entity;
			if (animal.getArmor() == AnimalArmor.DIAMOND) {
				horse.getInventory().setArmor(new ItemStack(Material.DIAMOND_BARDING));
			}
			else if (animal.getArmor() == AnimalArmor.IRON) {
				horse.getInventory().setArmor(new ItemStack(Material.IRON_BARDING));
			}
			else if (animal.getArmor() == AnimalArmor.GOLD) {
				horse.getInventory().setArmor(new ItemStack(Material.GOLD_BARDING));
			}
			
			horse.setColor(Color.valueOf(animal.getColor()));
			horse.setVariant(animal.getHorse_variant());
			horse.setStyle(animal.getHorse_style());
			horse.setMaxHealth(animal.getMaxhp());
			horse.setJumpStrength(animal.getHorse_jumpstrength());
			horse.setOwner(Bukkit.getServer().getOfflinePlayer(cPlayer.getUniqueId()));
			horse.setTamed(true);
		}
		else if (livingEntity.getType().equals(EntityType.WOLF)) {
			Wolf wolf = (Wolf) entity;
			wolf.setCollarColor(DyeColor.valueOf(animal.getColor()));
		}
		
		/* Die Protection entfernen */
		plugin.getDatenbank().unlockAnimal(animal);
		
		/* Das Tier updaten und sichern */
		animal.updateAnimal(entity);
		if (animal.saveToDatabase(true))
		{ Messenger.sendMessage(cs, "§aDas Tier wurde erfolgreich respawned."); }
		else { Messenger.sendMessage(cs, "§cFehler: Das Tier konnte nicht komplett respawned werden!"); }
	}
	
	private static boolean isNumber(String value) {
		try {
			Integer.parseInt(value);
			return true;
		}
		catch (Exception e) { }
		return false;
	}
	
	private static boolean isUUID(String value) {
		//TODO: isUUID
		return false;
	}
}
