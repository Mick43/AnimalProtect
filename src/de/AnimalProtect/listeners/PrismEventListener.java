package de.AnimalProtect.listeners;

import java.util.HashMap;
import java.util.Map.Entry;

import me.botsko.prism.Prism;
import me.botsko.prism.actionlibs.ActionType;
import me.botsko.prism.actionlibs.MatchRule;
import me.botsko.prism.actionlibs.QueryParameters;
import me.botsko.prism.actions.GenericAction;
import me.botsko.prism.appliers.ChangeResult;
import me.botsko.prism.appliers.ChangeResultType;
import me.botsko.prism.events.PrismCustomPlayerActionEvent;
import me.botsko.prism.exceptions.InvalidActionException;

import org.bukkit.entity.Player;

import de.AnimalProtect.AnimalProtect;
import de.AnimalProtect.Messenger;
import de.AnimalProtect.structs.Animal;

public class PrismEventListener extends GenericAction {
	
	public PrismEventListener(final AnimalProtect plugin) {
		try { Prism.getHandlerRegistry().registerCustomHandler( plugin, PrismEventListener.class ); } 
		catch (final InvalidActionException e) 
		{ Messenger.exception("AnimalProtect.java/initializeCommands/registerCustomHandler", "Failed to register custom prism handler", e); }
	}
	
	public static void logEvent(final Animal animal, final Player sender, final AnimalProtect plugin) {
		final ActionType type = new ActionType("lock", false, true, false, "PrismEventListener", "locked");
		try { Prism.getActionRegistry().registerCustomAction(plugin, type); } 
		catch (final InvalidActionException e) { Messenger.exception("PrismEventListener.java/logEvent", "Failed to register custom actions for prism", e); }
		
		final PrismCustomPlayerActionEvent prismEvent = new PrismCustomPlayerActionEvent(plugin, "lock", sender, "Animal ("+animal.getId()+")");
		plugin.getServer().getPluginManager().callEvent(prismEvent);
	}
	
	@Override
	public ChangeResult applyRollback(final Player player, final QueryParameters parameters, final boolean is_preview ){
		if (!is_preview) {
			final HashMap<String,MatchRule> entities = parameters.getEntities();
			for (final Entry<String, MatchRule> entry : entities.entrySet()) {
				if (entry.getValue().equals(MatchRule.INCLUDE) && this.isNumber(entry.getKey())) {
					final Animal animal = AnimalProtect.plugin.getDatenbank().getAnimal(Integer.parseInt(entry.getKey()));
					AnimalProtect.plugin.getDatenbank().unlockAnimal(animal);
				}
			}
			
			return new ChangeResult( ChangeResultType.APPLIED );
		}
		return new ChangeResult( ChangeResultType.SKIPPED );
	}
	
	private boolean isNumber(final String value) {
		try { Integer.parseInt(value); return true; }
		catch (final Exception e) { return false; }
	}
}
