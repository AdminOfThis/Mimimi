package net;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

import data.Alarm;
import data.Button;

public interface ServerInterface extends Remote {

    public int createClientID() throws RemoteException;

    public boolean clientRegister(int index) throws RemoteException;

    public void send(String data) throws RemoteException;

    public void sendButton(Button button) throws RemoteException;

    public void sendColor(int color) throws RemoteException;

    public void sendBrightness(int brightness) throws RemoteException;

    public void modeNext() throws RemoteException;

    public void mode(int mode) throws RemoteException;

    public void addNetworkDevice(String mac) throws RemoteException;

    public ArrayList<String> getNetworkDevices() throws RemoteException;

	/**
	 * Adds an alarm to the server which gets executed in Time by the Timer
	 * 
	 * @param alarm
	 * @throws RemoteException
	 */
	public void addAlarm(Alarm alarm) throws RemoteException;

	public void removeAlarm(Alarm alarm) throws RemoteException;

	public ArrayList<Alarm> getAlarmList() throws RemoteException;
}
