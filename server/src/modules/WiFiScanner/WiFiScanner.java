package modules.WiFiScanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import data.Message;
import data.Message.MessageType;
import modules.Module;
import net.Server;

public class WiFiScanner extends Module {

	private static final Logger		LOG				= Logger.getLogger(WiFiScanner.class);
	private static final String[]	COMMANDS		= new String[] { "sudo", "arp-scan", "-l", };
	private static final int		MAX_FAILURES	= 30;
	private static final long		SLEEP			= 3500;
	/**/
	private ArrayList<String>		macs			= new ArrayList<>();
	private int						failures		= 0;

	public WiFiScanner(Server server) {
		super(server);
		macs.add("90:E7:C4:C9:6A:CF");
		start();
	}

	@Override
	public void run() {
		boolean deviceConnected = true;
		LOG.info("Starting network scanner");
		while (!isFinish()) {
			ProcessBuilder pb = new ProcessBuilder(COMMANDS);
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
					LOG.trace("Device found again");
					if (!deviceConnected) {
						connected();
						deviceConnected = true;
					}
				} else {
					if (failures < MAX_FAILURES) {
						failures++;
						LOG.trace("No device found (" + failures + "/" + MAX_FAILURES + ")");
					} else if (deviceConnected) {
						disconnected();
						deviceConnected = false;
					}
				}
				Thread.sleep(SLEEP);
			} catch (IOException | InterruptedException e) {
				LOG.error(e);
			}
		}
	}

	private void disconnected() throws RemoteException {
		LOG.info("Device disconnected");
		// getServer().update(new Command(Button.ALL_OFF));
		getServer().notifyClients(new Message(MessageType.WIFI, "No WiFi-Device connected"));
	}

	private void connected() throws RemoteException {
		LOG.info("Device connected");
		// getServer().update(new LightState(Button.ALL_ON));
		getServer().notifyClients(new Message(MessageType.WIFI, "WiFi-Device connected"));
	}

	public void addNetworkDevice(String mac) {
		if (!macs.contains(mac)) {
			macs.add(mac);
		}
	}

	public ArrayList<String> getNetworkDevices() {
		return new ArrayList<>(macs);
	}
}
