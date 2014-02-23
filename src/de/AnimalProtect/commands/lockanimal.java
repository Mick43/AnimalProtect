package de.AnimalProtect.commands;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import de.AnimalProtect.Main;
import de.AnimalProtect.MySQL;
import de.AnimalProtect.listener.InteractEventListener;
import de.AnimalProtect.structs.EntityList;

public class lockanimal implements CommandExecutor {

	Main plugin;
	MySQL database;
	EntityList list;
	
	public lockanimal(Main plugin) {
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
			Player player = (Player)cs;
			Entity entity = InteractEventListener.getSelected(player);
			

			/* Pr�fen ob der Spieler die Permission hat */
			if (!player.hasPermission("animalprotect.protect")) {
				player.sendMessage("�cFehler: Du hast nicht gen�gend Rechte um den Befehl auszuf�hren!");
				return true;
			}
			
			/* Pr�fen ob ein Tier ausgew�hlt wurde. */
			if (entity == null) { 
				player.sendMessage("�cFehler: Es wurde kein Tier ausgew�hlt!");
				return true;
			}
			
			/* Pr�fen ob das Tier bereits gelockt wurde */
			if (list.containsEntity(entity)) { 
				player.sendMessage("�cFehler: Das Tier ist bereits protected!");
				return true;
			}
			
			/* Das Tier wird gelockt. */
			list.lock(player.getName(), entity);
			
			/* Pr�fen ob das locken geklappt hat */
			if (!list.lastActionSucceeded()) {
				player.sendMessage("�cFehler: Das Tier konnte nicht gelockt werden!");
				return true;
			}
			
			Integer locksLeft = (int) ((list.MAX_ENTITIES_FOR_PLAYER-1) - list.sizeOfEntities(player.getName()));
			
			player.playSound(player.getLocation(), Sound.CLICK, 2.0f, 2.5f);
			player.sendMessage("�aDas Tier wurde erfolgreich gesichert! ("+locksLeft+" Locks verbleibend)");
			return true;
		}
		return false;
	}
}
