package de.AnimalProtect.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
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
		if (plugin.isEnabled() && database.checkConnection() && cs instanceof Player) {
			/* Variablen initialisieren */
			Player player = (Player)cs;
			Entity entity = InteractEventListener.getSelected(player);
			
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
			
			player.sendMessage("�aDas Tier wurde erfolgreich gesichert!");
		}
		return false;
	}
	
	private boolean isAnimal(Entity entity) {
		EntityType type = entity.getType();
		if (type == EntityType.SHEEP
		||  type == EntityType.PIG
		||  type == EntityType.COW
		||  type == EntityType.CHICKEN
		||  type == EntityType.HORSE
		||  type == EntityType.WOLF)
		{ return true; }
		return false;
	}
}
