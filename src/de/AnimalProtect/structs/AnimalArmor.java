package de.AnimalProtect.structs;

import org.bukkit.Material;

/**
 * Gibt die R�stung eines Tieres an. <br>
 * Zurzeit k�nnen nur Pferde eine R�stung besitzen. <br>
 * Jeder R�stungstyp besitzt auch ein Material. <br>
 * 
 * <b> Verf�gbare Typen: </b> <br>
 * '<code>UNKNOWN</code>' <br>
 * '<code>DIAMOND</code>' <br>
 * '<code>GOLD</code>' <br>
 * '<code>IRON</code>' <br>
 * 
 * @see Animal
 * @see Material
 */
public enum AnimalArmor {
	UNKNOWN("Unknown", Material.AIR),
	DIAMOND ("Diamond", Material.DIAMOND_BARDING),
	GOLD ("Gold", Material.GOLD_BARDING),
	IRON ("Iron", Material.IRON_BARDING);

	private String name;
	private Material material;

	private AnimalArmor(final String name, final Material material) {
		this.name = name;
		this.material = material;
	}

	public String getName() {
		return this.name;
	}

	public Material getMaterial() {
		return this.material;
	}
}
