package org.cyberpwn.launchkit.pack;

import ninja.bytecode.shuriken.collections.GList;

public class PackInstall
{
	private String download;
	private String location;
	private String name;
	private String type;
	private String activation;
	private String hint;

	public PackInstall()
	{
		this("", "", "");
	}

	public PackInstall(String download, String location, String activation)
	{
		this.download = download;
		this.location = location;
		this.activation = activation;
		hint = "";
		name = "";
		type = "";
	}

	public boolean shouldActivate(String profile)
	{
		return !hasActivation() || getActivatedProfiles().contains(profile);
	}

	public GList<String> getActivatedProfiles()
	{
		GList<String> m = new GList<>();

		if(!hasActivation())
		{
			return m;
		}

		if(activation.trim().contains("\\Q|\\E"))
		{
			m.add(activation.split("\\Q|\\E"));
		}

		else
		{
			m.add(activation.trim());
		}

		return m;
	}

	public boolean hasActivation()
	{
		if(activation.trim().isEmpty())
		{
			return false;
		}

		return true;
	}

	public String getDownload()
	{
		return download;
	}

	public void setDownload(String download)
	{
		this.download = download;
	}

	public String getLocation()
	{
		return location;
	}

	public void setLocation(String location)
	{
		this.location = location;
	}

	public String getActivation()
	{
		return activation;
	}

	public void setActivation(String activation)
	{
		this.activation = activation;
	}

	public String getHint()
	{
		return hint;
	}

	public void setHint(String hint)
	{
		this.hint = hint;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}
}
