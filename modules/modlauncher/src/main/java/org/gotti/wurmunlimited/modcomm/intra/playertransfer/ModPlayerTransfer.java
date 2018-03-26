package org.gotti.wurmunlimited.modcomm.intra.playertransfer;

import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.gotti.wurmunlimited.modloader.callbacks.CallbackApi;
import org.gotti.wurmunlimited.modloader.classhooks.HookException;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modsupport.IdType;

import com.wurmonline.server.Server;
import com.wurmonline.server.ServerEntry;
import com.wurmonline.server.items.Item;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.Descriptor;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

/**
 * Player transfer adjustments
 */
public class ModPlayerTransfer {

	static final Logger LOG = Logger.getLogger(ModPlayerTransfer.class.getName());

	private static ModPlayerTransfer instance;

	// Template mappers per remote server
	private Map<Integer, TemplateIdMapper> itemTemplateMappers = new ConcurrentHashMap<>();
	private Map<Integer, TemplateIdMapper> creatureTemplateMappers = new ConcurrentHashMap<>();

	// currently globally active item template mapper. NOT THREAD SAFE!
	private TemplateIdMapper currentItemTemplateMapper;

	/**
	 * Init
	 */
	private ModPlayerTransfer() {
		initClassHooks();
	}

	/**
	 * ExprEditor which adds a hook before Item.willLeaveServer
	 */
	private static class WillItemLeaveServerEditor extends ExprEditor {
		private String targetServerName;

		public WillItemLeaveServerEditor(String targetServerName) {
			this.targetServerName = targetServerName;
		}

		@Override
		public void edit(MethodCall m) throws CannotCompileException {
			switch (m.getClassName() + "." + m.getMethodName()) {
			case "com.wurmonline.server.items.Item.willLeaveServer":
				String code = "";
				code += "int targetId = " + targetServerName + ";";
				code += "$_ = (targetId == 0 || modPlayerTransfer.willItemLeaveServer(targetId, $0, $1)) && $proceed($$);";
				m.replace("{" + code + "}");
				break;
			}
		}
	}

