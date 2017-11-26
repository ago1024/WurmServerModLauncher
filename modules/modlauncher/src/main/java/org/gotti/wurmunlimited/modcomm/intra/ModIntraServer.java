package org.gotti.wurmunlimited.modcomm.intra;

import java.lang.reflect.Field;

import org.gotti.wurmunlimited.modcomm.intra.playertransfer.ModPlayerTransfer;
import org.gotti.wurmunlimited.modloader.ReflectionUtil;
import org.gotti.wurmunlimited.modloader.classhooks.HookException;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;

import com.wurmonline.communication.SocketConnection;
import com.wurmonline.server.intra.IntraClient;
import com.wurmonline.server.intra.IntraServerConnection;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

/**
 * Intra server communication.
 */
public class ModIntraServer {

	// Reflection fields
	private static Field intraClientConnection;
	private static Field intraServerConnectionConn;

	// internal api handler
	private static ModIntraServerHandler modCommHandler;

	/**
	 * init
	 */
	public static void init() {
		// create api handler
		modCommHandler = new ModIntraServerHandler();

		// class hooks
		final ClassPool classPool = HookManager.getInstance().getClassPool();
		try {
			CtClass ctCommunicator = classPool.getCtClass("com.wurmonline.server.intra.IntraServerConnection");

			HookManager.getInstance().addCallback(ctCommunicator, "modcomm", modCommHandler);

			ctCommunicator.getMethod("reallyHandle", "(ILjava/nio/ByteBuffer;)V").instrument(new ExprEditor() {
				private boolean first = true;

				@Override
				public void edit(MethodCall m) throws CannotCompileException {
					if (m.getMethodName().equals("get") && first) {
						m.replace("$_ = $proceed($$);" + "if ($_ == " + ModIntraServerConstants.CMD_MODCOMM + ") { modcomm.handle(this, $0); return; }");
						first = false;
					}
				}
			});
		} catch (NotFoundException | CannotCompileException e) {
			throw new RuntimeException("Error initializing ModComm", e);
		}

		// Init player transfers
		ModPlayerTransfer.init();
	}

	/**
	 * Start communications
	 */
	public static void serverStarted() {
		try {
			intraClientConnection = ReflectionUtil.getField(IntraClient.class, "connection");
			intraServerConnectionConn = ReflectionUtil.getField(IntraServerConnection.class, "conn");

			ModPlayerTransfer.serverStarted();
		} catch (NoSuchFieldException e) {
			throw new HookException(e);
		}
	}

	/**
	 * Helper to retrieve a {@link SocketConnection} from an {@link IntraClient}
	 * 
	 * @param client
	 *            intra client
	 * @return socket connection
	 */
	public static SocketConnection getConnection(IntraClient client) {
		try {
			return ReflectionUtil.getPrivateField(client, intraClientConnection);
		} catch (IllegalArgumentException | IllegalAccessException | ClassCastException e) {
			throw new HookException(e);
		}
	}

	/**
	 * Helper to retrieve a {@link SocketConnection} from an {@link IntraServerConnection}
	 * 
	 * @param connection
	 *            intra server connection
	 * @return socket connection
	 */
	public static SocketConnection getConnection(IntraServerConnection connection) {
		try {
			return ReflectionUtil.getPrivateField(connection, intraServerConnectionConn);
		} catch (IllegalArgumentException | IllegalAccessException | ClassCastException e) {
			throw new HookException(e);
		}
	}
}
