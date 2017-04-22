package data;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class Alarm implements Serializable {

    private static final long serialVersionUID = 376334195670122678L;

    public enum Mode {
	ONCE, HOUR, DAY, WEEK, MONTH, YEAR
    };

    private GregorianCalendar date;
    private Mode mode;
    private LightState state;

    public Alarm(GregorianCalendar date, Mode mode, LightState state) {
	super();
	this.date = date;
	this.mode = mode;
	this.state = state;
    }

    public boolean isTimeToExecute() {
	GregorianCalendar current = new GregorianCalendar();
	return !current.before(date);
    }

    public Mode getMode() {
	return mode;
    }

    public void setMode(Mode mode) {
	this.mode = mode;
    }

    @Override
    public String toString() {
	SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
	return formatter.format(date) + " (" + getMode().toString() + ")";
    }

    public LightState getState() {
	return state;
    }

    public void setState(LightState state) {
	this.state = state;
    }

    public GregorianCalendar getDate() {
	return date;
    }

    public boolean isDone() {
	return System.currentTimeMillis() >= date.getTimeInMillis();
    }

    public void rewindAlarm() {
	switch (mode) {
	case HOUR:
	    date.add(GregorianCalendar.HOUR, 1);
	    break;
	case DAY:
	    date.add(GregorianCalendar.DAY_OF_YEAR, 1);
	    break;
	case WEEK:
	    date.add(GregorianCalendar.WEEK_OF_YEAR, 1);
	    break;
	case MONTH:
	    date.add(GregorianCalendar.MONTH, 1);
	    break;
	case YEAR:
	    date.add(GregorianCalendar.YEAR, 1);
	    break;
	default:
	    break;
	}
    }
}
