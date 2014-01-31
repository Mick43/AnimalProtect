package de.Fear837.listener;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import de.Fear837.MySQL;
import de.Fear837.structs.EntityList;

public class EntityInteractListener implements Listener{
	
	private EntityList list;
	private MySQL database;
	
	private static HashMap<Player, Entity> selectedList;
	
	public EntityInteractListener(EntityList list, MySQL database) {
		this.list = list;
		this.database = database;
	}

	@EventHandler
	public void onEntityInteract(PlayerInteractEntityEvent event) {
		if (event.getPlayer().getInventory().getItemInHand().getType() == Material.NAME_TAG) {
			if (isAnimal(event.getRightClicked())) {
				if (list.containsEntity(event.getRightClicked())) {
					if (event.getPlayer().getInventory().getItemInHand().getItemMeta().getDisplayName() != null) {
						String nametag = event.getPlayer().getInventory().getItemInHand().getItemMeta().getDisplayName();
						database.write("UPDATE ap_entities SET nametag='" + nametag + "' WHERE uuid='" + event.getRightClicked().getUniqueId().toString() + "';");
					}
				}
			}
		}
		else if (event.getPlayer().isSneaking()) {
			Entity entity = event.getRightClicked();
			if (isAnimal(entity)) {
				Player player = event.getPlayer();
				if (selectedList.get(player) == entity) {
					player.sendMessage(ChatColor.YELLOW + "Du hast das Tier bereits ausgewählt."); 
					player.playSound(player.getLocation(), Sound.CLICK, 0.4f, 0.8f);
					return;
				}
				String entityOwner = null;
				try { entityOwner = list.getPlayer(entity); }
				catch (Exception e) { }
				
				player.playSound(player.getLocation(), Sound.CLICK, 0.75f, 0.8f);
				addSelected(player, event.getRightClicked());
				
				if (entityOwner == null) {
					switch (entity.getType()) {
					case COW: player.sendMessage(ChatColor.YELLOW + "Du hast eine §6Kuh§e ausgewählt. §7(/lockanimal um sie zu protecten.)"); 
						break;
					case PIG: player.sendMessage(ChatColor.YELLOW + "Du hast ein §6Schwein§e ausgewählt. §7(/lockanimal um es zu protecten.)");
						break;
					case SHEEP: player.sendMessage(ChatColor.YELLOW + "Du hast ein §6Schaf§e ausgewählt. §7(/lockanimal um es zu protecten.)");
						break;
					case CHICKEN: player.sendMessage(ChatColor.YELLOW + "Du hast ein §6Huhn§e ausgewählt. §7(/lockanimal um es zu protecten.)");
						break;
					case HORSE: player.sendMessage(ChatColor.YELLOW + "Du hast ein §6Pferd§e ausgewählt. §7(/lockanimal um es zu protecten.)");
						break;
					case WOLF: player.sendMessage(ChatColor.YELLOW + "Du hast einen §6Wolf§e ausgewählt. §7(/lockanimal um ihn zu protecten.)");
						break;
					default: player.sendMessage(ChatColor.YELLOW + "Du hast ein §6unbekanntes§e Tier ausgewählt. §7(/lockanimal um es zu protecten.)");
						break;
					}
				}
				else { 
					player.sendMessage(ChatColor.YELLOW + "Du hast das Tier von §6" + entityOwner + "§e ausgewählt."); 
					// TODO list.updateEntity(entity);
				}
			}
		}
	}
	
	private void addSelected(Player player, Entity entity) {
		if (!selectedList.containsKey(player)) {
			selectedList.put(player, entity);
		}
		else {
			selectedList.remove(player);
			selectedList.put(player, entity);
		}
	}
	
	public static Entity getSelected(Player player) {
		if (selectedList.containsKey(player)) {
			return selectedList.get(player);
		}
		else { return null; }
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
