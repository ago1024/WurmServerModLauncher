package org.gotti.wurmunlimited.mods.httpserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.wurmonline.server.Server;

@SuppressWarnings("restriction")
public abstract class PackServer {
	
	private final Logger logger = Logger.getLogger(PackServer.class.getName());
	
	private final HttpServer httpServer;
	
	private final String publicServerAddress;
	private final int publicServerPort;
	
	private final ExecutorService executor;
	
	public PackServer(int port, String publicServerAddress, int publicServerPort, String internalServerAddress, int maxThreads) throws IOException {
		
		this.publicServerAddress = publicServerAddress;
		this.publicServerPort = publicServerPort;

		InetAddress addr;
		if (internalServerAddress == null)
			addr = InetAddress.getByAddress(Server.getInstance().getExternalIp());
		else
			addr = InetAddress.getByName(internalServerAddress);
		
		executor = new ThreadPoolExecutor(0, maxThreads, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());

		InetSocketAddress address = new InetSocketAddress(addr, port);
		httpServer = HttpServer.create(address, 0);
		httpServer.setExecutor(executor);
		httpServer.createContext("/", new HttpHandler() {
			
			@Override
			public void handle(HttpExchange paramHttpExchange) throws IOException {
				logger.info("Got request " + paramHttpExchange.getRequestURI().toString());
				
				try (InputStream stream = getStream(paramHttpExchange.getRequestURI().getPath())) {
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
				paramHttpExchange.sendResponseHeaders(404, -1);
			}
		});
		
		httpServer.start();
	}
	
	protected abstract InputStream getStream(String path) throws IOException;

	public URI getUri() throws URISyntaxException {
		String address = publicServerAddress;
		if (publicServerAddress == null)
			address = httpServer.getAddress().getHostString();
		
		int port = publicServerPort;
		if (port == 0)
			port = httpServer.getAddress().getPort();
		
		return new URI("http", null, address, port, "/", null, null);
	}
	
	public void stop() throws IOException {
		httpServer.stop(0);
		executor.shutdown();
		try {
			executor.awaitTermination(60, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			throw new IOException(e);
		}
	}

}
