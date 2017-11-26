package org.gotti.wurmunlimited.modcomm.intra.playertransfer;

import static org.gotti.wurmunlimited.modcomm.intra.BBHelper.getUtf8String;
import static org.gotti.wurmunlimited.modcomm.intra.BBHelper.putUtf8String;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.IntPredicate;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gotti.wurmunlimited.modcomm.intra.IntraRequest;
import org.gotti.wurmunlimited.modcomm.intra.ModIntraServerHandler;
import org.gotti.wurmunlimited.modcomm.intra.playertransfer.TemplateIdMapper.Builder;
import org.gotti.wurmunlimited.modsupport.IdFactory;
import org.gotti.wurmunlimited.modsupport.IdType;

import com.wurmonline.communication.SocketConnection;
import com.wurmonline.server.Server;
import com.wurmonline.server.ServerEntry;
import com.wurmonline.server.Servers;
import com.wurmonline.server.creatures.CreatureTemplateFactory;
import com.wurmonline.server.creatures.NoSuchCreatureTemplateException;
import com.wurmonline.server.intra.ModIntraServerMessage;
import com.wurmonline.server.items.ItemTemplateFactory;

/**
 * Request item and creature templates of the remote server
 */
public class GetRemoteTemplatesMessage implements IntraRequest {

	private static final Logger LOGGER = Logger.getLogger(GetRemoteTemplatesMessage.class.getName());

	/**
	 * Message name
	 */
	private static final String NAME = "modloader.transfer.templates";

	/**
	 * The target server id
	 */
	private int targetServer;

	/**
	 * Create a new message
	 * @param targetServer Target server id
	 */
	private GetRemoteTemplatesMessage(int targetServer) {
		this.targetServer = targetServer;
	}

	/**
	 * Send the request
	 * @param server Server instance
	 * @param targetServer Target server id
	 */
	public static void send(Server server, int targetServer) {
		ServerEntry serverEntry = Servers.getServerWithId(targetServer);

		ModIntraServerMessage message = new ModIntraServerMessage(serverEntry, new GetRemoteTemplatesMessage(targetServer), new byte[0]);
		Server.getInstance().addIntraCommand(message);
	}

	/**
	 * Send template ids
	 * @param buffer Buffer
	 * @param idType Template type
	 * @param predicate Test if template should be sent
	 */
	private static void sendIds(ByteBuffer buffer, IdType idType, IntPredicate predicate) {
		List<Entry<String, Integer>> templateIds = IdFactory.getIdsFor(idType);
		// remove all inactive ids
		templateIds.removeIf(e -> !predicate.test(e.getValue()));
		buffer.putInt(templateIds.size());
		for (Entry<String, Integer> entry : templateIds) {
			buffer.putInt(entry.getValue());
			putUtf8String(buffer, entry.getKey());
			LOGGER.info(entry.toString());
		}
	}

	/**
	 * Read template ids
	 * @param recvBuffer Buffer
	 * @param type Template type
	 * @return TemplateIdMapper
	 */
	private TemplateIdMapper readTemplateIds(ByteBuffer recvBuffer, IdType type) {
		Builder builder = new TemplateIdMapper.Builder(type);

		int count = recvBuffer.getInt();
		while (count-- > 0) {
			int id = recvBuffer.getInt();
			String name = getUtf8String(recvBuffer);
			builder.addRemoteTemplate(name, id);
			LOGGER.info(String.format("%d> %s=%d", targetServer, name, id));
		}

		return builder.build();
	}

	/**
	 * Test if an item template is currently registered
	 * @param templateId Template id
	 * @return true if the template is registered
	 */
	private static boolean itemTemplateExists(int templateId) {
		return ItemTemplateFactory.getInstance().getTemplateOrNull(templateId) != null;
	}

	/**
	 * Test if a creature template is currently registered
	 * @param templateId Template id
	 * @return true if the template is registered
	 */
	private static boolean creatureTemplateExists(int templateId) {
		try {
			return CreatureTemplateFactory.getInstance().getTemplate(templateId) != null;
		} catch (NoSuchCreatureTemplateException e) {
			return false;
		}
	}

	/**
	 * Handle the request (remote server)
	 * @param connection Connection
	 * @param recvBuffer Buffer
	 */
	public static void handleRequest(SocketConnection connection, ByteBuffer recvBuffer) {
		try {
			ByteBuffer buffer = connection.getBuffer();
			buffer.put((byte) 9); // We're sending data. This triggers dataReceived on the client

			// send template
			sendIds(buffer, IdType.ITEMTEMPLATE, GetRemoteTemplatesMessage::itemTemplateExists);
			sendIds(buffer, IdType.CREATURETEMPLATE, GetRemoteTemplatesMessage::creatureTemplateExists);

			// send
			connection.flush();
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Failed to send GetItemTemplatesMessage reply", e);
		}
	}

	/**
	 * Register the message handler
	 */
	public static void register() {
		ModIntraServerHandler.registerRequestHandler(NAME, GetRemoteTemplatesMessage::handleRequest);
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public boolean handleReply(ByteBuffer recvBuffer) {

		ModPlayerTransfer.setItemTemplateMapper(targetServer, readTemplateIds(recvBuffer, IdType.ITEMTEMPLATE));
		ModPlayerTransfer.setCreatureTemplateMapper(targetServer, readTemplateIds(recvBuffer, IdType.CREATURETEMPLATE));

		return true;
	}
}
