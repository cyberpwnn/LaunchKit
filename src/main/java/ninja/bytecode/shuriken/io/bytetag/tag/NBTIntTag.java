package ninja.bytecode.shuriken.io.bytetag.tag;

public class NBTIntTag extends NBTNumberTag<Integer> implements Comparable<NBTIntTag> {

	public static final byte ID = 3;
	public static final int ZERO_VALUE = 0;

	public NBTIntTag() {
		super(ZERO_VALUE);
	}

	public NBTIntTag(int value) {
		super(value);
	}

	@Override
	public byte getID() {
		return ID;
	}

	public void setValue(int value) {
		super.setValue(value);
	}

	@Override
	public boolean equals(Object other) {
		return super.equals(other) && asInt() == ((NBTIntTag) other).asInt();
	}

	@Override
	public int compareTo(NBTIntTag other) {
		return getValue().compareTo(other.getValue());
	}

	@Override
	public NBTIntTag clone() {
		return new NBTIntTag(getValue());
	}
}
