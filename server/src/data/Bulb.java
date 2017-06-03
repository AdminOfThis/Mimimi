package data;

import java.io.Serializable;

public class Bulb implements Serializable {

	private static final long	serialVersionUID	= 87094281771026090L;
	private Address				address;
	private String				name;
	private State				state;

	public Bulb(Address address) {
		this.address = address;
	}

	public Bulb(Address address, String name) {
		this.address = address;
		this.name = name;
	}

	// GETTER AND SETTER
	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setState(State state) {
		this.state = state;
	}

	public State getState() {
		return state;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Bulb) {
			Bulb other = (Bulb) obj;
			if (this.getName() == null || other.getName() == null) { return false; }
			return (this.getName().equals(other.getName()) && this.getAddress().equals(other.getAddress()));
		}
		return false;
	}

}
