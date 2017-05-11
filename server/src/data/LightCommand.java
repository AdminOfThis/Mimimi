package data;

import java.util.ArrayList;

public interface LightCommand {

	// public ArrayList<String> buildCommands();

	public ArrayList<Address> getAddressList();

	public State getState();
}
