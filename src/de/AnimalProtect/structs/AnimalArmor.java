package de.AnimalProtect.structs;

import org.bukkit.Material;

public enum AnimalArmor {
	UNKNOWN("Unknown", Material.AIR),
	DIAMOND ("Diamond", Material.DIAMOND_BARDING),
	GOLD ("Gold", Material.GOLD_BARDING),
	IRON ("Iron", Material.IRON_BARDING);
	
	private String name;
	private Material material;
	
	private AnimalArmor(String name, Material material) {
		this.name = name;
		this.material = material;
	}
	
	public String getName() {
		return name;
	}
	
	public Material getMaterial() {
		return material;
	}
}
