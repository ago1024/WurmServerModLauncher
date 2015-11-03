package org.gotti.wurmunlimited.modloader;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtPrimitiveType;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.Bytecode;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool;
import javassist.bytecode.Descriptor;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

import org.gotti.wurmunlimited.modloader.classhooks.HookException;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modloader.classhooks.InvocationHandlerFactory;

import com.wurmonline.server.creatures.Communicator;

/**
 * Hook into com.wurmonline.server.Server.startRunning()
 * 
 * The InvocationHandler calls startRunning() first, then fires onServerStarted event
 */
public class ProxyServerHook extends ServerHook {

	private static ProxyServerHook instance;

	private ProxyServerHook() {
		registerStartRunningHook();
		registerOnMessageHook();
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
	
	private void registerOnMessageHook() {
		try {
			ClassPool classPool = HookManager.getInstance().getClassPool();
			CtClass ctCommunicator = classPool.get("com.wurmonline.server.creatures.Communicator");

			CtClass[] paramTypes = {
					CtPrimitiveType.intType,
					classPool.get("java.nio.ByteBuffer")
			};
			
			String descriptor = Descriptor.ofMethod(CtClass.booleanType, new CtClass[] {
					classPool.get("com.wurmonline.server.creatures.Communicator"),
					classPool.get(String.class.getName())
			});
			
			CtMethod method = ctCommunicator.getMethod("reallyHandle", Descriptor.ofMethod(CtPrimitiveType.voidType, paramTypes));
			MethodInfo methodInfo = method.getMethodInfo();
			CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
			ConstPool constPool = methodInfo.getConstPool();
			
			LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
			int messageIndex = -1;
			for (int i = 0; i < attr.tableLength(); i++) {
				if ("message".equals(attr.variableName(i))) {
					messageIndex = attr.index(i);
				}
			}
			
			if (messageIndex == -1) {
				throw new HookException("Message variable can not be resolved");
			}
			
			CodeIterator codeIterator = codeAttribute.iterator();
			int lastOp = 0;
			while (codeIterator.hasNext()) {
				int pos = codeIterator.next();
				int op = codeIterator.byteAt(pos);
				if (op == CodeIterator.PUTSTATIC) {
					int indexByte1 = codeIterator.byteAt(pos + 1);
					int indexByte2 = codeIterator.byteAt(pos + 2);
					int constPoolIndex = indexByte1 << 8 | indexByte2;
					if ("commandMessage".equals(constPool.getFieldrefName(constPoolIndex)) && lastOp == CodeIterator.ALOAD) {
						Bytecode bytecode = new Bytecode(constPool);
						bytecode.add(Bytecode.ALOAD_0);
						bytecode.addAload(codeIterator.byteAt(pos - 1));
						bytecode.addInvokestatic(classPool.get(this.getClass().getName()), "communicatorMessageHook", descriptor);
						bytecode.add(Bytecode.IFEQ, 0, 4, Bytecode.RETURN);
						codeIterator.insertAt(pos + 3, bytecode.get());
						break;
					}
				}
				lastOp = op;
			}
			
			methodInfo.rebuildStackMap(classPool);
		} catch (NotFoundException | BadBytecode e) {
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
