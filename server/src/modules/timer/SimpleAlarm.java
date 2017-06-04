package modules.timer;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import data.Bulb;
import data.State;

public class SimpleAlarm extends Alarm {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 4346675791191520129L;
	private State				command;

	public SimpleAlarm(GregorianCalendar date, Mode mode, State Command) {
		super(mode, date);
		this.command = Command;
	}

	@Override
	public boolean isTimeToExecute() {
		return System.currentTimeMillis() >= getDate().getTimeInMillis();
	}


	public void setState(State Command) {
		this.command = Command;
	}

	@Override
	public boolean isDone() {
		return System.currentTimeMillis() >= getDate().getTimeInMillis();
	}

	@Override
	public State getState() {
		return command;
	}

	@Override
	public ArrayList<Bulb> getBulbList() {
		return super.getBulbList();
	}


}
