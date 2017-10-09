package org.gotti.wurmunlimited.mods.hitchingpost;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gotti.wurmunlimited.modsupport.actions.ActionPerformer;
import org.gotti.wurmunlimited.modsupport.actions.ActionPropagation;
import org.gotti.wurmunlimited.modsupport.actions.BehaviourProvider;
import org.gotti.wurmunlimited.modsupport.vehicles.VehicleFacadeImpl;

import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.behaviours.Actions;
import com.wurmonline.server.behaviours.Vehicle;
import com.wurmonline.server.behaviours.VehicleBehaviour;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.creatures.Creatures;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.zones.Zone;
import com.wurmonline.server.zones.Zones;

public class UnhitchAction implements BehaviourProvider, ActionPerformer {
	
	private static Logger logger = Logger.getLogger(HitchingPostMod.class.getName());
	
	private HitchingPostMod mod;
	
	protected UnhitchAction(HitchingPostMod mod) {
		this.mod = mod;
	}
	
	@Override
	public short getActionId() {
		return Actions.UNHITCH;
	}
	
	private boolean canUnhitch(Creature performer, Creature target) {
		try {
			if (!target.isHitched()) {
				return false;
			}
			final Vehicle hitched = target.getHitched();
			if (hitched == null) {
				return false;
			}
			
			final Item item = new VehicleFacadeImpl(hitched).getItem();
			if (mod.isHitchingPost(item) && (VehicleBehaviour.hasKeyForVehicle(performer, item) || VehicleBehaviour.mayDriveVehicle(performer, item, null))) {
				return true;
			}
			return false;
		} catch (NoSuchItemException e) {
			logger.log(Level.WARNING, e.getMessage(), e);
			return false;
		}
	}
	
	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Item subject, Creature target) {
		return getBehavioursFor(performer, target);
	}
	
	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Creature target) {
		if (canUnhitch(performer, target)) {
			return Arrays.asList(Actions.actionEntrys[Actions.UNHITCH]);
		}
		return null;
	}
	
	@Override
	public boolean action(Action action, Creature performer, Creature target, short num, float counter) {
		if (canUnhitch(performer, target)) {
			final Vehicle hitched = target.getHitched();
			try {
				final Zone z = Zones.getZone(target.getTilePos(), target.isOnSurface());
				target.getStatus().savePosition(target.getWurmId(), true, z.getId(), true);
			} catch (Exception ex) {
				logger.log(Level.WARNING, ex.getMessage(), ex);
			}
			hitched.removeDragger(target);
			Creatures.getInstance().setLastLed(target.getWurmId(), performer.getWurmId());
			final VolaTile t = target.getCurrentTile();
			if (t == null) {
				logger.log(Level.WARNING, target.getName() + " has no tile?");
				return propagate(action, ActionPropagation.NO_SERVER_PROPAGATION, ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION, ActionPropagation.FINISH_ACTION);
			}
			t.sendAttachCreature(target.getWurmId(), -10L, 0.0f, 0.0f, 0.0f, 0);
			return propagate(action, ActionPropagation.NO_SERVER_PROPAGATION, ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION, ActionPropagation.FINISH_ACTION);
		}
		return propagate(action);
	}
	
	@Override
	public boolean action(Action action, Creature performer, Item source, Creature target, short num, float counter) {
		return this.action(action, performer, target, num, counter);
	}
}
