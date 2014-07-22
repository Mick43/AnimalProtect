package de.AnimalProtect.structs;

import org.bukkit.entity.Horse.Variant;

public enum AnimalVariant {
	NONE ("None", null),
	DONKEY ("Donkey", Variant.DONKEY),
	HORSE ("HORSE", Variant.HORSE),
	MULE ("MULE", Variant.MULE),
	SKELETON_HORSE ("Sheep", Variant.SKELETON_HORSE),
	UNDEAD_HORSE ("Horse", Variant.UNDEAD_HORSE);
	
	private String name;
	private Variant variant;
	
	private AnimalVariant(final String name, final Variant variant) {
		this.name = name;
		this.variant = variant;
	}
	
	public String getName() {
		return this.name;
	}
	
	public Variant getVariant() {
		return this.variant;
	}
}
