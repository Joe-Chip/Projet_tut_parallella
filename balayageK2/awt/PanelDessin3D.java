package balayageK2.awt;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.JRadioButton;

import balayageK2.FonctionBalayage;

public class PanelDessin3D extends PanelDessin2D {
	Hashtable<Coordonnees3D, ListeCouleurs> tablePoints3D = new Hashtable<Coordonnees3D, ListeCouleurs>();
	private static final long serialVersionUID = 1L;
	double[][] rotation;
	double[] src = new double[4];
	double[] dst = new double[4];
	
	public Thread changementAngle;
	
	double angle = 0.0;
	public PanelDessin3D() {
		rotation = new double[4][4];
		for (int i=0; i<4; i++) rotation[i][i] = 1.0;
		modePointLargeurFixe = true;
	}

	public void setParametres(InDouble pasX2, InDouble minX2, InDouble maxX2, 
			InDouble minY2, InDouble maxY2, JRadioButton afficherCourbes2, FonctionBalayage runner) {
		minX = minX2;
		maxX = maxX2;
		minY = minY2;
		maxY = maxY2;
		pasX = pasX2;
		//pasY = pasY2;
		afficherCourbes = afficherCourbes2;
		this.runner = runner;
	}


	public synchronized void raz() {
		super.raz();
       	Dimension dim = getSize();
		tablePoints3D = new Hashtable<Coordonnees3D, ListeCouleurs>();
		xmax = dim.width;
		ymax = dim.height;
		zmax = dim.height;
	}

	public synchronized void dessinerLeCadre(Graphics g) {
		g.setColor(Color.blue);
		double x = minX.getVal();
		double X = maxX.getVal();
		double y = minY.getVal();
		double Y = maxY.getVal();
		double z = minY.getVal();
		double Z = maxY.getVal();
		dessinerAxe(g, null, x, y, Z, x, Y, Z);
		dessinerAxe(g, null, x, y, Z, X, y, Z);
		dessinerAxe(g, null, x, y, Z, x, y, z);
		dessinerAxe(g, null, X, Y, Z, x, Y, Z);
		dessinerAxe(g, null, X, Y, Z, X, y, Z);
		dessinerAxe(g, null, X, Y, Z, X, Y, z);
		dessinerAxe(g, null, x, Y, z, x, Y, Z);
		dessinerAxe(g, null, x, Y, z, x, y, z);
		dessinerAxe(g, null, x, Y, z, X, Y, z);
		dessinerAxe(g, null, X, y, z, X, Y, z);
		dessinerAxe(g, null, X, y, z, x, y, z);
		dessinerAxe(g, null, X, y, z, X, y, Z);	
		g.setColor(Color.black);	
	}
	
	public synchronized void dessinerLesAxes(Graphics g) {
		prepaDessinerLesAxes(g);
		dessinerAxe(g, "X", minX.getVal(), 0.0, 0.0, maxX.getVal(), 0.0, 0.0);
		dessinerAxe(g, "Y", 0.0, minY.getVal(), 0.0, 0.0, maxY.getVal(), 0.0);
		dessinerAxe(g, "Z", 0.0, 0.0, minY.getVal(), 0.0, 0.0, maxY.getVal());
		for (int i = (int)(minX.getVal()*10.0); i<=(int)(maxX.getVal()*10.0); i++) {
			dessinerTrait(g, (double)i/10, 0.0, 0.0);
		}
		for (int i = (int)(minY.getVal()*10.0); i<=(int)(maxY.getVal()*10.0); i++) {
			dessinerTrait(g, 0.0, (double)i/10, 0.0);
		}
		for (int i = (int)(minY.getVal()*10.0); i<=(int)(maxY.getVal()*10.0); i++) {
			dessinerTrait(g, 0.0, 0.0, (double)i/10);
		}
	}
		  
	 synchronized void dessinerTrait(Graphics g, double debX, double debY, double debZ) {
 		src[0]= debX;
   		src[1]= debY;
   		src[2]= debZ;
   		src[3]= 1.0;
   		produit(dst, rotation, src);
       	int xxD = convertX(dst[0]);
    	int yyD = convertY(dst[1]);
   		g.drawLine(xxD-2, yyD+2, xxD+2, yyD-2);
   		g.drawLine(xxD-2, yyD-2, xxD+2, yyD+2);
	}
	
