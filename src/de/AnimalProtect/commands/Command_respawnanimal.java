package de.AnimalProtect.commands;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
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
import org.bukkit.entity.Horse.Variant;
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
	
	public static void runCommand(CommandSender cs, String[] args) { // respawnanimal <name> <id> <flags>
		/* Datenbank-Verbindung aufbauen, falls nicht vorhanden. */
		if (plugin.getDatenbank().isConnected())
		{ plugin.getDatenbank().connect(); }
		
		if (!(cs instanceof Player)) {
			Messenger.sendMessage(cs, "SENDER_NOT_PLAYER");
			return;
		}
		
		if (args.length < 2) {
			Messenger.sendMessage(cs, "TOO_FEW_ARGUMENTS"); 
			return;
		}
		
		/* Variablen bereitstellen */
		Player sender = (Player)cs;
		CraftoPlayer cOwner = null;
		String owner = null;
		Integer start = null;
		Integer end = null;
		Boolean idFlag = false;
		Boolean startFlag = false;
		Boolean endFlag = false;
		Boolean missingFlag = false;
		Boolean locationFlag = false;
		
		/* Die Flags ermitteln */
		for (String s : args) {
			if (s.startsWith("p:")) {
				owner = s.substring(2, s.length());
			}
			else if (s.startsWith("id:")) {
				if (isNumber(s.substring(3, s.length()))) {
					if (!startFlag) {
						start = Integer.parseInt(s.substring(3, s.length()));
						end = start;
						startFlag = false;
						endFlag = false;
						idFlag = true;
					}
					else { Messenger.sendMessage(cs, "START_FLAG_ALREADY"); return; }
				}
				else { Messenger.sendMessage(cs, "ID_NOT_NUMBER"); return; }
			}
			else if (s.startsWith("start:")) {
				if (!idFlag) {
					if (isNumber(s.substring(6, s.length()))) {
						start = Integer.parseInt(s.substring(6, s.length()));
						startFlag = true;
						idFlag = false;
					}
					else { Messenger.sendMessage(cs, "START_NOT_NUMBER"); return; }
				}
				else { Messenger.sendMessage(cs, "ID_FLAG_ALREADY"); return; }
			}
			else if (s.startsWith("end:")) {
				if (!idFlag) {
					if (isNumber(s.substring(4, s.length()))) {
						end = Integer.parseInt(s.substring(4, s.length()));
						endFlag = true;
						idFlag = false;
					}
				}
				else { Messenger.sendMessage(cs, "ID_FLAG_ALREADY"); return; }
			}
			else if (s.startsWith("-missing")) {
				missingFlag = true;
			}
			else if (s.startsWith("-location")) {
				locationFlag = true;
			}
		}
		
		Messenger.broadcast("start:"+start+" / end:"+end+" / missing:"+missingFlag+" / location:"+locationFlag+" / idFlag="+idFlag+ " / startFlag:"+startFlag+" / endflag:"+endFlag);
		
		/* Den angegebenen Spieler ermitteln */
		if (isUUID(args[0])) { cOwner = CraftoPlayer.getPlayer(UUID.fromString(owner)); }
		else { cOwner = CraftoPlayer.getPlayer(owner); }
		
		if (cOwner == null) { Messenger.sendMessage(cs, "PLAYER_NOT_FOUND"); return; }
		
		/* Alle Entities in eine ArrayList speichern, damit wir sie später für isMissing() haben */
		ArrayList<UUID> foundEntities = new ArrayList<UUID>();
		for (Entity entity : sender.getWorld().getEntities()) {
			if (!entity.isDead()) { foundEntities.add(entity.getUniqueId()); }
		}
		
		/* Alle Flags durchgehen */
		if (idFlag) {
			Animal animal = plugin.getDatenbank().getAnimals(cOwner.getUniqueId()).get(start);
			
			if (animal == null) { Messenger.sendMessage(cs, "ANIMAL_NOT_FOUND"); return; }
			else if (missingFlag && isMissing(animal, foundEntities)) { Command_respawnanimal.respawnAnimal(animal, cOwner, sender, true, locationFlag); }
			else if (!missingFlag) { Command_respawnanimal.respawnAnimal(animal, cOwner, sender, true, locationFlag); }
		}
		else if (startFlag && !endFlag) {
			for (int i=start; i<plugin.getDatenbank().getAnimals(cOwner.getUniqueId()).size(); i++) { 
				Animal foundAnimal = plugin.getDatenbank().getAnimals(cOwner.getUniqueId()).get(i);
				if (foundAnimal == null) { continue; }
				
				if(missingFlag && isMissing(foundAnimal, foundEntities)) {
					Command_respawnanimal.respawnAnimal(foundAnimal, cOwner, sender, true, locationFlag);
				}
				else if (!missingFlag) {
					Command_respawnanimal.respawnAnimal(foundAnimal, cOwner, sender, true, locationFlag);
				}
			}
		}
		else if  (startFlag && endFlag) {
			if (end > plugin.getDatenbank().getAnimals(cOwner.getUniqueId()).size()) {
				Messenger.sendMessage(cs, "ENDFLAG_TOO_HIGH");
			}
			else {
				for (int i=start; i<end; i++) {
					Animal foundAnimal = plugin.getDatenbank().getAnimals(cOwner.getUniqueId()).get(i);
					if (foundAnimal == null) { continue; }
					
					if(missingFlag && isMissing(foundAnimal, foundEntities)) {
						Command_respawnanimal.respawnAnimal(foundAnimal, cOwner, sender, true, locationFlag);
					}
					else if (!missingFlag) {
						Command_respawnanimal.respawnAnimal(foundAnimal, cOwner, sender, true, locationFlag);
					}
				}
			}
		}
	}
		
	public static boolean isMissing(Animal animal, ArrayList<UUID> entities) {
		if (animal.isAlive() && !entities.contains(animal.getUniqueId())) {
			return true;
		}
		return false;
	}
	
	public static boolean respawnAnimal(Animal animal, CraftoPlayer owner, Player sender, Boolean response, Boolean locationFlag) {
		Entity entity = null;
				
		if (locationFlag) { 
			Location loc = new Location(sender.getWorld(), animal.getLast_x(), animal.getLast_y(), animal.getLast_z());
			entity = sender.getWorld().spawnEntity(loc, animal.getAnimaltype().getEntity());
		}
		else { entity = sender.getWorld().spawnEntity(sender.getLocation(), animal.getAnimaltype().getEntity()); }
		
		UUID oldUUID = animal.getUniqueId();
		
		if (entity == null)
		{ Messenger.sendMessage(sender, "ANIMAL_NOT_RESPAWNED"); return false; }
		
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
			horse.setVariant(Variant.valueOf(animal.getHorse_variant().toString()));
			horse.setStyle(animal.getHorse_style());
			horse.setMaxHealth(animal.getMaxhp());
			horse.setJumpStrength(animal.getHorse_jumpstrength());
			horse.setOwner(Bukkit.getServer().getOfflinePlayer(owner.getUniqueId()));
			horse.setTamed(true);
		}
		else if (livingEntity.getType().equals(EntityType.WOLF)) {
			Wolf wolf = (Wolf) entity;
			wolf.setCollarColor(DyeColor.valueOf(animal.getColor()));
		}
		
		/* Das Tier updaten und sichern */
		animal.updateAnimal(entity);
		if (plugin.getDatenbank().updateAnimal(animal.getId(), animal, oldUUID)) {
			if (response) { Messenger.sendMessage(sender, "ANIMAL_RESPAWNED"); }
			return true;
		}
		else { Messenger.sendMessage(sender, "ANIMAL_NOT_RESPAWNED"); }
		
		return false;
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
		return value.matches(".*-.*-.*-.*-.*");
	}
}
