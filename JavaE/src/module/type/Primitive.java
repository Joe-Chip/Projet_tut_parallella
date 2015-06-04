package module.type;


public class Primitive implements Type {
	public static final int BOOLEAN = 0;
	public static final int BYTE = 1;
	public static final int CHAR = 2;
	public static final int SHORT = 3;
	public static final int INT = 4;
	public static final int LONG = 5;
	public static final int FLOAT = 6;
	public static final int DOUBLE = 7;
	
	private static final String C[] = {"boolean", "byte", "char", "short", "int", "long", "float", "double"}; 
	private static final String FQN[] = {"Z", "B", "C", "S", "I", "J", "F", "D"};
	private static final int SIZE[] = {1, 1, 2, 2, 4, 8, 4, 8};
	
	private int type;
	
	public Primitive(String type) {
		for (int i = 0; i < C.length; i++) {
			if (type.equals(C[i])) {
				this.type = i;
				break;
			}
		}
	}
	
	public int getSize() {
		return SIZE[this.type];
	}
	
	public String toString() {
		return this.base();
	}
	
	@Override
	public String prefix(String name) {
		return "";
	}
	
	@Override
	public String base() {
		return C[type];
	}

	@Override
	public String suffix() {
		return "";
	}

	@Override
	public String getFullyQualifiedName() {
		return FQN[type];
	}

	@Override
	public String getJNI() {
		return "j" + C[type];
	}

	@Override
	public String fulfill(String indent, String name, String object, String id) {
		return indent + name + " = (" + C[type] + ") (*env)->Get" + C[type].substring(0, 1).toUpperCase() + C[type].substring(1) + "Field(env, " + object + ", " + id + ");";
	}

	@Override
	public String extract(String indent, String name, String object, String id) {
		return indent + "(*env)->Set" + C[type].substring(0, 1).toUpperCase() + C[type].substring(1) + "Field(env, " + object + ", " + id + ", " + name + ");";
	}
	

}
