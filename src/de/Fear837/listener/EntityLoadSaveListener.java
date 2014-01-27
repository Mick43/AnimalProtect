package de.Fear837.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import de.Fear837.Main;

public class EntityLoadSaveListener implements Listener {

	private Main plugin;

	public EntityLoadSaveListener(Main plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerJoin(PlayerJoinEvent event) {
		plugin.getEntityList().connect(event.getPlayer());
		plugin.getServer().getLogger().info("Loading player "
						+ event.getPlayer().getName() + ". => "
						+ (plugin.getEntityList().lastActionSucceeded() ? "Success."
								: "Failed!"));
	}

}
