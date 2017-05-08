package data;

import java.io.Serializable;

public class Remote implements Serializable {

	private static final int	START_REMOTE	= 0x0D33;
	private static int			remoteCount		= 1;
	private int					id;

	@Override
	public String toString() {
		String string = String.format("%06X", id);
		string = string.substring(0, 1) + " " + string.substring(2, 3) + " " + string.substring(4, 5);
		return string;
	}


	public Remote() {
		this.id = remoteCount;
		remoteCount++;
	}

	public Remote(int id) {
		this.id = id;
	}

}
