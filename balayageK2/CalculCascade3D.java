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
import balayageK2.awt.PanelDessin2D;
import balayageK2.awt.InDouble;
import balayageK2.awt.InInteger;
import balayageK2.awt.PanelDessin3D;
import balayageK2.awt.PanelDessinBase;
import balayageK2.awt.PanelHTML;
import balayageK2.awt.PanelTexte;
import balayageK2.rmi.PanelDistant;
import balayageK2.rmi.PanelDistantImpl;

public abstract class CalculCascade3D extends CalculBase {
	protected InDouble minX;
	protected InDouble maxX;
	protected InDouble pasX;
	protected InDouble minY;
	protected InDouble maxY;
	//protected InDouble pasY = new InDouble("pas Y", 0.004d, 8);
	protected InDouble valB;
	protected JComboBox lstChoixPlan;
	//protected InDouble epsilon = new InDouble("epsilon", 1E-10, 8);
	protected InInteger nbrIter;
	protected JComboBox lstChoix;
	protected JLabel titre;
	protected JPanel contenu;
	protected PanelDessin3D panelDessin;
	
	protected PanelCouleurs panelCouleurs;
	protected JPanel panel3D;
	protected JSlider sliderX;
	protected JSlider sliderY;
	protected JSlider sliderZ;
	protected JSlider sliderZoom;
	PanelTexte panelPerf;
	JTabbedPane tabbedPane;
	JPanel panelDessinCmd3D;

	public JRadioButton afficherCourbes;

	protected double a;
	protected double b;
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
	
	public abstract void calculM ();
	protected abstract double valeurInit (int i);
	public abstract void dessinerEquations(Graphics g, PanelDessin2D dessin);
	
