package de.AnimalProtect.commands;

import java.util.ArrayList;

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
import org.bukkit.entity.Horse.Style;
import org.bukkit.entity.Horse.Variant;
import org.bukkit.inventory.ItemStack;

import de.AnimalProtect.Main;
import de.AnimalProtect.MySQL;
import de.AnimalProtect.structs.EntityList;
import de.AnimalProtect.structs.EntityObject;
import de.AnimalProtect.utility.APLogger;

public class lockrespawn implements CommandExecutor {

	Main plugin;
	MySQL database;
	EntityList list;
	
	public lockrespawn(Main plugin) {
		this.plugin = plugin;
		this.database = plugin.database;
		this.list = plugin.list;
	}
	
	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if (plugin.isEnabled() && cs instanceof Player) {
			/* Variablen intialisieren */
			Player player = (Player)cs;
			Entity entity = null;
			String owner = null;
			Integer animal = null;
			
			/* Pr�fen ob der Spieler die Permission hat */
			if (!player.hasPermission("animalprotect.admin")) {
				player.sendMessage("�cFehler: Du hast nicht gen�gend Rechte um den Befehl auszuf�hren!");
				return true;
			}
			
			/* Argumente �berpr�fen */
			if (args.length == 0)
			{ player.sendMessage("�cFehler: Es fehlen Argumente! /lockrespawn <id> <owner>"); return true; }
			else if (args.length == 1)
			{ owner = player.getName(); }
			else if (args.length == 2)
			{ owner = args[1]; }
			else
			{ player.sendMessage("�cFehler: Zu viele Argumente! /lockrespawn <id> <owner>"); return true; }
			
			/* Pr�fen ob die <ID> eine Zahl ist */
			try { animal = Integer.parseInt(args[0]); }
			catch (NumberFormatException e) { 
				player.sendMessage("�cFehler: Die angegebene ID ist keine Zahl! /lockrespawn <id> <owner>");
				return true;
			}
			
			/* Alle Entities des Spielers in eine Liste packen */
			ArrayList<EntityObject> array = list.getEntities(owner);
			
			/* Pr�fen ob das Entity gefunden wurde */
			if (array == null) {
				player.sendMessage("�cFehler: Das Tier wurde nicht gefunden!");
				return true;
			}
			
			if (array.isEmpty() || animal < 0 || animal > array.size() || array.size() == 0) {
				player.sendMessage("�cFehler: Das Tier wurde nicht gefunden!");
				return true;
			}
			
			/* Das Entity aus der Liste rausholen */
			EntityObject object = array.get(animal-1);
			
			/* Jetzt versuchen das Entity in der Welt zu spawnen */
			try { plugin.getLogger().info("Spawning Entity: " + EntityType.valueOf(object.getType())); } catch (Exception e) { }
			try { entity = player.getWorld().spawnEntity(player.getLocation(), EntityType.valueOf(object.getType().toUpperCase())); } 
			catch (Exception e) {
				player.sendMessage("�cFehler: Das Tier konnte nicht in der Welt gespawned werden!");
				e.printStackTrace(); 
			}
			
			/* Nun dem Entity alle Eigenschaften geben */
			LivingEntity le = (LivingEntity) entity;
			try { if (object.getNametag() != null) {
				le.setCustomName(object.getNametag()); }
			} catch (Exception e) { e.printStackTrace(); }
			if (entity.getType() == EntityType.HORSE) {
				Horse horse = (Horse)entity;
				try { horse.setColor(Color.valueOf(object.getColor().toUpperCase())); } catch (Exception e) { e.printStackTrace();}
				try { horse.setMaxHealth(object.getMaxhp()); } catch (Exception e) { e.printStackTrace(); }
				try { horse.setJumpStrength(object.getJumpstrength()); } catch (Exception e) { e.printStackTrace(); }
				try { horse.setStyle(Style.valueOf(object.getStyle().toUpperCase())); } catch (Exception e) { e.printStackTrace(); }
				try { horse.setVariant(Variant.valueOf(object.getVariant().toUpperCase())); } catch (Exception e) { e.printStackTrace(); }
				try { horse.setOwner(plugin.getServer().getOfflinePlayer(object.getOwner())); } catch (Exception e) { e.printStackTrace(); }
				String armor = null;
				try { armor = object.getArmor(); } catch (Exception e) { }
				if (armor != null) {
					if (armor.equalsIgnoreCase("iron")) { horse.getInventory().setArmor(new ItemStack(Material.IRON_BARDING)); }
					else if (armor.equalsIgnoreCase("gold")) { horse.getInventory().setArmor(new ItemStack(Material.GOLD_BARDING)); }
					else if (armor.equalsIgnoreCase("diamond")) { horse.getInventory().setArmor(new ItemStack(Material.DIAMOND_BARDING)); }
				}
			}
			else if (entity.getType() == EntityType.SHEEP) {
				Sheep sheep = (Sheep)entity;
				try { sheep.setColor(DyeColor.valueOf(object.getColor().toUpperCase())); } catch (Exception e) { e.printStackTrace(); }
			}
			else if (entity.getType() == EntityType.WOLF) {
				Wolf wolf = (Wolf)entity;
				try { wolf.setCollarColor(DyeColor.valueOf(object.getColor().toUpperCase())); } catch (Exception e) { e.printStackTrace(); }
				try { wolf.setOwner(plugin.getServer().getOfflinePlayer(object.getOwner())); } catch (Exception e) { e.printStackTrace(); }
			}
			
			/* Jetzt dem EntityObject in der Datenbank die neuen Werte geben */
			try {
				int x = entity.getLocation().getBlockX();
				int y = entity.getLocation().getBlockY();
				int z = entity.getLocation().getBlockZ();
				String id = entity.getUniqueId().toString();
				
				String Query = "UPDATE ap_entities SET uuid='"+id+"', last_x="+x+", last_y="+y+", last_z="+z+" "
						+ "WHERE id=" + object.getId();
				database.write(Query, true);
				
				APLogger.info("[TEMPDEBUG] Getting EntityObject from uuid: " + object.getUniqueID());
				
				
				/* Das EntityObject updaten */
				object.update();
				
				player.sendMessage("�aDas Tier wurde erfolgreich gespawnt!");
				return true;
			} 
			catch (Exception e) { 
				e.printStackTrace(); 
				player.sendMessage("�cFehler: Das respawnen ist fehlgeschlagen!");
			}
		}
		return false;
	}
}
