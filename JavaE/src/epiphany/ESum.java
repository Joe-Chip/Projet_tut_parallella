package epiphany;

import epiphany.Espace;
import epiphany.MInt;
import epiphany.Coordonnee;

public class ESum {

	private static ESum instance;
	static {
		instance = null;
		System.loadLibrary("src/epiphany/sum.so");
	}

	public static ESum open() {
		if (instance == null)
			instance = new ESum();
		return instance;
	}

	private ESum() {
		this.einit();
	}

	private final Class<Espace> espace = Espace.class;
	private final Class<MInt> mint = MInt.class;
	private final Class<Coordonnee> coordonnee = Coordonnee.class;

	private native void einit();
	private native void eclose();

	public void close() {
		this.eclose();
		instance = null;
	}

	public native int callSomme(Espace data, MInt maxIter, MInt minIter);
	public native int joinSomme(int id, Coordonnee convergence);

}