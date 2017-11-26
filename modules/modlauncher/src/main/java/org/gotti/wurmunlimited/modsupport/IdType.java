package org.gotti.wurmunlimited.modsupport;

/**
 * Default id types.
 */
public enum IdType implements IIdType {
	ITEMTEMPLATE {
		@Override
		public int startValue() {
			return 22767;
		}
	},
	CREATURETEMPLATE {
		@Override
		public int startValue() {
			return Integer.MAX_VALUE;
		}
	},
	SKILL {
		@Override
		public int startValue() {
			return Integer.MAX_VALUE;
		}
	},
	PLAYERPROPERTY {
		@Override
		public int startValue() {
			return 0;
		}

		@Override
		public boolean isCountingDown() {
			return false;
		}
	};

	private int lastUsedId = startValue();

	@Override
	public abstract int startValue();

	@Override
	public boolean isCountingDown() {
		return true;
	}

	@Override
	public String typeName() {
		return name();
	}

	@Override
	public void updateLastUsedId(int id) {
		if (isCountingDown()) {
			lastUsedId = Math.min(lastUsedId, id);
		} else {
			lastUsedId = Math.max(lastUsedId, id);
		}
	}

	@Override
	public int getLastUsedId() {
		return lastUsedId;
	}
}
