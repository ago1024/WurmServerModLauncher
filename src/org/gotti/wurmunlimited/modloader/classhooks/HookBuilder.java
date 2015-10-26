package org.gotti.wurmunlimited.modloader.classhooks;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.Loader;
import javassist.NotFoundException;
import javassist.Translator;

public class HookBuilder {

	private ClassPool classPool;

	private Loader loader;

	private Map<String, List<ClassHook>> classes = new HashMap<>();

	private Map<String, InvocationTarget> invocationHandlers = new HashMap<>();

	private static HookBuilder instance;

	private HookBuilder() {
		classPool = ClassPool.getDefault();
		loader = new Loader(classPool);
		try {
			loader.addTranslator(classPool, new HookBuilderTranslator());
		} catch (CannotCompileException | NotFoundException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.WARNING, null, e);
		}
	}

	public static synchronized HookBuilder getInstance() {
		if (instance == null) {
			instance = new HookBuilder();
		}
		return instance;
	}

	public ClassPool getClassPool() {
		return classPool;
	}

	public Loader getLoader() {
		return loader;
	}

	private class HookBuilderTranslator implements Translator {

		@Override
		public void start(ClassPool classPool) throws NotFoundException, CannotCompileException {
		}

		@Override
		public void onLoad(ClassPool classPool, String className) throws NotFoundException, CannotCompileException {
			System.out.println(className);
			if (classes.containsKey(className)) {
				addClassHooks(classPool, className, classes.get(className));
			}
		}
		

	}

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

	private void addClassHooks(ClassPool pool, String className, List<ClassHook> classHooks) throws NotFoundException, CannotCompileException {
		CtClass ctClass = pool.get(className);
		for (ClassHook classHook : classHooks) {
			CtMethod origMethod = ctClass.getMethod(classHook.getMethodName(), classHook.getMethodType());

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
			
			InvocationTarget invocationTarget = new InvocationTarget(classHook.getInvocationHandler(), origMethod.getName(), exceptionClasses);
			invocationHandlers.put(origMethod.getLongName(), invocationTarget);

			String type = newMethod.getReturnType().getName();
			StringBuilder builder = new StringBuilder();
			builder.append("{\nObject result = org.gotti.wurmunlimited.modloader.classhooks.HookBuilder.getInstance().invoke(this,\"");
			builder.append(origMethod.getLongName());
			builder.append("\",$args);\n");
			if (!"void".equals(type)) {
				builder.append("return (" + type + ")result;\n");
			}
			builder.append("\n}");

			newMethod.setBody(builder.toString());
			ctClass.addMethod(newMethod);
		}
	}

	public void registerHook(String className, String methodName, String methodType, InvocationHandler invocationHandler) {
		List<ClassHook> classHooks = classes.get(className);
		if (classHooks == null) {
			classHooks = new ArrayList<>();
			classes.put(className, classHooks);
		}

		classHooks.add(new ClassHook(methodName, methodType, invocationHandler));
	}

	public Object invoke(Object object, String wrappedMethod, Object[] args) throws Throwable {
		InvocationTarget invocationTarget = invocationHandlers.get(wrappedMethod);
		if (invocationTarget == null) {
			throw new RuntimeException("Uninstrumented method " + wrappedMethod);
		}

		try {
			Method method = invocationTarget.resolveMethod(object.getClass());

			return invocationTarget.getInvocationHandler().invoke(object, method, args);
		} catch (Throwable e) {
			for (Class<?> exceptionType : invocationTarget.getExceptionTypes()) {
				if (exceptionType.isInstance(e)) {
					throw e;
				}
			}
			throw new RuntimeException(e);
		}
	}
}
