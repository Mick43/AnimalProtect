package de.AnimalProtect;

/* Java Imports */
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

/* Bukkit Imports */
import org.bukkit.entity.Horse.Style;

/* CraftoPlugin Imports */
import craftoplugin.core.CraftoPlugin;
import craftoplugin.core.database.CraftoPlayer;
import craftoplugin.core.database.MySQL;
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

	/** Die AnimalProtect-Instanz. */
	private final AnimalProtect plugin;
	/** Die Verbindung zur Datenbank. */
	private final MySQL connection;

	/** Eine Map in der alle Tiere zu ihren UniqueId's gemappt werden. */
	private final HashMap<UUID, Animal> entities;        // Tier(UUID)    <-> Tier
	/** Eine Map in der alle Tiere zu ihren Ownern gemappt werden. */
	private final HashMap<UUID, ArrayList<Animal>> keys; // Spieler(UUID) <-> List<Tiere>
	/** Eine Map in der alle Owner zu ihren Tieren gemappt werden. */
	private final HashMap<UUID, UUID> reverseKeys;       // Tier(UUID)    <-> Spieler(UUID)
	/** Eine map in der alle Tiere zu ihren Datenbank-Id's gemappt werden. */
	private final HashMap<Integer, Animal> entitiesId;  // Tier(ID)      <-> Tier

	/**
	 * Erstellt eine Datenbank-Instanz von AnimalProtect
	 * @param plugin - Das AnimalProtect-Plugin
	 */
	public Database(final AnimalProtect plugin) {
		this.plugin = plugin;

		this.entities = new HashMap<UUID, Animal>();
		this.keys = new HashMap<UUID, ArrayList<Animal>>();
		this.reverseKeys = new HashMap<UUID, UUID>();
		this.entitiesId = new HashMap<Integer, Animal>();

		this.connection = CraftoPlugin.instance.getDatenbank().getSQL();

		if (this.connection.checkConnection()) { this.createTable(); }
	}

	/**
	 * Erstellt die Tabelle in der Datenbank.
	 */
	private void createTable() {
		try {
			if (!this.isConnected()) { return; }

			final String[] columns = new String[17];
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
			columns[10] = "color VARCHAR(32) NOT NULL";
			columns[11] = "armor ENUM('UNKNOWN', 'DIAMOND','GOLD','IRON') NOT NULL";
			columns[12] = "horse_jumpstrength DOUBLE NOT NULL";
			columns[13] = "horse_style ENUM('NONE', 'WHITE', 'WHITEFIELD', 'WHITE_DOTS', 'BLACK_DOTS') NOT NULL";
			columns[14] = "horse_variant ENUM('NONE', 'HORSE', 'DONKEY', 'MULE', 'UNDEAD_HORSE', 'SKELETON_HORSE') NOT NULL";
			columns[15] = "uuid CHAR(36) NOT NULL UNIQUE KEY";
			columns[16] = "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL";

			this.connection.createTable("ap_entities", columns, false);
			this.loadFromDatabase();
		}
		catch (final Exception e) { Messenger.exception("Database/createTable", "An exception occured in de.AnimalProtect.Database.createTabel()", e); }
	}

	/**
	 * Lädt alle Entities aus der Datenbank und speichert sie in den Arbeitsspeicher.
	 */
	private void loadFromDatabase() {
		try {
			if (!this.isConnected()) { return; }
			final Long loadStart = System.currentTimeMillis();

			/* Als erstes die CraftoPlayer's laden */
			if (CraftoPlugin.instance.getDatenbank().getPlayerCount() > 0) {
				for(final CraftoPlayer player : CraftoPlugin.instance.getDatenbank().getPlayers()) {
					this.keys.put(player.getUniqueId(), new ArrayList<Animal>());
				}
			}
			else { Messenger.error("Warning: Failed to load players from the Database! (CraftoPlugin.getPlayerCount < 0)"); return; }


			/* Dann die Tiere laden */
			final ResultSet result = this.connection.getResult("SELECT * FROM ap_entities", false, false);

			if (result != null) {
				try {
					while (result.next() && loadStart + 60000 > System.currentTimeMillis()) {
						try {
							final Animal animal = new Animal(this.plugin);
							animal.setId(result.getInt("id"));
							animal.setOwner(result.getInt("owner"));
							animal.setAnimaltype(AnimalType.valueOf(result.getString("animaltype")));
							animal.setX(result.getInt("last_x"));
							animal.setY(result.getInt("last_y"));
							animal.setZ(result.getInt("last_z"));
							animal.setAlive(result.getBoolean("alive"));
							animal.setMaxhp(result.getDouble("maxhp"));
							animal.setColor(result.getString("color"));
							animal.setArmor(AnimalArmor.valueOf(result.getString("armor")));
							animal.setHorse_jumpstrength(result.getDouble("horse_jumpstrength"));
							animal.setHorse_style(Style.valueOf(result.getString("horse_style")));
							animal.setHorse_variant(AnimalVariant.valueOf(result.getString("horse_variant")));
							animal.setUniqueId(UUID.fromString(result.getString("uuid")));
							animal.setCreated_at(result.getTimestamp("created_at"));

							final CraftoPlayer owner = CraftoPlugin.instance.getDatenbank().getPlayer(animal.getOwner());
							if (owner != null) {
								if (this.keys.containsKey(owner.getUniqueId())) {
									this.entities.put(animal.getUniqueId(), animal);
									this.reverseKeys.put(animal.getUniqueId(), owner.getUniqueId());
									this.keys.get(owner.getUniqueId()).add(animal);
									this.entitiesId.put(animal.getId(), animal);
								}
								else
								{ Messenger.warn("Warning: An animal could not be loaded because the owner is not in the owners hashmap! (AnimalId: " +animal.getId()+ ") (OwnerId: " +owner.getId() +")"); }
							}
							else
							{ Messenger.warn("Warning: An animal could not be loaded because the the owner does not exist! (AnimalId: " +animal.getId()+ ")"); }
						}
						catch (final Exception e) { Messenger.exception("Database/loadFromDatabase", "An animal could not be loaded because an unknown error occured.", e); }
					}
				}
				catch (final SQLException e)
				{ Messenger.exception("Database.java/loadFromDatabase", "Exception caught while trying to load every entity from the database.", e); }
			}
			else
			{ Messenger.warn("Warning: Failed to load every entity from the database! (ResultSet is null)"); }

			if (loadStart + 60000 < System.currentTimeMillis())
			{ Messenger.warn("Warning: AnimalProtect took to long to load all animals from the database!"); }
		}
		catch (final Exception e) { Messenger.exception("Database/loadFromDatabase", "An exception occured in de.AnimalProtect.Database.loadFromDatabase()", e); }
	}

	/**
	 * Startet die Verbindung zur Datenbank neu.
	 */
	public void connect() {
		try {
			if (!this.isConnected()) {
				this.connection.openConnection();
			}
		}
		catch (final Exception e) { Messenger.exception("Database/connect", "An exception occured in de.AnimalProtect.Database.connect()", e); }
	}

	/**
	 * Schließt die Verbindung zur Datenbank.
	 */
	public void closeConnection() {
		if (this.isConnected()) {
			this.connection.closeConnection();
		}
	}

	/**
	 * Leert alle Eintrï¿½ge im Arbeitsspeicher.
	 */
	public void clear() {
		this.entities.clear();
		this.keys.clear();
		this.reverseKeys.clear();
	}

	/**
	 * Fï¿½gt einen Spieler der Spielerliste hinzu, falls er beim Start noch nicht geladen wurde.
	 * @param uuid - Die UniqueId des Spielers, der eingefï¿½gt werden soll.
	 * @return True, falls die Aktion ohne Probleme funktioniert hat.
	 */
	public boolean insertPlayer(final UUID uuid) {
		if (uuid == null) { return false; }

		try {
			if (!this.keys.containsKey(uuid)) {
				this.keys.put(uuid, new ArrayList<Animal>());
				return true;
			}
		}
		catch (final Exception e) { Messenger.exception("Database.java/insertPlayer", "Unknown exception occured while trying to insert a player", e); }

		return false;
	}

	/**
	 * Fï¿½gt ein Tier der Datenbank hinzu, oder aktualisiert seine Werte.
	 * @param animal - Das Tier welches aktualisiert/eingefï¿½gt werden soll.
	 * @return True, falls die Aktion ohne Probleme funktioniert hat.
	 */
	public boolean insertAnimal(final Animal animal) {
		if (animal == null) { return false; }

		try {
			if (animal.getId() == null) {
				/* Query zum updaten/inserten aufbauen */
				final String Query = this.getInsertQuery(animal);

				/* Query ausfï¿½hren und das Ergebnis returnen */
				if(this.connection.execute(Query, this.plugin.isDebugging())) {
					final CraftoPlayer owner = CraftoPlayer.getPlayer(animal.getOwner());
					if (owner != null && this.keys.containsKey(owner.getUniqueId())) {
						/* Den HashMaps hinzufï¿½gen */
						this.entities.put(animal.getUniqueId(), animal);
						this.reverseKeys.put(animal.getUniqueId(), owner.getUniqueId());
						this.keys.get(owner.getUniqueId()).add(animal);
						this.entitiesId.put(animal.getId(), animal);

						final Integer id = (Integer) this.connection.getValue("SELECT id FROM ap_entities WHERE uuid='"+animal.getUniqueId()+"';", "id", true);
						animal.setId(id);
					}
					else { Messenger.error("Warning: Failed to insert an animal because the owner does not exist. (AnimalUUID="+animal.getUniqueId().toString()+")"); }

					return true;
				}
			}
			else {
				/* Query zum updaten/inserten aufbauen */
				final String Query = this.getUpdateQuery(animal);

				/* Query ausfï¿½hren und das Ergebnis returnen */
				if (this.plugin.getQueue().isRunning()) { this.plugin.getQueue().insertQuery(Query); }
				else { this.connection.execute(Query, true); }
				return true;
			}
		}
		catch (final Exception e) { Messenger.exception("Database.java/insertAnimal", "An Error occured while trying to insert an entity.", e); }

		return false;
	}

	/**
	 * Gibt die Insertquery, für die Datenbank, des angegebenen Tieres zurück.
	 * @param animal - Das Tier, aus dem die Werte genommen werden.
	 * @return Eine SQL-Query für die Datenbank.
	 */
	private String getInsertQuery(final Animal animal) {
		final String Query = "INSERT INTO `ap_entities` (`owner`, `animaltype`, `last_x`, `last_y`, `last_z`, `alive`, `nametag`, `maxhp`, "
				+ "`deathcause`, `color`, `armor`, `horse_jumpstrength`, `horse_style`, `horse_variant`, `uuid`) "
				+ "VALUES ("+animal.getOwner()+", '"+animal.getAnimaltype().toString()+"', "+animal.getX()+", "+animal.getY()+", "
				+ ""+animal.getZ()+", "+animal.isAlive()+", '"+animal.getNametag()+"', "+animal.getMaxhp()+", "
				+ "'"+animal.getDeathcauseToString()+"', '"+animal.getColorToString()+"', '"+animal.getArmor()+"', "+animal.getHorse_jumpstrength()+", "
				+ "'"+animal.getHorse_styleToString()+"', '"+animal.getHorse_variantToString()+"', '"+animal.getUniqueId()+"') "
				+ "ON DUPLICATE KEY UPDATE owner="+animal.getOwner()+", last_x="+animal.getX()+", last_y="+animal.getY()+", "
				+ "last_z="+animal.getZ()+", alive="+animal.isAlive()+", nametag='"+animal.getNametag()+"', "
				+ "deathcause='"+animal.getDeathcauseToString()+"', color='"+animal.getColorToString()+"', armor='"+animal.getArmor().toString()+"';";
		return Query;
	}

	/**
	 * Gibt eine Updatequery, für die Datenbank, des angegebenen Tieres wieder.
	 * @param animal - Das Tier, aus dem die Werte genommen werden.
	 * @return Eine SQL-Query für die Datenbank.
	 */
	private String getUpdateQuery(final Animal animal) {
		final String Query = "UPDATE `ap_entities` SET "
				+ "`owner`=" + animal.getOwner()                           + ", "
				+ "`last_x`=" + animal.getX()                              + ", "
				+ "`last_y`=" + animal.getY()                              + ", "
				+ "`last_z`=" + animal.getZ()                              + ", "
				+ "`alive`=" + animal.isAlive()                            + ", "
				+ "`nametag`='" + animal.getNametag()                      + "', "
				+ "`deathcause`='" + animal.getDeathcauseToString()        + "', "
				+ "`color`='" + animal.getColorToString()                  + "', "
				+ "`armor`='" + animal.getArmor()                          + "' "
				+ "WHERE `id` = " + animal.getId() + ";";
		return Query;
	}

	/**
	 * Updatet alle Eigenschaften des Entities in der Datenbank.
	 * @param id - Die Id des Entities.
	 * @param animal - Das Animal-Objekt, von dem alle Eigenschaften genommen werden.
	 * @param oldUniqueId - Die alte UniqueId des Tieres.
	 * @return True, falls das Updaten erfolgreich war.
	 */
	public boolean updateAnimal(final Integer id, final Animal animal, final UUID oldUniqueId) {
		if (id == null) { return false; }

		try {
			/* Query zum updaten/inserten aufbauen */
			final String Query = "UPDATE `ap_entities` SET "
					+ "`owner`=" + animal.getOwner()                           + ", "
					+ "`animaltype`='" + animal.getAnimaltype()                + "', "
					+ "`last_x`=" + animal.getX()                              + ", "
					+ "`last_y`=" + animal.getY()                              + ", "
					+ "`last_z`=" + animal.getZ()                              + ", "
					+ "`alive`=" + animal.isAlive()                            + ", "
					+ "`nametag`='" + animal.getNametag()                      + "', "
					+ "`maxhp`=" + animal.getMaxhp()                           + ", "
					+ "`deathcause`='" + animal.getDeathcauseToString()        + "', "
					+ "`color`='" + animal.getColorToString()                  + "', "
					+ "`armor`='" + animal.getArmor()                          + "', "
					+ "`horse_jumpstrength`=" + animal.getHorse_jumpstrength() + ", "
					+ "`horse_style`='" + animal.getHorse_styleToString()      + "', "
					+ "`horse_variant`='" + animal.getHorse_variantToString()  + "', "
					+ "`uuid`='" + animal.getUniqueId().toString()             + "' "
					+ "WHERE `id` = " + id + ";";

			/* Query ausfï¿½hren und das Ergebnis returnen */
			if(this.connection.execute(Query, this.plugin.isDebugging())) {
				final CraftoPlayer owner = CraftoPlayer.getPlayer(animal.getOwner());
				if (owner != null && this.keys.containsKey(owner.getUniqueId())) {
					/* HashMaps updaten */
					this.entities.remove(oldUniqueId);
					this.reverseKeys.remove(oldUniqueId);
					this.keys.get(owner.getUniqueId()).remove(animal);

					this.entities.put(animal.getUniqueId(), animal);
					this.reverseKeys.put(animal.getUniqueId(), owner.getUniqueId());
					this.keys.get(owner.getUniqueId()).add(animal);
				}
				else { Messenger.error("Warning: Failed to update an animal because the owner does not exist. (AnimalId="+animal.getId()+")"); }

				return true;
			}
		}
		catch (final Exception e) { Messenger.exception("Database.java/updateAnimal", "An Error occured while trying to update an entity.", e); }

		return false;
	}

	/**
	 * Entsichert ein Tier und lï¿½scht es aus der Datenbank
	 * @param animal - Das Animal-Objekt
	 * @return True, falls das entsichern ohne Probleme geklappt hat.
	 */
	public boolean unlockAnimal(final Animal animal) {
		if (animal == null) { return false; }

		try {
			/* Query zum updaten/inserten aufbauen */
			final String Query = "DELETE FROM `ap_entities` WHERE `id` = "+animal.getId()+";";

			final CraftoPlayer owner = CraftoPlayer.getPlayer(animal.getOwner());
			if (owner != null && this.keys.containsKey(owner.getUniqueId())) {
				/* HashMaps updaten */
				this.entities.remove(animal.getUniqueId());
				this.reverseKeys.remove(animal.getUniqueId());
				this.keys.get(owner.getUniqueId()).remove(animal);
				this.entitiesId.remove(animal.getId());
			}
			if (this.plugin.getQueue().isRunning()) { this.plugin.getQueue().insertQuery(Query); }
			else { this.connection.execute(Query, true); }
			return true;
		}
		catch (final Exception e) { Messenger.exception("Database.java/unlockAnimal", "An Error occured while trying to unlock an entity.", e); }

		return false;
	}

	/**
	 * Gibt das Animal mit der angegeben UniqueId zurï¿½ck.
	 * @param uuid - Die UniqueId, nach der gesucht wird.
	 * @return Gibt das Animal wieder, oder null, falls keins gefunden wurde.
	 */
	public Animal getAnimal(final UUID uuid) {
		if (uuid == null) { return null; }

		if (this.entities.containsKey(uuid)) {
			return this.entities.get(uuid);
		}
		return null;
	}

	/**
	 * Gibt das Animal mit der angegeben Id zurï¿½ck.
	 * @param id - Die Datenbank-ID, nach der gesucht wird.
	 * @return Gibt das Animal wieder, oder null, falls keins gefunden wurde.
	 */
	public Animal getAnimal(final Integer id) {
		if (id == null) { return null; }

		if (this.entitiesId.containsKey(id)) {
			return this.entitiesId.get(id);
		}
		return null;
	}

	/**
	 * Gibt den Owner des Tieres mit der angegeben UniqueId wieder
	 * @param uuid - Die UniqueId, nach der gesucht wird.
	 * @return Gibt den CraftoPlayer wieder, oder null, falls keiner gefunden wurde.
	 */
	public CraftoPlayer getOwner(final UUID uuid) {
		if (uuid == null) { return null; }

		if (this.reverseKeys.containsKey(uuid)) {
			final CraftoPlayer player = CraftoPlayer.getPlayer(this.reverseKeys.get(uuid));

			if (player != null) {
				return player;
			}
		}

		return null;
	}

	/**
	 * Gibt alle Tiere, die dem Spieler mit der angegeben Id gehï¿½ren wieder
	 * @param uuid - Die UniqueId des Spielers
	 * @return Gibt eine Liste der gesicherten Tiere zurï¿½ck.
	 */
	public ArrayList<Animal> getAnimals(final UUID uuid) {
		if (uuid == null) { return null; }

		if  (this.keys.containsKey(uuid)) {
			final ArrayList<Animal> key = this.keys.get(uuid);
			Collections.sort(key);
			return key;
		}

		return null;
	}

	/**
	 * Sucht das Tier mit der angegebenen UniqueId.
	 * @param uuid - Die UniqueId, nach der gesucht werden soll.
	 * @return True, falls das Tier gefunden wurde.
	 */
	public boolean containsAnimal(final UUID uuid) {
		if (uuid == null) { return false; }

		if (this.entities.containsKey(uuid)) {
			return true;
		}

		return false;
	}

	/**
	 * Sucht den Spieler mit der angegebenen UniqueId.
	 * @param uuid - Die UniqueId, nach der gesucht werden soll.
	 * @return True, falls der Spieler gefunden wurde.
	 */
	public boolean containsPlayer(final UUID uuid) {
		if (uuid == null) { return false; }

		if (CraftoPlugin.instance.getDatenbank().containsPlayer(uuid)) {
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
	 * Gibt alle fehlgeschlagenen Queries zurï¿½ck.
	 * @return Eine ArrayList<String> an Queries.
	 */
	public ArrayList<String> getFailedQueries() {
		return this.connection.getFailedQueries();
	}

	/**
	 * Zählt die Anzahl der lebenden gelockten Tiere eines Spielers.
	 * @param player - Die UniqueID des Spielers.
	 * @return Die Anzahl.
	 */
	public int countAnimals(final UUID player) {
		int count = 0;
		for(final Animal animal : this.keys.get(player)) {
			if (animal.isAlive()) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Prï¿½ft, ob die Datenbank-Verbindung aktiv ist.
	 * @return True, wenn die Verbindung steht.
	 */
	public boolean isConnected() {
		if (this.connection == null) { return false; }
		return this.connection.checkConnection();
	}

	/**
	 * Gibt die MySQL-Verbindung von AnimalProtect wieder.
	 * @return Das, in dieser Klasse verwendete, MySQL-Objekt.
	 */
	public MySQL getConnection() {
		return this.connection;
	}
}