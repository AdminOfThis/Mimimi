package net.rest;

import java.io.IOException;
import java.net.URI;

import org.apache.log4j.Logger;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;


public class RestServer {

	private static final Logger	LOG			= Logger.getLogger(RestServer.class);
	private static final URI	BASE_URI	= URI.create("http://localhost:8080/rest/");

	private static RestServer	instance;
	private HttpServer			server;

	private RestServer() {
		LOG.info("REST-Server starting");
		try {
			final ResourceConfig resourceConfig = new ResourceConfig(RestClient.class);
			// TODO Reroute log to log4j
			server = GrizzlyHttpServerFactory.createHttpServer(BASE_URI, resourceConfig, false);
			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

				@Override
				public void run() {
					server.shutdownNow();
				}
			}));
			server.start();
			LOG.info("REST-Server started");
		}
		catch (IOException e) {
			LOG.error("Unable to start REST-Server", e);
			return;
		}
	}

	public static final RestServer getInstance() {
		if (instance == null) {
			instance = new RestServer();
		}
		return instance;
	}

	public void finish() {
		server.shutdown();
	}


}