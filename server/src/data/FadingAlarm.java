package data;

import java.util.GregorianCalendar;

public class FadingAlarm extends Alarm {

    private static final long serialVersionUID = 376334195670122678L;
    private int minutesFading;

    public FadingAlarm(GregorianCalendar date, int minutesFading, Mode mode, LightState state) {
	super(date, mode, state);
	this.minutesFading = minutesFading;
    }

    @Override
    public boolean isTimeToExecute() {
	GregorianCalendar current = new GregorianCalendar();
	return current.getTimeInMillis() >= getDate().getTimeInMillis() - (60000 * minutesFading);
    }
}
