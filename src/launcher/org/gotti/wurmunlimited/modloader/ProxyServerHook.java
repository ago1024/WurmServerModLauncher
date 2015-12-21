package org.gotti.wurmunlimited.modloader;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtPrimitiveType;
import javassist.NotFoundException;
import javassist.bytecode.Descriptor;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;

import org.gotti.wurmunlimited.modloader.classhooks.HookException;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modloader.classhooks.InvocationHandlerFactory;

import com.wurmonline.server.creatures.Communicator;
import com.wurmonline.server.players.Player;

/**
 * Hook into com.wurmonline.server.Server.startRunning()
 * 
 * The InvocationHandler calls startRunning() first, then fires onServerStarted event
 */
public class ProxyServerHook extends ServerHook {

	private static ProxyServerHook instance;

	private ProxyServerHook() {
		registerStartRunningHook();
		registerItemTemplatesCreatedHook();
		registerOnMessageHook();
		registerOnPlayerLoginHook();
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

		HookManager.getInstance().registerHook("com.wurmonline.server.Server", "startRunning", "()V", invocationHandlerFactory);
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
						code.append(String.format("if (%s#communicatorMessageHook(this, $1)) { return; };\n", ProxyServerHook.class.getName()));
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
		} catch (NotFoundException e) {
			throw new HookException(e);
		}
	}
	
	
	public static boolean communicatorMessageHook(Communicator communicator, String message) {
		return getInstance().fireOnMessage(communicator, message);
	}

	public static synchronized ProxyServerHook getInstance() {
		if (instance == null) {
			instance = new ProxyServerHook();
		}
		return instance;
	}
}
