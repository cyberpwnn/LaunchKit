package ninja.bytecode.shuriken.io.bytetag.tag;

import java.util.Arrays;

public class NBTIntArrayTag extends NBTArrayTag<int[]> implements Comparable<NBTIntArrayTag> {

	public static final byte ID = 11;
	public static final int[] ZERO_VALUE = new int[0];

	public NBTIntArrayTag() {
		super(ZERO_VALUE);
	}

	public NBTIntArrayTag(int[] value) {
		super(value);
	}

	@Override
	public byte getID() {
		return ID;
	}

	@Override
	public boolean equals(Object other) {
		return super.equals(other) && Arrays.equals(getValue(), ((NBTIntArrayTag) other).getValue());
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(getValue());
	}

	@Override
	public int compareTo(NBTIntArrayTag other) {
		return Integer.compare(length(), other.length());
	}

	@Override
	public NBTIntArrayTag clone() {
		return new NBTIntArrayTag(Arrays.copyOf(getValue(), length()));
	}
}
