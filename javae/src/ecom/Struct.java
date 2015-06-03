package ecom;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import exception.CircularReference;
import exception.MultipleDeclaration;

public class Struct implements Type {
	static public HashMap<String,Struct> objects;
	static private HashMap<String,Set<String>> references;
	static {
		objects = new HashMap<String,Struct>();
		references = new HashMap<String,Set<String>>();
	}
	
	public static Struct namedStruct(String name) {
		Struct s = objects.get(name);
		if (s == null) {
			s = new Struct(name);
			objects.put(name, s);
		}
		return s;
	}
	
	private String name;
	private HashMap<String, Type> fields;
	private String header;
	private int size;
	
	private Struct(String name) {
		this.name = name;
		this.fields = new HashMap<String, Type>();
		this.header = null;
		this.size = -1;
	}
	
	public void addField(String name, Type type) throws MultipleDeclaration {
		Type old = this.fields.get(name);
		if (old != null)
			throw new MultipleDeclaration(name, this.name);
		else this.fields.put(name, type);
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getSize() throws CircularReference {
		if (references.get(this.name) == null)
			this.linearize();
		return this.size;
	}
	
	private void linearize() throws CircularReference {
		Set<String> reference = new HashSet<String>();
		reference.add(this.name);
		references.put(this.name, reference);
		int size = 0;
		StringBuilder header = new StringBuilder("struct " + this.name + " {\n");
		for (Entry<String, Type> e : fields.entrySet()) {
			String type = e.getValue().base();
			Set<String> set = references.get(type);
			if (set != null)
				if (set.contains(this.name))
					throw new CircularReference(this.name, type);
			reference.add(type);
			size += e.getValue().getSize();
			header.append("\t" + e.getValue().base() + " " + e.getKey() + e.getValue().suffix() + ";\n");
		}
		header.append("};");
		this.size = size;
		this.header = header.toString();
	}
	
	public String toString() {
		return this.prefix() + this.name;
	}
	
	public String getHeader() throws CircularReference {
		if (references.get(this.name) == null)
			this.linearize();
		return this.header;
	}

	@Override
	public String prefix() {
		return "struct " + this.name + ";\ntypedef struct " + this.name + " " + this.name;
	}

	@Override
	public String base() {
		return this.name;
	}
	
	@Override
	public String suffix() {
		return "";
	}
	
}
