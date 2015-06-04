package module.type;

import exception.CircularReference;

public interface Type {
	public String base();
	public String suffix();
	public int getSize() throws CircularReference;
}
