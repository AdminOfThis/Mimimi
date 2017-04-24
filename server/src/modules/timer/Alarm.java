package modules.timer;

import java.io.Serializable;
import java.util.GregorianCalendar;

import data.LightState;

public abstract class Alarm implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -1178811957835437175L;

    public enum Mode {
	ONCE, HOUR, DAY, WEEK, MONTH, YEAR
    };

    private Mode mode;
    private GregorianCalendar date;

    public Alarm(Mode m, GregorianCalendar d) {
	this.mode = m;
	this.date = d;
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

    abstract boolean isTimeToExecute();

    abstract boolean isDone();

    abstract public LightState getCmd();

    public Mode getMode() {
	return mode;
    }

    public void setMode(Mode mode) {
	this.mode = mode;
    }

    public GregorianCalendar getDate() {
	return date;
    }

}
