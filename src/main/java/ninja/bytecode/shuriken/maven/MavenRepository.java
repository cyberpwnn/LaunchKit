package ninja.bytecode.shuriken.maven;

public class MavenRepository
{
	private final String repository;
	
	public MavenRepository(String repository)
	{
		this.repository = repository;
	}

	public String getRepository()
	{
		return repository;
	}
}
