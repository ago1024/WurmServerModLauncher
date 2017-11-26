package org.gotti.wurmunlimited.modcomm.intra;

import java.nio.ByteBuffer;

/**
 * ModIntraServer request
 */
public interface IntraRequest {

	/**
	 * @return Unique message name
	 */
	String getName();

	/**
	 * Handle the reply
	 * @param recvBuffer Receive buffer
	 * @return true if the request is done
	 */
	boolean handleReply(ByteBuffer recvBuffer);

	/**
	 * Handle a timeout
	 * @return true to finish the request
	 */
	default boolean handleTimeout() {
		return true;
	}
}
