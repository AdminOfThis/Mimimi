package modules.timer;

import java.io.File;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import control.FileUtil;
import control.Sender;
import modules.Module;
import modules.timer.Alarm.Mode;
import net.Server;

public class Timer extends Module {

    private static final Logger LOG = Logger.getLogger(Timer.class);
    private static final File ALARM_LIST_FILE = new File("./alarms.data");
    private ArrayList<Alarm> alarmList = new ArrayList<>();
    private ArrayList<Alarm> addList = new ArrayList<>();
    private ArrayList<Alarm> removeList = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public Timer(Server server) {
	super(server);
	ArrayList<Alarm> tempList =  (ArrayList<Alarm>) FileUtil.loadList(ALARM_LIST_FILE);
	
	for(int i=0;i<tempList.size();i++) {
	    tempList.get(i).setID(i);
	}
	alarmList =tempList;
	Alarm.setIndex(alarmList.size());
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
		LOG.info("Executing alarm");
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
	    FileUtil.saveList(alarmList, ALARM_LIST_FILE);
	    getServer().updateAlarms();
	}
    }

    public void addAlarm(Alarm alarm) {
	addList.add(alarm);
	LOG.debug("Alarm added: " + alarm.toString());
    }

    public void removeAlarm(Alarm alarm) {
	removeList.add(alarm);
	LOG.debug("Alarm removed: " + alarm.toString());
    }

    public ArrayList<Alarm> getAlarmList() {
	// return new ArrayList<>(alarmList);
	return alarmList;
    }
}
