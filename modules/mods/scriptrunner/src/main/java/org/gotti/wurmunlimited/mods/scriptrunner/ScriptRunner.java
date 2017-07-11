package org.gotti.wurmunlimited.mods.scriptrunner;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.script.ScriptException;

public class ScriptRunner {
	
	private static class ScriptState {
		private Path file;
		boolean checked = false;
		private long size;
		private FileTime mtime;
		
		public ScriptState(Path file) {
			this.file = file;
		}
		
		public boolean check() throws IOException {
			if (checked && size == Files.size(file) && this.mtime.equals(Files.getLastModifiedTime(file))) {
				return true;
			}
			this.checked = true;
			this.size = Files.size(file);
			this.mtime = Files.getLastModifiedTime(file);
			return false;
		}
	}
	
	private final boolean refresh;
	private final Path folder;
	private final Map<Path, ScriptState> states;
	
	private List<Path> scripts = Collections.emptyList();
	private String methodName;
	private List<Path> importPaths;

	public ScriptRunner(Path folder, String methodName, boolean refresh, List<Path> importDirs) {
		this.refresh = refresh;
		this.folder = folder;
		this.methodName = methodName;
		this.states = new HashMap<>(); 
		this.importPaths = importDirs;
		
		refreshScriptNames();
	}
	
	public void refreshScriptNames() {
		if (!Files.exists(folder)) {
			scripts = Collections.emptyList();
			return;
		}
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder, "*.js")) {
			scripts = StreamSupport.stream(stream.spliterator(), false)
				.filter(Files::isRegularFile)
				.collect(Collectors.toList());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private Object runScript(Path file, Map<String, Object> context, Object... args) {
		try {
			if (refresh && !states.computeIfAbsent(file, f -> new ScriptState(f)).check()) {
				ScriptManager.getInstance().refresh(file, importPaths);
			}
			return ScriptManager.getInstance().invoke(file, methodName, context, importPaths, args);
		} catch (IOException | ScriptException e) {
			String logger = String.format("%s.%s.%s", ScriptRunner.class.getName(), methodName, file.getFileName());
			Logger.getLogger(logger).log(Level.SEVERE, e.getMessage(), e);
			return null;
		}
	}
	
	public List<Object> runScripts(Object... args) {
		if (refresh) {
			refreshScriptNames();
		}
		Map<String, Object> context = new HashMap<>();
		return scripts.stream().map(path -> runScript(path, context, args)).collect(Collectors.toList());
	}

}
