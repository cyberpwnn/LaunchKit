package ninja.bytecode.shuriken.io.bytetag;

import ninja.bytecode.shuriken.io.bytetag.tag.NBTArrayTag;
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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public final class SNBTParser implements NBTRawMaxDepthIO {

	private static final Pattern
			FLOAT_LITERAL_PATTERN = Pattern.compile("^[-+]?(?:\\d+\\.?|\\d*\\.\\d+)(?:e[-+]?\\d+)?f$", Pattern.CASE_INSENSITIVE),
			DOUBLE_LITERAL_PATTERN = Pattern.compile("^[-+]?(?:\\d+\\.?|\\d*\\.\\d+)(?:e[-+]?\\d+)?d$", Pattern.CASE_INSENSITIVE),
			DOUBLE_LITERAL_NO_SUFFIX_PATTERN = Pattern.compile("^[-+]?(?:\\d+\\.|\\d*\\.\\d+)(?:e[-+]?\\d+)?$", Pattern.CASE_INSENSITIVE),
			BYTE_LITERAL_PATTERN = Pattern.compile("^[-+]?\\d+b$", Pattern.CASE_INSENSITIVE),
			SHORT_LITERAL_PATTERN = Pattern.compile("^[-+]?\\d+s$", Pattern.CASE_INSENSITIVE),
			INT_LITERAL_PATTERN = Pattern.compile("^[-+]?\\d+$", Pattern.CASE_INSENSITIVE),
			LONG_LITERAL_PATTERN = Pattern.compile("^[-+]?\\d+l$", Pattern.CASE_INSENSITIVE),
			NUMBER_PATTERN = Pattern.compile("^[-+]?\\d+$");

	private NBTRawStringPointer ptr;

	private SNBTParser(String string) {
		this.ptr = new NBTRawStringPointer(string);
	}

	public static NBTTag<?> parse(String string, int maxDepth) throws NBTRawParseException {
		SNBTParser parser = new SNBTParser(string);
		NBTTag<?> tag = parser.parseAnything(maxDepth);
		parser.ptr.skipWhitespace();
		if (parser.ptr.hasNext()) {
			throw parser.ptr.parseException("invalid characters after end of snbt");
		}
		return tag;
	}

	public static NBTTag<?> parse(String string) throws NBTRawParseException {
		return parse(string, NBTTag.DEFAULT_MAX_DEPTH);
	}

	private NBTTag<?> parseAnything(int maxDepth) throws NBTRawParseException {
		ptr.skipWhitespace();
		switch (ptr.currentChar()) {
			case '{':
				return parseCompoundTag(maxDepth);
			case '[':
				if (ptr.hasCharsLeft(2) && ptr.lookAhead(1) != '"' && ptr.lookAhead(2) == ';') {
					return parseNumArray();
				}
				return parseListTag(maxDepth);
		}
		return parseStringOrLiteral();
	}

	private NBTTag<?> parseStringOrLiteral() throws NBTRawParseException {
		ptr.skipWhitespace();
		if (ptr.currentChar() == '"') {
			return new NBTStringTag(ptr.parseQuotedString());
		}
		String s = ptr.parseSimpleString();
		if (s.isEmpty()) {
			throw new NBTRawParseException("expected non empty value");
		}
		if (FLOAT_LITERAL_PATTERN.matcher(s).matches()) {
			return new NBTFloatTag(Float.parseFloat(s.substring(0, s.length() - 1)));
		} else if (BYTE_LITERAL_PATTERN.matcher(s).matches()) {
			try {
				return new NBTByteTag(Byte.parseByte(s.substring(0, s.length() - 1)));
			} catch (NumberFormatException ex) {
				throw ptr.parseException("byte not in range: \"" + s.substring(0, s.length() - 1) + "\"");
			}
		} else if (SHORT_LITERAL_PATTERN.matcher(s).matches()) {
			try {
				return new NBTShortTag(Short.parseShort(s.substring(0, s.length() - 1)));
			} catch (NumberFormatException ex) {
				throw ptr.parseException("short not in range: \"" + s.substring(0, s.length() - 1) + "\"");
			}
		} else if (LONG_LITERAL_PATTERN.matcher(s).matches()) {
			try {
				return new NBTLongTag(Long.parseLong(s.substring(0, s.length() - 1)));
			} catch (NumberFormatException ex) {
				throw ptr.parseException("long not in range: \"" + s.substring(0, s.length() - 1) + "\"");
			}
		} else if (INT_LITERAL_PATTERN.matcher(s).matches()) {
			try {
				return new NBTIntTag(Integer.parseInt(s));
			} catch (NumberFormatException ex) {
				throw ptr.parseException("int not in range: \"" + s.substring(0, s.length() - 1) + "\"");
			}
		} else if (DOUBLE_LITERAL_PATTERN.matcher(s).matches()) {
			return new NBTDoubleTag(Double.parseDouble(s.substring(0, s.length() - 1)));
		} else if (DOUBLE_LITERAL_NO_SUFFIX_PATTERN.matcher(s).matches()) {
			return new NBTDoubleTag(Double.parseDouble(s));
		} else if ("true".equalsIgnoreCase(s)) {
			return new NBTByteTag(true);
		} else if ("false".equalsIgnoreCase(s)) {
			return new NBTByteTag(false);
		}
		return new NBTStringTag(s);
	}

	private NBTCompoundTag parseCompoundTag(int maxDepth) throws NBTRawParseException {
		ptr.expectChar('{');

		NBTCompoundTag compoundTag = new NBTCompoundTag();

		ptr.skipWhitespace();
		while (ptr.hasNext() && ptr.currentChar() != '}') {
			ptr.skipWhitespace();
			String key = ptr.currentChar() == '"' ? ptr.parseQuotedString() : ptr.parseSimpleString();
			if (key.isEmpty()) {
				throw new NBTRawParseException("empty keys are not allowed");
			}
			ptr.expectChar(':');

			compoundTag.put(key, parseAnything(decrementMaxDepth(maxDepth)));

			if (!ptr.nextArrayElement()) {
				break;
			}
		}
		ptr.expectChar('}');
		return compoundTag;
	}

	private NBTListTag<?> parseListTag(int maxDepth) throws NBTRawParseException {
		ptr.expectChar('[');
		ptr.skipWhitespace();
		NBTListTag<?> list = NBTListTag.createUnchecked(NBTEndTag.class);
		while (ptr.currentChar() != ']') {
			NBTTag<?> element = parseAnything(decrementMaxDepth(maxDepth));
			try {
				list.addUnchecked(element);
			} catch (IllegalArgumentException ex) {
				throw ptr.parseException(ex.getMessage());
			}
			if (!ptr.nextArrayElement()) {
				break;
			}
		}
		ptr.expectChar(']');
		return list;
	}

	private NBTArrayTag<?> parseNumArray() throws NBTRawParseException {
		ptr.expectChar('[');
		char arrayType = ptr.next();
		ptr.expectChar(';');
		ptr.skipWhitespace();
		switch (arrayType) {
			case 'B':
				return parseByteArrayTag();
			case 'I':
				return parseIntArrayTag();
			case 'L':
				return parseLongArrayTag();
		}
		throw new NBTRawParseException("invalid array type '" + arrayType + "'");
	}

	private NBTByteArrayTag parseByteArrayTag() throws NBTRawParseException {
		List<Byte> byteList = new ArrayList<>();
		while (ptr.currentChar() != ']') {
			String s = ptr.parseSimpleString();
			ptr.skipWhitespace();
			if (NUMBER_PATTERN.matcher(s).matches()) {
				try {
					byteList.add(Byte.parseByte(s));
				} catch (NumberFormatException ex) {
					throw ptr.parseException("byte not in range: \"" + s + "\"");
				}
			} else {
				throw ptr.parseException("invalid byte in ByteArrayTag: \"" + s + "\"");
			}
			if (!ptr.nextArrayElement()) {
				break;
			}
		}
		ptr.expectChar(']');
		byte[] bytes = new byte[byteList.size()];
		for (int i = 0; i < byteList.size(); i++) {
			bytes[i] = byteList.get(i);
		}
		return new NBTByteArrayTag(bytes);
	}

	private NBTIntArrayTag parseIntArrayTag() throws NBTRawParseException {
		List<Integer> intList = new ArrayList<>();
		while (ptr.currentChar() != ']') {
			String s = ptr.parseSimpleString();
			ptr.skipWhitespace();
			if (NUMBER_PATTERN.matcher(s).matches()) {
				try {
					intList.add(Integer.parseInt(s));
				} catch (NumberFormatException ex) {
					throw ptr.parseException("int not in range: \"" + s + "\"");
				}
			} else {
				throw ptr.parseException("invalid int in IntArrayTag: \"" + s + "\"");
			}
			if (!ptr.nextArrayElement()) {
				break;
			}
		}
		ptr.expectChar(']');
		return new NBTIntArrayTag(intList.stream().mapToInt(i -> i).toArray());
	}

	private NBTLongArrayTag parseLongArrayTag() throws NBTRawParseException {
		List<Long> longList = new ArrayList<>();
		while (ptr.currentChar() != ']') {
			String s = ptr.parseSimpleString();
			ptr.skipWhitespace();
			if (NUMBER_PATTERN.matcher(s).matches()) {
				try {
					longList.add(Long.parseLong(s));
				} catch (NumberFormatException ex) {
					throw ptr.parseException("long not in range: \"" + s + "\"");
				}
			} else {
				throw ptr.parseException("invalid long in LongArrayTag: \"" + s + "\"");
			}
			if (!ptr.nextArrayElement()) {
				break;
			}
		}
		ptr.expectChar(']');
		return new NBTLongArrayTag(longList.stream().mapToLong(l -> l).toArray());
	}
}
