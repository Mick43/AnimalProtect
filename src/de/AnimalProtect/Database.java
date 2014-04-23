package de.AnimalProtect;

/* Java Imports */
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/* Bukkit Imports */
import org.bukkit.entity.Horse.Style;

/* CraftoPlugin Imports */
import craftoplugin.core.CraftoPlugin;
import craftoplugin.core.database.CraftoPlayer;
import craftoplugin.modules.general.GeneralModule;

/* AnimalProtect Imports */
import de.AnimalProtect.structs.Animal;
import de.AnimalProtect.structs.AnimalArmor;
import de.AnimalProtect.structs.AnimalType;
import de.AnimalProtect.structs.AnimalVariant;

public class Database {
	
   /* AnimalProtect  ---  Tabellenstruktur  ---  ap_entities
	* 
	* id                 - (INT)                   (PRIMARY KEY) (AUTO_INCREMENT)
	* owner              - (INT11)      (NOT NULL) 
	* animaltype         - (ENUM)       (NOT NULL) 
	* last_x             - (SMALLINT5)  (NOT NULL) 
	* last_y             - (SMALLINT3)  (NOT NULL) (UNSIGNED) 
	* last_z             - (SMALLINT5)  (NOT NULL) 
	* alive              - (BOOL)       (NOT NULL) 
	* nametag            - (VARCHAR255) (NOT NULL) 
	* maxhp              - (DOUBLE)     (NOT NULL) 
	* deathcause         - (ENUM)       (NOT NULL) 
	* color              - (VARCHAR40)  (NOT NULL) 
	* armor              - (ENUM)       (NOT NULL) 
	* horse_jumpstrength - (DOUBLE)     (NOT NULL) 
	* horse_style        - (ENUM)       (NOT NULL) 
	* horse_variant      - (ENUM)       (NOT NULL) 
	* uuid               - (CHAR36)     (NOT NULL) (UNIQUE KEY)
	* created_at         - (TIMESTAMP)  (NOT NULL) (DEFAULT CURRENT TIMESTAMP)
	*/
	
	private AnimalProtect plugin;
	private GeneralModule module;
	
	private MySQL connection;
	private final String hostname;
	private final String username;
	private final String dbname;
	private final String password;
	private final String port;
	
	private HashMap<UUID, Animal> entities;        // Tier(UUID)    <-> Tier
	private HashMap<UUID, ArrayList<Animal>> keys; // Spieler(UUID) <-> List<Tiere>
	private HashMap<UUID, UUID> reverseKeys;       // Tier(UUID)    <-> Spieler(UUID)
	
	/**
	 * Erstellt eine Datenbank-Instanz von AnimalProtect
	 * @param plugin - Das AnimalProtect-Plugin
	 */
	public Database(AnimalProtect plugin) {
		this.plugin = plugin;
		this.hostname = plugin.getConfig().getString("database.hostname");
		this.username = plugin.getConfig().getString("database.username");
		this.dbname = plugin.getConfig().getString("database.dbname");
		this.password = plugin.getConfig().getString("database.password");
		this.port = plugin.getConfig().getString("database.port");
		
		this.entities = new HashMap<UUID, Animal>();
		this.keys = new HashMap<UUID, ArrayList<Animal>>();
		this.reverseKeys = new HashMap<UUID, UUID>();
		
		this.connection = new MySQL(plugin, hostname, port, dbname, username, password, plugin.isDebugging());
		this.connection.openConnection();
		
		if (connection.checkConnection()) {
			this.createTable();
		}
	}
	
	private void createTable() {
		try {
			if (!isConnected()) { return; }
			
			String[] columns = new String[17];
			columns[0] = "id INT AUTO_INCREMENT PRIMARY KEY";
			columns[1] = "owner INT(11) NOT NULL";
			columns[2] = "animaltype ENUM('UNKNOWN', 'COW', 'CHICKEN', 'PIG', 'SHEEP', 'HORSE', 'WOLF', 'IRON_GOLEM', 'SNOWMAN', 'VILLAGER', 'OCELOT') NOT NULL";
			columns[3] = "last_x SMALLINT(5) NOT NULL";
			columns[4] = "last_y SMALLINT(3) UNSIGNED NOT NULL";
			columns[5] = "last_z SMALLINT(5) NOT NULL";
			columns[6] = "alive BOOL NOT NULL";
			columns[7] = "nametag VARCHAR(255) NOT NULL";
			columns[8] = "maxhp DOUBLE NOT NULL";
			columns[9] = "deathcause ENUM('NONE', 'CUSTOM', 'CONTACT', 'ENTITY_ATTACK', 'PROJECTILE', 'SUFFOCATION', 'FALL', 'FIRE', 'FIRE_TICK', 'MELTING', 'LAVA', 'DROWNING', 'BLOCK_EXPLOSION', 'ENTITY_EXPLOSION', 'VOID', 'LIGHTNING', 'SUICIDE', 'STARVATION', 'POISON', 'MAGIC', 'WITHER', 'FALLING_BLOCK', 'THORNS') NOT NULL";
			columns[10] = "color VARCHAR(40) NOT NULL";
			columns[11] = "armor ENUM('UNKNOWN', 'DIAMOND','GOLD','IRON') NOT NULL";
			columns[12] = "horse_jumpstrength DOUBLE NOT NULL";
			columns[13] = "horse_style ENUM('NONE', 'WHITE', 'WHITEFIELD', 'WHITE_DOTS', 'BLACK_DOTS') NOT NULL";
			columns[14] = "horse_variant ENUM('NONE', 'HORSE', 'DONKEY', 'MULE', 'UNDEAD_HORSE', 'SKELETON_HORSE') NOT NULL";
			columns[15] = "uuid CHAR(36) NOT NULL UNIQUE KEY";
			columns[16] = "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL";
			
			this.connection.createTable("ap_entities", columns, true);
			this.loadFromDatabase();
		}
		catch (Exception e) { Messenger.exception("Database/createTable", "An exception occured in de.AnimalProtect.Database.createTabel()", e); }
	}
	
