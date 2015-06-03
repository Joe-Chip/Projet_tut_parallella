package ecom;

import exception.CircularReference;

public interface Type {
	public String prefix();
	public String base();
	public String suffix();
	public int getSize() throws CircularReference;
}
