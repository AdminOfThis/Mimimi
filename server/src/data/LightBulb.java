package data;

import java.io.Serializable;

public class LightBulb implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -4105953527680859696L;
    private Address address;
    private String name;

    public LightBulb(String name, Address address) {
	this.name = name;
	this.address = address;
    }

    public String getName() {
	return name;
    }

    public Address getAddress() {
	return address;
    }

    @Override
    public String toString() {
	return name;
    }

}
