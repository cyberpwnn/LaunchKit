package ninja.bytecode.shuriken.io.bytetag.tag;

public class NBTDoubleTag extends NBTNumberTag<Double> implements Comparable<NBTDoubleTag> {

	public static final byte ID = 6;
	public static final double ZERO_VALUE = 0.0D;

	public NBTDoubleTag() {
		super(ZERO_VALUE);
	}

	public NBTDoubleTag(double value) {
		super(value);
	}

	@Override
	public byte getID() {
		return ID;
	}

	public void setValue(double value) {
		super.setValue(value);
	}

	@Override
	public boolean equals(Object other) {
		return super.equals(other) && getValue().equals(((NBTDoubleTag) other).getValue());
	}

	@Override
	public int compareTo(NBTDoubleTag other) {
		return getValue().compareTo(other.getValue());
	}

	@Override
	public NBTDoubleTag clone() {
		return new NBTDoubleTag(getValue());
	}
}
