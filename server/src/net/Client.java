package net;

import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

import org.apache.log4j.Logger;

import data.Message;

public abstract class Client extends UnicastRemoteObject implements ClientInterface {

	public Client() throws RemoteException {
		super();
	}

	private static final long	serialVersionUID	= -7945728527925114454L;
	private static final Logger	LOG					= Logger.getLogger(Client.class);
	private ServerInterface		server;
	private int					id;
	private String				address;

	protected ServerInterface connect(String serverIP) {
		try {
			server = (ServerInterface) Naming.lookup("rmi://" + serverIP + "/Mimimi");
			id = server.createClientID();
			if (id >= 0) {
				address = InetAddress.getLocalHost().getHostAddress();
				LOG.info("Register Client @ " + address);
				try {
					LocateRegistry.createRegistry(1099);
				}
				catch (Exception e) {}
				Naming.rebind("rmi://" + address + "/" + id, this);
				boolean registerSuccess = server.registerClient(id);
				if (registerSuccess) {
					LOG.info("Registered successful");
				} else {
					LOG.error("Server didnt allow successful registration");
				}
			}
		}
		catch (Exception e) {
			LOG.error("Unable to register Client on Registry");
			e.printStackTrace();
		}
		return server;
	}

	public ServerInterface getServer() {
		return server;
	}

	@Override
	public abstract void notify(Message message) throws RemoteException;
}
