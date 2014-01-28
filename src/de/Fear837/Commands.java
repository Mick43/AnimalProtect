package de.Fear837;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import de.Fear837.listener.EntityListener;
import de.Fear837.structs.EntityList;

public class Commands implements CommandExecutor {

	private MySQL sql;
	private Main plugin;
	private EntityList list;

	public Commands(Main plugin, MySQL sql, EntityList list) {
		this.plugin = plugin;
		this.sql = sql;
		this.list = list;
	}
	
	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("lockanimal")) {
			if (cs instanceof Player) {
				Player player = (Player)cs;
				Entity selectedEntity = null;
				try { selectedEntity = EntityListener.getSelected(player); } 
				catch (Exception e) { cs.sendMessage(ChatColor.RED + "Es wurde kein Tier ausgew�hlt."); }
				
				if (selectedEntity != null) {
					if (!selectedEntity.isDead()) {
						LivingEntity entity = (LivingEntity) selectedEntity;
						if (isAnimal(entity)) {
							if (!list.contains(entity)) {
								list.lock(player, (Entity) entity);
								if (list.lastActionSucceeded()) { cs.sendMessage("�aDas Tier wurde erfolgreich gesichert!"); }
								else { 
									cs.sendMessage("�cFehler: Das Tier konnte nicht gesichert werden.");
									plugin.getLogger().warning("Warnung: Entity konnte nicht gelockt werden!");
								}
							}
							else { cs.sendMessage("�c�cFehler: Das Tier ist bereits protected!"); }
						}
						else { cs.sendMessage("�c�cFehler: Das ausgew�hlte Entity ist kein Tier!"); }
					}
					else { cs.sendMessage("�c�cFehler: Das gew�hlte Tier ist tot."); }
				}
				else { cs.sendMessage("�c�cFehler: Es wurde kein Tier ausgew�hlt."); }
			}
			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("lockinfo")) 
		{
			if (cs instanceof Player) {
				Player player = (Player)cs;
				Entity selectedEntity = EntityListener.getSelected(player);
				try { selectedEntity = EntityListener.getSelected(player); } 
				catch (Exception e) { cs.sendMessage(ChatColor.RED + "Es wurde kein Tier ausgew�hlt."); }
				
				if (selectedEntity != null) {
					if (list.contains(selectedEntity)) {
						Player owner = null;
						try { owner = list.get(selectedEntity); } 
						catch (Exception e) { }
						
						if (owner != null) { cs.sendMessage("�eDieses Tier ist von �6" + owner.getName() + " �egesichert."); }
						else { 
							cs.sendMessage("�eDieses Tier ist von einer unbekannten Person �egesichert."); 
							plugin.getLogger().warning("Warnung: Ein Tier hat einen unbekannten Owner! (/lockinfo)");
							plugin.getLogger().warning("OWNER == NULL >>> Entity-UUID: [" + selectedEntity.getUniqueId() + "]");
						}
					}
					else { cs.sendMessage("�eDieses Tier ist nicht protected."); }
				}
				else { cs.sendMessage("�cFehler: Es wurde kein Tier ausgew�hlt."); }
			}
			return true;
		}
		return false;
	}

	/* TODO Funktion entfernen und daf�r EntityList nutzen */
	public String getEntityOwner(UUID uuid) {
		ResultSet result = null;
		try {
			result = sql.get("SELECT name FROM ap_owners o INNER JOIN ap_locks l ON l.owner_id = o.id WHERE entity_id IN (SELECT id FROM ap_entities WHERE uuid = '"
							+ uuid + "')", true);
		} catch (Exception e1) {
		}

		if (result != null) {
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
			canFindEntity = sql.get("SELECT id FROM ap_entities WHERE uuid = '" + uuid + "';", true);
		} catch (Exception e1) { }

		ResultSet canFindPlayer = null;
		try {
			canFindPlayer = sql.get("SELECT id FROM ap_owners WHERE name = '" + Owner + "';", true);
		} catch (Exception e2) { }

		try {
			if (canFindEntity == null) {
				sql.write("INSERT INTO ap_entities (`uuid`, `last_x`, `last_y`, `last_z`, `animaltype`, `nametag`) "
						+ "VALUES ('" + uuid + "', " + x + ", " + y + ", " + z + ", '" + Type + "', '" + Nametag + "');");
				canFindEntity = sql.get("SELECT id FROM ap_entities WHERE uuid = '" + uuid + "' LIMIT 1;", true);
				if (!canFindEntity.next())
					throw new SQLException("Inserting new entity failed.");
			}
			if (canFindPlayer == null) {
				sql.write("INSERT INTO ap_owners (`name`) VALUES ('" + Owner + "');");
				canFindPlayer = sql.get("SELECT id FROM ap_owners WHERE name = '" + Owner + "';", true);
				if (!canFindPlayer.next())
					throw new SQLException("Inserting new entity-owner failed.");
			}

			sql.write("INSERT INTO ap_locks (`owner_id`, `entity_id`) "
					+ "VALUES (" + canFindPlayer.getInt("id") + ", " + canFindEntity.getInt("id") + ");");
			
			plugin.getLogger().info( "Inserting new entity-owner-lock at " + uuid + " for " + Owner + ".");
			canFindEntity.close();
			canFindPlayer.close();
		} catch (SQLException e) {
			plugin.getLogger().info(e.getMessage());
		}
	}
	
	public boolean isAnimal(Entity entity) {
		if (entity ==  null) { return false; }
		else { 
			if (entity.getType() == EntityType.COW 
					|| entity.getType() == EntityType.PIG
					|| entity.getType() == EntityType.SHEEP
					|| entity.getType() == EntityType.CHICKEN
					|| entity.getType() == EntityType.HORSE
					|| entity.getType() == EntityType.WOLF) {
				return true;
			}
		}
		return false;
	}
}
