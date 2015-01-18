package de.AnimalProtect.structs;

/* Java Imports */
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

/* Bukkit Imports */
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Horse.Style;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Wolf;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

/* CraftoPlugin Imports */
import craftoplugin.core.database.CraftoPlayer;
/* AnimalProtect Imports */
import de.AnimalProtect.AnimalProtect;

/**
 * Das Animal-Objekt in AnimalProtect.
 * 
 * @see AnimalProtect
 * @see AnimalArmor
 * @see AnimalType
 * @see AnimalVariant
 * @see Comparable
 */
public class Animal implements Comparable<Animal> {

	/** Die AnimalProtect-Instanz. */
	private final AnimalProtect plugin;

	/** Die Datenbank-ID des Tieres. Kann {@code null} sein!*/
	private Integer id;
	/** Die {@link CraftoPlayer}-Id des Owners. */
	private Integer owner;
	/** Der Typ des Tieres. */
	private AnimalType animaltype;
	/** Die X-Koordinate der letzten bekannten Position des Tieres. */
	private int last_x;
	/** Die Y-Koordinate der letzten bekannten Position des Tieres. */
	private int last_y;
	/** Die Z-Koordinate der letzten bekannten Position des Tieres. */
	private int last_z;
	/** True, wenn das Tier noch nicht als tot erkannt wurde. */
	private boolean alive;
	/** Der Nametag des Tieres. */
	private String nametag;
	/** Die maximalen HP des Tieres. */
	private double maxhp;
	/** Der Todesgrund des Tieres, falls es bereits tot ist. */
	private DamageCause deathcause;
	/** Die Farbe des Tieres, falls vorhanden. */
	private String color;
	/** Die Rüstung des Tieres. */
	private AnimalArmor armor;
	/** Die Sprungstärke des Tieres, falls es ein Pferd ist. */
	private double horse_jumpstrength;
	/** Der Style des Tieres, falls es ein Pferd ist. */
	private Style horse_style;
	/** Die Variante des Tieres, falls es ein Pferd ist. */
	private AnimalVariant horse_variant;
	/** Die UniqueId des Tieres. */
	private UUID uuid;
	/** Der Zeitpunkt an dem das Tier gesichert wurde. */
	private Timestamp created_at;
	/** Das {@link CraftoPlayer}-Objekt des Owners. */
	private CraftoPlayer cOwner;

	/**
	 * Initialisiert ein leeres Animal-Objekt.
	 * @param plugin - Das AnimalProtect-Plugin.
	 */
	public Animal(final AnimalProtect plugin) { 
		this.plugin = plugin;
		this.created_at = new Timestamp(new Date().getTime());
	}

	/**
	 * Initialisiert ein neues Animal-Objekt mit einem Owner und dem Entity.
	 * @param plugin - Das AnimalProtect-Plugin.
	 * @param owner - Der Owner des Tieres.
	 * @param entity - Das Tier selber, als Entity-Objekt.
	 */
	public Animal(final AnimalProtect plugin, final CraftoPlayer owner, final Entity entity) { 
		this.plugin = plugin;
		this.owner = owner.getId();
		this.created_at = new Timestamp(new Date().getTime());
		this.updateAnimal(entity);
	}

	/**
	 * Speichert alle Werte des Tieres in die Datenbank.
	 * @param log - True, für Console-Output.
	 * @return Gibt True aus, falls das Speichern geklappt hat.
	 */
	public boolean saveToDatabase(final Boolean log) {
		if (this.plugin == null) { return false; }

		if (this.plugin.getDatenbank().insertAnimal(this)) { return true; }
		else { return false; }
	}

	/**
	 * Lädt das Tier mit der angegebenen UUID aus der Datenbank und speichert es in dieses Objekt.
	 * @param uuid - Die UniqueId nach welcher in der Datenbank gesucht werden soll.
	 * @return Gibt True aus, falls das Laden geklappt hat.
	 */
	public boolean loadFromDatabase(final UUID uuid) {
		if (this.plugin == null) { return false; }

		final Animal animal = this.plugin.getDatenbank().getAnimal(uuid);

		if (animal != null) {
			this.id = animal.id;
			this.owner = animal.owner;
			this.animaltype = animal.animaltype;
			this.last_x = animal.last_x;
			this.last_y = animal.last_y;
			this.last_z = animal.last_z;
			this.alive = animal.alive;
			this.maxhp = animal.maxhp;
			this.color = animal.color;
			this.armor = animal.armor;
			this.horse_jumpstrength = animal.horse_jumpstrength;
			this.horse_style = animal.horse_style;
			this.horse_variant = animal.horse_variant;
			this.uuid = animal.uuid;
			this.created_at = new Timestamp(new Date().getTime());
			return true;
		}

		return false;
	}

