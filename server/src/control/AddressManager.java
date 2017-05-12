package control;

import java.io.File;
import java.util.ArrayList;

import data.Address;
import data.Bulb;
import data.Remote;

public class AddressManager {

	private static AddressManager	instance;

	private static final File		BULB_LIST		= new File("./bulbs.data");
	private ArrayList<Address>		freeAddresses	= new ArrayList<>();
	private ArrayList<Bulb>			usedBulbs		= new ArrayList<>();
	private ArrayList<Remote>		usedRemotes		= new ArrayList<>();

	@SuppressWarnings("unchecked")
	private AddressManager() {
		usedBulbs = (ArrayList<Bulb>) FileUtil.loadList(BULB_LIST);
	}

	public static AddressManager getInstance() {
		if (instance == null) {
			instance = new AddressManager();
		}
		return instance;
	}

	public Address getNextFreeAddress() {
		if (freeAddresses.size() >= 1) {
			Address a = freeAddresses.get(0);
			freeAddresses.remove(a);
			return a;
		} else {
			Remote r = createNewRemote();
			usedRemotes.add(r);
			for (int i = 1; i <= 4; i++) {
				Address a = new Address(r, i);
				freeAddresses.add(a);
			}
			return getNextFreeAddress();
		}
	}

	private Remote createNewRemote() {
		// TODO lückenfinder
		return new Remote(usedRemotes.size());
	}

	public boolean registerBulb(Bulb a) {
		if (usedBulbs.contains(a)) {
			return false;
		}
		usedBulbs.add(a);
		saveAddresses();
		return true;
	}

	private void saveAddresses() {
		ArrayList<String> clearAddresses = new ArrayList<>();
		for (Bulb b : usedBulbs) {
			clearAddresses
			        .add(b.getName() + ": " + b.getAddress().getRemote().toString() + " ," + b.getAddress().getGroup());
		}
		FileUtil.saveClearList(clearAddresses, new File("./addresses.txt"));
		FileUtil.saveList(usedBulbs, BULB_LIST);

	}

	public void freeAddress(Bulb a) {

		if (usedBulbs.contains(a)) {
			usedBulbs.remove(a);
			freeAddresses.add(a.getAddress());
			Remote remoteToCheck = a.getAddress().getRemote();
			boolean stillOneUsedAddress = false;
			for (Bulb bulb : usedBulbs) {
				if (bulb.getAddress().getRemote().equals(remoteToCheck)) {
					stillOneUsedAddress = true;
					break;
				}
			}
			if (!stillOneUsedAddress) {
				usedRemotes.remove(remoteToCheck);
			}
			saveAddresses();
		}
	}

	public ArrayList<Remote> getAllRemotes() {
		return new ArrayList<>(usedRemotes);
	}

	public ArrayList<Bulb> getUsedBulbs() {
		return new ArrayList<>(usedBulbs);
	}

}
