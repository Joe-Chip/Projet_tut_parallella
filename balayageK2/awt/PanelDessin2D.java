package balayageK2.awt;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import sun.swing.ImageCache;

import balayageK2.FonctionBalayage;
import balayageK2.Foo;
import balayageK2.rmi.PanelDistant;

public class PanelDessin2D extends PanelDessinBase {
	public PanelCouleursBoutons panelCouleursBoutons;
	public PanelCouleurs panelCouleurs;
	public boolean arretRafraichissementEcran = false;
	public Thread threadRafraichissementEcran;
	public boolean modePointLargeurFixe = false; // vrai pour un point de largeur fixe  
	public Hashtable<Coordonnees2D, ListeCouleurs> tablePoints2D = new Hashtable<Coordonnees2D, ListeCouleurs>();
	//public Color[][] tablePoints = new Color[300][200];
	int largeurPoint = 1;
	int hauteurPoint = 1;
	//PanelParametres params;
	Graphics g2 = null; 
	public double zoom = 1.0;
	public FonctionBalayage runner;
	int largeurGraduationDe10 = 10;
	InDouble minY;
	InDouble minX;
	InDouble maxY;
	InDouble maxX;
	InDouble pasX;
	InDouble pasY;
	JRadioButton afficherCourbes;
	Image doubleBuffer;
	private boolean doubleBufferVisible = true;
	
	static int ctr=0;
	int no;

	private static final long serialVersionUID = 1L;

	public PanelDessin2D () {
		no = ctr++;
		setOpaque(true);
	}

	public void putHashtableCalcul(Hashtable<String, Object> initialisation) {
		initialisation.put("échelleY", échelleY);
		initialisation.put("échelleX", échelleX);
		initialisation.put("deplX", deplX);
		initialisation.put("deplY", deplY);
		initialisation.put("minYVal", minY.getVal());
		initialisation.put("minXVal", minX.getVal());
		initialisation.put("maxYVal", maxY.getVal());
		initialisation.put("maxXVal", maxX.getVal());
		initialisation.put("xmax", xmax);
		initialisation.put("ymax", ymax);
		initialisation.put("zmax", zmax);
		initialisation.put("dim", getSize());
	}
	
	public void redessiner() {
	   Dimension dim = getSize();
	   //g.clearRect(0, 0, dim.width, dim.height);
	   doubleBuffer = createImage(dim.width, dim.height);
	   Graphics g = doubleBuffer.getGraphics();
	   dessinerLesAxes(g);
	   dessinerLeCadre(g);
	   // on dessine les points
	   for (Enumeration<Coordonnees2D> e = tablePoints2D.keys(); e.hasMoreElements();) {
		   Coordonnees2D cle = e.nextElement();
	       ListeCouleurs lc = tablePoints2D.get(cle);
	       int yy = convertY(cle.getY());
	       int xx = convertX(cle.getX());
	       dessinerPoint(g, xx, yy, lc);
	       if (arretRafraichissementEcran) {
	    	   break;
	       }
	   }
	   dessinerLesAxes(g);
       repaint();
   }
   
   public void dessinerLeCadre(Graphics g) {
   }
   
   public void paintComponent(Graphics g) {
	   super.paintComponent(g);
	   /*if (doubleBuffer==null) {
		   redessiner();
	   }*/
	   redessiner();
	   g.drawImage(doubleBuffer, 0, 0, this);
	}
   
   protected void initEchelle() {
       Dimension d = getSize();
       minXVal = minX.getVal();
       minYVal = minY.getVal();
       maxXVal = maxX.getVal();
       maxYVal = maxY.getVal();
       double hauteurDouble = maxY.getVal()-minY.getVal();
       double largeurDouble = maxX.getVal()-minX.getVal();
       
       échelleY = ((double)(d.height))*zoom/hauteurDouble;
       échelleX = ((double)(d.width))*zoom/largeurDouble; 
	   
       deplY = -(hauteurDouble - ((double) d.height)/échelleY)/2.0 ;
       deplX = -(largeurDouble - ((double) d.width)/échelleX)/2.0 ;
       
       if (modePointLargeurFixe) {
    	   largeurPoint = 1;
    	   if (pasY!=null ) hauteurPoint = 1;
       } else {
           largeurPoint = (int)Math.round(pasX.getVal()*échelleX);
           if (pasY!=null ) hauteurPoint = (int)Math.round(pasY.getVal()*échelleY);
           //System.out.println(""+largeurPoint+" "+(pasX.getVal()*échelleX)+" "+(convertXInv(3)-convertXInv(2))+" "+(convertXInv(4)-convertXInv(3)));
       }
       if (largeurPoint<=0) largeurPoint = 1;
       if (hauteurPoint<=0) hauteurPoint= 1;
   }
   