	private void loadFromDatabase() {
		try {
			if (!isConnected()) { return; }
			
			/* Als erstes die CraftoPlayer's laden */
			if (CraftoPlugin.plugin.getModuleManager().containsModule("GeneralModule")) {
				this.module = (GeneralModule) CraftoPlugin.plugin.getModuleManager().getModule("GeneralModule");
				
				if (module == null) { 
					Messenger.warn("Warning: Failed to load players from the GeneralModule! (Module is null)");
					return; 
				}
				
				for(CraftoPlayer player : module.getDatabase().getPlayers()) {
					this.keys.put(player.getUniqueId(), new ArrayList<Animal>());
				}
			}
			
			
			/* Dann die Tiere laden */
			ResultSet result = connection.getResult("SELECT * FROM ap_entities", false, true);
			
			if (result != null) { 
				try {
					while (result.next()) {
						Animal animal = new Animal(plugin);
						animal.setId(result.getInt("id"));
						animal.setOwner(result.getInt("owner"));
						animal.setAnimaltype(AnimalType.valueOf(result.getString("animaltype")));
						animal.setLast_x(result.getInt("last_x"));
						animal.setLast_y(result.getInt("last_y"));
						animal.setLast_z(result.getInt("last_z"));
						animal.setAlive(result.getBoolean("alive"));
						animal.setMaxhp(result.getDouble("maxhp"));
						animal.setColor(result.getString("color"));
						animal.setArmor(AnimalArmor.valueOf(result.getString("armor")));
						animal.setHorse_jumpstrength(result.getDouble("horse_jumpstrength"));
						animal.setHorse_style(Style.valueOf(result.getString("horse_style")));
						animal.setHorse_variant(AnimalVariant.valueOf(result.getString("horse_variant")));
						animal.setUniqueId(UUID.fromString(result.getString("uuid")));
						animal.setCreated_at(result.getTimestamp("created_at"));
						
						CraftoPlayer owner = module.getDatabase().getPlayer(animal.getOwner());
						if (owner != null) {
							if (this.keys.containsKey(owner.getUniqueId())) {
								entities.put(animal.getUniqueId(), animal);
								reverseKeys.put(animal.getUniqueId(), owner.getUniqueId());
								keys.get(owner.getUniqueId()).add(animal);
							}
							else 
							{ Messenger.warn("Warning: An animal could not be loaded because the owner is not in the owners hashmap! (AnimalId: " +animal.getId()+ ") (OwnerId: " +owner.getId() +")"); }
						}
						else 
						{ Messenger.warn("Warning: An animal could not be loaded because the the owner does not exist! (AnimalId: " +animal.getId()+ ")"); }
					}
				}  
				catch (SQLException e) 
				{ Messenger.exception("Database.java/loadFromDatabase", "Exception caught while trying to load every entity from the database.", e); }
			}
			else 
			{ Messenger.warn("Warning: Failed to load every entity from the database! (ResultSet is null)"); }
		}
		catch (Exception e) { Messenger.exception("Database/loadFromDatabase", "An exception occured in de.AnimalProtect.Database.loadFromDatabase()", e); }
	}
	
	/**
	 * Startet die Verbindung zur Datenbank neu.
	 */
	public void connect() {
		try {
			if (!isConnected()) {
				connection.openConnection();
			}
		}
		catch (Exception e) { Messenger.exception("Database/connect", "An exception occured in de.AnimalProtect.Database.connect()", e); }
	}
	
