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

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj) {
			return true;
		}
		if (!Path.class.isInstance(obj)) {
			return false;
		}
		return getFullPath().equals(Path.class.cast(obj).getFullPath());
	}

	@Override
	public int hashCode()
	{
		return getFullPath().hashCode();
	}

	@Override
	public String toString()
	{
		return getFullPath();
	}
}
