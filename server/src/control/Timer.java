package control;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;

import data.Alarm;
import net.Server;

public class Timer extends Thread {

	private static final Logger LOG = Logger.getLogger(Timer.class);
	private ArrayList<Alarm> alarmList = new ArrayList<>();
	private ArrayList<Alarm> addList = new ArrayList<>();
	private ArrayList<Alarm> removeList = new ArrayList<>();
	private boolean finish = false;
	private Server server;

	public Timer(Server server) {
		this.server = server;
		start();
	}

	@Override
	public void run() {
		LOG.info("Starting timer");
		while (!finish) {
			checkAlarms();
			try {
				Thread.sleep(1000);
			}
			catch (InterruptedException e) {
				LOG.error("Timer thread sleep interrupted", e);
			}
		}
	}

	private void checkAlarms() {
		boolean update = false;
		for (Alarm alarm : alarmList) {
			// search for alarms to execute
			if (alarm.isTimeToExecute()) {
				// TODO
				LOG.info("Executing alarm");
				server.alarm(alarm);
				switch (alarm.getMode()) {
				case ONCE:
					removeList.add(alarm);
					break;
				case HOUR:
					alarm.add(GregorianCalendar.HOUR, 1);
					break;
				case DAY:
					alarm.add(GregorianCalendar.DAY_OF_YEAR, 1);
					break;
				case WEEK:
					alarm.add(GregorianCalendar.WEEK_OF_YEAR, 1);
					break;
				case MONTH:
					alarm.add(GregorianCalendar.MONTH, 1);
					break;
				case YEAR:
					alarm.add(GregorianCalendar.YEAR, 1);
					break;
				}
				update = true;
			}
		}
		if (addList.size() > 0 || removeList.size() > 0 || update) {
			alarmList.removeAll(removeList);
			alarmList.addAll(addList);
			removeList.clear();
			addList.clear();
			server.updateAlarms();
		}
	}

	public void addAlarm(Alarm alarm) {
		addList.add(alarm);
	}

	public void removeAlarm(Alarm alarm) {
		removeList.add(alarm);
	}

	public ArrayList<Alarm> getAlarmList() {
		return new ArrayList<>(alarmList);
	}
}
