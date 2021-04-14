package ninja.bytecode.shuriken.plugin;

public class PluginException extends Exception
{
	private static final long serialVersionUID = -4051924660091690247L;

	public PluginException() {
		super();
	}

	public PluginException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public PluginException(String message, Throwable cause) {
		super(message, cause);
	}

	public PluginException(String message) {
		super(message);
	}

	public PluginException(Throwable cause) {
		super(cause);
	}
}
