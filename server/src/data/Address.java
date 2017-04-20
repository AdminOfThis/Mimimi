package data;

import java.io.Serializable;

public class Address implements Serializable {

	private static final int START_ADDRESS = 0x0D33;
	public static final Address MASTER_ADRESS = new Address(START_ADDRESS, 0x00);
	private int remote;
	private int group;


	public Address(int address, int group) {
		this.remote = address;
		this.group = group;
	}


	public static Address idToAddress(int id) {
		id++;
		int group = id % 4;
		int address = id - group;
		address = START_ADDRESS + (address / 4);
		return new Address(address, group);
	}

	public int getGroup() {
		return group;
	}

	public int getAddress() {
		return remote;
	}
}
