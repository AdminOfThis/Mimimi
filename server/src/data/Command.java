package data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class Command implements Serializable, LightCommand {

	private static final long	serialVersionUID	= 316855726837350042L;
	private State				state;
	private ArrayList<Bulb>		addressList			= new ArrayList<>();

	public Command(State state) {
		this.state = state;
	}

	public Command(State state, Bulb Bulb) {
		this.state = state;
		addressList.add(Bulb);
	}

	public Command(State state, Collection<Bulb> Bulbs) {
		this.state = state;
		addressList.addAll(Bulbs);
	}

	// GETTER AND SETTER
	@Override
	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	@Override
	public ArrayList<Bulb> getBulbList() {
		return addressList;
	}

	public void setBulbList(ArrayList<Bulb> BulbList) {
		this.addressList = BulbList;
	}

	public void addBulb(Bulb address) {
		if (!addressList.contains(address)) {
			addressList.add(address);
		}
	}

	public void addBulbs(Collection<Bulb> bulbs) {
		for (Bulb b : bulbs) {
			if (!addressList.contains(b)) {
				addressList.add(b);
			}
		}
	}


}
