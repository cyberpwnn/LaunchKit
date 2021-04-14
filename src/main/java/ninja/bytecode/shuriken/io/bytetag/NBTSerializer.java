package ninja.bytecode.shuriken.io.bytetag;

import ninja.bytecode.shuriken.io.bytetag.tag.NBTTag;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

public class NBTSerializer implements NBTRawSerializer<NBTRawNamedTag> {

	private boolean compressed;

	public NBTSerializer() {
		this(true);
	}

	public NBTSerializer(boolean compressed) {
		this.compressed = compressed;
	}

	@Override
	public void toStream(NBTRawNamedTag object, OutputStream out) throws IOException {
		NBTOutputStream nbtOut;
		if (compressed) {
			nbtOut = new NBTOutputStream(new GZIPOutputStream(out, true));
		} else {
			nbtOut = new NBTOutputStream(out);
		}
		nbtOut.writeTag(object, NBTTag.DEFAULT_MAX_DEPTH);
		nbtOut.flush();
	}
}
