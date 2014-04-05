package de.AnimalProtectOld.listener;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import de.AnimalProtectOld.Main;
import de.AnimalProtectOld.MySQL;
import de.AnimalProtectOld.structs.EntityList;
import de.AnimalProtectOld.utility.APLogger;

public class DamageEventListener implements Listener {
	
	private Main plugin;
	private MySQL database;
	private EntityList list;
	
	public DamageEventListener(Main plugin) {
		this.plugin = plugin;
		this.database = plugin.database;
		this.list = plugin.list;
	}
	
	@EventHandler
	public void onEntityEvent(EntityDamageByEntityEvent event) {
		/* Datenbank-Verbindung aufbauen, falls nicht vorhanden. */
		if (!database.checkConnection()) { database.openConnection(); }
		
		if (plugin.isEnabled() && database.checkConnection() && !event.isCancelled()) {
			if (!isAnimal(event.getEntity())) { return; }
			
			/* Erst die Variablen bereit stellen, die sp�ter genutzt werden. */
			Entity entity = event.getEntity();
			Entity damager = event.getDamager();
			String owner = list.getPlayer(entity.getUniqueId());
			
			/* Wenn es keinen Owner gibt, dann ist das Tier auch nicht protected */
			/* Wenn es also keinen Owner gibt, dann wird die Methode abgebrochen.*/
			if (owner == null || owner.isEmpty()) { return; }
			
			/* Jetzt die einzelnen Damager-Typen durchlaufen, um den wahren Damager herauszufinden. */
			switch (damager.getType()) {
			case PLAYER:
				break;
			case ARROW:
				Arrow projectile1 = (Arrow)damager;
				damager = (Entity) projectile1.getShooter();
				break;
			case EGG:
				Egg projectile2 = (Egg)damager;
				damager = (Entity) projectile2.getShooter();
				break;
			case SNOWBALL:
				Snowball projectile3 = (Snowball)damager;
				damager = (Entity) projectile3.getShooter();
				break;
			case SPLASH_POTION:
				ThrownPotion projectile4 = (ThrownPotion)damager;
				damager = (Entity) projectile4.getShooter();
				break;
			default:
				if (Main.DEBUGMODE) { APLogger.info("[DEBUG] Unknown Damager detected! DamagerType: " +damager.getType().toString()); }
				damager = null;
				break;
			}
			
			/* Wenn der echte Damager null ist, wird die Funktion abgebrochen */
			if (damager == null) { return; }
			
			/* Wenn der echte Damager ein Spieler ist,     */
			/* dann wird �berpr�ft ob der Spieler das darf */
			if (damager instanceof Player) { 
				Player player = (Player)damager;
				if (!player.hasPermission("animalprotect.bypass") && !player.getName().equalsIgnoreCase(owner)) {
					event.setCancelled(true);
					player.sendMessage("�eDieses Tier ist von �6" +owner+ " �egesichert!");
				}
			}
		}
	}
	
	private boolean isAnimal(Entity entity) {
		EntityType type = entity.getType();
		if (type == EntityType.SHEEP
		||  type == EntityType.PIG
		||  type == EntityType.COW
		||  type == EntityType.CHICKEN
		||  type == EntityType.HORSE
		||  type == EntityType.WOLF
		||  type == EntityType.IRON_GOLEM
		||  type == EntityType.SNOWMAN
		||  type == EntityType.VILLAGER
		||  type == EntityType.OCELOT)
		{ return true; }
		return false;
	}
}
