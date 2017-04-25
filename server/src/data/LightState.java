package data;

import java.io.Serializable;

public class LightState implements Serializable {

    public enum FIELD {
	COLOR, BRIGHTNESS, MODE
    };

    private static final long serialVersionUID = 8782198898112704078L;
    private Button button;
    private int color = -1;
    private int brightness = -1;
    private int mode = -1;
    private Address address = Address.MASTER_ADRESS;

    public LightState(Button button) {
	this.setButton(button);
    }

    public LightState(int color) {
	this.setButton(Button.COLOR_WHEEL);
	this.setColor(color);
    }

    public LightState(FIELD field, int value) {
	switch (field) {
	case BRIGHTNESS:
	    this.brightness = value;
	    this.button = Button.BRIGHTNESS;
	    break;
	case COLOR:
	    this.color = value;
	    this.button = Button.COLOR_WHEEL;
	    break;
	case MODE:
	    this.setMode(value);
	    this.setButton(Button.MODE);
	    break;
	default:
	    break;
	}
    }

    public void setAddress(Address adr) {
	this.address = adr;
    }

    public Address getAddress() {
	return address;
    }

    public Button getButton() {
	return button;
    }

    public void setButton(Button button) {
	this.button = button;
    }

    public int getColor() {
	return color;
    }

    public void setColor(int color) {
	if (color < 0) {
	    color += 256;
	}
	color = color % 256;
	this.color = color;
    }

    public int getBrightness() {
	return brightness;
    }

    public void setBrightness(int brightness) {
	this.brightness = brightness;
    }

    public int getMode() {
	return mode;
    }

    public void setMode(int mode) {
	this.mode = mode;
    }
}
