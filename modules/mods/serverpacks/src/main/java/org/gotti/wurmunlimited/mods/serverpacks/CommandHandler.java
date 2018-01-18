package org.gotti.wurmunlimited.mods.serverpacks;

import com.wurmonline.server.Server;
import com.wurmonline.server.players.Player;

import java.util.logging.Level;
import java.util.logging.Logger;

public class CommandHandler {
    private static Logger logger = Logger.getLogger(ServerPackMod.class.getName());

    static void sendModelRefresh(Player player) {
        try {
            player.createVisionArea();
            Server.getInstance().addCreatureToPort(player);
        } catch (Exception e) {
            logger.log(Level.WARNING, e.getMessage(), e);
        }
    }

}
