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

/**
 * Die Respawncommand-Klasse. {@code /respawnanimal}
 * 
 * @author Fear837, Pingebam
 * @version 1.0
 * @see CommandExecutor
 */
public class Command_respawn implements CommandExecutor {

	/** Die AnimalProtect-Instanz. */
	private final AnimalProtect plugin;

	/**
	 * Initialisiert die Commandklasse.
	 * @param plugin - Das AnimalProtect-Plugin.
	 */
	public Command_respawn(final AnimalProtect plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(final CommandSender cs, final Command cmd, final String label, final String[] args) {
		if (!this.plugin.isEnabled()) { Messenger.sendMessage(cs, "§cFehler: Der Befehl konnte nicht ausgeführt werden."); return true; }

		/* Datenbank-Verbindung aufbauen, falls nicht vorhanden. */
		if (!this.plugin.getDatenbank().isConnected())
		{ this.plugin.getDatenbank().connect(); }

		if (!(cs instanceof Player)) { Messenger.sendMessage(cs, "SENDER_NOT_PLAYER"); return true; }
		if (args.length < 2) { Messenger.sendMessage(cs, "TOO_FEW_ARGUMENTS"); return true; }

		/* Liste erstellen */
		final ArrayList<Animal> list = this.plugin.parseAnimal(cs, args, false);
		if (list == null) { return true; }
		else if (list.isEmpty()) { Messenger.sendMessage(cs, "ANIMALS_NOT_FOUND"); return true; }

		/* LocationFlag */
		boolean locationFlag = false;
		for(final String arg : args) { if (arg.equalsIgnoreCase("-location")) { locationFlag = true; } }

		int failedCounter = 0;
		for (final Animal animal : list) {
			if(!this.respawnAnimal(animal, animal.getCraftoOwner(), ((Player)cs), true, locationFlag)) 
			{ failedCounter += 1; }
		}

		if (failedCounter == 0) { Messenger.sendMessage(cs, "§aEs wurden alle ausgewählten Tiere erfolgreich respawned."); }
		else { Messenger.sendMessage(cs, "§cFehler: Es konnten "+failedCounter+" von "+list.size()+" nicht respawned werden."); }
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
}