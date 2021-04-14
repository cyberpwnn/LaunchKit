package ninja.bytecode.shuriken.io.bytetag;

import ninja.bytecode.shuriken.io.bytetag.tag.NBTTag;

public class NBTRawNamedTag {

	private String name;
	private NBTTag<?> tag;

	public NBTRawNamedTag(String name, NBTTag<?> tag) {
		this.name = name;
		this.tag = tag;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setTag(NBTTag<?> tag) {
		this.tag = tag;
	}

	public String getName() {
		return name;
	}

	public NBTTag<?> getTag() {
		return tag;
	}
}
