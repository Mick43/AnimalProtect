package de.AnimalProtect.listeners;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.entity.Player;

import de.AnimalProtect.AnimalProtect;
import de.AnimalProtect.Messenger;
import de.AnimalProtect.structs.Animal;
import me.botsko.prism.Prism;
import me.botsko.prism.actionlibs.ActionType;
import me.botsko.prism.actionlibs.MatchRule;
import me.botsko.prism.actionlibs.QueryParameters;
import me.botsko.prism.actions.GenericAction;
import me.botsko.prism.appliers.ChangeResult;
import me.botsko.prism.appliers.ChangeResultType;
import me.botsko.prism.events.PrismCustomPlayerActionEvent;
import me.botsko.prism.exceptions.InvalidActionException;

public class PrismEventListener extends GenericAction {
	
	public PrismEventListener(AnimalProtect plugin) {
		try { Prism.getHandlerRegistry().registerCustomHandler( plugin, PrismEventListener.class ); } 
		catch (InvalidActionException e) 
		{ Messenger.exception("AnimalProtect.java/initializeCommands/registerCustomHandler", "Failed to register custom prism handler", e); }
	}
	
	public static void logEvent(Animal animal, Player sender, AnimalProtect plugin) {
		ActionType type = new ActionType("lock", false, true, false, "PrismEventListener", "locked");
		try { Prism.getActionRegistry().registerCustomAction(plugin, type); } 
		catch (InvalidActionException e) { Messenger.exception("PrismEventListener.java/logEvent", "Failed to register custom actions for prism", e); }
		
		PrismCustomPlayerActionEvent prismEvent = new PrismCustomPlayerActionEvent(plugin, "lock", sender, "Animal ("+animal.getId()+")");
		plugin.getServer().getPluginManager().callEvent(prismEvent);
	}
	
	@Override
	public ChangeResult applyRollback(Player player, QueryParameters parameters, boolean is_preview ){
		if (!is_preview) {
			HashMap<String,MatchRule> entities = parameters.getEntities();
			for (Entry<String, MatchRule> entry : entities.entrySet()) {
				if (entry.getValue().equals(MatchRule.INCLUDE) && isNumber(entry.getKey())) {
					Animal animal = AnimalProtect.plugin.getDatenbank().getAnimal(Integer.parseInt(entry.getKey()));
					AnimalProtect.plugin.getDatenbank().unlockAnimal(animal);
				}
			}
			
			return new ChangeResult( ChangeResultType.APPLIED );
		}
		return new ChangeResult( ChangeResultType.SKIPPED );
	}
	
	private boolean isNumber(String value) {
		try { Integer.parseInt(value); return true; }
		catch (Exception e) { return false; }
	}
}
