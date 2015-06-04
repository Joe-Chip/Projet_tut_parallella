package epiphany;

import test.Machin;
import test.Truc;

public class ETest {

	private static ETest instance;
	static {
		instance = null;
		System.loadLibrary("src/test/test.so");
	}

	public static ETest open() {
		if (instance == null)
			instance = new ETest();
		return instance;
	}

	private ETest() {
		this.einit();
	}

	private native void einit();
	private native void eclose();

	public void close() {
		this.eclose();
	}

	public native int callMain(Machin a);
	public native int joinMain(int id, Truc r);

}