package ninja.bytecode.shuriken.random;
@FunctionalInterface
public interface NoiseProvider
{
	public double noise(double x, double z);
}