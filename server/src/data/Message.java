package data;

import java.io.Serializable;

public class Message implements Serializable {

	public enum MessageType {
		LIGHT_COLOR, LIGHT_BRIGHTNESS, WIFI, ERROR
	};

	private static final long serialVersionUID = -2570247449726248976L;
	private String text;
	private MessageType type;
	private Object value;

	public Message(MessageType type, String message) {
		this.type = type;
		this.text = message;
	}

	public String getText() {
		return text;
	}

	public MessageType getType() {
		return type;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
}
