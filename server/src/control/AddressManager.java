package control;

import java.io.File;
import java.util.ArrayList;

import data.Address;
import data.Bulb;
import data.Remote;

public class AddressManager {

	private static AddressManager	instance;

	private ArrayList<Address>		freeAddresses	= new ArrayList<>();
	private ArrayList<Address>		usedAddresses	= new ArrayList<>();
	private ArrayList<Remote>		usedRemotes		= new ArrayList<>();

	private AddressManager() {

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

	public boolean blockAddress(Address a) {
		if (usedAddresses.contains(a)) { return false; }
		usedAddresses.add(a);
		saveAddresses();
		return true;
	}

	private void saveAddresses() {
		ArrayList<String> clearAddresses = new ArrayList<>();
		for (Address a : usedAddresses) {
			clearAddresses.add(a.getRemote().toString() + " : " + a.getGroup());
		}
		FileUtil.saveClearList(clearAddresses, new File("./addresses.txt"));
	}

	public void freeAddress(Address a) {

		if (usedAddresses.contains(a)) {
			usedAddresses.remove(a);
			freeAddresses.add(a);
			Remote remoteToCheck = a.getRemote();
			boolean stillOneUsedAddress = false;
			for (Address a2 : usedAddresses) {
				if (a2.getRemote().equals(remoteToCheck)) {
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

	public boolean setUsedAddresses(ArrayList<Bulb> bulbList) {
		if (usedAddresses.isEmpty()) {
			for (Bulb b : bulbList) {
				usedAddresses.add(b.getAddress());
			}
			return true;
		}
		return false;
	}

	public ArrayList<Remote> getAllRemotes() {
		return new ArrayList<>(usedRemotes);
	}

}
