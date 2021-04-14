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

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * SNBTWriter creates an SNBT String.
 *
 * */
public final class SNBTWriter implements NBTRawMaxDepthIO {

	private static final Pattern NON_QUOTE_PATTERN = Pattern.compile("[a-zA-Z_.+\\-]+");

	private Writer writer;

	private SNBTWriter(Writer writer) {
		this.writer = writer;
	}

	public static void write(NBTTag<?> tag, Writer writer, int maxDepth) throws IOException {
		new SNBTWriter(writer).writeAnything(tag, maxDepth);
	}

	public static void write(NBTTag<?> tag, Writer writer) throws IOException {
		write(tag, writer, NBTTag.DEFAULT_MAX_DEPTH);
	}

	private void writeAnything(NBTTag<?> tag, int maxDepth) throws IOException {
		switch (tag.getID()) {
		case NBTEndTag.ID:
			//do nothing
			break;
		case NBTByteTag.ID:
			writer.append(Byte.toString(((NBTByteTag) tag).asByte())).write('b');
			break;
		case NBTShortTag.ID:
			writer.append(Short.toString(((NBTShortTag) tag).asShort())).write('s');
			break;
		case NBTIntTag.ID:
			writer.write(Integer.toString(((NBTIntTag) tag).asInt()));
			break;
		case NBTLongTag.ID:
			writer.append(Long.toString(((NBTLongTag) tag).asLong())).write('l');
			break;
		case NBTFloatTag.ID:
			writer.append(Float.toString(((NBTFloatTag) tag).asFloat())).write('f');
			break;
		case NBTDoubleTag.ID:
			writer.append(Double.toString(((NBTDoubleTag) tag).asDouble())).write('d');
			break;
		case NBTByteArrayTag.ID:
			writeArray(((NBTByteArrayTag) tag).getValue(), ((NBTByteArrayTag) tag).length(), "B");
			break;
		case NBTStringTag.ID:
			writer.write(escapeString(((NBTStringTag) tag).getValue()));
			break;
		case NBTListTag.ID:
			writer.write('[');
			for (int i = 0; i < ((NBTListTag<?>) tag).size(); i++) {
				writer.write(i == 0 ? "" : ",");
				writeAnything(((NBTListTag<?>) tag).get(i), decrementMaxDepth(maxDepth));
			}
			writer.write(']');
			break;
		case NBTCompoundTag.ID:
			writer.write('{');
			boolean first = true;
			for (Map.Entry<String, NBTTag<?>> entry : (NBTCompoundTag) tag) {
				writer.write(first ? "" : ",");
				writer.append(escapeString(entry.getKey())).write(':');
				writeAnything(entry.getValue(), decrementMaxDepth(maxDepth));
				first = false;
			}
			writer.write('}');
			break;
		case NBTIntArrayTag.ID:
			writeArray(((NBTIntArrayTag) tag).getValue(), ((NBTIntArrayTag) tag).length(), "I");
			break;
		case NBTLongArrayTag.ID:
			writeArray(((NBTLongArrayTag) tag).getValue(), ((NBTLongArrayTag) tag).length(), "L");
			break;
		default:
			throw new IOException("unknown tag with id \"" + tag.getID() + "\"");
		}
	}

	private void writeArray(Object array, int length, String prefix) throws IOException {
		writer.append('[').append(prefix).write(';');
		for (int i = 0; i < length; i++) {
			writer.append(i == 0 ? "" : ",").write(Array.get(array, i).toString());
		}
		writer.write(']');
	}

	public static String escapeString(String s) {
		if (!NON_QUOTE_PATTERN.matcher(s).matches()) {
			StringBuilder sb = new StringBuilder();
			sb.append('"');
			for (int i = 0; i < s.length(); i++) {
				char c = s.charAt(i);
				if (c == '\\' || c == '"') {
					sb.append('\\');
				}
				sb.append(c);
			}
			sb.append('"');
			return sb.toString();
		}
		return s;
	}
}
