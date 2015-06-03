package ecom;

import exception.CircularReference;

public class Collection implements Type {
	private String type;
	private Type of;
	private int size;
	
	public Collection (String type, Type of, int size) {
		this.type = type;
		this.of = of;
		this.size = size;
	}
	
	public int getSize() throws CircularReference {
		return this.of.getSize()*this.size;
	}
	
	public String toString() {
		return this.base() + this.suffix();
	}

	@Override
	public String prefix() {
		return "";
	}
	
	@Override
	public String base() {
		return this.of.base();
	}

	@Override
	public String suffix() {
		return "[" + this.size + "]";
	}
}