	 synchronized void dessinerAxe(Graphics g, String nom, double debX, double debY, double debZ, 
			double finX, double finY, double finZ) {
 		src[0]= debX;
   		src[1]= debY;
   		src[2]= debZ;
   		src[3]= 1.0;
   		produit(dst, rotation, src);
       	int xxD = convertX(dst[0]);
    	int yyD = convertY(dst[1]);
 		src[0]= finX;
   		src[1]= finY;
   		src[2]= finZ;
   		src[3]= 1.0;
   		produit(dst, rotation, src);
      	int xxF = convertX(dst[0]);
    	int yyF = convertY(dst[1]);
   		g.drawLine(xxD, yyD, xxF, yyF);
   		if (nom!=null) {
   			g.drawString("-"+nom, xxD+2, yyD-2);
   			g.drawString("+"+nom, xxF+2, yyF-2);
   		}
	}
	
/*	String créerClé(double x, double y, double z) {
		StringBuffer sb = new StringBuffer();
		sb.append((int)(x*1000));
		sb.append('x');
		sb.append((int)(y*1000));
		sb.append('x');
		sb.append((int)(z*1000));
		return sb.toString();
	}*/
	
	public synchronized void rotate(double angleX, double angleY, double angleZ) {
		Dimension dim = getSize();
		double centreX = convertXInv(dim.width/2);
		double centreY = convertYInv(dim.height/2);
		double centreZ = centreY;
		//System.out.println("Centre "+centreX+" "+centreY);
		rotation = new double[4][4];
		double[][] tmp1 = new double[4][4];
		double[][] tmp2 = new double[4][4];
		produit(tmp1, initDepl(centreX, centreY, centreZ), initX(angleX));
		produit(tmp2, tmp1, initY(angleY));
		produit(tmp1, tmp2, initZ(angleZ));
		produit(rotation, tmp1, initDepl(-centreX, -centreY, -centreZ));
		//print(rotation);
		super.raz();
		redessiner();
		//int nbrTotalInitial = tablePoints3D.size();
		//int nbrTotal = nbrTotalInitial;
		for (Enumeration<Coordonnees3D> e = tablePoints3D.keys(); e.hasMoreElements();) {
			Coordonnees3D cle = e.nextElement();
			//nbrTotal--;
			src[0]= cle.getX();
			src[1]= cle.getY();
			src[2]= cle.getZ();
			src[3]= 1.0;
			produit(dst, rotation, src);
			ajouterPoint(dst[0], dst[1], tablePoints3D.get(cle));
			if (arretRafraichissementEcran) {
				//System.out.println("Arret rafraichissement 3D "+nbrTotal+"/"+nbrTotalInitial);
				break;
			}// else System.out.println(cle.toString());
		}
		//threadRafraichissementEcran.setPriority(Thread.currentThread().getPriority()-1);
		//threadRafraichissementEcran.start();
	}

	public synchronized void rotateSimpleXX(double angleX, double angleY, double angleZ) {
		rotation = new double[4][4];
		double[][] tmp1 = new double[4][4];
		produit(tmp1, initX(angleX), initY(angleY));
		produit(rotation, tmp1, initZ(angleZ));
		//print(rotation);
		super.raz();
		redessiner();
		for (Enumeration<Coordonnees3D> e = tablePoints3D.keys(); e.hasMoreElements();) {
			Coordonnees3D cle = e.nextElement();
			src[0]= cle.getX();
			src[1]= cle.getY();
			src[2]= cle.getZ();
	   		src[3]= 1.0;
			produit(dst, rotation, src);
			ajouterPoint(dst[0], dst[1], tablePoints3D.get(cle));
		}
	}

   public void saveData (BufferedWriter bw) throws IOException {
		for (Enumeration<Coordonnees3D> e = tablePoints3D.keys(); e.hasMoreElements();) {
			Coordonnees3D cle = e.nextElement();
			bw.write(cle.toString());
			bw.newLine();
		}   	
    }
    
    public void readData (BufferedReader br) throws IOException {
    	raz();
    	redessiner();
    	String cle = br.readLine();
    	while (cle!=null) {
    		ajouterPoint3D(Coordonnees3D.getCoordonnees3D(cle), new ListeCouleurs(Color.red));
    		cle= br.readLine();
    	}
    	
    }
    
    public void ajouterPoint3Ddistant(double x, double y, double z, ListeCouleurs c)
    throws RemoteException {
    	ajouterPoint3D(x, y, z, c);
    }
    
