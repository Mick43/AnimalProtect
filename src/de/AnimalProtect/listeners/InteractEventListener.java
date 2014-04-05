package de.AnimalProtect.listeners;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import craftoplugin.core.database.CraftoPlayer;
import de.AnimalProtect.AnimalProtect;
import de.AnimalProtect.Database;
import de.AnimalProtect.structs.Animal;

public class InteractEventListener {
	
	private AnimalProtect plugin;
	private Database database;
	private static HashMap<UUID, Entity> selectedList;
	
	public InteractEventListener(AnimalProtect plugin) {
		this.plugin = plugin;
		this.database = plugin.getDatenbank();
	}
	
	@EventHandler
	public void onEntityEvent(PlayerInteractEntityEvent event) {
		/* Datenbank-Verbindung aufbauen, falls nicht vorhanden. */
		if (!database.isConnected()) { database.connect(); }
		
		if (plugin.isEnabled() && database.isConnected() && !event.isCancelled()) {
			/* Wenn der Spieler nicht geduckt ist, dann wird returned. */
			if (!event.getPlayer().isSneaking()) { return; }
			/* Wenn das Entity kein Tier ist, dann wird returned. */
			if (!plugin.isAnimal(event.getRightClicked())) { return; }
			
			/* Die Variablen werden initialisiert. */
			Player player = event.getPlayer();
			Entity entity = event.getRightClicked();
			CraftoPlayer owner = null;
			
			/* Prüfen ob das ausgewählte Tier bereits vom Spieler ausgewählt ist. */
			if (selectedList.get(player) == entity) {
				player.sendMessage("§eDu hast das Tier bereits ausgewählt!");
				player.playSound(player.getLocation(), Sound.CLICK, 0.4f, 0.8f);
				return;
			}
			
			/* Den Owner des Entities festlegen, null falls Entity nicht locked. */
			owner = database.getOwner(entity.getUniqueId().toString());
			
			/* Wenn das Tier nicht gelockt ist, dann wird der Owner nicht erwähnt. */
			if (owner == null) {
				switch (entity.getType()) {
				case COW:
					player.sendMessage("§eDu hast eine §6Kuh §eausgewählt. §7(/lockanimal um sie zu protecten.);");
					break;
				case PIG:
					player.sendMessage("§eDu hast ein §6Schwein §eausgewählt. §7(/lockanimal um es zu protecten.);");
					break;
				case CHICKEN:
					player.sendMessage("§eDu hast ein §6Huhn §eausgewählt. §7(/lockanimal um es zu protecten.);");
					break;
				case HORSE:
					player.sendMessage("§eDu hast ein §6Pferd §eausgewählt. §7(/lockanimal um es zu protecten.);");
					break;
				case WOLF:
					player.sendMessage("§eDu hast einen §6Wolf §eausgewählt. §7(/lockanimal um ihn zu protecten.);");
					break;
				case SHEEP:
					player.sendMessage("§eDu hast ein §6Schaf §eausgewählt. §7(/lockanimal um es zu protecten.);");
					break;
				default:
					player.sendMessage("§eDu hast ein §6unbekanntes §eTier ausgewählt. §7(/lockanimal um es zu protecten.);");
					break;
				}
			}
			/* Wenn das Tier protected ist, dann wird der Owner erwähnt. */
			else { 
				player.sendMessage("§eDu hast das Tier von §6"+owner+" §eausgewählt.");
				Animal animal = database.getAnimal(entity.getUniqueId().toString());
				if (animal == null) { return; }
				animal.updateAnimal(entity);
				animal.saveToDatabase(true);
			}
			
			/* Zum Schluss wird bei dem Spieler noch ein Sound abgespielt und sein zuletzt ausgewähltes Tier wird gespeichert. */
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
