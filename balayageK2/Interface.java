package balayageK2;

public class Interface {

	static {
		String libPathProperty = System.getProperty("java.library.path");
        System.out.println(libPathProperty);
		System.loadLibrary("calcul");
	}
	
	static public native void tests_calcul();
	
	static public void rien() {
		
	}
}
