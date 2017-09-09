package org.gotti.wurmunlimited.modloader.server;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.gotti.wurmunlimited.modloader.classhooks.HookException;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modloader.classhooks.InvocationHandlerFactory;

import com.wurmonline.server.Message;
import com.wurmonline.server.creatures.Communicator;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.villages.PvPAlliance;
import com.wurmonline.server.villages.Village;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtPrimitiveType;
import javassist.NotFoundException;
import javassist.bytecode.Descriptor;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import javassist.expr.MethodCall;

/**
 * Hook into com.wurmonline.server.Server.startRunning()
 * 
 * The InvocationHandler calls startRunning() first, then fires onServerStarted event
 */
public class ProxyServerHook extends ServerHook {

	private static ProxyServerHook instance;

	private ProxyServerHook() {
		registerStartRunningHook();
		registerShutdownHook();
		registerItemTemplatesCreatedHook();
		registerOnMessageHook();
		registerOnPlayerLoginHook();
		registerOnServerPollHook();
		registerChannelHooks();
	}
	
	private void registerStartRunningHook() {

		InvocationHandlerFactory invocationHandlerFactory = new InvocationHandlerFactory() {

			@Override
			public InvocationHandler createInvocationHandler() {
				return new InvocationHandler() {

					@Override
					public Object invoke(Object wrapped, Method method, Object[] args) throws Throwable {
						Object result = method.invoke(wrapped, args);
						fireOnServerStarted();
						return result;
					}
				};
			}
		};

		// com.wurmonline.server.ServerLauncher.runServer(boolean)
		HookManager.getInstance().registerHook("com.wurmonline.server.ServerLauncher", "runServer", "(ZZ)V", invocationHandlerFactory);
	}
	
