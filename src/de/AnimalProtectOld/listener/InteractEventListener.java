package de.AnimalProtectOld.listener;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import de.AnimalProtectOld.Main;
import de.AnimalProtectOld.MySQL;
import de.AnimalProtectOld.structs.EntityList;

public class InteractEventListener implements Listener {
	
	private Main plugin;
	private MySQL database;
	private EntityList list;
	
	private static HashMap<Player, Entity> selectedList;
	
	public InteractEventListener(Main plugin) {
		this.plugin = plugin;
		this.database = plugin.database;
		this.list = plugin.list;
		
		InteractEventListener.selectedList = new HashMap<Player, Entity>();
	}
	
	@EventHandler
	public void onEntityEvent(PlayerInteractEntityEvent event) {
		/* Datenbank-Verbindung aufbauen, falls nicht vorhanden. */
		if (!database.checkConnection()) { database.openConnection(); }
		
		if (plugin.isEnabled() && database.checkConnection() && !event.isCancelled()) {
			/* Wenn der Spieler nicht geduckt ist, dann wird returned. */
			if (!event.getPlayer().isSneaking()) { return; }
			/* Wenn das Entity kein Tier ist, dann wird returned. */
			if (!isAnimal(event.getRightClicked())) { return; }
			
			/* Die Variablen werden initialisiert. */
			Player player = event.getPlayer();
			Entity entity = event.getRightClicked();
			String owner = null;
			
			/* Pr�fen ob das ausgew�hlte Tier bereits vom Spieler ausgew�hlt ist. */
			if (selectedList.get(player) == entity) {
				player.sendMessage("�eDu hast das Tier bereits ausgew�hlt!");
				player.playSound(player.getLocation(), Sound.CLICK, 0.4f, 0.8f);
				return;
			}
			
			/* Den Owner des Entities festlegen, null falls Entity nicht locked. */
			owner = list.getPlayer(entity.getUniqueId());
			
			/* Wenn das Tier nicht gelockt ist, dann wird der Owner nicht erw�hnt. */
			if (owner == null) {
				switch (entity.getType()) {
				case COW:
					player.sendMessage("�eDu hast eine �6Kuh �eausgew�hlt. �7(/lockanimal um sie zu protecten.);");
					break;
				case PIG:
					player.sendMessage("�eDu hast ein �6Schwein �eausgew�hlt. �7(/lockanimal um es zu protecten.);");
					break;
				case CHICKEN:
					player.sendMessage("�eDu hast ein �6Huhn �eausgew�hlt. �7(/lockanimal um es zu protecten.);");
					break;
				case HORSE:
					player.sendMessage("�eDu hast ein �6Pferd �eausgew�hlt. �7(/lockanimal um es zu protecten.);");
					break;
				case WOLF:
					player.sendMessage("�eDu hast einen �6Wolf �eausgew�hlt. �7(/lockanimal um ihn zu protecten.);");
					break;
				case SHEEP:
					player.sendMessage("�eDu hast ein �6Schaf �eausgew�hlt. �7(/lockanimal um es zu protecten.);");
					break;
				default:
					player.sendMessage("�eDu hast ein �6unbekanntes �eTier ausgew�hlt. �7(/lockanimal um es zu protecten.);");
					break;
				}
			}
			/* Wenn das Tier protected ist, dann wird der Owner erw�hnt. */
			else {
				player.sendMessage("�eDu hast das Tier von �6"+owner+" �eausgew�hlt.");
				if (entity.getType() == EntityType.HORSE) { list.updateEntity(entity, false); }
				else { list.updateEntity(entity, true); }
			}
			
			/* Zum Schluss wird bei dem Spieler noch ein Sound abgespielt und sein zuletzt ausgew�hltes Tier wird gespeichert. */
			player.playSound(player.getLocation(), Sound.CLICK, 0.75f, 0.8f);
			addSelected(player, entity);
		}
	}
	
	private boolean isAnimal(Entity entity) {
		EntityType type = entity.getType();
		if (type == EntityType.SHEEP
		||  type == EntityType.PIG
		||  type == EntityType.COW
		||  type == EntityType.CHICKEN
		||  type == EntityType.HORSE
		||  type == EntityType.WOLF
		||  type == EntityType.IRON_GOLEM
		||  type == EntityType.SNOWMAN
		||  type == EntityType.VILLAGER
		||  type == EntityType.OCELOT)
		{ return true; }
		return false;
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
	
	public static Entity getSelected(UUID uuid) {
		if (selectedList.containsKey(uuid)) {
			return selectedList.get(uuid);
		}
		else { return null; }
	}
}
