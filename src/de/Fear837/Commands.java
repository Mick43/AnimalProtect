package de.Fear837;

import java.util.ArrayList;
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
				catch (Exception e) { cs.sendMessage(ChatColor.RED + "Es wurde kein Tier ausgewählt."); }
				
				if (selectedEntity != null) {
					if (!selectedEntity.isDead()) {
						LivingEntity entity = (LivingEntity) selectedEntity;
						if (isAnimal(entity)) {
							if (!list.contains(entity)) {
								list.lock(player, (Entity) entity);
								if (list.lastActionSucceeded()) { cs.sendMessage("§aDas Tier wurde erfolgreich gesichert!"); }
								else { 
									cs.sendMessage("§cFehler: Das Tier konnte nicht gesichert werden.");
									plugin.getLogger().warning("Warnung: Entity konnte nicht gelockt werden!");
								}
							}
							else { cs.sendMessage("§c§cFehler: Das Tier ist bereits protected!"); }
						}
						else { cs.sendMessage("§c§cFehler: Das ausgewählte Entity ist kein Tier!"); }
					}
					else { cs.sendMessage("§c§cFehler: Das gewählte Tier ist tot."); }
				}
				else { cs.sendMessage("§c§cFehler: Es wurde kein Tier ausgewählt."); }
			}
			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("lockinfo")) 
		{
			if (cs instanceof Player) {
				Player player = (Player)cs;
				Entity selectedEntity = EntityListener.getSelected(player);
				try { selectedEntity = EntityListener.getSelected(player); } 
				catch (Exception e) { cs.sendMessage(ChatColor.RED + "Es wurde kein Tier ausgewählt."); }
				
				if (selectedEntity != null) {
					if (list.contains(selectedEntity)) {
						Player owner = null;
						try { owner = list.get(selectedEntity); } 
						catch (Exception e) { }
						
						if (owner != null) { cs.sendMessage("§eDieses Tier ist von §6" + owner.getName() + " §egesichert."); }
						else { 
							cs.sendMessage("§eDieses Tier ist von einer unbekannten Person §egesichert."); 
							plugin.getLogger().warning("Warnung: Ein Tier hat einen unbekannten Owner! (/lockinfo)");
							plugin.getLogger().warning("OWNER == NULL >>> Entity-UUID: [" + selectedEntity.getUniqueId() + "]");
						}
					}
					else { cs.sendMessage("§eDieses Tier ist nicht protected."); }
				}
				else { cs.sendMessage("§cFehler: Es wurde kein Tier ausgewählt."); }
			}
			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("locklist")) {
			if (cs instanceof Player) {
				Player player = (Player)cs;
				String targetPlayer = "";
				if (args.length == 0) { targetPlayer = player.getName(); }
				else if (args.length == 1) { targetPlayer = args[0]; }
				
				ArrayList<UUID> entityList = new ArrayList<UUID>();
				try { entityList = list.get((Player) plugin.getServer().getOfflinePlayer(targetPlayer)); } 
				catch (Exception e) 
				{ cs.sendMessage("§cFehler: Der Spieler wurde nicht gefunden."); e.printStackTrace(); }
				
				if (entityList != null) {
					plugin.getLogger().info("Number of entities in list: " + entityList.size());
					cs.sendMessage("§e------ Liste der gesicherten Tiere von " + targetPlayer + " ------");
					int index = 1;
					for (UUID id : entityList) {
						ArrayList<Object> info = list.get(id);
						if (info != null) {
							if ((Boolean) info.get(6) == true) {
								player.sendMessage("§e[" + index + "] - " 
										+ info.get(3)  + " "
										+ "[§6" + info.get(0) + "§e, "
										+ "§6" + info.get(1) + "§e, "
										+ "§6" + info.get(2) + "§e] "
										+ "['" + info.get(4) + "'] - "
										+ "§a[ALIVE]"
										+ "");
							}
							else {
								player.sendMessage("§e[" + index + "] - " 
										+ info.get(3) + " "
										+ "[§6" + info.get(0) + "§e, "
										+ "§6" + info.get(1) + "§e, "
										+ "§6" + info.get(2) + "§e] "
										+ "['" + info.get(4) + "'] - "
										+ "§c[DEAD]"
										+ "");
							}
						}
						else {
							player.sendMessage("§e[" + index + "] - " 
									+ "NULL"  + " "
									+ "[§6" + "NULL" + "§e, "
									+ "§6" + "NULL" + "§e, "
									+ "§6" + "NULL" + "§e] "
									+ "['" + "NULL" + "'] - "
									+ "§4[UNKNOWN]"
									+ "");
							plugin.getLogger().info("NullPointer at Commands.onCommand.info = list.get(id)");
						}
						index += 1;
					}
					cs.sendMessage("§e-------------------------------");
				}
				else { cs.sendMessage("§cFehler: Die Liste konnte nicht geladen werden."); }
			}
		}
		return false;
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
