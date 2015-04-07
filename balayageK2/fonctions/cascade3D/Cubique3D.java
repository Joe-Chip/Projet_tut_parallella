package balayageK2.fonctions.cascade3D;

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
import balayageK2.awt.PanelDessin2D;
import balayageK2.awt.PanelDessin3D;

public class Cubique3D extends CalculCascade3D  {

	public Cubique3D(Hashtable<String, Object> initialisation) {
		//System.out.println("Classe distante balayageK2.fonctions.cascade2D.Cubique2D");
		getHashtableCalcul(initialisation);
		//System.out.println("Fini distante balayageK2.fonctions.cascade2D.Cubique2D");
	}
	
	public Cubique3D(BufferedReader br) {
		this();
		try {
			readParam(br);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public Cubique3D() {
		initObjetsAWT();
		String[] lstChoixInit = { "MyrbVal_Init=-0.1", "Val_Init=Sinus( i )/10" };
		lstChoix = new JComboBox(lstChoixInit);
		titre = new JLabel("Cascade 3D", JLabel.CENTER);
		String[] lstChoixPlanStr = { "A", "B" };
		lstChoixPlan = new JComboBox(lstChoixPlanStr);
	}
	
	
	protected double valeurInit (int i) {
		if (lstChoixSelectedIndex==0) return -0.1;
		else return Math.sin((double) i) / 10.0;
	}

/*
	private double foo(double valM) {
		return valM * valM * b + a;
	}
	
	public boolean testCycle1() {
		double valM = lgN[0][m];
		return Math.abs(valM-foo(valM)) <= epsilon;
	}
	*/

	
	public void dessinerEquations(Graphics g, PanelDessin2D dessin) {
	}
	
//==========================
	
	public double foo(double valM1, double valM2) {
		return valM1 * valM1 * valM1 + b * valM2 + a;
	}

	public void calculM() {
		//System.out.println("Calcul M ==> "+noItérationCourante+" "+m);
		double valM1 = lgN[indiceItérationCourante][m-1];   // y[i][j-1]
		double valM2 = lgN[indiceItérationPrécédente][m-1]; // y[i-1][j-1]
		//double valMnouveau = valM1 * valM1 * valM1 + b * valM2 + a;
		double valMnouveau;
		if (lstChoixPlanSelectedIndex==0)
			valMnouveau =  valM2 * valM2 * valM2 + b * valM1 + a; // A
		else 
			valMnouveau =  valM2 * valM2 * valM2 + a * valM1 + b; 

		lgN[indiceItérationCourante][m] = valMnouveau;
	}
		
//==========================
	
	
	
}
