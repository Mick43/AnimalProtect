package de.AnimalProtectOld.utility;

import java.util.logging.Logger;

import de.AnimalProtectOld.Main;

public class APLogger {

	//
	// HIER DIE LIEBLINGSWERTE FEST EINCODIERT EINGEBEN
	//

	/**
	 * Ein definierbarer Prefix vor jeder Nachricht, der Separator wird
	 * automatisch angehaengt.
	 */
	private static final transient String PREFIX = "";
	/**
	 * Ein definierbarer Suffix nach jeder Nachricht, der Separator wird
	 * automatisch vor den Suffix geschrieben.
	 */
	private static final transient String SUFFIX = "";
	/** Der Separator trennt alle Nachrichtenteile voneinander. */
	private static final transient char SEPARATOR = ' ';

	//
	// AB HIER NICHTS AENDERN
	//

	/** Der Logger, der die Ausgabe darstellen soll */
	private static transient Logger logger;
	/** Freigehaltene minimale Anzahl an Zeichen fuer eine Nachricht */
	private static final transient int BUFFER_MULTIPLIER = 16;
	/** Freigehaltener Platz fuer Eingabestrings als 2^X Zeichen des Builders */
	private static final transient int BUFFER_MIN_LENGTH = ((PREFIX.length() + SUFFIX
			.length()) / BUFFER_MULTIPLIER + 1)
			* BUFFER_MULTIPLIER;
	/** Cache der letzten Eingabe */
	private static transient String[] messageCache;
	/** Cache der letzten Ausgabe */
	private static transient String builderCache;
	/***/
	private static transient boolean isWarning = false;

	/**
	 * Kein Konstruktor, da keine Instanziierung erlaubt.
	 */
	private APLogger() {
	}

	/**
	 * Setzt den korrekten Logger, muss vor dem ersten Aufruf mindestens 1 Mal
	 * aufgerufen worden sein.
	 * 
	 * @param plugin
	 *            Das Plugin, auf dessen Logger die Ausgaben stattfinden sollen.
	 * @throws RuntimeException
	 *             wenn <tt>plugin</tt> gleich <tt>null</tt> ist.
	 */
	public static void setPlugin(Main plugin) {
		if (plugin == null || plugin.getLogger() == null) {
			throw new RuntimeException(
					"Das Plugin oder sein Logger darf nicht 'null' sein.");
		}
		logger = plugin.getLogger();
	}

	/**
	 * Gibt eine beliebig lange Nachricht auf dem zugehoerigen Logger des
	 * Plugins aus.<br>
	 * Der letzte Status (Info oder Warn) wird uebernommen.<br>
	 * <br>
	 * <u>Beispiele:</u><br>
	 * <tt>APLogger.log();</tt><br>
	 * <tt>APLogger.log("Ich bin eine Nachricht!");</tt><br>
	 * <tt>APLogger.log("Ich bin die ", i+".", "Nachricht");</tt><br>
	 * <tt>APLogger.log(new String[]{"Hallo", "User!", "Was machst du?"});</tt><br>
	 * <tt>APLogger.log("A"+"B"+"C"+"D"+"E"+"F"+"G"+"H"+"I"+"J");</tt> Das wird
	 * nicht empfohlen, stattdessen die einzelnen Teile durch Kommata trennen
	 * und den Separator korrekt angeben (Performance).
	 * 
	 * @param message
	 *            Die zu schreibende Nachricht. Diese kann leer sein, ein
	 *            String, ein String[] oder eine Aufzaehlung von Strings durch
	 *            Kommata getrennt. Ist ein Teil der Nachricht <tt>null</tt>,
	 *            wird 'null' stattdessen ausgegeben.<br>
	 */
	public static void log(String... message) {
		if (null == logger || 0 >= message.length) {
			return;
		}
		if (messageCache != null && builderCache != null
				&& messageCache.equals(message)) {
			logger.info(builderCache);
			return;
		}
		messageCache = message;
		StringBuilder builder = new StringBuilder(BUFFER_MIN_LENGTH
				+ BUFFER_MULTIPLIER * message.length);
		if(PREFIX != null && !PREFIX.isEmpty()){
			builder.append(PREFIX).append(SEPARATOR);
		}
		for (String msg : message) {
			builder.append(msg).append(SEPARATOR);
		}
		builder.append(SUFFIX);
		if (!isWarning) {
			logger.info(builderCache = builder.toString());
		} else {
			logger.warning(builderCache = builder.toString());
		}
	}

	/**
	 * Gibt eine Nachricht als Information aus.<br>
	 * Der Status wird fuer weitere Ausgaben beibehalten.
	 * 
	 * @param message
	 *            Die auszugebende Nachricht
	 */
	public static void info(String... message) {
		setWarning(false);
		log(message);
	}

	/**
	 * Gibt eine Nachricht als Warnung aus.<br>
	 * Der Status wird fuer weitere Ausgaben beibehalten.
	 * 
	 * @param message
	 *            Die auszugebende Nachricht
	 */
	public static void warn(String... message) {
		setWarning(true);
		log(message);
	}

	/**
	 * Setzt den Warnstatus
	 * 
	 * @param warning
	 *            Neuer Status fuer Ausgaben
	 */
	public static void setWarning(boolean warning) {
		isWarning = warning;
	}
}
