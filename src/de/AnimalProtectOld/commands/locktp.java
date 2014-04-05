package de.AnimalProtectOld.commands;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import de.AnimalProtectOld.Main;
import de.AnimalProtectOld.MySQL;
import de.AnimalProtectOld.structs.EntityList;
import de.AnimalProtectOld.structs.EntityObject;

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
				cs.sendMessage("§cFehler: Es fehlen Argumente! (/locklist <owner> <id>)");
				return true;
			}
			else if (args.length == 1) {
				/* Prüfen ob das Argument die Seitennummer oder der Spielername ist */
				if (isNumber(args[0])) {
					id = Integer.parseInt(args[0]);
					
					if (cs instanceof Player) { 
						Player p = (Player)cs;
						player = p.getName();
					}
					else {
						cs.sendMessage("Fehler: Es fehlen Argumente! (/locklist <owner> <id>)");
						return true;
					}
				}
				else {
					id = 1;
					player = args[0];
				}
			}
			else if (args.length == 2) {
				player = args[0];
				
				if (isNumber(args[1])) {
					id = Integer.parseInt(args[1]);
				}
				else {
					cs.sendMessage("§cFehler: Die angegebene Zahl ist keine Nummer!");
					return true;
				}
			}
			else { cs.sendMessage("§cFehler: Zu viele Argumente angegeben!"); return true; }
			
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
	
	private boolean isNumber(String value) {
		try {
			Integer.parseInt(value);
			return true;
		}
		catch (Exception e) { return false; }
	}
}
