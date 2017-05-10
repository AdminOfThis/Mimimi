package data;

import java.io.Serializable;

public class Remote implements Serializable {

	private static final int	START_REMOTE	= 0x0D33;
	private int					id;

	@Override
	public String toString() {
		String string = String.format("%06X", id);
		string = string.substring(0, 1) + " " + string.substring(2, 3) + " " + string.substring(4, 5);
		return string;
	}

	public Remote(int id) {
		this.id = id;
	}

	public int getID() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Remote) {
			Remote other = (Remote) obj;
			return other.getID() == this.getID();
		}
		return false;
	}

}
