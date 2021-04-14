package ninja.bytecode.shuriken.io.bytetag.tag;

public class NBTFloatTag extends NBTNumberTag<Float> implements Comparable<NBTFloatTag> {

	public static final byte ID = 5;
	public static final float ZERO_VALUE = 0.0F;

	public NBTFloatTag() {
		super(ZERO_VALUE);
	}

	public NBTFloatTag(float value) {
		super(value);
	}

	@Override
	public byte getID() {
		return ID;
	}

	public void setValue(float value) {
		super.setValue(value);
	}

	@Override
	public boolean equals(Object other) {
		return super.equals(other) && getValue().equals(((NBTFloatTag) other).getValue());
	}

	@Override
	public int compareTo(NBTFloatTag other) {
		return getValue().compareTo(other.getValue());
	}

	@Override
	public NBTFloatTag clone() {
		return new NBTFloatTag(getValue());
	}
}
