package de.AnimalProtect;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.entity.Horse.Style;
import org.bukkit.entity.Horse.Variant;

import craftoplugin.core.CraftoMessenger;
import craftoplugin.core.CraftoPlugin;
import craftoplugin.core.database.CraftoPlayer;
import craftoplugin.modules.general.GeneralModule;
import de.AnimalProtect.structs.Animal;
import de.AnimalProtect.structs.AnimalArmor;
import de.AnimalProtect.structs.AnimalType;

public class Database {
	
	private AnimalProtect plugin;
	private GeneralModule module;
	
	private MySQL connection;
	private final String hostname;
	private final String username;
	private final String dbname;
	private final String password;
	private final String port;
	
	private HashMap<String, Animal> entities;
	private HashMap<CraftoPlayer, ArrayList<Animal>> owners;
	
	public Database(AnimalProtect plugin) {
		this.plugin = plugin;
		this.hostname = plugin.getConfig().getString("database.hostname");
		this.username = plugin.getConfig().getString("database.username");
		this.dbname = plugin.getConfig().getString("database.dbname");
		this.password = plugin.getConfig().getString("database.password");
		this.port = plugin.getConfig().getString("database.port");
		
		this.connection = new MySQL(plugin, hostname, port, dbname, username, password, plugin.isDebugging());
		this.connection.openConnection();
		
		if (connection.checkConnection()) {
			this.createTable();
		}
	}
	
	private void createTable() {
		if (!isConnected()) { return; }
		
		String[] columns = new String[17];
		columns[0] = "id INT AUTO_INCREMENT PRIMARY KEY";
		columns[1] = "owner INT(11) NOT NULL";
		columns[2] = "animaltype ENUM('UNKNOWN', 'COW', 'CHICKEN', 'PIG', 'SHEEP', 'HORSE', 'WOLF') NOT NULL";
		columns[3] = "last_x SMALLINT(5) NOT NULL";
		columns[4] = "last_y SMALLINT(3) UNSIGNED NOT NULL";
		columns[5] = "last_z SMALLINT(5) NOT NULL";
		columns[6] = "alive BOOL NOT NULL";
		columns[7] = "nametag VARCHAR(255)";
		columns[8] = "maxhp FLOAT";
		columns[9] = "deathcause ENUM('CUSTOM', 'CONTACT', 'ENTITY_ATTACK', 'PROJECTILE', 'SUFFOCATION', 'FALL', 'FIRE', 'FIRE_TICK', 'MELTING', 'LAVA', 'DROWNING', 'BLOCK_EXPLOSION', 'ENTITY_EXPLOSION', 'VOID', 'LIGHTNING', 'SUICIDE', 'STARVATION', 'POISON', 'MAGIC', 'WITHER', 'FALLING_BLOCK', 'THORNS')";
		columns[10] = "color VARCHAR(40) NOT NULL";
		columns[11] = "armor ENUM('UNKNOWN', 'DIAMOND','GOLD','IRON') NOT NULL";
		columns[12] = "horse_jumpstrength DOUBLE NOT NULL";
		columns[13] = "horse_style ENUM('NONE', 'WHITE', 'WHITEFIELD', 'WHITE_DOTS', 'BLACK_DOTS') NOT NULL";
		columns[14] = "horse_variant ENUM('NONE', 'HORSE', 'DONKEY', 'MULE', 'UNDEAD_HORSE', 'SKELETON_HORSE') NOT NULL";
		columns[15] = "uuid CHAR(36) NOT NULL UNIQUE KEY";
		columns[16] = "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP";
		
		connection.createTable("ap_entities", columns, true);
	}
	
	private void loadFromDatabase() {
		if (!isConnected()) { return; }
		
		/* Als erstes die CraftoPlayer's laden */
		if (CraftoPlugin.plugin.getModuleManager().containsModule("GeneralModule")) {
			this.module = (GeneralModule) CraftoPlugin.plugin.getModuleManager().getModule("GeneralModule");
			
			if (module == null) { return; }
			
			for(CraftoPlayer player : module.getDatabase().getPlayers()) {
				this.owners.put(player, new ArrayList<Animal>());
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
					animal.setMaxhp(result.getFloat("maxhp"));
					animal.setColor(result.getString("color"));
					animal.setArmor(AnimalArmor.valueOf(result.getString("armor")));
					animal.setHorse_jumpstrength(result.getDouble("horse_jumpstrength"));
					animal.setHorse_style(Style.valueOf(result.getString("horse_style")));
					animal.setHorse_variant(Variant.valueOf(result.getString("horse_variant")));
					animal.setUniqueId(result.getString("uuid"));
					animal.setCreated_at(result.getTimestamp("created_at"));
					
					CraftoPlayer owner = module.getDatabase().getPlayer(animal.getOwner());
					if (owner != null) {
						if (this.owners.containsKey(owner)) {
							entities.put(animal.getUniqueId(), animal);
							owners.get(owner).add(animal);
						}
						else { CraftoMessenger.warn("Warning: An animal could not be loaded because the owner is not in the owners hashmap!"); }
					}
					else { CraftoMessenger.warn("Warning: An animal could not be loaded because the the owner does not exist!"); }
				}
			} 
			catch (SQLException e) {
				Messenger.exception("Database.java", "Exception caught while trying to load every entity from the database.", e);
			}
		}
	}
	
	public boolean createAnimal(Animal animal) {
		String Query = "INSERT INTO ap_entities (`owner`, `animaltype`, `last_x`, `last_y`, `last_z`, `alive`, `nametag`, `maxhp`, "
					 + "`deathcause`, `color`, `armor`, `horse_jumpstrength`, `horse_style`, `horse_variant`, `uuid`"
					 + "VALUES ("+animal.getOwner()+", "+animal.getAnimaltype().toString()+", "+animal.getLast_x()+", "+animal.getLast_y()+", "
					 		 + ""+animal.getLast_z()+", "+animal.getAlive()+", '"+animal.getNametag()+"', "+animal.getMaxhp()+", "
					 		 + ""+animal.getDeathcause()+", '"+animal.getColor()+"', "+animal.getArmor()+", "+animal.getHorse_jumpstrength()+", "
					 		 + ""+animal.getHorse_style()+", "+animal.getHorse_variant()+", '"+animal.getUniqueId()+"')"
					 + "ON DUPLICATE KEY UPDATE owner="+animal.getOwner()+", last_x="+animal.getLast_x()+", last_y="+animal.getLast_y()+", last_z="+animal.getLast_z()+", "
					 		+ "alive="+animal.getAlive()+", nametag='"+animal.getNametag()+"', deathcause='"+animal.getDeathcause().toString()+"', color='"+animal.getColor()+"', "
					 		+ "armor='"+animal.getArmor().toString()+"';";
		
		if(connection.execute(Query, true)) {
			return true;
		}
		return false;
	}
	
	public Animal getAnimal(String uuid) {
		//TODO: de.AnimalProtect.Database.getAnimal(uuid);
		return null;
	}
	
	public boolean isConnected() {
		if (connection == null) { return false; }
		return connection.checkConnection();
	}
}
