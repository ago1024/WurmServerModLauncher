package org.gotti.wurmunlimited.mods.harvesthelper;

import java.util.Locale;

import com.wurmonline.server.WurmCalendar;

public enum HarvestHelperSeasons {

	OLIVE {
		public long getStartGrowth() {
			return WurmCalendar.getStartOliveGrowth();
		}
	},
	GRAPE {
		public long getStartGrowth() {
			return WurmCalendar.getStartGrapeGrowth();
		}
	},
	CHERRY {
		public long getStartGrowth() {
			return WurmCalendar.getStartCherryGrowth();
		}
	},
	APPLE {
		public long getStartGrowth() {
			return WurmCalendar.getStartAppleGrowth();
		}
	},
	LEMON {
		public long getStartGrowth() {
			return WurmCalendar.getStartLemonGrowth();
		}
	},
	OLEANDER {
		public long getStartGrowth() {
			return WurmCalendar.getStartOleanderGrowth();
		}
	},
	CAMELLIA {
		public long getStartGrowth() {
			return WurmCalendar.getStartCamelliaGrowth();
		}
	},
	LAVENDER {
		public long getStartGrowth() {
			return WurmCalendar.getStartLavenderGrowth();
		}
	},
	MAPLE {
		public long getStartGrowth() {
			return WurmCalendar.getStartMapleGrowth();
		}
	},
	ROSE {
		public long getStartGrowth() {
			return WurmCalendar.getStartRoseGrowth();
		}
	},
	CHESTNUT {
		public long getStartGrowth() {
			return WurmCalendar.getStartChestnutGrowth();
		}
	},
	WALNUT {
		public long getStartGrowth() {
			return WurmCalendar.getStartWalnutGrowth();
		}
	},
	PINE {
		@Override
		public long getStartGrowth() {
			return WurmCalendar.getStartPineGrowth();
		}
	},
	HAZEL {
		@Override
		public long getStartGrowth() {
			return WurmCalendar.getStartHazelGrowth();
		}
	},
	HOPS {
		@Override
		public long getStartGrowth() {
			return WurmCalendar.getStartHopsGrowth();
		}
	},
	OAK {
		@Override
		public long getStartGrowth() {
			return WurmCalendar.getStartOakGrowth();
		}
	},
	ORANGE {
		@Override
		public long getStartGrowth() {
			return WurmCalendar.getStartOrangeGrowth();
		}
	},
	;

	public abstract long getStartGrowth();

	public String getName() {
		return this.name().toLowerCase(Locale.ROOT);
	}

}