	/**
	 * Init class hooks
	 */
	private void initClassHooks() {
		try {

			HookManager hookManager = HookManager.getInstance();
			ClassPool classPool = new ClassPool(hookManager.getClassPool());
			//classPool.importPackage(ItemTemplateMapper.class.getPackage().getName());

			CtClass ctPlayerTransfer = classPool.get("com.wurmonline.server.intra.PlayerTransfer");
			CtClass ctCommunicator = classPool.get("com.wurmonline.server.creatures.Communicator");
			CtClass ctMailSendConfirmQuestion = classPool.get("com.wurmonline.server.questions.MailSendConfirmQuestion");
			CtClass ctMailSendQuestion = classPool.get("com.wurmonline.server.questions.MailSendQuestion");
			CtClass ctPortalQuestion = classPool.get("com.wurmonline.server.questions.PortalQuestion");
			CtClass ctServerEntry = classPool.get("com.wurmonline.server.ServerEntry");

			hookManager.addCallback(ctPlayerTransfer, "modPlayerTransfer", this);
			hookManager.addCallback(ctCommunicator, "modPlayerTransfer", this);
			hookManager.addCallback(ctMailSendConfirmQuestion, "modPlayerTransfer", this);
			hookManager.addCallback(ctMailSendQuestion, "modPlayerTransfer", this);
			hookManager.addCallback(ctPortalQuestion, "modPlayerTransfer", this);
			hookManager.addCallback(ctServerEntry, "modPlayerTransfer", this);

			// Check for custom items in willItemLeaveServer
			CtMethod ctWillItemsTransfer = ctPlayerTransfer.getMethod("willItemsTransfer", Descriptor.ofMethod(CtClass.booleanType, new CtClass[] {
					classPool.get("com.wurmonline.server.players.Player"), // player
					CtClass.booleanType, // setTransferFlag
					CtClass.intType, // targetServer
					CtClass.booleanType, // changingCluster
			}));
			ctWillItemsTransfer.instrument(new WillItemLeaveServerEditor("targetServer"));

			CtMethod ctCheckVehicle = ctCommunicator.getMethod("checkVehicle", Descriptor.ofMethod(CtClass.booleanType, classPool.get(new String[] {
					"int", // final int aNewTileX,
					"int", // final int aNewTileY,
					"boolean", // final boolean leaving
			})));
			ctCheckVehicle.instrument(new WillItemLeaveServerEditor("targetserver.id"));

			CtMethod ctCreateDraggedTransferData  = ctCommunicator.getMethod("createDraggedTransferData", Descriptor.ofMethod(classPool.get(String.class.getName()), new CtClass[] {
					classPool.get("com.wurmonline.server.ServerEntry"), // targetserver
					classPool.get("com.wurmonline.server.items.Item"), // dragged
					CtClass.intType, // aNewTileX
					CtClass.intType, // aNewTileY
					classPool.get(LinkedList.class.getName()), // passengers
			}));
			ctCreateDraggedTransferData.instrument(new WillItemLeaveServerEditor("targetserver.id"));

			CtMethod ctCreateVehicleTransferData  = ctCommunicator.getMethod("createVehicleTransferData", Descriptor.ofMethod(classPool.get(String.class.getName()), new CtClass[] {
					classPool.get("com.wurmonline.server.ServerEntry"), // targetserver
					CtClass.intType, // aNewTileX
					CtClass.intType, // aNewTileY
					classPool.get(LinkedList.class.getName()), // passengers
			}));
			ctCreateVehicleTransferData.instrument(new WillItemLeaveServerEditor("targetserver.id"));

			CtMethod ctAnswer = ctMailSendQuestion.getMethod("answer", Descriptor.ofMethod(CtClass.voidType, classPool.get(new String[] {
					Properties.class.getName()
			})));
			ctAnswer.instrument(new WillItemLeaveServerEditor("entry.id"));

			CtMethod ctAnswer2 = ctPortalQuestion.getMethod("answer", Descriptor.ofMethod(CtClass.voidType, classPool.get(new String[] {
					Properties.class.getName()
			})));
			ctAnswer2.instrument(new WillItemLeaveServerEditor("entry.id"));

			CtMethod ctSendQuestion = ctPortalQuestion.getMethod("sendQuestion", "()V");
			ctSendQuestion.instrument(new WillItemLeaveServerEditor("entry.id"));

			//
			// translate template Ids in sendItem
			//
			CtMethod ctSendItem = ctPlayerTransfer.getMethod("sendItem", Descriptor.ofMethod(CtClass.voidType, new CtClass[] {
					classPool.get("com.wurmonline.server.items.Item"), // item
					classPool.get("java.io.DataOutputStream"), // dos
					CtClass.booleanType, // dragged
			}));
			ctSendItem.instrument(new ExprEditor() {
				@Override
				public void edit(MethodCall m) throws CannotCompileException {
					switch (m.getClassName() + "." + m.getMethodName()) {
					case "com.wurmonline.server.items.Item.getTemplateId":
						m.replace("$_ = modPlayerTransfer.getRemoteItemTemplateId($proceed($$));");
						break;
					}
				}
			});

			//
			// Set the target server in methods calling PlayerTransfer.sendItem directly or indirectly
			//
			// PlayerTransfer.createPlayerData
			CtMethod ctCreatePlayerData = ctPlayerTransfer.getMethod("createPlayerData", Descriptor.ofMethod(classPool.get("byte[]"), new CtClass[] {
					classPool.get("com.wurmonline.server.players.PlayerInfo"), //final PlayerInfo pinf,
					classPool.get("com.wurmonline.server.creatures.CreatureStatus"), // final CreatureStatus status,
					classPool.get("com.wurmonline.server.skills.Skill[]"),
					CtClass.intType, //final int targServId,
					CtClass.byteType, // final byte targetKingdomId,
					CtClass.longType, // final long clientTimeDifference
			}));
			ctCreatePlayerData.insertBefore("modPlayerTransfer.setTargetServer(targServId);");
			ctCreatePlayerData.insertAfter("modPlayerTransfer.resetTargetServer();", true);

			// PlayerTransfer.createPlayerData
			CtMethod ctCreatePlayerData2 = ctPlayerTransfer.getMethod("createPlayerData", Descriptor.ofMethod(classPool.get("byte[]"), classPool.get(new String[] {
					"com.wurmonline.server.bodys.Wound[]", // final Wound[] wounds
					"com.wurmonline.server.players.PlayerInfo", // final PlayerInfo pinf
					"com.wurmonline.server.creatures.CreatureStatus", // final CreatureStatus status,
					"com.wurmonline.server.items.Item[]", // final Item[] items, 
					"com.wurmonline.server.skills.Skill[]", // final Skill[] skills,
					"com.wurmonline.server.items.Item", // final Item draggedItem,
					"int", // final int targServId,
					"long", // final long clientTimeDifference,
					"byte", // final byte targetKingdom
			})));
			ctCreatePlayerData2.insertBefore("modPlayerTransfer.setTargetServer(targServId);");
			ctCreatePlayerData2.insertAfter("modPlayerTransfer.resetTargetServer();", true);

			// Communicator.createVehicleTransferData
			ctCreateVehicleTransferData.insertBefore("modPlayerTransfer.setTargetServer(targetserver.id);");
			ctCreateVehicleTransferData.insertAfter("modPlayerTransfer.resetTargetServer();", true);

			// Communicator.createDraggedTransferData
			ctCreateDraggedTransferData.insertBefore("modPlayerTransfer.setTargetServer(targetserver.id);");
			ctCreateDraggedTransferData.insertAfter("modPlayerTransfer.resetTargetServer();", true);

			// MailSendConfirmQuestion.sendMailSetToServer
			CtMethod ctSendMailSetToServer = ctMailSendConfirmQuestion.getMethod("sendMailSetToServer", Descriptor.ofMethod(CtClass.booleanType, new CtClass[] {
					CtClass.longType, // senderId
					classPool.get("com.wurmonline.server.creatures.Creature"), // responder,
					CtClass.intType, // targetServer,
					classPool.get(Set.class.getName()), // mails, 
					CtClass.longType, // receiverId,
					classPool.get("com.wurmonline.server.items.Item[]"), // Item[] items
			}));
			ctSendMailSetToServer.insertBefore("modPlayerTransfer.setTargetServer(targetServer);");
			ctSendMailSetToServer.insertAfter("modPlayerTransfer.resetTargetServer();", true);

			//
			// Hook into the ServerEntry.setAvailable call to retrieve the remote template lists
			//
			CtMethod ctSetAvailable = ctServerEntry.getMethod("setAvailable", Descriptor.ofMethod(CtClass.voidType, classPool.get(new String[] {
					"boolean", // available
					"boolean", // maintain
					"int", // currentPlayerCount
					"int", // plimit
					"int", // secsToShutdown
					"int", // mSize
			})));
			ctSetAvailable.insertBefore("modPlayerTransfer.updateServerStatus(this, $1);");

		} catch (NotFoundException | CannotCompileException e) {
			throw new HookException(e);
		}
	}

