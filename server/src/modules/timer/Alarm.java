package modules.timer;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;

import data.Bulb;
import data.LightCommand;
import data.State;

public abstract class Alarm implements Serializable, LightCommand {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -1178811957835437175L;
	private static final Logger	LOG					= Logger.getLogger(Alarm.class);
	private static int			count				= 0;
	private int					id;

	public enum Mode {
		ONCE, HOUR, DAY, WEEK, MONTH, YEAR
	};

	private Mode				mode;
	private GregorianCalendar	date;
	private ArrayList<Bulb>		bulbList	= new ArrayList<>();

	public Alarm(Mode m, GregorianCalendar d) {
		this.mode = m;
		this.date = d;
		this.id = count;
		count++;
	}

	void setID(int id) {
		this.id = id;
	}

	static void setIndex(int index) {
		count = index;
	}

	public int getID() {
		return id;
	}

	public void rewindAlarm() {
		LOG.info("Rewinding alarm");
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

	@Override
	public String toString() {
		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		return formatter.format(getDate().getTime()) + " (" + getMode().toString() + ")";
	}

	abstract boolean isTimeToExecute();

	abstract boolean isDone();

	public Mode getMode() {
		return mode;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}

	public GregorianCalendar getDate() {
		return date;
	}

	public void setDate(GregorianCalendar cal) {
		this.date = cal;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Alarm) {
			Alarm other = (Alarm) obj;
			return this.getID() == other.getID();
		}
		return false;
	}

	public ArrayList<Bulb> getBulbList() {
		return new ArrayList<>(bulbList);
	}

	public void addBulb(Bulb bulb) {
		if (!bulbList.contains(bulb)) {
			bulbList.add(bulb);
		}
	}

	public void removeBulb(Bulb bulb) {
		bulbList.remove(bulb);
	}

	public void setAllBulbs(Collection<Bulb> bulbs) {
		bulbList.clear();
		bulbList.addAll(bulbs);
	}
}
