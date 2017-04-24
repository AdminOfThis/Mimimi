package net;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

import data.Address;
import data.LightBulb;
import data.LightState;
import modules.timer.Alarm;
import modules.timer.SimpleAlarm;

public interface ServerInterface extends Remote {

    public int createClientID() throws RemoteException;

    public boolean clientRegister(int index) throws RemoteException;

    public void update(LightState state) throws RemoteException;

    public void addNetworkDevice(String mac) throws RemoteException;

    public ArrayList<String> getNetworkDevices() throws RemoteException;

    /**
     * Adds an alarm to the server which gets executed in Time by the Timer
     * 
     * @param alarm
     * @throws RemoteException
     */
    public void addAlarm(SimpleAlarm alarm) throws RemoteException;

    public void removeAlarm(Alarm alarm) throws RemoteException;

    public ArrayList<Alarm> getAlarmList() throws RemoteException;

    public Address connectLightBulb() throws RemoteException;

    public void addLightBulbToList(LightBulb bulb) throws RemoteException;

    public void removeLightFromBulbList(LightBulb bulb) throws RemoteException;

    public ArrayList<LightBulb> getBulbList() throws RemoteException;
}
