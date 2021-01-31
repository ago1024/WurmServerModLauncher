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
import org.gotti.wurmunlimited.modsupport.creatures.ModCreatures;
import org.gotti.wurmunlimited.modsupport.vehicles.ModVehicleBehaviours;

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
		ModVehicleBehaviours.init();
		ModCreatures.init();
	}
	
	private void initRunner(String triggerName, Properties properties, Path scriptsPath, ModEntry<?> modEntry) {
		boolean refresh = Boolean.parseBoolean(properties.getProperty(triggerName + ".refresh", "false"));
		final Path path = scriptsPath.resolve(triggerName);
		
		if (!Files.exists(path) && !refresh) {
			return;
		}
		LOGGER.info(String.format("script runner %s, path: %s, refresh: %s", triggerName, path, refresh));
		
		ArrayList<Path> importPaths = new ArrayList<Path>();
		// always add the scriptrunner imports
		importPaths.add(Paths.get("mods").resolve("scriptrunner/imports"));
		// add module specific import path
		if (properties.getProperty("scriptsImport") != null) {
			Path importPath = Paths.get("mods").resolve(properties.getProperty("scriptsImport"));
			if (Files.isDirectory(importPath)) {
				importPaths.add(importPath);
			}
		}
		
		final ClassLoader classLoader = modEntry.getModClassLoader();
		scriptRunners.computeIfAbsent(triggerName, key -> new ArrayList<>()).add(new ScriptRunner(path, triggerName, refresh, importPaths, classLoader));
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
		
		final String modName = entry.getName();
		final String defaultScriptsFolder = modName + "/scripts";
		final String scriptsFolder = properties.getProperty("scriptsFolder", defaultScriptsFolder);
		
		Path scriptsPath = Paths.get("mods").resolve(scriptsFolder);
		if (Files.isDirectory(scriptsPath)) {
			LOGGER.info(modName + ": scriptsFolder: " + scriptsFolder);
		} else if (entry.getWurmMod() == this) {
			// the scripts path must exist if the mod that's being initialized is the scriptrunner mod
			throw new IllegalArgumentException("ScriptsPath does not exist: " + scriptsPath);
		} else {
			return;
		}

		ModContext context = new ModContext(properties, scriptsPath, entry);

		context.initRunner("onServerStarted");
		context.initRunner("onServerShutdown");
		context.initRunner("onPlayerLogin");
		context.initRunner("onPlayerLogout");
		context.initRunner("onPlayerMessage");
		context.initRunner("onItemTemplatesCreated");
		context.initRunner("onServerPoll");
	}

	private class ModContext {
		
		private final Properties properties;
		private final Path scriptsPath;
		private final ModEntry<?> modEntry;

		public ModContext(Properties properties, Path scriptsPath, ModEntry<?> modEntry) {
			this.properties = properties;
			this.scriptsPath = scriptsPath;
			this.modEntry = modEntry;
		}

		private void initRunner(String triggerName) {
			ScriptRunnerMod.this.initRunner(triggerName, properties, scriptsPath, modEntry);
		}
	}
}
