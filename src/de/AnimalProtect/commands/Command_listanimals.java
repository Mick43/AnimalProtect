package de.AnimalProtect.commands;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import craftoplugin.core.database.CraftoPlayer;
import de.AnimalProtect.AnimalProtect;
import de.AnimalProtect.Messenger;
import de.AnimalProtect.structs.Animal;

public class Command_listanimals implements CommandExecutor {
	
	private static AnimalProtect plugin;
	
	public Command_listanimals(AnimalProtect plugin) {
		Command_listanimals.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if (!plugin.isEnabled()) { return true; }
		Command_listanimals.runCommand(cs, args);
		return true;
	}
	
	public static void runCommand(CommandSender cs, String[] args) {
		/* Datenbank-Verbindung aufbauen, falls nicht vorhanden. */
		if (plugin.getDatenbank().isConnected())
		{ plugin.getDatenbank().connect(); }
		
		/* Variablen initialisieren */
		CraftoPlayer cPlayer = null;
		Integer page = 1;
		Integer pages = 1;
		World world = Bukkit.getServer().getWorld(AnimalProtect.plugin.getConfig().getString("settings.world"));
		ArrayList<Animal> animals = null;
		
		/* Argumente überprüfen */
		if (args.length == 0) { /*  /listanimals  */
			if (cs instanceof Player) 
			{ cPlayer = CraftoPlayer.getPlayer(cs.getName()); }
			else { Messenger.sendMessage(cs, "§cFehler: Es fehlen Argumente! (/locklist <spieler> <seite>)"); return; }
		}
		else if (args.length == 1) { /*  /listanimals <args[0]>  */
			if (isNumber(args[0])) { 
				page = Integer.parseInt(args[0]); 
				
				if (cs instanceof Player) 
				{ cPlayer = CraftoPlayer.getPlayer(cs.getName()); }
				else { Messenger.sendMessage(cs, "§cFehler: Es wurde kein Spieler angegeben!"); return; }
			}
			else { 
				if (isUUID(args[0])) { cPlayer = CraftoPlayer.getPlayer(UUID.fromString(args[0])); }
				else { cPlayer = CraftoPlayer.getPlayer(args[0]); }
			}
		}
		else if (args.length == 2) { /*  /listanimals <args[0]> <args[1]  */
			if (isUUID(args[0])) { cPlayer = CraftoPlayer.getPlayer(UUID.fromString(args[0])); }
			else { cPlayer = CraftoPlayer.getPlayer(args[0]); }
			
			if (isNumber(args[1])) 
			{ page = Integer.parseInt(args[1]);  }
			else { Messenger.sendMessage(cs, "§cFehler: Die angegebene Seite ist keine Zahl!"); return; }
		}
		else { Messenger.sendMessage(cs, "§cFehler: Zu viele Argumente angegeben!"); return; }
		
		/* Prüfen ob der Spieler gefunden wurde */
		if (cPlayer.getId() == null)
		{ Messenger.sendMessage(cs, "§cFehler: Der Spieler konnte nicht gefunden werden!"); return; }
		
		/* Die Tiere des Spielers laden */
		animals = plugin.getDatenbank().getAnimals(cPlayer.getUniqueId());
		
		if (animals == null || animals.isEmpty())
		{ Messenger.sendMessage(cs, "§cFehler: Der Spieler hat noch keine Tiere gesichert!"); return; }
		
		/* Die Seitenanzahl ausrechnen */
		Double pagesAsDouble = ((double)animals.size() / (double)10);
		pages = (int) Math.ceil(pagesAsDouble);
		
		/* Seitenangabe überprüfen */
		if (pages == 0)
		{ Messenger.sendMessage(cs, "§eDer Spieler hat noch keine Tiere gesichert."); return; }
		else if (page > pages)
		{ Messenger.sendMessage(cs, "§cFehler: Die angegebene Seite existiert nicht!"); return; }
		
		/* Listenanfang schicken */
		Messenger.sendMessage(cs, "§e--------- §fListe der Tiere von "+cPlayer.getName()+" ("+page+"/"+pages+")");
		Messenger.sendMessage(cs, "§7§oInsgesamte Anzahl an Tieren: " +animals.size());
		
		for (int i=page*10-10; i<page*10 && i<animals.size(); i++) {
			Animal animal = animals.get(i);
			Integer x = animal.getLast_x();
			Integer y = animal.getLast_y();
			Integer z = animal.getLast_z();
			if (world != null) {
				for (Entity entity : world.getEntities()) {
					if (entity.getUniqueId().equals(animal.getUniqueId())) {
						if (!entity.isDead()) {
							x = entity.getLocation().getBlockX();
							y = entity.getLocation().getBlockY();
							z = entity.getLocation().getBlockZ();
							animal.setAlive(true);
						}
						else if (animal.isAlive()) {
							animal.setAlive(false);
							animal.saveToDatabase(true);
						}
					}
				}
			}
			
			String Message = "["+i+"] ";
			Message += animal.getAnimaltype() + " - ";
			Message += "[§6" +x+ "§e";
			Message += ", §6" +y+ "§e";
			Message += ", §6" +z+ "§e] ";
			Message += "['§6"+animal.getNametag()+"§e']";
			Message += "["+animal.isAliveAsString()+"]";
			
			Messenger.sendMessage(cs, Message);
		}
		
	}
	
	private static boolean isUUID(String value) {
		return false;
		//TODO: isUUID!
	}
	
	private static boolean isNumber(String value) {
		try {
			Integer.parseInt(value);
			return true;
		}
		catch (Exception e) { return false; }
	}
}
