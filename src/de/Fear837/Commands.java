package de.Fear837;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import com.volcanicplaza.BukkitDev.AnimalSelector.AnimalSelector;

public class Commands implements CommandExecutor {

	private static MySQL sql;
	private AnimalSelector animSel;
	private Server server;

	public Commands(Server server, MySQL sql) {
		if (animSel == null) { animSel = getAnimalSelector(); }
		
		this.server = server;
		
		Commands.sql = sql;
	}

	public static AnimalSelector getAnimalSelector() {
		// Get AnimalSelector plugin
		AnimalSelector plugin = (AnimalSelector) Bukkit.getServer().getPluginManager().getPlugin("AnimalSelector");

		if (plugin == null || !(plugin instanceof AnimalSelector)) {
			Bukkit.getLogger().info("[WARNING] AnimalSelector isn't loaded yet.");
			return null;
		}
		return (AnimalSelector) plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if (animSel == null) { animSel = getAnimalSelector(); }

		if (cmd.getName().equalsIgnoreCase("lockanimal")) {
			Entity entity = null;
			try { entity = animSel.getPlayerSelectedEntity(cs.getName()); } 
			catch (Exception e) { cs.sendMessage(ChatColor.YELLOW + "Es wurde kein Tier ausgew�hlt."); }

			if (entity != null) {
				if (entity.getType() == EntityType.COW 
						|| entity.getType() == EntityType.PIG
						|| entity.getType() == EntityType.SHEEP
						|| entity.getType() == EntityType.CHICKEN
						|| entity.getType() == EntityType.HORSE
						|| entity.getType() == EntityType.WOLF) {
					String isAlreadyLocked = getEntityOwner(entity.getUniqueId());
					
					LivingEntity e = (LivingEntity)entity;
					
					if (isAlreadyLocked == null) 
					{
						addEntity(entity.getUniqueId(), cs.getName(), e
								.getLocation().getBlockX(), e.getLocation()
								.getBlockY(), e.getLocation().getBlockZ(), e
								.getType().toString(), e.getCustomName());
						cs.sendMessage(ChatColor.GREEN + "Das Tier wurde gesichert! (ID: "
								+ e.getUniqueId() + ", NewOwner: "
								+ getEntityOwner(entity.getUniqueId()) + ")");
					} 
					else { cs.sendMessage(ChatColor.RED + "Das Tier ist bereits von " + isAlreadyLocked + " gesichert."); }
				}
				else { cs.sendMessage(ChatColor.RED + "Dieses Entity ist kein Tier!"); }
			}
			else { cs.sendMessage(ChatColor.RED + "Du hast kein Tier ausgew�hlt!"); }
			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("lockinfo")) 
		{
			Entity entity = null;
			
			try { entity = animSel.getPlayerSelectedEntity(cs.getName()); } 
			catch (Exception e) { cs.sendMessage(ChatColor.RED + "Es wurde kein Tier ausgew�hlt."); }
			
			if (entity != null) 
			{
				if (entity.getType() == EntityType.COW 
						|| entity.getType() == EntityType.PIG
						|| entity.getType() == EntityType.SHEEP
						|| entity.getType() == EntityType.CHICKEN
						|| entity.getType() == EntityType.HORSE
						|| entity.getType() == EntityType.WOLF) {
					String isAlreadyLocked = getEntityOwner(entity.getUniqueId()); {
						String isLocked = getEntityOwner(entity.getUniqueId());
						if (isLocked == null) { cs.sendMessage(ChatColor.YELLOW + "Dieses Tier ist nicht gesichert."); } 
						else { cs.sendMessage(ChatColor.YELLOW + "Dieses Tier ist von "+ isLocked + " gesichert."); }
					}
				}
			}
			else { cs.sendMessage(ChatColor.RED + "Es wurde kein Tier ausgew�hlt!"); }
			return true;
		}
		return false;
	}

	/* TODO Funktion entfernen und daf�r EntityList nutzen */
	public static String getEntityOwner(UUID uuid) {
		ResultSet result = null;
		try {
			result = sql
					.get("SELECT name FROM ap_owners o INNER JOIN ap_locks l ON l.owner_id = o.id WHERE entity_id IN (SELECT id FROM ap_entities WHERE uuid = '"
							+ uuid + "')");
		} catch (Exception e1) {
		}

		if (result != null) {
			try {
				if (!result.next()) {
					System.out.println("Keine Exception .................");
					return null;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

			try {
				if (result.getString("name") != null) { 
					String ownerName = result.getString("name");
					result.close();
					return ownerName;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				result.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/* TODO Funktion entfernen und daf�r EntityList nutzen */
	public void addEntity(UUID uuid, String Owner, int x, int y, int z, String Type, String Nametag) {
		ResultSet canFindEntity = null;
		try {
			canFindEntity = sql.get("SELECT id FROM ap_entities WHERE uuid = '" + uuid + "';");
		} catch (Exception e1) { }

		ResultSet canFindPlayer = null;
		try {
			canFindPlayer = sql.get("SELECT id FROM ap_owners WHERE name = '" + Owner + "';");
		} catch (Exception e2) { }

		try {
			if (!canFindEntity.next()) {
				sql.write("INSERT INTO ap_entities (`uuid`, `last_x`, `last_y`, `last_z`, `animaltype`, `nametag`) "
						+ "VALUES ('" + uuid + "', " + x + ", " + y + ", " + z + ", '" + Type + "', '" + Nametag + "');");
				canFindEntity = sql.get("SELECT id FROM ap_entities WHERE uuid = '" + uuid + "' LIMIT 1;");
				if (!canFindEntity.next())
					throw new SQLException("Inserting new entity failed.");
			}
			if (!canFindPlayer.next()) {
				sql.write("INSERT INTO ap_owners (`name`) VALUES ('" + Owner + "');");
				canFindPlayer = sql.get("SELECT id FROM ap_owners WHERE name = '" + Owner + "';");
				if (!canFindPlayer.next())
					throw new SQLException("Inserting new entity-owner failed.");
			}

			sql.write("INSERT INTO ap_locks (`owner_id`, `entity_id`) "
					+ "VALUES (" + canFindPlayer.getInt("id") + ", " + canFindEntity.getInt("id") + ");");
			
			server.getLogger().info( "Inserting new entity-owner-lock at " + uuid + " for " + Owner + ".");
			canFindEntity.close();
			canFindPlayer.close();
		} catch (SQLException e) {
			server.getLogger().info(e.getMessage());
		}
	}
}
