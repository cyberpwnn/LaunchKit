package org.cyberpwn.launchkit.pack;

import ninja.bytecode.shuriken.collections.KList;

public class PackTweak
{
	private String file;
	private String find;
	private String replace;
	private boolean addIfMissing;
	private String activation;

	public PackTweak()
	{
		this("", "", "", "", true);
	}

	public PackTweak(String file, String find, String replace, String activation, boolean addIfMissing)
	{
		this.find = find;
		this.file = file;
		this.replace = replace;
		this.activation = activation;
		this.addIfMissing = addIfMissing;
	}

	public boolean shouldActivate(String profile)
	{
		return !hasActivation() || getActivatedProfiles().contains(profile);
	}

	public KList<String> getActivatedProfiles()
	{
		KList<String> m = new KList<>();

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

	public String getFile()
	{
		return file;
	}

	public void setFile(String file)
	{
		this.file = file;
	}

	public String getFind()
	{
		return find;
	}

	public void setFind(String find)
	{
		this.find = find;
	}

	public String getReplace()
	{
		return replace;
	}

	public void setReplace(String replace)
	{
		this.replace = replace;
	}

	public String getActivation()
	{
		return activation;
	}

	public void setActivation(String activation)
	{
		this.activation = activation;
	}

	public boolean isAddIfMissing()
	{
		return addIfMissing;
	}

	public void setAddIfMissing(boolean addIfMissing)
	{
		this.addIfMissing = addIfMissing;
	}
}
