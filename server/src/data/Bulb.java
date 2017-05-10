package data;

import java.io.Serializable;

public class Bulb implements Serializable {


	private Address				address;
	private String				name;


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

	@Override
	public String toString() {
		return name;
	}


}
