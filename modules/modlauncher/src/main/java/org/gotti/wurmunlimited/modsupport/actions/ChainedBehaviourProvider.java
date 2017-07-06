package org.gotti.wurmunlimited.modsupport.actions;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.wurmonline.mesh.Tiles.TileBorderDirection;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.bodys.Wound;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.structures.BridgePart;
import com.wurmonline.server.structures.Fence;
import com.wurmonline.server.structures.Floor;
import com.wurmonline.server.structures.Wall;

public class ChainedBehaviourProvider implements BehaviourProvider {
	private Iterable<BehaviourProvider> behaviourProviders;
	private List<BehaviourProvider> prov;

	public ChainedBehaviourProvider(BehaviourProvider wrapped, List<BehaviourProvider> behaviourProviders) {
		this.prov = behaviourProviders;
		this.behaviourProviders = new ChainedBehaviourProviders(wrapped, behaviourProviders);
	}

	private List<ActionEntry> merge(List<ActionEntry> list, List<ActionEntry> entries) {
		if (list == null) {
			return entries;
		} else if (entries == null) {
			return list;
		} else {
			list.addAll(entries);
			return list;
		}
	}
	
	private List<ActionEntry> call(Function<BehaviourProvider, List<ActionEntry>> code) {
		List<ActionEntry> list = null;
		for (BehaviourProvider behaviourProvider : behaviourProviders) {
			try {
				list = merge(list, code.apply(behaviourProvider));
			} catch (Exception e) {
				Logger.getLogger(ChainedBehaviourProvider.class.getName()).log(Level.SEVERE, e.getMessage(), e);
				prov.remove(behaviourProvider);
			}
		}
		return list;
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature aPerformer, boolean aOnSurface, BridgePart aBridgePart) {
		return call(behaviourProvider -> behaviourProvider.getBehavioursFor(aPerformer, aOnSurface, aBridgePart));
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature aPerformer, Item item, boolean aOnSurface, BridgePart aBridgePart) {
		return call(behaviourProvider -> behaviourProvider.getBehavioursFor(aPerformer, item, aOnSurface, aBridgePart));
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature creature, Item item, boolean onSurface, Floor floor) {
		return call(behaviourProvider -> behaviourProvider.getBehavioursFor(creature, item, onSurface, floor));
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, boolean onSurface, Floor floor) {
		return call(behaviourProvider -> behaviourProvider.getBehavioursFor(performer, onSurface, floor));
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Creature target) {
		return call(behaviourProvider -> behaviourProvider.getBehavioursFor(performer, target));
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Fence target) {
		return call(behaviourProvider -> behaviourProvider.getBehavioursFor(performer, target));
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, int planetId) {
		return call(behaviourProvider -> behaviourProvider.getBehavioursFor(performer, planetId));
	}

	@SuppressWarnings("deprecation")
	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, int tilex, int tiley, boolean onSurface, boolean corner, int tile) {
		return call(behaviourProvider -> behaviourProvider.getBehavioursFor(performer, tilex, tiley, onSurface, corner, tile));
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, int tilex, int tiley, boolean onSurface, boolean corner, int tile, int heightOffset) {
		return call(behaviourProvider -> behaviourProvider.getBehavioursFor(performer, tilex, tiley, onSurface, corner, tile, heightOffset));
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, int tilex, int tiley, boolean onSurface, int tile) {
		return call(behaviourProvider -> behaviourProvider.getBehavioursFor(performer, tilex, tiley, onSurface, tile));
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, int tilex, int tiley, boolean onSurface, int tile, int dir) {
		return call(behaviourProvider -> behaviourProvider.getBehavioursFor(performer, tilex, tiley, onSurface, tile, dir));
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, int tilex, int tiley, boolean onSurface, TileBorderDirection dir, boolean border, int heightOffset) {
		return call(behaviourProvider -> behaviourProvider.getBehavioursFor(performer, tilex, tiley, onSurface, dir, border, heightOffset));
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Item object, int planetId) {
		return call(behaviourProvider -> behaviourProvider.getBehavioursFor(performer, object, planetId));
	}

	@SuppressWarnings("deprecation")
	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Item object, int tilex, int tiley, boolean onSurface, boolean corner, int tile) {
		return call(behaviourProvider -> behaviourProvider.getBehavioursFor(performer, object, tilex, tiley, onSurface, corner, tile));
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Item object, int tilex, int tiley, boolean onSurface, boolean corner, int tile, int heightOffset) {
		return call(behaviourProvider -> behaviourProvider.getBehavioursFor(performer, object, tilex, tiley, onSurface, corner, tile, heightOffset));
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Item object, int tilex, int tiley, boolean onSurface, int tile) {
		return call(behaviourProvider -> behaviourProvider.getBehavioursFor(performer, object, tilex, tiley, onSurface, tile));
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Item object, int tilex, int tiley, boolean onSurface, int tile, int dir) {
		return call(behaviourProvider -> behaviourProvider.getBehavioursFor(performer, object, tilex, tiley, onSurface, tile, dir));
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Item object, int tilex, int tiley, boolean onSurface, TileBorderDirection dir, boolean border, int heightOffset) {
		return call(behaviourProvider -> behaviourProvider.getBehavioursFor(performer, object, tilex, tiley, onSurface, dir, border, heightOffset));
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Item subject, Creature target) {
		return call(behaviourProvider -> behaviourProvider.getBehavioursFor(performer, subject, target));
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Item subject, Fence target) {
		return call(behaviourProvider -> behaviourProvider.getBehavioursFor(performer, subject, target));
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Item subject, Item target) {
		return call(behaviourProvider -> behaviourProvider.getBehavioursFor(performer, subject, target));
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Item subject, Skill skill) {
		return call(behaviourProvider -> behaviourProvider.getBehavioursFor(performer, subject, skill));
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Item subject, Wall target) {
		return call(behaviourProvider -> behaviourProvider.getBehavioursFor(performer, subject, target));
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Item subject, Wound target) {
		return call(behaviourProvider -> behaviourProvider.getBehavioursFor(performer, subject, target));
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Item target) {
		return call(behaviourProvider -> behaviourProvider.getBehavioursFor(performer, target));
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, long target) {
		return call(behaviourProvider -> behaviourProvider.getBehavioursFor(performer, target));
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Skill skill) {
		return call(behaviourProvider -> behaviourProvider.getBehavioursFor(performer, skill));
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Wall target) {
		return call(behaviourProvider -> behaviourProvider.getBehavioursFor(performer, target));
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Wound target) {
		return call(behaviourProvider -> behaviourProvider.getBehavioursFor(performer, target));
	}

	private static class ChainedBehaviourProviders implements Iterable<BehaviourProvider> {

		private final BehaviourProvider wrapped;
		private final Iterable<BehaviourProvider> iterable;

		public ChainedBehaviourProviders(BehaviourProvider wrapped, Collection<BehaviourProvider> behaviourProviders) {
			this.wrapped = wrapped;
			this.iterable = behaviourProviders;
		}

		@Override
		public Iterator<BehaviourProvider> iterator() {

			return new Iterator<BehaviourProvider>() {
				
				BehaviourProvider first = wrapped;
				
				Iterator<BehaviourProvider> iterator = iterable == null ? Collections.emptyIterator() : iterable.iterator();

				@Override
				public boolean hasNext() {
					if (first != null) {
						return true;
					} else {
						return iterator.hasNext();
					}
				}

				@Override
				public BehaviourProvider next() {
					if (first != null) {
						try {
							return first;
						} finally {
							first = null;
						}
					} else {
						return iterator.next();
					}
				}
			};
		}
	}
}
