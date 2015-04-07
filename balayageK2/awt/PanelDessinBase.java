package balayageK2.awt;

import java.awt.Dimension;
import java.util.Hashtable;

import javax.swing.JPanel;


public class PanelDessinBase  extends JPanel {
	public double échelleY;
	public double échelleX;
	public double deplX;
	public double deplY;
	public double minYVal;
	public double minXVal;
	public double maxYVal;
	public double maxXVal;
	public Dimension dimZone;
	
	/* pour 3D */
	int xmax=300;
	int ymax=300;
	int zmax=300;

	public void getHashtableCalcul(Hashtable<String, Object> initialisation) {
		échelleY = (Double) initialisation.get("échelleY");
		échelleX = (Double) initialisation.get("échelleX");
		deplX = (Double) initialisation.get("deplX");
		deplY = (Double) initialisation.get("deplY");
		minYVal = (Double) initialisation.get("minYVal");
		minXVal = (Double) initialisation.get("minXVal");
		maxYVal = (Double) initialisation.get("maxYVal");
		maxXVal = (Double) initialisation.get("maxXVal");
		xmax = (Integer) initialisation.get("xmax");
		ymax = (Integer) initialisation.get("ymax");
		zmax = (Integer) initialisation.get("zmax");
		dimZone = (Dimension) initialisation.get("dim");
	}
	
	public boolean dansZoneAffichage(double x, double y) {
		int yy = convertY(y);
		int xx = convertX(x);
		return (yy>=0 && yy<dimZone.height && xx>=0 && xx<dimZone.width);
	}
	
	public boolean dansZoneAffichage(double x, double y, double z) {
		int zz = convertY(z);
		int yy = convertY(y);
		int xx = convertX(x);
		return (zz>=0 && zz<zmax &&
       			yy>=0 && yy<ymax &&
       			xx>=0 && xx<xmax);
	}
	
    public int convertX(double x) {
    	//double res = (x-minX())*échelleX;
       	double res = (x+deplX-minXVal)*échelleX;
       	return ((int)Math.round(res));
    }
    
    public int convertY(double y) {
    	//double res = (maxY()-y)*échelleY;
    	double res = (maxYVal-y+deplY)*échelleY;
    	return ((int)Math.round(res));
    }
    
    public double convertXInv(int x) {
    	//double res = ((double) x)/échelleX + minX();
    	double res = ((double) x)/échelleX + minXVal - deplX;
    	return res;
    }
    
    public double convertYInv(int y) {
    	//double res = maxY() - ((double) y)/échelleY ;
       	double res = maxYVal + deplY - ((double) y)/échelleY ;
           	return res;
    }

}
