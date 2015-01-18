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

import craftoplugin.core.database.CraftoPlayer;
import craftoplugin.utility.CraftoTime;
import de.AnimalProtect.AnimalProtect;
import de.AnimalProtect.Messenger;
import de.AnimalProtect.structs.Animal;

/**
 * Die Listcommand-Klasse. {@code /listanimals}
 * 
 * @author Fear837, Pingebam
 * @version 1.0
 * @see CommandExecutor
 */
public class Command_list implements CommandExecutor {

	/** Die AnimalProtect-Instanz. */
	private final AnimalProtect plugin;

	/**
	 * Initialisiert die Commandklasse.
	 * @param plugin - Das AnimalProtect-Plugin.
	 */
	public Command_list(final AnimalProtect plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(final CommandSender cs, final Command cmd, final String label, final String[] args) {
		if (!this.plugin.isEnabled()) { Messenger.sendMessage(cs, "§cFehler: Der Befehl konnte nicht ausgeführt werden."); return true; }

		/* Datenbank-Verbindung aufbauen, falls nicht vorhanden. */
		if (!this.plugin.getDatenbank().isConnected())
		{ this.plugin.getDatenbank().connect(); }

		/* Variablen initialisieren */
		Integer page = 1;
		Integer pages = 1;
		CraftoPlayer cPlayer = null;
		final ArrayList<Animal> animals = this.plugin.parseAnimal(cs, args, true);

		if (animals == null) { return true; }

		if (animals.size() < 1)
		{ Messenger.sendMessage(cs, "ANIMALS_NOT_FOUND"); return true; }

		cPlayer = CraftoPlayer.getPlayer(animals.get(0).getOwner());

		/* Seite ermitteln */
		for (final String arg : args) { if (this.isNumber(arg)) { page = Integer.parseInt(arg); } }

		/* Die Seitenanzahl ausrechnen */
		final Double pagesAsDouble = ((double)animals.size() / (double)10);
		pages = (int) Math.ceil(pagesAsDouble);

		Collections.sort(animals);

		/* Seitenangabe überprüfen */
		if (pages == 0)
		{ Messenger.sendMessage(cs, "PLAYER_NO_LOCKS"); return true; }
		else if (page > pages)
		{ Messenger.sendMessage(cs, "PAGE_NOT_EXIST"); return true; }

		/* Listenanfang schicken */
		//Messenger.help(cs, "Liste der Tiere von "+cPlayer.getName()+" ("+page+"/"+pages+")");
		//Messenger.sendMessage(cs, "§7§oInsgesamte Anzahl an Tieren: " +animals.size());
		Messenger.messageHeader(cs, "Liste der Tiere von " +cPlayer.getName()+" ("+page+"/"+pages+", insg. "+animals.size()+" Tiere)");

		final HashMap<UUID, Entity> entities = new HashMap<UUID, Entity>();
		for (final Entity entity : Bukkit.getServer().getWorlds().get(0).getEntities()) {
			entities.put(entity.getUniqueId(), entity);
		}

		for (int i=page*10-10; i<page*10 && i<animals.size(); i++) {
			final Animal animal = animals.get(i);
			String status = animal.isAliveAsString(); // ALIVE // DEAD
			Boolean found = false;

			if (entities.containsKey(animal.getUniqueId())) {
				final Entity entity = entities.get(animal.getUniqueId());
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
				final Chunk chunk = Bukkit.getServer().getWorlds().get(0).getChunkAt(animal.getX(), animal.getZ());
				for (final Entity entity : chunk.getEntities()) {
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

			if (animal.getNametag() != null && !animal.getNametag().isEmpty())
			{ Message += "§fnamed '§3" + animal.getNametag() + "§f' "; }
			else { Message += "§flocated at §3"+animal.getX()+", "+animal.getY()+", "+animal.getZ()+" "; }

			Message += "§flocked at §3" + CraftoTime.getTime(animal.getCreated_at(), "dd.MM.yyyy") + "§f ";
			Message += "§7("+animals.indexOf(animal)+")";

			Messenger.sendMessage(cs, Message);
		}
		return true;
	}

	/**
	 * @param value - Der übergebene Wert.
	 * @return True, wenn der übergebene Wert eine Zahl ist.
	 */
	private boolean isNumber(final String value) {
		try { Integer.parseInt(value); return true; }
		catch (final Exception e) { return false; }
	}
}