	/**
	 * Schließt die Verbindung zur Datenbank.
	 */
	public void closeConnection() {
		if (isConnected()) {
			connection.closeConnection();
			this.entities.clear();
			this.keys.clear();
			this.reverseKeys.clear();
		}
	}
	
	/**
	 * Fügt ein Tier der Datenbank hinzu, oder aktualisiert seine Werte.
	 * @param animal - Das Tier welches aktualisiert/eingefügt werden soll.
	 * @return True, falls die Aktion ohne Probleme funktioniert hat.
	 */
	public boolean insertAnimal(Animal animal) {
		if (animal == null) { return false; }
		
		try {
			/* Query zum updaten/inserten aufbauen */
			String Query = "INSERT INTO ap_entities (`owner`, `animaltype`, `last_x`, `last_y`, `last_z`, `alive`, `nametag`, `maxhp`, "
						 + "`deathcause`, `color`, `armor`, `horse_jumpstrength`, `horse_style`, `horse_variant`, `uuid`) "
						 + "VALUES ("+animal.getOwner()+", '"+animal.getAnimaltype().toString()+"', "+animal.getLast_x()+", "+animal.getLast_y()+", "
						 		 + ""+animal.getLast_z()+", "+animal.isAlive()+", '"+animal.getNametag()+"', "+animal.getMaxhp()+", "
						 		 + "'"+animal.getDeathcauseToString()+"', '"+animal.getColorToString()+"', '"+animal.getArmor()+"', "+animal.getHorse_jumpstrength()+", "
						 		 + "'"+animal.getHorse_styleToString()+"', '"+animal.getHorse_variantToString()+"', '"+animal.getUniqueId()+"')"
						 + "ON DUPLICATE KEY UPDATE owner="+animal.getOwner()+", last_x="+animal.getLast_x()+", last_y="+animal.getLast_y()+", "
						 		 + "last_z="+animal.getLast_z()+", alive="+animal.isAlive()+", nametag='"+animal.getNametag()+"', "
						 		 + "deathcause='"+animal.getDeathcauseToString()+"', color='"+animal.getColorToString()+"', armor='"+animal.getArmor().toString()+"';";
			
			/* Query ausführen und das Ergebnis returnen */
			if(connection.execute(Query, plugin.isDebugging())) { 
				CraftoPlayer owner = CraftoPlayer.getPlayer(animal.getOwner());
				if (owner != null && keys.containsKey(owner.getUniqueId())) {
					/* Den HashMaps hinzufügen */					
					entities.put(animal.getUniqueId(), animal);
					reverseKeys.put(animal.getUniqueId(), owner.getUniqueId());
					keys.get(owner.getUniqueId()).add(animal);
				}
				else { Messenger.error("Warning: Failed to insert an animal because the owner does not exist. (AnimalUUID="+animal.getUniqueId().toString()+")"); }

				return true; 
			}
		}
		catch (Exception e) { Messenger.exception("Database.java/insertAnimal", "An Error occured while trying to insert an entity.", e); }
		
		return false;
	}
	
	/**
	 * Updatet alle Eigenschaften des Entities in der Datenbank.
	 * @param id - Die Id des Entities.
	 * @param animal - Das Animal-Objekt, von dem alle Eigenschaften genommen werden.
	 * @param oldUniqueId - Die alte UniqueId des Tieres.
	 * @return True, falls das Updaten erfolgreich war.
	 */
	public boolean updateAnimal(Integer id, Animal animal, UUID oldUniqueId) {
		if (id == null) { return false; }
		
		try {
			/* Query zum updaten/inserten aufbauen */
			String Query = "UPDATE `ap_entities` "
					     + "SET `owner`="+animal.getOwner()+",`animaltype`='"+animal.getAnimaltype()+"',`last_x`="+animal.getLast_x()+","
					     + "`last_y`="+animal.getLast_y()+",`last_z`="+animal.getLast_z()+", `alive`="+animal.isAlive()+","
					     + "`nametag`='"+animal.getNametag()+"',`maxhp`="+animal.getMaxhp()+",`deathcause`='"+animal.getDeathcauseToString()+"',"
					     + "`color`='"+animal.getColorToString()+"',`armor`='"+animal.getArmor()+"', "
					     + "`horse_jumpstrength`="+animal.getHorse_jumpstrength()+",`horse_style`='"+animal.getHorse_styleToString()+"',"
					     + "`horse_variant`='"+animal.getHorse_variantToString()+"',`uuid`='"+animal.getUniqueId().toString()+"'"
					     + " WHERE id="+id+";";
			
			/* Query ausführen und das Ergebnis returnen */
			if(connection.execute(Query, plugin.isDebugging())) {
				CraftoPlayer owner = CraftoPlayer.getPlayer(animal.getOwner());
				if (owner != null && keys.containsKey(owner.getUniqueId())) {
					/* HashMaps updaten */
					entities.remove(oldUniqueId);
					reverseKeys.remove(oldUniqueId);
					keys.get(owner.getUniqueId()).remove(animal);
					
					entities.put(animal.getUniqueId(), animal);
					reverseKeys.put(animal.getUniqueId(), owner.getUniqueId());
					keys.get(owner.getUniqueId()).add(animal);
				}
				else { Messenger.error("Warning: Failed to update an animal because the owner does not exist. (AnimalId="+animal.getId()+")"); }

				return true; 
			}
		}
		catch (Exception e) { Messenger.exception("Database.java/updateAnimal", "An Error occured while trying to update an entity.", e); }
		
		return false;
	}
	
