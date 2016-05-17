package org.gotti.wurmunlimited.mods.serverpacks;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gotti.wurmunlimited.modloader.interfaces.Configurable;
import org.gotti.wurmunlimited.modloader.interfaces.Initable;
import org.gotti.wurmunlimited.modloader.interfaces.ModEntry;
import org.gotti.wurmunlimited.modloader.interfaces.ModListener;
import org.gotti.wurmunlimited.modloader.interfaces.ServerStartedListener;
import org.gotti.wurmunlimited.modloader.interfaces.WurmServerMod;
import org.gotti.wurmunlimited.modcomm.Channel;
import org.gotti.wurmunlimited.modcomm.IChannelListener;
import org.gotti.wurmunlimited.modcomm.ModComm;
import org.gotti.wurmunlimited.modcomm.PacketReader;
import org.gotti.wurmunlimited.modcomm.PacketWriter;

import com.wurmonline.server.players.Player;

public class ServerPackMod implements WurmServerMod, ModListener, Initable, Configurable, ServerStartedListener {

	private static final byte CMD_REFRESH = 0x01;

	private Map<String, Path> packs = new HashMap<>();

	private Logger logger = Logger.getLogger(ServerPackMod.class.getName());

	private int serverPort = 0;
	private String publicServerAddress = null;
	private String internalServerAddress = null;
	private int publicServerPort = 0;

	private PackServer packServer;
	private Channel channel;

	@Override
	public void init() {
		channel = ModComm.registerChannel("ago.serverpacks", new IChannelListener() {
			@Override
			public void onPlayerConnected(Player player) {
				if (packServer == null) {
					logger.log(Level.WARNING, "HTTP server did not start properly. No server packs will be delivered.");
					return;
				}
				try {
					URI uri = packServer.getUri();
					try (PacketWriter writer = new PacketWriter()) {
						writer.writeInt(packs.size());
						for (String packId : packs.keySet()) {
							URI packUri = uri.resolve(packId);
							writer.writeUTF(packId);
							writer.writeUTF(packUri.toString());
						}
						channel.sendMessage(player, writer.getBytes());
					}
				} catch (URISyntaxException | IOException e) {
					logger.log(Level.WARNING, e.getMessage(), e);
				}
			}

			@Override
			public void handleMessage(Player player, ByteBuffer message) {
				try (PacketReader reader = new PacketReader(message)) {
					byte cmd = reader.readByte();
					switch (cmd) {
					case CMD_REFRESH:
						sendModelRefresh(player);
						break;
					default:
						logger.log(Level.WARNING, String.format("Unknown channel command 0x%02x", 128 + cmd));
						break;
					}
				} catch (IOException e) {
					logger.log(Level.WARNING, e.getMessage(), e);
				}
			}
		});
	}

	private void sendModelRefresh(Player player) {
		try {
			player.createVisionArea();
		} catch (Exception e) {
			logger.log(Level.WARNING, e.getMessage(), e);
		}
	}

	@Override
	public void configure(Properties properties) {
		this.serverPort = Integer.parseInt(properties.getProperty("serverPort", Integer.toString(serverPort)));
		this.publicServerPort = Integer.parseInt(properties.getProperty("publicServerPort", Integer.toString(publicServerPort)));
		this.publicServerAddress = properties.getProperty("publicServerAddress");
		this.internalServerAddress = properties.getProperty("internalServerAddress");

		logger.info("serverPort: " + serverPort);
		logger.info("publicServerAddress: " + publicServerAddress);
		logger.info("publicServerPort: " + publicServerPort);
		logger.info("internalServerAddress: " + internalServerAddress);
	}

	@Override
	public void modInitialized(ModEntry entry) {
		if (entry == null || entry.getProperties() == null)
			return;

		String serverPacks = entry.getProperties().getProperty("serverPacks");
		if (serverPacks == null) {
			return;
		}

		String[] packs = serverPacks.split(",");

		for (String pack : packs) {
			try {
				Path packPath = Paths.get("mods").resolve(entry.getName()).resolve(Paths.get(pack));
				if (Files.isRegularFile(packPath)) {
					addPack(packPath);
				} else {
					logger.log(Level.WARNING, "Missing serverPack " + packPath);
				}
			} catch (IOException | NoSuchAlgorithmException e) {
				logger.log(Level.WARNING, "Error reading server pack", e);
			}
		}
	}

	private String getSha1Sum(Path packPath) throws IOException, NoSuchAlgorithmException {
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
		messageDigest.reset();

		try (InputStream is = Files.newInputStream(packPath)) {
			int n = 0;
			byte[] buffer = new byte[8192];
			while (n != -1) {
				n = is.read(buffer);
				if (n > 0) {
					messageDigest.update(buffer, 0, n);
				}
			}
		}
		byte[] digest = messageDigest.digest();
		return javax.xml.bind.DatatypeConverter.printHexBinary(digest);
	}

	private void addPack(Path packPath) throws NoSuchAlgorithmException, IOException {
		String sha1Sum = getSha1Sum(packPath);
		packs.put(sha1Sum, packPath);
		logger.info("Added pack " + sha1Sum + " for pack " + packPath);
	}

	@Override
	public void onServerStarted() {
		try {
			packServer = new PackServer(serverPort, publicServerAddress, publicServerPort, internalServerAddress) {

				@Override
				protected InputStream getPackStream(String packid) throws IOException {
					Path path = packs.get(packid);
					if (path != null) {
						return Files.newInputStream(path);
					}
					return null;
				}
			};
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}
}
