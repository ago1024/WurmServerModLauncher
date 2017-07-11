package org.gotti.wurmunlimited.mods.scriptrunner;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.script.ScriptException;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ScriptManagerTest {
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	private static final Path IMPORTS = Paths.get("src/dist/scriptrunner/imports");
	private static final Path RESOURCES = Paths.get("src/test/resources");
	
	private Object invoke(String name, String methodName, Object... args) throws IOException, ScriptException {
		final List<Path> imports = Collections.singletonList(IMPORTS);
		return invoke(name, methodName, imports, args);
	}

	private Object invoke(String name, String methodName, final List<Path> imports, Object... args) throws IOException, ScriptException {
		return ScriptManager.getInstance().invoke(RESOURCES.resolve(name), methodName, Collections.emptyMap(), imports, args);
	}

	@Test
	public void testRunWithResult() throws Exception {
		Object result = invoke("testRunWithResult.js", "test", "test");
		Assert.assertEquals("OK", result);
	}
	
	@Test
	public void testRequire() throws Exception {
		Object result = invoke("testRequire.js", "test");
		Assert.assertEquals("OK", result);
	}
	
	@Test
	public void testRequireDir() throws Exception {
		Object result = invoke("testRequireDir.js", "test");
		Assert.assertEquals("OK", result);
	}
	
	@Test
	public void testRequireAbsoluteDirFail() throws Exception {
		expectedException.expect(ScriptException.class);
		Object result = invoke("testRequireAbsoluteDirFail.js", "test");
		Assert.assertEquals("OK", result);
	}
	
	@Test
	public void testRequireSubDir() throws Exception {
		Object result = invoke("testRequireSubDir.js", "test");
		Assert.assertEquals("OK", result);
	}

	@Test
	public void testActions() throws Exception {
		Object result = invoke("testActions.js", "test");
		Assert.assertEquals("OK", result);
	}
	
	@Test
	public void testRequireActions() throws Exception {
		Object result = invoke("testRequireActions.js", "test");
		Assert.assertEquals("OK", result);
	}
	
	@Test
	public void testRequireDependencies() throws Exception {
		Object result = invoke("testRequireDependencies.js", "test");
		Assert.assertEquals("OK", result);
	}
	
	@Test
	public void testRequireRelativeLibraryDir() throws Exception {
		Object result = invoke("testRequireRelativeLibraryDir.js", "test", Arrays.asList(IMPORTS, RESOURCES));
		Assert.assertEquals("OK", result);
	}
}
