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

public class Animal implements Comparable<Animal> {
	
	private AnimalProtect plugin;
	
	private Integer id;
	private Integer owner;
	private AnimalType animaltype;
	private Integer last_x;
	private Integer last_y;
	private Integer last_z;
	private Boolean alive;
	private String nametag;
	private Double maxhp;
	private DamageCause deathcause;
	private String color;
	private AnimalArmor armor;
	private Double horse_jumpstrength;
	private Style horse_style;
	private AnimalVariant horse_variant; 
	private UUID uuid;
	private Timestamp created_at;
	
	public Animal(AnimalProtect plugin) { 
		this.plugin = plugin;
		this.created_at = new Timestamp(new Date().getTime());
	}
	public Animal(AnimalProtect plugin, CraftoPlayer owner, Entity entity) { 
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
	public boolean saveToDatabase(Boolean log) {
		if (plugin == null) { return false; }
		
		if (plugin.getDatenbank().insertAnimal(this)) { return true; }
		else { return false; }
	}
	
	/**
	 * Lädt das Tier mit der angegebenen UUID aus der Datenbank und speichert es in dieses Objekt.
	 * @param uuid - Die UniqueId nach welcher in der Datenbank gesucht werden soll.
	 * @return Gibt True aus, falls das Laden geklappt hat.
	 */
	public boolean loadFromDatabase(UUID uuid) {
		if (plugin == null) { return false; }
		
		Animal animal = plugin.getDatenbank().getAnimal(uuid);
		
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
	public boolean updateAnimal(Entity entity) {
		if (entity == null) { return false; }
		
		this.uuid = entity.getUniqueId();
		this.last_x = entity.getLocation().getBlockX();
		this.last_y = entity.getLocation().getBlockY();
		this.last_z = entity.getLocation().getBlockZ();
		this.alive = !entity.isDead();
		this.animaltype = AnimalType.valueOf(entity.getType().toString());
		this.deathcause = null;
		this.nametag = "";
		this.armor = AnimalArmor.UNKNOWN;
		this.maxhp = 0.0;
		this.horse_jumpstrength = 0.0;
		this.horse_style = Style.NONE;
		
		if (entity.getType().equals(EntityType.SHEEP)) { 
			Sheep sheep = (Sheep) entity; 
			this.color = sheep.getColor().toString(); 
		}
		else if (entity.getType().equals(EntityType.HORSE)) {
			Horse horse = (Horse) entity;
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
			Wolf wolf = (Wolf) entity;
			this.color = wolf.getCollarColor().toString();
		}
		
		try {
			LivingEntity le = (LivingEntity) entity;
			this.nametag = le.getCustomName();
			if (maxhp == 0.0) { this.maxhp = le.getMaxHealth(); }
			
			return true;
		}
		catch (Exception e) { return false; }
	}
	
	/**
	 * @return Die Datenbank-Id des Tieres.
	 */
	public Integer getId() { return id; }
	/**
	 * @return Die CraftoPlayer-Id des Besitzers.
	 */
	public Integer getOwner() { return owner; }
	/**
	 * @return Der Typ des Tieres.
	 * @see AnimalType
	 */
	public AnimalType getAnimaltype() { return animaltype; }
	/**
	 * @return Die letzte bekannte x-Position
	 */
	public Integer getX() { return last_x; }
	/**
	 * @return Die letzte bekannte y-Position
	 */
	public Integer getY() { return last_y; }
	/**
	 * @return Die letzte bekannte z-Position
	 */
	public Integer getZ() { return last_z; }
	/**
	 * @return Gibt False aus, falls bekannt ist, dass das Tier tot ist.
	 */
	public Boolean isAlive() { return alive; }
	public String isAliveAsString() {
		if (alive) { return ChatColor.GREEN + "+"; }
		else { return ChatColor.RED + "-"; }
	}
	/**
	 * @return Gibt den Nametag des Tieres aus.
	 */
	public String getNametag() {
		if (nametag == null) { return ""; }
		return nametag;
	}
	/**
	 * @return Die maximale HP des Tieres.
	 */
	public Double getMaxhp() {
		if (maxhp == null) { return 0.0; }
		return maxhp;
	}
	/**
	 * @return Der Grund, warum das Tier getötet wurde.
	 */
	public DamageCause getDeathcause() { return deathcause; }
	/**
	 * @return Der Grund, warum das Tier getötet wurde.
	 */
	public String getDeathcauseToString() {
		if (deathcause == null) { return "NONE"; }
		return deathcause.toString();
	}
	/**
	 * @return Die Farbe des Tieres.
	 */
	public String getColor() { return color; }
	/**
	 * @return Die Farbe des Tieres.
	 */
	public String getColorToString() {
		if (color == null) { return ""; }
		return color;
	}
	/**
	 * @return Die Rüstung des Tieres.
	 */
	public AnimalArmor getArmor() { return armor; }
	/**
	 * @return Die Sprungstärke des Pferdes.
	 */
	public Double getHorse_jumpstrength() { return horse_jumpstrength; }
	/**
	 * @return Der Style des Pferdes.
	 */
	public Style getHorse_style() { return horse_style; }
	/**
	 * @return Der Style des Pferdes
	 */
	public String getHorse_styleToString() {
		if (horse_style == null) { return "NONE"; }
		else { return horse_style.toString(); }
	}
	/**
	 * @return Die Variante des Pferdes
	 */
	public AnimalVariant getHorse_variant() { return horse_variant; }
	/**
	 * @return Die Variante des Pferdes
	 */
	public String getHorse_variantToString() {
		if (horse_variant == null) { return "NONE"; }
		else { return horse_variant.toString(); }
	}
	/**
	 * @return Die UniqueId des Tieres.
	 */
	public UUID getUniqueId() { return uuid; }
	/**
	 * @return Gibt aus, wann das Tier protected wurde.
	 */
	public Timestamp getCreated_at() { return created_at; }
	/**
	 * Setzt die Id auf den angegebenen Wert.
	 * @param id - Die neue Id des Tieres.
	 */
	public void setId(Integer id) { this.id = id; }
	/**
	 * Ändert den Owner auf einen neuen Owner.
	 * @param Die Id des neuen Owners.
	 */
	public void setOwner(Integer owner) { this.owner = owner; }
	/**
	 * Ändert den Typ des Tieres.
	 * @param animaltype - Der neue AnimalType.
	 */
	public void setAnimaltype(AnimalType animaltype) { this.animaltype = animaltype; }
	/**
	 * Setzt die letzte bekannte X-Position auf den neuen Wert.
	 * @param last_x - Die X-Koordinate
	 */
	public void setX(Integer last_x) { this.last_x = last_x; }
	/**
	 * Setzt die letzte bekannte Y-Position auf den neuen Wert.
	 * @param last_y - Die Y-Koordinate
	 */
	public void setY(Integer last_y) { this.last_y = last_y; }
	/**
	 * Setzt die letzte bekannte Z-Position auf den neuen Wert.
	 * @param last_z - Die Z-Koordinate
	 */
	public void setZ(Integer last_z) { this.last_z = last_z; }
	/**
	 * Ändert den Lebens-Status des Tieres.
	 * @param alive - Der neue Status
	 */
	public void setAlive(Boolean alive) { this.alive = alive; }
	/**
	 * Ändert den Nametag des Tieres.
	 * @param nametag - Der neue Nametag
	 */
	public void setNametag(String nametag) { this.nametag = nametag; }
	/**
	 * Ändert die maximale HP des Tieres.
	 * @param maxhp - Die neuen maximalen HP.
	 */
	public void setMaxhp(Double maxhp) { this.maxhp = maxhp; }
	/**
	 * Ändert die Angabe, warum das Tier gestorben ist.
	 * @param deathcause - der Todesgrund.
	 * @see DamageCause
	 */
	public void setDeathcause(DamageCause deathcause) { this.deathcause = deathcause; }
	/**
	 * Ändert die Farbe des Tieres.
	 * @param color - Die neue Farbe des Tieres.
	 */
	public void setColor(String color) { this.color = color;}
	/**
	 * Ändert die Rüstung des Tieres.
	 * @param armor - Die neue Rüstung
	 * @see AnimalArmor
	 */
	public void setArmor(AnimalArmor armor) { this.armor = armor; }
	/**
	 * Ändert die Sprungstärke des Pferdes.
	 * @param horse_jumpstrength - Die neue Sprungstärke
	 */
	public void setHorse_jumpstrength(Double horse_jumpstrength) { this.horse_jumpstrength = horse_jumpstrength; }
	/**
	 * Ändert den Style des Pferdes.
	 * @param horse_style - Der Style des Pferdes.
	 */
	public void setHorse_style(Style horse_style) { this.horse_style = horse_style; }
	/**
	 * Ändert die Variante des Pferdes.
	 * @param horse_variant - Die Variante des Pferdes.
	 */
	public void setHorse_variant(AnimalVariant horse_variant) { this.horse_variant = horse_variant; }
	/**
	 * Ändert die UniqueId des Tieres.
	 * @param uuid - Die neue UniqueId
	 */
	public void setUniqueId(UUID uuid) { this.uuid = uuid; }
	/**
	 * Setzt das Erstelldatum auf einen neuen Timestamp.
	 * @param created_at - Das neue Erstelldatum
	 */
	public void setCreated_at(Timestamp created_at) { this.created_at = created_at; }
	
	@Override
	public int compareTo(Animal animal) {
		if (animal == null) { return 1; }
		else if (this.getCreated_at().after(animal.getCreated_at())) 
		{ return 1; }
		return -1;
	}
}