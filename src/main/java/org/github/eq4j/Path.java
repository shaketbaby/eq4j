package org.github.eq4j;

public class Path
{
	private String path;
	private final Path parent;

	Path(final String path, final Path parent)
	{
		this.path = path;
		this.parent = parent;
	}

	public String getFullPath() {
		return parent == null ? path : parent.getFullPath() + "." + path;
	}

	void setPath(final String path)
	{
		this.path = path;
	}
}
