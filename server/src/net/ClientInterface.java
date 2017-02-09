package net;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

import data.Alarm;
import data.Message;

public interface ClientInterface extends Remote {

	public void notify(Message message) throws RemoteException;

	public void ping() throws RemoteException;

	public void updateAlarms(ArrayList<Alarm> alarmList) throws RemoteException;
}