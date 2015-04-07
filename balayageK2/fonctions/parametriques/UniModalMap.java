package balayageK2.fonctions.parametriques;

import java.awt.Color;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Hashtable;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import balayageK2.CalculParametriques;
import balayageK2.Foo;
import balayageK2.awt.PanelDessin2D;

public class UniModalMap extends CalculParametriques {

	public UniModalMap(Hashtable<String, Object> initialisation) {
		getHashtableCalcul(initialisation);
	}
	
	public UniModalMap() {
		initObjetsAWT();
		String[] lstChoixInit = { "MyrbVal_Init=-0.1", "Val_Init=Sinus( i )/10" };
		lstChoix = new JComboBox(lstChoixInit);
		titre = new JLabel("paramétrique - Unimodal Map", JLabel.CENTER);
	}
	
	public UniModalMap(BufferedReader br) {
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
		double valM1 = lgN[indiceItérationPrécédente][m];   // y[i-1][j]
		double valM2 = lgN[indiceItérationPrécédente][m-1]; // y[i-1][j-1]
		//double valMnouveau = valM1 * valM1 * valM1 + b * valM2 + a;
		double valMnouveau = a * (valM2 * (valM1 + b) - valM2 * valM2);

		lgN[indiceItérationCourante][m] = valMnouveau;
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

