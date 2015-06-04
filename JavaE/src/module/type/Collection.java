package module.type;

import exception.CircularReference;

public class Collection implements Type {
	private Type of;
	private int size;

	public Collection(Type of, int size) {
		this.of = of;
		this.size = size;
	}

	public int getSize() throws CircularReference {
		return 4 + this.of.getSize() * this.size;
	}

	public String toString() {
		return this.base() + this.suffix();
	}

	@Override
	public String prefix(String name) {
		return "int " + name + "_length;\n\t";
	}
	
	@Override
	public String base() {
		return this.of.base();
	}

	@Override
	public String suffix() {
		return "[" + this.size + "]";
	}

	@Override
	public String getFullyQualifiedName() {
		return "[" + this.of.getFullyQualifiedName();
	}

	@Override
	public String getJNI() {
		return "j" + this.of.getJNI() + "array";
	}

	@Override
	public String fulfill(String indent, String name, String object, String id) {
		StringBuilder str = new StringBuilder();
		String fname = name.replace(".", "_").replace("[", "_").replace("]", "_");
		String array;
		String type;
		if (this.of instanceof Primitive) {
			type = this.of.base().substring(0, 1).toUpperCase() + this.of.base().substring(1);
			array = "j" + this.of.base() + "Array";
			str.append(indent + array + " a" + fname + " = (" + array + ") (*env)->GetObjectField(env, " + object + ", " + id + ");\n");
			str.append(indent + name + "_length = getArrayLength(env, a" + fname + ");\n");
			str.append(indent + "(*env)->Get" + type + "ArrayRegion(env, a" + fname + ", 0, " + name + "_length,  &(" + name + ");\n");
		}
		else {
			array = "jobjectArray";
			str.append(indent + array + " a" + fname + " = (" + array + ") (*env)->GetObjectField(env, " + object + ", " + id + ");\n");
			str.append(indent + "int i" + fname + ";\n");
			str.append(indent + name + "_length = getArrayLength(env, a" + fname + ");\n");
			str.append(indent + "for (i" + fname + " = 0; i" + fname + " < " + name + "_length ; i" + fname + "++) {\n");
			str.append(this.of.fulfill(indent + "\t", name + "[i" + fname + "]", object, id));
			str.append(indent + "}\n");
		}
		return str.toString();
	}

	@Override
	public String extract(String indent, String name, String object, String id) {
		StringBuilder str = new StringBuilder();
		String fname = name.replace(".", "_").replace("[", "_").replace("]", "_");
		String array;
		String type;
		if (this.of instanceof Primitive) {
			type = this.of.base().substring(0, 1).toUpperCase() + this.of.base().substring(1);
			array = "j" + this.of.base() + "Array";
			str.append(indent + array + " a" + fname + " = (" + array + ") (*env)->GetObjectField(env, " + object + ", " + id + ");\n");
			str.append(indent + "(*env)->Set" + type + "ArrayRegion(env, a" + fname + ", 0, " + name + "_length,  &(" + name + ");\n");
		}
		else {
			array = "jobjectArray";
			str.append(indent + array + " a" + fname + " = (" + array + ") (*env)->GetObjectField(env, " + object + ", " + id + ");\n");
			str.append(indent + "int i" + fname + ";\n");
			str.append(indent + "for (i" + fname + " = 0; i" + fname + " < " + name + "_length ; i" + fname + "++) {\n");
			str.append(this.of.extract(indent + "\t", name + "[i" + fname + "]", object, id));
			str.append(indent + "}\n");
		}
		return str.toString();
	}


}
