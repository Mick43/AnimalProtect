package de.AnimalProtect;

/* Java Imports */
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

/* Bukkit Imports */
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/* CraftoPlugin Imports */
import craftoplugin.core.CraftoMessenger;
import craftoplugin.utility.CraftoFile;
import craftoplugin.utility.CraftoTime;

public class Messenger {

	/** Der Chatprefix von AnimalProtect. */
	private final static String Prefix = ChatColor.RESET + "" + ChatColor.GOLD + "AnimalProtect //";
	/** der Konsolenprefix von AnimalProtect. */
	private final static String ConsolePrefix = "AnimalProtect";
	/** True, für Debugausgaben vom Messenger. */
	private final static Boolean Debugging = true;
	/** Eine Map an Exceptions. */
	private static HashMap<String, Integer> exceptions;
	/** Eine Map an Exceptions mit dem Zeitpunkt an der sie zum ersten mal aufgetreten sind. */
	private static HashMap<String, Long> exceptionTimings;

	/**
	 * Gibt eine Nachricht in der Konsole aus.
	 * 
	 * @param message
	 *            Die auszugebende Nachricht
	 */
	public static void log(final String message) {
		Bukkit.getServer().getLogger().info("["+Messenger.ConsolePrefix+"] "  + message);
	}

	/**
	 * Gibt eine Nachricht in der Konsole aus.
	 * 
	 * @param message
	 *            Die auszugebende Nachricht
	 * @param level
	 *            Der Nachrichten-Typ
	 */
	public static void log(final String message, final Level level) {
		Bukkit.getServer().getLogger().log(level, "["+Messenger.ConsolePrefix+"] " + message);
	}

	/**
	 * Warnt die Konsole mit einer bestimmten Nachricht.
	 * 
	 * @param message
	 *            Die auszugebende Nachricht.
	 */
	public static void warn(final String message) {
		Bukkit.getServer().getLogger().warning("["+Messenger.ConsolePrefix+"] " + message);
	}

	/**
	 * Gibt eine wichtige Fehlermeldung in der Konsole aus.
	 * 
	 * @param message
	 *            Die auszugebende Nachricht.
	 */
	public static void error(final String message) {
		Bukkit.getServer().getLogger().log(Level.SEVERE, "["+Messenger.ConsolePrefix+"] " + message);
	}

	/**
	 * Gibt eine Chat-Nachricht an alle Spieler im Server aus.
	 * 
	 * @param message
	 *            Die Nachricht, die an jeden Spieler geschickt wird.
	 */
	public static void broadcast(final String message) {
		Bukkit.getServer().broadcastMessage(message);
	}			


