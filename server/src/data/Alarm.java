package data;

import java.util.GregorianCalendar;

public class Alarm extends GregorianCalendar {

	private static final long serialVersionUID = 376334195670122678L;

	public enum Mode {
		ONCE, DAILY, WEEKLY, MONTHLY, YEARLY
	};

	private Mode mode;

	public Alarm(GregorianCalendar date, Mode mode) {
		super();
		setTime(date.getTime());
		this.setMode(mode);
	}

	public boolean isTimeToExecute() {
		GregorianCalendar current = new GregorianCalendar();
		return !current.before(this);
	}

	public Mode getMode() {
		return mode;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}
}