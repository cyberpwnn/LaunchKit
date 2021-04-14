package ninja.bytecode.shuriken.io.bytetag;

/**
 * Exception indicating that the maximum (de-)serialization depth has been reached.
 */
@SuppressWarnings("serial")
public class NBTRawMaxDepthReachedException extends RuntimeException {

	public NBTRawMaxDepthReachedException(String msg) {
		super(msg);
	}
}
