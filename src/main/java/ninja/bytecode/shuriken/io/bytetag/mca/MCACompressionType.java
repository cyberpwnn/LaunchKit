package ninja.bytecode.shuriken.io.bytetag.mca;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.InflaterInputStream;

public enum MCACompressionType {

	NONE(0, t -> t, t -> t),
	GZIP(1, GZIPOutputStream::new, GZIPInputStream::new),
	ZLIB(2, DeflaterOutputStream::new, InflaterInputStream::new);

	private byte id;
	private MCAExceptionFunction<OutputStream, ? extends OutputStream, IOException> compressor;
	private MCAExceptionFunction<InputStream, ? extends InputStream, IOException> decompressor;

	MCACompressionType(int id,
				    MCAExceptionFunction<OutputStream, ? extends OutputStream, IOException> compressor,
				    MCAExceptionFunction<InputStream, ? extends InputStream, IOException> decompressor) {
		this.id = (byte) id;
		this.compressor = compressor;
		this.decompressor = decompressor;
	}

	public byte getID() {
		return id;
	}

	public OutputStream compress(OutputStream out) throws IOException {
		return compressor.accept(out);
	}

	public InputStream decompress(InputStream in) throws IOException {
		return decompressor.accept(in);
	}

	public static MCACompressionType getFromID(byte id) {
		for (MCACompressionType c : MCACompressionType.values()) {
			if (c.id == id) {
				return c;
			}
		}
		return null;
	}
}
