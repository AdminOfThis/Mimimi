package modules;

import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;

import org.apache.log4j.Logger;

import com.fazecast.jSerialComm.SerialPort;

import net.Server;

public class SerialScanner extends Module {

	private static final Logger LOG = Logger.getLogger(SerialScanner.class);
	private SerialPort port;
	private int color = 0;
	private int brightness = 0;

	public SerialScanner(Server server) {
		super(server);
		start();
	}

	@Override
	public void run() {
		LOG.info("Starting serial scanner");
		while (!isFinish()) {
			// Find right Serial Port
			if (port == null || !port.isOpen()) {
				for (SerialPort tempPort : SerialPort.getCommPorts()) {
					if (tempPort.getSystemPortName().toLowerCase().contains("usb")) {
						if (tempPort.openPort()) {
							this.port = tempPort;
							LOG.info("Opened Serial Port" + port.getDescriptivePortName());
						}
					}
				}
			} else {
				InputStream stream = port.getInputStream();
				char s;
				StringBuilder sb = new StringBuilder();
				try {
					while ((s = (char) stream.read()) != 0 && port.isOpen()) {
						if (s != '\n') {
							sb.append(s);
						} else {
							String cmd = sb.toString();
							if (!cmd.isEmpty()) {
								knobToData(cmd);
							}
							sb = new StringBuilder();
						}
					}
				}
				catch (IOException e) {
					LOG.info("Finished Serial Connection");
					port = null;
				}
			}
		}
	}

	private void knobToData(String input) {
		LOG.info("Serial: " + input);
		String[] cmd = input.split(":");
		try {
			int newColor = Integer.parseInt(cmd[0]);
			if (color != newColor) {
				getServer().sendColor(newColor);
				color = newColor;
			}
			int newBright = Integer.parseInt(cmd[1].trim());
			if (brightness != newBright) {
				getServer().sendBrightness(newBright);
				brightness = newBright;
			}
		}
		catch (NumberFormatException | RemoteException e) {
			LOG.error(e);
			e.printStackTrace();
		}
	}
}
