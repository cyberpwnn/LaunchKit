package org.cyberpwn.launchkit.pack;

import java.util.ArrayList;
import java.util.List;

import org.cyberpwn.launchkit.util.UniversalType;

public class PackProfile
{
	private String name;

	@UniversalType(String.class)
	private List<String> launchArgs;

	@UniversalType(String.class)
	private List<String> activation;

	public PackProfile()
	{
		this("");
	}

	public PackProfile(String name)
	{
		this.name = name;
		this.launchArgs = new ArrayList<>();
		this.activation = new ArrayList<>();
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public List<String> getLaunchArgs()
	{
		return launchArgs;
	}

	public void setLaunchArgs(List<String> launchArgs)
	{
		this.launchArgs = launchArgs;
	}

	public List<String> getActivation()
	{
		return activation;
	}

	public void setActivation(List<String> activation)
	{
		this.activation = activation;
	}
}