   protected void prepaDessinerLesAxes(Graphics g) {
	   initEchelle();
	   g.setColor(Color.black);
	   
   }
   
  public void dessinerLesAxes(Graphics g) {
	   prepaDessinerLesAxes(g);
	   g.drawLine(0, convertY(0.0), getSize().width, convertY(0.0));
        for (int i = (int)(minX.getVal()*10.0); i<=(int)(maxX.getVal()*10.0); i++) {
        	g.drawLine(convertX((double)i/10), convertY(0.0)-4, 
        				convertX((double)i/10), convertY(0.0)+4);
        }
        for (int i = (int)minX.getVal(); i<=(int)maxX.getVal(); i++) {
        	g.drawLine(convertX((double)i), convertY(0.0)-largeurGraduationDe10, 
        				convertX((double)i), convertY(0.0)+largeurGraduationDe10);
        }
        for (int i = (int)(minY.getVal()*10.0); i<=(int)(maxY.getVal()*10.0); i++) {
        	g.drawLine(convertX(0.0)-4, convertY((double)i/10), convertX(0.0)+4, convertY((double)i/10));
        }
        for (int i = (int)minY.getVal(); i<=(int)maxY.getVal(); i++) {
        	g.drawLine(convertX(0.0)-largeurGraduationDe10, convertY((double)i), 
        			convertX(0.0)+largeurGraduationDe10, convertY((double)i));
        }
       g.drawLine(convertX(0.0), 0, convertX(0.0), getSize().height);
       runner.dessinerEquations(g, this);
    }
    
    public void dessinerEquation(Graphics g, Color c, Foo foo) {
    	if (afficherCourbes.isSelected()) {
    		initEchelle();
    		Dimension d = getSize();
    		g.setColor(c);
    		for (int ix = 0; ix<d.width; ix++) {
    			double x = minX.getVal()+ ((double) ix)/échelleX;
    			int y1;
    			if (foo.condition(x)) {
    				y1 = convertY(foo.foo(x));
    				g.fillRect(ix, y1, 1, 1);
    			}          	
    		}
    	}   	
    }

    //boolean test=false;
    public void dessinerPoint(Graphics g2, int xx, int yy, ListeCouleurs lc) {
		if (lc==null) {
			return;
		}
		if (panelCouleursBoutons!=null) {
    		//if (!panelCouleursBoutons.boutonV.isSelected()&!panelCouleursBoutons.boutonH.isSelected()&!panelCouleursBoutons.boutonD.isSelected()&!panelCouleursBoutons.boutonG.isSelected())
    		//	test=true;
    		
    		// on n'affiche que les boutons sélectionnés
    		ListeCouleurs lcBis = new ListeCouleurs(lc);
    		if (!panelCouleursBoutons.boutonDefaut.isSelected()) {
    			lcBis.retirerCouleur(0);
    		}
    		if (!panelCouleursBoutons.boutonV.isSelected()) {
    			lcBis.retirerCouleur(1);
    		}
       		if (!panelCouleursBoutons.boutonH.isSelected()) {
    			lcBis.retirerCouleur(2);
    		}
       		if (!panelCouleursBoutons.boutonD.isSelected()) {
    			lcBis.retirerCouleur(3);
    		}
       		if (!panelCouleursBoutons.boutonG.isSelected()) {
    			lcBis.retirerCouleur(4);
    		}
    		if (!lcBis.getCouleurs().equals(Color.white)) {
    			//if (test)System.out.println("lstCouleurs "+Integer.toHexString(lc.getCouleurs().getRGB())+" "+Integer.toHexString(lcBis.getCouleurs().getRGB()));
    			g2.setColor(lcBis.getCouleurs());
    			g2.fillRect(xx, yy, largeurPoint, hauteurPoint);
    		}
    	} else {
    		Color c = lc.getCouleurs();
    		if (panelCouleurs.getStatusCouleur(c)) {
    			g2.setColor(c);
    			g2.fillRect(xx, yy, largeurPoint, hauteurPoint);
    		}
    	}
    }
    
