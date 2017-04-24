package net;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

import data.LightBulb;
import data.Message;
import modules.timer.Alarm;

public interface ClientInterface extends Remote {

    public void notify(Message message) throws RemoteException;

    public void ping() throws RemoteException;

    public void updateAlarms(ArrayList<Alarm> alarmList) throws RemoteException;

    public void updateBulbs(ArrayList<LightBulb> bulbList) throws RemoteException;
}