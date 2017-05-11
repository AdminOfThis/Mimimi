package modules.timer;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import data.Address;
import data.Command;
import data.State;

public class SimpleAlarm extends Alarm {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 4346675791191520129L;
	private Command				command;

	public SimpleAlarm(GregorianCalendar date, Mode mode, Command Command) {
		super(mode, date);
		this.command = Command;
	}

	@Override
	public boolean isTimeToExecute() {
		return System.currentTimeMillis() >= getDate().getTimeInMillis();
	}


	public void setCommand(Command Command) {
		this.command = Command;
	}

	@Override
	public boolean isDone() {
		return System.currentTimeMillis() >= getDate().getTimeInMillis();
	}

	@Override
	public State getState() {
		return command.getState();
	}

	@Override
	public ArrayList<Address> getAddressList() {
		return command.getAddressList();
	}
}
