package ninja.bytecode.shuriken.io.bytetag;

import java.io.IOException;

public class NBTRawParseException extends IOException {

	public NBTRawParseException(String msg) {
		super(msg);
	}

	public NBTRawParseException(String msg, String value, int index) {
		super(msg + " at: " + formatError(value, index));
	}

	private static String formatError(String value, int index) {
		StringBuilder builder = new StringBuilder();
		int i = Math.min(value.length(), index);
		if (i > 35) {
			builder.append("...");
		}
		builder.append(value, Math.max(0, i - 35), i);
		builder.append("<--[HERE]");
		return builder.toString();
	}
}
