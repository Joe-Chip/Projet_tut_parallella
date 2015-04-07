package balayageK2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import balayageK2.awt.AppletDessin;
import balayageK2.awt.Coordonnees2D;
import balayageK2.awt.Coordonnees3D;
import balayageK2.awt.ListeCouleurs;
import balayageK2.awt.PanelCommande;
import balayageK2.awt.PanelCouleurs;
import balayageK2.awt.PanelCouleursBoutons;
import balayageK2.awt.PanelDessin2D;
import balayageK2.awt.InDouble;
import balayageK2.awt.InInteger;
import balayageK2.awt.PanelDessin3D;
import balayageK2.awt.PanelDessinBase;
import balayageK2.awt.PanelHTML;
import balayageK2.awt.PanelTexte;
import balayageK2.rmi.PanelDistant;
import balayageK2.rmi.PanelDistantImpl;

public abstract class CalculCascade2D extends CalculBase {
	protected InDouble minX;
	protected InDouble maxX;
	protected InDouble pasX;
	protected InDouble minY;
	protected InDouble maxY;
	protected InDouble pasY;
	protected InDouble valB;
	protected JComboBox lstChoixPlan;
	//protected InDouble epsilon = new InDouble("epsilon", 1E-10, 8);
	protected InDouble epsilon;
	protected InInteger nbrIter;
	protected JComboBox lstChoix;
	protected JLabel titre;
	protected JPanel contenu;
	protected PanelDessin2D panelDessin;
	
	protected PanelCouleursBoutons panelCouleursBoutons; /* Août 2010 */
	protected PanelCouleurs panelCouleurs;
	PanelTexte panelPerf;
	JTabbedPane tabbedPane;
	
	//public JRadioButton afficherCourbes = new JRadioButton("afficher Courbes", true);

	protected double a;
	protected double b;
	protected double epsilonVal;
	protected double nbrIterVal;
	protected int m;
	protected byte ordreCycle;
	protected int noItérationCourante;
	protected int indiceItérationSuivante;
	protected int indiceItérationCourante;
	protected int indiceItérationPrécédente;
	protected int nMax;
	protected int mMax;
	protected int lstChoixSelectedIndex;
	protected int lstChoixPlanSelectedIndex;
	
	public String[] lstChoixInit;
	protected int choixListeInit;
	public boolean[] cycleImprimé;
	protected int ctrCycleRestantAImprimé = 0;
	
	public int nombreLignes = 8; // il faut nombreLignes<cycleMax+2 et nbrlignes soit une puissance de 2 !
	public int masqueIndiceLigne = nombreLignes-1;
	
	protected double[][] lgN;
	protected double[] valInit;
	private int ctrV; // ctrCycleVertical
	private int ctrH; // ctrCycleHorizontal
	private int ctrD; // ctrCycleDiagonal
	private int ctrG; // ctrCycleGeneral
	
	public abstract void calculM ();
	protected abstract double valeurInit (int i);
	public abstract void dessinerEquations(Graphics g, PanelDessin2D dessin);
		
	protected void initObjetsAWT() {
		minX = new InDouble("min X", -1.0d); // -2.0d
		maxX = new InDouble("max X", +1.0d); //+2.0d
		pasX = new InDouble("pas X", 0, 8);//0.004d, 8);
		minY = new InDouble("min Y", -2.0d);
		maxY = new InDouble("max Y", +2.0d);
		pasY = new InDouble("pas Y", 0, 8);
		valB = new InDouble("B", -0.261, 8);
		epsilon = new InDouble("epsilon", 1E-10, 8);
		nbrIter = new InInteger("Iterations", 30);
		contenu = new JPanel();
		panelDessin = new PanelDessin2D();
		Main.debug("panel="+panelDessin.hashCode());
		panelCouleursBoutons = new PanelCouleursBoutons(panelDessin);/* Août 2010 */
		panelCouleurs = new PanelCouleurs(panelDessin);
	}
	
