package org.gotti.wurmunlimited.mods;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ModLoader {

	public ModLoader() {

	}

	public List<WurmMod> loadModsFromModDir(Path modDir) throws IOException {
		List<WurmMod> mods = new ArrayList<WurmMod>();

		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(modDir, "*.properties")) {
			for (Path modInfo : directoryStream) {
				mods.add(loadModFromInfo(modInfo));
			}
		}

		return mods;
	}

	public WurmMod loadModFromInfo(Path modInfo) throws IOException {
		Properties properties = new Properties();

		try (InputStream inputStream = Files.newInputStream(modInfo)) {
			properties.load(inputStream);
		}

		final String className = properties.getProperty("classname");
		if (className == null) {
			throw new IOException("Missing property classname for mod " + modInfo);
		}

		try {
			return Class.forName(className).asSubclass(WurmMod.class).newInstance();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			throw new IOException(e);
		}
	}
}
