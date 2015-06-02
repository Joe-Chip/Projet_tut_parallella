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
        /*
		double a,
		double b,
		double epsilonVal,
	    int mMax,
	    int nMax,
	    int m,
	    int nombreLignes,
	    int masqueIndiceLigne,
	    int lstChoixPlanSelectedIndex,
	    //long ctrCalculs,
        */
	    double echelleX,
	    double echelleY,
	    double deplX,
	    double deplY,
	    double maxXVal,
	    double maxYVal
	);
	
	static public void rien() {
		
	}
}
