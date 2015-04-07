package balayageK2;

import java.awt.Graphics;
import java.io.BufferedReader;
import java.util.Hashtable;

import javax.swing.JPanel;

import balayageK2.awt.AppletDessin;
import balayageK2.awt.PanelDessin2D;

public interface FonctionBalayage {
	public JPanel getPanel(AppletDessin applet);
	public void calcul();
	public void stopCalcul();
	public void readData (BufferedReader br);
	public void dessinerEquations(Graphics g, PanelDessin2D dessin);
	public void getHashtableCalcul(Hashtable<String, Object> initialisation);
}