    public synchronized void ajouterPoint3D(double x, double y, double z, ListeCouleurs c) {
    	//dessin2D.ajouterPoint(x, y, c);
    	int zz = convertY(z);
    	int yy = convertY(y);
       	int xx = convertX(x);
       	//ajouterPoint(xx, yy, Color.green);
			//System.out.println("==Table1 "+xx+" "+yy+" "+zz);
       	//System.out.println("==Table1 "+x+" "+xx+" "+convertXInv(xx));
        //System.out.println("==Table1i "+x+" "+y+" "+z+" : "+xx+" "+yy+" "+zz);
        //System.out.println("==Table1i2 "+xmax+" "+ymax+" "+zmax);
       	if (zz>=0 && zz<zmax &&
       			yy>=0 && yy<ymax &&
       			xx>=0 && xx<xmax) {
   			//System.out.println("==Table1 "+x+" "+y+" "+z);
       		tablePoints3D.put(Coordonnees3D.getCoordonnees3D(x, y, z), c);
       		//System.out.println("==Table2 "+xx+" "+yy+" "+xx);
       		src[0]= x;
       		src[1]= y;
       		src[2]= z;
       		src[3]= 1.0;
       		//System.out.println("==>"+" "+x+" "+y+" == "+dst[0]+" "+dst[1]);
       		produit(dst, rotation, src);
       	   	ajouterPoint(dst[0], dst[1], c);
      	} //else System.out.println("=?=>"+" "+x+" "+dessin2D.convertXInv(xx)+" "+y+" "+dessin2D.convertYInv(yy));
        	
    }
    
   public synchronized void ajouterPoint3D(Coordonnees3D coord, ListeCouleurs c) {
	   //dessin2D.ajouterPoint(x, y, c);
	   int zz = convertY(coord.getZ());
	   int yy = convertY(coord.getY());
	   int xx = convertX(coord.getX());
	   //ajouterPoint(xx, yy, Color.green);
	   //System.out.println("==Table1 "+xx+" "+yy+" "+zz);
	   //System.out.println("==Table1 "+x+" "+xx+" "+convertXInv(xx));
	   //System.out.println("==Table1i "+x+" "+y+" "+z+" : "+xx+" "+yy+" "+zz);
	   //System.out.println("==Table1i2 "+xmax+" "+ymax+" "+zmax);
	   if (zz>=0 && zz<zmax &&
			   yy>=0 && yy<ymax &&
			   xx>=0 && xx<xmax) {
		   //System.out.println("==Table1 "+x+" "+y+" "+z);
		   tablePoints3D.put(coord, c);
		   //System.out.println("==Table2 "+xx+" "+yy+" "+xx);
		   src[0]= coord.getX();
		   src[1]= coord.getY();
		   src[2]= coord.getZ();
	   		src[3]= 1.0;
		   //System.out.println("==>"+" "+x+" "+y+" == "+dst[0]+" "+dst[1]);
		   produit(dst, rotation, src);
		   ajouterPoint(dst[0], dst[1], c);
	   } //else System.out.println("=?=>"+" "+x+" "+dessin2D.convertXInv(xx)+" "+y+" "+dessin2D.convertYInv(yy));

   }
   
	private static void produit(double[][] res, double[][] m1, double[][] m2) {
		// res <= m1 x m2
		for (int i=0; i<4; i++)
			for (int j=0; j<4; j++) {
				res[i][j] = 0.0;
				for (int k=0; k<4; k++)
					res[i][j] = res[i][j] + m1[i][k]*m2[k][j];
			}
		
	}
	
	private static void produit(double[] res, double[][] m1, double[] m2) {
		// res <= m1 x m2
		for (int i=0; i<4; i++) {
			res[i] = 0.0;
				for (int k=0; k<4; k++)
					res[i] = res[i] + m1[i][k]*m2[k];
		}
	}
	
