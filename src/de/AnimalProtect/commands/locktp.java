package de.AnimalProtect.commands;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import de.AnimalProtect.Main;
import de.AnimalProtect.MySQL;
import de.AnimalProtect.structs.EntityList;
import de.AnimalProtect.structs.EntityObject;

public class locktp implements CommandExecutor {

	Main plugin;
	MySQL database;
	EntityList list;
	
	public locktp(Main plugin) {
		this.plugin = plugin;
		this.database = plugin.database;
		this.list = plugin.list;
	}
	
	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		/* Datenbank-Verbindung aufbauen, falls nicht vorhanden. */
		if (!database.checkConnection()) { database.openConnection(); }
		
		if (plugin.isEnabled() && cs instanceof Player) {
			/* Variablen initialisieren */
			Player sender = (Player)cs;
			String player = "";
			Integer id = 1;
			ArrayList<EntityObject> entities = null;
			String world = plugin.getConfig().getString("settings.worldname");
			
			/* Prüfen ob der Spieler die Permission hat */
			if (!sender.hasPermission("animalprotect.admin")) {
				sender.sendMessage("§cFehler: Du hast nicht genügend Rechte um den Befehl auszuführen!");
				return true;
			}
			
			/* Argumente überprüfen */
			if (args.length == 0) {
				cs.sendMessage("§cFehler: Zu wenig Argumente! (/locktp <id> <name>)");
				return true;
			}
			else if (args.length == 1) {
				try { id = Integer.parseInt(args[0]); }
				catch (Exception e) {
					cs.sendMessage("§cFehler: Die angegebene Seite ist keine Zahl!");
					return true;
				}
				
				player = sender.getName();
			}
			else if (args.length == 2) {
				try { id = Integer.parseInt(args[0]); }
				catch (Exception e) {
					cs.sendMessage("§cFehler: Die angegebene Seite ist keine Zahl!");
					return true;
				}
				
				player = args[1];
			}
			else {
				cs.sendMessage("§cFehler: Zu viele Argumente wurden angegeben! (/locktp <id> <name>)");
			}
			
			/* Die Entities von dem Spieler laden */
			entities = list.getEntities(player);
			
			/* Prüfen ob die Entities-Liste leer ist */
			if (entities == null) { 
				cs.sendMessage("§cFehler: Es wurden keine Tiere gefunden!");
				return true;
			}
			if (!(entities.size() >= id) || id==0) { 
				cs.sendMessage("§cFehler: Das angegebene Tier wurde nicht gefunden!");
				return true;
			}
			
			/* Position des Entities bekommen */
			Integer x = null;
			Integer y = null;
			Integer z = null;
			
			try {
				x = entities.get(id-1).getLastx();
				y = entities.get(id-1).getLasty();
				z = entities.get(id-1).getLastz();
			}
			catch (Exception e) { }
			
			for (Entity e : plugin.getServer().getWorld(world).getEntities()) {
				if (e.getUniqueId().toString().equals(entities.get(id-1).getUniqueID())) {
					x = e.getLocation().getBlockX();
					y = e.getLocation().getBlockY();
					z = e.getLocation().getBlockZ();
				}
			}
			
			if (x != null && y != null && z != null) {
				sender.teleport(new Location(plugin.getServer().getWorld(world), x, y, z));
				sender.sendMessage("§eTeleported.");
			}
			else {
				sender.sendMessage("§cFehler: Position konnte nicht ermittelt werden!");
			}
		}
		return true;
	}
}
