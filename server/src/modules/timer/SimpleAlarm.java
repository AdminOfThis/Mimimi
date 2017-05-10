package modules.timer;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import data.Command;

public class SimpleAlarm extends Alarm {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 4346675791191520129L;
	private Command			command;

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
	public ArrayList<String> buildCommands() {
		// TODO Auto-generated method stub
		return command.buildCommands();
		
	}
}