	private static void copie(double[][] res, double[][] m1) {
		// res <= m1
		for (int i=0; i<4; i++)
			for (int j=0; j<4; j++) {
				res[i][j] = m1[i][j];
			}		
	}
	
	
	private static double[][] initDepl(double deplX, double deplY, double deplZ) {
		/*
		     1       0          0       deplX
	         0       1     		0    	deplY
		     0       0      	1     	deplZ
		     0		 0			0		1
		 */
		double[][] mat = new double[4][4];
		mat[0][0] = 1.0;
		mat[0][1] = 0.0;
		mat[0][2] = 0.0;
		mat[0][3] = deplX;
		mat[1][0] = 0.0;
		mat[1][1] = 1.0;
		mat[1][2] = 0.0;
		mat[1][3] = deplY;
		mat[2][0] = 0.0;
		mat[2][1] = 0.0;
		mat[2][2] = 1.0;
		mat[2][3] = deplZ;
		mat[3][0] = 0.0;
		mat[3][1] = 0.0;
		mat[3][2] = 0.0;
		mat[3][3] = 1.0;
		return mat;
	}

	private static double[][] initX(double angle) {
		/*
		     1       0          0           0
	         0      cos(Ax)     -sin(Ax)    0
		     0      sin(Ax)     cos(Ax)     0
		     0		0			0			1
		 */
		double[][] mat = new double[4][4];
		mat[0][0] = 1.0;
		mat[0][1] = 0.0;
		mat[0][2] = 0.0;
		mat[0][3] = 0.0;
		mat[1][0] = 0.0;
		mat[1][1] = Math.cos(angle);
		mat[1][2] = -Math.sin(angle);
		mat[1][3] = 0.0;
		mat[2][0] = 0.0;
		mat[2][1] = Math.sin(angle);
		mat[2][2] = Math.cos(angle);
		mat[2][3] = 0.0;
		mat[3][0] = 0.0;
		mat[3][1] = 0.0;
		mat[3][2] = 0.0;
		mat[3][3] = 1.0;
		return mat;
	}

	private static double[][] initY(double angle) {
	/*
		 	cos(Ay)   0   sin(Ay)  0
         	0         1   0        0
	        -sin(Ay)  0   cos(Ay)  0
			0		  0   0        1
		 */
		double[][] mat = new double[4][4];
		mat[0][0] = Math.cos(angle);
		mat[0][1] = 0.0;
		mat[0][2] = Math.sin(angle);
		mat[0][3] = 0.0;
		mat[1][0] = 0;
		mat[1][1] = 1.0;
		mat[1][2] = 0.0;
		mat[1][3] = 0.0;
		mat[2][0] = -Math.sin(angle);
		mat[2][1] = 0.0;
		mat[2][2] = Math.cos(angle);
		mat[2][3] = 0.0;
		mat[3][0] = 0.0;
		mat[3][1] = 0.0;
		mat[3][2] = 0.0;
		mat[3][3] = 1.0;
		return mat;
	}

	private static double[][] initZ(double angle) {
		/*
		 	cos(Az)   sin(Az)   0     0
	        -sin(Az)   cos(Az)    0    0
	        0           0        1     0
			0			0		0	   1
		 */
		double[][] mat = new double[4][4];
		mat[0][0] = Math.cos(angle);
		mat[0][1] = -Math.sin(angle);
		mat[0][2] = 0.0;
		mat[0][3] = 0.0;
		mat[1][0] = Math.sin(angle);
		mat[1][1] = Math.cos(angle);
		mat[1][2] = 0.0;
		mat[1][3] = 0.0;
		mat[2][0] = 0.0;
		mat[2][1] = 0.0;
		mat[2][2] = 1.0;
		mat[2][3] = 0.0;
		mat[3][0] = 0.0;
		mat[3][1] = 0.0;
		mat[3][2] = 0.0;
		mat[3][3] = 1.0;
		return mat;
	}

	private static void print(double[][] mat) {
		System.out.println("   "+mat[0][0]+"  "+mat[0][1]+"  "+mat[0][2]+"  "+mat[0][3]);
		System.out.println("   "+mat[1][0]+"  "+mat[1][1]+"  "+mat[1][2]+"  "+mat[1][3]);
		System.out.println("   "+mat[2][0]+"  "+mat[2][1]+"  "+mat[2][2]+"  "+mat[2][3]);
		System.out.println("   "+mat[3][0]+"  "+mat[3][1]+"  "+mat[3][2]+"  "+mat[3][3]);
		System.out.println();
	}

	
 	public static void main(String[] args) {
		double angle = 3.141592;
		double[][] rotation = new double[3][3];
		double[][] tmp1 = new double[3][3];
		produit(tmp1, initX(angle), initY(angle));
		produit(rotation, tmp1, initZ(angle));
		print(rotation);
	}
 	
}

