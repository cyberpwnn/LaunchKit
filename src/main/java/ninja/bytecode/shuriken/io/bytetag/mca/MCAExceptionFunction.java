package ninja.bytecode.shuriken.io.bytetag.mca;

@FunctionalInterface
public interface MCAExceptionFunction<T, R, E extends Exception> {

	R accept(T t) throws E;
}
