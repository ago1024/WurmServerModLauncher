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

		public void voidMethod() {
		}

		public boolean booleanMethod() {
			return true;
		}

		public char charMethod() {
			return 'c';
		}

		public byte byteMethod() {
			return (byte) 1;
		}

		public short shortMethod() {
			return (short) 1;
		}

		public int intMethod() {
			return (int) 1;
		}

		public long longMethod() {
			return (long) 1;
		}

		public float floatMethod() {
			return (float) 1;
		}

		public double doubleMethod() {
			return (double) 1;
		}

		public String stringMethod() {
			return "1";
		}

		public static void main(String[] args) {
			TestClass self = new TestClass();
			staticMethod();
			self.method();
			self.voidMethod();
			self.booleanMethod();
			self.charMethod();
			self.byteMethod();
			self.shortMethod();
			self.intMethod();
			self.longMethod();
			self.floatMethod();
			self.doubleMethod();
			self.stringMethod();
		}
	}

	private static Set<String> calledMethods = new HashSet<>();

	private static Loader loader;

	@BeforeClass
	public static void setupClass() throws Exception {

		loader = HookManager.getInstance().getLoader();
		loader.delegateLoadingOf("org.gotti.wurmunlimited.modloader.classhooks.HookManager");

		for (final String methodName : Arrays.asList("method", "privateMethod", "staticMethod", "staticPrivateMethod")) {
			HookManager.getInstance().registerHook("org.gotti.wurmunlimited.modloader.classhooks.HookManagerTest$TestClass", methodName, "()I", new InvocationHandlerFactory() {

				public InvocationHandler createInvocationHandler() {
					return new InvocationHandler() {
						@Override
						public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
							calledMethods.add(methodName);
							return method.invoke(proxy, args);
						}
					};
				};

			});
		}

		for (final String methodName : Arrays.asList("voidMethod", "booleanMethod", "charMethod", "byteMethod", "shortMethod", "intMethod", "longMethod", "floatMethod", "doubleMethod", "stringMethod")) {
			HookManager.getInstance().registerHook("org.gotti.wurmunlimited.modloader.classhooks.HookManagerTest$TestClass", methodName, null, new InvocationHandlerFactory() {

				@Override
				public InvocationHandler createInvocationHandler() {
					return new InvocationHandler() {
						@Override
						public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
							calledMethods.add(methodName);
							return method.invoke(proxy, args);
						}
					};
				}
			});
		}

		for (final String methodName : Arrays.asList("voidMethod")) {
			HookManager.getInstance().registerHook("org.gotti.wurmunlimited.modloader.classhooks.HookManagerTest$TestClass", methodName, null, new InvocationHandlerFactory() {

				@Override
				public InvocationHandler createInvocationHandler() {
					return new InvocationHandler() {
						@Override
						public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
							calledMethods.add(methodName + "_twice");
							return method.invoke(proxy, args);
						}
					};
				}
			});
		}

	}

	@Before
	public void setup() throws Throwable {
		calledMethods.clear();

		loader.run("org.gotti.wurmunlimited.modloader.classhooks.HookManagerTest$TestClass", new String[0]);
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

	@Test
	public void testHookVoid() {
		Assert.assertTrue(calledMethods.contains("voidMethod"));
	}

	@Test
	public void testHookPrimitives() {
		Assert.assertTrue(calledMethods.contains("booleanMethod"));
		Assert.assertTrue(calledMethods.contains("charMethod"));
		Assert.assertTrue(calledMethods.contains("byteMethod"));
		Assert.assertTrue(calledMethods.contains("shortMethod"));
		Assert.assertTrue(calledMethods.contains("intMethod"));
		Assert.assertTrue(calledMethods.contains("longMethod"));
		Assert.assertTrue(calledMethods.contains("floatMethod"));
		Assert.assertTrue(calledMethods.contains("doubleMethod"));
		Assert.assertTrue(calledMethods.contains("stringMethod"));
	}

	@Test
	public void testHookTwice() {
		Assert.assertTrue(calledMethods.contains("voidMethod_twice"));
	}

}
