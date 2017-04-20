package data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LightBulb implements Serializable {

	private static final ArrayList<LightBulb> bulbList = new ArrayList<>();
	private int id;

	public static List<LightBulb> getBulbList() {
		return new ArrayList<>(bulbList);
	}


}
