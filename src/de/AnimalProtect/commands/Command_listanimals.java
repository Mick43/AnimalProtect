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
		World world = Bukkit.getServer().getWorlds().get(0);
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
				else { Messenger.sendMessage(cs, "NO_GIVEN_PLAYER"); return; }
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
			else { Messenger.sendMessage(cs, "PAGE_NOT_NUMBER"); return; }
		}
		else { Messenger.sendMessage(cs, "TOO_MANY_ARGUMENTS"); return; }
		
		/* Prüfen ob der Spieler gefunden wurde */
		if (cPlayer.getId() == null)
		{ Messenger.sendMessage(cs, "PLAYER_NOT_FOUND"); return; }
		
		/* Die Tiere des Spielers laden */
		animals = plugin.getDatenbank().getAnimals(cPlayer.getUniqueId());
		
		if (animals == null || animals.isEmpty())
		{ Messenger.sendMessage(cs, "PLAYER_NO_LOCKS"); return; }
		
		/* Die Seitenanzahl ausrechnen */
		Double pagesAsDouble = ((double)animals.size() / (double)10);
		pages = (int) Math.ceil(pagesAsDouble);
		
		/* Seitenangabe überprüfen */
		if (pages == 0)
		{ Messenger.sendMessage(cs, "PLAYER_NO_LOCKS"); return; }
		else if (page > pages)
		{ Messenger.sendMessage(cs, "PAGE_NOT_EXIST"); return; }
		
		/* Listenanfang schicken */
		Messenger.sendMessage(cs, "§e--------- §fListe der Tiere von "+cPlayer.getName()+" ("+page+"/"+pages+") §e---------");
		Messenger.sendMessage(cs, "§7§oInsgesamte Anzahl an Tieren: " +animals.size());
		
		for (int i=page*10-10; i<page*10 && i<animals.size(); i++) {
			Animal animal = animals.get(i);
			Integer x = animal.getLast_x();
			Integer y = animal.getLast_y();
			Integer z = animal.getLast_z();
			String status = animal.isAliveAsString(); // ALIVE // DEAD
			Boolean found = false;
			
			if (world != null) {
				for (Entity entity : world.getEntities()) {
					if (entity.getUniqueId().equals(animal.getUniqueId())) {
						if (!entity.isDead()) {
							x = entity.getLocation().getBlockX();
							y = entity.getLocation().getBlockY();
							z = entity.getLocation().getBlockZ();
							animal.setAlive(true);
							status = "§aALIVE";
						}
						else if (animal.isAlive()) {
							animal.setAlive(false);
							animal.saveToDatabase(true);
							status = "§cDEAD";
						}
						found = true;
					}
				}
			}
			
			if (!found && animal.isAlive()) { status = "§cMISSING"; }
			
			String Message = "["+i+"] ";
			Message += animal.getAnimaltype() + " - ";
			Message += "[§6" +x+ "§e";
			Message += ", §6" +y+ "§e";
			Message += ", §6" +z+ "§e] ";
			Message += "['§6"+animal.getNametag()+"§e'] ";
			Message += "["+status+"§e]";
			
			Messenger.sendMessage(cs, Message);
		}
		
	}
	
	private static boolean isUUID(String value) {
		return value.matches(".*-.*-.*-.*-.*");
	}
	
	private static boolean isNumber(String value) {
		try {
			Integer.parseInt(value);
			return true;
		}
		catch (Exception e) { return false; }
	}
}
