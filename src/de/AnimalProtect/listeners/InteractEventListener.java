package de.AnimalProtect.listeners;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import craftoplugin.core.database.CraftoPlayer;
import de.AnimalProtect.AnimalProtect;
import de.AnimalProtect.Database;
import de.AnimalProtect.Messenger;
import de.AnimalProtect.structs.Animal;

public class InteractEventListener implements Listener {
	
	private AnimalProtect plugin;
	private Database database;
	private static HashMap<UUID, Entity> selectedList;
	
	public InteractEventListener(AnimalProtect plugin) {
		this.plugin = plugin;
		this.database = AnimalProtect.getDatenbank();
	}
	
	@EventHandler
	public void onEntityEvent(PlayerInteractEntityEvent event) {
		if (!plugin.isEnabled() || event.isCancelled()) { return; }
		
		/* Datenbank-Verbindung aufbauen, falls nicht vorhanden. */
		if (!database.isConnected()) { database.connect(); }
		
		if (plugin.isEnabled() && database.isConnected() && !event.isCancelled()) {
			/* Wenn der Spieler nicht geduckt ist, dann wird returned. */
			if (!event.getPlayer().isSneaking()) { return; }
			/* Wenn das Entity kein Tier ist, dann wird returned. */
			if (!AnimalProtect.isAnimal(event.getRightClicked())) { return; }
			
			/* Die Variablen werden initialisiert. */
			Player player = event.getPlayer();
			Entity entity = event.getRightClicked();
			CraftoPlayer owner = null;
			
			/* Pr�fen ob das ausgew�hlte Tier bereits vom Spieler ausgew�hlt ist. */
			if (selectedList.get(player) == entity) {
				Messenger.sendMessage(player, "Du hast das Tier bereits ausgew�hlt!");
				player.playSound(player.getLocation(), Sound.CLICK, 0.4f, 0.8f);
				return;
			}
			
			/* Den Owner des Entities festlegen, null falls Entity nicht locked. */
			owner = database.getOwner(entity.getUniqueId());
			
			/* Wenn das Tier nicht gelockt ist, dann wird der Owner nicht erw�hnt. */
			if (owner == null) {
				switch (entity.getType()) {
				case COW:
					Messenger.sendMessage(player, "Du hast eine �6Kuh �eausgew�hlt. �7(/lockanimal um sie zu protecten.);");
					break;
				case PIG:
					Messenger.sendMessage(player, "Du hast ein �6Schwein �eausgew�hlt. �7(/lockanimal um es zu protecten.);");
					break;
				case CHICKEN:
					Messenger.sendMessage(player, "Du hast ein �6Huhn �eausgew�hlt. �7(/lockanimal um es zu protecten.);");
					break;
				case HORSE:
					Messenger.sendMessage(player, "Du hast ein �6Pferd �eausgew�hlt. �7(/lockanimal um es zu protecten.);");
					break;
				case WOLF:
					Messenger.sendMessage(player, "Du hast einen �6Wolf �eausgew�hlt. �7(/lockanimal um ihn zu protecten.);");
					break;
				case SHEEP:
					Messenger.sendMessage(player, "Du hast ein �6Schaf �eausgew�hlt. �7(/lockanimal um es zu protecten.);");
					break;
				default:
					Messenger.sendMessage(player, "Du hast ein �6unbekanntes �eTier ausgew�hlt. �7(/lockanimal um es zu protecten.);");
					break;
				}
			}
			/* Wenn das Tier protected ist, dann wird der Owner erw�hnt. */
			else { 
				Messenger.sendMessage(player, "Du hast das Tier von �6"+owner+" �eausgew�hlt.");
				Animal animal = database.getAnimal(entity.getUniqueId());
				if (animal == null) { return; }
				animal.updateAnimal(entity);
				animal.saveToDatabase(true);
			}
			
			/* Zum Schluss wird bei dem Spieler noch ein Sound abgespielt und sein zuletzt ausgew�hltes Tier wird gespeichert. */
			player.playSound(player.getLocation(), Sound.CLICK, 0.75f, 0.8f);
			addSelected(player.getUniqueId(), entity);
		}
	}
	
	private void addSelected(UUID uuid, Entity entity) {
		if (!selectedList.containsKey(uuid)) {
			selectedList.put(uuid, entity);
		}
		else {
			selectedList.remove(uuid);
			selectedList.put(uuid, entity);
		}
	}
	
	public static Entity getSelected(UUID uuid) {
		if (selectedList.containsKey(uuid)) {
			return selectedList.get(uuid);
		}
		else { return null; }
	}
}
