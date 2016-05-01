package org.gotti.wurmunlimited.modsupport;

public enum IdType {
	ITEMTEMPLATE {
		@Override
		public int maxvalue() {
			return 22767;
		}
	},
	CREATURETEMPLATE {
		@Override
		public int maxvalue() {
			return Integer.MAX_VALUE;
		}
	},
	SKILL {
		@Override
		public int maxvalue() {
			return Integer.MAX_VALUE;
		}
	};

	public abstract int maxvalue();
}
