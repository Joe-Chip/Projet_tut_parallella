package balayageK2.fonctions.parametriques;

import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Hashtable;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import balayageK2.CalculParametriques;
import balayageK2.awt.PanelDessin2D;

public class Myrberg extends CalculParametriques {

	public Myrberg(Hashtable<String, Object> initialisation) {
		getHashtableCalcul(initialisation);
	}
	
	public Myrberg() {
		initObjetsAWT();
		String[] lstChoixInit = { "MyrbVal_Init=-0.1", "Val_Init=Sinus( i )/10" };
		lstChoix = new JComboBox(lstChoixInit);
		titre = new JLabel("paramétrique - Récurrence Myrberg", JLabel.CENTER);
	}
	
	public Myrberg(BufferedReader br) {
		this();
		try {
			readParam(br);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	protected double valeurInit (int i) {
		if (lstChoixSelectedIndex==0) return -0.1;
		else return Math.sin((double) i) / 10.0;
	}

	/*$debfoo$*/
	private double foo(double valM) {
		return valM * valM * b + a;
	}

	public void calculM() {
		double valM = lgN[indiceItérationPrécédente][m-1];
		double valMnouveau = valM * valM * b + a; 
		lgN[indiceItérationCourante][m] = valMnouveau;
		lgN[indiceItérationSuivante][m] = valMnouveau * valMnouveau * b + a;
	}
	/*$finfoo$*/	
	public double fooRecur() {
		double valM = lgN[1][m-1];
		return foo(valM);
	}

	public boolean testCycle1() {
		double valM = lgN[0][m];
		return Math.abs(valM-foo(valM)) <= epsilonVal;
	}
	
	public void dessinerEquations(Graphics g, PanelDessin2D dessin) {
	}

}
