package control;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import data.Address;
import data.Bulb;
import data.Button;
import data.Command;
import data.LightCommand;
import data.State;

public class Sender {

	private static final Logger		LOG					= Logger.getLogger(Sender.class);
	private static final String[]	ARGS				= new String[] { "sudo", "/bin/bash", "-c", "./openmilight \"B0 0D 33 C2 CA 0F A0\"" };
	private static final File		BULB_LIST_FILE		= new File("./bulbs.data");
	private static final int		SEQUENCE_RANGE		= 255;
	private static final int		SEQUENCE_SPACE		= 10;
	private static Sender			instance;
	private boolean					isLinux;
	private Thread					thread;
	private Queue<LightCommand>		queue				= new LinkedBlockingQueue<>();
	private Process					proc;
	private OutputStreamWriter		writer;
	private ArrayList<Bulb>			bulbList			= new ArrayList<>();
	private int						currentColor		= 0;
	private int						currentBrightness	= 0;


	public static Sender getInstance() {
		if (instance == null) {
			instance = new Sender();
		}
		return instance;
	}

	@SuppressWarnings("unchecked")
	private Sender() {
		String os = System.getProperty("os.name").toLowerCase();
		LOG.info("Detected OS: " + os);
		if (!os.contains("linux")) {
			LOG.warn("!!!=== Will only simulate Sender ===!!!");
			isLinux = false;
		} else {
			isLinux = true;
		}
		bulbList = (ArrayList<Bulb>) FileUtil.loadList(BULB_LIST_FILE);
		boolean success = AddressManager.getInstance().setUsedAddresses(bulbList);
		if (!success) {
			LOG.warn("Unable to import used Bulb Addresses");
		}
		checkAndStartSendThread();
	}

	private void checkAndStartSendThread() {
		if (thread == null) {
			thread = new Thread(new Runnable() {

				@Override
				public void run() {
					while (true) {
						if (queue.peek() == null) {
							Thread.yield();
							continue;
						}
						try {
							if ((proc == null || !proc.isAlive()) && isLinux) {
								LOG.info("Starting Sender");
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
							LightCommand command = queue.poll();
							ArrayList<String> rawCommands = buildCommands(command);
							for (String cmd : rawCommands) {
								System.out.println(cmd);
								LOG.debug(cmd);
								if (isLinux()) {
									for (int run = 0; run < SEQUENCE_RANGE / SEQUENCE_SPACE; run++) {
										writer.write(cmd + " " + Integer.toHexString(SEQUENCE_SPACE * run) + "\n");
										writer.flush();
										Thread.sleep(50);
									}
								}
							}
							LOG.trace("Sended, " + queue.size() + " queued");
						}
						catch (Exception e) {
							LOG.error("Sender crashed", e);

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

	public void update(LightCommand state) {
		queue.add(state);
		checkAndStartSendThread();
	}

	public void queueFirst(ArrayList<LightCommand> states) {
		Queue<LightCommand> oldQueue = new LinkedBlockingQueue<>(queue);
		queue.clear();
		queue.addAll(states);
		queue.addAll(oldQueue);
	}

	public void queueFirst(LightCommand state) {
		Queue<LightCommand> oldQueue = new LinkedBlockingQueue<>(queue);
		queue.clear();
		queue.add(state);
		queue.addAll(oldQueue);
	}

	public boolean isLinux() {
		return isLinux;
	}

	public Address connectLightBulb() {
		Address address = AddressManager.getInstance().getNextFreeAddress();
		AddressManager.getInstance().blockAddress(address);
		LOG.info("SEND CONNECT");
		// Send signal for 3 seconds
		Button btn = null;
		switch (address.getGroup()) {
		case 1:
			btn = Button.GROUP1_ON;
			break;
		case 2:
			btn = Button.GROUP2_ON;
			break;
		case 3:
			btn = Button.GROUP3_ON;
			break;
		case 4:
			btn = Button.GROUP4_ON;
			break;
		}
		Command state = new Command(new State(btn), address);
		queueFirst(state);
		return address;
	}

	private ArrayList<String> buildCommands(LightCommand cmd) {
		ArrayList<String> result = new ArrayList<>();
		for (Address a : cmd.getAddressList()) {
			result.add(buildCommand(a, cmd.getState()));
		}
		return result;
	}

	private String buildCommand(Address address, State state) {
		String command = "";
		// return "B" + Integer.toHexString(currentMode) + " " + addressString + " " + colorString + " " + brightString + " " + buttonString;

		command = buildCurrentMode(state);
		command += " " + buildRemoteCommand(address);
		command += " " + buildColorCommand(state);
		command += " " + buildBrighnessCommand(state, address);
		command += " " + buildButtonCommand(state);
		return command;
	}

	public String buildCurrentMode(State state) {
		if (state.getButton() == Button.MODE) {
			return "B8";
		} else {
			return "B0";
		}
	}

	private String buildRemoteCommand(Address address) {
		return address.getRemote().toString();
	}

	public String buildColorCommand(State state) {
		int color = state.getColor();
		if (color < 0) {
			color = currentColor;
		} else {
			currentColor = color;
		}
		return String.format("%02X", color);
	}

	private String buildBrighnessCommand(State state, Address address) {
		int brightness = state.getBrightness();
		if (brightness < 0) {
			brightness = currentBrightness;
		} else {
			currentBrightness = brightness;
		}
		return String.format("%02X", parseToBrightness(brightness) + address.getGroup());
	}

	private String buildButtonCommand(State state) {
		return String.format("%02X", state.getButton().getCmd());
	}

	private int parseToBrightness(int round) {
		int newRound = -(round - 16) * 8;
		if (newRound < 0) {
			newRound = newRound + 256;
		}
		return Math.abs(newRound);
	}

	public ArrayList<Bulb> getBulbList() {
		return new ArrayList<>(bulbList);
	}

	public void addToBulbList(Bulb bulb) {
		if (!bulbList.contains(bulb)) {
			bulbList.add(bulb);
			FileUtil.saveList(bulbList, BULB_LIST_FILE);
		}
	}

	public void removeFromBulbList(Bulb bulb) {
		if (bulbList.contains(bulb)) {
			bulbList.remove(bulb);
			FileUtil.saveList(bulbList, BULB_LIST_FILE);
		}
	}


}
