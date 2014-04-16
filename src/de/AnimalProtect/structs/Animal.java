package de.AnimalProtect.structs;

import java.sql.Timestamp;
import java.util.Date;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Horse.Style;
import org.bukkit.entity.Horse.Variant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Wolf;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

import craftoplugin.core.database.CraftoPlayer;
import de.AnimalProtect.AnimalProtect;
import de.AnimalProtect.Database;

public class Animal {
	
	private AnimalProtect plugin;
	
	private Integer id;
	private Integer owner;
	private AnimalType animaltype;
	private Integer last_x;
	private Integer last_y;
	private Integer last_z;
	private Boolean alive;
	private String nametag;
	private Float maxhp;
	private DamageCause deathcause;
	private String color;
	private AnimalArmor armor;
	private Double horse_jumpstrength;
	private Style horse_style;
	private Variant horse_variant; 
	private String uuid;
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
	public Animal(AnimalProtect plugin, Integer owner, AnimalType animaltype, Integer last_x, Integer last_y, Integer last_z, Boolean alive, Float maxhp,
				  String color, AnimalArmor armor, Double horse_jumpstrength, Style horse_style, Variant horse_variant, String uuid) {
		
		this.plugin = plugin;
		this.owner = owner;
		this.animaltype = animaltype;
		this.last_x = last_x;
		this.last_y = last_y;
		this.last_z = last_z;
		this.alive = alive;
		this.maxhp = maxhp;
		this.color = color;
		this.armor = armor;
		this.horse_jumpstrength = horse_jumpstrength;
		this.horse_style = horse_style;
		this.horse_variant = horse_variant;
		this.uuid = uuid;
		this.created_at = new Timestamp(new Date().getTime());
	}
	
