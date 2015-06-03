package exception;

public class MultipleDeclaration extends Exception {

	private static final long serialVersionUID = 8058952763169817253L;
	public MultipleDeclaration(String field, String struct) {
		super("parsing error : field \"" + field + "\" of object \"" + "\" was declared more than once");
	}
}
