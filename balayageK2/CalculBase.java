package balayageK2;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.event.ChangeListener;

import balayageK2.awt.AppletDessin;
import balayageK2.awt.ListeCouleurs;
import balayageK2.awt.PanelCommande;
import balayageK2.awt.PanelDessinBase;
import balayageK2.rmi.PanelDistant;

public abstract class CalculBase implements FonctionBalayage,  ActionListener, ChangeListener, Runnable {
	protected AppletDessin applet;
	public PanelDistant panelDessinDistantMaitre;
	public PanelDistant panelDessinDistant;
	public PanelDessinBase panelDessinBase;
	public Vector<Double> lstPtsX;
	public Vector<Double> lstPtsY;
	public Vector<Double> lstPtsZ;
	public Vector<Integer> lstPtsC;
	
	public boolean arrêtRunner = false;
	Thread threadCalcul;
	protected PanelCommande panelCommande;

	private void startCalcul() {
		//System.out.println("==> GO");
		arrêtRunner = false;
		threadCalcul = new Thread(this);
		//threadCalcul.setPriority(Thread.currentThread().getPriority()+4);
		threadCalcul.start();
	}
	
	public void stopCalcul() {
		//stopPrintCycleMaintenant();
		arrêtRunner = true;
		try {
			if (applet.serveurPrincipal!=null) applet.serveurPrincipal.arretCalculs();
			//System.out.println("on attend le join() "+threadCalcul);
			if (threadCalcul!=null) threadCalcul.join();
			//System.out.println("Done");
		} catch (InterruptedException e) {
		}
		threadCalcul = null;
		panelCommande.ajouterBoutonGo();
	}

	public void actionPerformed(ActionEvent arg0) {
		//System.out.println("Action Performed => "+arg0.getActionCommand());
		if (arg0.getActionCommand().equals("créer jpg")) {
			try {
				applet.saveImage();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (arg0.getActionCommand().equals("restauration données")) {
			applet.restaureData();
		} else if (arg0.getActionCommand().equals("sauvegarde données")) {
				saveData();
		}
		/*
 else if (arg0.getActionCommand().equals("print Cycle")) {
				printCycle();
		} 
		*/
		else if (arg0.getActionCommand().equals("go"))  {
			startCalcul();
		} else if (arg0.getActionCommand().equals("stop"))  {
			stopCalcul();
		}
	}
	
	public abstract void saveData();
	
	/* partie rmi */
	
	public void calculPar(Hashtable<String, Object> initialisation) {
		initialisation.put("NomClasse", this.getClass().getName());
		applet.serveurPrincipal.addCalcul(initialisation);
	}

	public void attendreInactivité() {
		if (applet.serveurPrincipal!=null)
			applet.serveurPrincipal.attenteFinCalcul(this);
	}
	
	protected void envoyerLstPointsDifférés2D() {
		if (panelDessinDistant!=null) try {
			//Main.debug("Envoie "+a);
			panelDessinDistant.ajouterLstPoints2Ddistant(lstPtsX, lstPtsY, lstPtsC);
			//System.out.println("Fin Envoie "+a);
		} catch (RemoteException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	protected void envoyerLstPointsDifférés3D() {
		if (panelDessinDistant!=null) try {
			//System.out.println("Envoie "+a);
			panelDessinDistant.ajouterLstPoints3Ddistant(lstPtsX, lstPtsY, lstPtsZ, lstPtsC);
			//System.out.println("Fin Envoie "+a);
		} catch (RemoteException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	protected void différerPoint2D(double x, double y, Color c) {
		if (panelDessinBase.dansZoneAffichage(x, y)) {
			lstPtsX.add(x);
			lstPtsY.add(y);
			lstPtsC.add(c.getRGB());
		}
	}
	
    private double xPrec;
    private double yPrec;
    private ListeCouleurs lcPrec;
	protected void différerPoint2D(double x, double y, ListeCouleurs lc) {
		if (lcPrec!=null && xPrec==x && yPrec==y && lcPrec.equals(lc)) return;
		if (panelDessinBase.dansZoneAffichage(x, y)) {
			lstPtsX.add(x);
			lstPtsY.add(y);
			lstPtsC.add(lc.nbrCouleurs);
		}
		xPrec = x;
		yPrec = y;
		lcPrec = lc;
	}
	
	protected void différerPoint3D(double x, double y, double z, Color c) {
		if (panelDessinBase.dansZoneAffichage(x, y, z)) {
			lstPtsX.add(x);
			lstPtsY.add(y);
			lstPtsZ.add(z);
			lstPtsC.add(c.getRGB());
		}
	}
}
