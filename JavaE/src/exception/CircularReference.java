package exception;

public class CircularReference extends Exception {

	private static final long serialVersionUID = 917187314260052882L;
	public CircularReference(String object1, String object2) {
		super("parsing error : infinite loop reference between object " + object1 + " and object " + object2);
	}
}
