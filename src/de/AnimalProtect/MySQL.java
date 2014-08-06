package de.AnimalProtect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;

import org.bukkit.plugin.Plugin;

/**
 * Connects to and uses a MySQL database
 * 
 * @author -_Husky_-
 * @author tips48
 */
public class MySQL {
	private final String user;
	private final String database;
	private final String password;
	private final String port;
	private final String hostname;

	private Plugin plugin;
	private Connection connection;
	private final Boolean debug;

	private final ArrayList<String> failedQueries;

	/**
	 * Creates a new MySQL instance
	 * 
	 * @param plugin
	 *            Plugin instance
	 * @param hostname
	 *            Name of the host
	 * @param port
	 *            Port number
	 * @param database
	 *            Database name
	 * @param username
	 *            Username
	 * @param password
	 *            Password
	 * @param debug
	 *            debug
	 */
	public MySQL(final Plugin plugin, final String hostname, final String port, final String database, final String username, final String password, final Boolean debug) {
		this.plugin = plugin;
		this.hostname = hostname;
		this.port = port;
		this.database = database;
		this.user = username;
		this.password = password;
		this.plugin = plugin;
		this.connection = null;
		this.debug = debug;
		this.failedQueries = new ArrayList<String>();
	}

	/**
	 * Opens the MySQL-Connection
	 */
	public Connection openConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			this.plugin.getLogger().info("[MySQL/openConnection] Connecting to jdbc:mysql://" + this.hostname + ":" + this.port + "/" + this.database + " ...");
			this.connection = DriverManager.getConnection("jdbc:mysql://" + this.hostname + ":" + this.port + "/" + this.database + "?allowMultiQueries=true", this.user, this.password);
		} 
		catch (final SQLException e)
		{ this.plugin.getLogger().log(Level.SEVERE, "[MySQL] Could not connect to MySQL server! because: " + e.getMessage()); } 
		catch (final ClassNotFoundException e)
		{ this.plugin.getLogger().log(Level.SEVERE, "[MySQL] JDBC Driver not found!"); }

		return this.connection;
	}

	/**
	 * Checks if the connection is open. Returns null if not
	 */
	public boolean checkConnection() {
		try {
			if (this.connection == null) { return false; }
			else if (this.connection.isClosed()) { return false; }
			else if (!this.connection.isValid(2)) { return false; }
		}
		catch (final Exception e) { return false; }

		return true;
	}

	/**
	 * Returns the connection
	 */
	public Connection getConnection() {
		return this.connection;
	}

	/**
	 * Closes the MySQL-Connection
	 */
	public void closeConnection() {
		if (this.connection != null) {
			try {
				this.connection.close();
			} catch (final SQLException e) {
				this.error("MySQL/closeConnection", "An error occured while closing the connection!", e);
			}
		}
	}

	/**
	 * Creates a table with the given columns
	 * 
	 * @param tableName
	 *            How the table should be named
	 * @param columns
	 *            An array of the names of the columns.
	 * @param log
	 *            True, for Console-Output
	 */
	public void createTable(final String tableName, final String[] columns, final Boolean log) {
		String Query = "CREATE TABLE IF NOT EXISTS " + tableName + " (";

		for (final String column : columns) {
			if (column != null && !column.equalsIgnoreCase("")) {
				Query += column;

				if (!column.equalsIgnoreCase(columns[columns.length-1])) {
					Query += ", ";
				}
			}
		}

		Query += ");";

		this.execute(Query, log);
	}

	/**
	 * Returns the ResultSet from the Query.
	 * 
	 * @param Query
	 *            The Query that will be executed
	 * @param next
	 *            True, if ResultSet.next() should be executed.
	 * @param log
	 *            True, for Console-Output
	 */
	public ResultSet getResult(final String Query, final boolean next, final boolean log)
	{
		/* Prüfen ob die MySQL-Verbindung besteht. */
		/* Wenn nicht, dann neu connecten.         */
		try {
			if (this.connection.isClosed() || !this.checkConnection()) 
			{ this.openConnection(); }
		}
		catch (final Exception e) { }

		/* Prüfen ob der Verbindungsaufbau funktioniert hat */
		if (!this.checkConnection()) { this.noConnection(); return null; }

		/* Das Statement und das ResultSet erstellen */
		Statement statement = null;
		ResultSet res = null;

		/* In der Konsole Informationen ausgeben */
		if (this.debug && log) {
			this.plugin.getLogger().info("[MySQL/get] Executing: | " + Query + " |");
		}

		/* Das Statement erstellen */
		try { statement = this.connection.createStatement(); } 
		catch (final SQLException e) {
			this.error("MySQL/get", "Exception caught while trying to create an statement!' ", e);
			return null;
		}

		/* Die Query ausführen */
		try { res = statement.executeQuery(Query); } 
		catch (final SQLException e) {
			this.error("MySQL/get", "Exception caught while trying to execute a query!' ", e);
			return null;
		}

		/* Prüfen ob next() gemacht werden soll */
		if (next) {
			try { if (!res.next()) { return null; } } 
			catch (final SQLException e) {
				this.error("MySQL/get", "Exception caught while trying to run ResultSet.next!' ", e);
				return null;
			}
		}

		return res;
	}

	/**
	 * Returns the size of the given ResultSet.
	 * 
	 * @param result
	 *            The ResultSet
	 * @return Returns the size of the given ResultSet
	 */
	public int getResultSize(final ResultSet result) {
		int rows = 0;

		try {
			result.last();
			rows = result.getRow();
			result.beforeFirst();
		}
		catch (final Exception e) { return 0; }

		return rows;
	}

	/**
	 * Returns the specified value from the ResultSet.
	 * 
	 * @param result
	 *            The ResultSet
	 * @param columnLabel
	 *            The column of which the value is to be returned  
	 * @return Returns an Object
	 */
	public Object getValueFromResult(final ResultSet result, final String columnLabel) {
		Object object = null;

		try { object = result.getObject(columnLabel); } 
		catch (final SQLException e) { return null; }

		return object;
	}

	/**
	 * Returns the specified value from the ResultSet.
	 * 
	 * @param result
	 *            The ResultSet
	 * @param columnID
	 *            The ID of the column of which the value is to be returned  
	 * @return Returns an Object
	 */
	public Object getValueFromResult(final ResultSet result, final int columnID) {
		Object object = null;

		try { object = result.getObject(columnID); } 
		catch (final SQLException e) { return null; }

		return object;
	}

	/**
	 * Returns the specified value from the database.
	 * 
	 * @param Query
	 *            The Query which should return the wanted Value
	 * @param columnLabel
	 *            The column of which the value is to be returned  
	 * @param log
	 *            A Boolean wether a message should show up in the console.  
	 * @return Returns an Object
	 */
	public Object getValue(final String Query, final String columnLabel, final Boolean log) {
		final ResultSet result = this.getResult(Query, true, log);

		if (result == null) { return null; }

		try {
			final Object returnObject = result.getObject(columnLabel);
			return returnObject;
		} 
		catch (final SQLException e) { }

		return null;
	}

	/**
	 * Returns the specified value from the database.
	 * 
	 * @param Query
	 *            The Query which should return the wanted Value
	 * @param columnID
	 *            The ID of the column of which the value is to be returned  
	 * @param log
	 *            A Boolean wether a message should show up in the console.  
	 * @return Returns an Object
	 */
	public Object getValue(final String Query, final int columnID, final Boolean log) {
		final ResultSet result = this.getResult(Query, true, log);

		if (result == null) { return null; }

		try {
			final Object returnObject = result.getObject(columnID);
			return returnObject;
		} 
		catch (final SQLException e) { }

		return null;
	}

	/**
	 * Executes the given Query.
	 * 
	 * @param Query
	 *            The Query that should be executed
	 * @param log
	 *            A Boolean wether a message should show up in the console.  
	 */
	public boolean execute(final String Query, final Boolean log)
	{
		/* Prüfen ob eine Datenbank-Verbindung besteht */
		if (!this.checkConnection()) {
			this.openConnection();
		}

		if (this.checkConnection()) {
			/* Das Statement erstellen */
			Statement statement = null;

			try { statement = this.connection.createStatement(); } 
			catch (final SQLException e1) { this.error("MySQL/execute", "An error occured while creating the statement!", e1); }

			/* Konsole benachrichtigen */
			if (this.debug && log)
			{ this.plugin.getLogger().info("[MySQL/execute] Executing: | " + Query + " |"); }

			try 
			{
				/* Das Statement ausführen */
				statement.executeUpdate(Query);

				return true;
			}
			catch (final SQLException e) { 
				this.error("MySQL/execute", "Could not execute a Query.", e); 
				this.failedQueries.add(Query);
			}
		} 
		else { this.noConnection(); this.failedQueries.add(Query); }

		return false;
	}

	public ArrayList<String> getFailedQueries() {
		return this.failedQueries;
	}

	private void noConnection() {
		String isNull = "";
		String isClosed = "";
		String isValid = "";

		if (this.connection == null) { isNull = "[NULL: true]"; }
		else { 
			isNull = "[NULL: false]";

			try {
				if (this.connection.isClosed()) { isClosed = "[Closed: true]"; }
				else { isClosed = "[Closed = false]"; }
			} 
			catch (final SQLException e) { isClosed = "[Closed: true]"; }

			try {
				if (this.connection.isValid(2)) { isValid = "[Valid: true]"; }
				else { isValid = "[Valid: false]"; }
			} 
			catch (final SQLException e) { isValid = "[Valid: false]"; }
		}

		this.plugin.getLogger().warning("[Error/MySQL/noConnection] Warning: Couldn't connect to the database.");
		this.plugin.getLogger().warning("[Error/MySQL/noConnection] More Information: " + isNull + " " + isClosed + " " + isValid);
	}

	private void error(final String Source, final String Info, final Exception e) {
		if (e == null) { return; }

		this.plugin.getLogger().warning("---------------------------- "+this.plugin.getName()+" Exception! ------------------------");
		this.plugin.getLogger().warning("An Exception occured in animalprotect/" + Source);
		this.plugin.getLogger().warning("More Information: " + Info);
		this.plugin.getLogger().warning("Exception: " + e.getClass().getName());
		if (e.getMessage() != null) { this.plugin.getLogger().warning("Description: " + e.getMessage()); }
		this.plugin.getLogger().warning("---------------------------- Exception Stacktrace ----------------------------");
		for (final StackTraceElement s : e.getStackTrace()) {
			this.plugin.getLogger().warning(" -> " +s.getClassName()+"."+s.getMethodName()+" -> Line: "+s.getLineNumber());
		}
		this.plugin.getLogger().warning("-------------------------- Exception Stacktrace End --------------------------");
	}
}