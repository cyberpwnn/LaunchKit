package ninja.bytecode.shuriken.io.bytetag.tag;

public class NBTStringTag extends NBTTag<String> implements Comparable<NBTStringTag> {

	public static final byte ID = 8;
	public static final String ZERO_VALUE = "";

	public NBTStringTag() {
		super(ZERO_VALUE);
	}

	public NBTStringTag(String value) {
		super(value);
	}

	@Override
	public byte getID() {
		return ID;
	}

	@Override
	public String getValue() {
		return super.getValue();
	}

	@Override
	public void setValue(String value) {
		super.setValue(value);
	}

	@Override
	public String valueToString(int maxDepth) {
		return escapeString(getValue(), false);
	}

	@Override
	public boolean equals(Object other) {
		return super.equals(other) && getValue().equals(((NBTStringTag) other).getValue());
	}

	@Override
	public int compareTo(NBTStringTag o) {
		return getValue().compareTo(o.getValue());
	}

	@Override
	public NBTStringTag clone() {
		return new NBTStringTag(getValue());
	}
}
