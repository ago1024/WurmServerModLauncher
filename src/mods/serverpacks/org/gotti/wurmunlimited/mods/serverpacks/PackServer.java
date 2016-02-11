package org.gotti.wurmunlimited.mods.serverpacks;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.wurmonline.server.Server;

public abstract class PackServer {
	
	private Logger logger = Logger.getLogger(PackServer.class.getName());
	
	private HttpServer httpServer;
	
	private final String publicServerAddress;
	private final int publicServerPort;
	
	public PackServer(int port, String publicServerAddress, int publicServerPort, String internalServerAddress) throws IOException {
		
		this.publicServerAddress = publicServerAddress;
		this.publicServerPort = publicServerPort;

		InetAddress addr;
		if (internalServerAddress == null)
			addr = InetAddress.getByAddress(Server.getInstance().getExternalIp());
		else
			addr = InetAddress.getByName(internalServerAddress);

		InetSocketAddress address = new InetSocketAddress(addr, port);
		httpServer = HttpServer.create(address, 0);
		httpServer.createContext("/packs/", new HttpHandler() {
			
			@Override
			public void handle(HttpExchange paramHttpExchange) throws IOException {
				logger.info("Got request " + paramHttpExchange.getRequestURI().toString());
				
				Matcher matcher = Pattern.compile("^.*/packs/([^/]*)$").matcher(paramHttpExchange.getRequestURI().getPath());
				if (matcher.matches()) {
					try (InputStream stream = getPackStream(matcher.group(1))) {
						if (stream != null) {
							paramHttpExchange.getResponseHeaders().add("Cache-control", "max-age=31556926");
							paramHttpExchange.sendResponseHeaders(200, 0);
							try (OutputStream os = paramHttpExchange.getResponseBody()) {
								int n = 0;
								byte[] buffer = new byte[8192];
								while (n != -1) {
									n = stream.read(buffer);
									if (n > 0) {
										os.write(buffer, 0, n);
									}
								}
							}
							return;
						}
					} catch (IOException e) {
						logger.log(Level.SEVERE, e.getMessage(), e);
					}
				}
				paramHttpExchange.sendResponseHeaders(404, -1);
			}
		});
		
		httpServer.start();
	}
	
	protected abstract InputStream getPackStream(String packid) throws IOException;

	public URI getUri() throws URISyntaxException {
		String address = publicServerAddress;
		if (publicServerAddress == null)
			address = httpServer.getAddress().getHostString();
		
		int port = publicServerPort;
		if (port == 0)
			port = httpServer.getAddress().getPort();
		
		return new URI("http", null, address, port, "/packs/", null, null);
	}
	

}
