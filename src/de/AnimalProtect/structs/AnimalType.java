package de.AnimalProtect.structs;

import org.bukkit.entity.EntityType;

/**
 * Beschreibt den Typ eines Tieres und Entities. <br>
 * Diese Enumeration besteht aus fast allen freundlichen EntityType's. <br>
 * 
 * <b> Verfügbare Typen: </b> <br>
 * '<code>UNKNOWN</code>' <br>
 * '<code>COW</code>' <br>
 * '<code>CHICKEN</code>' <br>
 * '<code>PIG</code>' <br>
 * '<code>SHEEP</code>' <br>
 * '<code>HORSE</code>' <br>
 * '<code>IRON_GOLEM</code>' <br>
 * '<code>SNOWMAN</code>'
 * '<code>VILLAGER</code>' <br>
 * '<code>OCELOT</code>' <br>
 * '<code>WOLF</code>' <br>
 * 
 * @see Animal
 * @see EntityType
 */
public enum AnimalType {
	/** Ein unbekannter Typ. */
	UNKNOWN ("Unknown", EntityType.UNKNOWN),
	/** Eine Kuh. */
	COW ("Cow", EntityType.COW),
	/** Ein Huhn. */
	CHICKEN ("Chicken", EntityType.CHICKEN),
	/** Ein Schwein. */
	PIG ("Pig", EntityType.PIG),
	/** Ein Schaf. */
	SHEEP ("Sheep", EntityType.SHEEP),
	/** Ein Pferd. */
	HORSE ("Horse", EntityType.HORSE),
	/** Ein Eisengolem. */
	IRON_GOLEM ("IronGolem", EntityType.IRON_GOLEM),
	/** Ein Schneemann. */
	SNOWMAN ("Snowman", EntityType.SNOWMAN),
	/** Ein Villager. */
	VILLAGER ("Villager", EntityType.VILLAGER),
	/** Ein Ozelot. */
	OCELOT ("Ocelot", EntityType.OCELOT),
	/** Ein Wolf. */
	WOLF ("Wolf", EntityType.WOLF);

	/** Der Name des Tieres. */
	private String name;
	/** Der {@link EntityType} des Tieres. */
	private EntityType entity;

	/**
	 * Initialisiert ein neues AnimalType.
	 * @param name - Der Name des Types.
	 * @param entity - Der {@link EntityType} des Types.
	 */
	private AnimalType(final String name, final EntityType entity) {
		this.name = name;
		this.entity = entity;
	}

	/**
	 * @return Den Namen des Types.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return Den {@link EntityType} des Types.
	 */
	public EntityType getEntity() {
		return this.entity;
	}

	/**
	 * Prüft ob für den übergebenen String ein AnimalType existiert.
	 * @param value - Der übergebene String.
	 * @return True, wenn dafür ein AnimalType existiert.
	 */
	public static boolean contains(final String value) {
		for (final AnimalType type : AnimalType.values()) {
			if (type.getName().equalsIgnoreCase(value)) { return true; }
		}
		return false;
	}
}