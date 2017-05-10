package net;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

import data.Address;
import data.Bulb;
import data.LightCommand;
import modules.timer.Alarm;

public interface ServerInterface extends Remote {

	public int createClientID() throws RemoteException;

	public boolean clientRegister(int index) throws RemoteException;

	public void update(LightCommand state) throws RemoteException;

	public void addNetworkDevice(String mac) throws RemoteException;

	public ArrayList<String> getNetworkDevices() throws RemoteException;

	/**
	 * Adds an alarm to the server which gets executed in Time by the Timer
	 * 
	 * @param a
	 * @throws RemoteException
	 */
	public void addAlarm(Alarm a) throws RemoteException;

	public void removeAlarm(Alarm alarm) throws RemoteException;

	public ArrayList<Alarm> getAlarmList() throws RemoteException;

	public Address connectBulb() throws RemoteException;

	public void addBulbToList(Bulb bulb) throws RemoteException;

	public void removeLightFromBulbList(Bulb bulb) throws RemoteException;

	public ArrayList<Bulb> getBulbList() throws RemoteException;
}
