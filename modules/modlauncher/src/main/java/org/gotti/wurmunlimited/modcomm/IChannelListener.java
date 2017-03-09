package org.gotti.wurmunlimited.modcomm;

import com.wurmonline.server.players.Player;

import java.nio.ByteBuffer;

/**
 * Listener for mod channels, implement in a class and register with {@link ModComm#registerChannel}
 */
public interface IChannelListener {
    /**
     * Handle a message from a player
     *
     * @param player  player object
     * @param message message contents
     */
    default void handleMessage(Player player, ByteBuffer message) {
    }

    /**
     * Called when a player is connected and this channel is activated
     *
     * @param player player object
     */
    default void onPlayerConnected(Player player) {
    }

}
