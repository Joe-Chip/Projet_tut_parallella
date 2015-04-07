package balayageK2.fonctions.compteurCyclesParametriques;

import java.awt.Color;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Hashtable;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import balayageK2.CompteurCyclesParametriques;
import balayageK2.Foo;
import balayageK2.awt.PanelDessin2D;

public class LatticeMap extends CompteurCyclesParametriques {

	public LatticeMap(Hashtable<String, Object> initialisation) {
		getHashtableCalcul(initialisation);
	}
	
	public LatticeMap() {
		initObjetsAWT();
		String[] lstChoixInit = { "MyrbVal_Init=-0.1", "Val_Init=Sinus( i )/10" };
		lstChoix = new JComboBox(lstChoixInit);
		titre = new JLabel("Compteur Cycles H-V paramétrique - Lattice Map", JLabel.CENTER);
	}
	
	public LatticeMap(BufferedReader br) {
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
/*
	private double foo(double valM1, double valM2) {
		return valM1 * valM1 + b * valM2 + a;
	}

	public double fooRecur() {
		double valM = lgN[indiceItérationPrécédente][m];
		double valMm1 = lgN[indiceItérationPrécédente][m-1];
		return foo(valM, valMm1);
	}
*/
	public void calculM() {
		double valM1 = lgN[indiceItérationPrécédente][m-1];   // y[i][j-1]
		double valM2 = lgN[indiceItérationPrécédente][m]; // y[i-1][j-1]
		double valMnouveau = b * ((1.0-a) * valM1 * (1.0 - valM1) + a * valM2 * (1.0 - valM2));
		lgN[indiceItérationCourante][m] = valMnouveau;
		lgN[indiceItérationSuivante][m] = b * ((1.0-a) * valMnouveau * (1.0 - valMnouveau) + a * valMnouveau * (1.0 - valMnouveau));
	}
	
	public boolean testCycle1() {
		double valM = lgN[indiceItérationCourante][m];
		return Math.abs(lgN[indiceItérationCourante][m]-lgN[indiceItérationSuivante][m]) <= epsilonVal;
	}

	Foo fooUn = new Foo() {
		public double foo(double x) {
			return 1.0;		}			

		public boolean condition(double x) {
			return true;
		}			
	};

	Foo fooMoinsUn = new Foo() {
		public double foo(double x) {
			return -1.0;		}			

		public boolean condition(double x) {
			return true;
		}			
	};
	
	Foo fooTrois= new Foo() {
		public double foo(double x) {
			return 3.0;		}			

		public boolean condition(double x) {
			return true;
		}			
	};
	
	Foo fooFold1= new Foo() {
		public double foo(double x) {
			return -1.0 / (2.0 * x -1.0);		}			

		public boolean condition(double x) {
			return true;
		}			
	};
	
	Foo fooFold2= new Foo() {
		public double foo(double x) {
			return 1.0 / (2.0 * x -1.0);		}			

		public boolean condition(double x) {
			return true;
		}			
	};
	
	Foo fooFold3= new Foo() {
		public double foo(double x) {
			return (4.0 * x - 1.0) / (2.0 * x -1.0);		}			

		public boolean condition(double x) {
			return true;
		}			
	};
	
	Foo fooFold4= new Foo() {
		public double foo(double x) {
			return (4.0 * x - 3.0) / (2.0 * x -1.0);		}			

		public boolean condition(double x) {
			return true;
		}			
	};
	
	public void dessinerEquations(Graphics g, PanelDessin2D dessin) {
		dessin.dessinerEquation(g, Color.pink, fooUn);
		dessin.dessinerEquation(g, Color.green, fooMoinsUn);
		dessin.dessinerEquation(g, Color.green, fooTrois);
		dessin.dessinerEquation(g, Color.black, fooFold1);
		dessin.dessinerEquation(g, Color.black, fooFold2);
		dessin.dessinerEquation(g, Color.blue, fooFold3);
		dessin.dessinerEquation(g, Color.blue, fooFold4);

	}


}

