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
import data.LightCommand;

public class Sender {

	private static final Logger		LOG				= Logger.getLogger(Sender.class);
	private static final String[]	ARGS			= new String[] { "sudo", "/bin/bash", "-c", "./openmilight \"B0 0D 33 C2 CA 0F A0\"" };
	private static final File		BULB_LIST_FILE	= new File("./bulbs.data");
	private static final int		SEQUENCE_RANGE	= 255;
	private static final int		SEQUENCE_SPACE	= 10;
	private static Sender			instance;
	private boolean					isLinux;
	private Thread					thread;
	private Queue<LightCommand>		queue			= new LinkedBlockingQueue<>();
	private Process					proc;
	private OutputStreamWriter		writer;
	private ArrayList<Bulb>			bulbList		= new ArrayList<>();

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
							ArrayList<String> rawCommands = queue.poll().buildCommands();
							for (String cmd : rawCommands) {
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

	public Address connectLightBulb(Address idToAddress) {

		LOG.info("SEND CONNECT");
		// Send signal for 3 seconds
		Button btn = null;
		switch (idToAddress.getGroup()) {
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
		// TODO
// Command state = new Command(btn);
// state.setAddress(idToAddress);
// queueFirst(state);
		return idToAddress;
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
