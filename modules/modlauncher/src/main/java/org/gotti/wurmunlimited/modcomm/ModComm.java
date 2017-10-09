package org.gotti.wurmunlimited.modcomm;

import com.wurmonline.server.players.Player;
import javassist.*;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ModComm {
    static final HashMap<String, Channel> channels = new HashMap<>();
    static final HashMap<Integer, Channel> idMap = new HashMap<>();

    private static int nextChannelId = 1;

    private static Field fPlayerConnection;

    private static final Logger logger = Logger.getLogger("ModComm");

    /**
     * Register mod channel
     *
     * @param name     Unique identifier of the channel
     * @param listener Listener that will handle communication
     * @return new channel object
     */
    public static Channel registerChannel(String name, IChannelListener listener) {
        if (channels.containsKey(name))
            throw new RuntimeException(String.format("Channel %s already registered", name));
        Channel ch = new Channel(nextChannelId++, name, listener);
        idMap.put(ch.id, ch);
        channels.put(name, ch);
        return ch;
    }

    // === internal stuff ===

    /**
     * Get player connection state
     */
    static PlayerModConnection getPlayerConnection(Player player) {
        try {
            return (PlayerModConnection) fPlayerConnection.get(player);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Internal initialization, called from {@link org.gotti.wurmunlimited.modloader.ModLoader#loadModsFromModDir}
     */
    public static void init() {
        final ClassPool classPool = HookManager.getInstance().getClassPool();
        try {
            CtClass ctPlayer = classPool.getCtClass("com.wurmonline.server.players.Player");

            CtField fConnection = new CtField(classPool.get("org.gotti.wurmunlimited.modcomm.PlayerModConnection"), "modConnection", ctPlayer);
            fConnection.setModifiers(Modifier.PUBLIC);
            ctPlayer.addField(fConnection, "new org.gotti.wurmunlimited.modcomm.PlayerModConnection()");

            CtClass ctCommunicator = classPool.getCtClass("com.wurmonline.server.creatures.Communicator");
            ctCommunicator.getMethod("reallyHandle", "(ILjava/nio/ByteBuffer;)V").instrument(new ExprEditor() {
                private boolean first = true;

                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getMethodName().equals("get") && first) {
                        m.replace("$_ = $proceed($$);" +
                                "if ($_ == " + ModCommConstants.CMD_MODCOMM + ") {" +
                                "   org.gotti.wurmunlimited.modcomm.ModCommHandler.handlePacket(player, byteBuffer);" +
                                "   return;" +
                                "}");
                        first = false;
                    }
                }
            });
        } catch (NotFoundException | CannotCompileException e) {
            throw new RuntimeException("Error initializing ModComm", e);
        }
    }

    /**
     * Internal late initialization, called from {@link org.gotti.wurmunlimited.modloader.server.ServerHook#fireOnServerStarted}
     */
    public static void serverStarted() {
        try {
            fPlayerConnection = Player.class.getDeclaredField("modConnection");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Error initializing ModComm", e);
        }
    }

    /**
     * Player connected handler, called from {@link org.gotti.wurmunlimited.modloader.server.ServerHook#fireOnPlayerLogin}
     */
    public static void playerConnected(Player player) {
        if (!channels.isEmpty())
            player.getCommunicator().sendNormalServerMessage(ModCommConstants.BANNER);
    }

    // === Logging ===

    static void logException(String msg, Throwable e) {
        if (logger != null)
            logger.log(Level.SEVERE, msg, e);
    }

    static void logWarning(String msg) {
        if (logger != null)
            logger.log(Level.WARNING, msg);
    }

    static void logInfo(String msg) {
        if (logger != null)
            logger.log(Level.INFO, msg);
    }

}
