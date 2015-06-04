package module;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import exception.MultipleDeclaration;

import module.type.Struct;

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
	private HashMap<String,Struct> arguments;
	private HashMap<String,Struct> results;
	
	private Module(String name, String path) {
		this.name = name;
		this.path = path;
		this.ios = new HashSet<String>();
		this.arguments = new HashMap<String,Struct>();
		this.results = new HashMap<String,Struct>();
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getPath() {
		return this.path;
	}
	
	public void addArg(String name, Struct type) throws MultipleDeclaration {
		if (ios.contains(name))
			throw new MultipleDeclaration(this.name,name);
		ios.add(name);
		arguments.put(name,type);
	}
	
	public Set<Entry<String, Struct>> getArgs() {
		return this.arguments.entrySet();
	}
	
	public void addRes(String name, Struct type) throws MultipleDeclaration {
		if (ios.contains(name))
			throw new MultipleDeclaration(this.name,name);
		ios.add(name);
		results.put(name,type);
	}
	
	public Set<Entry<String, Struct>> getRess() {
		return this.results.entrySet();
	}
	
	public Set<Struct> getObjects() {
		HashSet<Struct> set = new HashSet<Struct>();
		for (Entry<String,Struct> object : this.arguments.entrySet())
			set.add(object.getValue());
		for (Entry<String,Struct> object : this.results.entrySet())
			set.add(object.getValue());
		return set;
	}
	
	public String toString() {
		StringBuilder str = new StringBuilder(this.name);
		return str.toString();
	}
	
	public String getCallCSignature(String name) {
		StringBuilder sig = new StringBuilder();
		sig.append("JNIEXPORT jint JNICALL Java_" + name + "_call" + this.name + "\n");
		sig.append("\t(JNIEnv * env, jobject this");
		for (String arg : this.arguments.keySet()) {
			sig.append(", jobject " + arg);
		}
		sig.append(")");
		return sig.toString();
	}
	
	public String getJoinCSignature(String name) {
		StringBuilder sig = new StringBuilder();
		sig.append("JNIEXPORT jint JNICALL Java_" + name + "_join" + this.name + "\n");
		sig.append("\t(JNIEnv * env, jobject this, jint id");
		for (String res : this.results.keySet()) {
			sig.append(", jobject " + res);
		}
		sig.append(")");
		return sig.toString();
	}
	
}
