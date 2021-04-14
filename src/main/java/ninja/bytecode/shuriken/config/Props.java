package ninja.bytecode.shuriken.config;

import java.io.File;
import java.util.UUID;

import com.google.gson.Gson;

import ninja.bytecode.shuriken.Shuriken;
import ninja.bytecode.shuriken.io.IO;
import ninja.bytecode.shuriken.json.JSONObject;
import ninja.bytecode.shuriken.collections.KMap;
import ninja.bytecode.shuriken.execution.J;
import ninja.bytecode.shuriken.logging.L;

public class Props extends KMap<String, Object>
{
	private static final long serialVersionUID = -7017325150027317798L;
	protected transient String key;

	public Props()
	{
		super();
	}

	public void commit()
	{
		if(key == null)
		{
			return;
		}

		try
		{
			IO.writeAll(getProperties(key), toJSON().toString(0));
			L.v("Saved " + key + " properties.");
		}

		catch(Throwable e)
		{
			L.f("Failed to save " + key + " properties.");
			L.ex(e);
		}
	}

	public void apply()
	{
		if(key == null)
		{
			return;
		}

		J.a(this::commit);
	}

	public static Props of(String key)
	{
		try
		{
			Props p = new Gson().fromJson(IO.readAll(getProperties(key)), Props.class);
			p.key = key;
			return p;
		}

		catch(Throwable e)
		{

		}

		Props p = new Props();
		p.key = key;
		return p;
	}

	protected static File getProperties(String key)
	{
		File f = new File(Shuriken.DIR + "/Property Caches/" + UUID.nameUUIDFromBytes(key.getBytes()) + ".properties");
		f.getParentFile().mkdirs();

		return f;
	}

	public static Props fromJSON(JSONObject j)
	{
		return new Gson().fromJson(j.toString(0), Props.class);
	}

	public JSONObject toJSON()
	{
		return new JSONObject(new Gson().toJson(this));
	}

	public Props crop(String prekey)
	{
		Props cropped = new Props();

		for(String i : k())
		{
			if(i.startsWith(prekey))
			{
				String k = i.replaceFirst("\\Q" + prekey + "\\E", "");
				cropped.put(k.startsWith(".") ? k.substring(1) : k, get(i));
			}
		}

		return cropped;
	}

	public Props put(Props cc)
	{
		super.putAll(cc);

		return this;
	}

	public Props put(Props cc, String prefix)
	{
		for(String i : cc.k())
		{
			put(prefix + i, cc.get(i));
		}

		return this;
	}

	public Props copy()
	{
		Props cc = new Props();
		cc.putAll(super.copy());

		return cc;
	}

	public boolean has(String key)
	{
		return containsKey(key);
	}

	public Integer getInt(String key)
	{
		return get(key);
	}

	public Long getLong(String key)
	{
		return get(key);
	}

	public Float getFloat(String key)
	{
		return get(key);
	}

	public Short getShort(String key)
	{
		return get(key);
	}

	public Boolean getBoolean(String key)
	{
		return get(key);
	}

	public Double getDouble(String key)
	{
		return get(key);
	}

	public String getString(String key)
	{
		return get(key);
	}

	public Byte getByte(String key)
	{
		return get(key);
	}

	public int getInt(String key, int defaultValue)
	{
		return containsKey(key) ? get(key) : defaultValue;
	}

	public long getLong(String key, long defaultValue)
	{
		Object o = get(key);

		if(o instanceof Double)
		{
			Double x = get(key);

			return containsKey(key) ? x.longValue() : defaultValue;
		}

		return containsKey(key) ? get(key) : defaultValue;
	}

	public float getFloat(String key, float defaultValue)
	{
		return containsKey(key) ? get(key) : defaultValue;
	}

	public short getShort(String key, short defaultValue)
	{
		return containsKey(key) ? get(key) : defaultValue;
	}

	public boolean getBoolean(String key, boolean defaultValue)
	{
		return containsKey(key) ? get(key) : defaultValue;
	}

	public double getDouble(String key, double defaultValue)
	{
		return containsKey(key) ? get(key) : defaultValue;
	}

	public String getString(String key, String defaultValue)
	{
		return containsKey(key) ? get(key) : defaultValue;
	}

	public byte getByte(String key, byte defaultValue)
	{
		return containsKey(key) ? get(key) : defaultValue;
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String key)
	{
		return (T) super.get(key);
	}
}
