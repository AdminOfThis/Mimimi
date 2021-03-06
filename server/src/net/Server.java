package net;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import control.AddressManager;
import control.Sender;
import data.Address;
import data.Bulb;
import data.Button;
import data.Command;
import data.LightCommand;
import data.Message;
import data.Remote;
import data.State;
import modules.SerialScanner;
import modules.WiFiScanner.WiFiScanner;
import modules.timer.Alarm;
import modules.timer.Timer;
import net.rest.RestServer;

/**
 * 
 * @author Florian
 *
 */
public class Server extends UnicastRemoteObject implements ServerInterface {

	private static final long							serialVersionUID	= 2772132741229895918L;
	private static final Logger							LOG					= Logger.getLogger(Server.class);
	private static Server								instance;
	private ConcurrentHashMap<ClientInterface, String>	clients				= new ConcurrentHashMap<>();
	private WiFiScanner									scanner;
	private Timer										timer;

	private static final String							UPDATE_CMD			= "java -jar updater.jar";

	public static Server getInstance() {
		if (instance == null) {
			try {
				instance = new Server();
			}
			catch (Exception e) {
				LOG.error("Unable to start server", e);
				return null;
			}
		}
		return instance;
	}

	public Server() throws Exception {
		LOG.info("Starting registry");
		new ServerFinder();
		try {
			LocateRegistry.createRegistry(1099);
			String address = ServerFinder.getIp();
			Naming.rebind("rmi://" + address + "/Mimimi", this);
			LOG.info("Detected IP: " + address);
		}
		catch (MalformedURLException | RemoteException e) {
			throw e;
		}
		if (Sender.getInstance().isLinux()) {
			scanner = new WiFiScanner(this);
		}
		new Pinger(this);
		new SerialScanner(this);
		timer = new Timer(this);
		RestServer.getInstance();
	}

	public void notifyClients(Message message) {
		LOG.info("Notifying clients: " + message.getText());
		for (ClientInterface client : clients.keySet()) {
			new Thread() {

				@Override
				public void run() {
					LOG.debug("Notify Client " + clients.get(client));
					try {
						client.notify(message);
					}
					catch (RemoteException e) {
						LOG.error("Unable to notify Client " + client);
					}
				};
			}.start();
		}
	}

	@Override
	public void update(LightCommand state) throws RemoteException {
		Sender.getInstance().update(state);
		// Message message = new Message(MessageType.LIGHT_COLOR, "Color changed");
		// message.setValue(color);
		// notifyClients(message);
	}

	@Override
	public int createClientID() throws RemoteException {
		String clientIP;
		try {
			clientIP = getClientHost();
		}
		catch (ServerNotActiveException e) {
			return -1;
		}
		LOG.info("Registering Client " + clientIP + ", # " + clients.size());
		return clients.size();
	}

	@Override
	public boolean registerClient(int index) throws RemoteException {
		try {
			String clientIP = getClientHost();
			ClientInterface clIntf = (ClientInterface) Naming.lookup("rmi://" + clientIP + "/" + index);
			clients.put(clIntf, clientIP);
			LOG.info("Client " + clientIP + " successful registered");
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public ConcurrentHashMap<ClientInterface, String> getClients() {
		return clients;
	}

	public void removeClient(ClientInterface c) {
		clients.remove(c);

	}

	@Override
	public void addAlarm(Alarm alarm) throws RemoteException {
		timer.addAlarm(alarm);
	}

	@Override
	public void removeAlarm(Alarm alarm) throws RemoteException {
		timer.removeAlarm(alarm);
	}

	@Override
	public ArrayList<Alarm> getAlarmList() throws RemoteException {
		return timer.getAlarmList();
	}

	@Override
	public void addNetworkDevice(String mac) throws RemoteException {
		scanner.addNetworkDevice(mac);
	}

	@Override
	public ArrayList<String> getNetworkDevices() throws RemoteException {
		return scanner.getNetworkDevices();
	}

	public void updateAlarms() {
		for (ClientInterface client : clients.keySet()) {
			new Thread() {

				@Override
				public void run() {
					LOG.debug("Updating Alarms on Clients" + clients.get(client));
					try {
						client.updateAlarms(timer.getAlarmList());
					}
					catch (RemoteException e) {
						LOG.error("Unable to notify Client " + client);
					}
				};
			}.start();
		}
	}

	public void updateBulbs() {
		for (ClientInterface client : clients.keySet()) {
			new Thread() {

				@Override
				public void run() {
					LOG.debug("Updating Lights on Clients" + clients.get(client));
					try {
						client.updateBulbs(AddressManager.getInstance().getUsedBulbs());
					}
					catch (RemoteException e) {
						LOG.error("Unable to notify Client " + client);
					}
				};
			}.start();
		}
	}

	@Override
	public Address connectBulb() throws RemoteException {
		return Sender.getInstance().connectLightBulb();
	}

	@Override
	public void addBulbToList(Bulb bulb) throws RemoteException {
		AddressManager.getInstance().registerBulb(bulb);
		updateBulbs();
	}

	@Override
	public void removeLightFromBulbList(Bulb bulb) throws RemoteException {
		Sender.getInstance().removeLightBulb(bulb);
		AddressManager.getInstance().freeAddress(bulb);
		updateBulbs();
	}

	@Override
	public ArrayList<Bulb> getBulbList() throws RemoteException {
		return AddressManager.getInstance().getUsedBulbs();
	}

	public void allBulbsOff() {
		Command cmd = new Command(new State(Button.ALL_OFF));
		for (Remote remote : AddressManager.getInstance().getAllRemotes()) {
			cmd.addBulb(new Bulb(remote.getAddress()));
		}
		Sender.getInstance().queueFirst(cmd);
	}

	@Override
	public void unregisterClient(int index) throws RemoteException {
		// TODO

	}

	@Override
	public void turnWiFiDetectorOn(boolean value) throws RemoteException {
		LOG.info("Activated WiFi-Detector: " + (value ? "ON" : "OFF"));
		scanner.setActive(value);
	}

	@Override
	public boolean sendData(String filename, byte[] data, int len) throws RemoteException {
		LOG.info("Receiving new File \"" + filename + "\"");
		try {
			File f = new File(filename);
			f.createNewFile();
			FileOutputStream out = new FileOutputStream(f, true);
			out.write(data, 0, len);
			out.flush();
			out.close();
			LOG.info("File sucessfully received");
		}
		catch (Exception e) {
			LOG.error("Unable to receive file", e);
			return false;
		}
		return true;
	}

	@Override
	public void updateServer() {
		try {
			LOG.info("Starting update process");
			Process p = Runtime.getRuntime().exec(UPDATE_CMD);
			LOG.info("Exiting now");
			System.exit(0);
		}
		catch (IOException e) {
			LOG.error("", e);
		}
	}
}