	protected void initObjetsAWT() {
		minX = new InDouble("min X", -1.0d); // -2.0d
		maxX = new InDouble("max X", +1.0d); //+2.0d
		pasX = new InDouble("pas X", 0, 8);//0.004d, 8);
		minY = new InDouble("min Y", -2.0d);
		maxY = new InDouble("max Y", +2.0d);
		valB = new InDouble("B", -0.261, 8);
		nbrIter = new InInteger("Iterations", 30);
		contenu = new JPanel();
		panelDessin = new PanelDessin3D();
		panelCouleurs = new PanelCouleurs(panelDessin);
		afficherCourbes = new JRadioButton("afficher Courbes", true);
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
		lstPtsZ = new Vector<Double>();
		lstPtsC = new Vector<Integer>();
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
		try {
			if (panelDessinDistantMaitre==null) panelDessinDistantMaitre = (PanelDistant) UnicastRemoteObject.exportObject(new PanelDistantImpl(panelDessin), 0);
			initialisation.put("panelDessinDistant", panelDessinDistantMaitre);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
/*  
 	public void ajouterPoint(double a, double b, Color c) {
		panelDessin.ajouterPoint(a, b, c);
	}
*/
	
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
		//epsilon.checkVal();
		nbrIter.checkVal();
	}
	
	public static long ctrCalculs;
	public void run() {
		Date dateDebut = new Date();
		long ctrAppel = 0l;
		Coordonnees3D.ctr3D = 0L;
		Coordonnees2D.ctr2D = 0L;
		ctrCalculs = 0;
		checkParam();
		panelCommande.enleverBoutonGo();
		panelDessin.raz();
		panelDessin.redessiner();
		//panelTexte.raz();
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
		if (afficherCourbes.isSelected())
			panelDessin.dessinerLesAxes(panelDessin.getGraphics());
		
		attendreInactivité();
		String txtPerf = AppletDessin.textePerf(dateDebut, ctrAppel, ctrCalculs, 0.0);
		if (panelPerf!=null) {
			panelPerf.raz();
			panelPerf.print(txtPerf);
		}
		System.out.println(txtPerf);
		panelCommande.ajouterBoutonGo();
	}

	public void calcul() {
		// initialisation itération 0
		for (int m=0; m<mMax; m++) {
			lgN[0][m] = valInit[m];
		}
		// itération 2..nMax
		indiceItérationCourante = 0;
		indiceItérationSuivante = 1;
		
		for (int k=1; k<=100; k++) {
			for (int n=1; n<nMax; n++) {
				noItérationCourante = n;
				indiceItérationPrécédente= indiceItérationCourante;
				indiceItérationCourante =  indiceItérationSuivante;
				indiceItérationSuivante = (n+1) & masqueIndiceLigne;
				lgN[indiceItérationCourante][0] = valInit[n];
				for (int m=1; m<mMax; m++) {
					//System.out.println("Calcul ==> "+k+" "+n+" "+m+" "+lgN[indiceItérationCourante][m]);
					if (arrêtRunner) return;
					this.m = m;
					//lgN[indiceItérationCourante][m] = fooRecur();
					calculM();
					//System.out.println("Point ==> "+a+" "+lgN[indiceItérationCourante][m]+" "+lgN[indiceItérationPrécédente][m-1]);
					if (!Double.isNaN(lgN[indiceItérationCourante][m]) &&
							!Double.isNaN(lgN[indiceItérationPrécédente][m-1]) &&
							!Double.isInfinite(lgN[indiceItérationCourante][m]) &&
							!Double.isInfinite(lgN[indiceItérationPrécédente][m-1])) {
						
						if (panelDessinDistant==null) panelDessin.ajouterPoint3D(a, lgN[indiceItérationCourante][m], lgN[indiceItérationPrécédente][m-1], new ListeCouleurs(Color.red));
						else différerPoint3D(a, lgN[indiceItérationCourante][m], lgN[indiceItérationPrécédente][m-1], Color.red);
					}
					//panelDessin.ajouterPoint3D(a, lgN[indiceItérationCourante][m], lgN[indiceItérationPrécédente][m-1], Color.red);
					//System.out.println("Point ==> "+a+" "+lgN[indiceItérationCourante][m]+" "+lgN[indiceItérationPrécédente][m-1]);
					ctrCalculs++;
				}
			}
		}
		envoyerLstPointsDifférés3D();
	}

	/*
	PrintWriter fout;
	
	public void stopPrintCycleMaintenant() {
		if (fout!= null) fout.close();
	}
	
	public void startPrintCycleMaintenant() {
		cycleImprimé = new boolean[cycleMax];
		ctrCycleRestantAImprimé = cycleMax;
	}

	public void printTraceCycle(int cycle, int m) {
		//printTraceCycleDirect(cycle, m);
		dessin.panelTexte.print(" ==> Cycle "+cycle+" : a="+a+" b="+b+" -->");
		for (int noCyY=cycle; noCyY>=0; noCyY--) {
			dessin.panelTexte.println();
			dessin.panelTexte.print("    ");
			for (int noCyX=cycle; noCyX>=0; noCyX--) {
				//dessin.panelTexte.print(" "+noCyY+"x"+(m-noCyX)+"="+lgN[noCyY][m-noCyX]);
				dessin.panelTexte.print(" "+lgN[noCyY][m-noCyX]);
			}dessin.panelTexte.println();
		}
		cycleImprimé[cycle-1]=true;
		ctrCycleRestantAImprimé--;
		if (ctrCycleRestantAImprimé==0)
			dessin.panelTexte.println("------------------------------------------------");
	}
	*/
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
		//panelParam.add(epsilon);
		panelParam.add(nbrIter);
		panelParam.add(minY);
		panelParam.add(maxY);
		//panelParam.add(pasY);
		panelParam.add(lstChoix);
		String[] lstChoixPlanStr = { "A", "B" };
		lstChoixPlan = new JComboBox(lstChoixPlanStr);
		lstChoixPlan.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (lstChoixPlan.getSelectedIndex()==0) {
					valB.changeLabel("B");
				} else {
					valB.changeLabel("A");
				}
			}	
		});
		JPanel jp = new JPanel();
		jp.add(lstChoixPlan);
		jp.add(valB);
		panelParam.add(jp);
		//panelParam.add(afficherCourbes);
		

		panel3D = new JPanel();
		panel3D.setLayout(new GridLayout(4, 2));
		sliderX = new JSlider(JSlider.VERTICAL, 0, 180, 0);
		sliderY = new JSlider(JSlider.VERTICAL, 0, 180, 0);
		sliderZ = new JSlider(JSlider.VERTICAL, 0, 180, 0);
		sliderZoom = new JSlider(JSlider.VERTICAL, -40, +40, 0);
		panel3D.add(sliderX);
		panel3D.add(new JLabel("X"));
		panel3D.add(sliderY);
		panel3D.add(new JLabel("Y"));
		panel3D.add(sliderZ);
		panel3D.add(new JLabel("Z"));
		panel3D.add(sliderZoom);
		panel3D.add(new JLabel("%"));
		sliderX.addChangeListener(this);
		sliderY.addChangeListener(this);
		sliderZ.addChangeListener(this);
		sliderZoom.addChangeListener(this);
		
		
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
		JPanel panelContenu = new JPanel();
		panelContenu.setLayout(new BorderLayout());
		panelDessin.setParametres(pasX, minX, maxX, minY, maxY, afficherCourbes, this);
		panelDessinCmd3D= new JPanel();
		panelDessinCmd3D.setLayout(new BorderLayout());
		panelDessinCmd3D.add(panelDessin, BorderLayout.CENTER);
		panelDessinCmd3D.add(panel3D, BorderLayout.EAST);
		panelCommande = new PanelCommande(applet.boutonMainHTML);
		panelCommande.addActionListener(this);
		bas.add(panelCommande, BorderLayout.CENTER);
		panelContenu.add(panelParam, BorderLayout.NORTH);
		tabbedPane = new JTabbedPane();
		tabbedPane.insertTab("Dessin", null, panelDessinCmd3D, null, 0);
		String nomFichHelp = "HTML/"+this.getClass().getName()+".html";
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
    	//epsilon.setVal(Double.parseDouble(bf.readLine()));
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
    	//bw.write(""+epsilon.getVal()); bw.newLine();
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

	public void actionPerformed(ActionEvent arg0) {
		//System.out.println("Action Performed => "+arg0.getActionCommand());
		if (arg0.getSource()==afficherCourbes) {
			panelDessin.redessiner();
		} else super.actionPerformed(arg0);
	}
	
	public void stateChanged(ChangeEvent e) {
		if (e.getSource()==sliderX || e.getSource()==sliderY || e.getSource()==sliderZ  || e.getSource()==sliderZoom) {
			final double angleX = Math.toRadians((double)sliderX.getValue());
			final double angleY = Math.toRadians((double)sliderY.getValue());
			final double angleZ = Math.toRadians((double)sliderZ.getValue());
			panelDessin.zoom = Math.pow(2.0, ((double)sliderZoom.getValue())/10.0);
			//System.out.println("==>"+dessin.panelDessin.zoom+" "+dessin.panelDessin.deplX);
			if (panelDessin.threadRafraichissementEcran!=null) {
				panelDessin.arretRafraichissementEcran = true;
				try {
					panelDessin.threadRafraichissementEcran.join();
				} catch (InterruptedException ee) {
				}
				panelDessin.threadRafraichissementEcran = null;
				panelDessin.arretRafraichissementEcran = false;
			}
			panelDessin.threadRafraichissementEcran = new Thread() {
				public void run() {
					panelDessin.rotate(angleX, angleY, angleZ);
				}
			};
			//panelDessin.threadRafraichissementEcran.setPriority(Thread.currentThread().getPriority()-1);
			panelDessin.threadRafraichissementEcran.start();
		} else if (e.getSource()==tabbedPane) {
			if (tabbedPane.getSelectedComponent()==panelDessinCmd3D)
				panelDessin.setDoubleBufferVisible();
			else panelDessin.setDoubleBufferInvisible();
		}
	}
	

}