	public void getHashtableCalcul(Hashtable<String, Object> initialisation) {
		a = (Double) initialisation.get("a");
		b = (Double) initialisation.get("b");
		nombreLignes = (Integer) initialisation.get("nombreLignes");
		mMax = (Integer) initialisation.get("mMax");
		lgN = new double[nombreLignes][mMax];
		lstChoixSelectedIndex = (Integer) initialisation.get("lstChoixSelectedIndex");
		nMax = (Integer) initialisation.get("nMax");
		valInit = new double[nMax];
		for (int n=0; n<nMax; n++) {
			valInit[n] = valeurInit(n);
		}
		lstChoixPlanSelectedIndex = (Integer) initialisation.get("lstChoixPlanSelectedIndex");
		panelDessinDistant = (PanelDistant) initialisation.get("panelDessinDistant");
		lstPtsX = new Vector<Double>();
		lstPtsY = new Vector<Double>();
		lstPtsC = new Vector<Integer>();
		epsilonVal = (Double) initialisation.get("epsilonVal");
		nbrIterVal = (Double) initialisation.get("nbrIterVal");
		if (panelDessinBase==null) panelDessinBase = new PanelDessinBase();
		panelDessinBase.getHashtableCalcul(initialisation);
	}
	
	protected void putHashtableCalcul(Hashtable<String, Object> initialisation) {
		panelDessin.putHashtableCalcul(initialisation);
		initialisation.put("a", a);
		initialisation.put("b", b);
		initialisation.put("nombreLignes", nombreLignes);
		initialisation.put("nMax", nMax);
		initialisation.put("mMax", mMax);
		initialisation.put("lstChoixPlanSelectedIndex", lstChoixPlanSelectedIndex);
		initialisation.put("lstChoixSelectedIndex", lstChoixSelectedIndex);
		initialisation.put("nbrIterVal", nbrIterVal);
		initialisation.put("epsilonVal", epsilonVal);
		try {
			if (panelDessinDistantMaitre==null) panelDessinDistantMaitre = (PanelDistant) UnicastRemoteObject.exportObject(new PanelDistantImpl(panelDessin), 0);
			initialisation.put("panelDessinDistant", panelDessinDistantMaitre);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	public void checkParam() {
		minX.checkVal();
		maxX.checkVal();
		pasX.checkVal();
		if (pasX.getVal()==0.0) {
			pasX.setVal((maxX.getVal()-minX.getVal())/panelDessin.getWidth());
		}
		minY.checkVal();
		maxY.checkVal();
		valB.checkVal();
		//pasY.checkVal();
		epsilon.checkVal();
		nbrIter.checkVal();
	}
	
	public static long ctrCalculs;
	public void run() {
		//System.out.println("==> Run");
		Date dateDebut = new Date();
		long ctrAppel = 0l;
		Coordonnees2D.ctr2D = 0L;
		ctrCalculs = 0;
		checkParam();
		panelCommande.enleverBoutonGo();
		panelDessin.raz();
		panelDessin.redessiner();
		//panelTexte.raz();
		epsilonVal = epsilon.getVal();
		nbrIterVal = nbrIter.getVal();
		nMax = nbrIter.getVal();
		mMax = nMax;
		arrêtRunner = false;
		lstChoixPlanSelectedIndex = lstChoixPlan.getSelectedIndex();
		lstChoixSelectedIndex = lstChoix.getSelectedIndex();
		lgN = new double[nombreLignes][mMax];
		valInit = new double[nMax];
		
		for (int n=0; n<nMax; n++) {
			valInit[n] = valeurInit(n);
		}
		
		boucleFor: for (double a=minX.getVal(); 
			a<=maxX.getVal(); 
			a=a+pasX.getVal()) {
			b = valB.getVal();
			ctrAppel++;
			this.a = a;
			if (applet.serveurPrincipal==null) calcul();
			else {
				Hashtable<String, Object> initialisation = new Hashtable<String, Object>();
				putHashtableCalcul(initialisation);
				calculPar(initialisation);
			}
			if (arrêtRunner) break boucleFor;
			if (panelPerf!=null) {
				panelPerf.raz();
				double nbrAppelsRestants = (maxX.getVal()-a)/pasX.getVal();
				panelPerf.print(AppletDessin.textePerf(dateDebut, ctrAppel, ctrCalculs, nbrAppelsRestants));
			}
			panelDessin.repaint();
		}
		/*
		if (afficherCourbes.isSelected())
			panelDessin.dessinerLesAxes(panelDessin.getGraphics());
			*/

		attendreInactivité();
		String txtPerf = AppletDessin.textePerf(dateDebut, ctrAppel, ctrCalculs, 0.0);
		if (panelPerf!=null) {
			panelPerf.raz();
			panelPerf.print(txtPerf);
		}
		System.out.println(txtPerf);
		panelCommande.ajouterBoutonGo();
	}

	boolean egalEpsilonPres(double x, double y) {
		return (Math.abs(x-y) <= epsilonVal);
	}
	
	boolean differentEpsilonPres(double x, double y) {
		return (Math.abs(x-y) > epsilonVal);
	}
	
	public void calcul() {
		//System.out.println("==> "+a);
		boolean cycleV = false;
		boolean cycleH = false;
		boolean cycleD = false;
		boolean cycleNouv = false;
		ordreCycle = 0;
		// initialisation itération 0
		for (int m=0; m<mMax; m++) {
			lgN[0][m] = valInit[m];
		}
		// itération 2..nMax
		indiceItérationCourante = 0;
		indiceItérationSuivante = 1;
		indiceItérationPrécédente = -1;
		int indiceItérationAvantPrécédente = -1;

		/*	
pour k=1 to nbr_max
 pour i=1 to n
  pour j=1 to n
  calculer y(i,j)
 tq cycle et no gal à 2 on plot en noir
  si cycl=2 
  on test si V (on plot en vert), H (on plot en vert),D (on plot en rouge)  ou G (on plot en jaune)
fin si
fin tq
fin j
fin i
fin k
		 */

		for (int k=1; k<=100; k++) {
			for (int n=1; n<nMax; n++) {
				noItérationCourante = n;
				indiceItérationPrécédente= indiceItérationCourante;
				indiceItérationCourante =  indiceItérationSuivante;
				indiceItérationSuivante = (n+1) & masqueIndiceLigne;
				lgN[indiceItérationCourante][0] = valInit[n];
				for (int j=1; j<mMax; j++) { /* boucle j */
					//System.out.println("Calcul ==> "+k+" "+n+" "+m+" "+lgN[indiceItérationCourante][m]);
					if (arrêtRunner) return;
					this.m = j;
					//lgN[indiceItérationCourante][m] = fooRecur();
					calculM();
					//System.out.println("Point ==> "+a+" "+lgN[indiceItérationCourante][m]+" "+lgN[indiceItérationPrécédente][m-1]);
					//System.out.println("Point ==> "+a+" "+lgN[indiceItérationCourante][m]);
					if (!Double.isNaN(lgN[indiceItérationCourante][j]) &&
							!Double.isInfinite(lgN[indiceItérationCourante][j])) {

						// Rajout août 2010
						// calcul des cycles H et V
						ListeCouleurs lc = new ListeCouleurs(5);
						lc.ajouterCouleur(0);
						//if (panelDessinDistant==null) panelDessin.ajouterPoint(a, lgN[indiceItérationCourante][m], Color.green);
						//Main.debug("Point "+a+" "+lgN[indiceItérationCourante][j]);
						if (panelDessinDistant==null) {
							panelDessin.ajouterPoint(a, lgN[indiceItérationCourante][j], lc);
						}
						else {
							différerPoint2D(a, lgN[indiceItérationCourante][j], lc);//Color.red);
						}
						for (int m=2; m<mMax; m++) {
							int cycle = 2; // cycle 2 uniquement
							if (
									(Math.abs(lgN[indiceItérationCourante][m]-lgN[indiceItérationSuivante][m]) > epsilonVal) &&
									(Math.abs(lgN[indiceItérationCourante][m]-lgN[(noItérationCourante-cycle) & masqueIndiceLigne][m]) <= epsilonVal) &&
									(Math.abs(lgN[indiceItérationCourante][m]-lgN[indiceItérationCourante][m-cycle]) <= epsilonVal)) {
								// c'est un cycle 2.
								// Test cycle vertical
								if (differentEpsilonPres(lgN[indiceItérationCourante][m],lgN[indiceItérationCourante][m-1]) &&
										egalEpsilonPres(lgN[indiceItérationCourante][m], lgN[indiceItérationPrécédente][m]) &&
										egalEpsilonPres(lgN[indiceItérationCourante][m-1], lgN[indiceItérationPrécédente][m-1])) {
									cycleV = true;
								}
								// test cycle horizontal
								if (differentEpsilonPres(lgN[indiceItérationCourante][m], lgN[indiceItérationPrécédente][m]) &&
										egalEpsilonPres(lgN[indiceItérationCourante][m], lgN[indiceItérationCourante][m-1]) &&
										egalEpsilonPres(lgN[indiceItérationPrécédente][m], lgN[indiceItérationPrécédente][m-1])) {
									cycleH = true;
								}
								// test cycle diagonal
								if (differentEpsilonPres(lgN[indiceItérationCourante][m], lgN[indiceItérationPrécédente][m]) &&
										egalEpsilonPres(lgN[indiceItérationCourante][m], lgN[indiceItérationPrécédente][m-1]) &&
										egalEpsilonPres(lgN[indiceItérationCourante][m-1], lgN[indiceItérationPrécédente][m])) {
									cycleD = true;
								}
								// test cycle Nouv
								if (indiceItérationAvantPrécédente!=-1 &&
										differentEpsilonPres(lgN[indiceItérationCourante][m], lgN[indiceItérationCourante][m-1]) && // y1#x1
										differentEpsilonPres(lgN[indiceItérationCourante][m-1], lgN[indiceItérationPrécédente][m-1]) && // x1#x2
										differentEpsilonPres(lgN[indiceItérationCourante][m-1], lgN[indiceItérationPrécédente][m]) && // x1#y2
										differentEpsilonPres(lgN[indiceItérationCourante][m], lgN[indiceItérationPrécédente][m-1]) && // y1#x2
										differentEpsilonPres(lgN[indiceItérationCourante][m], lgN[indiceItérationPrécédente][m]) && // y1#y2
										differentEpsilonPres(lgN[indiceItérationPrécédente][m-1], lgN[indiceItérationPrécédente][m]) && // x2#y2
										Math.abs(lgN[indiceItérationCourante][m-1]-lgN[indiceItérationAvantPrécédente][m-1]) <= epsilonVal &&
										Math.abs(lgN[indiceItérationCourante][m]-lgN[indiceItérationAvantPrécédente][m]) <= epsilonVal) {
									cycleNouv = true;
								}
								//ain.debug("==> ctrV="+ctrV+" ctrH="+ctrH+" ctrD="+ctrD+" cycleNouv="+cycleNouv);
								if (cycleV) ctrV++;
								if (cycleH) ctrH++;
								if (cycleD) ctrD++;
								if (cycleNouv) ctrG++;

								if (cycleV) lc.ajouterCouleur(1);
								if (cycleH) lc.ajouterCouleur(2);
								if (cycleD) lc.ajouterCouleur(3);
								if (cycleNouv) lc.ajouterCouleur(4);
							}

						}
					}
					//panelDessin.ajouterPoint(a, lgN[indiceItérationCourante][m], Color.red);
					//System.out.println("Point ==> "+a+" "+lgN[indiceItérationCourante][m]+" "+lgN[indiceItérationPrécédente][m-1]);
					ctrCalculs++;
				}

			}
		}	
		/* fin ajout */
		envoyerLstPointsDifférés2D();
	}

	public void printTraceCycleDirect(int cycle, int m) {
		System.out.print("Cycle "+cycle+" : a="+a+" b="+b+" -->");
		for (int noCy=0; noCy<cycle; noCy++) {
			System.out.print(" "+lgN[noCy][m]);
		}
		System.out.println();
		//cycleImprimé[cycle-1]=true;
		//ctrCycleRestantAImprimé--;
		if (ctrCycleRestantAImprimé==0)
			System.out.println("------------------------------------------------");
	}
	
	public PanelDessin2D créerDessin() {
		return new PanelDessin2D();
	}
	
	public String getNomFonction (){ 
		return "?";
		}

	public JPanel getPanel(AppletDessin applet) {		
		this.applet = applet;
		JPanel panelParam = new JPanel();
		GridLayout gridLayout = new GridLayout();
		gridLayout.setRows(2);
		gridLayout.setHgap(2);
		gridLayout.setVgap(2);
		gridLayout.setColumns(4);
		panelParam.setLayout(gridLayout);
		panelParam.setSize(800, 40);
		panelParam.add(minX);
		panelParam.add(maxX);
		panelParam.add(pasX);
		panelParam.add(epsilon);
		panelParam.add(nbrIter);
		panelParam.add(minY);
		panelParam.add(maxY);
		//panelParam.add(pasY);
		JPanel jp = new JPanel();
		jp.add(lstChoix);
		lstChoixPlan.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (lstChoixPlan.getSelectedIndex()==0) {
					valB.changeLabel("B");
				} else {
					valB.changeLabel("A");
				}
			}	
		});
		jp.add(lstChoixPlan);
		panelParam.add(jp);
		panelParam.add(valB);
		//panelParam.add(afficherCourbes);
		
