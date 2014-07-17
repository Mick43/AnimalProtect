package de.AnimalProtect.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import craftoplugin.core.database.CraftoPlayer;
import craftoplugin.utility.CraftoTime;
import de.AnimalProtect.AnimalProtect;
import de.AnimalProtect.Messenger;
import de.AnimalProtect.structs.Animal;

public class Command_list implements CommandExecutor {
	
	private static AnimalProtect plugin;
	
	public Command_list(AnimalProtect plugin) {
		Command_list.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if (!plugin.isEnabled()) { return true; }
		Command_list.runCommand(cs, args);
		return true;
	}
	
	public static void runCommand(CommandSender cs, String[] args) {
		if (plugin == null) { Messenger.sendMessage(cs, "§cFehler: Der Befehl konnte nicht ausgeführt werden."); return; }
		
		/* Datenbank-Verbindung aufbauen, falls nicht vorhanden. */
		if (!plugin.getDatenbank().isConnected())
		{ plugin.getDatenbank().connect(); }
		
		/* Variablen initialisieren */
		CraftoPlayer cPlayer = null;
		Integer page = 1;
		Integer pages = 1;
		ArrayList<Animal> animals = null;
		
		/* Argumente überprüfen */
		if (args.length == 0) { /*  /listanimals  */
			if (cs instanceof Player) 
			{ cPlayer = CraftoPlayer.getPlayer(cs.getName()); }
			else { Messenger.sendMessage(cs, "TOO_FEW_ARGUMENTS"); return; }
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
		
		Collections.sort(animals);
		
		/* Seitenangabe überprüfen */
		if (pages == 0)
		{ Messenger.sendMessage(cs, "PLAYER_NO_LOCKS"); return; }
		else if (page > pages)
		{ Messenger.sendMessage(cs, "PAGE_NOT_EXIST"); return; }
		
		/* Listenanfang schicken */
		//Messenger.help(cs, "Liste der Tiere von "+cPlayer.getName()+" ("+page+"/"+pages+")");
		//Messenger.sendMessage(cs, "§7§oInsgesamte Anzahl an Tieren: " +animals.size());
		Messenger.messageHeader(cs, "Liste der Tiere von " +cPlayer.getName()+" ("+page+"/"+pages+", insg. "+animals.size()+" Tiere)");
		
		HashMap<UUID, Entity> entities = new HashMap<UUID, Entity>();
		for (Entity entity : Bukkit.getServer().getWorlds().get(0).getEntities()) {
			entities.put(entity.getUniqueId(), entity);
		}
		
		for (int i=page*10-10; i<page*10 && i<animals.size(); i++) {
			Animal animal = animals.get(i);
			String status = animal.isAliveAsString(); // ALIVE // DEAD
			Boolean found = false;
			
			if (entities.containsKey(animal.getUniqueId())) {
				Entity entity = entities.get(animal.getUniqueId());
				if (!entity.isDead()) {
					animal.setAlive(true);
					status = Messenger.parseMessage("ANIMAL_ALIVE"); // "§aALIVE";
				}
				else if (animal.isAlive()) {
					animal.setAlive(false);
					animal.saveToDatabase(true);
					status = Messenger.parseMessage("ANIMAL_DEAD"); // "§cDEAD";
				}
				found = true;
			}
			else {
				Bukkit.getServer().getWorlds().get(0).loadChunk(animal.getX(), animal.getZ());
				Chunk chunk = Bukkit.getServer().getWorlds().get(0).getChunkAt(animal.getX(), animal.getZ());
				for (Entity entity : chunk.getEntities()) {
					if (entity.getUniqueId().equals(animal.getUniqueId())) {
						if (!entity.isDead()) {
							animal.setAlive(true);
							status = Messenger.parseMessage("ANIMAL_ALIVE"); // "§aALIVE";
						}
						else {
							status = Messenger.parseMessage("ANIMAL_DEAD");
							if (animal.isAlive()) {
								animal.setAlive(false);
								animal.saveToDatabase(true);
							}
						}
						found = true;
					}
				}
			}
			
			if (!found && animal.isAlive()) { status = Messenger.parseMessage("ANIMAL_MISSING"); } // "§cMISSING";
			
			String Message = " " + status + " ";
			Message += "§3" + animal.getAnimaltype().toString() + " ";
			
			if (animal.getNametag() != null || !animal.getNametag().equalsIgnoreCase("") && !animal.getNametag().isEmpty())
			{ Message += "§fnamed '§3" + animal.getNametag() + "§f' "; }
			
			Message += "§flocked at §3" + CraftoTime.getTime("dd.MM.yyyy") + "§f ";
			Message += "§7("+animals.indexOf(animal)+")";
			
			Messenger.sendMessage(cs, Message);
		}
		
	}
	
	private static boolean isUUID(String value) {
		if (value.length() != 36) { return false; }
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
