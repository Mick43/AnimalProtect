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
import org.bukkit.entity.Horse.Color;
import org.bukkit.entity.Horse.Variant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.ItemStack;

import craftoplugin.core.database.CraftoPlayer;
import de.AnimalProtect.AnimalProtect;
import de.AnimalProtect.Messenger;
import de.AnimalProtect.structs.Animal;
import de.AnimalProtect.structs.AnimalArmor;
import de.AnimalProtect.structs.AnimalType;

public class Command_respawn implements CommandExecutor {
	
	private final AnimalProtect plugin;
	
	public Command_respawn(final AnimalProtect plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(final CommandSender cs, final Command cmd, final String label, final String[] args) {
		if (this.plugin == null || !this.plugin.isEnabled()) { Messenger.sendMessage(cs, "§cFehler: Der Befehl konnte nicht ausgeführt werden."); return true; }
		
		/* Datenbank-Verbindung aufbauen, falls nicht vorhanden. */
		if (!this.plugin.getDatenbank().isConnected())
		{ this.plugin.getDatenbank().connect(); }
		
		if (!(cs instanceof Player)) { Messenger.sendMessage(cs, "SENDER_NOT_PLAYER"); return true; }
		if (args.length < 2) { Messenger.sendMessage(cs, "TOO_FEW_ARGUMENTS"); return true; }
		
		/* Variablen bereitstellen */
		final Player sender = (Player)cs;
		CraftoPlayer cOwner = null;
		String owner = null;
		Integer start = null;
		Integer end = null;
		AnimalType type = null;
		Boolean idFlag = false;
		Boolean startFlag = false;
		Boolean endFlag = false;
		Boolean missingFlag = false;
		Boolean locationFlag = false;
		Boolean typeFlag = false;
		
		/* Die Flags ermitteln */
		for (final String s : args) {
			if (s.startsWith("p:")) {
				owner = s.substring(2, s.length());
			}
			else if (s.startsWith("id:")) {
				if (this.isNumber(s.substring(3, s.length()))) {
					if (!startFlag) {
						start = Integer.parseInt(s.substring(3, s.length()));
						end = start;
						startFlag = false;
						endFlag = false;
						idFlag = true;
					}
					else { Messenger.sendMessage(cs, "START_FLAG_ALREADY"); return true; }
				}
				else { Messenger.sendMessage(cs, "ID_NOT_NUMBER"); return true; }
			}
			else if (s.startsWith("start:")) {
				if (!idFlag) {
					if (this.isNumber(s.substring(6, s.length()))) {
						start = Integer.parseInt(s.substring(6, s.length()));
						startFlag = true;
						idFlag = false;
					}
					else { Messenger.sendMessage(cs, "START_NOT_NUMBER"); return true; }
				}
				else { Messenger.sendMessage(cs, "ID_FLAG_ALREADY"); return true; }
			}
			else if (s.startsWith("end:")) {
				if (!idFlag) {
					if (this.isNumber(s.substring(4, s.length()))) {
						end = Integer.parseInt(s.substring(4, s.length()));
						endFlag = true;
						idFlag = false;
					}
				}
				else { Messenger.sendMessage(cs, "ID_FLAG_ALREADY"); return true; }
			}
			else if (s.startsWith("-missing")) {
				missingFlag = true;
			}
			else if (s.startsWith("-location")) {
				locationFlag = true;
			}
			else if (s.startsWith("type:")) {
				type = AnimalType.valueOf(s.substring(5, s.length()));
				if (type != null) { typeFlag = true; }
			}
		}
		
		if (owner == null) { Messenger.sendMessage(cs, "§cFehler: Es wurde kein Spieler angegeben!"); return true; }
		if (!idFlag && !startFlag && !endFlag) { Messenger.sendMessage(cs, "§cFehler: Es wurde kein Start oder Endpunkt festgelegt!"); }
		
		/* Den angegebenen Spieler ermitteln */
		if (this.isUUID(args[0])) { cOwner = CraftoPlayer.getPlayer(UUID.fromString(owner)); }
		else { cOwner = CraftoPlayer.getPlayer(owner); }
		
		if (cOwner == null) { Messenger.sendMessage(cs, "PLAYER_NOT_FOUND"); return true; }
		
		/* Alle Entities in eine ArrayList speichern, damit wir sie später für isMissing() haben */
		final ArrayList<UUID> foundEntities = new ArrayList<UUID>();
		for (final Entity entity : sender.getWorld().getEntities()) {
			if (!typeFlag) { if (!entity.isDead()) { foundEntities.add(entity.getUniqueId()); } }
			else if (entity.getType().equals(type.getEntity())) 
			{ if (!entity.isDead()) { foundEntities.add(entity.getUniqueId()); } }
		}
		
		/* Alle Flags durchgehen */
		if (idFlag) {
			final Animal animal = this.plugin.getDatenbank().getAnimals(cOwner.getUniqueId()).get(start);
			
			if (animal == null) { Messenger.sendMessage(cs, "ANIMAL_NOT_FOUND"); return true; }
			else if (missingFlag && this.isMissing(animal, foundEntities)) { this.respawnAnimal(animal, cOwner, sender, true, locationFlag); }
			else if (!missingFlag) { this.respawnAnimal(animal, cOwner, sender, true, locationFlag); }
		}
		else if (startFlag && !endFlag) {
			for (int i=start; i<this.plugin.getDatenbank().getAnimals(cOwner.getUniqueId()).size(); i++) { 
				final Animal foundAnimal = this.plugin.getDatenbank().getAnimals(cOwner.getUniqueId()).get(i);
				if (foundAnimal == null) { continue; }
				
				if(missingFlag && this.isMissing(foundAnimal, foundEntities)) {
					this.respawnAnimal(foundAnimal, cOwner, sender, true, locationFlag);
				}
				else if (!missingFlag) {
					this.respawnAnimal(foundAnimal, cOwner, sender, true, locationFlag);
				}
			}
		}
		else if  (startFlag && endFlag) {
			if (end > this.plugin.getDatenbank().getAnimals(cOwner.getUniqueId()).size()) {
				Messenger.sendMessage(cs, "ENDFLAG_TOO_HIGH");
			}
			else {
				for (int i=start; i<end; i++) {
					final Animal foundAnimal = this.plugin.getDatenbank().getAnimals(cOwner.getUniqueId()).get(i);
					if (foundAnimal == null) { continue; }
					
					if(missingFlag && this.isMissing(foundAnimal, foundEntities)) {
						this.respawnAnimal(foundAnimal, cOwner, sender, true, locationFlag);
					}
					else if (!missingFlag) {
						this.respawnAnimal(foundAnimal, cOwner, sender, true, locationFlag);
					}
				}
			}
		}
		return true;
	}
		
	public boolean isMissing(final Animal animal, final ArrayList<UUID> entities) {
		if (animal.isAlive() && !entities.contains(animal.getUniqueId())) {
			return true;
		}
		return false;
	}
	
	public boolean respawnAnimal(final Animal animal, final CraftoPlayer owner, final Player sender, final Boolean response, final Boolean locationFlag) {
		Entity entity = null;
				
		if (locationFlag) { 
			final Location loc = new Location(sender.getWorld(), animal.getX(), animal.getY(), animal.getZ());
			entity = sender.getWorld().spawnEntity(loc, animal.getAnimaltype().getEntity());
		}
		else { entity = sender.getWorld().spawnEntity(sender.getLocation(), animal.getAnimaltype().getEntity()); }
		
		final UUID oldUUID = animal.getUniqueId();
		
		if (entity == null)
		{ Messenger.sendMessage(sender, "ANIMAL_NOT_RESPAWNED"); return false; }
		
		final LivingEntity livingEntity = (LivingEntity) entity;
		livingEntity.setCustomName(animal.getNametag());
		
		if (livingEntity.getType().equals(EntityType.SHEEP)) { 
			final Sheep sheep = (Sheep) entity; 
			sheep.setColor(DyeColor.valueOf(animal.getColor())); 
		}
		else if (livingEntity.getType().equals(EntityType.HORSE)) {
			final Horse horse = (Horse) entity;
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
			final Wolf wolf = (Wolf) entity;
			wolf.setCollarColor(DyeColor.valueOf(animal.getColor()));
		}
		
		/* Das Tier updaten und sichern */
		animal.updateAnimal(entity);
		if (this.plugin.getDatenbank().updateAnimal(animal.getId(), animal, oldUUID)) {
			if (response) { Messenger.sendMessage(sender, "ANIMAL_RESPAWNED"); }
			return true;
		}
		else { Messenger.sendMessage(sender, "ANIMAL_NOT_RESPAWNED"); }
		
		return false;
	}
	
	private boolean isNumber(final String value) {
		try {
			Integer.parseInt(value);
			return true;
		}
		catch (final Exception e) { }
		return false;
	}
	
	private boolean isUUID(final String value) {
		return value.matches(".*-.*-.*-.*-.*");
	}
}
