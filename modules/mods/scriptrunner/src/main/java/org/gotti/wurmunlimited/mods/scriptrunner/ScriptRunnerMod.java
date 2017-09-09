package org.gotti.wurmunlimited.mods.scriptrunner;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.gotti.wurmunlimited.modloader.interfaces.Configurable;
import org.gotti.wurmunlimited.modloader.interfaces.Initable;
import org.gotti.wurmunlimited.modloader.interfaces.ItemTemplatesCreatedListener;
import org.gotti.wurmunlimited.modloader.interfaces.MessagePolicy;
import org.gotti.wurmunlimited.modloader.interfaces.ModEntry;
import org.gotti.wurmunlimited.modloader.interfaces.ModListener;
import org.gotti.wurmunlimited.modloader.interfaces.PlayerLoginListener;
import org.gotti.wurmunlimited.modloader.interfaces.PlayerMessageListener;
import org.gotti.wurmunlimited.modloader.interfaces.PreInitable;
import org.gotti.wurmunlimited.modloader.interfaces.ServerPollListener;
import org.gotti.wurmunlimited.modloader.interfaces.ServerShutdownListener;
import org.gotti.wurmunlimited.modloader.interfaces.ServerStartedListener;
import org.gotti.wurmunlimited.modloader.interfaces.WurmServerMod;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import com.wurmonline.server.MiscConstants;
import com.wurmonline.server.creatures.Communicator;
import com.wurmonline.server.items.ItemTypes;
import com.wurmonline.server.players.Player;

public class ScriptRunnerMod implements WurmServerMod, Configurable, Initable, PreInitable, ServerStartedListener, ServerShutdownListener, ItemTemplatesCreatedListener, PlayerLoginListener, PlayerMessageListener, ItemTypes, MiscConstants, ServerPollListener, ModListener {
	
	private static final Logger LOGGER = Logger.getLogger(ScriptRunnerMod.class.getName());
	private Map<String, List<ScriptRunner>> scriptRunners = new HashMap<>();
	
	@Override
	public void configure(Properties properties) {
	}

	@Override
	public void preInit() {
		ModActions.init();
	}
	
	@Override
	public void init() {
	}
	
	private void initRunner(String name, Properties properties, Path scriptsPath) {
		boolean refresh = Boolean.parseBoolean(properties.getProperty(name + ".refresh", "false"));
		final Path path = scriptsPath.resolve(name);
		
		if (!Files.exists(path) && !refresh) {
			return;
		}
		LOGGER.info(String.format("script runner %s, path: %s, refresh: %s", name, path, refresh));
		
		ArrayList<Path> importPaths = new ArrayList<Path>();
		importPaths.add(Paths.get("mods").resolve("scriptrunner/imports"));
		if (properties.getProperty("scriptsImport") != null) {
			Path importPath = Paths.get("mods").resolve(properties.getProperty("scriptsImport"));
			if (Files.isDirectory(importPath)) {
				importPaths.add(importPath);
			}
		}
		
		scriptRunners.computeIfAbsent(name, key -> new ArrayList<>()).add(new ScriptRunner(path, name, refresh, importPaths));
	}
	
	private List<Object> run(List<ScriptRunner> runners, Object... args) {
		if (runners != null) {
			return runners.stream().flatMap(runner -> runner.runScripts(args).stream()).collect(Collectors.toList());
		} else {
			return Collections.emptyList();
		}
	}
	
	@Override
	public void onServerStarted() {
		run(scriptRunners.get("onServerStarted"));
	}
	
	@Override
	public void onServerShutdown() {
		run(scriptRunners.get("onServerShutdown"));
	}
	
	@Override
	public void onItemTemplatesCreated() {
		run(scriptRunners.get("onItemTemplatesCreated"));
	}
	
	@Override
	public void onPlayerLogin(Player player) {
		run(scriptRunners.get("onPlayerLogin"), player);
	}
	
	@Override
	public void onPlayerLogout(Player player) {
		run(scriptRunners.get("onPlayerLogout"), player);
	}
	
	@Override
	public MessagePolicy onPlayerMessage(Communicator communicator, String message, String title) {
		List<Object> results = run(scriptRunners.get("onPlayerMessage"), communicator, message, title);
		if (results.stream().anyMatch(MessagePolicy.DISCARD::equals)) {
			return MessagePolicy.DISCARD;
		}
		return MessagePolicy.PASS;
	}
	
	@Override
	public boolean onPlayerMessage(Communicator communicator, String message) {
		return false;
	}
	
	@Override
	public void onServerPoll() {
		run(scriptRunners.get("onServerPoll"));
	}

	@Override
	public void modInitialized(ModEntry<?> entry) {
		if (entry == null || entry.getProperties() == null)
			return;
		
		Properties properties = entry.getProperties();
		
		String scriptsFolder = properties.getProperty("scriptsFolder", entry.getName() + "/scripts");
		
		Path scriptsPath = Paths.get("mods").resolve(scriptsFolder);
		if (Files.isDirectory(scriptsPath)) {
			LOGGER.info(entry.getName() + ": scriptsFolder: " + scriptsFolder);
		} else if (entry.getWurmMod() == this) {
			throw new IllegalArgumentException("ScriptsPath does not exist: " + scriptsPath);
		} else {
			return;
		}
		
		initRunner("onServerStarted", properties, scriptsPath);
		initRunner("onServerShutdown", properties, scriptsPath);
		initRunner("onPlayerLogin", properties, scriptsPath);
		initRunner("onPlayerLogout", properties, scriptsPath);
		initRunner("onPlayerMessage", properties, scriptsPath);
		initRunner("onItemTemplatesCreated", properties, scriptsPath);
		initRunner("onServerPoll", properties, scriptsPath);
	}
}
