package control;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import data.Button;
import data.Message;
import data.Message.MessageType;
import net.Server;

public class NetworkScanner extends Thread {

	private static final Logger LOG = Logger.getLogger(NetworkScanner.class);
	private static final String[] cmds = new String[] { "sudo", "arp-scan", "-l", };
	private static final ArrayList<String> macs = new ArrayList<>();
	private static final int MAX_FAILURES = 20;
	private static final long SLEEP = 3500;
	private Server server;
	private int failures = 0;

	public NetworkScanner(Server server) {
		this.server = server;
		macs.add("90:E7:C4:C9:6A:CF");
		start();
	}

	@Override
	public void run() {
		boolean deviceConnected = true;
		LOG.info("Starting network scanner");
		while (true) {
			ProcessBuilder pb = new ProcessBuilder(cmds);
			try {
				Process p = pb.start();
				p.waitFor();
				BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String line;
				boolean found = false;
				while ((line = reader.readLine()) != null) {
					for (String mac : macs) {
						if (line.toLowerCase().contains(mac.toLowerCase())) {
							found = true;
							break;
						}
					}
				}
				reader.close();
				if (found) {
					failures = 0;
					if (!deviceConnected) {
						connected();
						deviceConnected = true;
					}
				} else {
					if (failures < MAX_FAILURES) {
						failures++;
						LOG.debug("No device found (" + failures + "/" + MAX_FAILURES + ")");
					} else if (deviceConnected) {
						disconnected();
						deviceConnected = false;
					}
				}
				Thread.sleep(SLEEP);
			}
			catch (IOException | InterruptedException e) {
				LOG.error(e);
			}
		}
	}

	private void disconnected() throws RemoteException {
		LOG.info("Device disconnected");
		server.sendButton(Button.ALL_OFF);
		server.notifyClients(new Message(MessageType.WIFI, "No WiFi-Device disconnected"));
	}

	private void connected() throws RemoteException {
		LOG.info("Device connected");
		server.sendButton(Button.ALL_ON);
		server.notifyClients(new Message(MessageType.WIFI, "WiFi-Device connected"));
	}
}
