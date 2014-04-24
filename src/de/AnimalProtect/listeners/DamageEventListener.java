package de.AnimalProtect.listeners;

/* Bukkit Imports */
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Fish;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/* CraftoPlugin Imports */
import craftoplugin.core.database.CraftoPlayer;

/* AnimalProtect Imports */
import de.AnimalProtect.AnimalProtect;
import de.AnimalProtect.Database;
import de.AnimalProtect.Messenger;

public class DamageEventListener implements Listener {
	
	private AnimalProtect plugin;
	private Database database;
	
	public DamageEventListener(AnimalProtect plugin) {
		this.plugin = plugin;
		this.database = plugin.getDatenbank();
	}
	
	@EventHandler
	public void onEntityEvent(EntityDamageByEntityEvent event) {
		if (!plugin.isEnabled() || event.isCancelled()) { return; }
		
		/* Datenbank-Verbindung aufbauen, falls nicht vorhanden. */
		if (!database.isConnected()) { database.connect(); }
		
		if (!plugin.isAnimal(event.getEntity())) { return; }
		
		/* Erst die Variablen bereit stellen, die später genutzt werden. */
		Entity entity = event.getEntity();
		Entity damager = event.getDamager();
		CraftoPlayer owner = database.getOwner(entity.getUniqueId());
		
		/* Wenn es keinen Owner gibt, dann ist das Tier auch nicht protected */
		/* Wenn es also keinen Owner gibt, dann wird die Methode abgebrochen.*/
		if (owner == null) { return; }
		
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
		case FIREBALL:
			Fireball projectile4 = (Fireball)damager;
			damager = (Entity) projectile4.getShooter();
			break;
		case SPLASH_POTION:
			ThrownPotion projectile5 = (ThrownPotion)damager;
			damager = (Entity) projectile5.getShooter();
			break;
		case SMALL_FIREBALL:
			SmallFireball projectile6 = (SmallFireball)damager;
			damager = (Entity) projectile6.getShooter();
			break;
		case PRIMED_TNT:
			TNTPrimed tnt = (TNTPrimed)damager;
			damager = (Entity) tnt.getSource();
			Messenger.broadcast("DAMAGER=PRIMED_TNT! source="+tnt.getSource().getType().toString());
			break;
		case FISHING_HOOK:
			Fish fishingHook = (Fish)damager;
			damager = (Entity) fishingHook.getShooter();
			break;
		default:
			Messenger.warn("Unknown Damager detected! DamagerType: " +damager.getType().toString());
			Messenger.messageStaff("§c[!] §7AnimalProtect: Ein unbekannter Damager wurde entdeckt: " + damager.getType().toString());
			damager = null;
			break;
		}
		
		/* Wenn der echte Damager null ist, wird die Funktion abgebrochen */
		if (damager == null) { return; }
		
		/* Wenn der echte Damager ein Spieler ist,     */
		/* dann wird überprüft ob der Spieler das darf */
		if (damager instanceof Player) { 
			Player player = (Player)damager;
			if (!player.hasPermission("animalprotect.bypass") && !player.getUniqueId().equals(owner.getUniqueId())) {
				event.setCancelled(true);
				Messenger.sendMessage(player, "Dieses Tier ist von §6" +owner.getName()+ " §egesichert!");
			}
		}
	}
}
