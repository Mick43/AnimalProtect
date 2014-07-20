package de.AnimalProtect.listeners;

/* Java Imports */
import java.util.HashMap;
import java.util.UUID;

/* Bukkit Imports */
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

/* CraftoPlugin Imports */
import craftoplugin.core.database.CraftoPlayer;

/* AnimalProtect Imports */
import de.AnimalProtect.AnimalProtect;
import de.AnimalProtect.Database;
import de.AnimalProtect.Messenger;
import de.AnimalProtect.structs.Animal;

public class InteractEventListener implements Listener {
	
	private AnimalProtect plugin;
	private Database database;
	private HashMap<UUID, Entity> selectedList;
	private HashMap<UUID, Long> selectedTime;
	
	public InteractEventListener(AnimalProtect plugin, HashMap<UUID, Entity> list, HashMap<UUID, Long> time) {
		this.plugin = plugin;
		this.database = plugin.getDatenbank();
		this.selectedList = list;
		this.selectedTime = time;
	}
	
	@EventHandler
	public void onEntityEvent(PlayerInteractEntityEvent event) {
		try {
			if (!plugin.isEnabled() || event.isCancelled()) { return; }
			
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
				if (this.plugin.playerHasSelection(player.getUniqueId()) && this.plugin.getSelectedAnimal(player.getUniqueId()).equals(entity)) {
					Messenger.sendMessage(player, "SELECTED_ALREADY");
					player.playSound(player.getLocation(), Sound.CLICK, 0.4f, 0.8f);
					return;
				}
				
				/* Den Owner des Entities festlegen, null falls Entity nicht locked. */
				owner = database.getOwner(entity.getUniqueId());
				
				/* Wenn das Tier nicht gelockt ist, dann wird der Owner nicht erwähnt. */
				if (owner == null) {
					switch (entity.getType()) {
					case COW:
						Messenger.sendMessage(player, "SELECTED_COW");
						break;
					case PIG:
						Messenger.sendMessage(player, "SELECTED_PIG");
						break;
					case CHICKEN:
						Messenger.sendMessage(player, "SELECTED_CHICKEN");
						break;
					case HORSE:
						Messenger.sendMessage(player, "SELECTED_HORSE");
						break;
					case WOLF:
						Messenger.sendMessage(player, "SELECTED_WOLF");
						break;
					case SHEEP:
						Messenger.sendMessage(player, "SELECTED_SHEEP");
						break;
					case IRON_GOLEM:
						Messenger.sendMessage(player, "SELECTED_IRONGOLEM");
						break;
					case SNOWMAN:
						Messenger.sendMessage(player, "SELECTED_SNOWMAN");
						break;
					case VILLAGER:
						Messenger.sendMessage(player, "SELECTED_VILLAGER");
						break;
					case OCELOT:
						Messenger.sendMessage(player, "SELECTED_OCELOT");
						break;
					default:
						Messenger.sendMessage(player, "SELECTED_UNKNOWN");
						break;
					}
				}
				/* Wenn das Tier protected ist, dann wird der Owner erwähnt. */
				else {
					Messenger.sendMessage(player, "Du hast das Tier von §6"+owner.getName()+" §eausgewählt.");
					
					try {
						/* Wenn seit dem letzten Select 30 Sekunden vergangen sind */
						if (this.plugin.playerHasSelection(player.getUniqueId())) {
							if (this.plugin.getLastSelection(player.getUniqueId()) + 60000 < System.currentTimeMillis()) {
								Animal animal = database.getAnimal(entity.getUniqueId());
								if (animal != null) { 
									animal.updateAnimal(entity);
									animal.saveToDatabase(true);
								}
							}
						}
					}
					catch (Exception e) { Messenger.exception("InteractEventListener.onEntityEvent", "Exception :(", e); }
				}
				
				/* Zum Schluss wird bei dem Spieler noch ein Sound abgespielt und sein zuletzt ausgewähltes Tier wird gespeichert. */
				player.playSound(player.getLocation(), Sound.CLICK, 0.75f, 0.8f);
				addSelected(player.getUniqueId(), entity);
			}
		}
		catch (Exception e) { Messenger.exception("InteractEventListener/onEntityEvent", "Unknown Exception", e); }
	}
	
	private void addSelected(UUID uuid, Entity entity) {
		this.selectedList.put(uuid, entity);
		this.selectedTime.put(uuid, System.currentTimeMillis());
	}
}