	/**
	 * Set the target server for the currently active player transfer. NOT THREAD SAFE!
	 * @param targetServer Target server id
	 */
	@CallbackApi
	public void setTargetServer(int targetServer) {
		currentItemTemplateMapper = getItemTemplateMapper(targetServer);
	}

	/**
	 * Reset the target server for the currently active player transfer. NOT THREAD SAFE!
	 */
	@CallbackApi
	public void resetTargetServer() {
		currentItemTemplateMapper = null;
	}

	/**
	 * Test if the item can leave this server to the target server
	 * @param targetServer Target server id
	 * @param item Item to check
	 * @param setTransferFlag Should the {@link Item#setTransferred(boolean)} flag be set
	 * @return true if the item can leave the server
	 */
	@CallbackApi
	public boolean willItemLeaveServer(int targetServer, Item item, boolean setTransferFlag) {
		TemplateIdMapper itemTemplateMapper = getItemTemplateMapper(targetServer);
		int templateId = item.getTemplateId();
		boolean result = TemplateIdMapper.isRegularTemplate(IdType.ITEMTEMPLATE, templateId) || itemTemplateMapper != null && itemTemplateMapper.willTemplateLeaveServer(templateId);
		if (setTransferFlag && !result) {
			// Mark the item as not transferable. Yes, "true" is correct
			item.setTransferred(true);
		}
		return result;
	}

