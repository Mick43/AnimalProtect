package de.Fear837;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.event.Listener;

import com.volcanicplaza.BukkitDev.AnimalSelector.AnimalSelector;

public class Commands implements CommandExecutor, Listener {

	private Main plugin;
	
	private Connection c;
	
	private String reason = "rieson";
	
	private AnimalSelector animSel;
	
	public Commands(Main plugin, Connection c, AnimalSelector animSel)
	{
		this.plugin = plugin;
		this.c = c;
		this.animSel = animSel;
	}
	
	public static AnimalSelector getAnimalSelector() {
		//Get AnimalSelector plugin for later on use.
		AnimalSelector plugin = (AnimalSelector) Bukkit.getServer().getPluginManager().getPlugin("AnimalSelector");
		
		if (plugin == null || !(plugin instanceof AnimalSelector)) {
	        Bukkit.getLogger().info("[WARNING] AnimalSelector isn't loaded yet.");
	        return null;
	    }
	    return (AnimalSelector) plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if (animSel == null) { animSel = getAnimalSelector(); }
		
		if (cmd.getName().equalsIgnoreCase("lockanimal"))
		{
			Entity entity = null;
			try {
				entity = animSel.getPlayerSelectedEntity(cs.getName());
			} catch (Exception e) {
				cs.sendMessage("Es wurde kein Tier ausgewählt.");
			}
			
			if (entity != null)
			{
				String isAlreadyLocked = getEntityOwner(entity.getUniqueId());
				if (isAlreadyLocked == null)
				{
					addEntity(entity.getUniqueId(), cs.getName(), entity.getLocation().getBlockX(), entity.getLocation().getBlockY(), entity.getLocation().getBlockZ());
					cs.sendMessage("Das Tier wurde gesichert!");
				}
				else { cs.sendMessage("Das Tier ist bereits gesichert."); }
			}
			return true;
		}
		return false;
	}
	
	public String getEntityOwner(UUID uuid)
	{
		Statement statement = null;
		try {
			statement = c.createStatement();
			
			ResultSet res;
			try {
				res = statement.executeQuery("SELECT * FROM animalprotect WHERE entityid = '" + uuid + "';");
				res.next();
				
				if(res.getString("owner") != null) {
					return res.getString("owner");
				}
			} catch (SQLException e) { e.printStackTrace(); }
		} catch (SQLException e1) { e1.printStackTrace(); }

		return null;
	}
	
	public void addEntity(UUID entityid, String Owner, int x, int y, int z)
	{
		Statement statement = null;
		try {
			statement = c.createStatement();
			
			try {
				statement.executeUpdate("INSERT INTO animalprotect (`entityid`, `owner`) VALUES ('" + entityid + "', '" + Owner + "', '" + x + "', '" + y + "', '" + z + "');");
			} catch (SQLException e) { e.printStackTrace(); }
			System.out.println("Inserted info");
		} catch (SQLException e1) { e1.printStackTrace(); }
	}

}
