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

public class unlockanimal implements CommandExecutor {

	Main plugin;
	MySQL database;
	EntityList list;
	
	public unlockanimal(Main plugin) {
		this.plugin = plugin;
		this.database = plugin.database;
		this.list = plugin.list;
	}
	
	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if (plugin.isEnabled() && cs instanceof Player) {
			/* Variablen initialisieren */
			Player player = (Player)cs;
			Entity entity = InteractEventListener.getSelected(player);
			
			/* Prüfen ob ein Tier ausgewählt wurde. */
			if (entity == null) { 
				player.sendMessage("§cFehler: Es wurde kein Tier ausgewählt!");
				return true;
			}
			
			/* Prüfen ob das Tier bereits gelockt wurde */
			if (!list.containsEntity(entity)) { 
				player.sendMessage("§cFehler: Das Tier ist noch nicht protected!");
				return true;
			}
			
			/* Prüfen ob der Sender der Owner oder Admin/Moderator ist */
			if (!list.getPlayer(entity.getUniqueId()).equalsIgnoreCase(player.getName()) && !player.hasPermission("animalprotect.admin")) {
				player.sendMessage("§cFehler: Du hast nicht genügend Rechte um das Tier zu unlocken!");
				return true;
			}
			
			/* Das Tier wird gelockt. */
			list.unlock(entity.getUniqueId());
			
			/* Prüfen ob das locken geklappt hat */
			if (!list.lastActionSucceeded()) {
				player.sendMessage("§cFehler: Die Protection konnte nicht enfernt werden!");
				return true;
			}
			
			player.playSound(player.getLocation(), Sound.CLICK, 2.0f, 2.5f);
			player.sendMessage("§aDie Protection wurde erfolgreich entfernt!");
			return true;
		}
		
		return true;
	}
}
