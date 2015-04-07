package balayageK2.rmi;

import java.awt.Color;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Vector;

import balayageK2.Main;
import balayageK2.awt.ListeCouleurs;
import balayageK2.awt.PanelDessin2D;
import balayageK2.awt.PanelDessin3D;

public class PanelDistantImpl implements PanelDistant, Serializable {
	PanelDessin2D panel;

	public PanelDistantImpl (PanelDessin2D panel) {
		this.panel = panel;
	}

	public void ajouterLstPoints2Ddistant(Vector<Double> lstX, Vector<Double> lstY, Vector<Integer>lstC)
	throws RemoteException {
		synchronized (panel.tablePoints2D) {
			for (int i=0; i<lstX.size(); i++) {
				panel.ajouterPoint(lstX.get(i), lstY.get(i), new ListeCouleurs(new Color(lstC.get(i))));
			}
		}
		panel.repaint();
	}

	public void ajouterLstPoints3Ddistant(Vector<Double> lstX, Vector<Double> lstY, Vector<Double> lstZ, Vector<Integer>lstC)
	throws RemoteException {
		synchronized (panel.tablePoints2D) {
			for (int i=0; i<lstX.size(); i++) {
				((PanelDessin3D)panel).ajouterPoint3D(lstX.get(i), lstY.get(i), lstZ.get(i), new ListeCouleurs(new Color(lstC.get(i))));
			}
		}
		panel.repaint();
	}


}
