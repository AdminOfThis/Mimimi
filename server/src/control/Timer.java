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
		LOG.info("Starting Timer");
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
				case DAILY:
					alarm.add(GregorianCalendar.DAY_OF_YEAR, 1);
					break;
				case WEEKLY:
					alarm.add(GregorianCalendar.WEEK_OF_YEAR, 1);
					break;
				case MONTHLY:
					alarm.add(GregorianCalendar.MONTH, 1);
					break;
				case YEARLY:
					alarm.add(GregorianCalendar.YEAR, 1);
					break;
				default:
					break;
				}
			}
		}
		alarmList.removeAll(removeList);
		alarmList.addAll(addList);
		removeList.clear();
		addList.clear();
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