	/**
	 * Get the remote template id for this server template id. This uses the target server set via {@link #resetTargetServer()}
	 * @param templateId Template id
	 * @return  remote template id
	 */
	@CallbackApi
	public int getRemoteItemTemplateId(int templateId) {
		if (TemplateIdMapper.isRegularTemplate(IdType.ITEMTEMPLATE, templateId)) {
			return templateId;
		} else if (currentItemTemplateMapper != null) {
			return currentItemTemplateMapper.getRemoteTemplate(templateId);
		} else {
			LOG.warning("PlayerTransfer without current ItemTemplateWrapper");
			return templateId;
		}
	}

	/**
	 * remote server status update. Request template ids from the remote server 
	 * @param serverEntry Server entry
	 * @param isAvailable is the remote server available?
	 */
	@CallbackApi
	public void updateServerStatus(ServerEntry serverEntry, boolean isAvailable) {
		if (!isAvailable) {
			getInstance().itemTemplateMappers.remove(serverEntry.id);
			getInstance().creatureTemplateMappers.remove(serverEntry.id);
		} else if (!getInstance().itemTemplateMappers.containsKey(serverEntry.id) || !getInstance().creatureTemplateMappers.containsKey(serverEntry.id)) {
			GetRemoteTemplatesMessage.send(Server.getInstance(), serverEntry.id);
		} 
	}

	/**
	 * Set item template mapper for the target server
	 * @param targetServer Target server
	 * @param itemTemplateMapper Template mapper
	 */
	public static void setItemTemplateMapper(int targetServer, TemplateIdMapper itemTemplateMapper) {
		getInstance().itemTemplateMappers.put(targetServer, itemTemplateMapper);
	}

	/**
	 * Set creature template mapper for the target server
	 * @param targetServer Target server
	 * @param itemTemplateMapper Template mapper
	 */
	public static void setCreatureTemplateMapper(int targetServer, TemplateIdMapper creatureTemplateMapper) {
		getInstance().creatureTemplateMappers.put(targetServer, creatureTemplateMapper);
	}

	/**
	 * Get item template mapper for the target server
	 * @param targetServer Target server
	 * @return Template mapper
	 */
	private TemplateIdMapper getItemTemplateMapper(int targetServer) {
		return itemTemplateMappers.get(targetServer);
	}

	/**
	 * Get creature template mapper for the target server
	 * @param targetServer Target server
	 * @return Template mapper
	 */
	private TemplateIdMapper getCreatureTemplateMapper(int targetServer) {
		return creatureTemplateMappers.get(targetServer);
	}

	/**
	 * Get instance
	 * @return instance
	 */
	public static synchronized ModPlayerTransfer getInstance() {
		if (instance == null) {
			instance = new ModPlayerTransfer();
		}
		return instance;
	}

	/**
	 * init
	 */
	public static void init() {
		getInstance();
	}

	/**
	 * Statup communications
	 */
	public static void serverStarted() {
		GetRemoteTemplatesMessage.register();
	}

}
