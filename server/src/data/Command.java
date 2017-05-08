package data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class Command implements Serializable, LightCommand {

	private State			state;
	private ArrayList<Bulb>	bulbList	= new ArrayList<>();


	public Command(State state) {
		this.state = state;
	}

	public Command(State state, Bulb bulb) {
		this.state = state;
		bulbList.add(bulb);
	}

	public Command(State state, Collection<Bulb> bulbs) {
		this.state = state;
		bulbList.addAll(bulbs);
	}


	// GETTER AND SETTER
	public State getState() {
		return state;
	}


	public void setState(State state) {
		this.state = state;
	}


	public ArrayList<Bulb> getBulbList() {
		return bulbList;
	}


	public void setBulbList(ArrayList<Bulb> bulbList) {
		this.bulbList = bulbList;
	}

	@Override
	public ArrayList<String> buildCommands() {
		ArrayList<String> resultList = new ArrayList<>();
		for (Bulb bulb : bulbList) {
			// TODO optimization if every bulb in remote control is afffected
			resultList.add(buildCommand(bulb));
		}
		return resultList;
	}


	private String buildCommand(Bulb bulb) {
		String command = "";
		// return "B" + Integer.toHexString(currentMode) + " " + addressString + " " + colorString + " " + brightString + " " + buttonString;

		command = buildCurrentMode(state);
		command += " " + buildRemoteCommand(bulb);
		command += " " + buildColorCommand(state);
		command += " " + buildBrighnessCommand(state, bulb.getAddress());
		command += " " + buildButtonCommand(state);
		return command;
	}

	public String buildCurrentMode(State state) {
		if (state.getButton() == Button.MODE) {
			return "B8";
		} else {
			return "B0";
		}
	}

	private String buildRemoteCommand(Bulb bulb) {
		return bulb.getAddress().getRemote().toString();
	}

	public String buildColorCommand(State state) {
		return String.format("%02X", state.getColor());
	}

	private String buildBrighnessCommand(State state, Address address) {
		return String.format("%02X", Integer.toHexString(parseToBrightness(state.getBrightness()) + address.getGroup()));
	}

	private String buildButtonCommand(State state) {
		return String.format("%02X", state.getButton().getCmd());
	}

	private int parseToBrightness(int round) {
		int newRound = -(round - 16) * 8;
		if (newRound < 0) {
			newRound = newRound + 256;
		}
		return Math.abs(newRound);
	}
}
