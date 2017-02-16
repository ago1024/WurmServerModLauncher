package org.gotti.wurmunlimited.mods.scriptrunner;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import org.gotti.wurmunlimited.modloader.interfaces.Configurable;
import org.gotti.wurmunlimited.modloader.interfaces.Initable;
import org.gotti.wurmunlimited.modloader.interfaces.ItemTemplatesCreatedListener;
import org.gotti.wurmunlimited.modloader.interfaces.MessagePolicy;
import org.gotti.wurmunlimited.modloader.interfaces.PlayerLoginListener;
import org.gotti.wurmunlimited.modloader.interfaces.PlayerMessageListener;
import org.gotti.wurmunlimited.modloader.interfaces.PreInitable;
import org.gotti.wurmunlimited.modloader.interfaces.ServerStartedListener;
import org.gotti.wurmunlimited.modloader.interfaces.WurmServerMod;

import com.wurmonline.server.MiscConstants;
import com.wurmonline.server.creatures.Communicator;
import com.wurmonline.server.items.ItemTypes;
import com.wurmonline.server.players.Player;

public class ScriptRunnerMod implements WurmServerMod, Configurable, Initable, PreInitable, ServerStartedListener, ItemTemplatesCreatedListener, PlayerLoginListener, PlayerMessageListener, ItemTypes, MiscConstants {
	
	private Map<String, ScriptRunner> scriptRunners = new HashMap<>();
	private Path scriptsPath;
	
	@Override
	public void configure(Properties properties) {
		String scriptsFolder = properties.getProperty("scriptsFolder", "scriptrunner/scripts");
		this.scriptsPath = Paths.get("mods").resolve(scriptsFolder);
		if (!Files.isDirectory(this.scriptsPath)) {
			throw new IllegalArgumentException("ScriptsPath does not exist: " + scriptsPath);
		}
		
		initRunner("onServerStarted", true);
		initRunner("onPlayerLogin", true);
		initRunner("onPlayerLogout", true);
	}

	@Override
	public void preInit() {
	}
	
	@Override
	public void init() {
		
	}
	
	private void initRunner(String name, boolean refresh) {
		scriptRunners.put(name, new ScriptRunner(scriptsPath.resolve(name), name, refresh));
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
		return MessagePolicy.PASS;
	}
	
	@Override
	public boolean onPlayerMessage(Communicator communicator, String message) {
		return false;
	}
}
