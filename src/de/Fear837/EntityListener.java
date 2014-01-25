package de.Fear837;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public final class EntityListener implements Listener{
	
	MySQL sql;
	
	Connection c;
	
	public EntityListener(MySQL sql, Connection c)
	{
		this.sql = sql;
		this.c = c;
	}
	
	DamageCause entityAttack = DamageCause.ENTITY_ATTACK;
	DamageCause entityProjectile = DamageCause.PROJECTILE;
	
	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent event)
	{
		if (sql != null)
		{
			EntityType entityType = event.getEntityType();
			if (entityType == EntityType.COW || entityType == EntityType.PIG || entityType == EntityType.SHEEP || entityType == EntityType.CHICKEN || entityType == EntityType.HORSE || entityType == EntityType.WOLF)
			{
				if (!event.isCancelled() && event.getDamager() instanceof Player){
					Entity entity = event.getEntity();
					
				}
			}
		}
	}
	
	public String getEntityOwner(int entityID)
	{
		Statement statement = null;
		try {
			statement = c.createStatement();
			
			ResultSet res;
			try {
				res = statement.executeQuery("SELECT * FROM tokens WHERE entityid = '" + entityID + "';");
				res.next();
				
				if(res.getString("owner") != null) {
					return res.getString("owner");
				}
			} catch (SQLException e) { e.printStackTrace(); }
		} catch (SQLException e1) { e1.printStackTrace(); }

		return null;
	}
}
