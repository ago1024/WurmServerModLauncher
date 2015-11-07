package org.gotti.wurmunlimited.modloader;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javassist.Loader;
import javassist.NotFoundException;

import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modloader.interfaces.Configurable;
import org.gotti.wurmunlimited.modloader.interfaces.Initable;
import org.gotti.wurmunlimited.modloader.interfaces.PreInitable;
import org.gotti.wurmunlimited.modloader.interfaces.WurmMod;

public class ModLoader {
	
	private static Logger logger = Logger.getLogger(ModLoader.class.getName());
	
	private class ModEntry {
		private WurmMod mod;
		private Properties properties;
		private String name;
		public ModEntry(WurmMod mod, Properties properties, String name) {
			this.mod = mod;
			this.properties = properties;
			this.name = name;
		}
	}

	public ModLoader() {

	}

	public List<WurmMod> loadModsFromModDir(Path modDir) throws IOException {
		List<ModEntry> mods = new ArrayList<ModEntry>();

		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(modDir, "*.properties")) {
			for (Path modInfo : directoryStream) {
				ModEntry mod = loadModFromInfo(modInfo);
				mods.add(mod);
			}
		}
		
		// new style mods with initable will do configure, preInit, init
		mods.stream().filter(modEntry -> (modEntry.mod instanceof Initable || modEntry.mod instanceof PreInitable) && modEntry.mod instanceof Configurable).forEach(modEntry -> ((Configurable) modEntry.mod).configure(modEntry.properties));

		mods.stream().filter(modEntry -> modEntry.mod instanceof PreInitable).forEach(modEntry -> ((PreInitable)modEntry.mod).preInit());

		mods.stream().filter(modEntry -> modEntry.mod instanceof Initable).forEach(modEntry -> ((Initable)modEntry.mod).init());

		// old style mods without initable ir preinitable will just be configure, but they are handled last
		mods.stream().filter(modEntry -> !(modEntry.mod instanceof Initable || modEntry.mod instanceof PreInitable) && modEntry.mod instanceof Configurable).forEach(modEntry -> ((Configurable) modEntry.mod).configure(modEntry.properties));

		mods.stream().forEach(modEntry -> logger.info("Loaded " + modEntry.mod.getClass().getName() + " as " + modEntry.name));
		
		return mods.stream().map(modEntry -> modEntry.mod).collect(Collectors.toList());
	}

	public ModEntry loadModFromInfo(Path modInfo) throws IOException {
		Properties properties = new Properties();

		try (InputStream inputStream = Files.newInputStream(modInfo)) {
			properties.load(inputStream);
		}

		String modname = modInfo.getFileName().toString().replaceAll("\\.properties$", "");
		
		final String className = properties.getProperty("classname");
		if (className == null) {
			throw new IOException("Missing property classname for mod " + modInfo);
		}
		
		try {
			Loader loader = HookManager.getInstance().getLoader();;
			final String classpath = properties.getProperty("classpath");

			final ClassLoader classloader;
			if (classpath != null) {
				classloader = createClassLoader(modname, classpath, loader, Boolean.valueOf(properties.getProperty("sharedClassLoader", "false")));
			} else {
				classloader = loader;
			}

			WurmMod mod = classloader.loadClass(className).asSubclass(WurmMod.class).newInstance();
			return new ModEntry(mod, properties, modname);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NotFoundException e) {
			throw new IOException(e);
		}
	}
	
	private ClassLoader createClassLoader(String modname, String classpath, Loader parent, Boolean shared) throws MalformedURLException, NotFoundException {
		
		String[] entries = classpath.split(",");
		
		List<URL> urls = new ArrayList<>();
		
		for (String entry : entries) {
			Path path = Paths.get("mods", modname, entry);
			if (!Files.isRegularFile(path) && !Files.isDirectory(path)) {
				throw new MalformedURLException("Missing classpath entry " + path.toString());
			}
			
			if (shared) {
				HookManager.getInstance().getClassPool().appendClassPath(path.toString());
			} else {
				urls.add(path.toUri().toURL());
			}
		}
		
		if (shared) {
			return parent;
		} else {
			return new URLClassLoader(urls.toArray(new URL[urls.size()]), parent);
		}
	}
}
