package de.Fear837;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

import com.volcanicplaza.BukkitDev.AnimalSelector.AnimalSelector;

public class Commands implements CommandExecutor {

	private MySQL sql;

	private AnimalSelector animSel;

	private Server server; // TODO use this!

	public Commands(Server server, MySQL sql) {
		if (animSel == null) {
			animSel = getAnimalSelector();
		}
		this.server = server;
		this.sql = sql;
	}

	public static AnimalSelector getAnimalSelector() {
		// Get AnimalSelector plugin
		AnimalSelector plugin = (AnimalSelector) Bukkit.getServer()
				.getPluginManager().getPlugin("AnimalSelector");

		if (plugin == null || !(plugin instanceof AnimalSelector)) {
			Bukkit.getLogger().info(
					"[WARNING] AnimalSelector isn't loaded yet.");
			return null;
		}
		return (AnimalSelector) plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label,
			String[] args) {
		if (animSel == null) {
			animSel = getAnimalSelector();
		}

		if (cmd.getName().equalsIgnoreCase("lockanimal")) {
			Entity entity = null;
			try {
				entity = animSel.getPlayerSelectedEntity(cs.getName());
			} catch (Exception e) {
				cs.sendMessage("Es wurde kein Tier ausgewählt.");
			}

			if (entity != null) {
				String isAlreadyLocked = getEntityOwner(entity.getUniqueId());
				if (isAlreadyLocked == null) {
					addEntity(entity.getUniqueId(), cs.getName(), entity
							.getLocation().getBlockX(), entity.getLocation()
							.getBlockY(), entity.getLocation().getBlockZ());
					cs.sendMessage("Das Tier wurde gesichert!");
				} else {
					cs.sendMessage("Das Tier ist bereits von "
							+ isAlreadyLocked + "gesichert.");
				}
			}
			return true;
		}
		return false;
	}

	// res =
	// statement.executeQuery("SELECT * FROM animalprotect WHERE entityid = '" +
	// uuid + "';");
	public String getEntityOwner(UUID uuid) {
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
				if (result.getString("name") != null) { // java.sql.SQLException:
														// Illegal operation on
														// empty result set.
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

	// statement.executeUpdate("INSERT INTO animalprotect (`entityid`, `owner`, `last_x`, `last_y`, `last_z`) VALUES ('"
	// + entityid + "', '" + Owner + "', " + x + ", " + y + ", " + z + ");");
	public void addEntity(UUID uuid, String Owner, int x, int y, int z) {
		ResultSet canFindEntity = null;
		try {
			canFindEntity = sql.get("SELECT * FROM ap_entities WHERE uuid = '"
					+ uuid + "';");
		} catch (Exception e1) {
		}
		ResultSet canFindPlayer = null;
		try {
			canFindPlayer = sql.get("SELECT * FROM ap_owners WHERE name = '"
					+ Owner + "';");
		} catch (Exception e2) {
		}

		if (canFindEntity == null) {
			sql.write("INSERT INTO ap_entities (`uuid`, `last_x`, `last_y`, `last_z`) VALUES ("
					+ uuid + ", " + y + ", " + z + ");");
			canFindEntity = sql.get("SELECT * FROM ap_entities WHERE uuid = '"
					+ uuid + "' LIMIT 1;");
		}
		if (canFindPlayer == null) {
			sql.write("INSERT INTO ap_owners (`name`) VALUES (" + Owner + ");");
			canFindPlayer = sql.get("SELECT * FROM ap_owners WHERE name = '"
					+ Owner + "';");
		}

		try {
			canFindEntity.next();
			canFindPlayer.next();

			sql.write("INSERT INTO ap_locks (`owner_id`, `entity_id`) VALUES ("
					+ canFindPlayer.getInt("id") + ", "
					+ canFindEntity.getInt("id") + ");");
			canFindEntity.close();
			canFindPlayer.close();
		} catch (SQLException e) {
		}

		// sql.write("INSERT INTO animalprotect (`entityid`, `owner`, `last_x`, `last_y`, `last_z`) VALUES ('"
		// + entityid + "', '" + Owner + "', " + x + ", " + y + ", " + z + ");")
	}

}
