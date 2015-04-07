package balayageK2.awt;

import java.util.Hashtable;

public class Coordonnees3D extends Coordonnees2D {
	protected int z;
	public static long ctr3D = 0L;

	private static Hashtable<String, Coordonnees3D> table3D = new Hashtable<String, Coordonnees3D>();
	
	private Coordonnees3D (double x, double y, double z) {
		this.x = (int)Math.round(x*coeficientArrondi);
		this.y = (int)Math.round(y*coeficientArrondi);
		this.z = (int)Math.round(z*coeficientArrondi);
		ctr3D++;
	}
	
	private Coordonnees3D (String str) {
		int pos1 = str.indexOf('x');
		int pos2 = str.indexOf('x', pos1+1);
		double dx = Double.parseDouble(str.substring(0, pos1));
		double dy = Double.parseDouble(str.substring(pos1+1, pos2));
		double dz = Double.parseDouble(str.substring(pos2+1));
		x = (int) Math.round(dx*coeficientArrondi);
		y = (int) Math.round(dy*coeficientArrondi);
		z = (int) Math.round(dz*coeficientArrondi);
		ctr3D++;
	}
	

	public static Coordonnees3D getCoordonnees3D(double x, double y, double z) {
		if (AppletDessin.avecTableCache) {
			int ix = (int) Math.round(x*coeficientArrondi);
			int iy = (int) Math.round(y*coeficientArrondi);
			int iz = (int) Math.round(z*coeficientArrondi);
			double dx = ((double) ix)/coeficientArrondi;
			double dy = ((double) iy)/coeficientArrondi;
			double dz = ((double) iz)/coeficientArrondi;
			String cle = ""+dx+'x'+dy+'x'+dz;
			if (!table3D.containsKey(cle)) table3D.put(cle, new Coordonnees3D(dx, dy, dz));
			return table3D.get(cle);
		} else return new Coordonnees3D(x, y, z);
	}
	
	public static Coordonnees3D getCoordonnees3D(String cle){
		if (AppletDessin.avecTableCache) {
			if (!table3D.containsKey(cle)) table3D.put(cle, new Coordonnees3D(cle));
			return table3D.get(cle);
		} else return new Coordonnees3D(cle);
	}
	
	public double getZ() { 
		return ((double)z)/coeficientArrondi;
	}
		
	public boolean equals(Object obj) {
		if (obj==null) return false;
		else if (obj instanceof Coordonnees3D) {
			Coordonnees3D objCoor = (Coordonnees3D) obj;
			return x==objCoor.x && y == objCoor.y && z == objCoor.z;
		} else return false;
	}
	
	public int hashCode() {
		return (int) Math.round((((x*coeficientArrondi+y)*coeficientArrondi)+z)*coeficientArrondi);
	}
	
	public String toString() {
		return ""+getX()+"x"+getY()+"x"+getZ();
	}
	
}