		JPanel panelComplet = new JPanel();
		BorderLayout bl = new BorderLayout();
		panelComplet.setLayout(new BorderLayout());
		JPanel pt = new JPanel();
		pt.setLayout(bl);
		titre.setFont(new Font("Serif", Font.BOLD, 36));
		titre.setForeground(Color.blue);
		bl.setVgap(5);
		pt.add(titre, BorderLayout.NORTH);
		pt.add(new JLabel(" "), BorderLayout.SOUTH);
		panelComplet.add(pt, BorderLayout.NORTH);
		panelCouleurs.boutonChoixNbreCycles.setSelectedIndex(3);// pour 4 cycles max
		JPanel bas = new JPanel();
		bas.setLayout(new BorderLayout());
		//bas.add(panelCouleurs, BorderLayout.WEST);
		bas.add(panelCouleursBoutons, BorderLayout.WEST); /* Août 2010 */
		JPanel panelContenu = new JPanel();
		panelContenu.setLayout(new BorderLayout());
		panelDessin.setParametres(pasX, pasY, minX, maxX, minY, maxY, null, this);
		panelDessin.modePointLargeurFixe = true;
		panelCommande = new PanelCommande(applet.boutonMainHTML);
		panelCommande.addActionListener(this);
		bas.add(panelCommande, BorderLayout.CENTER);
		panelContenu.add(panelParam, BorderLayout.NORTH);
		tabbedPane = new JTabbedPane();
		tabbedPane.insertTab("Dessin", null, panelDessin, null, 0);
		String nomFichHelp = "HTML/"+this.getClass().getName()+".html";
		//System.out.println("==>"+ClassLoader.getSystemResource(nomFichHelp)+" "+nomFichHelp);
		if (ClassLoader.getSystemResource(nomFichHelp)!=null) 
			tabbedPane.insertTab("Aide", null, new PanelHTML(nomFichHelp), null, 1);
		if (AppletDessin.avecPerf) {
			panelPerf = new PanelTexte();
			String[] lst = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
			for (int i=0; i<lst.length; i++) if (lst[i].startsWith("Mono")) panelPerf.setFont(new Font(lst[i], Font.BOLD, 16));
			tabbedPane.addTab("Perf", panelPerf);
			//System.out.println(panelPerf.getFont().getFontName());
		}
		tabbedPane.addChangeListener(this);
		panelContenu.add(tabbedPane, BorderLayout.CENTER);
		panelContenu.add(bas, BorderLayout.SOUTH);
		panelComplet.add(panelContenu, BorderLayout.CENTER);
		panelComplet.validate();
		return panelComplet;
	}

	public void readData (BufferedReader br) {
		try {
			panelDessin.raz();
			panelDessin.readData(br);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void readParam(BufferedReader bf) throws NumberFormatException, IOException {
    	minX.setVal(Double.parseDouble(bf.readLine()));
    	maxX.setVal(Double.parseDouble(bf.readLine()));
    	pasX.setVal(Double.parseDouble(bf.readLine()));
    	minY.setVal(Double.parseDouble(bf.readLine()));
    	maxY.setVal(Double.parseDouble(bf.readLine()));
    	//pasY.setVal(Double.parseDouble(bf.readLine()));
    	epsilon.setVal(Double.parseDouble(bf.readLine()));
    	nbrIter.setVal(Integer.parseInt(bf.readLine()));
    	valB.setVal(Double.parseDouble(bf.readLine()));
    	lstChoix.setSelectedIndex(Integer.parseInt(bf.readLine())); 
    	lstChoixPlan.setSelectedIndex(Integer.parseInt(bf.readLine())); 
    	//afficherCourbes.setSelected(Boolean.parseBoolean(bf.readLine()));
    	//panelCouleurs.boutonChoixNbreCycles.setSelectedIndex(Integer.parseInt(bf.readLine())-1);
	}

	public void saveParam(BufferedWriter bw) throws IOException {
    	bw.write(""+minX.getVal()); bw.newLine();
    	bw.write(""+maxX.getVal()); bw.newLine();
    	bw.write(""+pasX.getVal()); bw.newLine();
    	bw.write(""+minY.getVal()); bw.newLine();
    	bw.write(""+maxY.getVal()); bw.newLine();
    	//bw.write(""+pasY.getVal()); bw.newLine();
    	bw.write(""+epsilon.getVal()); bw.newLine();
    	bw.write(""+nbrIter.getVal()); bw.newLine();
    	bw.write(""+valB.getVal()); bw.newLine();
    	bw.write(""+lstChoix.getSelectedIndex()); bw.newLine();
    	bw.write(""+lstChoixPlan.getSelectedIndex()); bw.newLine();
    	//bw.write(""+afficherCourbes.isSelected()); bw.newLine();
    	//bw.write(""+panelCouleurs.cycleMax);  bw.newLine();
	}

	public void saveData() {
		String nomDate = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
		File f = new File("Data_"+ nomDate +".data");
		try {
			FileWriter fout = new FileWriter(f);
			BufferedWriter bw = new BufferedWriter(fout);
			bw.write(this.getClass().getName());bw.newLine();
			saveParam(bw);
			panelDessin.saveData(bw);
			bw.flush();
			bw.close();
			System.out.println("Fichier "+f.getAbsolutePath()+" créé !");
		} catch (IOException e) {
			System.out.println("Erreur IO sur Fichier "+f.getAbsolutePath()+" !");
		}
	}

	public void stateChanged(ChangeEvent e) {
		if (e.getSource()==tabbedPane) {
			if (tabbedPane.getSelectedComponent()==panelDessin)
				panelDessin.setDoubleBufferVisible();
			else panelDessin.setDoubleBufferInvisible();
		}
	}
		

}
