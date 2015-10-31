package org.gotti.wurmunlimited.modloader.classhooks;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.Loader;
import javassist.NotFoundException;

public class HookManager {

	// Javassist class pool
	private ClassPool classPool;

	// Javassist class loader
	private Loader loader;

	// Invocation targets
	private Map<String, InvocationTarget> invocationTargets = new HashMap<>();

	// Instance
	private static HookManager instance;
	
	private static final Logger LOG = Logger.getLogger(HookManager.class.getName());

	private HookManager() {
		classPool = ClassPool.getDefault();
		loader = new Loader(classPool);
	}

	public static synchronized HookManager getInstance() {
		if (instance == null) {
			instance = new HookManager();
		}
		return instance;
	}

	public ClassPool getClassPool() {
		return classPool;
	}

	public Loader getLoader() {
		return loader;
	}

	/**
	 * Create a unique method name in the class. The name is generated from the baseName + "$" + number
	 * 
	 * @param ctClass
	 *            Class
	 * @param baseName
	 *            method base name
	 * @return unique name
	 */
	private static String getUniqueMethodName(CtClass ctClass, String baseName) {
		Set<String> usedNames = new HashSet<>();
		for (CtMethod method : ctClass.getDeclaredMethods()) {
			usedNames.add(method.getName());
		}

		int i = 1;
		do {
			String methodName = String.format("%s$%d", baseName, i++);
			if (!usedNames.contains(methodName)) {
				return methodName;
			}
		} while (true);
	}

	private InvocationTarget createHook(CtClass ctClass, ClassHook classHook) throws NotFoundException, CannotCompileException {
		CtMethod origMethod;
		
		if (classHook.getMethodType() != null) {
			origMethod = ctClass.getMethod(classHook.getMethodName(), classHook.getMethodType());
		} else {
			origMethod = ctClass.getDeclaredMethod(classHook.getMethodName());
		}
		
		if (Modifier.isNative(origMethod.getModifiers())) {
			throw new CannotCompileException("native methods can not be hooked");
		}
		

		String callee;
		boolean isStatic = Modifier.isStatic(origMethod.getModifiers());
		if (isStatic) {
			callee = String.format("%s.class", ctClass.getName());
		} else {
			callee = "this";
		}
		
		origMethod.setName(getUniqueMethodName(ctClass, classHook.getMethodName()));

		CtMethod newMethod = CtNewMethod.copy(origMethod, classHook.getMethodName(), ctClass, null);

		CtClass[] exceptionTypes = origMethod.getExceptionTypes();
		Class<?>[] exceptionClasses = new Class<?>[exceptionTypes.length];
		for (int i = 0; i < exceptionTypes.length; i++) {
			try {
				exceptionClasses[i] = loader.loadClass(exceptionTypes[i].getName());
			} catch (ClassNotFoundException e) {
				throw new CannotCompileException(e);
			}
		}
		
		InvocationTarget invocationTarget = new InvocationTarget(classHook.getInvocationHandler(), isStatic, origMethod.getName(), origMethod.getLongName(), exceptionClasses);

		CtClass type = newMethod.getReturnType();
		String typeName = type.getName();
		boolean voidType = "void".equals(typeName);
		
		StringBuilder builder = new StringBuilder();
		builder.append("{\n");
		if (!voidType) {
			builder.append("Object result = ");
		}
		builder.append(String.format("%s#getInstance().invoke(%s,\"%s\",$args);\n", HookManager.class.getName(), callee, origMethod.getLongName()));
		if (!voidType) {
			if (!type.isPrimitive()) {
				builder.append(String.format("return (%s)result;\n", typeName));
			} else if (type == CtClass.booleanType) {
				builder.append(String.format("return ((java.lang.Boolean)result).booleanValue();\n", typeName));
			} else if (type == CtClass.byteType) {
				builder.append(String.format("return ((java.lang.Number)result).byteValue();\n", typeName));
			} else if (type == CtClass.shortType) {
				builder.append(String.format("return ((java.lang.Number)result).shortValue();\n", typeName));
			} else if (type == CtClass.intType) {
				builder.append(String.format("return ((java.lang.Number)result).intValue();\n", typeName));
			} else if (type == CtClass.longType) {
				builder.append(String.format("return ((java.lang.Number)result).longValue();\n", typeName));
			} else if (type == CtClass.floatType) {
				builder.append(String.format("return ((java.lang.Number)result).floatValue();\n", typeName));
			} else if (type == CtClass.doubleType) {
				builder.append(String.format("return ((java.lang.Number)result).doubleValue();\n", typeName));
			} else if (type == CtClass.charType) {
				builder.append(String.format("return ((java.lang.Character)result).charValue();\n", typeName));
			}
		}
		builder.append("\n}");
		
		String body = builder.toString();
		LOG.fine(body);
		newMethod.setBody(body);
		ctClass.addMethod(newMethod);

		return invocationTarget;
	}

	/**
	 * Register a hook.
	 * 
	 * @param className
	 *            Class name to hook
	 * @param methodName
	 *            Method to hook
	 * @param methodType
	 *            Method signature to hook
	 * @param invocationHandler
	 *            InvocationHandler to call
	 */
	public void registerHook(String className, String methodName, String methodType, InvocationHandler invocationHandler) {
		ClassHook classHook = new ClassHook(methodName, methodType, invocationHandler);
		try {
			CtClass ctClass = classPool.get(className);
			InvocationTarget target = createHook(ctClass, classHook);
			invocationTargets.put(target.getIdentifier(), target);
		} catch (NotFoundException | CannotCompileException e) {
			throw new HookException(e); 
		}
	}

	/**
	 * Invoke the InvocationHandler for a class hook
	 * 
	 * @param object
	 *            Hooked class object
	 * @param wrappedMethod
	 *            Hooked method
	 * @param args
	 *            Call arguments
	 * @return Call result
	 * @throws Throwable
	 *             Throwables
	 */
	public Object invoke(Object object, String wrappedMethod, Object[] args) throws Throwable {
		// Get the invocation target
		InvocationTarget invocationTarget = invocationTargets.get(wrappedMethod);
		if (invocationTarget == null) {
			throw new HookException("Uninstrumented method " + wrappedMethod);
		}

		try {
			// Get the called method
			Method method = invocationTarget.resolveMethod(invocationTarget.isStaticMethod() ? (Class<?>)object : object.getClass());

			boolean accessible = method.isAccessible();
			method.setAccessible(true);
			try {
				// Call the invocation handler
				return invocationTarget.getInvocationHandler().invoke(object, method, args);
			} finally {
				method.setAccessible(accessible);
			}
		} catch (Throwable e) {
			for (Class<?> exceptionType : invocationTarget.getExceptionTypes()) {
				if (exceptionType.isInstance(e)) {
					throw e;
				}
			}
			throw new HookException(e);
		}
	}
}
