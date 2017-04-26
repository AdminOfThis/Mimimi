package main;

import java.rmi.RemoteException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import data.LightBulb;
import data.Message;
import gui.controller.Control;
import modules.timer.Alarm;
import net.Client;
import net.ServerFinder;
import net.ServerInterface;

public class GuiClient extends Client {

    private static final Logger LOG = Logger.getLogger(GuiClient.class);
    private static GuiClient instance;
    private ServerInterface server;

    public static GuiClient getInstance() {
	if (instance == null) {
	    try {
		instance = new GuiClient();
	    } catch (RemoteException e) {
		LOG.error(e);
	    }
	}
	return instance;
    }

    public boolean isConnected() {
	return server != null;
    }

    private GuiClient() throws RemoteException {
	super();
	findAndConnect();
    }

    private void findAndConnect() {
	String ip = ServerFinder.findServer();
	if (ip == null) {
	    LOG.error("Unable to find Server");

	} else {
	    LOG.info("Server: " + ip);
	    server = connect(ip);
	}
    }

    public ServerInterface getServer() {
	return server;
    }

    @Override
    public void ping() throws RemoteException {
	LOG.trace("Ping received");

    }

    @Override
    public void updateAlarms(ArrayList<Alarm> alarmList) throws RemoteException {
	Control.getInstance().updateAlarms(alarmList);
    }

    @Override
    public void updateBulbs(ArrayList<LightBulb> bulbList) throws RemoteException {
	Control.getInstance().updateBulbs(bulbList);
    }

    @Override
    public void notify(Message message) throws RemoteException {
	Control.getInstance().notify(message);
    }

}
