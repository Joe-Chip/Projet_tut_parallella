package module.type;

import exception.CircularReference;

public interface Type {
	public String getFullyQualifiedName();
	public String getJNI();
	public String fulfill(String indent, String name, String object, String id);
	public String extract(String indent, String name, String object, String id);
	public String prefix(String name);
	public String base();
	public String suffix();
	public int getSize() throws CircularReference;
}
