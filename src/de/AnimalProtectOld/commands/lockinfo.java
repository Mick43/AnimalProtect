package de.AnimalProtectOld.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import de.AnimalProtectOld.Main;
import de.AnimalProtectOld.MySQL;
import de.AnimalProtectOld.listener.InteractEventListener;
import de.AnimalProtectOld.structs.EntityList;

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
		/* Datenbank-Verbindung aufbauen, falls nicht vorhanden. */
		if (!database.checkConnection()) { database.openConnection(); }
		
		if (plugin.isEnabled() && cs instanceof Player) {
			/* Die Variablen initialisieren. */
			Player player = (Player)cs;
			Entity entity = InteractEventListener.getSelected(player);
			String owner = null;
			
			/* Prüfen ob der Spieler die Permission hat */
			if (!player.hasPermission("animalprotect.protect")) {
				player.sendMessage("§cFehler: Du hast nicht genügend Rechte um den Befehl auszuführen!");
				return true;
			}
			
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
