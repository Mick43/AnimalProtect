package de.AnimalProtect.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import de.AnimalProtect.Main;
import de.AnimalProtect.MySQL;
import de.AnimalProtect.listener.InteractEventListener;
import de.AnimalProtect.structs.EntityList;

public class lockinfo implements CommandExecutor {

	Main plugin;
	MySQL database;
	EntityList list;
	
	public lockinfo(Main plugin) {
		this.plugin = plugin;
		this.database = plugin.database;
		this.list = plugin.list;
	}
	
	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if (!database.checkConnection()) { database.openConnection(); }
		if (plugin.isEnabled() && database.checkConnection() && cs instanceof Player) {
			/* Die Variablen initialisieren. */
			Player player = (Player)cs;
			Entity entity = InteractEventListener.getSelected(player);
			String owner = null;
			
			/* Schauen ob ein Tier ausgewählt wurde. */
			if (entity == null) {
				player.sendMessage("§cFehler: Es wurde kein Tier ausgewählt!");
				return true;
			}
			
			/* Den Owner des Tieres bestimmen. */
			owner = list.getPlayer(entity.getUniqueId());
			
			/* Schauen ob das Tier protected ist. */
			if (owner == null) {
				player.sendMessage("§eDieses Tier ist nicht protected!");
				return true;
			}
			
			/* Wenn es protected ist, dann den Owner herausgeben. */
			player.sendMessage("§eDieses Tier ist von §6"+owner+" §eprotected.");
			return true;
		}
		return false;
	}
}
