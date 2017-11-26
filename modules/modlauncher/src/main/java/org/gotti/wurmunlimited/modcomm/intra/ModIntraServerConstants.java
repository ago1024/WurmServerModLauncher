package org.gotti.wurmunlimited.modcomm.intra;

public class ModIntraServerConstants {
	/**
	 * Command ID for all packets used by this system, should not collide with any packets used by WU (see {@link IntraServerProtocol})})
	 */
	public static final byte CMD_MODCOMM = -100;

	/**
	 * Packet types for the internal protocol
	 */
	public static final byte PACKET_MESSAGE = 1;
}
