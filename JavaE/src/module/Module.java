package module;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import exception.MultipleDeclaration;

import module.type.Struct;
import module.type.Type;

public class Module {
	static public HashMap<String,Module> modules;
	static {
		modules = new HashMap<String,Module>();
	}
	
	public static Module namedModule(String name, String path) {
		Module m = modules.get(name);
		if (m == null) {
			m = new Module(name,path);
			modules.put(name, m);
		}
		return m;
	}
	
	
	private String name;
	private String path;
	private HashSet<String> ios;
	private HashMap<String,Type> arguments;
	private HashMap<String,Type> results;
	
	private Module(String name, String path) {
		this.name = name;
		this.path = path;
		this.ios = new HashSet<String>();
		this.arguments = new HashMap<String,Type>();
		this.results = new HashMap<String,Type>();
	}
	
	public void addArg(String name, Struct type) throws MultipleDeclaration {
		if (ios.contains(name))
			throw new MultipleDeclaration(this.name,name);
		ios.add(name);
		arguments.put(name,type);
	}
	
	public Set<Entry<String, Type>> getArgs() {
		return this.arguments.entrySet();
	}
	
	public void addRes(String name, Struct type) throws MultipleDeclaration {
		if (ios.contains(name))
			throw new MultipleDeclaration(this.name,name);
		ios.add(name);
		results.put(name,type);
	}
	
	public Set<Entry<String, Type>> getRess() {
		return this.results.entrySet();
	}
	
	public Set<Struct> getObjects() {
		HashSet<Struct> set = new HashSet<Struct>();
		for (Entry<String,Type> object : this.arguments.entrySet())
			set.add((Struct) object.getValue());
		for (Entry<String,Type> object : this.results.entrySet())
			set.add((Struct) object.getValue());
		return set;
	}
	
	public String toString() {
		StringBuilder str = new StringBuilder(this.name + " " + this.path + "\n");
		for (Entry<String,Type> e : arguments.entrySet()) {
			str.append("=> " + e.getValue().base() + " " + e.getKey() + "\n");
		}
		for (Entry<String,Type> e : results.entrySet()) {
			str.append("<= " + e.getValue().base() + " " + e.getKey() + "\n");
		}
		return str.toString();
	}
	
}
