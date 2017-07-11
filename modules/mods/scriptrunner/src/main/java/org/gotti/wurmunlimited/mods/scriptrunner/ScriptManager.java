package org.gotti.wurmunlimited.mods.scriptrunner;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class ScriptManager {
	
	private static ScriptManager instance;
	
	private ScriptEngineManager manager;
	private Map<Path, ScriptEngine> engines;
	
	private ScriptManager() {
		this.manager = new ScriptEngineManager();
		this.engines = new HashMap<>();
	}
	
	public static synchronized ScriptManager getInstance() {
		if (instance == null) {
			instance = new ScriptManager();
		}
		return instance;
	}
	
	public ScriptEngine refresh(Path scriptPath, List<Path> importPaths) throws IOException, ScriptException {
		ScriptEngine engine = manager.getEngineByName("nashorn");
		//engine.put("readFully", new ReadFully(scriptPath));
		
		Map<String, Object> scriptRunnerContext = new HashMap<>();
		
		scriptRunnerContext.put("ScriptPath", scriptPath.toAbsolutePath().toString());
		scriptRunnerContext.put("ScriptRoot", scriptPath.toAbsolutePath().getParent().toString());
		scriptRunnerContext.put("ImportPaths", importPaths.stream().map(path -> path.toAbsolutePath().toString()).toArray(String[]::new));
		
		engine.put("ScriptRunner", scriptRunnerContext);

		for (Path importPath : importPaths) {
			Path jvmNpm = importPath.resolve("jvm-npm.js");
			if (Files.exists(jvmNpm)) {
				try (Reader reader = Files.newBufferedReader(jvmNpm)) {
					engine.eval(reader);
					break;
				}
			}
		}
		try (Reader reader = Files.newBufferedReader(scriptPath)) {
			engine.eval(reader);
		}
		
		this.engines.put(scriptPath, engine);
		return engine;
	}
	
	public Object invoke(Path scriptPath, String methodName, Map<String, Object> context, List<Path> importDirs, Object... args) throws IOException, ScriptException {
		ScriptEngine engine = engines.get(scriptPath);
		if (engine == null) {
			engine = refresh(scriptPath, importDirs);
		}
		
		synchronized (engine) {
			engine.put("context", context);
			
			Invocable invocable = (Invocable) engine;
			try {
				return invocable.invokeFunction(methodName, args);
			} catch (NoSuchMethodException e) {
				throw new ScriptException(e);
			}
		}
	}
}
