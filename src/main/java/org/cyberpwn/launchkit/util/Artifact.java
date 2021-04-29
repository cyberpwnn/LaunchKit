package org.cyberpwn.launchkit.util;

import java.io.File;

public class Artifact
{
	private String groupId;
	private String artifactId;
	private String version;
	private String repo;

	public Artifact(String slug, String repo)
	{
		this.repo = repo;
		this.groupId = slug.split(":")[0];
		this.artifactId = slug.split(":")[1];
		this.version = slug.split(":")[2];
	}

	public String getGroupId()
	{
		return groupId;
	}

	public void setGroupId(String groupId)
	{
		this.groupId = groupId;
	}

	public String getArtifactId()
	{
		return artifactId;
	}

	public void setArtifactId(String artifactId)
	{
		this.artifactId = artifactId;
	}

	public String getVersion()
	{
		return version;
	}

	public void setVersion(String version)
	{
		this.version = version;
	}

	public String getRepo()
	{
		return repo;
	}

	public void setRepo(String repo)
	{
		this.repo = repo;
	}

	public File getPath(File f)
	{
		return new File(f, groupId.replaceAll("\\.", "/") + "/" + artifactId + "/" + version + "/" + artifactId + "-" + version + ".jar");
	}

	public String getFormalUrl()
	{
		return repo + groupId.replaceAll("\\.", "/") + "/" + artifactId + "/" + version + "/" + artifactId + "-" + version + ".jar";
	}

	@Override
	public String toString()
	{
		return groupId + ":" + artifactId + ":" + version + " @ " + getFormalUrl();
	}
}