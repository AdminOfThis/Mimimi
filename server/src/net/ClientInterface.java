package net;

import java.rmi.Remote;
import java.rmi.RemoteException;

import data.Message;

public interface ClientInterface extends Remote {

	public void notify(Message message) throws RemoteException;

	public void ping() throws RemoteException;
}