    private double xPrec;
    private double yPrec;
    private ListeCouleurs lcPrec;
    
    public void ajouterPoint(double x, double y, ListeCouleurs lc) {
    	if (lcPrec!=null && xPrec==x && yPrec==y && lcPrec.equals(lc)) return;
    	Dimension dim = getSize();
    	int yy = convertY(y);
    	int xx = convertX(x);
    	//System.out.println("PanelDessin2D.ajouterPoint "+xx+" "+yy+" "+lc.nbrCouleurs);
    	//System.out.print(".");
    	if (yy>=0 && yy<dim.height && xx>=0 && xx<dim.width) {
    		synchronized (tablePoints2D) {
    			tablePoints2D.put(Coordonnees2D.getCoordonnees2D(x,y), lc);
    		}	   
    		if (doubleBufferVisible) dessinerPoint(doubleBuffer.getGraphics(), xx, yy, lc);
    	}
    	xPrec = x;
    	yPrec = y;
    	lcPrec = lc;

    }

    public void ajouterPoint(Coordonnees2D coordonnées, ListeCouleurs lc) {
 	   Dimension dim = getSize();
 	   int yy = convertY(coordonnées.getY());
 	   int xx = convertX(coordonnées.getX());
 	   //System.out.println(" "+x+" "+y+" : "+xx+" "+yy);
 	   if (yy>=0 && yy<dim.height && xx>=0 && xx<dim.width) {
 		  synchronized (tablePoints2D) {
 	 		   tablePoints2D.put(coordonnées, lc);
 		  }
 		  if (doubleBufferVisible) dessinerPoint(doubleBuffer.getGraphics(), xx, yy, lc);
 	   }
     }

    public void setDoubleBufferVisible() {
    	doubleBufferVisible = true;
    	//System.out.println("Visible");
    	redessiner();
    }
    
    public void setDoubleBufferInvisible() {
    	doubleBufferVisible = false;
    	//System.out.println("Invisible");
    }
    
    public synchronized void raz() {
       	Dimension dim = getSize();
       	tablePoints2D = new Hashtable<Coordonnees2D, ListeCouleurs>();
       	doubleBuffer = createImage(dim.width, dim.height);
       	redessiner();
       	repaint();
    }
    
    public void saveData (BufferedWriter bw) throws IOException {
 	   for (Enumeration<Coordonnees2D> e = tablePoints2D.keys(); e.hasMoreElements();) {
 		   Coordonnees2D cle = e.nextElement();
	       ListeCouleurs lc = tablePoints2D.get(cle);
	       bw.write(""+cle.toString()+"="+lc.getCouleurs().getRGB());
	       bw.newLine();
	   }
    }   	
    
    public void readData (BufferedReader br) throws IOException {
    	raz();
    	redessiner();
    	String ligne = br.readLine();
    	while (ligne!=null) {
		       int pos1 = ligne.indexOf('=');
		       ListeCouleurs lc = new ListeCouleurs(new Color(Integer.parseInt(ligne.substring(pos1+1))));
		       Coordonnees2D coordonnees = Coordonnees2D.getCoordonnees2D(ligne.substring(0, pos1));
		       ajouterPoint(coordonnees, lc);
		       ligne= br.readLine();
    	}
    }


	public void setParametres(InDouble pasX2, InDouble pasY2, InDouble minX2, InDouble maxX2, 
			InDouble minY2, InDouble maxY2, JRadioButton afficherCourbes2, FonctionBalayage runner) {
		minX = minX2;
		maxX = maxX2;
		minY = minY2;
		maxY = maxY2;
		pasX = pasX2;
		pasY = pasY2;
		afficherCourbes = afficherCourbes2;
		this.runner = runner;
	}

	}


