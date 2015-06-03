package ecom;

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
	public String prefix() {
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

}
