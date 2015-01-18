package de.AnimalProtect.listeners;

/* Bukkit Imports */
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.FishHook;
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

/**
 * Der DamageEventListener fängt das {@link EntityDamageByEntityEvent} ab
 * und prüft ob ein gesichertes Tier Schaden bekommt.
 * 
 * @author Fear837, Pingebam
 * @version 1.0
 * @see Listener
 */
public class DamageEventListener implements Listener {

	/** Die AnimalProtect-Instanz. */
	private final AnimalProtect plugin;
	/** Ein Verweis auf die AnimalProtect-Datenbank. */
	private final Database database;

	/**
	 * Initialisiert den EventListener.
	 * @param plugin - Das AnimalProtect-Plugin.
	 */
	public DamageEventListener(final AnimalProtect plugin) {
		this.plugin = plugin;
		this.database = plugin.getDatenbank();
	}

	@EventHandler
	public void onEntityEvent(final EntityDamageByEntityEvent event) {
		try {
			if (!this.plugin.isEnabled() || event.isCancelled()) { return; }

			/* Datenbank-Verbindung aufbauen, falls nicht vorhanden. */
			if (!this.database.isConnected()) { this.database.connect(); }

			if (!this.plugin.isAnimal(event.getEntity())) { return; }

			/* Erst die Variablen bereit stellen, die später genutzt werden. */
			final Entity entity = event.getEntity();
			Entity damager = event.getDamager();
			final CraftoPlayer owner = this.database.getOwner(entity.getUniqueId());

			/* Wenn es keinen Owner gibt, dann ist das Tier auch nicht protected */
			/* Wenn es also keinen Owner gibt, dann wird die Methode abgebrochen.*/
			if (owner == null) { return; }

			/* Jetzt die einzelnen Damager-Typen durchlaufen, um den wahren Damager herauszufinden. */
			switch (damager.getType()) {
				case PLAYER:
					break;
				case ARROW:
					final Arrow projectile1 = (Arrow)damager;
					damager = (Entity) projectile1.getShooter();
					break;
				case EGG:
					final Egg projectile2 = (Egg)damager;
					damager = (Entity) projectile2.getShooter();
					break;
				case SNOWBALL:
					final Snowball projectile3 = (Snowball)damager;
					damager = (Entity) projectile3.getShooter();
					break;
				case FIREBALL:
					final Fireball projectile4 = (Fireball)damager;
					damager = (Entity) projectile4.getShooter();
					break;
				case SPLASH_POTION:
					final ThrownPotion projectile5 = (ThrownPotion)damager;
					damager = (Entity) projectile5.getShooter();
					break;
				case SMALL_FIREBALL:
					final SmallFireball projectile6 = (SmallFireball)damager;
					damager = (Entity) projectile6.getShooter();
					break;
				case PRIMED_TNT:
					final TNTPrimed tnt = (TNTPrimed)damager;
					damager = tnt.getSource();
					break;
				case FISHING_HOOK:
					final FishHook fishingHook = (FishHook)damager;
					damager = (Entity) fishingHook.getShooter();
					break;
				default:
					damager = null;
					break;
			}

			/* Wenn der echte Damager null ist, wird die Funktion abgebrochen */
			if (damager == null) { return; }

			/* Wenn der echte Damager ein Spieler ist,     */
			/* dann wird überprüft ob der Spieler das darf */
			if (damager instanceof Player) { 
				final Player player = (Player)damager;
				if (!player.hasPermission("animalprotect.bypass") && !player.getUniqueId().equals(owner.getUniqueId())) {
					event.setCancelled(true);
					Messenger.sendMessage(player, "Dieses Tier ist von §6" +owner.getName()+ "§e gesichert!");
				}
			}
		}
		catch (final Exception e) { Messenger.exception("DamageEventListener/onEntityEvent", "Unknown Exception.", e); }
	}
}