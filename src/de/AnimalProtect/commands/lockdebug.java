package de.AnimalProtect.commands;

import java.sql.SQLException;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.AnimalProtect.Main;
import de.AnimalProtect.MySQL;
import de.AnimalProtect.structs.EntityList;

public class lockdebug implements CommandExecutor {

	Main plugin;
	MySQL database;
	EntityList list;
	
	public lockdebug(Main plugin) {
		this.plugin = plugin;
		this.database = plugin.database;
		this.list = plugin.list;
	}
	
	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if (cs instanceof Player) {
			Player player = (Player)cs;
			
			/* Prüfen ob der Spieler die Permission hat */
			if (!player.hasPermission("animalprotect.debug")) {
				player.sendMessage("§cFehler: Du hast nicht genügend Rechte um den Befehl auszuführen!");
				return true;
			}
			
			if (args.length == 3 && cs instanceof Player) {
				try {
					player.playSound(player.getLocation(), Sound.valueOf(args[0]), Float.parseFloat(args[1]), Float.parseFloat(args[2]));
				}
				catch (Exception e) { }
				
				return true;
			}
			
			if (!player.hasPermission("animalprotect.admin")) { 
				player.sendMessage("§cYou don't have permission.");
				return true;
			}
			
			int entities = list.sizeOfEntitiesInRam();
			int players = list.sizeOfPlayers();
			int locks = list.sizeOfLocks();
			boolean dbnull = !database.checkConnection();
			boolean dbclosed;
			try { dbclosed = database.getConnection().isClosed();
			} 
			catch (Exception e) { dbclosed=true; }
			boolean dbvalid;
			try { dbvalid = database.getConnection().isValid(2);
			} catch (Exception e) { dbvalid = false; }
			
			cs.sendMessage("");
			cs.sendMessage("EntityList-Size: [§7"+entities+" §fEntities] [§7"+players+" §fPlayers] [§7"+locks+" §fLocks]");
			cs.sendMessage("Amount of failed queries: [§7" + database.failedQueries.size() + "§f]");
			cs.sendMessage("Is plugin enabled: [§7" + plugin.isEnabled() + "§f]");
			cs.sendMessage("Database: [NULL: §7"+dbnull+"§f] [Closed: §7"+dbclosed+"§f] [Valid: §7"+dbvalid+"§f]");
		}
		else {
			int entities = list.sizeOfEntitiesInRam();
			int players = list.sizeOfPlayers();
			int locks = list.sizeOfLocks();
			boolean dbnull = !database.checkConnection();
			boolean dbclosed;
			try { dbclosed = database.getConnection().isClosed();
			} catch (SQLException e) { dbclosed=true; }
			boolean dbvalid;
			try { dbvalid = database.getConnection().isValid(2);
			} catch (SQLException e) { dbvalid = false; }
			
			cs.sendMessage("");
			cs.sendMessage("EntityList-Size: ["+entities+" Entities] ["+players+" Players] ["+locks+" Locks]");
			cs.sendMessage("Amount of failed queries: [" + database.failedQueries + "]");
			cs.sendMessage("Is plugin enabled: [" + plugin.isEnabled() + "]");
			cs.sendMessage("Database: [NULL: "+dbnull+"] [Closed: "+dbclosed+"] [Valid: "+dbvalid+"]");
		}
		
		return true;
	}
}
