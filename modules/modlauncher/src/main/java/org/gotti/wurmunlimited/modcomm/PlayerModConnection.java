package org.gotti.wurmunlimited.modcomm;

import java.util.Set;

/**
 * Connection information for a player
 */
public class PlayerModConnection {
    private boolean active;
    private byte version;
    private Set<Channel> channels;

    public PlayerModConnection() {
        active = false;
        version = -1;
    }

    void activate(byte version, Set<Channel> channels) {
        this.active = true;
        this.version = version;
        this.channels = channels;
    }

    /**
     * @return true if player has the client mod launcher loaded and can receive messages
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @return protocol version supported by this player
     */
    public byte getVersion() {
        return version;
    }

    /**
     * @return set of channel ids active for this player
     */
    public Set<Channel> getChannels() {
        return channels;
    }
}
