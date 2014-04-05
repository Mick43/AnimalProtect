package de.AnimalProtectOld;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;

import org.bukkit.plugin.Plugin;

import de.AnimalProtectOld.utility.APLogger;

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
    private Boolean debug;
    
    public ArrayList<String> failedQueries;

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
    public MySQL(Plugin plugin, String hostname, String port, String database, String username, String password, Boolean debug) {
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
            APLogger.info("[MySQL/openConnection] Connecting to jdbc:mysql://" + this.hostname + ":" + this.port + "/" + this.database + " ...");
            connection = DriverManager.getConnection("jdbc:mysql://" + this.hostname + ":" + this.port + "/" + this.database, this.user, this.password);
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "[MySQL] Could not connect to MySQL server! because: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            plugin.getLogger().log(Level.SEVERE, "[MySQL] JDBC Driver not found!");
        }
        return connection;
    }

    /**
     * Checks if the connection is open. Returns null if not
     */
    public boolean checkConnection() {
    	try {
    		if (connection == null) { return false; }
            else if (connection.isClosed()) { return false; }
            else if (!connection.isValid(2)) { return false; }
    	}
    	catch (Exception e) { return false; }
    	
    	return true;
    }

    /**
     * Returns the connection
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Closes the MySQL-Connection
     */
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
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
    public void createTable(String tableName, String[] columns, Boolean log) {
    	String Query = "CREATE TABLE IF NOT EXISTS " + tableName + " (";
    	
    	for (String column : columns) {
    		Query += column;
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
	public ResultSet get(String Query, boolean next, boolean log)
	{
		/* Prüfen ob die MySQL-Verbindung besteht. */
		/* Wenn nicht, dann neu connecten.         */
		try {
			if (connection.isClosed() || !this.checkConnection()) 
			{ this.openConnection(); }
		}
		catch (Exception e) { }
		
		/* Prüfen ob der Verbindungsaufbau funktioniert hat */
		if (!checkConnection()) { noConnection(); return null; }
		
		/* Das Statement und das ResultSet erstellen */
		Statement statement = null;
		ResultSet res = null;
		
		/* In der Konsole Informationen ausgeben */
		if (debug && log) {
			APLogger.info("[MySQL/get] Executing: | " + Query + " |");
		}
		
		/* Das Statement erstellen */
		try { statement = connection.createStatement(); } 
		catch (SQLException e) {
			this.error("MySQL/get", "Exception in 'statement = connection.createStatement()' ", e);
			return null;
		}
		
		/* Die Query ausführen */
		try { res = statement.executeQuery(Query); } 
		catch (SQLException e) {
			this.error("MySQL/get", "Exception in 'statement = connection.createStatement()' ", e);
			return null;
		}
		
		/* Prüfen ob next() gemacht werden soll */
		if (next) {
			try { if (!res.next()) { return null; } } 
			catch (SQLException e) {
				this.error("MySQL/get", "Exception in 'res.next()' ", e);
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
	public int getResultSize(ResultSet result) {
		int rows = 0;
		
		try {
			result.last();
			rows = result.getRow();
			result.beforeFirst();
		}
		catch (Exception e) { return 0; }
		
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
	public Object getValueFromResult(ResultSet result, String columnLabel) {
		Object object = null;
		
		try { object = result.getObject(columnLabel); } 
		catch (SQLException e) { return null; }
		
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
	public Object getValueFromResult(ResultSet result, int columnID) {
		Object object = null;
		
		try { object = result.getObject(columnID); } 
		catch (SQLException e) { return null; }
		
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
	public Object getValue(String Query, String columnLabel, Boolean log) {
		ResultSet result = get(Query, true, log);
		
		if (result == null) { return null; }
		
		try {
			Object returnObject = result.getObject(columnLabel);
			return returnObject;
		} 
		catch (SQLException e) { }
		
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
	public Object getValue(String Query, int columnID, Boolean log) {
		ResultSet result = get(Query, true, log);
		
		if (result == null) { return null; }
		
		try {
			Object returnObject = result.getObject(columnID);
			return returnObject;
		} 
		catch (SQLException e) { }
		
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
	public boolean execute(String Query, Boolean log)
	{
		/* Prüfen ob eine Datenbank-Verbindung besteht */
		if (!checkConnection()) {
			openConnection();
		}
		
		if (checkConnection()) {
			/* Das Statement erstellen */
			Statement statement = null;
			
			try { statement = connection.createStatement(); } 
			catch (SQLException e1) { this.error("MySQL/execute", "An error occured while creating the statement!", e1); }
			
			/* Konsole benachrichtigen */
			if (debug && log)
			{ APLogger.info("[MySQL/execute] Executing: | " + Query + " |"); }
			
			try 
			{
				/* Das Statement ausführen */
				statement.executeUpdate(Query);
				
				return true;
			}
			catch (SQLException e) { 
				error("MySQL/execute", "Could not execute a Query.", e); 
				failedQueries.add(Query);
			}
		} 
		else { noConnection(); failedQueries.add(Query); }
		
		return false;
	}
	
	private void noConnection() {
		String isNull = "";
		String isClosed = "";
		String isValid = "";
		
		if (connection == null) { isNull = "[NULL: true]"; }
		else { 
			isNull = "[NULL: false]";
			
			try {
				if (connection.isClosed()) { isClosed = "[Closed: true]"; }
				else { isClosed = "[Closed = false]"; }
			} 
			catch (SQLException e) { isClosed = "[Closed: true]"; }
			
			try {
				if (connection.isValid(2)) { isValid = "[Valid: true]"; }
				else { isValid = "[Valid: false]"; }
			} 
			catch (SQLException e) { isValid = "[Valid: false]"; }
		}
		
		APLogger.warn("[Error/MySQL/noConnection] Warning: Couldn't connect to the database.");
		APLogger.warn("[Error/MySQL/noConnection] More Information: " + isNull + " " + isClosed + " " + isValid);
	}

	private void error(String Source, String Info, Exception e) {
		APLogger.warn("["+Source+"] An error has occurred while interacting with the database.");
		APLogger.warn("["+Source+"] More Information: " + Info);
		APLogger.warn("--- Exception Stacktrace ---");
		e.printStackTrace();
		APLogger.warn(" ");
	}
}