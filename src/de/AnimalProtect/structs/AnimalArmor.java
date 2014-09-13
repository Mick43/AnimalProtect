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
	/** Wenn das Tier eine unbekannte oder keine R�stung hat. */
	UNKNOWN("Unknown", Material.AIR),
	/** Wenn das Tier die Diamant-Pferder�stung tr�gt. */
	DIAMOND ("Diamond", Material.DIAMOND_BARDING),
	/** Wenn das Tier eine goldene Pferder�stung tr�gt. */
	GOLD ("Gold", Material.GOLD_BARDING),
	/** Wenn das Tier die Eisen-Pferder�stung tr�gt. */
	IRON ("Iron", Material.IRON_BARDING);

	/** Der Name der R�stung. */
	private String name;
	/** Das Material, aus dem die R�stung ist. */
	private Material material;

	/**
	 * Initialisiert eine neue R�stung.
	 * @param name - Der Name der R�stung.
	 * @param material - Das Material, aus dem die R�stung besteht.
	 */
	private AnimalArmor(final String name, final Material material) {
		this.name = name;
		this.material = material;
	}

	/**
	 * @return Den richtigen Namen der R�stung.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return Das Material, aus dem die R�stung besteht.
	 */
	public Material getMaterial() {
		return this.material;
	}
}