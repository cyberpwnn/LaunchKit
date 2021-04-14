package ninja.bytecode.shuriken.io.bytetag;

import ninja.bytecode.shuriken.io.bytetag.tag.NBTByteArrayTag;
import ninja.bytecode.shuriken.io.bytetag.tag.NBTByteTag;
import ninja.bytecode.shuriken.io.bytetag.tag.NBTCompoundTag;
import ninja.bytecode.shuriken.io.bytetag.tag.NBTDoubleTag;
import ninja.bytecode.shuriken.io.bytetag.tag.NBTEndTag;
import ninja.bytecode.shuriken.io.bytetag.tag.NBTFloatTag;
import ninja.bytecode.shuriken.io.bytetag.tag.NBTIntArrayTag;
import ninja.bytecode.shuriken.io.bytetag.tag.NBTIntTag;
import ninja.bytecode.shuriken.io.bytetag.tag.NBTListTag;
import ninja.bytecode.shuriken.io.bytetag.tag.NBTLongArrayTag;
import ninja.bytecode.shuriken.io.bytetag.tag.NBTLongTag;
import ninja.bytecode.shuriken.io.bytetag.tag.NBTShortTag;
import ninja.bytecode.shuriken.io.bytetag.tag.NBTStringTag;
import ninja.bytecode.shuriken.io.bytetag.tag.NBTTag;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class NBTInputStream extends DataInputStream implements NBTRawMaxDepthIO {

	private static Map<Byte, NBTRawExceptionBiFunction<NBTInputStream, Integer, ? extends NBTTag<?>, IOException>> readers = new HashMap<>();
	private static Map<Byte, Class<?>> idClassMapping = new HashMap<>();

	static {
		put(NBTEndTag.ID, (i, d) -> NBTEndTag.INSTANCE, NBTEndTag.class);
		put(NBTByteTag.ID, (i, d) -> readByte(i), NBTByteTag.class);
		put(NBTShortTag.ID, (i, d) -> readShort(i), NBTShortTag.class);
		put(NBTIntTag.ID, (i, d) -> readInt(i), NBTIntTag.class);
		put(NBTLongTag.ID, (i, d) -> readLong(i), NBTLongTag.class);
		put(NBTFloatTag.ID, (i, d) -> readFloat(i), NBTFloatTag.class);
		put(NBTDoubleTag.ID, (i, d) -> readDouble(i), NBTDoubleTag.class);
		put(NBTByteArrayTag.ID, (i, d) -> readByteArray(i), NBTByteArrayTag.class);
		put(NBTStringTag.ID, (i, d) -> readString(i), NBTStringTag.class);
		put(NBTListTag.ID, NBTInputStream::readListTag, NBTListTag.class);
		put(NBTCompoundTag.ID, NBTInputStream::readCompound, NBTCompoundTag.class);
		put(NBTIntArrayTag.ID, (i, d) -> readIntArray(i), NBTIntArrayTag.class);
		put(NBTLongArrayTag.ID, (i, d) -> readLongArray(i), NBTLongArrayTag.class);
	}

	private static void put(byte id, NBTRawExceptionBiFunction<NBTInputStream, Integer, ? extends NBTTag<?>, IOException> reader, Class<?> clazz) {
		readers.put(id, reader);
		idClassMapping.put(id, clazz);
	}

	public NBTInputStream(InputStream in) {
		super(in);
	}

	public NBTRawNamedTag readTag(int maxDepth) throws IOException {
		byte id = readByte();
		return new NBTRawNamedTag(readUTF(), readTag(id, maxDepth));
	}

	public NBTTag<?> readRawTag(int maxDepth) throws IOException {
		byte id = readByte();
		return readTag(id, maxDepth);
	}

	private NBTTag<?> readTag(byte type, int maxDepth) throws IOException {
		NBTRawExceptionBiFunction<NBTInputStream, Integer, ? extends NBTTag<?>, IOException> f;
		if ((f = readers.get(type)) == null) {
			throw new IOException("invalid tag id \"" + type + "\"");
		}
		return f.accept(this, maxDepth);
	}

	private static NBTByteTag readByte(NBTInputStream in) throws IOException {
		return new NBTByteTag(in.readByte());
	}

	private static NBTShortTag readShort(NBTInputStream in) throws IOException {
		return new NBTShortTag(in.readShort());
	}

	private static NBTIntTag readInt(NBTInputStream in) throws IOException {
		return new NBTIntTag(in.readInt());
	}

	private static NBTLongTag readLong(NBTInputStream in) throws IOException {
		return new NBTLongTag(in.readLong());
	}

	private static NBTFloatTag readFloat(NBTInputStream in) throws IOException {
		return new NBTFloatTag(in.readFloat());
	}

	private static NBTDoubleTag readDouble(NBTInputStream in) throws IOException {
		return new NBTDoubleTag(in.readDouble());
	}

	private static NBTStringTag readString(NBTInputStream in) throws IOException {
		return new NBTStringTag(in.readUTF());
	}

	private static NBTByteArrayTag readByteArray(NBTInputStream in) throws IOException {
		NBTByteArrayTag bat = new NBTByteArrayTag(new byte[in.readInt()]);
		in.readFully(bat.getValue());
		return bat;
	}

	private static NBTIntArrayTag readIntArray(NBTInputStream in) throws IOException {
		int l = in.readInt();
		int[] data = new int[l];
		NBTIntArrayTag iat = new NBTIntArrayTag(data);
		for (int i = 0; i < l; i++) {
			data[i] = in.readInt();
		}
		return iat;
	}

	private static NBTLongArrayTag readLongArray(NBTInputStream in) throws IOException {
		int l = in.readInt();
		long[] data = new long[l];
		NBTLongArrayTag iat = new NBTLongArrayTag(data);
		for (int i = 0; i < l; i++) {
			data[i] = in.readLong();
		}
		return iat;
	}

	private static NBTListTag<?> readListTag(NBTInputStream in, int maxDepth) throws IOException {
		byte listType = in.readByte();
		NBTListTag<?> list = NBTListTag.createUnchecked(idClassMapping.get(listType));
		int length = in.readInt();
		if (length < 0) {
			length = 0;
		}
		for (int i = 0; i < length; i++) {
			list.addUnchecked(in.readTag(listType, in.decrementMaxDepth(maxDepth)));
		}
		return list;
	}

	private static NBTCompoundTag readCompound(NBTInputStream in, int maxDepth) throws IOException {
		NBTCompoundTag comp = new NBTCompoundTag();
		for (int id = in.readByte() & 0xFF; id != 0; id = in.readByte() & 0xFF) {
			String key = in.readUTF();
			NBTTag<?> element = in.readTag((byte) id, in.decrementMaxDepth(maxDepth));
			comp.put(key, element);
		}
		return comp;
	}
}
