package balayageK2.fonctions.cascade2D;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;
import java.util.Hashtable;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;

import balayageK2.CalculParametriques;
import balayageK2.CalculCascade3D;
import balayageK2.CalculCascade2D;
import balayageK2.Main;
import balayageK2.awt.PanelDessin2D;
import balayageK2.awt.PanelDessin3D;
 
public class Myrberg2D extends CalculCascade2D  {

	public Myrberg2D(Hashtable<String, Object> initialisation) {
		//Main.debug("Classe distante balayageK2.fonctions.cascade2D.Cubique2D");
		getHashtableCalcul(initialisation);
		//Main.debug("Fini distante balayageK2.fonctions.cascade2D.Cubique2D");
	}
	
	public Myrberg2D(BufferedReader br) {
		this();
		try {
			readParam(br);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public Myrberg2D() {
		initObjetsAWT();
		String[] lstChoixInit = { "MyrbVal_Init=-0.1", "Val_Init=Sinus( i )/10" };
		lstChoix = new JComboBox(lstChoixInit);
		titre = new JLabel("Feigenbaum Myrberg 2D", JLabel.CENTER);
		String[] lstChoixPlanStr = { "A", "B" };
		lstChoixPlan = new JComboBox(lstChoixPlanStr);
		}
	
	public void dessinerEquations(Graphics g, PanelDessin2D dessin) {
	}
	
	protected double valeurInit (int i) {
		if (lstChoixSelectedIndex==0) return -0.1;
		else return Math.sin((double) i) / 10.0;
	}
	
	/*
	public double foo(double valM1, double valM2) {
		return valM1 * valM1 * valM1 + b * valM2 + a;
	}
*/
	public void calculM() {
		//Main.debug("Calcul M ==> "+noItérationCourante+" "+m);
		double valM1 = lgN[indiceItérationCourante][m-1];   // y[i][j-1]
		double valM2 = lgN[indiceItérationPrécédente][m-1]; // y[i-1][j-1]
		//double valMnouveau = valM1 * valM1 * valM1 + b * valM2 + a;
		double valMnouveau;
		if (lstChoixPlanSelectedIndex==0)
			valMnouveau =  valM2 * valM2 * b + a; // A
		else 
			valMnouveau =  valM2 * valM2 * a + b; 

		lgN[indiceItérationCourante][m] = valMnouveau;
	}
		
	
	
}
