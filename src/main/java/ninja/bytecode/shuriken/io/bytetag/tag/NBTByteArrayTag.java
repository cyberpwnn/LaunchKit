package ninja.bytecode.shuriken.io.bytetag.tag;

import java.util.Arrays;

public class NBTByteArrayTag extends NBTArrayTag<byte[]> implements Comparable<NBTByteArrayTag> {

	public static final byte ID = 7;
	public static final byte[] ZERO_VALUE = new byte[0];

	public NBTByteArrayTag() {
		super(ZERO_VALUE);
	}

	public NBTByteArrayTag(byte[] value) {
		super(value);
	}

	@Override
	public byte getID() {
		return ID;
	}

	@Override
	public boolean equals(Object other) {
		return super.equals(other) && Arrays.equals(getValue(), ((NBTByteArrayTag) other).getValue());
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(getValue());
	}

	@Override
	public int compareTo(NBTByteArrayTag other) {
		return Integer.compare(length(), other.length());
	}

	@Override
	public NBTByteArrayTag clone() {
		return new NBTByteArrayTag(Arrays.copyOf(getValue(), length()));
	}
}
