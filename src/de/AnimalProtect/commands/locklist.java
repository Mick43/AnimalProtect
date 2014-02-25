package de.AnimalProtect.commands;

import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import de.AnimalProtect.Main;
import de.AnimalProtect.MySQL;
import de.AnimalProtect.structs.EntityList;
import de.AnimalProtect.structs.EntityObject;

public class locklist implements CommandExecutor {

	Main plugin;
	MySQL database;
	EntityList list;
	
	public locklist(Main plugin) {
		this.plugin = plugin;
		this.database = plugin.database;
		this.list = plugin.list;
	}
	
	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		/* Datenbank-Verbindung aufbauen, falls nicht vorhanden. */
		if (!database.checkConnection()) { database.openConnection(); }
		
		if (plugin.isEnabled()) {
			/* Prüfen ob der Spieler die Permission hat */
			if (cs instanceof Player) {
				/* Prüfen ob der Spieler die Permission hat */
				if (!((Player)cs).hasPermission("animalprotect.protect")) {
					((Player)cs).sendMessage("§cFehler: Du hast nicht genügend Rechte um den Befehl auszuführen!");
					return true;
				}
			}
			
			/* Variablen initialisieren */
			String player = "";
			Integer page = 1;
			Integer pages = 1;
			ArrayList<EntityObject> entities = null;
			String world = plugin.getConfig().getString("settings.worldname");
			
			/* Argumente überprüfen */
			if (args.length == 0) {
				if (cs instanceof Player) { 
					Player p = (Player)cs;
					player = p.getName();
				}
				else {
					cs.sendMessage("§cFehler: Es fehlen Argumente! (/locklist <name> <seite>)");
					return true;
				}
			}
			else if (args.length == 1) {
				/* Prüfen ob das Argument die Seitennummer oder der Spielername ist */
				if (isNumber(args[0])) {
					page = Integer.parseInt(args[0]);
					
					if (cs instanceof Player) { 
						Player p = (Player)cs;
						player = p.getName();
					}
					else {
						cs.sendMessage("§cFehler: Es fehlen Argumente! (/locklist <name> <seite>)");
						return true;
					}
				}
				else {
					page = 1;
					player = args[0];
				}
			}
			else if (args.length == 2) {
				player = args[0];
				
				if (isNumber(args[1])) {
					page = Integer.parseInt(args[1]);
				}
				else {
					cs.sendMessage("§cFehler: Die angegebene Zahl ist keine Nummer! (/locklist <name> <seite>)");
					return true;
				}
			}
			else { cs.sendMessage("§cFehler: Zu viele Argumente angegeben! (/locklist <seite> <name>)"); return true; }
			
			/* Die Entities von dem Spieler laden */
			entities = list.getEntities(player);
			
			/* Prüfen ob der Spieler jemals Entities protectet hat */
			if (entities.isEmpty()) { cs.sendMessage("§cFehler: Der Spieler wurde nicht gefunden!"); return true; }
			
			/* Die Seitenanzahl ausrechnen */
			Double pagesAsDouble = ((double)entities.size() / (double)10);
			pages = (int) Math.ceil(pagesAsDouble);
			
			/* Prüfen ob der Spieler nicht eine Seite angegeben hat, die nicht existiert. */
			if (page > pages ) {
				cs.sendMessage("§cFehler: Die angegebene Seite existiert nicht!");
				return true;
			}
			
			if (entities != null) {
				/* Die Überschrift der Liste schreiben und die Schleife beginnen. */
				cs.sendMessage("§e---------- §fListe der Tiere von "+player+" ("+page+"/"+pages+") §e----------");
				cs.sendMessage("§7§oInsgesamte Anzahl an Tieren: "+entities.size());
				for (int i=((page-1) * 10); i<((page-1) * 10)+10; i++) {
					
					/* Variablen bereit stellen */
					EntityObject e = entities.get(i);
					String dead = "";
					Integer x = e.getLastx();
					Integer y = e.getLasty();
					Integer z = e.getLastz();
										
					/* Den Status des Entities überprüfen */
					for (Entity entity : plugin.getServer().getWorld(world).getEntities()) {
						if (e.getUniqueID().equals(entity.getUniqueId().toString())) {
							if (entity.isDead()) { dead = "§cDEAD"; }
							else { dead = "§aALIVE"; }
							x = entity.getLocation().getBlockX();
							y = entity.getLocation().getBlockY();
							z = entity.getLocation().getBlockZ();
						}
					}
					if (dead == "") {
						if (!e.isAlive()) { dead = "§cDEAD"; }
						else { dead = "§cMISSING"; }
					}
					
					/* Die Message vorbereiten */
					String Message = "";
					Message += "§e["+(i+1)+"] "
							+  "§e" +e.getType().toUpperCase()+ " - "
							+  "§e[§6" +x+ "§e, "
							+  "§6" +y+ "§e, "
							+  "§6" +z+ "§e] "
							+  "§e['§6" +e.getNametag()+ "§e'] "
							+  "["+dead+"§e]";
					
					/* Die Message abschicken*/
					cs.sendMessage(Message);
					
					/* Wenn das letzte Entity erreicht wurde, dann wird die Schleife abgebrochen. */
					if (i == entities.size() - 1) { i = ((page-1) * 10)+10; }
				}
			}
			return true;
		}
		cs.sendMessage("§cFehler: Plugin ist deaktiviert oder es besteht keine Datenbank-Verbindung");
		return true;
	}
	
	private boolean isNumber(String value) {
		try {
			Integer number = Integer.parseInt(value);
			return true;
		}
		catch (Exception e) { return false; }
	}
}