	/**
	 * Aktualisiert alle Werte dieses Tieres.
	 * @param entity - Das Entity, von dem die Werte genommen werden.
	 * @return Gibt True aus, falls das Aktualisieren geklappt hat.
	 */
	public boolean updateAnimal(final Entity entity) {
		if (entity == null) { return false; }

		this.uuid = entity.getUniqueId();
		this.last_x = (short) entity.getLocation().getBlockX();
		this.last_y = (short) entity.getLocation().getBlockY();
		this.last_z = (short) entity.getLocation().getBlockZ();
		this.alive = !entity.isDead();
		this.animaltype = AnimalType.valueOf(entity.getType().toString());
		this.deathcause = null;
		this.nametag = "";
		this.armor = AnimalArmor.UNKNOWN;
		this.maxhp = 0.0;
		this.horse_jumpstrength = 0.0;
		this.horse_style = Style.NONE;

		if (entity.getType().equals(EntityType.SHEEP)) { 
			final Sheep sheep = (Sheep) entity; 
			this.color = sheep.getColor().toString(); 
		}
		else if (entity.getType().equals(EntityType.HORSE)) {
			final Horse horse = (Horse) entity;
			if (horse.getInventory().getArmor().equals(new ItemStack(Material.DIAMOND_BARDING))) 
			{ this.armor = AnimalArmor.DIAMOND; }
			else if (horse.getInventory().getArmor().equals(new ItemStack(Material.GOLD_BARDING))) 
			{ this.armor = AnimalArmor.GOLD;}
			else if (horse.getInventory().getArmor().equals(new ItemStack(Material.IRON_BARDING))) 
			{ this.armor = AnimalArmor.IRON; }

			this.horse_style = horse.getStyle();
			this.horse_variant = AnimalVariant.valueOf(horse.getVariant().toString());
			this.color = horse.getColor().toString();
			this.maxhp = horse.getMaxHealth();
			this.horse_jumpstrength = horse.getJumpStrength();
		}
		else if (entity.getType().equals(EntityType.WOLF)) {
			final Wolf wolf = (Wolf) entity;
			this.color = wolf.getCollarColor().toString();
		}

		try {
			final LivingEntity le = (LivingEntity) entity;
			this.nametag = le.getCustomName();
			if (this.maxhp == 0.0) { this.maxhp = le.getMaxHealth(); }

			return true;
		}
		catch (final Exception e) { return false; }
	}

