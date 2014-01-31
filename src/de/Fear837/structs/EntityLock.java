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

	private Integer entity_id;
	private String entity_uuid;
	private Integer entity_lastx;
	private Integer entity_lasty;
	private Integer entity_lastz;
	private String entity_type;
	private String entity_nametag;
	private Double entity_maxhp;
	private Boolean entity_alive;
	private String entity_color;
	private String entity_armor;
	private Double entity_jumpstrength;
	private String entity_style;
	private String entity_variant;
	
	private boolean connected;
	
	public EntityLock(Main plugin, MySQL database, String playername, UUID uuid) {
		this.database = database;
		this.plugin = plugin;
		this.player_name = playername;
		this.entity_uuid = uuid.toString();
		
		update();
	}

	public String getPlayer_name() {
		return player_name;
	}

	public Integer getPlayer_id() {
		return player_id;
	}

	public Integer getEntity_id() {
		return entity_id;
	}

	public String getEntity_uuid() {
		return entity_uuid;
	}

	public Integer getEntity_lastx() {
		return entity_lastx;
	}

	public Integer getEntity_lasty() {
		return entity_lasty;
	}

	public Integer getEntity_lastz() {
		return entity_lastz;
	}

	public String getEntity_type() {
		return entity_type;
	}

	public String getEntity_nametag() {
		return entity_nametag;
	}

	public Double getEntity_maxhp() {
		return entity_maxhp;
	}

	public Boolean getEntity_alive() {
		return entity_alive;
	}

	public String getEntity_color() {
		return entity_color;
	}

	public String getEntity_armor() {
		return entity_armor;
	}

	public Double getEntity_jumpstrength() {
		return entity_jumpstrength;
	}

	public String getEntity_style() {
		return entity_style;
	}

	public String getEntity_variant() {
		return entity_variant;
	}
	
	public boolean isConnected() {
		return connected;
	}
	
	public void update() {
		connected = true;
		
		try { player_id = (Integer) database.getValue("SELECT id FROM ap_owners WHERE name='" + player_name + "';", "id", true); }
		catch (Exception e1) { e1.printStackTrace(); }
		
		ResultSet result_Entities = database.get("SELECT * FROM ap_entities WHERE uuid='" + entity_id + "';", true, true);
		if (result_Entities != null) {
			try {
				entity_id = result_Entities.getInt("id");
				entity_uuid = result_Entities.getString("uuid");
				entity_lastx = result_Entities.getInt("last_x");
				entity_lasty = result_Entities.getInt("last_y");
				entity_lastz = result_Entities.getInt("last_z");
				entity_type = result_Entities.getString("animaltype");
				entity_nametag = result_Entities.getString("nametag");
				entity_maxhp = result_Entities.getDouble("maxhp");
				entity_alive = result_Entities.getBoolean("alive");
				entity_color = result_Entities.getString("color");
				entity_armor = result_Entities.getString("armor");
				entity_jumpstrength = result_Entities.getDouble("horse_jumpstrength");
				entity_style = result_Entities.getString("horse_style");
				entity_variant = result_Entities.getString("horse_variant");
			} catch (SQLException e) {
				connected = false;
				plugin.getLogger().warning("Ein EntityLock konnte nicht geladen werden, weil die Entity-Eigenschaften nicht geladen werden konnten!");
				plugin.getLogger().warning("Weitere Informationen: [Playername=" + player_name + "] [UUID=" + entity_uuid + "]");
			}
		}
		else { 
			connected = false;
			plugin.getLogger().warning("Ein EntityLock konnte nicht geladen werden, weil das Entity nicht gefunden werden konnte!");
			plugin.getLogger().warning("Weitere Informationen: [Playername=" + player_name + "] [UUID=" + entity_uuid + "]");
		}
		if (player_id == null) {
			connected = false;
			plugin.getLogger().warning("Ein EntityLock konnte nicht geladen werden, weil der Spieler nicht gefunden werden konnte!");
			plugin.getLogger().warning("Weitere Informationen: [Playername=" + player_name + "] [UUID=" + entity_uuid + "]");
		}
	}
}
