package org.gotti.wurmunlimited.modcomm;

import com.wurmonline.communication.SocketConnection;
import com.wurmonline.server.players.Player;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashSet;

public class ModCommHandler {
    public static void handlePacket(Player player, ByteBuffer msg) {
        try {
            byte type = msg.get();
            switch (type) {
                case ModCommConstants.PACKET_MESSAGE:
                    handlePacketMessage(player, msg);
                    break;
                case ModCommConstants.PACKET_CHANNELS:
                    handlePacketChannels(player, msg);
                    break;
                default:
                    ModComm.logWarning(String.format("Unknown packet from player %s (%d)", player, type));
            }
        } catch (Exception e) {
            ModComm.logException(String.format("Error handling packet from player %s", player.getName()), e);
        }
    }

    private static void handlePacketChannels(Player player, ByteBuffer msg) throws IOException {
        PacketReader reader = new PacketReader(msg);
        HashSet<Channel> toActivate = new HashSet<>();

        byte version = reader.readByte();
        int n = reader.readInt();

        ModComm.logInfo(String.format("Received client handshake from %s, %d channels, protocol version %d", player.getName(), n, version));

        while (n-- > 0) {
            String channel = reader.readUTF();
            if (ModComm.channels.containsKey(channel))
                toActivate.add(ModComm.channels.get(channel));
        }

        ModComm.logInfo(String.format("Activating %d channels for player %s", toActivate.size(), player.getName()));

        ModComm.getPlayerConnection(player).activate(version, toActivate);

        try (PacketWriter writer = new PacketWriter()) {
            writer.writeByte(ModCommConstants.CMD_MODCOMM);
            writer.writeByte(ModCommConstants.PACKET_CHANNELS);
            writer.writeByte(ModCommConstants.PROTO_VERSION);
            writer.writeInt(toActivate.size());
            for (Channel channel : toActivate) {
                writer.writeInt(channel.id);
                writer.writeUTF(channel.name);
            }
            SocketConnection conn = player.getCommunicator().getConnection();
            ByteBuffer buff = conn.getBuffer();
            buff.put(writer.getBytes());
            conn.flush();
        }

        for (Channel channel : toActivate) {
            try {
                channel.listener.onPlayerConnected(player);
            } catch (Exception e) {
                ModComm.logException(String.format("Error in channel %s onPlayerConnected", channel.name), e);
            }
        }
    }

    private static void handlePacketMessage(Player player, ByteBuffer msg) {
        int id = msg.getInt();
        if (!ModComm.idMap.containsKey(id)) {
            ModComm.logWarning(String.format("Message on unregistered channel %d from player %s", id, player.getName()));
            return;
        }
        Channel ch = ModComm.idMap.get(id);
        if (!ch.isActiveForPlayer(player)) {
            ModComm.logWarning(String.format("Message on inactive channel %s from player %s", ch.name, player.getName()));
            return;
        }
        try {
            ch.listener.handleMessage(player, msg.slice());
        } catch (Exception e) {
            ModComm.logException(String.format("Error in channel handler %s for player %s", ch.name, player.getName()), e);
        }
    }
}