	private void registerItemTemplatesCreatedHook() {

		InvocationHandlerFactory invocationHandlerFactory = new InvocationHandlerFactory() {

			@Override
			public InvocationHandler createInvocationHandler() {
				return new InvocationHandler() {

					@Override
					public Object invoke(Object wrapped, Method method, Object[] args) throws Throwable {
						Object result = method.invoke(wrapped, args);
						fireOnItemTemplatesCreated();
						return result;
					}
				};
			}
		};

		HookManager.getInstance().registerHook("com.wurmonline.server.items.ItemTemplateCreator", "initialiseItemTemplates", "()V", invocationHandlerFactory);
	}
	
	
	private void registerOnMessageHook() {
		try {
			ClassPool classPool = HookManager.getInstance().getClassPool();
			CtClass ctCommunicator = classPool.get("com.wurmonline.server.creatures.Communicator");

			CtClass[] paramTypes = {
					classPool.get("java.nio.ByteBuffer")
			};
			
			// com.wurmonline.server.creatures.Communicator.reallyHandle_CMD_MESSAGE(ByteBuffer)
			CtMethod method;
			try {
				method = ctCommunicator.getMethod("reallyHandle_CMD_MESSAGE", Descriptor.ofMethod(CtPrimitiveType.voidType, paramTypes));
			} catch (NotFoundException e) {
				// Backward compatible
				method = ctCommunicator.getMethod("reallyHandle", Descriptor.ofMethod(CtPrimitiveType.voidType, paramTypes));
			}
			
			method.instrument(new ExprEditor() {
				@Override
				public void edit(FieldAccess f) throws CannotCompileException {
					if (f.isWriter() && f.getClassName().equals("com.wurmonline.server.creatures.Communicator") && f.getFieldName().equals("commandMessage")) {
						StringBuffer code = new StringBuffer();
						code.append("$proceed($$);\n");
						code.append(String.format("if (%s#communicatorMessageHook(this, $1, title)) { return; };\n", ProxyServerHook.class.getName()));
						f.replace(code.toString());
					}
				}
			});
			
		} catch (NotFoundException | CannotCompileException e) {
			throw new HookException(e);
		}
	}
	
	
	private void registerOnPlayerLoginHook() {
		
		try {
			String descriptor = Descriptor.ofMethod(CtClass.voidType, new CtClass[] {
					HookManager.getInstance().getClassPool().get("com.wurmonline.server.players.Player")
			});
			HookManager.getInstance().registerHook("com.wurmonline.server.LoginHandler", "sendLoggedInPeople", descriptor, new InvocationHandlerFactory() {
				
				@Override
				public InvocationHandler createInvocationHandler() {
					return new InvocationHandler() {
						
						@Override
						public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
							Object result = method.invoke(proxy, args);
							fireOnPlayerLogin((Player)args[0]);
							return result;
						}
					};
				}
			});
			
			//com.wurmonline.server.players.Player.logout()
			HookManager.getInstance().registerHook("com.wurmonline.server.players.Player", "logout", "()V", new InvocationHandlerFactory() {
				
				@Override
				public InvocationHandler createInvocationHandler() {
					return new InvocationHandler() {
						
						@Override
						public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
							Object result = method.invoke(proxy, args);
							fireOnPlayerLogout((Player)proxy);
							return result;
						}
					};
				}
			});

		} catch (NotFoundException e) {
			throw new HookException(e);
		}
		
	}
	
	private void registerOnServerPollHook() {
		
		HookManager.getInstance().registerHook("com.wurmonline.server.Players", "pollPlayers", "()V", new InvocationHandlerFactory() {
			@Override
			public InvocationHandler createInvocationHandler() {
				return new InvocationHandler() {
					
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						fireOnServerPoll();
						return method.invoke(proxy, args);
					}
				};
			}
		});
	}
	
	private void registerChannelHooks() {
		try {
			final ClassPool classPool = HookManager.getInstance().getClassPool();
			
			// com.wurmonline.server.creatures.Communicator.reallyHandle_CMD_MESSAGE(ByteBuffer)
			final CtMethod methodHandleMessage = classPool.get("com.wurmonline.server.creatures.Communicator").getMethod("reallyHandle_CMD_MESSAGE", "(Ljava/nio/ByteBuffer;)V");
			methodHandleMessage.instrument(new ExprEditor() {
				@Override
				public void edit(MethodCall m) throws CannotCompileException {
					// com.wurmonline.server.Players.getPlayers()
					if ("com.wurmonline.server.Players".equals(m.getClassName()) && "getPlayers".equals(m.getMethodName())) {
						
						StringBuffer code = new StringBuffer();
						code.append("{\n");
						code.append(String.format("%s#communicatorChannelHook(mess);\n", ProxyServerHook.class.getName()));
						code.append("$_ = $proceed($$);\n");
						code.append("}\n");
						m.replace(code.toString());
						
					}
				}
			});
			
			// com.wurmonline.server.villages.Village.broadCastMessage(Message, boolean)
			final CtMethod methodBroadCast = classPool.get("com.wurmonline.server.villages.Village").getMethod("broadCastMessage", "(Lcom/wurmonline/server/Message;Z)V");
			methodBroadCast.insertAfter(String.format("%s#communicatorChannelHook($0, $1);\n", ProxyServerHook.class.getName()));
			
		
		} catch (NotFoundException | CannotCompileException e) {
			throw new HookException(e);
		}
	}
	
	private void registerShutdownHook() {

		InvocationHandlerFactory invocationHandlerFactory = new InvocationHandlerFactory() {

			@Override
			public InvocationHandler createInvocationHandler() {
				return new InvocationHandler() {

					@Override
					public Object invoke(Object wrapped, Method method, Object[] args) throws Throwable {
						fireOnServerShutdown();
						Object result = method.invoke(wrapped, args);
						return result;
					}
				};
			}
		};

		// com.wurmonline.server.Server.shutDown(boolean)
		HookManager.getInstance().registerHook("com.wurmonline.server.Server", "shutDown", "()V", invocationHandlerFactory);
	}
	
	public static boolean communicatorMessageHook(Communicator communicator, String message, String title) {
		return getInstance().fireOnMessage(communicator, message, title);
	}
	
	public static boolean communicatorChannelHook(Message message) {
		return getInstance().fireOnKingdomMessage(message);
	}
	
	public static boolean communicatorChannelHook(Village village, Message message) {
		if ("Alliance".equals(message.getWindow())) {
			return false;
		}
		return getInstance().fireOnVillageMessage(village, message);
	}
	
	public static boolean communicatorChannelHook(PvPAlliance alliance, Message message) {
		return getInstance().fireOnAllianceMessage(alliance, message);
	}
	
	public static synchronized ProxyServerHook getInstance() {
		if (instance == null) {
			instance = new ProxyServerHook();
		}
		return instance;
	}
}
