package ninja.bytecode.shuriken.maven;

import java.net.MalformedURLException;
import java.net.URL;

import ninja.bytecode.shuriken.format.Form;

public class MavenArtifact
{
	private final String group;
	private final String artifact;
	private final String version;
	
	public MavenArtifact(String group, String artifact, String version)
	{
		this.group = group;
		this.artifact = artifact;
		this.version = version;
	}
	
	public static MavenArtifact from(String group, String artifact, String version)
	{
		return new MavenArtifact(group, artifact, version);
	}

	public static MavenArtifact from(String a)
	{
		String[] gav = a.split("\\Q:\\E");
		
		return from(gav[0], gav[1], gav[2]);
	}
	
	public String toString()
	{
		return Form.split(":", group, artifact, version);
	}

	public String getGroup()
	{
		return group;
	}

	public String getArtifact()
	{
		return artifact;
	}

	public String getVersion()
	{
		return version;
	}
	
	public URL toURL(MavenRepository repo)
	{
		String trimmer = repo.getRepository().trim();
		
		if(!trimmer.endsWith("/"))
		{
			trimmer += "/";
		}
		
		try
		{
			return new URL(trimmer + getGroup().replaceAll("\\Q.\\E", "/") +"/" + getArtifact() + "/" + getVersion() + "/" + getArtifact() + "-" + getVersion() + ".jar");
		}
		
		catch(MalformedURLException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
}
