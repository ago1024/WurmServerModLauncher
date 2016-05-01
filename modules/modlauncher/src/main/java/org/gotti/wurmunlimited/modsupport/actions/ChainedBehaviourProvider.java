package org.gotti.wurmunlimited.modsupport.actions;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

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

	public ChainedBehaviourProvider(BehaviourProvider wrapped, Collection<BehaviourProvider> behaviourProviders) {
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

	@Override
	public List<ActionEntry> getBehavioursFor(Creature aPerformer, boolean aOnSurface, BridgePart aBridgePart) {
		List<ActionEntry> list = null;
		for (BehaviourProvider behaviourProvider : behaviourProviders) {
			list = merge(list, behaviourProvider.getBehavioursFor(aPerformer, aOnSurface, aBridgePart));
		}
		return list;
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature aPerformer, Item item, boolean aOnSurface, BridgePart aBridgePart) {
		List<ActionEntry> list = null;
		for (BehaviourProvider behaviourProvider : behaviourProviders) {
			list = merge(list, behaviourProvider.getBehavioursFor(aPerformer, item, aOnSurface, aBridgePart));
		}
		return list;
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature creature, Item item, boolean onSurface, Floor floor) {
		List<ActionEntry> list = null;
		for (BehaviourProvider behaviourProvider : behaviourProviders) {
			list = merge(list, behaviourProvider.getBehavioursFor(creature, item, onSurface, floor));
		}
		return list;
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, boolean onSurface, Floor floor) {
		List<ActionEntry> list = null;
		for (BehaviourProvider behaviourProvider : behaviourProviders) {
			list = merge(list, behaviourProvider.getBehavioursFor(performer, onSurface, floor));
		}
		return list;
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Creature target) {
		List<ActionEntry> list = null;
		for (BehaviourProvider behaviourProvider : behaviourProviders) {
			list = merge(list, behaviourProvider.getBehavioursFor(performer, target));
		}
		return list;
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Fence target) {
		List<ActionEntry> list = null;
		for (BehaviourProvider behaviourProvider : behaviourProviders) {
			list = merge(list, behaviourProvider.getBehavioursFor(performer, target));
		}
		return list;
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, int planetId) {
		List<ActionEntry> list = null;
		for (BehaviourProvider behaviourProvider : behaviourProviders) {
			list = merge(list, behaviourProvider.getBehavioursFor(performer, planetId));
		}
		return list;
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, int tilex, int tiley, boolean onSurface, boolean corner, int tile) {
		List<ActionEntry> list = null;
		for (BehaviourProvider behaviourProvider : behaviourProviders) {
			list = merge(list, behaviourProvider.getBehavioursFor(performer, tilex, tiley, onSurface, corner, tile));
		}
		return list;
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, int tilex, int tiley, boolean onSurface, int tile) {
		List<ActionEntry> list = null;
		for (BehaviourProvider behaviourProvider : behaviourProviders) {
			list = merge(list, behaviourProvider.getBehavioursFor(performer, tilex, tiley, onSurface, tile));
		}
		return list;
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, int tilex, int tiley, boolean onSurface, int tile, int dir) {
		List<ActionEntry> list = null;
		for (BehaviourProvider behaviourProvider : behaviourProviders) {
			list = merge(list, behaviourProvider.getBehavioursFor(performer, tilex, tiley, onSurface, tile, dir));
		}
		return list;
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, int tilex, int tiley, boolean onSurface, TileBorderDirection dir, boolean border, int heightOffset) {
		List<ActionEntry> list = null;
		for (BehaviourProvider behaviourProvider : behaviourProviders) {
			list = merge(list, behaviourProvider.getBehavioursFor(performer, tilex, tiley, onSurface, dir, border, heightOffset));
		}
		return list;
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Item object, int planetId) {
		List<ActionEntry> list = null;
		for (BehaviourProvider behaviourProvider : behaviourProviders) {
			list = merge(list, behaviourProvider.getBehavioursFor(performer, object, planetId));
		}
		return list;
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Item object, int tilex, int tiley, boolean onSurface, boolean corner, int tile) {
		List<ActionEntry> list = null;
		for (BehaviourProvider behaviourProvider : behaviourProviders) {
			list = merge(list, behaviourProvider.getBehavioursFor(performer, object, tilex, tiley, onSurface, corner, tile));
		}
		return list;
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Item object, int tilex, int tiley, boolean onSurface, int tile) {
		List<ActionEntry> list = null;
		for (BehaviourProvider behaviourProvider : behaviourProviders) {
			list = merge(list, behaviourProvider.getBehavioursFor(performer, object, tilex, tiley, onSurface, tile));
		}
		return list;
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Item object, int tilex, int tiley, boolean onSurface, int tile, int dir) {
		List<ActionEntry> list = null;
		for (BehaviourProvider behaviourProvider : behaviourProviders) {
			list = merge(list, behaviourProvider.getBehavioursFor(performer, object, tilex, tiley, onSurface, tile, dir));
		}
		return list;
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Item object, int tilex, int tiley, boolean onSurface, TileBorderDirection dir, boolean border, int heightOffset) {
		List<ActionEntry> list = null;
		for (BehaviourProvider behaviourProvider : behaviourProviders) {
			list = merge(list, behaviourProvider.getBehavioursFor(performer, object, tilex, tiley, onSurface, dir, border, heightOffset));
		}
		return list;
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Item subject, Creature target) {
		List<ActionEntry> list = null;
		for (BehaviourProvider behaviourProvider : behaviourProviders) {
			list = merge(list, behaviourProvider.getBehavioursFor(performer, subject, target));
		}
		return list;
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Item subject, Fence target) {
		List<ActionEntry> list = null;
		for (BehaviourProvider behaviourProvider : behaviourProviders) {
			list = merge(list, behaviourProvider.getBehavioursFor(performer, subject, target));
		}
		return list;
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Item subject, Item target) {
		List<ActionEntry> list = null;
		for (BehaviourProvider behaviourProvider : behaviourProviders) {
			list = merge(list, behaviourProvider.getBehavioursFor(performer, subject, target));
		}
		return list;
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Item subject, Skill skill) {
		List<ActionEntry> list = null;
		for (BehaviourProvider behaviourProvider : behaviourProviders) {
			list = merge(list, behaviourProvider.getBehavioursFor(performer, subject, skill));
		}
		return list;
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Item subject, Wall target) {
		List<ActionEntry> list = null;
		for (BehaviourProvider behaviourProvider : behaviourProviders) {
			list = merge(list, behaviourProvider.getBehavioursFor(performer, subject, target));
		}
		return list;
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Item subject, Wound target) {
		List<ActionEntry> list = null;
		for (BehaviourProvider behaviourProvider : behaviourProviders) {
			list = merge(list, behaviourProvider.getBehavioursFor(performer, subject, target));
		}
		return list;
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Item target) {
		List<ActionEntry> list = null;
		for (BehaviourProvider behaviourProvider : behaviourProviders) {
			list = merge(list, behaviourProvider.getBehavioursFor(performer, target));
		}
		return list;
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, long target) {
		List<ActionEntry> list = null;
		for (BehaviourProvider behaviourProvider : behaviourProviders) {
			list = merge(list, behaviourProvider.getBehavioursFor(performer, target));
		}
		return list;
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Skill skill) {
		List<ActionEntry> list = null;
		for (BehaviourProvider behaviourProvider : behaviourProviders) {
			list = merge(list, behaviourProvider.getBehavioursFor(performer, skill));
		}
		return list;
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Wall target) {
		List<ActionEntry> list = null;
		for (BehaviourProvider behaviourProvider : behaviourProviders) {
			list = merge(list, behaviourProvider.getBehavioursFor(performer, target));
		}
		return list;
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Wound target) {
		List<ActionEntry> list = null;
		for (BehaviourProvider behaviourProvider : behaviourProviders) {
			list = merge(list, behaviourProvider.getBehavioursFor(performer, target));
		}
		return list;
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
