package data;

import java.io.Serializable;

public class State implements Serializable {

	public enum FIELD {
		COLOR, BRIGHTNESS, MODE
	};

	private static final long	serialVersionUID	= 8782198898112704078L;
	public static final int		COLOR_MAX			= 255;
	public static final int		BRIGHTNESS_MAX		= 25;
	private Button				button;
	private int					color				= -1;
	private int					brightness			= -1;
	private int					mode				= -1;


	public State(Button button) {
		this.setButton(button);
	}

	public State(int color) {
		this.setButton(Button.COLOR_WHEEL);
		this.setColor(color);
	}

	public State(FIELD field, int value) {
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

	// GETTER AND SETTER

	public void setColor(int color) {
		if (color < 0) {
			color += 256;
		}
		color = color % 256;
		this.color = color;
	}

	public int getColor() {
		return color;
	}

	public Button getButton() {
		return button;
	}

	public void setButton(Button button) {
		this.button = button;
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
