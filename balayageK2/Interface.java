package balayageK2;

public class Interface {

	static {
		String libPathProperty = System.getProperty("java.library.path");
        System.out.println(libPathProperty);
		System.loadLibrary("calcul");
	}
	
	static public native int[] tests_calcul(
        CalculCascade2D objet,
	    double[] valInit,
	    double echelleX,
	    double echelleY,
	    double deplX,
	    double deplY,
	    double maxXVal,
	    double maxYVal
	);

        static public native int einit();

        static public native void eclose();
	
	static public void rien() {
		
	}
}
