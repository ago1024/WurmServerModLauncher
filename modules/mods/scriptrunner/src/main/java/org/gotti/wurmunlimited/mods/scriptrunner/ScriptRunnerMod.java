package org.gotti.wurmunlimited.mods.scriptrunner;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import org.gotti.wurmunlimited.modloader.interfaces.Configurable;
import org.gotti.wurmunlimited.modloader.interfaces.Initable;
import org.gotti.wurmunlimited.modloader.interfaces.ItemTemplatesCreatedListener;
import org.gotti.wurmunlimited.modloader.interfaces.MessagePolicy;
import org.gotti.wurmunlimited.modloader.interfaces.PlayerLoginListener;
import org.gotti.wurmunlimited.modloader.interfaces.PlayerMessageListener;
import org.gotti.wurmunlimited.modloader.interfaces.PreInitable;
import org.gotti.wurmunlimited.modloader.interfaces.ServerPollListener;
import org.gotti.wurmunlimited.modloader.interfaces.ServerStartedListener;
import org.gotti.wurmunlimited.modloader.interfaces.WurmServerMod;

import com.wurmonline.server.MiscConstants;
import com.wurmonline.server.creatures.Communicator;
import com.wurmonline.server.items.ItemTypes;
import com.wurmonline.server.players.Player;

public class ScriptRunnerMod implements WurmServerMod, Configurable, Initable, PreInitable, ServerStartedListener, ItemTemplatesCreatedListener, PlayerLoginListener, PlayerMessageListener, ItemTypes, MiscConstants, ServerPollListener {
	
	private static final Logger LOGGER = Logger.getLogger(ScriptRunnerMod.class.getName());
	private Map<String, ScriptRunner> scriptRunners = new HashMap<>();
	private Path scriptsPath;
	private Properties properties;
	
	@Override
	public void configure(Properties properties) {
		String scriptsFolder = properties.getProperty("scriptsFolder", "scriptrunner/scripts");
		LOGGER.info("scriptsFolder: " + scriptsFolder);
		
		this.scriptsPath = Paths.get("mods").resolve(scriptsFolder);
		if (!Files.isDirectory(this.scriptsPath)) {
			throw new IllegalArgumentException("ScriptsPath does not exist: " + scriptsPath);
		}
		
		this.properties = properties;
		
		initRunner("onServerStarted");
		initRunner("onPlayerLogin");
		initRunner("onPlayerLogout");
		initRunner("onPlayerMessage");
		initRunner("onItemTemplatesCreated");
		initRunner("onServerPoll");
		
	}

	@Override
	public void preInit() {
	}
	
	@Override
	public void init() {
		
	}
	
	private void initRunner(String name) {
		boolean refresh = Boolean.parseBoolean(properties.getProperty(name + ".refresh", "false"));
		final Path path = scriptsPath.resolve(name);
		
		LOGGER.info(String.format("script runner %s, path: %s, refresh: %s", name, path, refresh));
		
		scriptRunners.put(name, new ScriptRunner(path, name, refresh));
	}
	
	private List<Object> run(ScriptRunner runner, Object... args) {
		if (runner != null) {
			return runner.runScripts(args);
		} else {
			return Collections.emptyList();
		}
	}
	
	@Override
	public void onServerStarted() {
		run(scriptRunners.get("onServerStarted"));
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
}
