package balayageK2;

public class CalculInitial {
	static final int nMax = 30;
	static final int mMax = 30;
	static final double epsilon = 10E-10;

	static double[] lgN0 = new double[mMax];
	static double[] lgN1 = new double[mMax];
	static double[] lgN2 = new double[mMax];
	
	public static void main (String[] arg) {
		for (double a=-2.0; a<=+2.0; a=a+0.1) 
			for (double b=-2.0; b<=+2.0; b=b+0.1) {
				System.out.println("**************** A="+a+" B="+b);
				// initialisation lgN0
				for (int m=0; m<mMax; m++) lgN0[m] = 0.1;
				// initialisation lgN1
				lgN1[0] = 0.1;
				for (int m=1; m<mMax; m++) lgN1[m] = foo(lgN0[m], lgN0[m-1], a, b);
				// itération 2..nMax
				for (int n=2; n<nMax; n++) {
					lgN2[0] = 0.1;
					lgN2[1] = foo(lgN1[1], lgN1[0], a, b);
					for (int m=2; m<mMax; m++) {
						lgN2[m] = foo(lgN1[m], lgN1[m-1], a, b);
						//test d'affichage
						if ((Math.abs(lgN2[m]-lgN0[m]) < epsilon) &&
								(Math.abs(lgN2[m]-lgN2[m-2]) < epsilon)) {
							System.out.println("A="+a+" B="+b);
						}
					}
					// on décale les lignes
					lgN0 = lgN1;
					lgN1 = lgN2;
					lgN2 = new double[mMax];
				}
			}
	}

	static double foo (double valM, double valMm1, double a, double b) {
		return valM * valM + b * valMm1 + a;
	}
}
