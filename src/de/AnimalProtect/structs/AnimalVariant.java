package de.AnimalProtect.structs;

import org.bukkit.entity.Horse.Variant;

/**
 * Beschreibt die Variante des Tieres. <br>
 * Nur Pferde haben verschiedene Varianten. <br>
 * 
 * <b> Verfügbare Typen: </b> <br>
 * '<code>NONE</code>' <br>
 * '<code>DONKEY</code>' <br>
 * '<code>HORSE</code>' <br>
 * '<code>MULE</code>' <br>
 * '<code>SKELETON_HORSE</code>' <br>
 * '<code>UNDEAD_HORSE</code>' <br>
 * 
 * @see Animal
 * @see Variant
 */
public enum AnimalVariant {
	/** Das Tier hat keine Variante und ist deswegen (wahrscheinlich) kein Pferd. */
	NONE ("None", null),
	/** Das Tier ist ein Esel. */
	DONKEY ("Donkey", Variant.DONKEY),
	/** Das Tier ist ein normales Pferd. */
	HORSE ("Horse", Variant.HORSE),
	/** Das Tier ist ein Maulesel. */
	MULE ("Mule", Variant.MULE),
	/** Das Tier ist ein Skelettpferd. */
	SKELETON_HORSE ("Skeletonhorse", Variant.SKELETON_HORSE),
	/** Das Tier ist ein untotes Pferd. */
	UNDEAD_HORSE ("Undeadhorse", Variant.UNDEAD_HORSE);

	/** Der Name der Variante. */
	private String name;
	/** Die Bukkitvariante des Pferdes. Kann {@code null} sein. */
	private Variant variant;

	/**
	 * Initialisiert eine neue Tiervariante.
	 * @param name - Der Name der Variante.
	 * @param variant
	 */
	private AnimalVariant(final String name, final Variant variant) {
		this.name = name;
		this.variant = variant;
	}

	/**
	 * @return Den Namen des Pferdtypes.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return Die Bukkitvariante des Types. Kann {@code null} sein!
	 */
	public Variant getVariant() {
		return this.variant;
	}
}