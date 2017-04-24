package modules.timer;

import java.util.GregorianCalendar;

import data.LightState;

public class FadingAlarm extends Alarm {
    /**
     * 
     */
    private static final long serialVersionUID = 3281807284152588529L;
    private int minutesFading;

    public FadingAlarm(GregorianCalendar date, int minutesFading, Mode mode) {
	super(mode, date);
	this.minutesFading = minutesFading;
    }

    @Override
    public boolean isTimeToExecute() {
	return System.currentTimeMillis() >= getDate().getTimeInMillis() - (60000 * minutesFading);
    }

    @Override
    boolean isDone() {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public LightState getCmd() {
	// TODO Auto-generated method stub
	return null;
    }
}
