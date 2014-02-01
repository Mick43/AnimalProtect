package de.Fear837;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Sound;
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
				catch (Exception e) { cs.sendMessage(ChatColor.RED + "Es wurde kein Tier ausgew�hlt."); }
				
				if (selectedEntity != null) {
					if (!selectedEntity.isDead()) {
						LivingEntity entity = (LivingEntity) selectedEntity;
						if (isAnimal(entity)) {
							if (!list.containsEntity(entity)) {
								list.lock(player.getName(), (Entity) entity);
								if (list.lastActionSucceeded()) { cs.sendMessage("�aDas Tier wurde erfolgreich gesichert!"); }
								else { 
									cs.sendMessage("�cFehler: Das Tier konnte nicht gesichert werden.");
									APLogger.setWarning(true);
									APLogger.warn("Warnung: Ein Entity konnte nicht gelockt werden!");
									APLogger.setWarning(false);
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
				Entity selectedEntity = EntityInteractListener.getSelected(player);
				try { selectedEntity = EntityInteractListener.getSelected(player); } 
				catch (Exception e) { cs.sendMessage(ChatColor.RED + "Es wurde kein Tier ausgew�hlt."); }
				
				if (selectedEntity != null) {
					if (list.containsEntity(selectedEntity)) {
						String owner = null;
						try { owner = list.getPlayer(selectedEntity); } 
						catch (Exception e) { }
						
						if (owner != null) { cs.sendMessage("�eDieses Tier ist von �6" + owner + " �egesichert."); }
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
		else if (cmd.getName().equalsIgnoreCase("locklist")) {
			if (cs instanceof Player) {
				
				// TODO /locklist neuschreiben
				
					/*plugin.getLogger().info("Number of entities in list: " + entityList.size());
					String msg = "�e�n______Liste der gesicherten Tiere von " + targetPlayer + "______";
					cs.sendMessage(msg);
					cs.sendMessage("");
					int index = 1;
					for (UUID id : entityList) {
						ArrayList<Object> info = list.get(id);
						if (info != null) {
							if ((Boolean) info.get(6) == true) {
								player.sendMessage("�e[" + index + "] - " 
										+ info.get(3)  + " "
										+ "[�6" + info.get(0) + "�e, "
										+ "�6" + info.get(1) + "�e, "
										+ "�6" + info.get(2) + "�e] "
										+ "['" + info.get(4) + "'] - "
										+ "�a[ALIVE]"
										+ "");
							}
							else {
								player.sendMessage("�e[" + index + "] - " 
										+ info.get(3) + " "
										+ "[�6" + info.get(0) + "�e, "
										+ "�6" + info.get(1) + "�e, "
										+ "�6" + info.get(2) + "�e] "
										+ "['" + info.get(4) + "'] - "
										+ "�c[DEAD]"
										+ "");
							}
						}
						else {
							player.sendMessage("�e[" + index + "] - " 
									+ "NULL"  + " "
									+ "[�6" + "NULL" + "�e, "
									+ "�6" + "NULL" + "�e, "
									+ "�6" + "NULL" + "�e] "
									+ "['" + "NULL" + "'] - "
									+ "�4[UNKNOWN]"
									+ "");
							plugin.getLogger().warning("NullPointer at Commands.onCommand.info = list.get(id)");
						}
						index += 1;
					}
					String msg2 = "�n�e";
					for (int i = 0; i < msg.length(); i++) {
						msg2 += "_";
					}
					cs.sendMessage(msg2);
				}
				else { cs.sendMessage("�cFehler: Die Liste konnte nicht geladen werden."); } */
			}
			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("lockrespawn")) {
			if (cs instanceof Player) {
				Player player = (Player) cs;
				String playerName = "";
				
				if (args.length == 0) { player.sendMessage("�cFehler: Es fehlen Argumente! /lockrespawn <id> <owner>"); }
				else if (args.length == 1) { playerName = player.getName(); }
				else if (args.length == 2) { playerName = args[1]; }
				else { cs.sendMessage("�cFehler: Zu viele Argumente! /lockrespawn <id> <owner>"); }
			    
			    try { Integer.parseInt(args[0]); } catch (Exception e) { cs.sendMessage("�cFehler: Die angegebene ID ist keine Zahl!"); return false; }
				// TODO INNER JOIN geht noch nicht richtig
			    // Das Resultat soll der args[0]ste Entity-Eintrag sein der dem 
			    ResultSet result = sql.get("SELECT * FROM ap_entities WHERE ID=("
			    		+ "SELECT entity_id FROM ap_locks WHERE owner_id=("
			    		+ "SELECT id FROM ap_owners WHERE name='" + playerName + "') LIMIT " + args[0] + ", 1);", true, true);
				
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
							sql.write("UPDATE ap_entities SET uuid='" + entity.getUniqueId() + "' WHERE id=" + result.getString("id"));
						} catch (SQLException e) { e.printStackTrace(); }
						
						player.sendMessage("�aDas Tier wurde erfolgreich respawnt!");
					}
					else { player.sendMessage("�cFehler: Das respawnen ist fehlgeschlagen!"); }
				}
				else { player.sendMessage("�cFehler: Es wurde kein Tier gefunden."); }
				
				return true;
			}
		}
		else if (cmd.getName().equalsIgnoreCase("locktp")) {
			// TODO /locktp Befehl schreiben
			cs.sendMessage("Dieser Befehl ist noch nicht fertig :)");
			if (cs instanceof Player) { ((Player) cs).playSound(((Player) cs).getLocation(), Sound.LEVEL_UP, 1f, 0.5f); }
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
