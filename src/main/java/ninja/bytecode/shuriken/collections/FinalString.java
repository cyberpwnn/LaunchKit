package ninja.bytecode.shuriken.collections;


/**
 * Represents a string that can be changed even if final
 *
 * @author cyberpwn
 */
public class FinalString extends Wrapper<String>
{
	public FinalString(String t)
	{
		super(t);
	}
}
