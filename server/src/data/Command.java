package data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class Command implements Serializable, LightCommand {

	private State				state;
	private ArrayList<Address>	addressList	= new ArrayList<>();

	public Command(State state) {
		this.state = state;
	}

	public Command(State state, Address Address) {
		this.state = state;
		addressList.add(Address);
	}

	public Command(State state, Collection<Address> Addresss) {
		this.state = state;
		addressList.addAll(Addresss);
	}

	// GETTER AND SETTER
	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public ArrayList<Address> getAddressList() {
		return addressList;
	}

	public void setAddressList(ArrayList<Address> AddressList) {
		this.addressList = AddressList;
	}

	public void addAddress(Address address) {
		if (!addressList.contains(address)) {
			addressList.add(address);
		}
	}

	@Override
	public ArrayList<String> buildCommands() {
		ArrayList<String> resultList = new ArrayList<>();
		for (Address Address : addressList) {
			// TODO optimization if every Address in remote control is afffected
			resultList.add(buildCommand(Address));
		}
		return resultList;
	}

	private String buildCommand(Address address) {
		String command = "";
		// return "B" + Integer.toHexString(currentMode) + " " + addressString + " " + colorString + " " + brightString + " " + buttonString;

		command = buildCurrentMode(state);
		command += " " + buildRemoteCommand(address);
		command += " " + buildColorCommand(state);
		command += " " + buildBrighnessCommand(state, address);
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

	private String buildRemoteCommand(Address address) {
		return address.getRemote().toString();
	}

	public String buildColorCommand(State state) {
		return String.format("%02X", state.getColor());
	}

	private String buildBrighnessCommand(State state, Address address) {
		return String.format("%02X", parseToBrightness(state.getBrightness()) + address.getGroup());
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
