package data;

public enum Button {
	NONE(0x00), ALL_ON(0x01), ALL_OFF(0x02), GROUP1_ON(0x03), GROUP1_OFF(0x04), GROUP2_ON(0x05),
	GROUP2_OFF(0x06), GROUP3_ON(0x07), GROUP3_OFF(0x08), GROUP4_ON(0x09), GROUP4_OFF(0x0A),
	MODE_FASTER(0x0B), MODE_SLOWER(0x0C), MODE(0x0D), ALL_WHITE(0x11), GROUP1_WHITE(0x13),
	GROUP2_WHITE(0x15), GROUP3_WHITE(0x17), GROUP4_WHITE(0x19), BRIGHTNESS(0x0E), COLOR_WHEEL(0x0F);

	private int cmd;

	Button(int cmd) {
		this.cmd = cmd;
	}

	public int getCmd() {
		return cmd;
	}
}
