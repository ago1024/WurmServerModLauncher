package com.wurmonline.server.intra;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gotti.wurmunlimited.modcomm.intra.BBHelper;
import org.gotti.wurmunlimited.modcomm.intra.IntraRequest;
import org.gotti.wurmunlimited.modcomm.intra.ModIntraServer;
import org.gotti.wurmunlimited.modcomm.intra.ModIntraServerConstants;

import com.wurmonline.communication.SocketConnection;
import com.wurmonline.server.ServerEntry;
import com.wurmonline.server.Servers;

/**
 * ModIntraServer message type
 * @author ago
 */
public final class ModIntraServerMessage extends IntraCommand implements IntraServerProtocol {
	private static final Logger logger = Logger.getLogger(ModIntraServerMessage.class.getName());

	private final ServerEntry server;
	private IntraClient client;
	private boolean done;
	private boolean sent;
	private String message;
	private byte[] data;
	private IntraRequest handler;

	/**
	 * Create intra server message
	 * @param serverEntry Target server
	 * @param handler Message handler
	 * @param payload Request payload
	 */
	public ModIntraServerMessage(final ServerEntry serverEntry, IntraRequest handler, byte[] payload) {
		this.done = false;
		this.sent = false;
		this.server = serverEntry;
		this.message = handler.getName();
		this.data = payload;
		this.handler = handler;
	}

	/**
	 * Called from Server.poll
	 */
	public boolean poll() {
		if (this.server.id == Servers.localServer.id) {
			return true;
		}
		// Client is null, so connect to the target
		if (this.client == null) {
			try {
				this.client = new IntraClient(server.INTRASERVERADDRESS, Integer.parseInt(server.INTRASERVERPORT), this);
				this.client.login(this.server.INTRASERVERPASSWORD, true);
				ModIntraServerMessage.logger.log(Level.INFO, "connecting to " + this.server.id);
			} catch (IOException iox) {
				this.client.disconnect("Failed.");
				this.client = null;
				ModIntraServerMessage.logger.log(Level.INFO, "Failed");
				this.done = true;
			}
		}
		// Client is not null and we're not done
		if (this.client != null && !this.done) {
			// check for timeouts
			if (System.currentTimeMillis() > this.timeOutAt) {
				this.done = handler.handleTimeout();
			}
			// Main processing loop
			if (!this.done) {
				try {
					// we're logged in but have not sent the message. Send it
					if (this.client.loggedIn && !this.sent) {

						SocketConnection connection = ModIntraServer.getConnection(this.client);

						final ByteBuffer buf = connection.getBuffer();
						buf.put(ModIntraServerConstants.CMD_MODCOMM);
						buf.put(ModIntraServerConstants.PACKET_MESSAGE);
						BBHelper.putUtf8String(buf, message);
						buf.put(data);
						connection.flush();

						this.sent = true;
						this.timeOutAt = System.currentTimeMillis() + this.timeOutTime;
					}
					// poll the connection
					if (!this.done) {
						this.client.update();
					}
				} catch (IOException iox) {
					this.done = true;
				}
			}
			// we're done. Disconnect
			if (this.done && this.client != null) {
				this.client.disconnect("Done");
				this.client = null;
			}
		}
		return this.done;
	}

	public void commandExecuted(final IntraClient aClient) {
		this.done = true;
	}

	public void commandFailed(final IntraClient aClient) {
		this.done = true;
	}

	public void dataReceived(final IntraClient aClient) {
		this.done = true;
	}

	public void reschedule(final IntraClient aClient) {
		this.done = true;
	}

	public void remove(final IntraClient aClient) {
		this.done = true;
	}

	public void receivingData(final ByteBuffer buffer) {
		this.done = this.done || handler.handleReply(buffer);
	}

}