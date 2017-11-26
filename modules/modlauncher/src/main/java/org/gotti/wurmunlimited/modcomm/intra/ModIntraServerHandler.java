package org.gotti.wurmunlimited.modcomm.intra;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.gotti.wurmunlimited.modloader.callbacks.CallbackApi;

import com.wurmonline.communication.SocketConnection;
import com.wurmonline.server.intra.IntraServerConnection;

/**
 * ModIntraServer api handler
 */
public class ModIntraServerHandler {

	private static final Logger LOGGER = Logger.getLogger(ModIntraServerHandler.class.getName());

	/**
	 * Registered request handlers.
	 */
	private static Map<String, IntraRequestHandler> requestHandlers = new HashMap<>();

	/**
	 * Handle a ModIntraServer request.
	 * @param intraServerConnection Connection
	 * @param byteBuffer Buffer
	 */
	@CallbackApi
	public void handle(IntraServerConnection intraServerConnection, ByteBuffer byteBuffer) {
		byte packet = byteBuffer.get();
		if (packet == ModIntraServerConstants.PACKET_MESSAGE) {
			handle_PACKET_MESSAGE(intraServerConnection, byteBuffer);
		} else {
			LOGGER.warning("Unknown packet type " + packet);
		}
	}

	/**
	 * Handler a PACKTE_MESSAGE
	 * @param intraServerConnection Connection
	 * @param byteBuffer Buffer
	 */
	private void handle_PACKET_MESSAGE(IntraServerConnection intraServerConnection, ByteBuffer byteBuffer) {
		String message = BBHelper.getUtf8String(byteBuffer);
		IntraRequestHandler handler = requestHandlers.get(message);
		if (handler != null) {
			SocketConnection connection = ModIntraServer.getConnection(intraServerConnection);
			handler.handleRequest(connection, byteBuffer);
		} else {
			LOGGER.warning("Unknown request type " + message);
		}
	}

	/**
	 * Register a request handler
	 * @param name Message name
	 * @param handler Handler
	 */
	public static void registerRequestHandler(String name, IntraRequestHandler handler) {
		if (requestHandlers.containsKey(name)) {
			throw new RuntimeException("handler " + name + " is already registered");
		}
		requestHandlers.put(name, handler);
	}
}
