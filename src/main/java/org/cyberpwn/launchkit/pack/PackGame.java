package org.cyberpwn.launchkit.pack;

public class PackGame
{
	private String forgeVersion;
	private String minecraftVersion;

	public PackGame()
	{
		this("no", "1.12.2");
	}

	public PackGame(String forgeVersion, String minecraftVersion)
	{
		this.forgeVersion = forgeVersion;
		this.minecraftVersion = minecraftVersion;
	}

	public String getForgeVersion()
	{
		return forgeVersion;
	}

	public void setForgeVersion(String forgeVersion)
	{
		this.forgeVersion = forgeVersion;
	}

	public String getMinecraftVersion()
	{
		return minecraftVersion;
	}

	public void setMinecraftVersion(String minecraftVersion)
	{
		this.minecraftVersion = minecraftVersion;
	}
}
