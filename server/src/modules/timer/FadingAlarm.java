package modules.timer;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;

import data.State;

public class FadingAlarm extends Alarm {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 3281807284152588529L;
	private static final Logger	LOG					= Logger.getLogger(FadingAlarm.class);
	private int					minutesFading;
	private int					startColor;
	private int					endColor;

	public FadingAlarm(GregorianCalendar date, int minutesFading, Mode mode, int startColor, int endColor) {
		super(mode, date);
		this.minutesFading = minutesFading;
		this.startColor = startColor;
		this.endColor = endColor;
	}

	@Override
	public boolean isTimeToExecute() {
		return System.currentTimeMillis() >= getDate().getTimeInMillis() - (60000 * minutesFading);
	}

	@Override
	boolean isDone() {
		return System.currentTimeMillis() >= getDate().getTimeInMillis();
	}

	@Override
	public ArrayList<String> buildCommands() {
//		long time = System.currentTimeMillis();
//		long endtime = getDate().getTimeInMillis();
//		long starttime = (endtime - (60000 * minutesFading));
//		// TODO
//		double range = endtime - starttime;
//		double done = time - starttime;
//		double percent = (done / range);
//		LOG.info("Fading Alarm to " + Math.round(percent * 100.0) + "% done");
//		double colorRange = (startColor - endColor);
//		int color = (int) Math.round(startColor + (colorRange * percent));
//		LightState state = new LightState(color);
//		state.setBrightness((int) Math.round(LightState.BRIGHTNESS_MAX * percent));
//		return state;
		return null;
	}

}
