package org.gotti.wurmunlimited.mods.httpserver.api;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import org.gotti.wurmunlimited.modloader.interfaces.WurmServerMod;
import org.gotti.wurmunlimited.mods.httpserver.ModHttpServerImpl;

public interface ModHttpServer {
	
	/**
	 * Serve data under /modname/path
	 * @param mod Mod to serve the URL for. The modname will be used as an URL prefix
	 * @param regex Regex to match "path". 
	 * @param dataSupplier Supplies an {@link InputStream} for "path". if "regex" contains a named group "path" (e.g. <code>(?&lt;path&gt;.*)</code>) then the groups content will be used as paramter instead.
	 * @return "/modname/" prefix or null 
	 */
	public String serve(WurmServerMod mod, Pattern regex, Function<String, InputStream> dataSupplier);
	
	/**
	 * Serve data under /modname/path
	 * @param mod Mod to serve the URL for. The modname will be used as an URL prefix
	 * @param regex Regex to match "path". 
	 * @param dataSupplier Supplies an {@link InputStream}.
	 * @return "/modname/" prefix or null 
	 */
	public String serve(WurmServerMod mod, Pattern regex, Supplier<InputStream> dataSupplier);
	
	/**
	 * Serve a file under /modname/path
	 * @param mod Mod to serve the URL for. The modname will be used as an URL prefix
	 * @param regex Regex to match "path". 
	 * @param file file to server
	 * @return "/modname/" prefix or null 
	 */
	public String serve(WurmServerMod mod, Pattern regex, Path file);
	
	/**
	 * Check if the HTTP server is running
	 * @return true if its running
	 */
	public boolean isRunning();
	
	/**
	 * Get the server base URI
	 * @return Server base URI
	 * @throws URISyntaxException
	 */
	public URI getUri() throws URISyntaxException;
	
	/**
	 * Get an instance of the ModHttpServer.
	 * @return instance of ModHttpServer
	 */
	static ModHttpServer getInstance() {
		return ModHttpServerImpl.getInstance();
	}
}
