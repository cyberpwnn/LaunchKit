package ninja.bytecode.shuriken.io.bytetag.tag;

public final class NBTEndTag extends NBTTag<Void> {

	public static final byte ID = 0;
	public static final NBTEndTag INSTANCE = new NBTEndTag();

	private NBTEndTag() {
		super(null);
	}

	@Override
	public byte getID() {
		return ID;
	}

	@Override
	protected Void checkValue(Void value) {
		return value;
	}

	@Override
	public String valueToString(int maxDepth) {
		return "\"end\"";
	}

	@Override
	public NBTEndTag clone() {
		return INSTANCE;
	}
}
