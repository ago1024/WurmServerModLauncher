package org.gotti.wurmunlimited.modcomm.intra;

import java.nio.ByteBuffer;

import com.wurmonline.communication.SocketConnection;

/**
 * ModIntraServer request handler (receiving end)
 */
@FunctionalInterface
public interface IntraRequestHandler {

	/**
	 * Handle the request
	 * @param connection Connection
	 * @param recvBuffer Receive buffer
	 */
	void handleRequest(SocketConnection connection, ByteBuffer recvBuffer);
}
