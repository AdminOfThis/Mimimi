package modules.timer;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import data.LightState;

public class SimpleAlarm extends Alarm {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 4346675791191520129L;
	private LightState			state;

	public SimpleAlarm(GregorianCalendar date, Mode mode, LightState state) {
		super(mode, date);
		this.state = state;
	}

	@Override
	public boolean isTimeToExecute() {
		return System.currentTimeMillis() >= getDate().getTimeInMillis();
	}


	public void setState(LightState state) {
		this.state = state;
	}

	@Override
	public boolean isDone() {
		return System.currentTimeMillis() >= getDate().getTimeInMillis();
	}

	@Override
	public LightState getCmd() {
		return state;
	}

	@Override
	public ArrayList<LightState> constructCMDs() {
		// TODO Auto-generated method stub
		return state;

	}
}
