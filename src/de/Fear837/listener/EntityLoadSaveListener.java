package de.Fear837.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import de.Fear837.Main;

public class EntityLoadSaveListener implements Listener {

	private Main plugin;

	public EntityLoadSaveListener(Main plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerJoin(PlayerJoinEvent event) {
		plugin.getEntityList().connect(event.getPlayer().getName());
		plugin.getServer().getLogger().info("Loading player "
						+ event.getPlayer().getName() + ". => "
						+ (plugin.getEntityList().lastActionSucceeded() ? "Success."
								: "Failed!"));
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerQuit(PlayerQuitEvent event) {
		plugin.getEntityList().disconnect(event.getPlayer().getName());
		plugin.getServer().getLogger().info("Unloading player "
				+ event.getPlayer().getName() + ". => "
				+ (plugin.getEntityList().lastActionSucceeded() ? "Success."
						: "Failed!"));
	}

}
