package ninja.bytecode.shuriken.io.bytetag;

import ninja.bytecode.shuriken.io.bytetag.tag.NBTTag;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.stream.Collectors;

public class SNBTDeserializer implements NBTRawStringDeserializer<NBTTag<?>> {

	@Override
	public NBTTag<?> fromReader(Reader reader) throws IOException {
		return fromReader(reader, NBTTag.DEFAULT_MAX_DEPTH);
	}

	public NBTTag<?> fromReader(Reader reader, int maxDepth) throws IOException {
		BufferedReader bufferedReader;
		if (reader instanceof BufferedReader) {
			bufferedReader = (BufferedReader) reader;
		} else {
			bufferedReader = new BufferedReader(reader);
		}
		return SNBTParser.parse(bufferedReader.lines().collect(Collectors.joining()), maxDepth);
	}
}