	/**
	 * Sendet einem CommandSender die Überschrift für eine Hilfe-Liste
	 * 
	 * @param cs
	 *            Der CommandSender, an den die Überschrift geschickt wird.
	 * @param message
	 *            Die Überschrift, ohne Farbcodes!
	 */
	public static void help(final CommandSender cs, String message) {
		final String parsedMessage = Messenger.parseMessage(message);
		if (parsedMessage != null) { message = parsedMessage; }

		String title = ChatColor.YELLOW + "---------- " + ChatColor.WHITE + message + ChatColor.YELLOW + " ";
		for (int i=0; i<18-message.length()+20; i++) {
			title += "-";
		}
		Messenger.sendMessage(cs, title);
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
	public static void sendMessage(final Player player, String message, final Boolean prefix) {
		if (player == null) { return; }

		final String parsedMessage = Messenger.parseMessage(message);
		if (parsedMessage != null) { message = parsedMessage; }

		if (prefix) { player.sendMessage(Messenger.Prefix + " " + ChatColor.YELLOW + message); }
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
	public static void sendMessage(final Player player, final String message) {
		if (player == null) { return; }

		Messenger.sendMessage(player, message, false);
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
	@SuppressWarnings("deprecation")
	public static void sendMessage(final String playerName, final String message, final Boolean prefix) {
		Messenger.sendMessage(Bukkit.getServer().getPlayer(playerName), message, prefix);
	}

	/**
	 * Benachrichtigt einen bestimmten Spieler.
	 * 
	 * @param playerName
	 *            Der Name des Spielers, an den die Nachricht geschickt wird.
	 * @param message
	 *            Die Nachricht, welche verschickt wird.
	 */
	@SuppressWarnings("deprecation")
	public static void sendMessage(final String playerName, final String message) {
		Messenger.sendMessage(Bukkit.getServer().getPlayer(playerName), message, false);
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
	public static void sendMessage(final UUID uniqueId, final String message, final Boolean prefix) {
		Messenger.sendMessage(Bukkit.getServer().getPlayer(uniqueId), message, prefix);
	}

	/**
	 * Benachrichtigt einen bestimmten Spieler.
	 * 
	 * @param uniqueId
	 *            Der UniqueId des Spielers, an den die Nachricht geschickt wird.
	 * @param message
	 *            Die Nachricht, welche verschickt wird.
	 */
	public static void sendMessage(final UUID uniqueId, final String message) {
		Messenger.sendMessage(Bukkit.getServer().getPlayer(uniqueId), message, false);
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
	public static void sendMessage(final CommandSender cs, String message, final Boolean prefix) {
		if (cs == null) { return; }

		if (cs instanceof Player) {
			Messenger.sendMessage((Player)cs, message, prefix);
		}
		else {
			final String parsedMessage = Messenger.parseMessage(message);
			if (parsedMessage != null) { message = parsedMessage; }

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
			message = message.replaceAll("§o", "");
			message = message.replaceAll("§l", "");
			message = message.replaceAll("§k", "");
			message = message.replaceAll("§m", "");
			message = message.replaceAll("§n", "");
			message = message.replaceAll("§r", "");
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

			if (prefix) { cs.sendMessage(Messenger.ConsolePrefix + " // " + message); }
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
	public static void sendMessage(final CommandSender cs, final String message) {
		if (cs == null || message == null) { return; }

		Messenger.sendMessage(cs, message, false);
	}

	/**
	 * Benachrichtigt einen bestimmten CommandSender mit einer Erfolgsnachricht.
	 * 
	 * @param cs
	 *            Der CommandSender, an den die Nachricht geschickt wird.
	 * @param message
	 *            Die Nachricht, welche verschickt wird.
	 */
	public static void sendMessageSuccess(final CommandSender cs, final String message) {
		if (cs == null || message == null) { return; }

		Messenger.sendMessage(cs, ChatColor.GREEN + message, false);
	}

	/**
	 * Benachrichtigt einen bestimmten CommandSender mit einer Fehlernachricht.
	 * 
	 * @param cs
	 *            Der CommandSender, an den die Nachricht geschickt wird.
	 * @param message
	 *            Die Nachricht, welche verschickt wird.
	 */
	public static void sendMessageFailed(final CommandSender cs, final String message) {
		if (cs == null || message == null) { return; }

		Messenger.sendMessage(cs, ChatColor.RED + message, false);
	}

	/**
	 * Schickt dem Spieler eine Titel-Nachricht für eine Auflistung.
	 * 
	 * @param cs
	 *            Der CommandSender, an den die Nachricht geschickt wird.
	 * @param message
	 *            Der Titel, welcher verschickt wird.
	 */
	public static void messageHeader(final CommandSender cs, String message) {
		if (cs == null || message == null) { return; }

		final String parsedMessage = Messenger.parseMessage(message);
		if (parsedMessage != null) { message = parsedMessage; }

		Messenger.sendMessage(cs, message, true);
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
	public static void messageList(final CommandSender cs, final String key, final String value) {
		if (cs == null || key == null || value == null) { return; }

		Messenger.sendMessage(cs, ChatColor.RESET + " • " + key + ": " + ChatColor.GRAY + value, false);
	}

	/**
	 * Benachrichtigt alle Operatoren und Administratoren.
	 * 
	 * @param message
	 *            Die Nachricht, welche verschickt wird.
	 */
	public static void messageStaff(String message) {
		final Player[] players = Bukkit.getServer().getOnlinePlayers();

		final String parsedMessage = Messenger.parseMessage(message);
		if (parsedMessage != null) { message = parsedMessage; }

		for (final Player p : players) {
			if (p.hasPermission("craftoplugin.admin") || p.hasPermission("craftoplugin.moderator") || p.isOp()) {
				p.sendMessage(Messenger.Prefix + message);
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
		if (Messenger.Debugging && AnimalProtect.plugin.isDebugging()) {
			final String parsedMessage = Messenger.parseMessage(message);
			if (parsedMessage != null) { message = parsedMessage; }

			Messenger.log("[DEBUG] " + message);
		}
	}

	public static String parseMessage(String message) {
		String tempMessage = AnimalProtect.plugin.getConfig().getString("messages."+message.toUpperCase());
		if (tempMessage != null) {
			tempMessage = tempMessage.replaceAll("%", "§");
			tempMessage = tempMessage.replaceAll("ae", "ä");
			tempMessage = tempMessage.replaceAll("oe", "ö");
			tempMessage = tempMessage.replaceAll("ue", "ü");
			tempMessage = tempMessage.replaceAll("(ä)", "ae");
			tempMessage = tempMessage.replaceAll("(ö)", "oe");
			tempMessage = tempMessage.replaceAll("(ü)", "ue");
			message = tempMessage;
		}

		return message;
	}

	/**
	 * Schreibt die Exception in die Konsole und speichert sie in einer Datei.
	 * @param e - Die Exception
	 * @param source - Die Quelle der Exception. (zb: GeneralModule/GeneralListener.java/onPlayerLogin)    
	 * @param information - Weitere Informationen über die Ursache der Exception
	 */
	public static void exception(final String source, final String information, final Exception e) {
		CraftoMessenger.exception(source, information, e, true);
	}

	/**
	 * Schreibt die Exception in die Konsole und speichert sie in einer Datei.
	 * @param e - Die Exception
	 * @param source - Die Quelle der Exception. (zb: GeneralModule/GeneralListener.java/onPlayerLogin)    
	 * @param information - Weitere Informationen über die Ursache der Exception
	 */
	public static synchronized void exception(final String source, final String information, final Exception e, final boolean createFile) {
		if (e == null) { return; }
		if (Messenger.exceptions == null) { Messenger.exceptions = new HashMap<String, Integer>(); Messenger.exceptions.put(source, 1); }
		if (Messenger.exceptionTimings == null) { Messenger.exceptionTimings = new HashMap<String, Long>(); }

		if (Messenger.exceptions.containsKey(source)) { Messenger.exceptions.put(source, Messenger.exceptions.get(source)+1); }
		else { Messenger.exceptions.put(source, 1); }

		if (Messenger.exceptionTimings.containsKey(source)) {
			if (Messenger.exceptionTimings.get(source) + 3600000 > System.currentTimeMillis()) { return; }
			else { Messenger.exceptionTimings.put(source, System.currentTimeMillis()); }
		}
		else { Messenger.exceptionTimings.put(source, System.currentTimeMillis()); }

		Messenger.warn("--------------------- AnimalProtect Exception! --------------------");
		Messenger.warn("An exception occured in animalprotect/" + source);
		Messenger.warn("Detailed information: " + information);
		Messenger.warn("Exception: " + e.getClass().getName());
		if (e.getCause() != null) { Messenger.warn("Cause: " + e.getCause().getMessage()); }
		for (final StackTraceElement s : e.getStackTrace()) {
			if (s.getClassName().startsWith("craftoplugin") || s.getClassName().startsWith("animalprotect"))
			{ Messenger.warn(" ---> " + s.getFileName() + "/" + s.getMethodName() + "() ---> Line: " + s.getLineNumber()); }
			else { Messenger.warn(" -> " + s.getFileName() + "/" + s.getMethodName() + "() -> Line: " + s.getLineNumber()); }
		}
		Messenger.warn("-------------------- Exception Stacktrace end --------------------");

		if (createFile) {
			final CraftoFile file = new CraftoFile(CraftoFile.BUKKIT_PATH + "plugins/AnimalProtect/exceptions/exception-"+CraftoTime.getFullTime());
			file.writeLine("--------------------- AnimalProtect Exception! --------------------");
			file.writeLine("An exception occured in animalprotect/" + source);
			file.writeLine("This exception was thrown " + Messenger.exceptions.get(source) + " times today.");
			file.writeLine("Detailed information: " + information);
			file.writeLine("Exception: " + e.getClass().getName());
			file.writeLine("Exception first occured at: " + CraftoTime.getFullTime());
			if (e.getCause() != null) { file.writeLine("Cause: " + e.getCause().getMessage()); }
			for (final StackTraceElement s : e.getStackTrace()) {
				if (s.getClassName().startsWith("craftoplugin") || s.getClassName().startsWith("animalprotect"))
				{ file.writeLine(" ---> " + s.getFileName() + "/" + s.getMethodName() + "() ---> Line: " + s.getLineNumber()); }
				else { file.writeLine(" -> " + s.getFileName() + "/" + s.getMethodName() + "() -> Line: " + s.getLineNumber()); }
			}
			file.writeLine("-------------------- Exception Stacktrace end --------------------");

			if (!file.save()) { Messenger.error("Failed to save an exception file."); }
			file.close();
		}
	}
}