package de.AnimalProtect.structs;

import org.bukkit.Material;

/**
 * Gibt die Rüstung eines Tieres an. <br>
 * Zurzeit können nur Pferde eine Rüstung besitzen. <br>
 * Jeder Rüstungstyp besitzt auch ein Material. <br>
 * 
 * <b> Verfügbare Typen: </b> <br>
 * '<code>UNKNOWN</code>' <br>
 * '<code>DIAMOND</code>' <br>
 * '<code>GOLD</code>' <br>
 * '<code>IRON</code>' <br>
 * 
 * @see Animal
 * @see Material
 */
public enum AnimalArmor {
	/** Wenn das Tier eine unbekannte oder keine Rüstung hat. */
	UNKNOWN("Unknown", Material.AIR),
	/** Wenn das Tier die Diamant-Pferderüstung trägt. */
	DIAMOND ("Diamond", Material.DIAMOND_BARDING),
	/** Wenn das Tier eine goldene Pferderüstung trägt. */
	GOLD ("Gold", Material.GOLD_BARDING),
	/** Wenn das Tier die Eisen-Pferderüstung trägt. */
	IRON ("Iron", Material.IRON_BARDING);

	/** Der Name der Rüstung. */
	private String name;
	/** Das Material, aus dem die Rüstung ist. */
	private Material material;

	/**
	 * Initialisiert eine neue Rüstung.
	 * @param name - Der Name der Rüstung.
	 * @param material - Das Material, aus dem die Rüstung besteht.
	 */
	private AnimalArmor(final String name, final Material material) {
		this.name = name;
		this.material = material;
	}

	/**
	 * @return Den richtigen Namen der Rüstung.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return Das Material, aus dem die Rüstung besteht.
	 */
	public Material getMaterial() {
		return this.material;
	}
}