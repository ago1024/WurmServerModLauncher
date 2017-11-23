package org.gotti.wurmunlimited.mods.httpserver;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gotti.wurmunlimited.modloader.interfaces.ModEntry;
import org.gotti.wurmunlimited.modloader.interfaces.WurmServerMod;
import org.gotti.wurmunlimited.mods.httpserver.api.ModHttpServer;

public class ModHttpServerImpl implements ModHttpServer {
	
	private static class Handler {
		Pattern modName;
		Pattern pattern;
		Function<String, InputStream> handler;
	}
	
	private static Logger logger = Logger.getLogger(HttpServerMod.class.getName());

	private static ModHttpServerImpl instance;

	private Map<Object, String> modEntries = new HashMap<>();
	private List<Handler> handlers = new ArrayList<>();

	private int port = 0;
	private int publicServerPort = 0;
	private String publicServerAddress = null;
	private String internalServerAddress = null;
	private int maxThreads = 10;

	private PackServer packServer;

	public static synchronized ModHttpServerImpl getInstance() {
		if (instance == null) {
			instance = new ModHttpServerImpl();
		}
		return instance;
	}

	protected void addModEntry(ModEntry<?> entry) {
		modEntries.put(entry.getWurmMod(), entry.getName());
	}
	
	/**
	 * Configure the server
	 * 
	 * @param internalServerAddress interface to bind to
	 * @param port Server port
	 * @param publicServerAddress publicly announced address 
	 * @param publicServerPort publicly announced port
	 */
	public void configure(String internalServerAddress, int port, String publicServerAddress, int publicServerPort) {
		this.port = port;
		this.publicServerAddress = publicServerAddress;
		this.publicServerPort = publicServerPort;
		this.internalServerAddress = internalServerAddress;
	}
	
	@Override
	public String serve(WurmServerMod mod, Pattern regex, Function<String, InputStream> dataSupplier) {
		String modName = modEntries.get(Objects.requireNonNull(mod, "mod must not be null"));
		if (modName == null) {
			logger.log(Level.WARNING, String.format("Mod %s is unknown", mod));
			return null;
		}
		
		Handler handler = new Handler();
		handler.modName = Pattern.compile("^/" + Pattern.quote(modName) + "(?<path>(/.*|$))"); 
		handler.pattern = regex;
		handler.handler = dataSupplier;
		this.handlers.add(handler);
		return "/" + modName + "/";
	}
	
	@Override
	public String serve(WurmServerMod mod, Pattern regex, Supplier<InputStream> dataSupplier) {
		return serve(mod, regex, path -> dataSupplier.get());
	}
	
	private InputStream readFile(Path file) {
		try {
			return Files.newInputStream(file);
		} catch (IOException e) {
			logger.log(Level.WARNING, e.getMessage(), e);
			return null;
		}
	}

	@Override
	public String serve(WurmServerMod mod, Pattern regex, Path file) {
		return serve(mod, regex, path -> readFile(file));
	}

	/**
	 * Start the http server
	 * @throws IOException
	 */
	protected void start() throws IOException {
		this.packServer = new PackServer(port, publicServerAddress, publicServerPort, internalServerAddress, maxThreads) {
			@Override
			protected InputStream getStream(String path) throws IOException {
				return handle(path);
			}
		};
	}
	
	/**
	 * Stop the http server
	 * @throws IOException
	 */
	protected void stop() throws IOException {
		if (this.packServer != null) {
			this.packServer.stop();
			this.packServer = null;
		}
	}
	
	/**
	 * Set maximum number of threads serving parallel downloads
	 * @param maxThreads maximum number of thread
	 */
	protected void setMaxThreads(int maxThreads) {
		this.maxThreads = maxThreads;
	}
	
	private InputStream handle(String path) {
		for (Handler handler : handlers) {
			final Matcher matcher = handler.modName.matcher(path);
			if (!matcher.matches()) {
				continue;
			}
			
			final String subpath = matcher.group("path");
			final Matcher subpathMatcher = handler.pattern.matcher(subpath); 
			if (subpathMatcher.matches()) {
				String p = subpath;
				try {
					p = subpathMatcher.group("path");
				} catch (IllegalArgumentException e) {
				}
				return handler.handler.apply(p);
			}
		}
		return null;
	}
	
	@Override
	public boolean isRunning() {
		return packServer != null;
	}
	
	@Override
	public URI getUri() throws URISyntaxException {
		if (packServer != null) {
			return packServer.getUri();
		}
		return null;
	}
}
