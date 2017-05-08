package data;

import java.io.Serializable;

public class Address implements Serializable {

	private Remote	remote;
	private int		group;


	public Address(Remote remote, int group) {
		this.remote = remote;
		this.group = group;
	}

	// GETTER AND SETTER
	public Remote getRemote() {
		return remote;
	}

	public void setRemote(Remote remote) {
		this.remote = remote;
	}

	public int getGroup() {
		return group;
	}

	public void setGroup(int group) {
		this.group = group;
	}

	@Override
	public String toString() {
		return remote.toString() + " " + String.format("%02X", group);
	}
}
