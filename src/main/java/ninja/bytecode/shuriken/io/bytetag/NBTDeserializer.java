package ninja.bytecode.shuriken.io.bytetag;

import ninja.bytecode.shuriken.io.bytetag.tag.NBTTag;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

public class NBTDeserializer implements NBTRawDeserializer<NBTRawNamedTag> {

	private boolean compressed;

	public NBTDeserializer() {
		this(true);
	}

	public NBTDeserializer(boolean compressed) {
		this.compressed = compressed;
	}

	@Override
	public NBTRawNamedTag fromStream(InputStream stream) throws IOException {
		NBTInputStream nbtIn;
		if (compressed) {
			nbtIn = new NBTInputStream(new GZIPInputStream(stream));
		} else {
			nbtIn = new NBTInputStream(stream);
		}
		return nbtIn.readTag(NBTTag.DEFAULT_MAX_DEPTH);
	}
}
