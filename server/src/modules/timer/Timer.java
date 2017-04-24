package modules.timer;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;

import control.Sender;
import modules.Module;
import modules.timer.Alarm.Mode;
import net.Server;

public class Timer extends Module {

    private static final Logger LOG = Logger.getLogger(Timer.class);
    private ArrayList<Alarm> alarmList = new ArrayList<>();
    private ArrayList<Alarm> addList = new ArrayList<>();
    private ArrayList<Alarm> removeList = new ArrayList<>();

    public Timer(Server server) {
	super(server);
	GregorianCalendar cal = new GregorianCalendar();
	cal.add(GregorianCalendar.MINUTE, 1);
	this.addList.add(new FadingAlarm(cal, 2, Mode.ONCE, 0, 100));
	start();
    }

    @Override
    public void run() {
	LOG.info("Starting timer");
	while (!isFinish()) {
	    checkAlarms();
	    try {
		Thread.sleep(1000);
	    } catch (InterruptedException e) {
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
		LOG.info("Executing alarm: " + alarm.toString());
		Sender.getInstance().queueFirst(alarm.getCmd());
		if (alarm.isDone()) {
		    if (alarm.getMode() == Mode.ONCE) {
			removeList.add(alarm);
		    } else {
			alarm.rewindAlarm();
		    }
		}
		update = true;
	    }
	}
	if (addList.size() > 0 || removeList.size() > 0 || update) {
	    alarmList.removeAll(removeList);
	    alarmList.addAll(addList);
	    removeList.clear();
	    addList.clear();
	    getServer().updateAlarms();
	}
    }

    public void addAlarm(Alarm alarm) {
	addList.add(alarm);
	LOG.info("Alarm added: " + alarm.toString());
    }

    public void removeAlarm(Alarm alarm) {
	removeList.add(alarm);
	LOG.info("Alarm removed: " + alarm.toString());
    }

    public ArrayList<Alarm> getAlarmList() {
	return new ArrayList<>(alarmList);
    }
}