	/**
	 * Entsichert ein Tier und löscht es aus der Datenbank
	 * @param animal - Das Animal-Objekt
	 * @return True, falls das entsichern ohne Probleme geklappt hat.
	 */
	public boolean unlockAnimal(Animal animal) {
		if (animal == null) { return false; }
		
		try {
			/* Query zum updaten/inserten aufbauen */
			String Query = "DELETE FROM `ap_entities` WHERE id="+animal.getId()+";";
			
			/* Query ausführen und das Ergebnis returnen */
			if(connection.execute(Query, plugin.isDebugging())) {
				CraftoPlayer owner = CraftoPlayer.getPlayer(animal.getOwner());
				if (owner != null && keys.containsKey(owner.getUniqueId())) {
					/* HashMaps updaten */
					entities.remove(animal.getUniqueId());
					reverseKeys.remove(animal.getUniqueId());
					keys.get(owner.getUniqueId()).remove(animal);
				}
				return true; 
			}
		}
		catch (Exception e) { Messenger.exception("Database.java/unlockAnimal", "An Error occured while trying to unlock an entity.", e); }
		
		return false;
	}
	
	/**
	 * Gibt das Animal mit der angegeben UniqueId zurück.
	 * @param uuid - Die UniqueId, nach der gesucht wird.
	 * @return Gibt das Animal wieder, oder null, falls keins gefunden wurde.
	 */
	public Animal getAnimal(UUID uuid) {
		if (uuid == null) { return null; }
		
		if (entities.containsKey(uuid)) {
			return entities.get(uuid);
		}
		return null;
	}
	
	/**
	 * Gibt den Owner des Tieres mit der angegeben UniqueId wieder
	 * @param uuid - Die UniqueId, nach der gesucht wird.
	 * @return Gibt den CraftoPlayer wieder, oder null, falls keiner gefunden wurde.
	 */
	public CraftoPlayer getOwner(UUID uuid) {
		if (uuid == null) { return null; }
		
		if (reverseKeys.containsKey(uuid)) {
			CraftoPlayer player = CraftoPlayer.getPlayer(reverseKeys.get(uuid));
			
			if (player != null) {
				return player;
			}
		}
		
		return null;
	}
	
	/**
	 * Gibt alle Tiere, die dem Spieler mit der angegeben Id gehören wieder
	 * @param uuid - Die UniqueId des Spielers
	 * @return Gibt eine Liste der gesicherten Tiere zurück.
	 */
	public ArrayList<Animal> getAnimals(UUID uuid) {
		if (uuid == null) { return null; }
		
		if  (keys.containsKey(uuid)) {
			return keys.get(uuid);
		}
		
		return null;
	}
	
	/**
	 * Sucht das Tier mit der angegebenen UniqueId.
	 * @param uuid - Die UniqueId, nach der gesucht werden soll.
	 * @return True, falls das Tier gefunden wurde.
	 */
	public boolean containsAnimal(UUID uuid) {
		if (uuid == null) { return false; }
		if (module == null) { return false; }
		
		if (entities.containsKey(uuid)) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Sucht den Spieler mit der angegebenen UniqueId.
	 * @param uuid - Die UniqueId, nach der gesucht werden soll.
	 * @return True, falls der Spieler gefunden wurde.
	 */
	public boolean containsPlayer(UUID uuid) {
		if (uuid == null) { return false; }
		
		if (module.getDatabase().containsPlayer(uuid)) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Gibt die aktuelle Anzahl der gesicherten Tiere wieder.
	 * @return Die aktuelle Anzahl als Integer.
	 */
	public int getLockedAnimals() {
		return this.entities.size();
	}
	
	/**
	 * Gibt alle fehlgeschlagenen Queries zurück.
	 * @return Eine ArrayList<String> an Queries.
	 */
	public ArrayList<String> getFailedQueries() {
		return this.connection.getFailedQueries();
	}
	
	/**
	 * Prüft, ob die Datenbank-Verbindung aktiv ist.
	 * @return True, wenn die Verbindung steht.
	 */
	public boolean isConnected() {
		if (connection == null) { return false; }
		return connection.checkConnection();
	}
}
