package org.gotti.wurmunlimited.mods.scriptrunner;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
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
	
	public ScriptEngine refresh(Path scriptPath) throws IOException, ScriptException {
		ScriptEngine engine = manager.getEngineByName("nashorn");
		
		try (Reader reader = Files.newBufferedReader(scriptPath)) {
			engine.eval(reader);
		}
		
		this.engines.put(scriptPath, engine);
		return engine;
	}
	
	public Object invoke(Path scriptPath, String methodName, Object... args) throws IOException, ScriptException {
		ScriptEngine engine = engines.get(scriptPath);
		if (engine == null) {
			engine = refresh(scriptPath);
		}
		
		Invocable invocable = (Invocable) engine;
		try {
			return invocable.invokeFunction(methodName, args);
		} catch (NoSuchMethodException e) {
			throw new ScriptException(e);
		}
	}
}
