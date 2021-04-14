package ninja.bytecode.shuriken.io.bytetag.tag;

import java.util.Arrays;

public class NBTLongArrayTag extends NBTArrayTag<long[]> implements Comparable<NBTLongArrayTag> {

	public static final byte ID = 12;
	public static final long[] ZERO_VALUE = new long[0];

	public NBTLongArrayTag() {
		super(ZERO_VALUE);
	}

	public NBTLongArrayTag(long[] value) {
		super(value);
	}

	@Override
	public byte getID() {
		return ID;
	}

	@Override
	public boolean equals(Object other) {
		return super.equals(other) && Arrays.equals(getValue(), ((NBTLongArrayTag) other).getValue());
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(getValue());
	}

	@Override
	public int compareTo(NBTLongArrayTag other) {
		return Integer.compare(length(), other.length());
	}

	@Override
	public NBTLongArrayTag clone() {
		return new NBTLongArrayTag(Arrays.copyOf(getValue(), length()));
	}
}
