package de.AnimalProtectOld.commands;

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

import de.AnimalProtectOld.Main;
import de.AnimalProtectOld.MySQL;
import de.AnimalProtectOld.structs.EntityList;
import de.AnimalProtectOld.structs.EntityObject;
import de.AnimalProtectOld.utility.APLogger;

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
		/* Datenbank-Verbindung aufbauen, falls nicht vorhanden. */
		if (!database.checkConnection()) { database.openConnection(); }
		
		if (plugin.isEnabled() && cs instanceof Player) {
			/* Variablen intialisieren */
			Player player = (Player)cs;
			Entity entity = null;
			String owner = null;
			Integer animal = null;
			
			/* Prüfen ob der Spieler die Permission hat */
			if (!player.hasPermission("animalprotect.admin")) {
				player.sendMessage("§cFehler: Du hast nicht genügend Rechte um den Befehl auszuführen!");
				return true;
			}
			
			/* Argumente überprüfen */
			if (args.length == 0) {
				cs.sendMessage("§cFehler: Es fehlen Argumente! (/locklist <owner> <id>)");
				return true;
			}
			else if (args.length == 1) {
				/* Prüfen ob das Argument die Seitennummer oder der Spielername ist */
				if (isNumber(args[0])) {
					animal = Integer.parseInt(args[0]);
					
					if (cs instanceof Player) { 
						Player p = (Player)cs;
						owner = p.getName();
					}
					else {
						cs.sendMessage("Fehler: Es fehlen Argumente! (/locklist <owner> <id>)");
						return true;
					}
				}
				else {
					animal = 1;
					owner = args[0];
				}
			}
			else if (args.length == 2) {
				owner = args[0];
				
				if (isNumber(args[1])) {
					animal = Integer.parseInt(args[1]);
				}
				else {
					cs.sendMessage("§cFehler: Die angegebene Zahl ist keine Nummer!");
					return true;
				}
			}
			else { cs.sendMessage("§cFehler: Zu viele Argumente angegeben!"); return true; }
			
			/* Prüfen ob der Spieler existiert */
			if (!list.containsPlayer(owner)) {
				player.sendMessage("§cFehler: Der angegebene Spieler wurde nicht gefunden!");
				return true;
			}
			
			/* Alle Entities des Spielers in eine Liste packen */
			ArrayList<EntityObject> array = list.getEntities(owner);
			
			/* Prüfen ob das Entity gefunden wurde */
			if (array == null) {
				player.sendMessage("§cFehler: Das Tier oder der Spieler wurde nicht gefunden!");
				return true;
			}
			
			if (array.isEmpty() || animal < 0 || animal > array.size() || array.size() == 0) {
				player.sendMessage("§cFehler: Das Tier wurde nicht gefunden!");
				return true;
			}
			
			/* Das Entity aus der Liste rausholen */
			EntityObject object = array.get(animal-1);
			
			/* Jetzt versuchen das Entity in der Welt zu spawnen */
			try { plugin.getLogger().info("Spawning Entity: " + EntityType.valueOf(object.getType())); } catch (Exception e) { }
			try { entity = player.getWorld().spawnEntity(player.getLocation(), EntityType.valueOf(object.getType().toUpperCase())); } 
			catch (Exception e) {
				player.sendMessage("§cFehler: Das Tier konnte nicht in der Welt gespawned werden!");
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
				database.execute(Query, true);
				
				APLogger.info("[TEMPDEBUG] Getting EntityObject from uuid: " + object.getUniqueID());
				
				
				/* Das EntityObject updaten */
				object.update();
				
				player.sendMessage("§aDas Tier wurde erfolgreich gespawnt!");
				return true;
			} 
			catch (Exception e) { 
				e.printStackTrace(); 
				player.sendMessage("§cFehler: Das respawnen ist fehlgeschlagen!");
			}
		}
		return false;
	}
	
	private boolean isNumber(String value) {
		try {
			Integer.parseInt(value);
			return true;
		}
		catch (Exception e) { return false; }
	}
}
