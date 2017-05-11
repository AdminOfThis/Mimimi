package main;

import java.rmi.RemoteException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import data.Bulb;
import data.Message;
import gui.controller.AlarmFrameController;
import gui.controller.ControlController;
import modules.timer.Alarm;
import net.Client;
import net.ServerFinder;
import net.ServerInterface;

public class GuiClient extends Client {

	private static final Logger	LOG	= Logger.getLogger(GuiClient.class);
	private static GuiClient	instance;
	private ServerInterface		server;

	public static GuiClient getInstance() {
		if (instance == null) {
			try {
				instance = new GuiClient();
			}
			catch (RemoteException e) {
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

	@Override
	public ServerInterface getServer() {
		return server;
	}

	@Override
	public void ping() throws RemoteException {
		LOG.trace("Ping received");

	}

	@Override
	public void updateAlarms(ArrayList<Alarm> alarmList) throws RemoteException {
		AlarmFrameController.getInstance().updateAlarms(alarmList);
	}

	@Override
	public void updateBulbs(ArrayList<Bulb> bulbList) throws RemoteException {
		if (ControlController.getInstance() != null) {
			ControlController.getInstance().updateBulbs(bulbList);
		}
	}

	@Override
	public void notify(Message message) throws RemoteException {
		ControlController.getInstance().notify(message);
	}

}
