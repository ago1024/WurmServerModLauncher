package org.gotti.wurmunlimited.mods.servermap;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Formatter;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;

import org.gotti.wurmunlimited.modloader.interfaces.Configurable;
import org.gotti.wurmunlimited.modloader.interfaces.ServerStartedListener;
import org.gotti.wurmunlimited.modloader.interfaces.WurmServerMod;
import org.gotti.wurmunlimited.mods.servermap.renderer.InternalServerMapRenderer;
import org.gotti.wurmunlimited.mods.servermap.renderer.IsoMetricServerMapRenderer;
import org.gotti.wurmunlimited.mods.serverpacks.api.ServerPacks;
import org.gotti.wurmunlimited.mods.serverpacks.api.ServerPacks.ServerPackOptions;

import com.wurmonline.server.ServerDirInfo;
import com.wurmonline.server.Servers;

/**
 * Create a server pack with the server map
 */
public class ServerMapMod implements WurmServerMod, Configurable, ServerStartedListener {

	private Logger logger = Logger.getLogger(ServerMapMod.class.getName());
	
	private enum Renderer {
		SHADED {
			@Override
			public ServerMapRenderer create() {
				return new InternalServerMapRenderer();
			}
		},
		ISOMETRIC {
			@Override
			public ServerMapRenderer create() {
				return new IsoMetricServerMapRenderer();
			}
		},
		;
		public abstract ServerMapRenderer create();
		
		public static Renderer parse(String name) {
			for (Renderer renderer : Renderer.values()) {
				if (renderer.name().equalsIgnoreCase(name)) {
					return renderer;
				}
			}
			String values = Arrays.stream(Renderer.values()).map(Renderer::name).collect(Collectors.joining(", "));
			String message = String.format("Invalid renderer %s. Possible values are: ", name, values);
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * add a pack with the server map
	 */
	private boolean addServerMapPack = true;

	/**
	 * Render the server map from the surface data
	 */
	private boolean renderServerMap = true;
	
	/**
	 * Renderer used to create the map dump
	 */
	private Renderer renderer;

	@Override
	public void configure(Properties properties) {
		renderServerMap = Boolean.parseBoolean(properties.getProperty("renderServerMap", "true"));
		addServerMapPack = Boolean.parseBoolean(properties.getProperty("addServerMapPack", "true"));
		renderer = Renderer.parse(properties.getProperty("renderer", "isometric"));

		logger.info("renderServerMap: " + renderServerMap);
		logger.info("addServerMapPack: " + addServerMapPack);
		logger.info("renderer: " + renderer.name());
	}

	@Override
	public void onServerStarted() {
		if (addServerMapPack) {
			addServerMapPack(Servers.localServer.mapname);
		}
	}

	/**
	 * Add the server map pack
	 * @param mapName Server map name
	 */
	private void addServerMapPack(String mapName) {
		try {
			byte[] data = createServerPackData(mapName);
			if (data != null) {
				ServerPacks.getInstance().addServerPack("map_" + mapName, data, ServerPackOptions.FORCE, ServerPackOptions.PREPEND);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Create the Jar with map and mappings.txt
	 * @param mapName Server map name
	 * @return binary data of the jar file
	 * @throws IOException IO Exception
	 */
	private byte[] createServerPackData(String mapName) throws IOException {
		String mapFileName = "map/" + mapName + ".png";
		BufferedImage image = getServerMap(mapName);
		if (image == null) {
			return null;
		}

		BufferedImage mapImage = new BufferedImage(920, 620, BufferedImage.TYPE_INT_RGB);
		mapImage.createGraphics().drawImage(image, 150, 0, 620, 620, null);

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try (ZipOutputStream zos = new ZipOutputStream(os)) {
			zos.putNextEntry(new ZipEntry(mapFileName));
			ImageIO.write(mapImage, "PNG", zos);
			zos.closeEntry();

			zos.putNextEntry(new ZipEntry("mappings.txt"));
			try (Formatter mapping = new Formatter()) {
				mapping.format("map.%s = %s\n", mapName.toLowerCase(Locale.ROOT), mapFileName);
				zos.write(mapping.out().toString().getBytes(StandardCharsets.UTF_8));
			}
			zos.closeEntry();
		}
		return os.toByteArray();
	}

	/**
	 * Get the server map. Optionally render the map from the live data
	 * @param mapName Server map name
	 * @return server map image
	 * @throws IOException IO Exception
	 */
	private BufferedImage getServerMap(String mapName) throws IOException {
		if (renderServerMap) {
			// render the map from the ingame data
			return renderer.create().renderServerMap();
		} else {
			// Look for map dumps
			Path mapFile = Paths.get(ServerDirInfo.getFileDBPath()).resolve(mapName + ".png");
			if (Files.exists(mapFile)) {
				return ImageIO.read(mapFile.toFile());
			}
			mapFile = Paths.get(ServerDirInfo.getFileDBPath()).resolve("render.png");
			if (Files.exists(mapFile)) {
				return ImageIO.read(mapFile.toFile());
			}
			mapFile = Paths.get(ServerDirInfo.getFileDBPath()).resolve("../mapdump.png");
			if (Files.exists(mapFile)) {
				return ImageIO.read(mapFile.toFile());
			}
			return null;
		}
	}
}
