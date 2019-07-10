package org.cyberpwn.launchkit.pack;

public class PackIdentity
{
	private String name;
	private String description;
	private int version;

	public PackIdentity()
	{
		this("", "", 1);
	}

	public PackIdentity(String name, String description, int version)
	{
		this.name = name;
		this.description = description;
		this.version = version;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public int getVersion()
	{
		return version;
	}

	public void setVersion(int version)
	{
		this.version = version;
	}
}
