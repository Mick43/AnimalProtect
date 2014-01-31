package de.Fear837.structs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import de.Fear837.Main;
import de.Fear837.MySQL;

public class EntityLock {
	
	private MySQL database;
	private Main plugin;
	
	private String player_name;
	private Integer player_id;
	private EntityObject entity;
	
	private boolean connected;
	
	public EntityLock(Main plugin, MySQL database, String playername, EntityObject entity) {
		this.database = database;
		this.plugin = plugin;
		this.player_name = playername;
		this.entity = entity;
	}

	public String getPlayer_name() {
		return player_name;
	}

	public Integer getPlayer_id() {
		return player_id;
	}
	
	public EntityObject getEntity() {
		return entity;
	}
	
	public boolean isConnected() {
		return connected;
	}
	
	
}
