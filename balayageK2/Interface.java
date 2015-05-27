package balayageK2;

import java.util.Vector;

public class Interface {

	static {
		String libPathProperty = System.getProperty("java.library.path");
        System.out.println(libPathProperty);
		System.loadLibrary("calcul");
	}
	
	static public native void tests_calcul(
		byte ordreCycle,
		//double[][] lgN,
		double[] valInit,
		double a,
		double b,
		double epsilonVal,
	    int mMax,
	    int nMax,
	    int m,
	    int nombreLignes,
	    int masqueIndiceLigne,
	    int lstChoixPlanSelectedIndex,
	    int indiceIterationCourante,
	    int indiceIterationPrecedente,
	    int noIterationCourante,
	    //int height,
	    //int width,
	    long ctrCalculs,
	    Vector<Double> lstPtsX,
	    Vector<Double> lstPtsY,
	    Vector<Integer> lstPtsC);
	
	static public void rien() {
		
	}
}
