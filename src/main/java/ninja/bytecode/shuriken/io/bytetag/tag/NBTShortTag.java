package ninja.bytecode.shuriken.io.bytetag.tag;

public class NBTShortTag extends NBTNumberTag<Short> implements Comparable<NBTShortTag> {

	public static final byte ID = 2;
	public static final short ZERO_VALUE = 0;

	public NBTShortTag() {
		super(ZERO_VALUE);
	}

	public NBTShortTag(short value) {
		super(value);
	}

	@Override
	public byte getID() {
		return ID;
	}

	public void setValue(short value) {
		super.setValue(value);
	}

	@Override
	public boolean equals(Object other) {
		return super.equals(other) && asShort() == ((NBTShortTag) other).asShort();
	}

	@Override
	public int compareTo(NBTShortTag other) {
		return getValue().compareTo(other.getValue());
	}

	@Override
	public NBTShortTag clone() {
		return new NBTShortTag(getValue());
	}
}
