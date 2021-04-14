package ninja.bytecode.shuriken.io.bytetag.tag;

public class NBTByteTag extends NBTNumberTag<Byte> implements Comparable<NBTByteTag> {

	public static final byte ID = 1;
	public static final byte ZERO_VALUE = 0;

	public NBTByteTag() {
		super(ZERO_VALUE);
	}

	public NBTByteTag(byte value) {
		super(value);
	}

	public NBTByteTag(boolean value) {
		super((byte) (value ? 1 : 0));
	}

	@Override
	public byte getID() {
		return ID;
	}

	public boolean asBoolean() {
		return getValue() > 0;
	}

	public void setValue(byte value) {
		super.setValue(value);
	}

	@Override
	public boolean equals(Object other) {
		return super.equals(other) && asByte() == ((NBTByteTag) other).asByte();
	}

	@Override
	public int compareTo(NBTByteTag other) {
		return getValue().compareTo(other.getValue());
	}

	@Override
	public NBTByteTag clone() {
		return new NBTByteTag(getValue());
	}
}
