package ninja.bytecode.shuriken.io.bytetag;

@FunctionalInterface
public interface NBTRawExceptionBiFunction<T, U, R, E extends Exception> {

	R accept(T t, U u) throws E;
}
