package data;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class Alarm extends GregorianCalendar {

	private static final long serialVersionUID = 376334195670122678L;

	public enum Mode {
		ONCE, HOUR, DAY, WEEK, MONTH, YEAR
	};

	private Mode mode;
	private LightState state;

	public Alarm(GregorianCalendar date, Mode mode, LightState state) {
		super();
		setTime(date.getTime());
		setMode(mode);
		setState(state);
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
		Date date = getTime();
		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		return formatter.format(date) + " (" + getMode().toString() + ")";
	}

	public LightState getState() {
		return state;
	}

	public void setState(LightState state) {
		this.state = state;
	}
}
