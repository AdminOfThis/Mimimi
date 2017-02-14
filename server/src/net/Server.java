package net;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import control.Sender;
import data.Alarm;
import data.Button;
import data.Message;
import data.Message.MessageType;
import modules.NetworkScanner;
import modules.SerialScanner;
import modules.Timer;

/**
 * 
 * @author Florian
 *
 */
public class Server extends UnicastRemoteObject implements ServerInterface {

	private static final long serialVersionUID = 2772132741229895918L;
	private static final Logger LOG = Logger.getLogger(Server.class);
	private ConcurrentHashMap<ClientInterface, String> clients = new ConcurrentHashMap<>();
	private Sender sender;
	private NetworkScanner scanner;
	private Timer timer;

	public Server() throws RemoteException {
		LOG.info("Starting registry");
		new ServerFinder();
		try {
			LocateRegistry.createRegistry(1099);
			String address = ServerFinder.getIp();
			Naming.rebind("rmi://" + address + "/Mimimi", this);
			LOG.info("Detected IP: " + address);
		}
		catch (MalformedURLException | RemoteException e) {
			LOG.info("Unable to register RMI-Module");
			LOG.error(e);
		}
		sender = Sender.getInstance();
		if (sender.isLinux()) {
			scanner = new NetworkScanner(this);
		}
		new Pinger(this);
		new SerialScanner(this);
		timer = new Timer(this);
	}

	public void notifyClients(Message message) {
		LOG.info("Notifying clients with Message: " + message.getText());
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
	public void send(String data) throws RemoteException {
		sender.send();
	}

	@Override
	public void sendButton(Button button) throws RemoteException {
		sender.sendButton(button);
	}

	@Override
	public void sendColor(int color) throws RemoteException {
		sender.sendColor(color);
		Message message = new Message(MessageType.LIGHT_COLOR, "Color changed");
		message.setValue(color);
		notifyClients(message);
	}

	@Override
	public void sendBrightness(int brightness) throws RemoteException {
		sender.sendBrightness(brightness);
	}

	@Override
	public void modeNext() throws RemoteException {
		sender.modeNext();
	}

	@Override
	public void mode(int mode) throws RemoteException {
		sender.mode(mode);
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
	public boolean clientRegister(int index) throws RemoteException {
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

	public void removeClients(ArrayList<ClientInterface> removeList) {
		for (ClientInterface intf : removeList) {
			clients.remove(intf);
		}
	}

	public void executeAlarm(Alarm alarm) {
		try {
			sender.sendButton(Button.ALL_ON);
			sender.sendButton(Button.ALL_WHITE);
		}
		catch (RemoteException e) {
			LOG.error("Unable to send Alarm  to Sender", e);
		}
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
}
