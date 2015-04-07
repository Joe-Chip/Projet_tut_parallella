package balayageK2.awt;

import java.util.Hashtable;

public class Coordonnees2D {
	protected static double coeficientArrondi = 10000.0;
	protected int x;
	protected int y;

	public static long ctr2D = 0L;
	private static Hashtable<String, Coordonnees2D> table2D = new Hashtable<String, Coordonnees2D>();
	
	protected Coordonnees2D() {
		
	}
	
	private Coordonnees2D (double x, double y) {
		this.x = (int)Math.round(x*coeficientArrondi);
		this.y = (int)Math.round(y*coeficientArrondi);
		//System.out.println("Coordonnees2D "+x+" "+y+" "+hashCode());
		ctr2D++;
	}
	
	private Coordonnees2D (String str) {
		int pos1 = str.indexOf('x');
		double dx = Double.parseDouble(str.substring(0, pos1));
		double dy = Double.parseDouble(str.substring(pos1+1));
		x = (int) Math.round(dx*coeficientArrondi);
		y = (int) Math.round(dy*coeficientArrondi);
		//System.out.println("Coordonnees2Dstr "+str);
		ctr2D++;
	}
	
	public static Coordonnees2D getCoordonnees2D(double x, double y) {
		if (AppletDessin.avecTableCache) {
			int ix = (int) Math.round(x*coeficientArrondi);
			int iy = (int) Math.round(y*coeficientArrondi);
			double dx = ((double) ix)/coeficientArrondi;
			double dy = ((double) iy)/coeficientArrondi;
			String cle = ""+dx+'x'+dy;
			if (!table2D.containsKey(cle)) table2D.put(cle, new Coordonnees2D(dx, dy));
			return table2D.get(cle);
		} else return new Coordonnees2D(x, y);
	}
	

	public static Coordonnees2D getCoordonnees2D(String cle) {
		if (AppletDessin.avecTableCache) {
			if (!table2D.containsKey(cle)) table2D.put(cle, new Coordonnees2D(cle));
			return table2D.get(cle);
		} else return new Coordonnees2D(cle);
	}
	
	public double getX() { 
		return ((double)x)/coeficientArrondi;
	}
	
	public double getY() { 
		return ((double)y)/coeficientArrondi;
	}
	
	public boolean equals(Object obj) {
		if (obj==null) return false;
		else if (obj instanceof Coordonnees2D) {
			Coordonnees2D objCoor = (Coordonnees2D) obj;
			return x==objCoor.x && y == objCoor.y;
		} else return false;
	}
	
	public int hashCode() {
		return (int) Math.round((x*coeficientArrondi+y)*coeficientArrondi);
	}
	
	public String toString() {
		return ""+getX()+"x"+getY();
	}
	
	public static void main(String[] args) {
		Coordonnees2D obj1 = new Coordonnees2D(1.2, 3.4);
		Coordonnees2D obj2 = new Coordonnees2D(1.20, 3.400);
		Coordonnees2D obj3 = new Coordonnees2D(1.21, 3.400);
		Coordonnees2D obj4 = new Coordonnees2D(10e36, 3.400);
		Coordonnees2D obj5 = new Coordonnees2D(Double.POSITIVE_INFINITY, 3.400);
		Coordonnees2D obj6 = new Coordonnees2D(Double.NaN, 3.400);
		System.out.println("1 2 "+obj1.equals(obj2)+" "+obj2.equals(obj1));
		System.out.println("1 3 "+obj1.equals(obj3)+" "+obj3.equals(obj1));
		System.out.println("2 3 "+obj2.equals(obj3)+" "+obj3.equals(obj2));
		System.out.println(""+obj1.hashCode());
		System.out.println(""+obj2.hashCode());
		System.out.println(""+obj3.hashCode());
		System.out.println(""+obj4.hashCode());
		System.out.println(""+obj5.hashCode());
		System.out.println(""+obj6.hashCode());
		System.out.println();
		java.util.Hashtable<Coordonnees2D,String> table = new java.util.Hashtable<Coordonnees2D,String>();
		table.put(obj1, "obj 1");
		table.put(obj2, "obj 2");
		table.put(obj3, "obj 3");
		table.put(obj1, "obj 1 bis");
		for (java.util.Enumeration<Coordonnees2D> e = table.keys(); e.hasMoreElements();)
		       System.out.println(e.nextElement());

		System.out.println();
		System.out.println(""+"abcdef".hashCode());
		System.out.println(""+"abcdef".hashCode());
	}
}
