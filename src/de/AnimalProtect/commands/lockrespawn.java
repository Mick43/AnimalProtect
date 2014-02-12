package de.AnimalProtect.commands;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

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
			
			/* Argumente überprüfen */
			if (args.length == 0)
			{ player.sendMessage("§cFehler: Es fehlen Argumente! /lockrespawn <id> <owner>"); return true; }
			else if (args.length == 1)
			{ owner = player.getName(); }
			else if (args.length == 2)
			{ owner = args[1]; }
			else
			{ player.sendMessage("§cFehler: Zu viele Argumente! /lockrespawn <id> <owner>"); return true; }
			
			/* Prüfen ob die <ID> eine Zahl ist */
			try { animal = Integer.parseInt(args[0]); }
			catch (NumberFormatException e) { 
				player.sendMessage("§cFehler: Die angegebene ID ist keine Zahl! /lockrespawn <id> <owner>");
				return true;
			}
			
			/* Das Entity aus der Datenbank auslesen */
			//ResultSet result = database.get("SELECT * FROM ap_entities WHERE ID=("
		    //		+ "SELECT entity_id FROM ap_locks WHERE owner_id=("
		    //		+ "SELECT id FROM ap_owners WHERE name='" + owner + "') LIMIT " + (animal-1) + ", 1);", true, true);
			ResultSet result = database.get("SELECT * FROM ap_entities "
					+ "INNER JOIN ap_locks ON ap_locks.entity_id=ap_entities.id "
					+ "INNER JOIN ap_owners ON ap_locks.owner_id=ap_owners.id WHERE ap_entities.id="+(animal)+";", true, true);
			
			/* Prüfen ob das Entity gefunden wurde */
			if (result == null) {
				player.sendMessage("§cFehler: Das Tier wurde nicht gefunden!");
				return true;
			}
			
			/* Jetzt versuchen das Entity in der Welt zu spawnen */
			try { plugin.getLogger().info("Spawning Entity: " + EntityType.valueOf(result.getString("animaltype").toUpperCase())); } catch (Exception e) { }
			try { entity = player.getWorld().spawnEntity(player.getLocation(), EntityType.valueOf(result.getString("animaltype").toUpperCase())); } 
			catch (SQLException e) {
				player.sendMessage("§cFehler: Das Tier konnte nicht in der Welt gespawned werden!");
				e.printStackTrace(); 
			}
			
			/* Nun dem Entity alle Eigenschaften geben */
			LivingEntity le = (LivingEntity) entity;
			try { if (result.getString("ap_entities.nametag") != null) {
				le.setCustomName(result.getString("ap_entities.nametag")); }
			} catch (Exception e) { e.printStackTrace(); }
			if (entity.getType() == EntityType.HORSE) {
				Horse horse = (Horse)entity;
				try { horse.setColor(Color.valueOf(result.getString("ap_entities.color").toUpperCase())); } catch (Exception e) { e.printStackTrace();}
				try { horse.setMaxHealth(result.getDouble("ap_entities.maxhp")); } catch (Exception e) { e.printStackTrace(); }
				try { horse.setJumpStrength(result.getDouble("ap_entities.horse_jumpstrength")); } catch (Exception e) { e.printStackTrace(); }
				try { horse.setStyle(Style.valueOf(result.getString("ap_entities.horse_style").toUpperCase())); } catch (Exception e) { e.printStackTrace(); }
				try { horse.setVariant(Variant.valueOf(result.getString("ap_entities.horse_variant").toUpperCase())); } catch (Exception e) { e.printStackTrace(); }
				try { horse.setOwner(plugin.getServer().getOfflinePlayer(result.getString("ap_owners.name"))); } catch (Exception e) { e.printStackTrace(); }
				String armor = null;
				try { armor = result.getString("ap_entities.armor"); } catch (Exception e) { }
				if (armor != null) {
					if (armor.equalsIgnoreCase("iron")) { horse.getInventory().setArmor(new ItemStack(Material.IRON_BARDING)); }
					else if (armor.equalsIgnoreCase("gold")) { horse.getInventory().setArmor(new ItemStack(Material.GOLD_BARDING)); }
					else if (armor.equalsIgnoreCase("diamond")) { horse.getInventory().setArmor(new ItemStack(Material.DIAMOND_BARDING)); }
				}
			}
			else if (entity.getType() == EntityType.SHEEP) {
				Sheep sheep = (Sheep)entity;
				try { sheep.setColor(DyeColor.valueOf(result.getString("ap_entities.color").toUpperCase())); } catch (Exception e) { e.printStackTrace(); }
			}
			else if (entity.getType() == EntityType.WOLF) {
				Wolf wolf = (Wolf)entity;
				try { wolf.setCollarColor(DyeColor.valueOf(result.getString("ap_entities.color").toUpperCase())); } catch (Exception e) { e.printStackTrace(); }
				try { wolf.setOwner(plugin.getServer().getOfflinePlayer(result.getString("ap_owners.name"))); } catch (Exception e) { e.printStackTrace(); }
			}
			
			/* Jetzt dem EntityObject in der Datenbank die neuen Werte geben */
			try {
				int x = entity.getLocation().getBlockX();
				int y = entity.getLocation().getBlockY();
				int z = entity.getLocation().getBlockZ();
				String id = entity.getUniqueId().toString();
				
				String Query = "UPDATE ap_entities SET uuid='"+id+"', last_x="+x+", last_y="+y+", last_z="+z+" "
						+ "WHERE uuid='" + result.getString("ap_entities.uuid")+"'";
				database.write(Query, true);
				
				/* Das EntityObjet aus dem RAM holen und updaten */
				EntityObject ent = list.getEntityObject(UUID.fromString(result.getString("ap_entities.uuid")));
				if (ent != null) { ent.setUniqueID(id); ent.update(); }
				else { APLogger.info("[Error/lockrespawn] EntityObject==null! Zeile 132"); }
				
				player.sendMessage("§aDas Tier wurde erfolgreich gespawnt!");
			} 
			catch (SQLException e) { 
				e.printStackTrace(); 
				player.sendMessage("§cFehler: Das respawnen ist fehlgeschlagen!");
			}
		}
		return false;
	}
}
