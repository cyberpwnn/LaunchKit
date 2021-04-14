package ninja.bytecode.shuriken.io.bytetag.tag;

public class NBTLongTag extends NBTNumberTag<Long> implements Comparable<NBTLongTag> {

	public static final byte ID = 4;
	public static final long ZERO_VALUE = 0L;

	public NBTLongTag() {
		super(ZERO_VALUE);
	}

	public NBTLongTag(long value) {
		super(value);
	}

	@Override
	public byte getID() {
		return ID;
	}

	public void setValue(long value) {
		super.setValue(value);
	}

	@Override
	public boolean equals(Object other) {
		return super.equals(other) && asLong() == ((NBTLongTag) other).asLong();
	}

	@Override
	public int compareTo(NBTLongTag other) {
		return getValue().compareTo(other.getValue());
	}

	@Override
	public NBTLongTag clone() {
		return new NBTLongTag(getValue());
	}
}
