package data;

import java.util.ArrayList;

public interface LightCommand {

	public ArrayList<Bulb> getBulbList();

	public State getState();

}
