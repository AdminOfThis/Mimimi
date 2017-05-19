package data;

import java.io.Serializable;

public class Address implements Serializable {

	private static final long	serialVersionUID	= 4622780137163075945L;
	private Remote				remote;
	private int					group;

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
		return "Remote: " + remote.toString() + ", Group: " + group;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Address) {
			Address other = (Address) obj;
			return this.getGroup() == other.getGroup() && this.getRemote().equals(other.getRemote());
		}
		return super.equals(obj);
	}

}
