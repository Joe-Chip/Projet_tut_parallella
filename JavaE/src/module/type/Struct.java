package module.type;

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
	
	public static Struct namedStruct(String name, String fQN) throws MultipleDeclaration {
		Struct s = objects.get(name);
		if (objects.containsKey(name))
			throw new MultipleDeclaration(name, fQN);
		s = new Struct(name, fQN);
		objects.put(name, s);
		return s;
	}
	public static Struct namedStruct(String name) {
		return objects.get(name);
	}
	
	private String name;
	private String fullyQualifiedName;
	private HashMap<String, Type> fields;
	private String header;
	private int size;
	
	private Struct(String name, String fQN) {
		this.name = name;
		this.fullyQualifiedName = fQN;
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
		StringBuilder header = new StringBuilder("struct " + this.name.toLowerCase() + " {\n");
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
		return this.name;
	}
	
	public String getDeclare() {
		return "struct " + this.name.toLowerCase() + ";\ntypedef struct " + this.name.toLowerCase() + " " + this.name + ";";
	}
	public String getStruct() throws CircularReference {
		if (references.get(this.name) == null)
			this.linearize();
		return this.header;
	}
	
	public String getFullyQualifiedName() {
		return this.fullyQualifiedName;
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
