package org.gotti.wurmunlimited.modcomm;

import com.wurmonline.communication.SocketConnection;
import com.wurmonline.server.players.Player;

import java.nio.ByteBuffer;

/**
 * Channel object, created by calling {@link ModComm#registerChannel}
 */
public class Channel {
    final int id;
    final IChannelListener listener;
    final String name;

    Channel(int id, String name, IChannelListener listener) {
        this.id = id;
        this.name = name;
        this.listener = listener;
    }

    /**
     * Send message to a player on this channel. Channel must be active for that player.
     *
     * @param player  player object
     * @param message contents of the message
     */
    public void sendMessage(Player player, ByteBuffer message) {
        if (!isActiveForPlayer(player))
            throw new RuntimeException(String.format("Channel %s is not active for player %s", name, player.getName()));
        try {
            SocketConnection conn = player.getCommunicator().getConnection();
            ByteBuffer buff = conn.getBuffer();
            buff.put(ModCommConstants.CMD_MODCOMM);
            buff.put(ModCommConstants.PACKET_MESSAGE);
            buff.putInt(id);
            buff.put(message);
            buff.put(message);
            conn.flush();
        } catch (Exception e) {
            ModComm.logException(String.format("Error sending packet on channel %s to player %s", name, player.getName()), e);
        }
    }

    /**
     * Check if a channel is active for a specific player.
     *
     * @param player player object
     * @return true if the channel is active
     */
    public boolean isActiveForPlayer(Player player) {
        PlayerModConnection conn = ModComm.getPlayerConnection(player);
        return conn.isActive() && conn.getChannels().contains(this);
    }
}
