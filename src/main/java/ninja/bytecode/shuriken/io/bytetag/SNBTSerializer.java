package ninja.bytecode.shuriken.io.bytetag;

import ninja.bytecode.shuriken.io.bytetag.tag.NBTTag;

import java.io.IOException;
import java.io.Writer;

public class SNBTSerializer implements NBTRawStringSerializer<NBTTag<?>> {

	@Override
	public void toWriter(NBTTag<?> tag, Writer writer) throws IOException {
		SNBTWriter.write(tag, writer);
	}

	public void toWriter(NBTTag<?> tag, Writer writer, int maxDepth) throws IOException {
		SNBTWriter.write(tag, writer, maxDepth);
	}
}
