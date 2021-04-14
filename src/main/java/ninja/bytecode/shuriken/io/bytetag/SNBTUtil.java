package ninja.bytecode.shuriken.io.bytetag;

import ninja.bytecode.shuriken.io.bytetag.tag.NBTTag;

import java.io.IOException;

public class SNBTUtil {

	public static String toSNBT(NBTTag<?> tag) throws IOException {
		return new SNBTSerializer().toString(tag);
	}

	public static NBTTag<?> fromSNBT(String string) throws IOException {
		return new SNBTDeserializer().fromString(string);
	}
}
