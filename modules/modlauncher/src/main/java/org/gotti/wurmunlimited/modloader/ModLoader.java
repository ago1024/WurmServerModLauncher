package org.gotti.wurmunlimited.modloader;

import java.io.Closeable;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.Loader;
import javassist.NotFoundException;
import javassist.Translator;

import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modloader.interfaces.Configurable;
import org.gotti.wurmunlimited.modloader.interfaces.Initable;
import org.gotti.wurmunlimited.modloader.interfaces.ModEntry;
import org.gotti.wurmunlimited.modloader.interfaces.ModListener;
import org.gotti.wurmunlimited.modloader.interfaces.PreInitable;
import org.gotti.wurmunlimited.modloader.interfaces.WurmMod;

public class ModLoader {
	
	private static Logger logger = Logger.getLogger(ModLoader.class.getName());
	
	private class Entry implements ModEntry {
		private WurmMod mod;
		private Properties properties;
		private String name;
		public Entry(WurmMod mod, Properties properties, String name) {
			this.mod = mod;
			this.properties = properties;
			this.name = name;
		}
		@Override
		public String getName() {
			return name;
		}
		@Override
		public Properties getProperties() {
			return properties;
		}
		@Override
		public WurmMod getWurmMod() {
			return mod;
		}
	}

	public ModLoader() {

	}

	public List<WurmMod> loadModsFromModDir(Path modDir) throws IOException {
		List<Entry> mods = new ArrayList<Entry>();

		logger.info(String.format("ModLoader version %1$s", this.getClass().getPackage().getImplementationVersion()));

		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(modDir, "*.properties")) {
			for (Path modInfo : directoryStream) {
				try (EarlyLoadingChecker c = initEarlyLoadingChecker(modInfo.getFileName().toString().replaceAll("\\.properties$", ""), "load")) {
					Entry mod = loadModFromInfo(modInfo);
					mods.add(mod);
				}
			}
		}
		
		// new style mods with initable will do configure, preInit, init
		mods.stream().filter(modEntry -> (modEntry.mod instanceof Initable || modEntry.mod instanceof PreInitable) && modEntry.mod instanceof Configurable).forEach(modEntry -> { 
			try (EarlyLoadingChecker c = initEarlyLoadingChecker(modEntry.name, "configure")) {
				((Configurable) modEntry.mod).configure(modEntry.properties);
				}
			});

		mods.stream().filter(modEntry -> modEntry.mod instanceof PreInitable).forEach(modEntry -> {
			try (EarlyLoadingChecker c = initEarlyLoadingChecker(modEntry.name, "preinit")) {
				((PreInitable)modEntry.mod).preInit();
				}
			});

		mods.stream().filter(modEntry -> modEntry.mod instanceof Initable).forEach(modEntry -> {
			try (EarlyLoadingChecker c = initEarlyLoadingChecker(modEntry.name, "init")) {
				((Initable)modEntry.mod).init();
				}
			});

		// old style mods without initable ir preinitable will just be configure, but they are handled last
		mods.stream().filter(modEntry -> !(modEntry.mod instanceof Initable || modEntry.mod instanceof PreInitable) && modEntry.mod instanceof Configurable).forEach(modEntry -> {
			try (EarlyLoadingChecker c = initEarlyLoadingChecker(modEntry.name, "configure")) {
				((Configurable) modEntry.mod).configure(modEntry.properties);
				}
			});

		mods.stream().forEach(modEntry -> {
			String implementationVersion = modEntry.mod.getClass().getPackage().getImplementationVersion();
			if (implementationVersion == null || implementationVersion.isEmpty()) {
				implementationVersion = "unversioned";
			}
			logger.info(String.format("Loaded %1$s as %2$s (%3$s)", modEntry.mod.getClass().getName(),  modEntry.name, implementationVersion));
		});
		
		// Send the list of initialized mods to all modlisteners
		mods.stream().filter(modEntry -> modEntry.mod instanceof ModListener).forEach(modEntry -> {
			try (EarlyLoadingChecker c = initEarlyLoadingChecker(modEntry.name, "modListener")) {
				mods.stream().forEach(mod -> ((ModListener)modEntry.mod).modInitialized(mod));
				}
			});
		
		return mods.stream().map(modEntry -> modEntry.mod).collect(Collectors.toList());
	}

	public Entry loadModFromInfo(Path modInfo) throws IOException {
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
			return new Entry(mod, properties, modname);
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
	
	private interface EarlyLoadingChecker extends Closeable {
		@Override
		public void close();
	}
	
	private EarlyLoadingChecker initEarlyLoadingChecker(String modname, String phase) {

		final List<String> earlyLoaded = new LinkedList<>();
		
		try {
			HookManager.getInstance().getLoader().addTranslator(HookManager.getInstance().getClassPool(), new Translator() {

				@Override
				public void start(ClassPool paramClassPool) throws NotFoundException, CannotCompileException {
				}
				
				@Override
				public void onLoad(ClassPool paramClassPool, String paramString) throws NotFoundException, CannotCompileException {
					if (paramString.startsWith("com.wurmonline.") && !paramString.endsWith("Exception")) {
						earlyLoaded.add(paramString);
					}
				}
			});
			
			return new EarlyLoadingChecker() {
				
				@Override
				public void close() {
					
					if (!earlyLoaded.isEmpty()) {
						for (String classname : earlyLoaded) {
							logger.log(Level.WARNING, String.format("Mod %1$s loaded server class %3$s during phase %2$s", modname, phase, classname));
						}
					}
					
					try {
						HookManager.getInstance().getLoader().addTranslator(HookManager.getInstance().getClassPool(), NOOP_TRANSLATOR);
					} catch (CannotCompileException | NotFoundException e) {
					}
				}
			};
		} catch (CannotCompileException | NotFoundException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	private final static Translator NOOP_TRANSLATOR = new Translator() {
		@Override
		public void start(ClassPool paramClassPool) throws NotFoundException, CannotCompileException {
		}
		
		@Override
		public void onLoad(ClassPool paramClassPool, String paramString) throws NotFoundException, CannotCompileException {
		}
	};
}