	/**
	 * @return Die Datenbank-Id des Tieres.
	 */
	public Integer getId() { return this.id; }
	/**
	 * @return Die CraftoPlayer-Id des Besitzers.
	 */
	public int getOwner() { return this.owner; }
	/**
	 * @return Der Typ des Tieres.
	 * @see AnimalType
	 */
	public AnimalType getAnimaltype() { return this.animaltype; }
	/**
	 * @return Die letzte bekannte x-Position
	 */
	public int getX() { return this.last_x; }
	/**
	 * @return Die letzte bekannte y-Position
	 */
	public int getY() { return this.last_y; }
	/**
	 * @return Die letzte bekannte z-Position
	 */
	public int getZ() { return this.last_z; }
	/**
	 * @return Gibt False aus, falls bekannt ist, dass das Tier tot ist.
	 */
	public boolean isAlive() { return this.alive; }
	public String isAliveAsString() {
		if (this.alive) { return ChatColor.GREEN + "+"; }
		else { return ChatColor.RED + "-"; }
	}
	/**
	 * @return Gibt den Nametag des Tieres aus.
	 */
	public String getNametag() {
		if (this.nametag == null) { return ""; }
		return this.nametag;
	}
	/**
	 * @return Die maximale HP des Tieres.
	 */
	public double getMaxhp() {
		return this.maxhp;
	}
	/**
	 * @return Der Grund, warum das Tier getötet wurde.
	 */
	public DamageCause getDeathcause() { return this.deathcause; }
	/**
	 * @return Der Grund, warum das Tier getötet wurde.
	 */
	public String getDeathcauseToString() {
		if (this.deathcause == null) { return "NONE"; }
		return this.deathcause.toString();
	}
	/**
	 * @return Die Farbe des Tieres.
	 */
	public String getColor() { return this.color; }
	/**
	 * @return Die Farbe des Tieres.
	 */
	public String getColorToString() {
		if (this.color == null) { return ""; }
		return this.color;
	}
	/**
	 * @return Die Rüstung des Tieres.
	 */
	public AnimalArmor getArmor() { return this.armor; }
	/**
	 * @return Die Sprungstärke des Pferdes.
	 */
	public double getHorse_jumpstrength() { return this.horse_jumpstrength; }
	/**
	 * @return Der Style des Pferdes.
	 */
	public Style getHorse_style() { return this.horse_style; }
	/**
	 * @return Der Style des Pferdes
	 */
	public String getHorse_styleToString() {
		if (this.horse_style == null) { return "NONE"; }
		else { return this.horse_style.toString(); }
	}
	/**
	 * @return Die Variante des Pferdes
	 */
	public AnimalVariant getHorse_variant() { return this.horse_variant; }
	/**
	 * @return Die Variante des Pferdes
	 */
	public String getHorse_variantToString() {
		if (this.horse_variant == null) { return "NONE"; }
		else { return this.horse_variant.toString(); }
	}
	/**
	 * @return Die UniqueId des Tieres.
	 */
	public UUID getUniqueId() { return this.uuid; }
	/**
	 * @return Gibt aus, wann das Tier protected wurde.
	 */
	public Timestamp getCreated_at() { return this.created_at; }
	/**
	 * @return Gibt den Owner des Tieres als CraftoPlayer-Objekt wieder.
	 */
	public CraftoPlayer getCraftoOwner() {
		if (this.cOwner == null ) { this.cOwner = CraftoPlayer.getPlayer(this.owner); }
		return this.cOwner;
	}
	/**
	 * Setzt die Id auf den angegebenen Wert.
	 * @param id - Die neue Id des Tieres.
	 */
	public void setId(final Integer id) { this.id = id; }
	/**
	 * Ändert den Owner auf einen neuen Owner.
	 * @param Die Id des neuen Owners.
	 */
	public void setOwner(final int owner) { this.owner = owner; }
	/**
	 * Ändert den Typ des Tieres.
	 * @param animaltype - Der neue AnimalType.
	 */
	public void setAnimaltype(final AnimalType animaltype) { this.animaltype = animaltype; }
	/**
	 * Setzt die letzte bekannte X-Position auf den neuen Wert.
	 * @param last_x - Die X-Koordinate
	 */
	public void setX(final int last_x) { this.last_x = last_x; }
	/**
	 * Setzt die letzte bekannte Y-Position auf den neuen Wert.
	 * @param last_y - Die Y-Koordinate
	 */
	public void setY(final int last_y) { this.last_y = last_y; }
	/**
	 * Setzt die letzte bekannte Z-Position auf den neuen Wert.
	 * @param last_z - Die Z-Koordinate
	 */
	public void setZ(final int last_z) { this.last_z = last_z; }
	/**
	 * Ändert den Lebens-Status des Tieres.
	 * @param alive - Der neue Status
	 */
	public void setAlive(final boolean alive) { this.alive = alive; }
	/**
	 * Ändert den Nametag des Tieres.
	 * @param nametag - Der neue Nametag
	 */
	public void setNametag(final String nametag) { this.nametag = nametag; }
	/**
	 * Ändert die maximale HP des Tieres.
	 * @param maxhp - Die neuen maximalen HP.
	 */
	public void setMaxhp(final double maxhp) { this.maxhp = maxhp; }
	/**
	 * Ändert die Angabe, warum das Tier gestorben ist.
	 * @param deathcause - der Todesgrund.
	 * @see DamageCause
	 */
	public void setDeathcause(final DamageCause deathcause) { this.deathcause = deathcause; }
	/**
	 * Ändert die Farbe des Tieres.
	 * @param color - Die neue Farbe des Tieres.
	 */
	public void setColor(final String color) { this.color = color;}
	/**
	 * Ändert die Rüstung des Tieres.
	 * @param armor - Die neue Rüstung
	 * @see AnimalArmor
	 */
	public void setArmor(final AnimalArmor armor) { this.armor = armor; }
	/**
	 * Ändert die Sprungstärke des Pferdes.
	 * @param horse_jumpstrength - Die neue Sprungstärke
	 */
	public void setHorse_jumpstrength(final double horse_jumpstrength) { this.horse_jumpstrength = horse_jumpstrength; }
	/**
	 * Ändert den Style des Pferdes.
	 * @param horse_style - Der Style des Pferdes.
	 */
	public void setHorse_style(final Style horse_style) { this.horse_style = horse_style; }
	/**
	 * Ändert die Variante des Pferdes.
	 * @param horse_variant - Die Variante des Pferdes.
	 */
	public void setHorse_variant(final AnimalVariant horse_variant) { this.horse_variant = horse_variant; }
	/**
	 * Ändert die UniqueId des Tieres.
	 * @param uuid - Die neue UniqueId
	 */
	public void setUniqueId(final UUID uuid) { this.uuid = uuid; }
	/**
	 * Setzt das Erstelldatum auf einen neuen Timestamp.
	 * @param created_at - Das neue Erstelldatum
	 */
	public void setCreated_at(final Timestamp created_at) { this.created_at = created_at; }

	@Override
	public int compareTo(final Animal animal) {
		if (animal == null) { return 1; }
		else if (this.getCreated_at().after(animal.getCreated_at())) 
		{ return 1; }
		return -1;
	}
}