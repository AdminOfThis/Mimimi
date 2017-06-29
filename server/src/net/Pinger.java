package net;

import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.Logger;

public class Pinger extends Thread {

	private static final Logger	LOG		= Logger.getLogger(Pinger.class);
	private Server				server;
	private boolean				finish	= false;
	private long				delay	= 1000;

	public Pinger(Server server) {
		this.server = server;
		start();
	}

	@Override
	public void run() {
		LOG.info("Starting pinger");
		while (!finish) {
			ArrayList<ClientInterface> removeList = new ArrayList<>();
			for (ClientInterface client : server.getClients().keySet()) {
				try {
					long dateBefore = new Date().getTime();
					client.ping();
					long dateAfter = new Date().getTime();
					LOG.trace("Ping to " + server.getClients().get(client) + ": " + (dateAfter - dateBefore) + "ms");
				}
				catch (Exception e) {
					LOG.warn("Unable to ping Client " + server.getClients().get(client) + ", gets removed");
					removeList.add(client);
				}
			}
			for (ClientInterface i : removeList) {
				server.removeClient(i);
				try {
					Thread.sleep(delay);
				}
				catch (InterruptedException e) {
					LOG.error("Pinger is tired, but unable to sleep :( ", e);
				}
			}
		}
	}
}
