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

public class Cubique2D extends CompteurCyclesParametriques {

	public Cubique2D(Hashtable<String, Object> initialisation) {
		getHashtableCalcul(initialisation);
	}
	
	public Cubique2D() {
		initObjetsAWT();
		String[] lstChoixInit = { "CubiVal_Init=-0.1", "Val_Init=Sinus( i )/10" };
		lstChoix = new JComboBox(lstChoixInit);
		titre = new JLabel("Compteur Cycles H-V paramétrique - Cubique 2d", JLabel.CENTER);
	}
	
	public Cubique2D(BufferedReader br) {
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
		//System.out.println("==>"+lstChoixSelectedIndex);
		if (lstChoixSelectedIndex==0) return -0.1;
		else return Math.sin((double) i) / 10.0;
	}

	public double foo(double valM1, double valM2) {
		return valM1 * valM1 * valM1 + b * valM2 + a;
	}

	public void calculM() {
		double valM1 = lgN[indiceItérationPrécédente][m-1];// x
		double valM2 = lgN[indiceItérationPrécédente][m]; //y 
		double valMnouveau = valM1 * valM1 * valM1 + b * valM2 + a;
		lgN[indiceItérationCourante][m] = valMnouveau;
		lgN[indiceItérationSuivante][m] = valMnouveau * valMnouveau * valMnouveau + b * valMnouveau + a;
	}
	
	public double fooRecur() {
		double valM1 = lgN[0][m-1];
		double valM2 = lgN[1][m-1];
		return foo(valM1, valM2);
	}

	public boolean testCycle1() {
		double valM = lgN[0][m];
		return Math.abs(valM-foo(valM, valM)) <= epsilonVal;
	}

	public void dessinerEquations(Graphics g, PanelDessin2D dessin) {
		Foo foo = new Foo() {
			public double foo(double x) {
				double sqrtX;
	           	sqrtX = Math.pow(2.0 * x * x, 1.0/3.0);
				return 1.0d-3.0/2.0*sqrtX;
			}

			public boolean condition(double x) {
				return true;
			}			
		};
		
		dessin.dessinerEquation(g, Color.red, foo);
		
		Foo foo2 = new Foo() {
			public double foo(double x) {
				double sqrtX;
	           	sqrtX = Math.pow(2.0 * x * x, 1.0/3.0);
				return -(2.0 +3.0/2.0*sqrtX);
			}

			public boolean condition(double x) {
				return true;
			}			
		};
		
		dessin.dessinerEquation(g, Color.green, foo2);

		Foo foo3 = new Foo() {
			public double foo(double x) {
				double alpha = 8.0+27.0 * x*x -3.0*Math.sqrt(3.0)*x*Math.sqrt(16.0+27.0*x*x);
				double rac3 = Math.pow(alpha, 1.0/3.0);
				return 1.0-2.0/rac3 -0.5*rac3;
			}

			public boolean condition(double x) {
				return true;
			}			
		};
		
		dessin.dessinerEquation(g, Color.blue, foo3);
		
		foo3 = new Foo() {
			public double foo(double x) {
				double alpha = 827.0 * x*x -3.0*Math.sqrt(48.0*x*x + 81.0 * x*x*x*x);
				double rac3 = Math.pow(alpha, 1.0/3.0);
				return 1.0-0.5*rac3 - 2.0/rac3;
			}

			public boolean condition(double x) {
				return true;
			}			
		};
		
		dessin.dessinerEquation(g, Color.blue, foo3);
		// ===================================================================

		/*
		dessin.dessinerEquation(g, Color.orange, new Foo() {
			public double foo(double x) {
				return (4.0 * Math.sqrt(-3.0 * x + 3.0))/9.0;}
			public boolean condition(double x) {return x<=1.0;}	});
		
		dessin.dessinerEquation(g, Color.orange, new Foo() {
			public double foo(double x) {
				return (-4.0 * Math.sqrt(-3.0 * x + 3.0))/9.0;}
			public boolean condition(double x) {return x<=1.0;}	});
		
		dessin.dessinerEquation(g, Color.orange, new Foo() {
			public double foo(double x) {
				return (4.0 * Math.sqrt(+3.0 * x - 3.0))/9.0;}
			public boolean condition(double x) {return x>=1.0;}	});
		
		dessin.dessinerEquation(g, Color.orange, new Foo() {
			public double foo(double x) {
				return (-4.0 * Math.sqrt(+3.0 * x - 3.0))/9.0;}
			public boolean condition(double x) {return x>=1.0;}	});
		*/
		
		dessin.dessinerEquation(g, Color.black, new Foo() {
			public double foo(double x) {
				return -(1.5 * Math.pow(2.0 * x*x, 1.0/3.0)+2.0);}
			public boolean condition(double x) {return true;}	});

		dessin.dessinerEquation(g, Color.black, new Foo() {
			public double foo(double x) {
				double z = Math.pow(-4.0 * x+2.0*Math.sqrt(5.0)*x, 1.0/3.0);
				return (-6.0*x+8.0*z+6.0*Math.sqrt(5.0)*x)/(z*8.0);}
			public boolean condition(double x) {return x>0.0;}	});

		dessin.dessinerEquation(g, Color.black, new Foo() {
			public double foo(double x) {
				double z = Math.pow(-4.0 * x-2.0*Math.sqrt(5.0)*x, 1.0/3.0);
				return (-6.0*x+8.0*z-6.0*Math.sqrt(5.0)*x)/(z*8.0);}
			public boolean condition(double x) {return x<0.0;}	});

	}

}
