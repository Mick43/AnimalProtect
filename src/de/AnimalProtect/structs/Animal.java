package de.AnimalProtect.structs;

import java.sql.Timestamp;
import java.util.Date;

import org.bukkit.entity.Horse.Style;
import org.bukkit.entity.Horse.Variant;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

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
	
	public Animal(AnimalProtect plugin) { this.plugin = plugin; }
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
	
	public boolean saveToDatabase(Boolean log) {
		if (plugin == null) { return false; }
		if (!plugin.getDatenbank().isConnected()) { return false; }
		
		Database database = plugin.getDatenbank();
		if (database.createAnimal(this)) {
			return true;
		}
		
		return false;
	}
	
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
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}
	/**
	 * @return the owner
	 */
	public Integer getOwner() {
		return owner;
	}
	/**
	 * @return the animaltype
	 */
	public AnimalType getAnimaltype() {
		return animaltype;
	}
	/**
	 * @return the last_x
	 */
	public Integer getLast_x() {
		return last_x;
	}
	/**
	 * @return the last_y
	 */
	public Integer getLast_y() {
		return last_y;
	}
	/**
	 * @return the last_z
	 */
	public Integer getLast_z() {
		return last_z;
	}
	/**
	 * @return the alive
	 */
	public Boolean getAlive() {
		return alive;
	}
	/**
	 * @return the nametag
	 */
	public String getNametag() {
		return nametag;
	}
	/**
	 * @return the maxhp
	 */
	public Float getMaxhp() {
		return maxhp;
	}
	/**
	 * @return the deathcause
	 */
	public DamageCause getDeathcause() {
		return deathcause;
	}
	/**
	 * @return the color
	 */
	public String getColor() {
		return color;
	}
	/**
	 * @return the armor
	 */
	public AnimalArmor getArmor() {
		return armor;
	}
	/**
	 * @return the horse_jumpstrength
	 */
	public Double getHorse_jumpstrength() {
		return horse_jumpstrength;
	}
	/**
	 * @return the horse_style
	 */
	public Style getHorse_style() {
		return horse_style;
	}
	/**
	 * @return the horse_variant
	 */
	public Variant getHorse_variant() {
		return horse_variant;
	}
	/**
	 * @return the uuid
	 */
	public String getUniqueId() {
		return uuid;
	}
	/**
	 * @return the created_at
	 */
	public Timestamp getCreated_at() {
		return created_at;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	/**
	 * @param owner the owner to set
	 */
	public void setOwner(Integer owner) {
		this.owner = owner;
	}
	/**
	 * @param animaltype the animaltype to set
	 */
	public void setAnimaltype(AnimalType animaltype) {
		this.animaltype = animaltype;
	}
	/**
	 * @param last_x the last_x to set
	 */
	public void setLast_x(Integer last_x) {
		this.last_x = last_x;
	}
	/**
	 * @param last_y the last_y to set
	 */
	public void setLast_y(Integer last_y) {
		this.last_y = last_y;
	}
	/**
	 * @param last_z the last_z to set
	 */
	public void setLast_z(Integer last_z) {
		this.last_z = last_z;
	}
	/**
	 * @param alive the alive to set
	 */
	public void setAlive(Boolean alive) {
		this.alive = alive;
	}
	/**
	 * @param nametag the nametag to set
	 */
	public void setNametag(String nametag) {
		this.nametag = nametag;
	}
	/**
	 * @param maxhp the maxhp to set
	 */
	public void setMaxhp(Float maxhp) {
		this.maxhp = maxhp;
	}
	/**
	 * @param deathcause the deathcause to set
	 */
	public void setDeathcause(DamageCause deathcause) {
		this.deathcause = deathcause;
	}
	/**
	 * @param color the color to set
	 */
	public void setColor(String color) {
		this.color = color;
	}
	/**
	 * @param armor the armor to set
	 */
	public void setArmor(AnimalArmor armor) {
		this.armor = armor;
	}
	/**
	 * @param horse_jumpstrength the horse_jumpstrength to set
	 */
	public void setHorse_jumpstrength(Double horse_jumpstrength) {
		this.horse_jumpstrength = horse_jumpstrength;
	}
	/**
	 * @param horse_style the horse_style to set
	 */
	public void setHorse_style(Style horse_style) {
		this.horse_style = horse_style;
	}
	/**
	 * @param horse_variant the horse_variant to set
	 */
	public void setHorse_variant(Variant horse_variant) {
		this.horse_variant = horse_variant;
	}
	/**
	 * @param uuid the uuid to set
	 */
	public void setUniqueId(String uuid) {
		this.uuid = uuid;
	}
	/**
	 * @param created_at the created_at to set
	 */
	public void setCreated_at(Timestamp created_at) {
		this.created_at = created_at;
	}

	
	
	
}