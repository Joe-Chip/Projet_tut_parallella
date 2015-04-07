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

public class BalayageK2 extends CompteurCyclesParametriques {

	public BalayageK2(Hashtable<String, Object> initialisation) {
		getHashtableCalcul(initialisation);
	}
	
	public BalayageK2() {
		initObjetsAWT();
		String[] lstChoixInit = { "MyrbVal_Init=-0.1", "Val_Init=Sinus( i )/10" };
		lstChoix = new JComboBox(lstChoixInit);
		titre = new JLabel("Compteur Cycles H-V paramétrique - Balayage K2", JLabel.CENTER);
	}
	
	public BalayageK2(BufferedReader br) {
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

	private double foo(double valM1, double valM2) {
		return valM1 * valM1 + b * valM2 + a;
	}

	public double fooRecur() {
		double valM = lgN[indiceItérationPrécédente][m];
		double valMm1 = lgN[indiceItérationPrécédente][m-1];
		return foo(valM, valMm1);
	}

	public void calculM() {
		double valM = lgN[indiceItérationPrécédente][m];
		double valMm1 = lgN[indiceItérationPrécédente][m-1];
		double valMnouveau = valM * valM + b * valMm1 + a; // foo(valM, valMm1);
		lgN[indiceItérationCourante][m] = valMnouveau;
		lgN[indiceItérationSuivante][m] = valMnouveau * valMnouveau + b * valMnouveau + a;
	}
	
	public boolean testCycle1() {
		double valM = lgN[indiceItérationCourante][m];
		return Math.abs(lgN[indiceItérationCourante][m]-lgN[indiceItérationSuivante][m]) <= epsilonVal;
	}

	public void dessinerEquations(Graphics g, PanelDessin2D dessin) {
		Foo fooPos1 = new Foo() {
			public double foo(double x) {
	           	return 1.0d+2.0*Math.sqrt(x);
			}

			public boolean condition(double x) {
				return x>=0.0;
			}			
		};
		
		Foo fooNeg1 = new Foo() {
			public double foo(double x) {
	           	return 1.0d-2.0*Math.sqrt(x);
			}			

			public boolean condition(double x) {
				return x>=0.0;
			}			
		};
		dessin.dessinerEquation(g, Color.black, fooPos1);
		dessin.dessinerEquation(g, Color.black, fooNeg1);

		Foo fooPos2 = new Foo() {
			public double foo(double x) {
	           	return 1.0d+2.0*Math.sqrt(1.0+x);
			}

			public boolean condition(double x) {
				return x>=-1.0;
			}			
		};
		
		Foo fooNeg2 = new Foo() {
			public double foo(double x) {
	           	return 1.0d-2.0*Math.sqrt(1.0+x);
			}			

			public boolean condition(double x) {
				return x>=-1.0;
			}			
		};

		dessin.dessinerEquation(g, Color.blue.darker(), fooPos2);
		dessin.dessinerEquation(g, Color.blue.darker(), fooNeg2);
		
		Foo fooPos3 = new Foo() {
			public double foo(double x) {
	           	return 1.0d+Math.sqrt(6.0+4.0*x);
			}			

			public boolean condition(double x) {
				return x>=-1.5;
			}			
		};

		Foo fooNeg3 = new Foo() {
			public double foo(double x) {
	           	return 1.0d-Math.sqrt(6.0+4.0*x);
			}			

			public boolean condition(double x) {
				return x>=-1.5;
			}			
		};

		dessin.dessinerEquation(g, new Color(120, 102, 97), fooPos3);
		dessin.dessinerEquation(g, new Color(120, 102, 97), fooNeg3);
		
		fooPos1 = new Foo() {
			public double foo(double x) {
	           	return -1.0d+2.0*Math.sqrt(x);
			}

			public boolean condition(double x) {
				return x>=0.0;
			}			
		};
		
		fooNeg1 = new Foo() {
			public double foo(double x) {
	           	return -1.0d-2.0*Math.sqrt(x);
			}			

			public boolean condition(double x) {
				return x>=0.0;
			}			
		};
		dessin.dessinerEquation(g, Color.red.brighter(), fooPos1);
		dessin.dessinerEquation(g, Color.red.brighter(), fooNeg1);

		fooPos1 = new Foo() {
			public double foo(double x) {
	           	return (5.0d+2.0*Math.sqrt(4.0-3.0*x))/3.0;
			}

			public boolean condition(double x) {
				return x<=4.0/3.0;
			}			
		};
		
		fooNeg1 = new Foo() {
			public double foo(double x) {
				return (5.0d-2.0*Math.sqrt(4.0-3.0*x))/3.0;
			}			

			public boolean condition(double x) {
				return x<=4.0/3.0;
			}			
		};
		dessin.dessinerEquation(g, Color.blue.brighter(), fooPos1);
		dessin.dessinerEquation(g, Color.blue.brighter(), fooNeg1);

		fooPos1 = new Foo() {
			public double foo(double x) {
	           	return 1.0d+(2.0*Math.sqrt(-3.0*x))/3.0;
			}

			public boolean condition(double x) {
				return x<=0.0;
			}			
		};
		
		fooNeg1 = new Foo() {
			public double foo(double x) {
				return 1.0d-(2.0*Math.sqrt(-3.0*x))/3.0;
			}			

			public boolean condition(double x) {
				return x<=0.0;
			}			
		};
		dessin.dessinerEquation(g, Color.green, fooPos1);
		dessin.dessinerEquation(g, Color.green, fooNeg1);
		
		fooPos1 = new Foo() {
			public double foo(double x) {
	           	return (1.0d+2.0*Math.sqrt(1.0-3.0*x))/3.0;
			}

			public boolean condition(double x) {
				return x<=1.0/3.0;
			}			
		};
		
		fooNeg1 = new Foo() {
			public double foo(double x) {
				return (1.0d-2.0*Math.sqrt(1.0-3.0*x))/3.0;
			}			

			public boolean condition(double x) {
				return x<=1.0/3.0;
			}			
		};
		dessin.dessinerEquation(g, new Color(147, 109, 173), fooPos1);
		dessin.dessinerEquation(g, new Color(147, 109, 173), fooNeg1);

		fooPos1 = new Foo() {
			public double foo(double x) {
	           	return -1.0d+(2.0*Math.sqrt(-3.0*x))/3.0;
			}

			public boolean condition(double x) {
				return x<0.0;
			}			
		};
		
		fooNeg1 = new Foo() {
			public double foo(double x) {
				return -1.0d-(2.0*Math.sqrt(-3.0*x))/3.0;
			}			

			public boolean condition(double x) {
				return x<0.0;
			}			
		};
		dessin.dessinerEquation(g, Color.pink, fooPos1);
		dessin.dessinerEquation(g, Color.pink, fooNeg1);


	}

}
