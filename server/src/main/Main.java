package main;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import net.Server;
import net.ServerFinder;

public class Main {

	private static Logger LOG;
	public static final String LOG_CONFIG_FILE = "./log4j.ini";
	private static boolean successfulStart = true;

	public static void main(String[] args) {
		PropertyConfigurator.configure(LOG_CONFIG_FILE);
		LOG = Logger.getLogger(Main.class);
		try {
			LOG.info("Starting server");
			System.setProperty("java.rmi.server.hostname", ServerFinder.getIp());
			new Server();
			Thread.sleep(1000);
			if (successfulStart) {
				LOG.info("=== STARTED ===");
			}
		}
		catch (Exception e) {
			LOG.fatal("Unexpected error during startup", e);
			error();
		}
		if (!successfulStart) {
			LOG.info("A problem during startup occured, will exit now");
			System.exit(1);
		}
	}

	public static void error() {
		successfulStart = false;
	}
}
