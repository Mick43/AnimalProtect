package de.Fear837.structs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.entity.Entity;

import de.Fear837.Main;
import de.Fear837.MySQL;

public class EntityObject implements Comparable<Object> {

	private Main plugin;
	private MySQL database;

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
	private String entity_owner;

	private Boolean connected;

	public EntityObject(Main plugin, MySQL database, UUID id, boolean loadFromDB) {
		this.plugin = plugin;
		this.database = database;
		this.entity_uuid = id.toString();
		this.connected = false;

		if (loadFromDB) {
			update();
		}
	}

	public void update() {
		ResultSet result_Entities = database.get(
				"SELECT * FROM ap_entities WHERE uuid='" + entity_id + "';",
				true, true);
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
				entity_jumpstrength = result_Entities
						.getDouble("horse_jumpstrength");
				entity_style = result_Entities.getString("horse_style");
				entity_variant = result_Entities.getString("horse_variant");

				ResultSet result_owner = database
						.get("SELECT name FROM ap_owners WHERE id=(SELECT owner_id FROM ap_locks WHERE entity_id=(SELECT id FROM ap_entities WHERE uuid='"
								+ entity_uuid + "'));", true, true);
				if (result_owner != null) {
					try {
						entity_owner = result_owner.getString("name");
					} catch (SQLException e) {
						connected = false;
						plugin.getLogger()
								.warning(
										"Ein EntityLock konnte nicht geladen werden, weil der Ownername nicht geladen werden konnte!");
						plugin.getLogger().warning(
								"Weitere Informationen: [UUID=" + entity_uuid
										+ "]");
					}
				} else {
					connected = false;
					plugin.getLogger()
							.warning(
									"Ein EntityLock konnte nicht geladen werden, weil der Owner nicht gefunden werden konnte!");
					plugin.getLogger()
							.warning(
									"Weitere Informationen: [UUID="
											+ entity_uuid + "]");
				}
			} catch (SQLException e) {
				connected = false;
				plugin.getLogger()
						.warning(
								"Ein EntityObject konnte nicht geladen werden, weil die Entity-Eigenschaften nicht geladen werden konnten!");
				plugin.getLogger().warning(
						"Weitere Informationen: [UUID=" + entity_uuid + "]");
			}
		} else {
			connected = false;
			plugin.getLogger()
					.warning(
							"Ein EntityLock konnte nicht geladen werden, weil das Entity nicht gefunden werden konnte!");
			plugin.getLogger().warning(
					"Weitere Informationen: [UUID=" + entity_uuid + "]");
		}
	}

	/**
	 * @return Die ID des Entities in der Datenbank wird zurückgegeben
	 */
	public Integer getEntity_id() {
		return entity_id;
	}

	/**
	 * @return Die UniqueID des Entities wird zurückgegeben
	 */
	public String getEntity_uuid() {
		return entity_uuid;
	}

	/**
	 * @return Die letzte bekannte X-Koordinate des Entities
	 */
	public Integer getEntity_lastx() {
		return entity_lastx;
	}

	/**
	 * @return Die letzte bekannte Y-Koordinate des Entities
	 */
	public Integer getEntity_lasty() {
		return entity_lasty;
	}

	/**
	 * @return Die letzte bekannte Z-Koordinate des Entities
	 */
	public Integer getEntity_lastz() {
		return entity_lastz;
	}

	/**
	 * @return Der Typ des Entities
	 */
	public String getEntity_type() {
		return entity_type;
	}

	/**
	 * @return Der CustomName des Entities
	 */
	public String getEntity_nametag() {
		return entity_nametag;
	}

	/**
	 * @return Die maximale HP des Entities (Nur bei Pferden)
	 */
	public Double getEntity_maxhp() {
		return entity_maxhp;
	}

	/**
	 * @return Gibt zurück, ob das Entity noch am leben ist
	 */
	public Boolean getEntity_alive() {
		return entity_alive;
	}

	/**
	 * @return Die Farbe des Entities (Nur bei Pferden, Wölfen, Schafen)
	 */
	public String getEntity_color() {
		return entity_color;
	}

	/**
	 * @return Gibt die Rüstung des Entities zurück (Nur bei Pferden)
	 */
	public String getEntity_armor() {
		return entity_armor;
	}

	/**
	 * @return Gibt die Sprungstärke des Entities zurück (Nur bei Pferden)
	 */
	public Double getEntity_jumpstrength() {
		return entity_jumpstrength;
	}

	/**
	 * @return Gibt den Style des Entities zurück (Nur bei Pferden)
	 */
	public String getEntity_style() {
		return entity_style;
	}

	/**
	 * @return Gibt die Variante des Entities zurück (Nur bei Pferden)
	 */
	public String getEntity_variant() {
		return entity_variant;
	}

	/**
	 * @return Gibt den Owner des Entities zurück.
	 */
	public String getEntity_owner() {
		return entity_owner;
	}

	/**
	 * @return Gibt zurück, ob die Eigenschaften des Entities erfolgreich aus
	 *         der Datenbank gelesen werden konnte.
	 */
	public Boolean getConnected() {
		return connected;
	}

	public int hashCode() {
		return entity_uuid.hashCode();
	}

	@Override
	public int compareTo(Object o) {
		if (this.equals(o))
			return 0;
		if (o instanceof EntityObject)
			return this.hashCode() > ((EntityObject) o).hashCode() ? 1 : -1;
		if (o instanceof Entity)
			return this.hashCode() > ((Entity) o).hashCode() ? 1 : -1;
		return this.hashCode() - o.hashCode();
	}

	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (o instanceof EntityObject
				&& this.hashCode() == ((EntityObject) o).hashCode())
			return true;
		if (o instanceof Entity
				&& this.hashCode() == ((Entity) o).getUniqueId().hashCode())
			return true;
		return false;
	}
}
