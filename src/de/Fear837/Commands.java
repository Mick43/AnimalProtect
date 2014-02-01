package de.Fear837;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.ChatColor;
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
import org.bukkit.entity.Horse.Style;
import org.bukkit.entity.Horse.Variant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.ItemStack;

import de.Fear837.listener.EntityInteractListener;
import de.Fear837.structs.EntityList;
import de.Fear837.structs.EntityObject;
import de.Fear837.utility.APLogger;

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
				try { selectedEntity = EntityInteractListener.getSelected(player); } 
				catch (Exception e) { cs.sendMessage(ChatColor.RED + "Es wurde kein Tier ausgewählt."); }
				
				if (selectedEntity != null) {
					if (!selectedEntity.isDead()) {
						LivingEntity entity = (LivingEntity) selectedEntity;
						if (isAnimal(entity)) {
							if (!list.containsEntity(entity)) {
								list.lock(player.getName(), (Entity) entity);
								if (list.lastActionSucceeded()) { cs.sendMessage("§aDas Tier wurde erfolgreich gesichert!"); }
								else { 
									cs.sendMessage("§cFehler: Das Tier konnte nicht gesichert werden.");
									APLogger.setWarning(true);
									APLogger.warn("Warnung: Ein Entity konnte nicht gelockt werden!");
									APLogger.setWarning(false);
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
				Entity selectedEntity = EntityInteractListener.getSelected(player);
				try { selectedEntity = EntityInteractListener.getSelected(player); } 
				catch (Exception e) { cs.sendMessage(ChatColor.RED + "Es wurde kein Tier ausgewählt."); }
				
				if (selectedEntity != null) {
					if (list.containsEntity(selectedEntity)) {
						String owner = null;
						try { owner = list.getPlayer(selectedEntity); } 
						catch (Exception e) { }
						
						if (owner != null) { cs.sendMessage("§eDieses Tier ist von §6" + owner + " §egesichert."); }
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
				String playerName = cs.getName();
				Player player = (Player)cs;
				
				if (args.length == 1) { 
					playerName = args[0];
				}
				else if (args.length > 1) {
					cs.sendMessage("§cFehler: Du hast zu viele Argumente angegeben! (/locklist <Name>)");
					return true;
				}
				
				ArrayList<EntityObject> entities = new ArrayList<EntityObject>();
				entities = list.getEntities(cs.getName());
				
				if (entities != null) {
					if (entities.size() != 0) {
						String msg = "§e----- Liste der gesicherten Tiere von " + playerName + "-----";
						cs.sendMessage(msg);
						int index = 1;
						for (EntityObject e : entities) {
							if (e.isConnected()) {
								String alive = "";
								if (e.isAlive()) { alive = "§a[ALIVE]"; }
								else { alive = "§c[DEAD]"; }
								
								int x = e.getLastx();
								int y = e.getLasty();
								int z = e.getLastz();
								
								boolean found = false;
								
								for (Entity entity : player.getWorld().getEntities()) {
									if (UUID.fromString(e.getUniqueID()).equals(entity.getUniqueId())) {
										x = entity.getLocation().getBlockX();
										y = entity.getLocation().getBlockY();
										z = entity.getLocation().getBlockZ();
										if (entity.isDead()) { alive = "§c[DEAD]"; }
										else { alive = "§a[ALIVE]"; }
										found = true;
									}
								}
								if (!found && e.isAlive()) { alive = "§7[MISSING]"; }
								
								cs.sendMessage("§e("+index+") - "
										+ e.getType().toUpperCase() + " "
										+ "[§6"+x+"§e, "
										+ "§6"+y+"§e, "
										+ "§6"+z+"§e] "
										+ "['§6"+e.getNametag()+"§e'] "
										+ alive
										+ "");
							}
							index += 1;
						}
						String msg2 = "§n§e";
						for (int i = 0; i < msg.length(); i++) {
							msg2 += "-";
						}
						cs.sendMessage(msg2);
					}
					else {
						if (args.length == 0) { cs.sendMessage("§cFehler: Du hast noch keine Tiere gesichert!"); }
						else { cs.sendMessage("§cFehler: Der Spieler "+playerName+" hat noch keine Tiere gesichert!"); }
					}
				}
				else {
					if (args.length == 0) { cs.sendMessage("§cFehler: Es wurden keine Tiere von dir gefunden!"); }
					else { cs.sendMessage("§cFehler: Es wurden keine Tiere von "+playerName+" gefunden!"); }
				}
			}
			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("lockrespawn")) {
			if (cs instanceof Player) {
				Player player = (Player) cs;
				String playerName = "";
				
				if (args.length == 0) { player.sendMessage("§cFehler: Es fehlen Argumente! /lockrespawn <id> <owner>"); }
				else if (args.length == 1) { playerName = player.getName(); }
				else if (args.length == 2) { playerName = args[1]; }
				else { cs.sendMessage("§cFehler: Zu viele Argumente! /lockrespawn <id> <owner>"); }
			    
			    try { Integer.parseInt(args[0]); } catch (Exception e) { cs.sendMessage("§cFehler: Die angegebene ID ist keine Zahl!"); return false; }
			    ResultSet result = sql.get("SELECT * FROM ap_entities WHERE ID=("
			    		+ "SELECT entity_id FROM ap_locks WHERE owner_id=("
			    		+ "SELECT id FROM ap_owners WHERE name='" + playerName + "') LIMIT " + (Integer.parseInt(args[0])-1) + ", 1);", true, true);
				
				if (result != null) {
					Entity entity = null;
					
					try { plugin.getLogger().info("Spawning Entity: " + EntityType.valueOf(result.getString("animaltype").toUpperCase())); } catch (Exception e) { }
					try { entity = player.getWorld().spawnEntity(player.getLocation(), EntityType.valueOf(result.getString("animaltype").toUpperCase())); } 
					
					catch (SQLException e) { e.printStackTrace(); }
					
					if (entity != null) {
						LivingEntity le = (LivingEntity) entity;
						try { le.setCustomName(result.getString("nametag")); } catch (Exception e) { e.printStackTrace(); }
						
						if (entity.getType() == EntityType.HORSE) {
							Horse horse = (Horse)entity;
							try { horse.setColor(Color.valueOf(result.getString("color").toUpperCase())); } catch (Exception e) { e.printStackTrace();}
							try { horse.setMaxHealth(result.getDouble("maxhp")); } catch (Exception e) { e.printStackTrace(); }
							try { horse.setJumpStrength(result.getDouble("horse_jumpstrength")); } catch (Exception e) { e.printStackTrace(); }
							try { horse.setStyle(Style.valueOf(result.getString("horse_style").toUpperCase())); } catch (Exception e) { e.printStackTrace(); }
							try { horse.setVariant(Variant.valueOf(result.getString("horse_variant").toUpperCase())); } catch (Exception e) { e.printStackTrace(); }
							try { horse.setOwner(plugin.getServer().getOfflinePlayer(result.getString("name"))); } catch (Exception e) { e.printStackTrace(); }
							String armor = null;
							try { armor = result.getString("armor"); } catch (Exception e) { }
							if (armor != null) {
								if (armor.equalsIgnoreCase("iron")) { horse.getInventory().setArmor(new ItemStack(Material.IRON_BARDING)); }
								else if (armor.equalsIgnoreCase("gold")) { horse.getInventory().setArmor(new ItemStack(Material.GOLD_BARDING)); }
								else if (armor.equalsIgnoreCase("diamond")) { horse.getInventory().setArmor(new ItemStack(Material.DIAMOND_BARDING)); }
							}
						}
						else if (entity.getType() == EntityType.SHEEP) {
							Sheep sheep = (Sheep)entity;
							try { sheep.setColor(DyeColor.valueOf(result.getString("color").toUpperCase())); } catch (Exception e) { e.printStackTrace(); }
						}
						else if (entity.getType() == EntityType.WOLF) {
							Wolf wolf = (Wolf)entity;
							try { wolf.setCollarColor(DyeColor.valueOf(result.getString("color").toUpperCase())); } catch (Exception e) { e.printStackTrace(); }
							try { wolf.setOwner(plugin.getServer().getOfflinePlayer(result.getString("name"))); } catch (Exception e) { e.printStackTrace(); }
						}
						
						try {
							int x = entity.getLocation().getBlockX();
							int y = entity.getLocation().getBlockY();
							int z = entity.getLocation().getBlockZ();
							String id = entity.getUniqueId().toString();
							
							sql.write("UPDATE ap_entities SET uuid='"+id+"', last_x="+x+", last_y="+y+", last_z="+z+" "
									+ "WHERE uuid='" + result.getString("uuid")+"'");
							
							EntityObject ent = list.getEntityObject(UUID.fromString(result.getString("uuid")));
							if (ent != null) { ent.update(); }
						} catch (SQLException e) { e.printStackTrace(); }
						
						player.sendMessage("§aDas Tier wurde erfolgreich respawnt!");
					}
					else { player.sendMessage("§cFehler: Das respawnen ist fehlgeschlagen!"); }
				}
				else { player.sendMessage("§cFehler: Es wurde kein Tier gefunden."); }
				
				return true;
			}
		}
		else if (cmd.getName().equalsIgnoreCase("locktp")) {
			if (cs instanceof Player) {
				Player player = (Player)cs;
                String playerName = "";
				
				if (args.length == 0) { player.sendMessage("§cFehler: Es fehlen Argumente! /lockrespawn <id> <owner>"); }
				else if (args.length == 1) { playerName = player.getName(); }
				else if (args.length == 2) { playerName = args[1]; }
				else { cs.sendMessage("§cFehler: Zu viele Argumente! /lockrespawn <id> <owner>"); }
				
				if (playerName != "") {
					try { Integer.parseInt(args[0]); } catch (Exception e) { cs.sendMessage("§cFehler: Die angegebene ID ist keine Zahl!"); return false; }
				    ResultSet result = sql.get("SELECT * FROM ap_entities WHERE ID=("
				    		+ "SELECT entity_id FROM ap_locks WHERE owner_id=("
				    		+ "SELECT id FROM ap_owners WHERE name='" + playerName + "') LIMIT " + (Integer.parseInt(args[0])-1) + ", 1);", true, true);
				    
				    if (result != null) {
				    	try {
							int x = result.getInt("last_x");
							int y = result.getInt("last_y");
							int z = result.getInt("last_z");
							
							for (Entity ent : ((Player) cs).getWorld().getEntities()) {
								if (ent.getUniqueId().toString() == result.getString("uuid")) {
									x = ent.getLocation().getBlockX();
									y = ent.getLocation().getBlockY();
									z = ent.getLocation().getBlockZ();
								}
							}
							
							Location location = new Location(player.getWorld(), x, y, z);
							player.teleport(location);
							cs.sendMessage("§eTeleported.");
						}
				    	catch (SQLException e) { e.printStackTrace(); }
				    }
				}
			}
			return true;
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
