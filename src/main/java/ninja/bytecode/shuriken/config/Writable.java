package ninja.bytecode.shuriken.config;

import ninja.bytecode.shuriken.json.JSONObject;

public interface Writable
{
	public void fromJSON(JSONObject j);

	public JSONObject toJSON();
}
