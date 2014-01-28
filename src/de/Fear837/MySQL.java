package de.Fear837;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import org.bukkit.plugin.Plugin;

/**
 * Connects to and uses a MySQL database
 * 
 * @author -_Husky_-
 * @author tips48
 */
public class MySQL extends Database {
    private final String user;
    private final String database;
    private final String password;
    private final String port;
    private final String hostname;
    
    private Plugin plugin;
    private Connection connection;

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
     */
    public MySQL(Plugin plugin, String hostname, String port, String database, String username, String password) {
        super(plugin);
        this.hostname = hostname;
        this.port = port;
        this.database = database;
        this.user = username;
        this.password = password;
        this.plugin = plugin;
        this.connection = null;
    }

    @Override
    public Connection openConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + this.hostname + ":" + this.port + "/" + this.database, this.user, this.password);
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not connect to MySQL server! because: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            plugin.getLogger().log(Level.SEVERE, "JDBC Driver not found!");
        }
        return connection;
    }

    @Override
    public boolean checkConnection() {
        return connection != null;
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Error closing the MySQL Connection!");
                e.printStackTrace();
            }
        }
    }

    public ResultSet querySQL(String query) {
        Connection c = null;

        if (checkConnection()) {
            c = getConnection();
        } else {
            c = openConnection();
        }

        Statement s = null;

        try {
            s = c.createStatement();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }

        ResultSet ret = null;

        try {
            ret = s.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        closeConnection();

        return ret;
    }

    public void updateSQL(String update) {

        Connection c = null;

        if (checkConnection()) {
            c = getConnection();
        } else {
            c = openConnection();
        }

        Statement s = null;

        try {
            s = c.createStatement();
            s.executeUpdate(update);
        } catch (SQLException e1) {
            e1.printStackTrace();
        }

        closeConnection();

    }
    
    
    
	public ResultSet get(String Query, boolean next)
	{
		Statement statement = null;
		ResultSet res = null;
		
		if (plugin.getConfig().getBoolean("settings.debug-messages")) {
			plugin.getLogger().info("Querying: " + Query);
		}
		
		try { statement = connection.createStatement(); } 
		catch (SQLException e) {
			plugin.getLogger().warning("Exception in MySQL.get() -> statement = connection.createStatement()");
			e.printStackTrace();
			return null;
		}
		
		try { res = statement.executeQuery(Query); } 
		catch (SQLException e) {
			plugin.getLogger().warning("Exception in MySQL.get() -> res = statement.executeQuery(Query)");
			e.printStackTrace();
			return null;
		}
		
		if (next) {
			try { if (!res.next()) { return null; } } 
			catch (SQLException e) {
				plugin.getLogger().warning("Exception in MySQL.get() -> res.next()");
				plugin.getLogger().warning("Query: " + Query);
				e.printStackTrace();
				return null;
			}
		}
		
		return res;
	}
	
	public Object getValue(ResultSet result, String columnLabel) {
		Object v = null;
		try { v = result.getObject(columnLabel); } 
		catch (SQLException e) { return null; }
		
		return v;
	}
	
	public boolean write(String Query)
	{
		Statement statement = null;
		try {
			statement = connection.createStatement();
			try {
				if (plugin.getConfig().getBoolean("settings.debug-messages")) {
					plugin.getLogger().info("Inserting: " + Query);
				}
				statement.executeUpdate(Query);
				return true;
			} catch (SQLException e) { e.printStackTrace(); }
		} catch (SQLException e1) { e1.printStackTrace(); }
		return false;
	}

}