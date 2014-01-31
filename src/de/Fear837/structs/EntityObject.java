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

	private Boolean connected;

	public EntityObject(Main plugin, MySQL database, UUID id, boolean loadFromDB) {
		this.plugin = plugin;
		this.database = database;
		this.uuid = id.toString();
		this.connected = false;

		if (loadFromDB) {
			update();
		}
	}

	public void update() {
		ResultSet result_Entities = database.get(
				"SELECT * FROM ap_entities WHERE uuid='" + uuid + "';",
				true, true);
		if (result_Entities != null) {
			try {
				id = result_Entities.getInt("id");
				uuid = result_Entities.getString("uuid");
				lastx = result_Entities.getInt("last_x");
				lasty = result_Entities.getInt("last_y");
				lastz = result_Entities.getInt("last_z");
				type = result_Entities.getString("animaltype");
				nametag = result_Entities.getString("nametag");
				maxhp = result_Entities.getDouble("maxhp");
				alive = result_Entities.getBoolean("alive");
				color = result_Entities.getString("color");
				armor = result_Entities.getString("armor");
				jumpstrength = result_Entities
						.getDouble("horse_jumpstrength");
				style = result_Entities.getString("horse_style");
				variant = result_Entities.getString("horse_variant");

				ResultSet result_owner = database
						.get("SELECT name FROM ap_owners WHERE id=(SELECT owner_id FROM ap_locks WHERE entity_id=(SELECT id FROM ap_entities WHERE uuid='"
								+ uuid + "'));", true, true);
				if (result_owner != null) {
					try {
						owner = result_owner.getString("name");
					} catch (SQLException e) {
						connected = false;
						plugin.getLogger()
								.warning(
										"Ein EntityLock konnte nicht geladen werden, weil der Ownername nicht geladen werden konnte!");
						plugin.getLogger().warning(
								"Weitere Informationen: [UUID=" + uuid
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
											+ uuid + "]");
				}
			} catch (SQLException e) {
				connected = false;
				plugin.getLogger()
						.warning(
								"Ein EntityObject konnte nicht geladen werden, weil die Entity-Eigenschaften nicht geladen werden konnten!");
				plugin.getLogger().warning(
						"Weitere Informationen: [UUID=" + uuid + "]");
			}
		} else {
			connected = false;
			plugin.getLogger()
					.warning(
							"Ein EntityLock konnte nicht geladen werden, weil das Entity nicht gefunden werden konnte!");
			plugin.getLogger().warning(
					"Weitere Informationen: [UUID=" + uuid + "]");
		}
	}


	
	/**
	 * @return Gibt zurück, ob die Eigenschaften des Entities erfolgreich aus
	 *         der Datenbank gelesen werden konnte.
	 */
	public Boolean isConnected() {
		return connected;
	}

	public int hashCode() {
		return uuid.hashCode();
	}
	
	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @return the uuid
	 */
	public String getUniqueID() {
		return uuid;
	}

	/**
	 * @return the lastx
	 */
	public Integer getLastx() {
		return lastx;
	}

	/**
	 * @return the lasty
	 */
	public Integer getLasty() {
		return lasty;
	}

	/**
	 * @return the lastz
	 */
	public Integer getLastz() {
		return lastz;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return the nametag
	 */
	public String getNametag() {
		return nametag;
	}

	/**
	 * @return the maxhp
	 */
	public Double getMaxhp() {
		return maxhp;
	}

	/**
	 * @return the alive
	 */
	public Boolean getAlive() {
		return alive;
	}

	/**
	 * @return the color
	 */
	public String getColor() {
		return color;
	}

	/**
	 * @return the armor
	 */
	public String getArmor() {
		return armor;
	}

	/**
	 * @return the jumpstrength
	 */
	public Double getJumpstrength() {
		return jumpstrength;
	}

	/**
	 * @return the style
	 */
	public String getStyle() {
		return style;
	}

	/**
	 * @return the variant
	 */
	public String getVariant() {
		return variant;
	}

	/**
	 * @return the owner
	 */
	public String getOwner() {
		return owner;
	}

	private Integer id;
	private String uuid;
	private Integer lastx;
	private Integer lasty;
	private Integer lastz;
	private String type;
	private String nametag;
	private Double maxhp;
	private Boolean alive;
	private String color;
	private String armor;
	private Double jumpstrength;
	private String style;
	private String variant;
	private String owner;

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