	/**
	 * Speichert alle Werte des Tieres in die Datenbank.
	 * @param log - True, f�r Console-Output.
	 * @return Gibt True aus, falls das Speichern geklappt hat.
	 */
	public boolean saveToDatabase(Boolean log) {
		if (plugin == null) { return false; }
		if (!plugin.getDatenbank().isConnected()) { return false; }
		
		Database database = plugin.getDatenbank();
		if (database.insertAnimal(this)) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * L�dt das Tier mit der angegebenen UUID aus der Datenbank und speichert es in dieses Objekt.
	 * @param uuid - Die UniqueId nach welcher in der Datenbank gesucht werden soll.
	 * @return Gibt True aus, falls das Laden geklappt hat.
	 */
	public boolean loadFromDatabase(String uuid) {
		if (plugin == null) { return false; }
		if (!plugin.getDatenbank().isConnected()) { return false; }
		
		Database database = plugin.getDatenbank();
		Animal animal = database.getAnimal(uuid);
		
		if (animal != null) {
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
		
		this.uuid = entity.getUniqueId().toString();
		this.last_x = entity.getLocation().getBlockX();
		this.last_y = entity.getLocation().getBlockY();
		this.last_z = entity.getLocation().getBlockZ();
		this.alive = !entity.isDead();
		
		if (entity.getType().equals(EntityType.SHEEP))
		{ Sheep sheep = (Sheep) entity; this.color = sheep.getColor().toString(); }
		else if (entity.getType().equals(EntityType.HORSE)) {
			Horse horse = (Horse) entity;
			if (horse.getInventory().getArmor().equals(new ItemStack(Material.DIAMOND_BARDING))) {
				this.armor = AnimalArmor.DIAMOND;
			}
			else if (horse.getInventory().getArmor().equals(new ItemStack(Material.GOLD_BARDING))) {
				this.armor = AnimalArmor.GOLD;
			}
			else if (horse.getInventory().getArmor().equals(new ItemStack(Material.IRON_BARDING))) {
				this.armor = AnimalArmor.IRON;
			}
			
			this.color = horse.getColor().toString();
		}
		else if (entity.getType().equals(EntityType.WOLF)) {
			Wolf wolf = (Wolf) entity;
			this.color = wolf.getCollarColor().toString();
		}
		
		try {
			LivingEntity le = (LivingEntity) entity;
			this.nametag = le.getCustomName();
			return true;
		}
		catch (Exception e) { }
		
		return false;
	}
	
	/**
	 * @return Die Id des Tieres.
	 */
	public Integer getId() {
		return id;
	}
	/**
	 * @return Die CraftoPlayer-Id des Besitzers.
	 */
	public Integer getOwner() {
		return owner;
	}
	/**
	 * @return Der Typ des Tieres.
	 */
	public AnimalType getAnimaltype() {
		return animaltype;
	}
	/**
	 * @return Die letzte bekannte x-Position
	 */
	public Integer getLast_x() {
		return last_x;
	}
	/**
	 * @return Die letzte bekannte y-Position
	 */
	public Integer getLast_y() {
		return last_y;
	}
	/**
	 * @return Die letzte bekannte z-Position
	 */
	public Integer getLast_z() {
		return last_z;
	}
	/**
	 * @return Gibt False aus, falls bekannt ist, dass das Tier tot ist.
	 */
	public Boolean isAlive() {
		return alive;
	}
	public String isAliveAsString() {
		if (alive) { return ChatColor.GREEN + "ALIVE"; }
		else { return ChatColor.RED + "MISSING"; }
	}
	/**
	 * @return Gibt den Nametag des Tieres aus.
	 */
	public String getNametag() {
		return nametag;
	}
	/**
	 * @return Die maximale HP des Tieres.
	 */
	public Float getMaxhp() {
		return maxhp;
	}
	/**
	 * @return Der Grund, warum das Tier get�tet wurde.
	 */
	public DamageCause getDeathcause() {
		return deathcause;
	}
	/**
	 * @return Die Farbe des Tieres.
	 */
	public String getColor() {
		return color;
	}
	/**
	 * @return Die R�stung des Tieres.
	 */
	public AnimalArmor getArmor() {
		return armor;
	}
	/**
	 * @return Die Sprungst�rke des Pferdes.
	 */
	public Double getHorse_jumpstrength() {
		return horse_jumpstrength;
	}
	/**
	 * @return Der Style des Pferdes.
	 */
	public Style getHorse_style() {
		return horse_style;
	}
	/**
	 * @return Die Variante des Pferdes
	 */
	public Variant getHorse_variant() {
		return horse_variant;
	}
	/**
	 * @return Die UniqueId des Tieres.
	 */
	public String getUniqueId() {
		return uuid;
	}
	/**
	 * @return Gibt aus, wann das Tier protected wurde.
	 */
	public Timestamp getCreated_at() {
		return created_at;
	}
	/**
	 * Setzt die Id auf den angegebenen Wert.
	 * @param id - Die neue Id des Tieres.
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	/**
	 * �ndert den Owner auf einen neuen Owner.
	 * @param Die Id des neuen Owners.
	 */
	public void setOwner(Integer owner) {
		this.owner = owner;
	}
	/**
	 * �ndert den Typ des Tieres.
	 * @param animaltype - Der neue AnimalType.
	 */
	public void setAnimaltype(AnimalType animaltype) {
		this.animaltype = animaltype;
	}
	/**
	 * Setzt die letzte bekannte X-Position auf den neuen Wert.
	 * @param last_x - Die X-Koordinate
	 */
	public void setLast_x(Integer last_x) {
		this.last_x = last_x;
	}
	/**
	 * Setzt die letzte bekannte Y-Position auf den neuen Wert.
	 * @param last_y - Die Y-Koordinate
	 */
	public void setLast_y(Integer last_y) {
		this.last_y = last_y;
	}
	/**
	 * Setzt die letzte bekannte Z-Position auf den neuen Wert.
	 * @param last_z - Die Z-Koordinate
	 */
	public void setLast_z(Integer last_z) {
		this.last_z = last_z;
	}
	/**
	 * �ndert den Lebens-Status des Tieres.
	 * @param alive - Der neue Status
	 */
	public void setAlive(Boolean alive) {
		this.alive = alive;
	}
	/**
	 * �ndert den Nametag des Tieres.
	 * @param nametag - Der neue Nametag
	 */
	public void setNametag(String nametag) {
		this.nametag = nametag;
	}
	/**
	 * �ndert die maximale HP des Tieres.
	 * @param maxhp - Die neuen maximalen HP.
	 */
	public void setMaxhp(Float maxhp) {
		this.maxhp = maxhp;
	}
	/**
	 * �ndert die Angabe, warum das Tier gestorben ist.
	 * @param deathcause - der Todesgrund.
	 */
	public void setDeathcause(DamageCause deathcause) {
		this.deathcause = deathcause;
	}
	/**
	 * �ndert die Farbe des Tieres.
	 * @param color - Die neue Farbe des Tieres.
	 */
	public void setColor(String color) {
		this.color = color;
	}
	/**
	 * �ndert die R�stung des Tieres.
	 * @param armor - Die neue R�stung
	 */
	public void setArmor(AnimalArmor armor) {
		this.armor = armor;
	}
	/**
	 * �ndert die Sprungst�rke des Pferdes.
	 * @param horse_jumpstrength - Die neue Sprungst�rke
	 */
	public void setHorse_jumpstrength(Double horse_jumpstrength) {
		this.horse_jumpstrength = horse_jumpstrength;
	}
	/**
	 * �ndert den Style des Pferdes.
	 * @param horse_style - Der Style des Pferdes.
	 */
	public void setHorse_style(Style horse_style) {
		this.horse_style = horse_style;
	}
	/**
	 * �ndert die Variante des Pferdes.
	 * @param horse_variant - Die Variante des Pferdes.
	 */
	public void setHorse_variant(Variant horse_variant) {
		this.horse_variant = horse_variant;
	}
	/**
	 * �ndert die UniqueId des Tieres.
	 * @param uuid - Die neue UniqueId
	 */
	public void setUniqueId(String uuid) {
		this.uuid = uuid;
	}
	/**
	 * Setzt das Erstelldatum auf einen neuen Timestamp.
	 * @param created_at - Das neue Erstelldatum
	 */
	public void setCreated_at(Timestamp created_at) {
		this.created_at = created_at;
	}

	
	
	
}