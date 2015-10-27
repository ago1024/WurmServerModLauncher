package org.gotti.wurmunlimited.modloader.classhooks;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javassist.Loader;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class HookManagerTest {
	
	public static class TestClass {
		
		public static final int RETURN_VALUE = 1234;
		
		public static int staticMethod() {
			return staticPrivateMethod();
		}
		
		private static int staticPrivateMethod() {
			return RETURN_VALUE;
		}
		
		public int method() {
			return privateMethod();
		}
		
		private int privateMethod() {
			return RETURN_VALUE;
		}
	}
	
	private static Set<String> calledMethods = new HashSet<>(); 
	
	private static Loader loader;
	
	@BeforeClass
	public static void setupClass() throws Exception {
		
		loader = HookManager.getInstance().getLoader();
		loader.delegateLoadingOf("org.gotti.wurmunlimited.modloader.classhooks.HookManager");
		
		for (final String methodName : Arrays.asList("method", "privateMethod", "staticMethod", "staticPrivateMethod")) {
			HookManager.getInstance().registerHook("org.gotti.wurmunlimited.modloader.classhooks.HookManagerTest$TestClass", methodName, null, new InvocationHandler() {
				@Override
				public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
					calledMethods.add(methodName);
					return method.invoke(proxy, args);
				}
			});
		}
	}
	
	@Before
	public void setup() throws Exception {
		calledMethods.clear();
		
		Class<?> testClass = loader.loadClass("org.gotti.wurmunlimited.modloader.classhooks.HookManagerTest$TestClass");
		Method method = testClass.getMethod("method");
		Method staticMethod = testClass.getMethod("staticMethod");
		
		Object testObject = testClass.newInstance();
		
		method.invoke(testObject);
		staticMethod.invoke(null);
	}
	
	@Test
	public void testHookPublic() {
		Assert.assertTrue(calledMethods.contains("method"));
	}
	
	@Test
	public void testHookPrivate() {
		Assert.assertTrue(calledMethods.contains("privateMethod"));
	}

	
	@Test
	public void testHookPublicStatic() {
		Assert.assertTrue(calledMethods.contains("staticMethod"));
	}

	@Test
	public void testHookPrivateStatic() {
		Assert.assertTrue(calledMethods.contains("staticPrivateMethod"));
	}
	

}
