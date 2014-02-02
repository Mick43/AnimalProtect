package de.AnimalProtect.structs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.entity.Entity;

import de.AnimalProtect.Main;
import de.AnimalProtect.MySQL;

public class EntityObject implements Comparable<Object> {

	private Main plugin;
	private MySQL database;
	private Boolean connected;
	
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

	public EntityObject(Main plugin, MySQL database, UUID uniqueID, boolean loadFromDB) {
		this.plugin = plugin;
		this.database = database;
		this.uuid = uniqueID.toString();
		this.connected = false;

		if (loadFromDB) {
			update();
		}
	}
	public EntityObject(Main plugin, MySQL database, int ID, boolean loadFromDB) {
		this.plugin = plugin;
		this.database = database;
		this.id = ID;
		this.connected = false;

		if (loadFromDB) {
			update();
		}
	}

	public void update() {
		boolean failedToConnect = false;
		
		ResultSet result_Entity = null;
	    if (id != null) { result_Entity = database.get("SELECT * FROM ap_entities WHERE id="+id+";" , true, false); }
	    else if (uuid != null) { result_Entity = database.get("SELECT * FROM ap_entities WHERE uuid='" + uuid + "';", true, false); }
		
		else { 
			connected = false; 
			plugin.getLogger().info("Fehler: Keine uniqueID oder ID beim initialisieren eines EntityObjects angegeben!");
		}
		
		if (result_Entity != null) {
			try {
				id = result_Entity.getInt("id");
				uuid = result_Entity.getString("uuid");
				lastx = result_Entity.getInt("last_x");
				lasty = result_Entity.getInt("last_y");
				lastz = result_Entity.getInt("last_z");
				type = result_Entity.getString("animaltype");
				nametag = result_Entity.getString("nametag");
				maxhp = result_Entity.getDouble("maxhp");
				alive = result_Entity.getBoolean("alive");
				color = result_Entity.getString("color");
				armor = result_Entity.getString("armor");
				jumpstrength = result_Entity
						.getDouble("horse_jumpstrength");
				style = result_Entity.getString("horse_style");
				variant = result_Entity.getString("horse_variant");

				ResultSet result_owner = database
						.get("SELECT name FROM ap_owners WHERE id=(SELECT owner_id FROM ap_locks WHERE entity_id=(SELECT id FROM ap_entities WHERE uuid='"
								+ uuid + "'));", true, false);
				if (result_owner != null) {
					try {
						owner = result_owner.getString("name");
					} catch (SQLException e) {
						connected = false;
						plugin.getLogger()
								.warning(
										"Ein EntityObject konnte nicht geladen werden, weil der Ownername nicht geladen werden konnte!");
						plugin.getLogger().warning(
								"Weitere Informationen: [UUID=" + uuid
										+ "]");
						failedToConnect = true;
					}
				} else {
					connected = false;
					plugin.getLogger()
							.warning(
									"Ein EntityObject konnte nicht geladen werden, weil der Owner nicht gefunden werden konnte!");
					plugin.getLogger()
							.warning(
									"Weitere Informationen: [UUID="
											+ uuid + "]");
					failedToConnect = true;
				}
			} catch (SQLException e) {
				connected = false;
				plugin.getLogger()
						.warning(
								"Ein EntityObject konnte nicht geladen werden, weil die Entity-Eigenschaften nicht geladen werden konnten!");
				plugin.getLogger().warning(
						"Weitere Informationen: [UUID=" + uuid + "]");
				failedToConnect = true;
			}
		} else {
			connected = false;
			plugin.getLogger()
					.warning(
							"Ein EntityObject konnte nicht geladen werden, weil das Entity nicht gefunden werden konnte!");
			plugin.getLogger().warning(
					"Weitere Informationen: [UUID=" + uuid + "]");
			failedToConnect = true;
		}
		
		if (!failedToConnect) {
			connected = true;
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
	 * @return Gibt die ID des Entities in der Datenbank zurück.
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @return Gibt die uniqueID des Entities zurück.
	 */
	public String getUniqueID() {
		return uuid;
	}

	/**
	 * @return Gibt die letzte bekannte X-Koordinate des Entities zurück.
	 */
	public Integer getLastx() {
		return lastx;
	}

	/**
	 * @return Gibt die letzte bekannte Y-Koordinate des Entities zurück.
	 */
	public Integer getLasty() {
		return lasty;
	}

	/**
	 * @return Gibt die letzte bekannte Z-Koordinate des Entities zurück.
	 */
	public Integer getLastz() {
		return lastz;
	}

	/**
	 * @return Gibt den Typ des Entities zurück.
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return Gibt den CustomName des Entities zurück.
	 */
	public String getNametag() {
		return nametag;
	}

	/**
	 * @return Gibt die maximale HP des Entities zurück. (Nur bei Pferden!)
	 */
	public Double getMaxhp() {
		return maxhp;
	}

	/**
	 * @return Gibt an, ob das Entity noch am Leben ist.
	 */
	public Boolean isAlive() {
		return alive;
	}

	/**
	 * @return Gibt die Farbe des Entities zurück. (Nur bei Pferden, Wölfen, Schafen)
	 */
	public String getColor() {
		return color;
	}

	/**
	 * @return Gibt den Namen der Rüstung des Tieres zurück.  (Nur bei Pferden!)
	 */
	public String getArmor() {
		return armor;
	}

	/**
	 * @return Gibt die Sprungstärke des Entities zurück. (Nur bei Pferden!)
	 */
	public Double getJumpstrength() {
		return jumpstrength;
	}

	/**
	 * @return Gibt den Style des Entities zurück. (Nur bei Pferden!)
	 */
	public String getStyle() {
		return style;
	}

	/**
	 * @return Gibt die Variante des Entities zurück. (Nur bei Pferden!)
	 */
	public String getVariant() {
		return variant;
	}

	/**
	 * @return Gibt den Owner an, der das Entity gelockt hat.
	 */
	public String getOwner() {
		return owner;
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
