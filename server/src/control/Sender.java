package control;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.rmi.RemoteException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import data.Button;

public class Sender {

	private static final Logger LOG = Logger.getLogger(Sender.class);
	private static final String REMOTE_ID = "0D 33"; // ID of the initial Remote for the Bulbs
	private static final String[] ARGS = new String[] { "sudo", "/bin/bash", "-c", "./openmilight \"B0 0D 33 C2 CA 0F A0\"" };
	private static final int SEQUENCE_RANGE = 255;
	private static final int SEQUENCE_SPACE = 10;
	private static Sender instance;
	private boolean isLinux;
	private Thread thread;
	private Queue<String> queue = new LinkedBlockingQueue<>();
	private int brightness = 0;
	private int color = 0;
	private Button button = Button.NONE;
	private int mode = 0x00;
	private int currentGroup = 0;
	private Process proc;
	private OutputStreamWriter writer;

	public static Sender getInstance() throws RemoteException {
		if (instance == null) {
			instance = new Sender();
		}
		return instance;
	}

	private Sender() throws RemoteException {
		String os = System.getProperty("os.name").toLowerCase();
		LOG.info("Detected OS: " + os);
		if (!os.contains("linux")) {
			LOG.warn("!!!=== Will only simulate Sender ===!!!");
			isLinux = false;
		} else {
			isLinux = true;
		}
		if (isLinux) {
			initProcess();
		}
	}

	public void send(String rawData) {
		queue.add(rawData);
		if (thread == null) {
			thread = new Thread(new Runnable() {

				@Override
				public void run() {
					while (true) {
						if (queue.peek() == null) {
							Thread.yield();
							continue;
						}
						if (queue.peek().length() != 8) {
							LOG.warn("Wrong DATA length, need 4Bytes and 3 spacings between: " + queue.peek());
							queue.poll();
						}
						try {
							// Could build process only once and hold it for livetime
							if ((proc == null || !proc.isAlive()) && isLinux) {
								LOG.info("Starting Sender");
								initProcess();
							}
							String cmd = "B" + Integer.toHexString(mode) + " " + REMOTE_ID + " " + queue.poll().trim();
							LOG.debug(cmd);
							if (isLinux()) {
								for (int run = 0; run < SEQUENCE_RANGE / SEQUENCE_SPACE; run++) {
									writer.write(cmd + " " + Integer.toHexString(SEQUENCE_SPACE * run) + "\n");
									writer.flush();
									Thread.sleep(50);
								}
							}
							LOG.trace("Sended, " + queue.size() + " queued");
						}
						catch (Exception e) {
							LOG.error("Sender crashed");
							LOG.debug(e);
							if (proc != null) {
								proc.destroyForcibly();
							}
						}
					}
				}
			});
			thread.start();
		}
	}

	private void initProcess() {
		LOG.debug("Building new process");
		ProcessBuilder pb = new ProcessBuilder(ARGS);
		try {
			proc = pb.start();
			OutputStream outStream = proc.getOutputStream();
			writer = new OutputStreamWriter(outStream);
		}
		catch (IOException e) {
			LOG.error(e);
		}
	}

	public void send() {
		String col = Integer.toHexString(color);
		if (col.length() < 2) {
			col = "0" + col;
		}
		String bright = Integer.toHexString(brightness + currentGroup);
		if (bright.length() < 2) {
			bright = "0" + bright;
		}
		String btn = Integer.toHexString(button.getCmd());
		if (btn.length() < 2) {
			btn = "0" + btn;
		}
		send(col + " " + bright + " " + btn);
	}

	public void sendButton(Button button) throws RemoteException {
		this.button = button;
		switch (button) {
		case ALL_ON:
			currentGroup = 0;
			break;
		case GROUP1_ON:
			currentGroup = 1;
			break;
		case GROUP2_ON:
			currentGroup = 2;
			break;
		case GROUP3_ON:
			currentGroup = 3;
			break;
		case GROUP4_ON:
			currentGroup = 4;
			break;
		default:
			break;
		}
		send();
	}

	public void sendColor(int color) throws RemoteException {
		this.color = (color + 0x1B) % 255;
		this.button = Button.COLOR_WHEEL;
		this.mode = 0x00;
		send();
	}

	public void sendBrightness(int brightness) throws RemoteException {
		this.brightness = parseToBrightness(brightness);
		this.button = Button.BRIGHTNESS;
		send();
	}

	private int parseToBrightness(int round) {
		int newRound = -(round - 16) * 8;
		if (newRound < 0) {
			newRound = newRound + 256;
		}
		return Math.abs(newRound);
	}

	public void modeNext() throws RemoteException {
		mode++;
		if (mode > 0x08) {
			mode = 0x00;
		}
		this.button = Button.MODE;
		send();
	}

	public void mode(int mode) throws RemoteException {
		if (mode <= 0x08) {
			this.mode = mode;
			this.button = Button.MODE;
			send();
		}
	}

	public boolean isLinux() {
		return isLinux;
	}
}
