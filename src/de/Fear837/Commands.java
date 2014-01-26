package de.Fear837;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

import com.volcanicplaza.BukkitDev.AnimalSelector.AnimalSelector;

public class Commands implements CommandExecutor {
	
	private MySQL sql;
	
	private AnimalSelector animSel;
	
	public Commands(MySQL sql)
	{
		if (animSel == null) { animSel = getAnimalSelector(); }
	}
	
	public static AnimalSelector getAnimalSelector() {
		//Get AnimalSelector plugin
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
			try { entity = animSel.getPlayerSelectedEntity(cs.getName()); } 
			catch (Exception e) { cs.sendMessage("Es wurde kein Tier ausgewählt."); }
			
			if (entity != null)
			{
				String isAlreadyLocked = getEntityOwner(entity.getUniqueId());
				if (isAlreadyLocked == null)
				{
					addEntity(entity.getUniqueId(), cs.getName(), entity.getLocation().getBlockX(), entity.getLocation().getBlockY(), entity.getLocation().getBlockZ());
					cs.sendMessage("Das Tier wurde gesichert!");
				}
				else { cs.sendMessage("Das Tier ist bereits von " + isAlreadyLocked + "gesichert."); }
			}
			return true;
		}
		return false;
	}
	
	//res = statement.executeQuery("SELECT * FROM animalprotect WHERE entityid = '" + uuid + "';");
	public String getEntityOwner(UUID uuid)
	{
		ResultSet result = sql.get("SELECT name FROM ap_owners o INNER JOIN ap_locks l ON l.owner_id = o.id WHERE entity_id IN (SELECT id FROM ap_entities WHERE uuid = 'langer-code' LIMIT 1) LIMIT 1");
		
		if(result != null)
		{
			try { result.next(); } catch (SQLException e) { e.printStackTrace(); }
			
			try {
				if(result.getString("name") != null) {
					String ownerName = result.getString("name");
					result.close();
					return ownerName;
				}
			} catch (SQLException e) { e.printStackTrace(); }
		}
		

		return null;
	}
	
	//statement.executeUpdate("INSERT INTO animalprotect (`entityid`, `owner`, `last_x`, `last_y`, `last_z`) VALUES ('" + entityid + "', '" + Owner + "', " + x + ", " + y + ", " + z + ");");
	public void addEntity(UUID entityid, String Owner, int x, int y, int z)
	{
		//TODO
		
		//sql.write("INSERT INTO animalprotect (`entityid`, `owner`, `last_x`, `last_y`, `last_z`) VALUES ('" + entityid + "', '" + Owner + "', " + x + ", " + y + ", " + z + ");")
	}

}
