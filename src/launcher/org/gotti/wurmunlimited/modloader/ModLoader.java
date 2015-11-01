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

import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modloader.interfaces.Configurable;
import org.gotti.wurmunlimited.modloader.interfaces.WurmMod;

public class ModLoader {

	public ModLoader() {

	}

	public List<WurmMod> loadModsFromModDir(Path modDir) throws IOException {
		List<WurmMod> mods = new ArrayList<WurmMod>();

		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(modDir, "*.properties")) {
			for (Path modInfo : directoryStream) {
				WurmMod mod = loadModFromInfo(modInfo);
				Logger.getLogger(this.getClass().getName()).info("Loaded " + mod.getClass().getName());
				mods.add(mod);
			}
		}


		for (WurmMod mod : mods) {
			mod.preInit();
		}
		
		for (WurmMod mod : mods) {
			mod.init();
		}

		return mods;
	}

	public WurmMod loadModFromInfo(Path modInfo) throws IOException {
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
			ClassLoader classloader = HookManager.getInstance().getLoader();
			final String classpath = properties.getProperty("classpath");
			if (classpath != null) {
				classloader = createClassLoader(modname, classpath, classloader);
			}

			WurmMod mod = classloader.loadClass(className).asSubclass(WurmMod.class).newInstance();
			if (mod instanceof Configurable) {
				((Configurable) mod).configure(properties);
			}
			return mod;
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			throw new IOException(e);
		}
	}
	
	private ClassLoader createClassLoader(String modname, String classpath, ClassLoader parent) throws MalformedURLException {
		
		String[] entries = classpath.split(",");
		
		List<URL> urls = new ArrayList<>();
		
		for (String entry : entries) {
			Path path = Paths.get("mods", modname, entry);
			if (Files.isRegularFile(path)) {
				urls.add(path.toUri().toURL());
			} else if (Files.isDirectory(path)) {
				urls.add(path.toUri().toURL());
			} else {
				throw new MalformedURLException("Missing classpath entry " + path.toString());
			}
		}
		
		return new URLClassLoader(urls.toArray(new URL[urls.size()]), parent);
	}
}
