package ninja.bytecode.shuriken.random;

@FunctionalInterface
public interface NoiseInjector
{
	public double[] combine(double src, double value);
}
