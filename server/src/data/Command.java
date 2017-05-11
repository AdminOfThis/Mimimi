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
	@Override
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

}
