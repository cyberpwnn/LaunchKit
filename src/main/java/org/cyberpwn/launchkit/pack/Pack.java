package org.cyberpwn.launchkit.pack;

import java.util.ArrayList;
import java.util.List;

import org.cyberpwn.launchkit.util.UniversalType;

public class Pack
{
	private PackIdentity identity;
	private PackGame game;

	@UniversalType(PackProfile.class)
	private List<PackProfile> profiles;

	@UniversalType(PackInstall.class)
	private List<PackInstall> install;

	public Pack()
	{
		this(new PackIdentity(), new PackGame());
	}

	public Pack(PackIdentity identity, PackGame game)
	{
		this.identity = identity;
		this.game = game;
		profiles = new ArrayList<>();
		install = new ArrayList<>();
	}

	public PackIdentity getIdentity()
	{
		return identity;
	}

	public void setIdentity(PackIdentity identity)
	{
		this.identity = identity;
	}

	public PackGame getGame()
	{
		return game;
	}

	public void setGame(PackGame game)
	{
		this.game = game;
	}

	public List<PackProfile> getProfiles()
	{
		return profiles;
	}

	public void setProfiles(List<PackProfile> profiles)
	{
		this.profiles = profiles;
	}

	public List<PackInstall> getInstall()
	{
		return install;
	}

	public void setInstall(List<PackInstall> install)
	{
		this.install = install;
	}
}
