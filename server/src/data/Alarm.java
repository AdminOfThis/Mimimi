package data;

import java.util.GregorianCalendar;

public class Alarm extends GregorianCalendar {

	private static final long serialVersionUID = 376334195670122678L;

	public enum Mode {
		ONCE, HOUR, DAY, WEEK, MONTH, YEAR
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

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.get(GregorianCalendar.YEAR) + "";
	}
}
