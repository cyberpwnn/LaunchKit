package ninja.bytecode.shuriken.io.bytetag;

public interface NBTRawMaxDepthIO {

	default int decrementMaxDepth(int maxDepth) {
		if (maxDepth < 0) {
			throw new IllegalArgumentException("negative maximum depth is not allowed");
		} else if (maxDepth == 0) {
			throw new NBTRawMaxDepthReachedException("reached maximum depth of NBT structure");
		}
		return --maxDepth;
	}
}
