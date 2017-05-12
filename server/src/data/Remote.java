package data;

import java.io.Serializable;

public class Remote implements Serializable {

	private static final long	serialVersionUID	= 5632451551976912937L;
	private static final int	START_REMOTE		= 0x0D33;
	private int					id;

	@Override
	public String toString() {
		String string = String.format("%04X", (id + START_REMOTE));
		String part1 = string.substring(0, 2);
		String part2 = string.substring(2, 4);

		return part1 + " " + part2;
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
