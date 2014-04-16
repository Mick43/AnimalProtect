package de.AnimalProtect;

/* Java Imports */
import java.util.UUID;
import java.util.logging.Level;

/* Bukkit Imports */
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Messenger {
	
	private static String Prefix = ChatColor.RESET + "" + ChatColor.AQUA + "AnimalProtect //";
	private static String ConsolePrefix = "AnimalProtect";
	private static Boolean Debugging = true;
	
	/**
	 * Gibt eine Nachricht in der Konsole aus.
	 * 
	 * @param message
     *            Die auszugebende Nachricht
     */
	public static void log(String message) {
		Bukkit.getServer().getLogger().info("["+ConsolePrefix+"] "  + message);
	}
	
	/**
	 * Gibt eine Nachricht in der Konsole aus.
	 * 
	 * @param message
     *            Die auszugebende Nachricht
	 * @param level
     *            Der Nachrichten-Typ
     */
	public static void log(String message, Level level) {
		Bukkit.getServer().getLogger().log(level, "["+ConsolePrefix+"] " + message);
	}
	
	/**
	 * Warnt die Konsole mit einer bestimmten Nachricht.
	 * 
	 * @param message
     *            Die auszugebende Nachricht.
     */
	public static void warn(String message) {
		Bukkit.getServer().getLogger().warning("["+ConsolePrefix+"] " + message);
	}
	
	/**
	 * Gibt eine wichtige Fehlermeldung in der Konsole aus.
	 * 
	 * @param message
     *            Die auszugebende Nachricht.
     */
	public static void error(String message) {
		Bukkit.getServer().getLogger().log(Level.SEVERE, "["+ConsolePrefix+"] " + message);
	}
	
	/**
	 * Gibt eine Chat-Nachricht an alle Spieler im Server aus.
	 * 
	 * @param message
     *            Die Nachricht, die an jeden Spieler geschickt wird.
     */
	public static void broadcast(String message) {
		Bukkit.getServer().broadcastMessage(message);
	}
	
	/**
	 * Benachrichtigt einen bestimmten Spieler.
	 * 
	 * @param player
     *            Der Spieler, an den die Nachricht geschickt wird.
	 * @param message
     *            Die Nachricht, welche verschickt wird.
     * @param prefix
     *            Gibt an, ob vor der Nachricht der Prefix angegeben werden soll.
     */
	public static void sendMessage(Player player, String message, Boolean prefix) {
		if (player == null) { return; }
		
		if (AnimalProtect.plugin.getConfig().contains("messages."+message.toUpperCase())) {
			message = AnimalProtect.plugin.getConfig().getString("messages."+message.toUpperCase());
		}
		
		if (prefix) { player.sendMessage(Prefix + " " + ChatColor.YELLOW + message); }
		else { player.sendMessage(ChatColor.YELLOW + message); }
	}
	
	/**
	 * Benachrichtigt einen bestimmten Spieler.
	 * 
	 * @param player
     *            Der Spieler, an den die Nachricht geschickt wird.
	 * @param message
     *            Die Nachricht, welche verschickt wird.
     */
	public static void sendMessage(Player player, String message) {
		if (player == null) { return; }
		
		sendMessage(player, ChatColor.YELLOW + message, false);
	}
	
	/**
	 * Benachrichtigt einen bestimmten Spieler.
	 * 
	 * @param playerName
     *            Der Name des Spielers, an den die Nachricht geschickt wird.
	 * @param message
     *            Die Nachricht, welche verschickt wird.
     * @param prefix
     *            Gibt an, ob vor der Nachricht der Prefix angegeben werden soll.
     */
	public static void sendMessage(String playerName, String message, Boolean prefix) {
		sendMessage(Bukkit.getServer().getPlayer(playerName), message, prefix);
	}
	
	/**
	 * Benachrichtigt einen bestimmten Spieler.
	 * 
	 * @param playerName
     *            Der Name des Spielers, an den die Nachricht geschickt wird.
	 * @param message
     *            Die Nachricht, welche verschickt wird.
     */
	public static void sendMessage(String playerName, String message) {
		sendMessage(Bukkit.getServer().getPlayer(playerName), message, false);
	}
	
	/**
	 * Benachrichtigt einen bestimmten Spieler.
	 * 
	 * @param uniqueId
     *            Der UniqueId des Spielers, an den die Nachricht geschickt wird.
	 * @param message
     *            Die Nachricht, welche verschickt wird.
     * @param prefix
     *            Gibt an, ob vor der Nachricht der Prefix angegeben werden soll.
     */
	public static void sendMessage(UUID uniqueId, String message, Boolean prefix) {
		sendMessage(Bukkit.getServer().getPlayer(uniqueId), message, prefix);
	}
	
	/**
	 * Benachrichtigt einen bestimmten Spieler.
	 * 
	 * @param uniqueId
     *            Der UniqueId des Spielers, an den die Nachricht geschickt wird.
	 * @param message
     *            Die Nachricht, welche verschickt wird.
     */
	public static void sendMessage(UUID uniqueId, String message) {
		sendMessage(Bukkit.getServer().getPlayer(uniqueId), message, false);
	}
	
	/**
	 * Benachrichtigt einen bestimmten CommandSender.
	 * 
	 * @param cs
     *            Der CommandSender, an den die Nachricht geschickt wird.
	 * @param message
     *            Die Nachricht, welche verschickt wird.
     * @param prefix
     *            Gibt an, ob vor der Nachricht der Prefix angegeben werden soll.
     */
	public static void sendMessage(CommandSender cs, String message, Boolean prefix) {
		if (cs == null) { return; }
		
		if (cs instanceof Player) {
			sendMessage((Player)cs, message, prefix);
		}
		else {
			message = message.replaceAll("§0", "");
			message = message.replaceAll("§1", "");
			message = message.replaceAll("§2", "");
			message = message.replaceAll("§3", "");
			message = message.replaceAll("§4", "");
			message = message.replaceAll("§5", "");
			message = message.replaceAll("§6", "");
			message = message.replaceAll("§7", "");
			message = message.replaceAll("§8", "");
			message = message.replaceAll("§9", "");
			message = message.replaceAll("§a", "");
			message = message.replaceAll("§b", "");
			message = message.replaceAll("§c", "");
			message = message.replaceAll("§d", "");
			message = message.replaceAll("§e", "");
			message = message.replaceAll("§f", "");
			message = message.replaceAll("§f", "");
			message = message.replaceAll("§f", "");
			message = message.replaceAll("§f", "");
			message = message.replaceAll("§f", "");
			message = message.replaceAll("§A", "");
			message = message.replaceAll("§B", "");
			message = message.replaceAll("§C", "");
			message = message.replaceAll("§D", "");
			message = message.replaceAll("§E", "");
			message = message.replaceAll("§F", "");
			message = message.replaceAll("§O", "");
			message = message.replaceAll("§L", "");
			message = message.replaceAll("§K", "");
			message = message.replaceAll("§M", "");
			message = message.replaceAll("§N", "");
			message = message.replaceAll("§R", "");
			if (prefix) { cs.sendMessage(ConsolePrefix + " // " + message); }
			else { cs.sendMessage(message); }
		}
	}
	
	/**
	 * Benachrichtigt einen bestimmten CommandSender.
	 * 
	 * @param cs
     *            Der CommandSender, an den die Nachricht geschickt wird.
	 * @param message
     *            Die Nachricht, welche verschickt wird.
     */
	public static void sendMessage(CommandSender cs, String message) {
		if (cs == null || message == null) { return; }
		
		sendMessage(cs, ChatColor.YELLOW + message, false);
	}
	
	/**
	 * Benachrichtigt einen bestimmten CommandSender mit einer Erfolgsnachricht.
	 * 
	 * @param cs
     *            Der CommandSender, an den die Nachricht geschickt wird.
	 * @param message
     *            Die Nachricht, welche verschickt wird.
     */
	public static void sendMessageSuccess(CommandSender cs, String message) {
		if (cs == null || message == null) { return; }
		
		sendMessage(cs, ChatColor.GREEN + message, false);
	}
	
	/**
	 * Benachrichtigt einen bestimmten CommandSender mit einer Fehlernachricht.
	 * 
	 * @param cs
     *            Der CommandSender, an den die Nachricht geschickt wird.
	 * @param message
     *            Die Nachricht, welche verschickt wird.
     */
	public static void sendMessageFailed(CommandSender cs, String message) {
		if (cs == null || message == null) { return; }
		
		sendMessage(cs, ChatColor.RED + message, false);
	}
	
	/**
	 * Schickt dem Spieler eine Titel-Nachricht für eine Auflistung.
	 * 
	 * @param cs
     *            Der CommandSender, an den die Nachricht geschickt wird.
	 * @param message
     *            Der Titel, welcher verschickt wird.
     */
	public static void messageHeader(CommandSender cs, String message) {
		if (cs == null || message == null) { return; }
		
		sendMessage(cs, message, true);
	}
	
	/**
	 * Schickt dem Spieler einen Punkt der Informationsliste.
	 * 
	 * @param cs
     *            Der CommandSender, an den die Nachricht geschickt wird.
	 * @param key
     *            Der Schlüssel, welcher vor dem Wert steht.
     * @param value
     *            Der Wert, zu dem der Schlüssel gehört.
     */
	public static void messageList(CommandSender cs, String key, String value) {
		if (cs == null || key == null || value == null) { return; }
		
		sendMessage(cs, ChatColor.RESET + "- " + key + ": " + ChatColor.GRAY + value, true);
	}
	
	/**
	 * Benachrichtigt alle Operatoren und Administratoren.
	 * 
	 * @param message
     *            Die Nachricht, welche verschickt wird.
     */
	public static void messageStaff(String message) {
		Player[] players = Bukkit.getServer().getOnlinePlayers();
		
		if (AnimalProtect.plugin.getConfig().contains("messages."+message.toUpperCase())) {
			message = AnimalProtect.plugin.getConfig().getString("messages."+message.toUpperCase());
		}
		
		for (Player p : players) {
			if (p.hasPermission("craftoplugin.admin") || p.hasPermission("craftoplugin.moderator") || p.isOp()) {
				p.sendMessage(Prefix + message);
			}
		}
	}
	
	/**
	 * Gibt eine Debug-Nachricht an die Konsole weiter.
	 * 
	 * @param message
     *            Die Nachricht die in der Konsole ausgegeben wird.
     */
	public static void debugMessage(String message) {
		if (Debugging) {
			if (AnimalProtect.plugin.getConfig().contains("messages."+message.toUpperCase())) {
				message = AnimalProtect.plugin.getConfig().getString("messages."+message.toUpperCase());
			}
			
			log("[DEBUG] " + message);
		}
	}
	
	/**
	 * Gibt eine Exception mit dem kompletten Stacktrace in der Konsole aus.
	 * 
	 * @param Source
     *            Die Quelle, aus welcher Klasse/Funktion die Exception kommt.
	 * @param Information
     *            Weitere Informationen über die Exception.
	 * @param e
     *            Die Exception, von der der Stacktrace in die Konsole geschrieben wird.
     */
	public static void exception(String Source, String Information, Exception e) {
		if (e == null) { return; }
		
		warn("---------------------------- "+ConsolePrefix+" Exception! ------------------------");
		warn("An Exception occured in animalprotect/" + Source);
		warn("More Information: " + Information);
		warn("Exception: " + e.getClass().getName());
		warn("---------------------------- Exception Stacktrace ----------------------------");
		for (StackTraceElement s : e.getStackTrace()) {
			warn(" -> " +s.getClassName()+"."+s.getMethodName()+" -> Line: "+s.getLineNumber());
		}
		warn("-------------------------- Exception Stacktrace End --------------------------");
	}
}
