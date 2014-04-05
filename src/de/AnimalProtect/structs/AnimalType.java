package de.AnimalProtect.structs;

import org.bukkit.entity.EntityType;

public enum AnimalType {
	UNKNOWN ("Unknown", EntityType.UNKNOWN),
	COW ("Cow", EntityType.COW),
	CHICKEN ("Chicken", EntityType.CHICKEN),
	PIG ("Pig", EntityType.PIG),
	SHEEP ("Sheep", EntityType.SHEEP),
	HORSE ("Horse", EntityType.HORSE),
	WOLF ("Wolf", EntityType.WOLF);
	
	private String name;
	private EntityType entity;
	
	private AnimalType(String name, EntityType entity) {
		this.name = name;
		this.entity = entity;
	}
	
	public String getName() {
		return name;
	}
	
	public EntityType getEntity() {
		return entity;
	}
}
