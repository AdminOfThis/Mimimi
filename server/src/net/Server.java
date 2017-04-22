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
import data.Address;
import data.Alarm;
import data.LightBulb;
import data.LightState;
import data.Message;
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
	} catch (MalformedURLException | RemoteException e) {
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
	LOG.info("Notifying clients: " + message.getText());
	for (ClientInterface client : clients.keySet()) {
	    new Thread() {

		@Override
		public void run() {
		    LOG.debug("Notify Client " + clients.get(client));
		    try {
			client.notify(message);
		    } catch (RemoteException e) {
			LOG.error("Unable to notify Client " + client);
		    }
		};
	    }.start();
	}
    }

    @Override
    public void update(LightState state) throws RemoteException {
	sender.update(state);
	// Message message = new Message(MessageType.LIGHT_COLOR, "Color changed");
	// message.setValue(color);
	// notifyClients(message);
    }

    @Override
    public int createClientID() throws RemoteException {
	String clientIP;
	try {
	    clientIP = getClientHost();
	} catch (ServerNotActiveException e) {
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
	} catch (Exception e) {
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
	sender.queueFirst(alarm.getState());
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
		    } catch (RemoteException e) {
			LOG.error("Unable to notify Client " + client);
		    }
		};
	    }.start();
	}
    }

    public void updateLights() {
	for (ClientInterface client : clients.keySet()) {
	    new Thread() {

		@Override
		public void run() {
		    LOG.debug("Updating Lights on Clients" + clients.get(client));
		    try {
			client.updateBulbs(sender.getBulbList());
		    } catch (RemoteException e) {
			LOG.error("Unable to notify Client " + client);
		    }
		};
	    }.start();
	}
    }

    @Override
    public Address connectLightBulb() throws RemoteException {
	return sender.connectLightBulb(Address.idToAddress(Sender.getInstance().getBulbList().size()));

    }

    @Override
    public void addLightBulbToList(LightBulb bulb) throws RemoteException {
	Sender.getInstance().addToBulbList(bulb);
	updateLights();
    }

    @Override
    public void removeLightFromBulbList(LightBulb bulb) throws RemoteException {
	Sender.getInstance().removeFromBulbList(bulb);
	updateLights();
    }

    @Override
    public ArrayList<LightBulb> getBulbList() throws RemoteException {
	return Sender.getInstance().getBulbList();

    }
}
