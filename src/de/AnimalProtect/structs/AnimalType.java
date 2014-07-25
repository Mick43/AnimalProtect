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
	UNKNOWN ("Unknown", EntityType.UNKNOWN),
	COW ("Cow", EntityType.COW),
	CHICKEN ("Chicken", EntityType.CHICKEN),
	PIG ("Pig", EntityType.PIG),
	SHEEP ("Sheep", EntityType.SHEEP),
	HORSE ("Horse", EntityType.HORSE),
	IRON_GOLEM ("IronGolem", EntityType.IRON_GOLEM),
	SNOWMAN ("Snowman", EntityType.SNOWMAN),
	VILLAGER ("Villager", EntityType.VILLAGER),
	OCELOT ("Ocelot", EntityType.OCELOT),
	WOLF ("Wolf", EntityType.WOLF);
	
	private String name;
	private EntityType entity;
	
	private AnimalType(final String name, final EntityType entity) {
		this.name = name;
		this.entity = entity;
	}
	
	public String getName() {
		return this.name;
	}
	
	public EntityType getEntity() {
		return this.entity;
	}
	
	public static boolean contains(final String value) {
		for (final AnimalType type : AnimalType.values()) {
			if (type.getName().equalsIgnoreCase(value)) { return true; }
		}
		return false;
	}
}
