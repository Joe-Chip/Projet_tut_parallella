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
		if (s == null) {
			s = new Struct(name);
			objects.put(name, s);
			s.fullyQualifiedName = fQN;
		}
		else {
			if (s.fullyQualifiedName == null)
				s.fullyQualifiedName = fQN;
			else if (!s.fullyQualifiedName.equals(fQN))
				throw new MultipleDeclaration(name, fQN);
		}
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
	
	private Struct(String name) {
		this.name = name;
		this.fullyQualifiedName = null;
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
	
	public Set<Entry<String,Type>> getFields() {
		return this.fields.entrySet();
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
			header.append("\t" + e.getValue().prefix(e.getKey()) + e.getValue().base() + " " + e.getKey() + e.getValue().suffix() + ";\n");
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
		return "L" + this.fullyQualifiedName;
	}
	
	@Override
	public String prefix(String name) {
		return "";
	}
	
	@Override
	public String base() {
		return this.name;
	}
	
	@Override
	public String suffix() {
		return "";
	}
	@Override
	public String getJNI() {
		return "jobject";
	}
	
	@Override
	public String fulfill(String indent, String name, String object, String id) {
		StringBuilder str = new StringBuilder();
		String fname = name.replace(".", "_").replace("[", "_").replace("]", "_");
		if (id != null) {
			str.append(indent + "jobject " + fname + " = (*env)->GetObjectField(env, " + object + ", " + id + ");\n");
			object = fname;
		}
		for (Entry<String, Type> e : this.getFields()) {
			String field = e.getKey();
			Type type = e.getValue();
			str.append(type.fulfill(indent, name + "." + field, object, this.base().toLowerCase() + "_" + field + "ID") + "\n");
		}
		return str.toString();
	}
	@Override
	public String extract(String indent, String name, String object, String id) {
		StringBuilder str = new StringBuilder();
		String fname = name.replace(".", "_").replace("[", "_").replace("]", "_");
		if (id != null) {
			str.append(indent + "jobject " + fname + " = (*env)->GetObjectField(env, " + object + ", " + id + ");\n");
			object = fname;
		}
		for (Entry<String, Type> e : this.getFields()) {
			String field = e.getKey();
			Type type = e.getValue();
			str.append(type.extract(indent, name + "." + field, object, this.base().toLowerCase() + "_" + field + "ID") + "\n");
		}
		return str.toString();
	}
	
	
}
